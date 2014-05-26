/**
 * 
 */
package de.csw.cl.importer.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.Element;

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
	}
	
	/**
	 * Creates complete xml documents for all includes and saves them to the
	 * includes directory.
	 */
	public void writeIncludes() {
		for (Entry<String, Element> entry : includes.entrySet()) {
			String fileName = entry.getKey() + ".xml";
			Element rootElement = entry.getValue();
			rootElement.detach();
			
			Document doc = new Document(rootElement);
			XMLUtil.writeXML(doc, new File(includesDir, fileName));
		}
	}
	
	/**
	 * Returns the root element of an include file for a given file hash.
	 * If none exists, the original element passed as parameter e will be returned.
	 * @param fileHash
	 * @param e
	 */
	public Element getInclude(String fileHash, Element e) {
		Element existingInclude = includes.get(fileHash);
		if (existingInclude == null) {
			includes.put(fileHash, e);
			return e;
		}
		return existingInclude;
	}
	
}
