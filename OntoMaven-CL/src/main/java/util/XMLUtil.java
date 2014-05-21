package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Util class for somee XML tasks.
 */
public class XMLUtil {
	
	/**
	 * Returns a cononilized form of the content of a xml document
	 * as string.
	 */
	public static String getCanonicalXML(Document document){
		Canonicalizer canon = null;
		try {
			canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
		} catch (InvalidCanonicalizerException e1) {
			e1.printStackTrace();
		}
		String contentAsString = new XMLOutputter().outputString(document);
		byte canonXmlBytes[] = new byte[1];
		try {
			canonXmlBytes = canon.canonicalize(contentAsString.getBytes());
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
	
	/**
	 * Reads a xml document from a given local file.
	 */
	public static Document readLocalDoc(File xmlFile) {
		try {
			return new SAXBuilder().build(xmlFile);
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
			return new SAXBuilder().build(new URL(urlAsString));
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
}
