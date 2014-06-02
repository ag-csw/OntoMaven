package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.http.client.utils.URIBuilder;
import org.apache.xerces.util.XMLCatalogResolver;
import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

/**
 * Util class for somee XML tasks.
 */
public class XMLUtil {
	
	public enum XMLParserCapability {SIMPLE, RESOLVE_XINLUDE, VALIDATE_AND_RESOLVE_XINCLUDE};
	
	public static final Namespace NS_XCL2 = Namespace.getNamespace("http://iso-commonlogic.org/xcl2");
	public static final Namespace NS_ONTOMAVEN = Namespace.getNamespace("http://ontomaven.org");
	public static final Namespace NS_XINCLUDE = Namespace.getNamespace("http://www.w3.org/2001/XInclude");
	public static final Namespace NS_CATALOG = Namespace.getNamespace("urn:oasis:names:tc:entity:xmlns:xml:catalog");
	
	private static final XMLOutputter XML_OUT;
	
	private static MessageDigest MD;
	
	private static Charset UTF8 = Charset.forName("UTF-8");;

	static {
		Init.init(); // for using the library to building canonical xml
		
		try {
			MD = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		// Initialize SAX parser
		
		// Java does not support Relax NG out of the box, so we have to use the Jing Bridge to the JAXP api.
		System.setProperty(SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI, "com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory");
		
		// Initialize XML outputter
		XML_OUT = new XMLOutputter(Format.getCompactFormat().setExpandEmptyElements(true).setOmitDeclaration(true));

	}
	
	public static SAXBuilder createSaxBuilder(XMLParserCapability capability, URI baseURI, URL schemaURL) {

		SAXBuilder saxBuilder = new SAXBuilder();

		switch (capability) {
			case VALIDATE_AND_RESOLVE_XINCLUDE:
				// configure sax builder for validation
				SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
				Schema schema = null;
				try {
					schema = schemaFactory.newSchema(schemaURL);
					XincludeAwareXMLReaderSchemaFactory factory = new XincludeAwareXMLReaderSchemaFactory(schema);
					saxBuilder.setXMLReaderFactory(factory);
				} catch (SAXException e) {
					e.printStackTrace();
					System.exit(1);
				}
			case RESOLVE_XINLUDE:
				// configure sax builder for xinclude handling
				
				URIBuilder uriBuilder = new URIBuilder(baseURI);
				uriBuilder.setPath((uriBuilder.getPath() + "/catalog.xml").replaceAll("//+", "/"));
				URI catalogURI = URI.create(uriBuilder.toString());

				XMLCatalogResolverFixed catalogResolver = new XMLCatalogResolverFixed(new String[] {catalogURI.toASCIIString()});
				saxBuilder.setEntityResolver(catalogResolver);
				break;
			case SIMPLE:
			default:
				break;
		}

		return saxBuilder;
	}
	
	/**
	 * Returns a cononilized form of the content of a xml document
	 * as string.
	 */
	public static String getCanonicalXML(Document document){
		String contentAsString = XML_OUT.outputString(document);
		return getCanonicalXML(contentAsString);
	}
	
	/**
	 * Returns a canonicalized representation of the XML subtree of which
	 * element is the root.
	 * 
	 * @param element
	 * @return
	 */
	public static String getCanonicalXML(Element element) {
		element = element.clone();
		
		// Collect prefixes
		final HashMap<String, String> prefixes = new HashMap<String, String>();
		final HashSet<Element> toDelete = new HashSet<Element>();
		
		performRecursivelAction(element, new Action() {
			public void doAction(Element e) {
				if (e.getName().equals("Prefix")) {
					prefixes.put(e.getAttributeValue("pre"), e.getAttributeValue("iri"));
					toDelete.add(e);
				}
			}
		});
		
		for (Element prefix : toDelete) {
			prefix.detach();
		}

		final HashSet<Element> dataAndNonIriNamesWithoutSymbolEdge = new HashSet<Element>();
		
		performRecursivelAction(element, new Action() {
			public void doAction(Element e) {
				// resolve all CURIES
				Attribute criAttr = e.getAttribute("cri");
				if (criAttr != null) {
					String cri = criAttr.getValue();
					String[] split = cri.split(":");
					if (split.length == 2) {
						String prefix = split[0];
						String iri = prefixes.get(prefix);
						if (iri != null) {
							criAttr.setValue(iri + split[1]);
						}
					}
				}
				
				// remove key attributes				
				e.removeAttribute("key");

				// make the <symbol> edge explicit in non-IRI Names and Data elements
				if (e.getName().equals("Name") && criAttr == null ||
						e.getName().equals("Data")) {
					if (e.getChildren().isEmpty()) {
						String text = e.getTextNormalize();
						if (text.length() != 0) {
							dataAndNonIriNamesWithoutSymbolEdge.add(e);
						}
					}
				}
			}
		});
		
		for (Element dataOrNonIriNameWithoutSymbolEdge : dataAndNonIriNamesWithoutSymbolEdge) {
			String text = dataOrNonIriNameWithoutSymbolEdge.getTextNormalize();
			dataOrNonIriNameWithoutSymbolEdge.setText("");
			Element symbolElement = new Element("symbol", NS_XCL2);
			dataOrNonIriNameWithoutSymbolEdge.addContent(symbolElement);
			symbolElement.setText(text);
		}
		
		String contentAsString = XML_OUT.outputString(element);
		return getCanonicalXML(contentAsString);
	}
	
	public static interface Action {
		public void doAction(Element e);
	}
	
	public static void performRecursivelAction(Element e, Action action) {
		action.doAction(e);
		List<Element> children = e.getChildren();
		for (Element child : children) {
			performRecursivelAction(child, action);
		}
	}
	
	private static String getCanonicalXML(String xmlString) {
		Canonicalizer canon = null;
		try {
			canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
			
		} catch (InvalidCanonicalizerException e1) {
			e1.printStackTrace();
		}
		byte canonXmlBytes[] = new byte[1];
		try {
			canonXmlBytes = canon.canonicalize(xmlString.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(canonXmlBytes);
	}
	
	/**
	 * Formats the content of a given xml document and returns it formatted.
	 */
	public static String formatXML(String input) {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			org.dom4j.Document document = DocumentHelper.parseText(input);
			StringWriter stringWriter = new StringWriter();
			XMLWriter xmlWriter = new XMLWriter(stringWriter, format);
			xmlWriter.write(document);
			return stringWriter.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getMD5Hash(Element element) {
		String canonicalXmlString = getCanonicalXML(element);
		byte[] hash = MD.digest(canonicalXmlString.getBytes(UTF8));
        
        //converting byte array to Hexadecimal String
       StringBuilder sb = new StringBuilder(2*hash.length);
       for(byte b : hash){
           sb.append(String.format("%02x", b&0xff));
       }
      
       return sb.toString();
	}
	
	/**
	 * Reads a xml document from a given local file.
	 */
	public static Document readLocalDoc(File xmlFile) {
		try {
			SAXBuilder saxBuilder = createSaxBuilder(XMLParserCapability.SIMPLE, null, null);
			return saxBuilder.build(xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	public static Document readAndValidate(File xmlFile, URL schemaURL) {
		URI baseURI = xmlFile.getParentFile().toURI();
		SAXBuilder validator = createSaxBuilder(XMLParserCapability.VALIDATE_AND_RESOLVE_XINCLUDE, baseURI, schemaURL);
		try {
			validator.build(xmlFile);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return readLocalDoc(xmlFile);
	}
	
	/**
	 * Reads a xml document from a remote file.
	 */
	public static Document readDocFromURL(URL url) {
		try {
			return createSaxBuilder(XMLParserCapability.SIMPLE, null, null).build(url);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Writes a given xml document into a given file.
	 */
	public static void writeXML(Document document, File file) {
		XMLOutputter xmlOutput = new XMLOutputter();
 		xmlOutput.setFormat(Format.getPrettyFormat());
		try {
			xmlOutput.output(document, new FileWriter(file));
		} catch (IOException e) {
		}
	}
	
	// ===========================
	
	/**
	 * Checks if two xml subtrees are equal.
	 * @param e1
	 * @param e2
	 * @return
	 */
	public static boolean equal(Element e1, Element e2) {
		String aaaa = getCanonicalXML(e1);
		String bbbb = getCanonicalXML(e2);
		boolean result = aaaa.equals(bbbb);
		if (result == false) {
			System.out.println("O lala");
		}
		return result;
	}	
}
