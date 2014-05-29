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
	
	public void addMapping(String uri, String fileHash) {
		uriMappings.put(uri, fileHash);
	}
	
	public String getFileHash(String uri) {
		return uriMappings.get(uri);
	}
	
	public void write() {
	    if (uriMappings.size() > 0) {
    		Document catalogDocument = new Document();
    		catalogDocument.setDocType(new DocType("catalog",
    						"-//OASIS//DTD XML Catalogs V1.1//EN",
    						"http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd"));
    		Element root = new Element("catalog", XMLUtil.NS_CATALOG);
    		root.setAttribute("prefer", "public");
    		catalogDocument.setRootElement(root);
    		for (Entry<String, String> mapping : uriMappings.entrySet()) {
    			Element newElement = new Element("uri");
    			newElement.setAttribute("name", mapping.getKey());
    			newElement.setAttribute("uri", "includes/" + mapping.getValue()+ ".xml");
    			newElement.setNamespace(XMLUtil.NS_CATALOG);
    			catalogDocument.getRootElement().addContent(newElement);
    		}
    		XMLUtil.writeXML(catalogDocument, catalogFile);
	    }
	}
	
	public static void main(String[] args) {
		XMLCatalog c = new XMLCatalog(new File("/tmp/catalog.xml"));
		c.addMapping("http://ontomaven.org?uri=http%3A%2F%2Fexample.org%2FcaseL%2FmyText-L1%23a", "1");
		c.addMapping("http://ontomaven.org?uri=http%3A%2F%2Fexample.org%2FcaseL%2FmyText-L1%23c", "2");
		c.write();
	}
}
