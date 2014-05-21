package de.csw.ontomaven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;

import de.csw.ontomaven.util.Util;

/**
 * Loads and saves all (also transitiv) imports. The loaded ontologies
 * will be saved in the specified directory. Every imported ontology
 * will also be registered in a catalog. All goals will work with the
 * local ontologies, if they are imported before.
 * 
 * @goal ImportOntologies
 */
public class ImportOntologies extends AbstractMojo {
  
	/**
	 * Working directory, where owl files are stored. It should be
	 * a relative path in the maven project directory.
	 * 
	 * @parameter 	property="owlDirectory"
	 * 				default-value="owl"
	 * @required
	 */
	private String owlDirectory;
	
	/**
	 * Name of the ontology file, which should be in the working directory.
	 * It should be a name like "myOntology.owl".
	 *
	 * @parameter 	property="owlFileName"
	 * 				default-value="ontology.owl"
	 * @required
	 */
	private String owlFileName;
	
	/**
	 * Path of directory, where to save imported ontologies and
	 * the catalog file. This directory will be created in the
	 * owl directory.
	 * 
	 * @parameter 	property="importDirectory"
	 * 				default-value="imports"
	 * @required
	 */
	private String importDirectory;
	
	/**
	 * Name of the catalog file, where the imports are registered.
	 * 
	 * @parameter 	property="catalogFileName"
	 * 				default-value="catalog.xml"
	 * @required
	 */
	private String catalogFileName;
    
	public void execute() throws MojoExecutionException {
	
		Log log = getLog();
		Util.printHead("Importing ontologies...", log);
		
		// Saving directory
		File importsDir = new File(owlDirectory + File.separator
				+ importDirectory);
		importsDir.mkdirs();

		
		
		// Loading main ontology
		File owlFile = new File(owlDirectory + File.separator + owlFileName);
		OWLOntology ontology = Util.loadOntologyByIgnoringMissingImports(
				null, log, new FileDocumentSource(owlFile));
		if (ontology == null) return; // ontology not loaded
		
		
		// Creating catalog
		File catalogFile = new File(owlDirectory + File.separator
				+ catalogFileName);
		Catalog catalog = new Catalog(catalogFile, log);
		
		
		// Initialization of the list of the imports. Here the import
		// declarations of the main ontology will added to it
		// In the loop also transitive declarations will be added
		// While this list is not empty, imports will be downloaded
		LinkedList<OWLImportsDeclaration> toImport =
				new LinkedList<OWLImportsDeclaration>();
		toImport.addAll(ontology.getImportsDeclarations());
		
		log.info("");
		log.info("");
		
		// Get recursiv all dependencies of the ontology
		int counter = 0; // for logging result
		while (!toImport.isEmpty()) {
			
			OWLImportsDeclaration currentDeclaration = toImport.pop();
			IRI currentIRI = currentDeclaration.getIRI();
			
			// some logging
			counter++;
			log.info(".........................................................................");
			log.info(" Import " + counter + ": " + currentIRI.toString());
			log.info(".........................................................................");

			// Check if can load ontology, if no -> skip this import declaration
			if(!Util.canLoadOntologyByIgnoringMissingImports(currentIRI)){
				log.error("Ontology cannot be loaded.");
				log.info(".........................................................................");
				log.info("");
				log.info("");
				continue;
			}
			
			// Loading the current ontology, it have to be loadable
			OWLOntology currentOntology = Util
					.loadOntologyByIgnoringMissingImports(Util.createManager(),
							log, new IRIDocumentSource(currentIRI));
			String currentURL = currentIRI.toString();
			
			// If the import is not already existing, add it to the catalog
			// and save the ontology. We could maybe also 
			if (!catalog.isImportExisting(currentURL)){
				String fileName = createFileName(importsDir, currentIRI);
				catalog.addImport(currentURL, fileName);
				File saveFile = new File(importsDir.getAbsolutePath() +
						File.separator + fileName);
				Util.saveOntology(currentOntology, saveFile, log);
			} else {
				log.info("Ontology already existing and won't be imported.");
			}
			
			// Take the imports of the current ontlogy, if they are
			// not already existing in the IRI or declaration list
			for (OWLImportsDeclaration decl : currentOntology.getImportsDeclarations()) {
				if (!toImport.contains(decl) && !catalog.isImportExisting(decl.toString()))
					toImport.add(decl);
			}
			
			// some logging
			log.info(".........................................................................");
			log.info("");
			log.info("");
		}
		
		log.info("Importig done.");
		catalog.saveCatalog();
		Util.printTail(log);
	}
	
