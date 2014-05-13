package de.csw.ontomaven;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.csw.ontomaven.util.AspectManager;
import de.csw.ontomaven.util.Util;

/**
 * Applys the choosen aspects of the ontology and creates a new result ontology
 * names of in a given ontology existing aspects.
 * 
 * @goal RemoveAspects
 */
public class RemoveAspects extends AbstractMojo {

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
	 * Name of the result ontology file.
	 * 
	 * @parameter	property="owlFileWithoutAspectsName"
	 * 				default-value="ontologyWithoutAspects.owl"
	 * @required
	 */
	private String owlFileWithoutAspectsName;

	/**
	 * Name of the result ontology file.
	 * 
	 * @parameter	property="owlFilesWithoutAspectsDirectory"
	 * 				default-value="target/owlFilesWithoutAspects"
	 * @required
	 */
	private String owlFilesWithoutAspectsDirectory;
	
	/**
	 * IRI of the aspect annotation property.
	 * 
	 * @parameter property="aspectsIRI"
	 * default-value="http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"
	 * 
	 */
	private String aspectsIRI;

	public void execute() throws MojoExecutionException {
		
		Log log = getLog();
		Util.printHead("Removing aspects from ontology...", log);

		// Loading ontology
		File owlFile = new File(owlDirectory + File.separator + owlFileName);
		OWLOntologyManager manager = Util.createManager();
		OWLOntology ontology = Util.loadOntologyFile(manager, log, owlFile);
		if (ontology == null) return; // Ontology not loaded

		// Getting aspects, to check if there are any aspects
		log.info("Checking aspect names...");
		AspectManager aspectManager = new AspectManager(manager, aspectsIRI,
				ontology, null);
		List<String> foundAspectNames = aspectManager.getAllAspectNames();
		log.info("");

		
		// If there are aspects, remove them
		if (foundAspectNames.isEmpty()) {
			log.info("No aspects found.");
		} else{
			log.info("These aspects were found:");
			for (String aspectName : foundAspectNames)
				log.info(" - " + aspectName);
			log.info("");
			log.info("Removing aspects...");
			aspectManager.removeAspects();
			log.info("Aspects succesfull removed.");
		}
		
		// Writing new ontology file
		File resultFile = new File(owlFilesWithoutAspectsDirectory
				+ File.separator + owlFileWithoutAspectsName);
		Util.saveOntology(ontology, resultFile, log);
				
		Util.printTail(log);
	}
}