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
	
	
	private HashMap<String, Element> includes = new HashMap<String, Element>();
	
	public Includes(File resultDir) {
	    File includesDir = new File(resultDir, "includes/");
		if(includesDir.exists()) {
		    File[] includeFiles = includesDir.listFiles();
		    for( File file: includeFiles) {
		        String fileName = file.getName();
		        String filePath = "includes/" + fileName;
		        Document inc = XMLUtil.readLocalDoc(file);
		        getInclude( filePath, inc.getRootElement());
		        System.out.println("Loaded include file: " + filePath);
		    }
		}
	}
	
	public boolean verifySequentialFileNames() {
	    for ( int i = 1 ; i < includes.size() + 1 ; i++) {
	        String filePath = "includes/" + i + ".xml";
	        System.out.println("Checking :" + filePath);
	        if(!(includes.containsKey(filePath))) {
	            return false;
	        }
	    }
	    return true;
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
            File outIncludesDir = new File(resultDir, "includes/");
            if (!(outIncludesDir.exists() || outIncludesDir.mkdir())) {
                throw new FolderCreationException("Error creating directory " + outIncludesDir.getAbsolutePath());
            }
		for (Entry<String, Element> entry : includes.entrySet()) {
			String filePath = entry.getKey();
			Element rootElement = entry.getValue();
						
            //Element newRootElement = rootElement.clone();
            Element newRootElement = rootElement;
			
			Document doc = new Document(newRootElement);
            XMLUtil.performRecursivelAction(doc.getRootElement(), new XMLUtil.Action() {
                public void doAction(Element e) {
                    e.removeAttribute("key");
                }
            });
            XMLUtil.writeXML(doc, new File(resultDir, filePath));
		}
        }
	}
	
	/**
	 * Returns the root element of an include file for a given file hash.
	 * If none exists, the original element passed as parameter e will be returned.
	 * @param fileHash
	 * @param e
	 */
	public Element getInclude(String filePath, Element e) {
	    System.out.println("Attempting to retrieve include file: " + filePath);
		Element existingInclude = includes.get(filePath);
		if (existingInclude == null) {
			existingInclude = e.clone();
			includes.put(filePath, existingInclude);
		}
		return existingInclude;
	}
	
}
