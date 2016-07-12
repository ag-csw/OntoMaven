/**
 * 
 */
package util;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.sax.XMLReaderSchemaFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * This class implements a custom JDOM {@link XMLReaderJAXPFactory} which
 * provides us with the liberty to create our own SAXParserFactory, configure it
 * to handle Xincludes and let the JDOM SaxBuilder use this reader.
 * 
 * @author ralph
 * 
 */
public class XincludeAwareXMLReaderSchemaFactory extends XMLReaderSchemaFactory {

	private Schema schema;

	/**
	 * @see XMLReaderSchemaFactory
	 * @param schema
	 */
	public XincludeAwareXMLReaderSchemaFactory(Schema schema) {
		super(schema);
		this.schema = schema;
	}
	
	
	@Override
	public XMLReader createXMLReader() throws JDOMException {
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		saxFactory.setNamespaceAware(true);
		saxFactory.setXIncludeAware(true);
		saxFactory.setValidating(false);
		saxFactory.setSchema(schema);
		try {
			SAXParser parser = saxFactory.newSAXParser();
			return parser.getXMLReader();
		} catch (ParserConfigurationException e) {
			throw new JDOMParseException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new JDOMParseException(e.getMessage(), e);
		}
	}
}
