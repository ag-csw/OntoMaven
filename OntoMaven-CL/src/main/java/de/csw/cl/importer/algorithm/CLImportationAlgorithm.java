/**
 * 
 */
package de.csw.cl.importer.algorithm;

import static util.XMLUtil.NS_XCL2;

import java.io.File;
//import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.Element;

import util.XMLUtil;
import de.csw.cl.importer.model.ConflictingTitlingException;
import de.csw.cl.importer.model.Corpus;
import de.csw.cl.importer.model.Include;
import de.csw.cl.importer.model.Includes;
import de.csw.cl.importer.model.XMLCatalog;

/**
 * @author ralph
 * 
 */
public class CLImportationAlgorithm {

    private enum ELEMENT_TYPE {
        Titling, Restrict, Import, include, other
    }

    private final File caseDir;

    private final File resultDir;
    private final File includesDir;

    private Corpus corpus;
    private final Includes includes;
    private final XMLCatalog catalog;

    // not used for now
    private final Queue<Element> potentiallyPendingImports = new LinkedList<Element>();

    /**
     * Constructs a {@link CLImportationAlgorithm} object initialized with a
     * given base file. If the base file resides in directory inputDir, the
     * following file layout will be used:
     * <ul>
     * <li>All xcl files residing in inputDir will be considered the corpus.
     * <li>The resulting xcl file with the importation closure will be saved to
     * inputDir/../result.
     * <li>Additional files for inclusion will be saved in
     * inputDir/result/includes.
     * </ul>
     * 
     * @param inputFile
     */
    public CLImportationAlgorithm(File caseDir) {
        this.caseDir = caseDir;

        resultDir = new File(caseDir, "test-result");
        includesDir = new File(resultDir, "includes");

        includes = new Includes(includesDir);
        catalog = new XMLCatalog(new File(resultDir, "catalog.xml"));
    }

    /**
     * Starts the importation process.
     * 
     * @throws ConflictingTitlingException
     * @throws FolderCreationException
     */
    public void run() throws ConflictingTitlingException,
            FolderCreationException {
        if (!(resultDir.exists() || resultDir.mkdir())) {
            throw new FolderCreationException("Error creating directory "
                    + resultDir.getAbsolutePath());
        }
        if (!(includesDir.exists() || includesDir.mkdir())) {
            throw new FolderCreationException("Error creating directory "
                    + includesDir.getAbsolutePath());
        }

        try {
            loadCorpus();
        } catch (ConflictingTitlingException e) {
            // System.out.println("Error: The corpus includes two conflicting titlings with the same name: "
            // + e.getName() + ". Aborting.");
            throw e;
        }

        processImports();
        Integer j = 0;
        for (Document doc : corpus.getDocuments()) {
            System.out.println("Writing a Document");
            j = j + 1;
            XMLUtil.writeXML(doc, new File(resultDir, "resultText-"
                    + caseDir.getName().replaceAll("case", "") + j.toString()
                    + ".xcl"));
        }
        System.out.println("Writing Catalog");
        catalog.write();
        System.out.println("Writing Includes");
        includes.writeIncludes();
    }

    /**
     * Loads the corpus.
     * 
     * @throws ConflictingTitlingException
     */
    private void loadCorpus() throws ConflictingTitlingException {
        corpus = new Corpus(caseDir, includes, catalog);
        System.out.println("Size of Corpus: " + corpus.getDocuments().size());
    }

    /**
     * Processes all import directives in a depth-first fashion, starting at the
     * root of the document. Repeats until all imports have been resolved or
     * only unasserted import directives (nested in titlings) are available.
     */
    private void processImports() {
        Stack<String> importHistory = new Stack<String>();
        while (true) {
            // repeat until a complete traversal does not yield any new import
            // resolutions.
            // TODO: possible performance optimization: remember unexecutable
            // imports hidden in titled texts and process them without having to
            // traverse the whole corpus again.
            System.out.println("Starting a pass");
            onePass(importHistory);

            // done
            if (!corpus.hasResolvableImports)
                break;

        }
        System.out.println("Done");
    }

    /**
     * 
     * @return True if an import has been executed, false otherwise
     */
    private void onePass(Stack<String> importHistory) {
        List<ElementPair> pendingReplacements = new LinkedList<CLImportationAlgorithm.ElementPair>();

        for (Document doc : corpus.getDocuments()) {
            System.out.println("Processing a Document");
            processImport(doc.getRootElement(), importHistory,
                    new Stack<String>(), pendingReplacements);
        }
        for (Entry<String, Include> entry : includes.getIncludes()) {
            System.out.println("Processing an Includes");
            Element rootElement = entry.getValue().e.getChildren().get(1);
            if (!(rootElement == null)) {
                processImport(rootElement, importHistory,
                        entry.getValue().restrictHistory, pendingReplacements);
            }
        }
        corpus.hasResolvableImports = pendingReplacements.size() > 0;
        for (ElementPair pair : pendingReplacements) {
            Element parent = pair.original.getParentElement();
            int position = parent.indexOf(pair.original);
            pair.original.detach();
            if (!(pair.replacement == null))
                parent.addContent(position, pair.replacement);
        }

    }

