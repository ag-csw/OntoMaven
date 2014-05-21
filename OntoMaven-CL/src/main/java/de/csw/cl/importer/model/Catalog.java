package de.csw.cl.importer.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import util.XMLUtil;

/**
 * Represents and manages a catalog for common logic files. In this catalog
 * the imports of a common logic document (also transitiv) will be mapped
 * to the local file names of the import files.
 */
public class Catalog {

	/**
	 * The list of the currently existing imports. It contains originally
	 * existing imports and the added imports.
	 */
	List<Import> existingImports;

	/**
	 * Main consturctor of the catalog. Creates a empty list of
	 * import declarations
	 */
	public Catalog() {
		existingImports = new LinkedList<Import>();
	}

	/**
	 * Adds a given import declaration to the list of the existing declarations
	 * 
	 * @param import to add
	 */
	public void addImport(Import importToAdd) {
		if (!existingImports.contains(importToAdd))
			existingImports.add(importToAdd);
	}

	/**
	 * Removes an import from the list of the existing declarations
	 * 
	 * @param importToRemove
	 */
	public void removeImport(Import importToRemove) {
		if(existingImports.contains(importToRemove))
			existingImports.remove(importToRemove);
	}

	/**
	 * Checks if a import declaration is already existing in catalog.
	 * 
	 * @param clImport to check for
	 * @return if import already existing
	 */
	public boolean containsImportDeclaration(Import clImport) {
		for (Import existingImport : existingImports) {
			if (existingImport.equals(clImport))
				return true;
		}
		return false;
	}

	/**
	 * Returns the list of the existing imports.
	 * 
	 * @return list of existing imports
	 */
	public List<Import> getImports() {
		return existingImports;
	}

	/**
	 * Saves the catalog document into the catalog file
	 */
	public void saveCatalogFile() {
		System.out.println("Writing catalog file...");
		Document catalogDocument = new Document();
		Element root = new Element("catalog",
				"urn:oasis:names:tc:entity:xmlns:xml:catalog");
		root.setAttribute("prefer", "public");
		catalogDocument.setRootElement(root);
		for (Import clImport : existingImports) {
			Element newElement = new Element("uri");
			newElement.setAttribute("name", clImport.getNewURL());
			newElement.setAttribute("uri", clImport.getFileName());
			newElement.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/XInclude"));
			catalogDocument.getRootElement().addContent(newElement);
		}
		XMLUtil.writeXML(catalogDocument, new File("catalog.xml"));
	}
}
