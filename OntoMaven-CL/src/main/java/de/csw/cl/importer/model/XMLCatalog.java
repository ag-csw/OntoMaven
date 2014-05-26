package de.csw.cl.importer.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;

import util.XMLUtil;

/**
 * 
 * @author ralph
 *
 */
public class XMLCatalog {
	
	private File catalogFile;
	
	private HashMap<String, String> uriMappings = new HashMap<String, String>();
	
	public XMLCatalog(File catalogFile) {
		this.catalogFile = catalogFile;
	}
	
	public void addMapping(String uri, String filePath) {
		uriMappings.put(uri, filePath);
	}
	
	public void write() {
		Document catalogDocument = new Document();
		catalogDocument.setDocType(new DocType("catalog",
						"-//OASIS//DTD XML Catalogs V1.1//EN",
						"http://www.oasis-open.org/committees/entity/release/1.1/catalog.dtd"));
		Element root = new Element("catalog", XMLUtil.NS_CATALOG);
		root.setAttribute("prefer", "public");
		catalogDocument.setRootElement(root);
		for (Entry<String, String> mapping : uriMappings.entrySet()) {
			Element newElement = new Element("uri");
			newElement.setAttribute("name", mapping.getKey());
			newElement.setAttribute("uri", mapping.getValue());
			newElement.setNamespace(XMLUtil.NS_CATALOG);
			catalogDocument.getRootElement().addContent(newElement);
		}
		XMLUtil.writeXML(catalogDocument, catalogFile);
	}
	
	public static void main(String[] args) {
		XMLCatalog c = new XMLCatalog(new File("/tmp/catalog.xml"));
		c.addMapping("http://ontomaven.org?uri=http%3A%2F%2Fexample.org%2FcaseL%2FmyText-L1%23a", "includes/1.xml");
		c.addMapping("http://ontomaven.org?uri=http%3A%2F%2Fexample.org%2FcaseL%2FmyText-L1%23c", "includes/2.xml");
		c.write();
	}
}
