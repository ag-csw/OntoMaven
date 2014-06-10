/**
 * 
 */
package de.csw.cl.importer.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.Element;

import de.csw.cl.importer.algorithm.FolderCreationException;
import util.XMLUtil;

/**
 * This class manages the included files.
 * @author ralph
 */
public class Includes {
	
	private File includesDir;
	
	private HashMap<String, Element> includes = new HashMap<String, Element>();
	
	public Includes(File includesDir) {
		this.includesDir = includesDir;
		if(includesDir.exists()) {
		    File[] includeFiles = includesDir.listFiles();
		    for( File file: includeFiles) {
		        String fileName = file.getName();
		        String fileNameWithoutExtension = fileName.substring(0, fileName.length()-4);
		        Document inc = XMLUtil.readLocalDoc(file);
		        Document doc = new Document();
		        Element e = new Element("Titling", XMLUtil.NS_XCL2);
		        e.addContent(new Element("Name", XMLUtil.NS_XCL2));
		        e.addContent(inc.getRootElement().detach());
		        doc.setRootElement(e);
		        getInclude( fileNameWithoutExtension, doc.getRootElement());
		        System.out.println("Loaded include file: " + fileNameWithoutExtension);
		    }
		}
	}
	
	/**
	 *  Returns the number of included files
	 *  
	 * @return
	 */
	public Integer size() {
	    return includes.size();
	}
	
	/**
	 * Creates complete xml documents for all includes and saves them to the
	 * includes directory.
	 * @throws FolderCreationException 
	 */
	public void writeIncludes(File resultDir) throws FolderCreationException {
        if (includes.size() > 0) {
            File outIncludesDir = new File(resultDir, "includes");
            if (!(outIncludesDir.exists() || outIncludesDir.mkdir())) {
                throw new FolderCreationException("Error creating directory " + outIncludesDir.getAbsolutePath());
            }
		for (Entry<String, Element> entry : includes.entrySet()) {
			String fileName = entry.getKey() + ".xml";
			Element rootElement = entry.getValue();
			
			if (!rootElement.getName().equals("Titling")) {
				System.err.println("Discovered an include that is not a Titling");
			}
			
			// GitHub issue #23
			Element newRootElement = rootElement.getChild("Construct", XMLUtil.NS_XCL2);			
			if (newRootElement == null) {
				newRootElement = rootElement.getChild("Import", XMLUtil.NS_XCL2);
			}
			if (newRootElement == null) {
				newRootElement = rootElement.getChild("Restrict", XMLUtil.NS_XCL2);
			}
			if (newRootElement == null) {
				// no text in this include - no need to save
				continue;
			}
			
			newRootElement.detach();
			
			Document doc = new Document(newRootElement);
            XMLUtil.performRecursivelAction(doc.getRootElement(), new XMLUtil.Action() {
                public void doAction(Element e) {
                    e.removeAttribute("key");
                }
            });
            XMLUtil.writeXML(doc, new File(outIncludesDir, fileName));
		}
        }
	}
	
	/**
	 * Returns the root element of an include file for a given file hash.
	 * If none exists, the original element passed as parameter e will be returned.
	 * @param fileHash
	 * @param e
	 */
	public Element getInclude(String fileHash, Element e) {
	    System.out.println("Attempting to retrieve include file: " + fileHash);
		Element existingInclude = includes.get(fileHash);
		if (existingInclude == null) {
			existingInclude = e.clone();
			includes.put(fileHash, existingInclude);
		}
		return existingInclude;
	}
	
}
