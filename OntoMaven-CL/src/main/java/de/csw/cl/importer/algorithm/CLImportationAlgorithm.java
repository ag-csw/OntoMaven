/**
 * 
 */
package de.csw.cl.importer.algorithm;

import static util.XMLUtil.NS_XCL2;

import java.io.File;
import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Parent;
import org.jdom2.output.XMLOutputter;

import util.XMLUtil;
import de.csw.cl.importer.model.ConflictingTitlingException;
import de.csw.cl.importer.model.Corpus;
import de.csw.cl.importer.model.MissingCatalogEntryException;
import de.csw.cl.importer.model.MissingIncludeEntryException;

/**
 * @author ralph
 *
 */
public class CLImportationAlgorithm {
	
    private static Logger LOG = Logger.getLogger(CLImportationAlgorithm.class);

    private enum ELEMENT_TYPE {Titling, Restrict, Import, Construct, include, other}
	
	private File baseDir;
	
	private Corpus corpus;
	
	// not used for now
	private final Queue<Element> potentiallyPendingImports = new LinkedList<Element>();
	
	private Integer includeNumber;
	
	/**
	 * Constructs a {@link CLImportationAlgorithm} object initialized with a given base file.
	 * If the base file resides in directory inputDir, the following file layout will be used:
	 * <ul>
	 * <li> All xcl files residing in inputDir will be considered the corpus.
	 * <li> The resulting xcl file with the importation closure will be saved to inputDir/../result.
	 * <li> Additional files for inclusion will be saved in inputDir/result/includes.
	 * </ul>
	 * @param corpusDirectory
	 */
	public CLImportationAlgorithm(File corpusDirectory) {

		// TODO weiterer Parameter schema URL
		
		baseDir = corpusDirectory;
		
	}
	
	/**
     * Starts the importation process.
	 * @param resultDir
	 * @throws ConflictingTitlingException
	 * @throws FolderCreationException
	 * @throws MissingIncludeEntryException 
	 * @throws MissingCatalogEntryException 
	 */
	public void run(File resultDir) throws ConflictingTitlingException, FolderCreationException, MissingCatalogEntryException, MissingIncludeEntryException {

		try {
		    corpus = new Corpus(baseDir);
		} catch (ConflictingTitlingException e) {
		    LOG.error(e);
			throw e;
		} catch (MissingCatalogEntryException e) {
            LOG.error(e);
            throw e;
        } catch (MissingIncludeEntryException e) {
            LOG.error(e);
            throw e;
        }
		
		// count the number of includes already in the corpus
		// TODO: This only works for minting new names if all included file names are sequentially numbered from 1.
		includeNumber = corpus.includes.size();
		
		processImports();
		
		corpus.write(resultDir);
		
	}
		
	/**
	 * Processes all import directives in a depth-first fashion, starting at the
	 * root of the document. Repeats until all imports have been resolved or
	 * only unasserted import directives (nested in titlings) are available.
	 * @throws ConflictingTitlingException 
	 * @throws MissingIncludeEntryException 
	 * @throws MissingCatalogEntryException 
	 */
	private void processImports() throws ConflictingTitlingException, MissingCatalogEntryException, MissingIncludeEntryException {

		while(true) {
		    LOG.debug("Starting a Pass");
		    System.out.println("Starting a Pass");
		    // flag to keep track of changes in the catalog
		    boolean changed = false;
			// repeat until a complete traversal does not yield any new import
			// resolutions, as indicated by a change in the catalog.
			// TODO: possible performance optimization: remember unexecutable imports hidden in titled texts and process them without having to traverse the whole corpus again.
			List<ElementPair> pendingReplacements = new LinkedList<CLImportationAlgorithm.ElementPair>(); 
			
			for (Document document : corpus.getDocuments()) {
				//visitedElements = new HashSet<Element>();
				if(processImport(document.getRootElement(), new Stack<String>(), new Stack<String>(), pendingReplacements)) {
				    changed = true;
				}
			}
						
			for (ElementPair pair : pendingReplacements) {
				Parent parent = pair.original.getParent();
				int position = parent.indexOf(pair.original);
				pair.original.detach();
				if (pair.replacement != null) {
					parent.addContent(position, pair.replacement);
				}
			}
			
			if(!changed) break;
			

		}
	}
	
