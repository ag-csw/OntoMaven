package de.csw.ontomaven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.csw.ontomaven.util.Util;

/**
 * Applys the choosen aspects of the ontology and creates a new result ontology
 * names of in a given ontology existing aspects.
 * 
 * @goal ApplyAspects
 */
public class ApplyAspects extends AbstractMojo {

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
	 * @parameter	property="aspectsAppliedOwlFileName"
	 * 				default-value="aspectsAppliedOntology.owl"
	 * @required
	 */
	private String aspectsAppliedOwlFileName;

	/**
	 * Name of the result ontology file.
	 * 
	 * @parameter	property="aspectsAppliedOwlDirectory"
	 * 				default-value="target/aspectsAppliedOwlDirectory"
	 * @required
	 */
	private String aspectsAppliedOwlDirectory;
	
	/**
	 * IRI of the aspect annotation property.
	 * 
	 * @parameter property="aspectsIRI"
	 * default-value="http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"
	 * 
	 */
	private String aspectsIRI;
	
	/**
	 * Aspects which are given from the user and will apply
	 * 
	 * @parameter
	 */
	private String[] userAspects;

	public void execute() throws MojoExecutionException {
		for (String userAspect: userAspects)
			System.out.println(userAspect);
		
		Log log = getLog();
		Util.printHead("Applying aspects on ontology...", log);

		// Loading ontology
		File owlFile = new File(owlDirectory + File.separator + owlFileName);
		OWLOntologyManager manager = Util.createManager();
		OWLOntology ontology = Util.loadOntologyFile(manager, log, owlFile);
		if (ontology == null) return; // Ontology not loaded
		
		
		// Applying aspects
		Util.applyAspects(manager, aspectsIRI, ontology, userAspects, log);

		
		// Writing new ontology file
		File resultFile = new File(aspectsAppliedOwlDirectory + File.separator
				+ aspectsAppliedOwlFileName);
		Util.saveOntology(ontology, resultFile, log);
				
		Util.printTail(log);
	}
}