package de.csw.ontomaven;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.csw.ontomaven.util.AspectManager;
import de.csw.ontomaven.util.Util;

/**
 * Prints names of in a given ontology existing aspects.
 * 
 * @goal PrintAspectNames
 */
public class PrintAspectNames extends AbstractMojo {

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
	 * IRI of the aspect annotation property. All annotations which have
	 * this iri as annotation property value, will be classified as
	 * aspect annotations.
	 * 
	 * @parameter property="aspectsIRI"
	 * default-value="http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"
	 */
	private String aspectsIRI;

	/** Executes the printing of the aspect names */
	public void execute() throws MojoExecutionException {

		Log log = getLog();
		Util.printHead("Printing names of aspects...", log);

		// Step 1: Loading ontology
		OWLOntologyManager manager = Util.createManager();
		File owlFile = new File(owlDirectory + File.separator + owlFileName);
		Optional<OWLOntology> oontology = Util.loadOntologyFile( manager, log, owlFile );
		if (!oontology.isPresent()) {
			log.warn("Could not load ontology " + owlFile.getAbsolutePath() );
			return;
		}
		OWLOntology ontology = oontology.get();


		// Step 3: Getting all aspect names
		log.info("Collecting aspect names...");
		List<String> foundAspectNames = new AspectManager(manager, aspectsIRI,
				ontology, null).getAllAspectNames();
		log.info("");

		
		// Step 4: Printing result
		if (foundAspectNames.isEmpty()) {
			log.info("No aspect names found.");
		} else{
			log.info("These aspect names were found:");
			for (String aspectName : foundAspectNames)
				log.info(" - " + aspectName);
		}

		Util.printTail(log);
	}
}