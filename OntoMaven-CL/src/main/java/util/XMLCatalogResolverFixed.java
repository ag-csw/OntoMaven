/**
 * 
 */
package util;

import java.io.IOException;

import org.apache.xerces.util.XMLCatalogResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author ralph
 *
 */
public class XMLCatalogResolverFixed extends XMLCatalogResolver {


	public XMLCatalogResolverFixed(String[] catalogs) {
		super(catalogs);
	}
	
	@Override
	public InputSource resolveEntity(String name, String publicId,
			String baseURI, String systemId) throws SAXException, IOException {
		InputSource result = super.resolveEntity(name, publicId, baseURI, systemId);
		if (result == null) {
			String resolvedURI = resolveURI(systemId);
			if (resolvedURI != null) {
				result = new InputSource(resolvedURI);
				result.setPublicId(publicId);
			}
		}
		return result;
	}

}