	/**
	 * 
	 * @param e an XML Element
     * @param includeHistory
     * @param restrictHistory
     * @param pendingReplacements
	 * @return True if an import has been executed, false otherwise
	 * @throws ConflictingTitlingException 
	 * @throws MissingIncludeEntryException 
	 * @throws MissingCatalogEntryException 
	 */
	private boolean processImport(Element e,  Stack<String> includeHistory, 
	        Stack<String> restrictHistory,  List<ElementPair> pendingReplacements) 
	        throws ConflictingTitlingException, MissingCatalogEntryException, MissingIncludeEntryException {
	    
		boolean changed = false;
		/*if (visitedElements.contains(e)) {
			return;
		}
		visitedElements.add(e);*/
		
		ELEMENT_TYPE elementType = null;
		try {
			elementType = ELEMENT_TYPE.valueOf(e.getName());
		} catch (IllegalArgumentException plannedException) {
			elementType = ELEMENT_TYPE.other;
		}	
		
		switch (elementType) {
			case Import:
				String name = Corpus.getName(e);
				Element titling = corpus.getImportableTitling(name);
				if (titling != null) {
					// no titling available for this import (yet):
					// return immediately because Import elements cannot have further Import elements as successors.

					// Otherwise: we have a matching titling. Replace the Import element with the contents of the Titling element.				
					Element newXincludeElement = executeImport(e, titling, restrictHistory);
                    //TODO: verify that an XInclude loop has not been created by this import resolution 
					//TODO: verify that the XInclude closure of the new document can be constructed
                    //TODO: verify that the newly resolved document is valid against the  XCL2 Relax NG schema  
					
					pendingReplacements.add(new ElementPair(e, newXincludeElement));
					
					if (!Corpus.isXInclude(newXincludeElement)) {
						// duplicate import
    						System.out.println("*** Duplicate: removing " + e);
    						return changed;
					}		
    					
				    System.out.println("*** Replacing " + e + " with " + newXincludeElement);
				    changed = true;
				
				    Element includeReference = corpus.followInclude(newXincludeElement);

					if(processImport(includeReference, includeHistory, restrictHistory, pendingReplacements)) {
					    changed = true;
					}
				}
				return changed;
			case include:
				String xincludeHref = e.getAttributeValue("href");
                if (includeHistory.contains(xincludeHref)){
				    System.out.println("Found a circular XInclude directive:");
                    //TODO: move this check into the Corpus followInclude method
	                if (xincludeHref.startsWith(XMLUtil.NS_ONTOMAVEN.getURI().toString())) {
    		            // duplicate include in Ontomaven namespace can be removed
    				    System.out.println("Warning: Should Never Happen: Deleting Duplicate Include");
    			        Element xincludeElement = makeInclude(xincludeHref);
                        pendingReplacements.add(new ElementPair(e, constructComment(xincludeElement)));
                        // do not follow this include
    				    return changed;
    				}
		        }
				
				includeHistory.push(xincludeHref);
                Element referencedInclude = corpus.followInclude(e);
							
				if(processImport(referencedInclude, includeHistory, restrictHistory, pendingReplacements)){
				    changed = true;
				}
				
				break;
			case Restrict:
				restrictHistory.add(Corpus.getName(e));
				break;
			case Titling:
				// do not process import directives in titlings (yet).
				return changed;
            case Construct:
                // continue recursion
                break;
            case other:
                // terminate recursion
                return changed;
		}
		
		// process children
		for (Element child : e.getChildren()) {
			if(processImport(child, includeHistory, restrictHistory, pendingReplacements)) {
			    changed = true;
			}
		}
		
		// undo history
		switch(elementType) {
            case include:
                includeHistory.pop();
                break;
            case Restrict:
                restrictHistory.pop();
                break;
    		default :
    			break;				
		}
		return changed;
		
	}

