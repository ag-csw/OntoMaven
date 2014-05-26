package de.csw.cl.importer.model;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import util.XMLUtil;
import de.csw.cl.importer.ContentLoader;
import de.csw.cl.importer.Main;

/**
 * Represents a import declaration in common logic. 
 */
public class Import {
	private String newURL;
	private String dowloadURL;
	private String fileName;
	private Element originalXMLElement;
	private boolean isRestrictImport;
	private String fileContent;
	private String originalURL;
	private String fragment;
	
	/**
	 * Standard constructor.
	 */
	public Import(String newURL, String downloadURL,
			Element originalXMLElement, boolean isRestrictImport,
			String originalURL, String fragment) {
		this.originalURL = originalURL;
		this.fragment = fragment;
		this.originalXMLElement = originalXMLElement;
		this.newURL = newURL;
		this.dowloadURL = downloadURL;
		this.isRestrictImport = isRestrictImport;

		Document xmlDocument = XMLUtil.readDocFromURL(downloadURL);
		fileContent = XMLUtil.getCanonicalXML(xmlDocument);
		fileName = String.valueOf(fileContent.hashCode()) + ".xml";
	}

	/**
	 * Returns the original url of the import element. This url is used to
	 * check which specific part of an importing document should be imported.
	 */
	public String getOriginalURL() {
		return originalURL;
	}

	/**
	 * Returns the fragment of the original URL. With this special part of an
	 * document can be imported.
	 */
	public String getFragment() {
		return fragment;
	}

	/**
	 * Returns the url which contains the original url of the import element and
	 * the URI of the restrict element if there was existing one.
	 */
	public String getNewURL() {
		return newURL;
	}

	/** Returns the file name of the document to import. */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns a string representation of this import which consists of the url
	 * and the file name of the import.
	 */
	public String toString(){
		return "[import: url=\"" + newURL +
				"\" filename=\"" + fileName + "\"]";
	}

	/** Returns the xml element, from which this import is created */
	public Element getOriginalXMLElement() {
		return originalXMLElement;
	}
	
	/** Returns the url, from which the document will be loaded. */
	public String getDownloadURL(){
		return dowloadURL;
	}

	/** Returns if this is a restrict import */
	public boolean isRestrictImport() {
		return isRestrictImport;
	}
	
	/** Returns the content of the target document as a String */
	public String getFileContent(){
		return fileContent;
	}
	
	/**
	 * Checks if this import declaration is equal to another one by 
	 * comparing the url's of them.
	 */
	public boolean equals(Import other){
		return newURL.equals(other.getNewURL());
	}

	/**
	 * Solves this import by loading the file and doing these things if
	 * necessary: saving the document, adding the to the catalog, replacing
	 * the import element with an include element, and filtering the parts
	 * of an document, if only one part of it should be imported
	 */
	public void solve() {
		System.out.println();
		System.out.println("-------------------------------------------------- ");
		System.out.println("- Solving import: " + originalURL);
		System.out.println("-------------------------------------------------- ");

		// Loading the document to import.
		Document loadedDocument = XMLUtil.readDocFromURL(dowloadURL);
		Element loadedRoot = loadedDocument.getRootElement();
		System.out.println("Document has been loaded.");
		
		// If loaded document inconsistent ->
		// Dont't save or don't use it for transitive imports
		if (!ContentLoader.isConsistent(loadedDocument)) {
			System.err.println("Import " + originalURL + " is inconsistent");
			return;
		}
		System.out.println("Loaded document is consistent.");

		// Removing the parts (construct, titling etc) which should not be
		// imported. This is necessary if the URI has a fragment. Only the part
		// of the document, whose name is equalent to the original URI with
		// the fragment
		if (!fragment.equals("")) {
			for (Element part : loadedRoot.getChildren()) {
				Element nameChild = part.getChild("Name", part.getNamespace());
				if (nameChild != null) {
					String name = nameChild.getAttributeValue("cri");
					if (name != null && !name.equals(originalURL))
						loadedRoot.removeContent(part);
				}
			}
		}
		
		// Replacing import element in the xml with an include element
		originalXMLElement.setName("include");
		Namespace includeNameSpace = Namespace.getNamespace
				("xi", "http://www.w3.org/2001/XInclude");
		originalXMLElement.getDocument().getRootElement()
				.addNamespaceDeclaration(includeNameSpace);
		originalXMLElement.setNamespace(includeNameSpace);
		for (Attribute attribute : originalXMLElement.getAttributes())
			originalXMLElement.removeAttribute(attribute);
		originalXMLElement.setAttribute("href", newURL);
		originalXMLElement.setAttribute("parse", "xml");
		for (Element child : originalXMLElement.getChildren())
			originalXMLElement.removeContent(child);
		System.out.println("Import element has been replaced with include element.");
		
		// Removing the wrapper titling element
		ContentLoader.activateTitling(originalXMLElement, false);		

		// Getting java representation of the currently loaded document
		// If it contains imports, first they have to been solved
		// If not, the file can be directly saved
		if (ContentLoader.loadConstruct(loadedDocument.getRootElement())
				.hasAnyImports()) {
			Main.documentsToDo.add(loadedDocument);
		} else {
			String fileNameOfLoaded = XMLUtil.getCanonicalXML(loadedDocument)
					.hashCode() + ".xml";
			ContentLoader.saveInclude(loadedDocument, fileNameOfLoaded);
		}
		
		// Saving the document of this import, if it does not have more imports
		Document documentOfThisImport = originalXMLElement.getDocument();
		if (documentOfThisImport != Main.mainDocument && !ContentLoader.
			loadConstruct(documentOfThisImport.getRootElement()).hasAnyImports()) {
			String fileName = String.valueOf(XMLUtil.getCanonicalXML(
					documentOfThisImport).hashCode());
			ContentLoader.saveInclude(documentOfThisImport, fileName);
		}
		
		System.out.println("--------------------------------------------------");
		System.out.println();
	}
}