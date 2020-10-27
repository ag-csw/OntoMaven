package de.csw.ontomaven;

import java.io.File;
import java.util.Optional;

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
	 * 				default-value="aspectsAppliedOwlDirectory"
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
	 * If true it handles axioms with no aspects as if they would have every aspect, i.e. it will keep axioms that have no aspects.
	 *
	 * @parameter property="keepNonAspectAxioms"
	 * default-value="false"
	 */
	private boolean keepNonAspectAxioms;

	public String getOwlDirectory() {
		return owlDirectory;
	}

	public void setOwlDirectory(String owlDirectory) {
		this.owlDirectory = owlDirectory;
	}

	public String getOwlFileName() {
		return owlFileName;
	}

	public void setOwlFileName(String owlFileName) {
		this.owlFileName = owlFileName;
	}

	public String getAspectsAppliedOwlFileName() {
		return aspectsAppliedOwlFileName;
	}

	public void setAspectsAppliedOwlFileName(String aspectsAppliedOwlFileName) {
		this.aspectsAppliedOwlFileName = aspectsAppliedOwlFileName;
	}

	public String getAspectsAppliedOwlDirectory() {
		return aspectsAppliedOwlDirectory;
	}

	public void setAspectsAppliedOwlDirectory(String aspectsAppliedOwlDirectory) {
		this.aspectsAppliedOwlDirectory = aspectsAppliedOwlDirectory;
	}

	public String getAspectsIRI() {
		return aspectsIRI;
	}

	public void setAspectsIRI(String aspectsIRI) {
		this.aspectsIRI = aspectsIRI;
	}

	public String[] getUserAspects() {
		return userAspects;
	}

	public void setUserAspects(String[] userAspects) {
		this.userAspects = userAspects;
	}

	public void setKeepNonAspectAxioms(boolean keepNonAspectAxioms) {
		this.keepNonAspectAxioms = keepNonAspectAxioms;
	}

	/**
	 * Aspects which are given from the user and will apply
	 * 
	 * @parameter
	 */
	private String[] userAspects;

	public void execute() throws MojoExecutionException {
		owlDirectory = "target/" + owlDirectory;
		aspectsAppliedOwlDirectory = "target/" + aspectsAppliedOwlDirectory;

		for (String userAspect: userAspects)
			System.out.println(userAspect);
		
		Log log = getLog();
		Util.printHead("Applying aspects on ontology...", log);

		// Loading ontology
		File owlFile = new File(owlDirectory + File.separator + owlFileName);
		OWLOntologyManager manager = Util.createManager();
		Optional<OWLOntology> oontology = Util.loadOntologyFile( manager, log, owlFile );
		if (!oontology.isPresent()) return; // Ontology not loaded

		OWLOntology ontology = oontology.get();


		// Applying aspects
		Util.applyAspects(manager, aspectsIRI, ontology, userAspects, log, keepNonAspectAxioms);

		
		// Writing new ontology file
		File resultFile = new File(aspectsAppliedOwlDirectory + File.separator
				+ aspectsAppliedOwlFileName);
		Util.saveOntology(manager.ontologies().findFirst().get(), resultFile, log);
				
		Util.printTail(log);
	}

}