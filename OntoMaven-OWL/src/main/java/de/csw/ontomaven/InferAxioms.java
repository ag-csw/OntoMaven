package de.csw.ontomaven;

import java.io.File;
import java.util.Optional;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.csw.ontomaven.util.Util;

/**
 * Export the axioms of the ontology into a file. The name of the
 * If wanted, export file can be set. Exports also inferred axioms
 * can be printed and aspects can be applied.
 * 
 * @goal InferAxioms
 */
public class InferAxioms extends AbstractMojo {
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

	/**
	 * If the goal should apply the aspects on the ontology before it
	 * works with the ontology.
	 * 
	 * @parameter 	expression=${ifApplyAspects}
	 *				defaul-value="false"
	 */
	private boolean ifApplyAspects;
	
	/**
	 * Aspects given by the user. These aspects will be applied on
	 * the ontology, if they are contained in the ontology.
	 * 
	 * @parameter
	 */
	private String[] userAspects;
	
	/**
	 * If inferred axioms should be printed.
	 * @parameter 	property="ifIncludeOriginalAxioms"
	 * 				default-value="true"
	 */
	private boolean ifIncludeOriginalAxioms;
	
	/**
	 * Name of axioms export file.
	 * @parameter	property="inferredOwlDirectory"
	 * 				default-value="target/inferredOwlFiles"
	 */
	private String inferredOwlDirectory;
	
	/**
	 * Name of axioms export file.
	 * @parameter	property="inferredOwlFile"
	 * 				default-value="inferredOntology.owl"
	 */
	private String inferredOwlFileName;

	/** Tests an ontology regarding syntax and consistency */
	public void execute() throws MojoExecutionException {

		Log log = getLog();
		Util.printHead("Inferring axioms ..", log);

		// Loading ontology
		File owlFile = new File(owlDirectory + File.separator + owlFileName);
		OWLOntologyManager manager = Util.createManager();
		Optional<OWLOntology> oontology = Util.loadOntologyFile( manager, log, owlFile );
		if (!oontology.isPresent()) return;
		OWLOntology ontology = oontology.get();
		
		// Applying aspects, if the user have sets the boolean true
		if(ifApplyAspects)
			Util.applyAspects(manager, aspectsIRI, ontology, userAspects, log);
		
		
		// Preparing export file
		File inferredOwlFile = new File(inferredOwlDirectory + File.separator
				+ inferredOwlFileName);
		
		
		// Case 1: Original and inferred axioms will be exported
		if (ifIncludeOriginalAxioms){
			log.info("Inferred ontology will consist of original and inferred axioms.");
			Util.inferAxioms(ontology, manager, log);
			Util.removeTwiceAxiomsWithAnnotations(ontology, manager);
			Util.saveOntology(ontology, inferredOwlFile, log);
			
		// Case 2: Only Inferred Axioms will be exported
		// Method: Select after inferring axioms, which was not at beginning there
		} else{
			log.info("Only inferred axioms will be added to new ontology.");
			Set<OWLAxiom> originalAxioms = ontology.getAxioms();
			Util.inferAxioms(ontology, manager, log);
			Util.removeTwiceAxiomsWithAnnotations(ontology, manager);
			for (OWLAxiom axiom: ontology.getAxioms()){
				if (originalAxioms.contains(axiom))
					manager.removeAxiom(ontology, axiom);
			}
			Util.saveOntology(ontology, inferredOwlFile, log);
		}
		
		Util.printTail(log);
	}
}