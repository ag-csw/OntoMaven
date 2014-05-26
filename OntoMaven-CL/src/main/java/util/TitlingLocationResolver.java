/**
 * 
 */
package util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.util.IteratorIterable;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author ralph
 */
public class TitlingLocationResolver implements EntityResolver {
	
	private static final Logger LOG = Logger.getLogger(TitlingLocationResolver.class);
	
	private HashMap<String, InputSource> titlingLocationMap;
	
	/**
	 * Constructs and intitializes a TitlingLocationResolver with a given set of base directories.  
	 * 
	 * @param baseDirectories
	 */
	public TitlingLocationResolver(File... baseDirectories) {
		
		LOG.debug("Initializing with baseDirectories: " + baseDirectories);
		
		titlingLocationMap = new HashMap<String, InputSource>();
		
		for (File sourceFolder : baseDirectories) {
			File[] xclFiles = sourceFolder.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.isFile() && file.getName().toLowerCase().endsWith(".xcl");
				}
			});
			
			for (File xclFile : xclFiles) {
				Document doc = XMLUtil.readLocalDoc(xclFile);
				IteratorIterable<Element> titlingElements = doc.getDescendants(new ElementFilter("Titling"));
				for (Element titlingElement : titlingElements) {
					Element nameElement = titlingElement.getChild("Name", Namespace.getNamespace("http://iso-commonlogic.org/xcl2"));
					String titlingName = nameElement.getAttributeValue("cri");
					try {
						titlingLocationMap.put(titlingName, new InputSource(xclFile.toURI().toURL().toExternalForm()));
					} catch (MalformedURLException e) {
						LOG.warn("Could not create systemId for file " + xclFile.getAbsolutePath(), e);
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		
		return titlingLocationMap.get(publicId);
	}
}
