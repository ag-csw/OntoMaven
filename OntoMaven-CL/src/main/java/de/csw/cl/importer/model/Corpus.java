/**
 * 
 */
package de.csw.cl.importer.model;

import static util.XMLUtil.NS_XCL2;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;

import de.csw.cl.importer.algorithm.FolderCreationException;
import util.XMLUtil;

/**
 * This class represents a CL text corpus and its elements which are relevant
 * during the importation process. The availability of such elements is subject
 * to change during the importation process. E.g., a nested titling can become
 * available for importation once its surrounding titling has been imported.
 * Likewise, the stack of processable import directives can grow if contents of
 * titlings including import directives are asserted after importation, or
 * shrink after import directives have been executed and replaced by the actual
 * content of the imported titled texts.
 * 
 * @author ralph
 * 
 */
public class Corpus {

    //TODO : why is logging not working here for the JUnit tests?
	private static Logger LOG = Logger.getLogger(Corpus.class);
	
	
    // The top-level XML documents of the corpus.
	// The documents should be valid with respect to the XCL2 schema, when
	// XInclude directives are resolved.
	// The state of the Corpus is inconsistent if there are circular XInclude directives.
	// Titlings should not contain XInclude directives in the Ontomaven namespace.
	private final HashMap<Document, File> documents = new HashMap<Document, File>();
	

	// All top-level titlings (by name) that are available for importation.
	// During the importation process, new titlings, which become importable
	// because their surrounding titling has been imported, might be added to
	// this map.
	private HashMap<String, Element> importableTitlings = new HashMap<String, Element>();
	
	//TODO: maintain a hashmap of accessible importation elements and their include IRI
    //private HashMap<Element, String> accessibleImportations = new HashMap<Element, String>();
	
	// An Includes data structure containing all XML elements that are 
	// included by XInclude directives in the corpus,
	// indexed by the relative path that will be used
	// for local caching when the corpus is written.
	// During the importation process, new entries may be added.
	public Includes includes;
	
	// Contains mappings between the IRIs of XInclude directives and the
	// relative path for the cached copy.
    // All Xinclude directives in the XInclude closure of documents that reference remote objects
	// should have mappings in the catalog that map to relative file paths.
	// The catalog should not contain mappings in the Ontomaven domain that are not used by some XInclude directive.
	// The catalog should not contain mappings to remote IRIs.
    // During the importation process, new entries may be added.
	public XMLCatalog catalog;

	/**
	 * @param corpusDir
	 *        the directory containing XCL2 documents and optionally
	 *        the catalog.xml file and /incldues directory
	 * @throws ConflictingTitlingException 
	 * @throws MissingIncludeEntryException 
	 * @throws MissingCatalogEntryException 
	 */
	public Corpus(File corpusDir) throws ConflictingTitlingException, MissingCatalogEntryException, MissingIncludeEntryException {
        includes = new Includes(corpusDir);
        catalog = new XMLCatalog(new File(corpusDir, "catalog.xml"));
        if(!(includes.verifySequentialFileNames())) {
            //TODO: do something if the includes are not named as sequential numbers
            System.out.println("Warning: include files are not sequentially numbered.");
        }
        File[] docFiles = corpusDir.listFiles(new FilenameFilter() {
	         public boolean accept(File dir, String name) {
	           return name.toLowerCase().endsWith(".xcl");
	         }
	    });
	    if (!(docFiles == null)) {    
    	    for (File docFile : docFiles) {
    	        Document doc = XMLUtil.readLocalDoc(docFile);
    	            
    	    // TODO wenn schemaURL, dann:   
    //	          XMLUtil.readAndValidate(file, schemaURL);
    	            
    	          addDocument(doc, docFile);
    	    }	    
	    }
	    
	    catalog = cleanCatalog();
	    //TODO: verify that each file in includes is used in some XInclude directive.
	    
	}

	/**
     * Adds a loaded document to the corpus.
	 * @param doc
     *            the document to add.
	 * @param documentFile
     *            the original file of the document.
	 * @throws ConflictingTitlingException
	 * @throws MissingIncludeEntryException 
	 * @throws MissingCatalogEntryException 
	 */
	public void addDocument(Document doc, File documentFile) throws ConflictingTitlingException, MissingCatalogEntryException, MissingIncludeEntryException {
        LOG.debug("Adding document: " + documentFile.getName());  
        System.out.println("Adding document: " + documentFile.getName());  
		documents.put(doc, documentFile);
		extractTitlings(doc.getRootElement());
	}
	