    private void processImport(Element e, Stack<String> importHistory,
            Stack<String> restrictHistory, List<ElementPair> pendingReplacements) {

        ELEMENT_TYPE elementType = null;
        try {
            elementType = ELEMENT_TYPE.valueOf(e.getName());
        } catch (IllegalArgumentException plannedException) {
            elementType = ELEMENT_TYPE.other;
        }

        switch (elementType) {
        case Import:
            System.out.println("Found an Import");
            String name = getName(e);
            Element titling = corpus.getImportableTitling(name);
            if (titling != null) {
                // no titling available for this import (yet):
                // return immediately because Import elements cannot have
                // further Import elements as successors.

                // Otherwise: we have a matching titling. Replace the Import
                // element with the contents of the Titling element.
                Element newXincludeElement = executeImport(e, titling,
                        importHistory, restrictHistory);

                System.out.println("Adding to pending");
                pendingReplacements.add(new ElementPair(e, newXincludeElement));

                if (newXincludeElement != null) {
                    List<Element> children = newXincludeElement.getChildren();
                    for (Element child : children) {
                        // importProcessed is true because we have just
                        // performed an import
                        processImport(child, importHistory, restrictHistory,
                                pendingReplacements);
                    }
                }
            }
            return;
        case include:
            System.out.println("Found an XInclude");
            // String xincludeHref = e.getAttributeValue("href");
            // importHistory.push(xincludeHref);
            // String fileHash = catalog.getFileHash(xincludeHref);
            // Element referencedInclude = includes.getInclude(fileHash, null);
            // processImport(referencedInclude, importHistory, restrictHistory,
            // pendingReplacements, isInclude);
            // break;
            return;
        case Restrict:
            System.out.println("Found a Restrict");
            restrictHistory.add(getName(e));
            break;
        case Titling:
            System.out.println("Found a Titling");
            // do not process import directives in titlings (yet).
            return;
        case other:
            System.out.println("Found Something Else");
            // do nothing
            break;
        }

        // process import

        List<Element> children = e.getChildren();
        for (Element child : children) {
            processImport(child, importHistory, restrictHistory,
                    pendingReplacements);
        }

        switch (elementType) {
        case Import:
        case include:
            // importHistory.pop();
            break;
        case Restrict:
            restrictHistory.pop();
            break;
        }

    }

    /**
     * Performs the import of a titling. Replaces the current Import element
     * with an Xinclude element, adds the imported content to an external file
     * and adds a mapping in the xml catalog.
     * 
     * @param importElement
     * @param titledContent
     * @param importHistory
     * @param restrictHistory
     * @return the new xml include element or null if a cyclic import was
     *         detected.
     */
    private Element executeImport(Element importElement, Element titledContent,
            Stack<String> importHistory, Stack<String> restrictHistory) {
        String titlingName = getName(importElement);

        String includeURI = getXincludeURI(titlingName, restrictHistory);

        if (importHistory.contains(includeURI)) {
            // cyclic import
            return null;
        }

        // create a new xinclude element and replace the content of this element
        // with it
        Element xincludeElement = new Element("include", XMLUtil.NS_XINCLUDE);
        xincludeElement.setAttribute("href", includeURI);
        xincludeElement.setAttribute("parse", "xml");

        String hashCode = XMLUtil.getMD5Hash(titledContent);

        // put the content of the titled text into a separate file
        titledContent = includes.getInclude(hashCode, titledContent,
                restrictHistory);

        // add a mapping to the xml catalog
        catalog.addMapping(includeURI, hashCode);

        importHistory.push(includeURI);

        return xincludeElement;
    }

    private String getXincludeURI(String titlingName,
            Stack<String> restrictHistory) {
        try {
            return XMLUtil.NS_ONTOMAVEN.getURI() + "?uri="
                    + URLEncoder.encode(titlingName, "UTF-8")
                    + getRestrictionFragment(restrictHistory);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTitlingName(String xincludeURI) {
        try {
            return URLDecoder.decode(xincludeURI.replace(
                    XMLUtil.NS_ONTOMAVEN.getURI() + "?uri=", ""), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getRestrictionFragment(Stack<String> restrictHistory) {
        StringBuilder buf = new StringBuilder();
        int domainCounter = 1;
        for (String restrictName : restrictHistory) {
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

    private boolean isTitling(Element e) {
        return e.getName().equals("Titling");
    }

    private boolean isImport(Element e) {
        return e.getName().equals("Import");
    }

    private boolean isRestrict(Element e) {
        return e.getName().equals("Restrict");
    }

    private boolean isXinclude(Element e) {
        return e.getName().equals("xinclude");
    }

    /**
     * Returns the name of a CL element
     * 
     * @param e
     * @return
     */
    private String getName(Element e) {
        Element nameElement = e.getChild("Name", NS_XCL2);
        return nameElement == null ? null : nameElement
                .getAttributeValue("cri");
    }

    private class ElementPair {

        public ElementPair(Element original, Element replacement) {
            this.original = original;
            this.replacement = replacement;
        }

        Element original;
        Element replacement;
    }

}