	/**
	 * Finds a valid file name for a iri by comparing its name with
	 * the names of the existing files.
	 * 
	 * @param directory of the comparing files
	 * @param iri from that the fill name will be created
	 * @return
	 */
	private static String createFileName(File directory, IRI iri) {
		
		// Get the name in the IRI. Maybe there is no name, than the
		// created name will be an empty string
		String iriAsString = iri.toString();
		String fileName = FilenameUtils.getBaseName(iriAsString)
				+ "." +FilenameUtils.getExtension(iriAsString);
		
		// if the filename was empty, name the file new_ontology.owl
		if (fileName == "")
			fileName = "new_ontology.owl";
		
		// if the filename was not empty but it did not have the
		// ".owl"-extenstion, add this extension to it.
		if (!fileName.endsWith(".owl"))
			fileName += ".owl";
			
		// Check if there is a file with the same name
		boolean isNameValid = true;
		for (File existingFile : directory.listFiles()) {
			if (existingFile.getName().equals(fileName)) {
				isNameValid = false;
				break;
			}
		}
		if (isNameValid)
			return fileName;

		// Maybe there is already a file with the same name as the until
		// now created file name
		String fileNameWithoutExtension = fileName.split(".owl")[0];
		for (int i = 2; i<Integer.MAX_VALUE; i++){
			System.out.println("aaa");
			// 1: Create a file name by adding a "_" and a number
			boolean isCurrentNameValid = true;
			String currentFileName = fileNameWithoutExtension + "_" +
				String.valueOf(i) + ".owl";
			
			// Check if there is a file with exact this name
			for (File existingFile: directory.listFiles()){
				if (existingFile.getName().equals(currentFileName)){
					isCurrentNameValid = false;
					break;
				}
			}
			
			// If there was not a file with this name, take the new name
			if (isCurrentNameValid){
				fileName = currentFileName;
				break;
			}
		}
		
		return fileName;
	}
}

/** Represents a xml catalog for mapping url's on local file names */
class Catalog{
	private File catalogFile;
	private Document catalogDocument;
	private List<ImportDeclaration> imports;
	private Log log;
	
	
	public Catalog(File catalogFile, Log log){
		this.catalogFile = catalogFile;
		this.log = log;
		catalogDocument = loadCatalogDocument(catalogFile);
		imports = readImports();
	}
	
	/** Reads the imports in the catalog file to the imports list */
	private List<ImportDeclaration> readImports() {
		List<ImportDeclaration> foundImports = new LinkedList<ImportDeclaration>();
		Element root = catalogDocument.getRootElement();
		for (Element importElement: root.getChildren()){
			String url = importElement.getAttributeValue("name");
			String fileName = importElement.getAttributeValue("uri");
			foundImports.add(new ImportDeclaration(url, fileName));
		}
		return foundImports;
	}

	/** Creates a fresh new catalog by adding all existing imports to it */
	private Document createFinalCatalog(){
		Document finalCatalog = createEmptyCatalogDocument();
		
		for (ImportDeclaration existingImport:imports){
			Element importElement = new Element("uri");
			importElement.setAttribute("id", "User Entered Import Resolution");
			importElement.setAttribute("name", existingImport.getUrl());
			importElement.setAttribute("uri", existingImport.getFileName());
			finalCatalog.getRootElement().addContent(importElement);
		}
		return finalCatalog;
	}
	
	/** Loads from a given file the xml catalog as document */
	private Document loadCatalogDocument(File catalogFile){
		if (catalogFile.exists()){
			try {
				Document catalog = new SAXBuilder().build(catalogFile);
				if (catalog.getRootElement() == null) {
					log.info("Catalog is empty. Will create fresh one.");
					return createEmptyCatalogDocument();
				}
				log.info("Catalog document succesfull loaded.");
				return catalog;
			} catch (Exception e) {
				log.error("Cannot load catalog document from file and"
						+ " will create a new empty catalog."
						+ catalogFile.getAbsolutePath(),e);
				return createEmptyCatalogDocument();
			}
		} else {
			log.info("Catalog file not existing. Will create a new"
					+ " empty catalog.");
			return createEmptyCatalogDocument();
		}
	}
	
	/** Adds a new import declaration to the catalog */
	public void addImport(String url, String fileName){
		
		Element newEntry = new Element("uri");
		newEntry.setAttribute("id", "User Entered Import Resolution");
		newEntry.setAttribute("name", url);
		newEntry.setAttribute("uri", fileName);
		catalogDocument.getRootElement().addContent(newEntry);
		log.info(url + " has been added to catalog.");
		
		imports.add(new ImportDeclaration(url, fileName));
	}
	
	/** Returns a new empyt catalog document with a root */
	private Document createEmptyCatalogDocument(){
		Document emptyCatalog = new Document();
		Element root = new Element("catalog", "urn:oasis:names:tc:entity:xmlns:xml:catalog");
		root.setAttribute("prefer", "public");
		emptyCatalog.setRootElement(root);
		return emptyCatalog;
	}
	
	/** Checks if an import is existing by comparing url's */
	public boolean isImportExisting(String url){
		for (ImportDeclaration existingImport: imports){
			if (existingImport.getUrl().equals(url))
				return true;
		}
		return false;
	}
	
	/** Saves the catalog document into the catalog file */
	public void saveCatalog(){
		log.info("");
		log.info("Writing catalog file...");
		Document finalCatalog = createFinalCatalog();
		XMLOutputter xmlOutput = new XMLOutputter();
 		xmlOutput.setFormat(Format.getPrettyFormat());
		try {
			xmlOutput.output(finalCatalog, new FileWriter(catalogFile));
			log.info("Catalog file saved: " + catalogFile.getAbsolutePath());
		} catch (IOException e) {
			log.error("Cannot write " + catalogFile.getAbsolutePath(), e);
		}
		log.info("");
	}
}

/** Represents a import declaration consisting on a url and filename.*/
class ImportDeclaration{
	private String url;
	private String fileName;
	
	/** Standard constructor */
	public ImportDeclaration(String url, String fileName){
		this.url = url;
		this.fileName = fileName;
	}
	
	/** Getter for url */
	public String getUrl(){
		return url;
	}
	
	/** Getter for filename */
	public  String getFileName(){
		return fileName;
	}

}