	public XMLCatalog cleanCatalog() throws MissingCatalogEntryException, MissingIncludeEntryException {
       //remove catalog entries in the Ontomaven domain that are unused,
	   XMLCatalog newCatalog = catalog.clone(); 
       for ( Entry<String, String> entry: catalog.getMappings().entrySet() ){
            String name = entry.getKey();
            if (name.startsWith(XMLUtil.NS_ONTOMAVEN.getURI().toString())) {
                if (!(verifyInclude(name))) {
                    LOG.warn("Warning: An unused catalog entries has been removed: " + name);
                    System.out.println("Warning: An unused catalog entries has been removed: " + name);
                    newCatalog.removeMapping(name);
                }
            }
        }
       //TODO: verify that each catalog entry maps to a relative path.
       //TODO: verify that each catalog entry has an includes entry.
       return newCatalog;
	}
	
    /**
     * Returns a {@link Boolean} indicating  if an IRI is used for XInclude in the corpus.
     * This implementation only checks for an exact String match of the IRI.
     * TODO: also check for equivalent IRIs?
     * @param uri the IRI to be checked.
     * @return true if there is an XInclude that references uri
     *    in the XInclude closure of the corpus documents.
     * @throws MissingIncludeEntryException 
     * @throws MissingCatalogEntryException 
     */
	private boolean verifyInclude(String uri) throws MissingCatalogEntryException, MissingIncludeEntryException {
	    for( Document doc: getDocuments()) {
	       //System.out.println("Checking:" + getOriginalFile(doc).getName()); 
	       if(verifyIncludeLoop(doc.getRootElement(), uri ))
	          return true;
	    }
	    return false;   
	}
	
	/**
     * Returns a {@link Boolean} indicating if an IRI is used for XInclude in an {@link Element}.
	 * @param e the {@link Element} to check.
	 * @param uri the IRI to check.
	 * @return  true if there is an XInclude that references uri
     *    in the XInclude closure of e.
	 * @throws MissingIncludeEntryException 
	 * @throws MissingCatalogEntryException 
	 */
	private boolean verifyIncludeLoop(Element e, String uri) throws MissingCatalogEntryException, MissingIncludeEntryException {
	    String ename = e.getName();
	    //System.out.println("Checking: " + ename);
        if(e.getNamespace().equals(XMLUtil.NS_XINCLUDE)) {
    	    if(ename.equals("include")){
    	        if (e.getAttributeValue("href").equals(uri)) return true;
    	        if (verifyIncludeLoop(followInclude(e), uri)) return true;
    	    }
    	    return false;
    	}
        if(!(e.getNamespace().equals(XMLUtil.NS_XCL2))) return false;
	    if(ename.equals("Restrict") || ename.equals("Construct")){
	        for( Element c : e.getChildren()) {
	            if(verifyIncludeLoop(c, uri)) return true;
	        }
	    }
	    return false;
	    
	}
	
	/**
	 * Returns an {@link Iterable} over all documents contained in this {@link Corpus}.
	 * @return an {@link Iterable} over all documents contained in this {@link Corpus}. 
	 */
	public Iterable<Document> getDocuments() {
		return documents.keySet();
	}
	
	/**
	 * Gets the original file of a document
	 * @param doc the document
	 * @return the original file
	 */
	public File getOriginalFile(Document doc) {
		return documents.get(doc);
	}
	
	/**
	 * Returns the number of documents in the corpus
	 * 
	 * @return
	 */
	public Integer size() {
	    return documents.size();
	}
	
	/**
	 * Returns an importable titling with a given name, or null if no such
	 * titling exists.
	 * 
	 * @param name
	 * @return
	 */
	public Element getImportableTitling(String name) {
		return importableTitlings.get(name);
	}
	
	/**
	 * Performs a recursive depth-first traversion of the subtree under the
	 * given element e for Titling elements and adds the found Titling elements
	 * to the map of importable Titlings. The search will not continue under the
	 * found Titling elements.
	 * 
	 * @param e
	 * @throws ConflictingTitlingException 
	 * @throws MissingIncludeEntryException 
	 * @throws MissingCatalogEntryException 
	 */
	public void extractTitlings(Element e) throws ConflictingTitlingException, MissingCatalogEntryException, MissingIncludeEntryException {
        e = followInclude(e);
        List<Element> childElements = e.getChildren();
		for (Element child : childElements) {
            LOG.debug("Checking child: "+ child.getName());
            System.out.println("Checking child: "+ child.getName());
		    child = followInclude(child);		    
		    if(child.getNamespace().equals(XMLUtil.NS_XCL2)) {
    			if (child.getName().equals("Titling")) {
                    LOG.debug("Adding titling");
                    System.out.println("Adding titling");
    				addImportableTitling(child);
    			} 
                if (child.getName().equals("Construct")) {
                    extractTitlings(child);
                } 
                if (child.getName().equals("Restrict")) {
                    extractTitlings(child);
                } 
    		}
		}
	}