	/**
	 * Performs the import of a titling. Replaces the current Import element with an Xinclude element,
	 * adds the imported content to an external file and adds a mapping in the xml catalog. 
	 * @param importElement
	 * @param titledElement
	 * @param restrictHistory
	 * @return the new xml include element or null if a cyclic import was detected. 
	 * @throws ConflictingTitlingException 
	 * @throws MissingIncludeEntryException 
	 * @throws MissingCatalogEntryException 
	 */
	private Element executeImport(Element importElement, Element titledElement, Stack<String> restrictHistory) throws ConflictingTitlingException, MissingCatalogEntryException, MissingIncludeEntryException {
	    Element titledContent;
		String titlingName = Corpus.getName(importElement);
		
		String includeURI = getXincludeURI(titlingName, restrictHistory);
		
        // create a new xinclude element and replace the content of this element with it
		Element xincludeElement = makeInclude(includeURI);

		if (!(corpus.catalog.getFileHash(includeURI) == null)) {
            // duplicate import 
		    // return a text construction containing an XML comments
            return constructComment(xincludeElement) ;
        }        

		//String hashCode = XMLUtil.getMD5Hash(titledElement);
        // TODO: hashCode only needs to be different for each includeURI 
        // if titledContent has an untitled Import or include element in it.
        String filePath = "includes/" + (++includeNumber).toString() + ".xml";
		
		// put the content of the titled text into a separate file
        titledContent = getTitlingContent(titledElement);
		titledContent = corpus.includes.getInclude(filePath, titledContent);
		System.out.println("Number of Includes: " + includeNumber.toString());
		
		// add any new titlings from the imported content to the corpus importableTitlings field
		corpus.extractTitlings(titledContent);
		
		// add a mapping to the xml catalog of the corpus
		corpus.catalog.addMapping(includeURI, filePath);
		
		
        return xincludeElement;
	}
	
	private Element makeInclude(String includeURI) {
        Element xincludeElement = new Element("include", XMLUtil.NS_XINCLUDE);
        xincludeElement.setAttribute("href", includeURI);
        return xincludeElement.setAttribute("parse", "xml");
	}
	
	private Element constructComment(Element e) {
        Element xincludeConstruct = new Element("Construct", XMLUtil.NS_XCL2);
        XMLOutputter xmlOutputter = new XMLOutputter();
        String commentString =  xmlOutputter.outputString(e);
        return xincludeConstruct.addContent(new Comment(commentString));
	}
	
	private Element getTitlingContent(Element titledElement) throws MissingCatalogEntryException, MissingIncludeEntryException {
        // extract the contents of the titling
        // It should be either a Restrict, Construct or Import in the XCL2 namespace
        // or one of the above obtained by XInclude resolution
        for (Element child: titledElement.getChildren()) {
            child = corpus.followInclude(child);
            if (Corpus.isRestrict(child) || Corpus.isConstruct(child) || Corpus.isImport(child)) {
                return child;
            }
        }
        return null; //This should not happen if the CL document is valid after XInclude resolution.
        //TODO: throw an exception here
	}
	
	
	private String getXincludeURI(String titlingName, Stack<String> restrictHistory) {
		try {
			return XMLUtil.NS_ONTOMAVEN.getURI() + "?uri=" + URLEncoder.encode(titlingName, "UTF-8") + getRestrictionFragment(restrictHistory);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// TODO: this will only work if there is no restrictions in the query string
	/*private String getTitlingName(String xincludeURI) {
		try {
			return URLDecoder.decode(xincludeURI.replace(XMLUtil.NS_ONTOMAVEN.getURI() + "?uri=", ""), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	private String getRestrictionFragment(Stack<String> restrictHistory) {
		StringBuilder buf = new StringBuilder();
		int domainCounter = 1;
		
		TreeSet<String> restrictHistoryNormalized = new TreeSet<String>(restrictHistory);
		
		for (String restrictName : restrictHistoryNormalized) {
			
			buf.append(";dom");
			buf.append(domainCounter++);
			buf.append('=');
			try {
				buf.append(URLEncoder.encode(restrictName, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return buf.toString();
	}
	
	    

	
	private class ElementPair {
		
		public ElementPair(Element original, Element replacement) {
			this.original = original;
			this.replacement = replacement;
		}
		
		Element original;
		Element replacement;
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof ElementPair) &&
					((ElementPair)obj).original == original && 
					((ElementPair)obj).replacement == replacement;
		}
		
		@Override
		public int hashCode() {
			return original.hashCode() + replacement.hashCode();
		}
	}
	
	
}
