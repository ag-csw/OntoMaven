/**
 * 
 */
package de.csw.cl.importer.model;

import static util.XMLUtil.NS_XCL2;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import util.XMLUtil;

/**
 * This class represents a CL text corpus and its elements which are relevant
 * during the imporation process. The availability of such elements is subject
 * to change during the imporation process. E.g., a nested titling can become
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
	
	private static Logger LOG = Logger.getLogger(Corpus.class);

	private final HashMap<Document, File> documents = new HashMap<Document, File>();
	

	// All top-level titlings (by name) that are available for importation.
	// During the importation process, new titlings, which become importable
	// because their surrounding titling has been imported, might be added to
	// this map.
	private HashMap<String, Element> importableTitlings = new HashMap<String, Element>();;

	/**
	 * 
	 * @param baseDocument
	 * @param documents
	 * @throws ConflictingTitlingException 
	 */
	public Corpus() throws ConflictingTitlingException {
	}

	/**
	 * Adds a loaded document to the corpus.
	 * 
	 * @param doc
	 *            the document to add.
	 * @throws ConflictingTitlingException 
	 */
	public void addDocument(Document doc, File documentFile) throws ConflictingTitlingException {
		documents.put(doc, documentFile);
		extractTitlings(doc.getRootElement());
	}
	
	/**
	 * Returns an {@link Iterable} over all documents contained in this {@link Corpus}.
	 * @return an {@link Iterable} over all documents contained in this {@link Corpus}. 
	 */
	public Iterable<Document> getDocuments() {
		return documents.keySet();
	}
	
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
	 */
	private void extractTitlings(Element e) throws ConflictingTitlingException {
		List<Element> childElements = e.getChildren();
		for (Element element : childElements) {
			if (element.getName().equals("Titling")) {
				addImportableTitling(element);
			} else {
				extractTitlings(element);
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

		Element existingImportableTitling = importableTitlings.get(titlingName);
		if (existingImportableTitling != null) {
			if (!XMLUtil.equal(e, existingImportableTitling)) {
				// conflicting Titling
				throw new ConflictingTitlingException(titlingName,
						existingImportableTitling, e);
			} else {
				System.out.println("  Titling " + titlingName + " exists. Skipping.");
			}
		} else {
			System.out.println("  Adding titling " + titlingName);
			
			importableTitlings.put(titlingName, e.clone());
		}
	}
	
}