	/**
	 * Adds an importable titling to the map of importable titlings.
	 * @param e
	 * @throws ConflictingTitlingException 
	 */
	private void addImportableTitling(Element e)
			throws ConflictingTitlingException {
		Element nameElement = e.getChild("Name", NS_XCL2);
		String titlingName = nameElement.getAttributeValue("cri");
        //TODO if titlingName is CURIE, expand to IRI
        //TODO if nameElement has no cri attribute, get symbol

		Element existingImportableTitling = importableTitlings.get(titlingName);
		if (existingImportableTitling != null) {
			if (!XMLUtil.equal(e, existingImportableTitling)) {
				// conflicting Titling
				throw new ConflictingTitlingException(titlingName,
						existingImportableTitling, e);
			} else {
                LOG.debug("  Titling " + titlingName + " exists. Skipping.");
                System.out.println("  Titling " + titlingName + " exists. Skipping.");
			}
		} else {
            LOG.debug("  Adding titling " + titlingName);
            System.out.println("  Adding titling " + titlingName);
			
			importableTitlings.put(titlingName, e.clone());
		}
	}
	
	public Element followInclude(Element e) throws MissingCatalogEntryException, MissingIncludeEntryException {
	    if(isXInclude(e)) {
            String uri =   e.getAttributeValue("href");
	        try {
	          String filePath = catalog.getFileHash(uri);
	            try {
	                    Element enew = includes.getInclude(filePath, null);
	                    return followInclude(enew);
	              } catch (NullPointerException ex) {
	                  LOG.warn("Warning: no entry for this key in includes. Not following.");
	                  System.out.println("Warning: no entry for this key in includes. Not following.");                
	                  throw new MissingIncludeEntryException(filePath);
	              }
	        } catch (NullPointerException ex) {
                LOG.warn("Warning: no entry for this key in includes. Not following.");
                System.out.println("Warning: no entry for this key in includes. Not following."); 
                throw new MissingCatalogEntryException(uri);
                //TODO try to resolve includes that are not in catalog
	        }
	    }
        return e;
	}
	
	public void write(File resultDir)  throws FolderCreationException {
        if (size() > 0) {        
            if (!resultDir.mkdir()) {
                throw new FolderCreationException("Error creating directory " + resultDir.getAbsolutePath());
            }
            for (Document document : getDocuments()) {
                XMLUtil.writeXML(document, new File(resultDir, getOriginalFile(document).getName().replaceAll("myText", "resultText")));
            }
            catalog.write(resultDir);
            includes.writeIncludes(resultDir);
            List<String> unresolvedImports = getUnresolvedImports();
            if (!(unresolvedImports.isEmpty())) {
                LOG.warn("Warning. There are unresolved importations:");
                System.out.println("Warning. There are unresolved importations:");
                for (String unresolvedImport : unresolvedImports) {
                    LOG.info(unresolvedImport);
                    System.out.println(unresolvedImport);
                }
            }
        }
	    
	}
	   private LinkedList<String> getUnresolvedImports() {
	        final LinkedList<String> unresolvedImports = new LinkedList<String>();
	        Iterable<Document> documents = getDocuments();
	        for (Document document : documents) {
	            final String fileName = getOriginalFile(document).getName().replaceAll("myText", "resultText");
	            XMLUtil.performRecursivelAction(document.getRootElement(), new XMLUtil.Action() {
	                public void doAction(Element e) {
	                    if (e.getName().equals("Import") && isUntitled(e)) {
	                        unresolvedImports.add(getName(e) + " in " + fileName);
	                    }
	                }
	            });
	        }
	        return unresolvedImports;
	    }
	   
	   private boolean isUntitled(Element e) {
	       if(!(e.isRootElement())) {
	           Element enew = e.getParentElement();
	           if(isTitling(enew)) {
	               return false;
	           }
	           return isUntitled(enew);
	       }
	       return true;
	   }

	    public static String getName(Element e) {
	        Element nameElement = e.getChild("Name", NS_XCL2);
	        return nameElement == null ? null : nameElement.getAttributeValue("cri");
	    }
	    
	    public static boolean isTitling(Content e) {
	        switch(e.getCType()) {
	            case Element : 
	                return (((Element) e).getName().equals("Titling") && ((Element) e).getNamespace().equals(XMLUtil.NS_XCL2));
	            default:
	                return false;
	        }
	    }


	    public static boolean isXInclude(Content e) {
	        switch(e.getCType()) {
	            case Element : 
	                return (((Element) e).getName().equals("include") && ((Element) e).getNamespace().equals(XMLUtil.NS_XINCLUDE) );
	            default:
	                return false;
	        }
	    }
	    
	    public static boolean isXMLComment(Content e) {
	           switch(e.getCType()) {
	           case Comment : 
	               return true;
	           default:
	               return false;
	       }
	    }
	    


	
}
