package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderSchemaFactory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.csw.cl.importer.MainForMaven;

/**
 * Util class for somee XML tasks.
 */
public class XMLUtil {
	
	public static final Namespace NS_XCL2 = Namespace.getNamespace("http://iso-commonlogic.org/xcl2");
	public static final Namespace NS_ONTOMAVEN = Namespace.getNamespace("http://ontomaven.org");
	public static final Namespace NS_XINCLUDE = Namespace.getNamespace("http://www.w3.org/2001/XInclude");
	public static final Namespace NS_CATALOG = Namespace.getNamespace("urn:oasis:names:tc:entity:xmlns:xml:catalog");
	
	private static final SAXBuilder SAX_PARSER;
	private static final XMLOutputter XML_OUT;
	
	private static MessageDigest MD;
	
	private static Charset UTF8;

	
	
	static {
		Init.init(); // for using the library to building canonical xml
		
		try {
			MD = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		UTF8 = Charset.forName("UTF-8"); 
		
		// Initialize SAX parser
		
		// Java does not support Relax NG out of the box, so we have to use the Jing Bridge to the JAXP api.
//		System.setProperty(SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI, "com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory");
//		
//		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
//		
//		File schemaLocation = new File("/Users/ralph/Desktop/xcl2.rnc");
//		Schema schema = null;
//		try {
//			schema = schemaFactory.newSchema(schemaLocation);
//		} catch (SAXException e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//		
//		XMLReaderJDOMFactory factory = new XMLReaderSchemaFactory(schema);
//		SAX_PARSER = new SAXBuilder(factory);
//		SAX_PARSER.setIgnoringElementContentWhitespace(true);
		
		// Jing handles validation of xmlns attributes incorrectly. Therefore, we use a non-validating parser.
		SAX_PARSER = new SAXBuilder();

		// Initialize XML outputter
		XML_OUT = new XMLOutputter(Format.getCompactFormat());

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
		String contentAsString = XML_OUT.outputString(element);
		return getCanonicalXML(contentAsString);
	}
	
	private static String getCanonicalXML(String xmlString) {
		Canonicalizer canon = null;
		try {
			canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
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
			return SAX_PARSER.build(xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * Reads a xml document from a remote file.
	 */
	public static Document readDocFromURL(String urlAsString){
		try {
			InputSource in = MainForMaven.titlingLocationResolver.resolveEntity(urlAsString, null);
//			sax.setEntityResolver(MainForMaven.titlingLocationResolver);
			return SAX_PARSER.build(in);
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
		return getCanonicalXML(e1).equals(getCanonicalXML(e2));
	}
	
	public static void main(String[] args) {
		Document doc;
		try {
			doc = SAX_PARSER.build(new File("/Users/ralph/dev/git/Local Git Repository/OntoMaven/OntoMaven-CL/test/TestClImport/src/resource/caseL/input/myText-L1.xcl"));
			Element e = doc.getRootElement().getChild("Titling", NS_XCL2);
			System.out.println(getCanonicalXML(e));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
