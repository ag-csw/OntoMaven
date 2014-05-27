/**
 * 
 */
package de.csw.cl.importer.model;

import static util.XMLUtil.NS_XCL2;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;

import util.XMLUtil;
import java.util.Stack;
import de.csw.cl.importer.model.Include;

/**
 * This class manages the included files.
 * 
 * @author ralph
 */
public class Includes {

    private final File includesDir;

    private final HashMap<String, Include> includes = new HashMap<String, Include>();

    public Includes(File includesDir) {
        this.includesDir = includesDir;
    }

    /**
     * Creates complete xml documents for all includes and saves them to the
     * includes directory.
     */
    public void writeIncludes() {
        for (Entry<String, Include> entry : includes.entrySet()) {
            String fileName = entry.getKey() + ".xml";
            Element rootElement = entry.getValue().e;

            if (!rootElement.getName().equals("Titling")) {
                System.err
                        .println("Discovered an include that is not a Titling");
            }

            List<Element> children = rootElement.getChildren();

            Element nameElement = rootElement.getChild("Name", NS_XCL2);
            children.remove(nameElement);

            Element newRootElement;

            if (children.size() == 1) {
                newRootElement = children.get(0);
                newRootElement.detach();
            } else {
                Element constructElement = new Element("Construct",
                        XMLUtil.NS_XCL2);
                constructElement.addContent(children);
                newRootElement = constructElement;
            }

            Document doc = new Document(newRootElement);
            XMLUtil.writeXML(doc, new File(includesDir, fileName));
        }
    }

    /**
     * Returns the root element of an include file for a given file hash. If
     * none exists, the original element passed as parameter e will be returned.
     * 
     * @param fileHash
     * @param e
     */
    public Element getInclude(String fileHash, Element e,
            Stack<String> restrictHistory) {
        Element existingElement;
        Include existingInclude = includes.get(fileHash);
        if (existingInclude == null) {
            existingElement = e.clone();
            Include newInclude = new Include(existingElement, restrictHistory);
            includes.put(fileHash, newInclude);
        } else {
            existingElement = includes.get(fileHash).e;
        }
        return existingElement;
    }

    public Set<Entry<String, Include>> getIncludes() {
        return includes.entrySet();
    }

}
