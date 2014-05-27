/**
 * 
 */
package de.csw.cl.importer.model;

import static util.XMLUtil.NS_XCL2;

import java.io.File;
import java.io.FilenameFilter;
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

    public Boolean hasResolvableImports;

    private static Logger LOG = Logger.getLogger(Corpus.class);

    private File baseDir;
    private HashSet<Document> documents = new HashSet<Document>();
    private Includes includes;
    private XMLCatalog catalog;

    // All top-level titlings (by name) that are available for importation.
    // During the importation process, new titlings, which become importable
    // because their surrounding titling has been imported, might be added to
    // this map.
    private HashMap<String, Element> importableTitlings = new HashMap<String, Element>();;

    /**
     * 
     * @param baseDir
     * @param includes
     * @param catalog
     * @throws ConflictingTitlingException
     */
    public Corpus(File baseDir, Includes includes, XMLCatalog catalog)
            throws ConflictingTitlingException {
        this.baseDir = baseDir;
        File inputDir = new File(baseDir, "input");
        this.includes = includes;
        this.catalog = catalog;
        File[] files = inputDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xcl");
            }
        });

        for (File file : files) {
            Document doc = XMLUtil.readLocalDoc(file);
            this.addDocument(doc);
        }
        hasResolvableImports = true;

    }

    /**
     * Clones an existing corpus.
     * 
     * @param corpus
     */
    public Corpus(Corpus corpus) {
        this.baseDir = corpus.baseDir;
        this.includes = corpus.includes;
        this.catalog = corpus.catalog;
        this.documents = corpus.documents;
        this.importableTitlings = corpus.importableTitlings;
        this.hasResolvableImports = corpus.hasResolvableImports;
    }

    /**
     * Adds a loaded document to the corpus.
     * 
     * @param doc
     *            the document to add.
     * @throws ConflictingTitlingException
     */
    public void addDocument(Document doc) throws ConflictingTitlingException {
        documents.add(doc);
        extractTitlings(doc.getRootElement());
    }

    /**
     * Returns the documentses.
     * 
     * @return
     */
    public HashSet<Document> getDocuments() {
        return documents;
    }

    /**
     * Returns the includes.
     * 
     * @return
     */
    public Includes getIncludes() {
        return includes;
    }

    /**
     * Returns the catalog.
     * 
     * @return
     */
    public XMLCatalog getXMLCatalog() {
        return catalog;
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
     * 
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
                System.out.println("  Titling " + titlingName
                        + " exists. Skipping.");
            }
        } else {
            System.out.println("  Adding titling " + titlingName);

            // store the titling, remove surrounding titling element
            // if it has more than one direct child, put a Construct element
            // around them

            importableTitlings.put(titlingName, e);
        }
    }

}
