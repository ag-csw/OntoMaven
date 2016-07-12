package de.csw.ontomaven;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import de.csw.ontomaven.util.Util;

/**
 * Tests if the axioms of an ontolgy can be inferred from the
 * axioms of another ontology.
 * 
 * @goal TestEntailment
 * @phase test
 */
public class TestEntailment extends AbstractMojo {
	
	/**
	 * Working directory, where owl files are stored. It should be
	 * a relative path in the maven project directory.
	 * 
	 * @parameter 	property="owlDirectory"
	 * 				default-value=""
	 * @required
	 */
	private String owlDirectory;
	
	/**
	 * Name of the premise ontology file. The axioms of this
	 * ontology will be used to infer the axioms of the other
	 * ontology.
	 * 
	 * @parameter property="premiseOntologyFileName"
	 *            default-value="premiseOntology.owl"
	 * @required
	 */
	private String premiseOntologyFileName;

	/**
	 * Name of the conclusion ontology file. The goal will
	 * try to infer the axioms of this ontology from the
	 * axioms of the other ontology.
	 * 
	 * @parameter property="conclusionOntologyFileName"
	 *            default-value="conclusionOntology.owl"
	 */
	private String conclusionOntologyFileName;

	/**
	 * IRI of the aspect annotation property.
	 * 
	 * @parameter property="aspectsIRI"
	 * default-value="http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"
	 */
	private String aspectsIRI;
	
	/**
	 * If the plugin should consider aspects in the premise ontology.
	 * If yes, the aspects will be applied before inferring.
	 *
	 * @parameter 	property="ifApplyPremiseAspects"
	 * 				defaul-value="true"
	 */
	private boolean ifApplyPremiseAspects;
	
	/**
	 * If the plugin should consider aspects in the conclusion ontology.
	 * If yes, the aspects will be applied before inferring.
	 * 
	 * @parameter 	property="ifApplyConclusionAspects"
	 * 				defaul-value="true"
	 */
	private boolean ifApplyConclusionAspects;
	
	/**
	 * Aspects given by the user for the premise ontology. These
	 * aspects will be applied on the ontology, if they are
	 * contained in the ontology.
	 * 
	 * @parameter
	 */
	private String[] userPremiseAspects;
	
	/**
	 * Aspects given by the user for conclusion ontology. These
	 * aspects will be applied on the ontology, if they are
	 * contained in the ontology.
	 * 
	 * @parameter
	 */
	private String[] userConclusionAspects;

	/**
	 * Tests if all statements of an ontology can be reasoned from the
	 * statements of another ontology
	 */
	public void execute() throws MojoExecutionException {

		Log log = getLog();
		Util.printHead("Entailment Test...", log);

		// Loaing Premise ontology
		OWLOntologyManager premiseManager = Util.createManager();
		File premiseOntologyFile = new File(owlDirectory + File.separator
				+ premiseOntologyFileName);
		OWLOntology premiseOntology = Util.loadOntologyFile(premiseManager,
				log, premiseOntologyFile);
		if (premiseOntology == null) return;
		
		
		// Loading conclusion ontology
		OWLOntologyManager conclusionManager = Util.createManager();
		File conclusionOntologyFile = new File(owlDirectory + File.separator
				+ conclusionOntologyFileName);
		OWLOntology conclusionOntology = Util.loadOntologyFile(
				conclusionManager, log, conclusionOntologyFile);
		if (conclusionOntology == null) return;
		
		
		// Applying aspects on the premise ontology
		if (ifApplyPremiseAspects) {
			log.info("PREMISE ONTOLOGY:");
			Util.applyAspects(premiseManager, aspectsIRI, premiseOntology,
					userPremiseAspects, log);
		}
		
		
		// Applying aspects on the conclusion ontology
		if (ifApplyConclusionAspects) {
			log.info("CONCLUSION ONTOLOGY:");
			Util.applyAspects(conclusionManager, aspectsIRI,
					conclusionOntology, userConclusionAspects, log);
		}
		
		
		// Getting list of axioms, which are not entailed
		OWLReasoner reasoner = Util.createReasoner(premiseOntology);
		Util.inferAxioms(premiseOntology, premiseManager, log);
		List<OWLAxiom> notEntailedAxioms = new LinkedList<OWLAxiom>();
		for (OWLAxiom conclusionAxiom: conclusionOntology.getAxioms()){
			if(!reasoner.isEntailed(conclusionAxiom))
				notEntailedAxioms.add(conclusionAxiom);
		}
		
		
		//Printing, if all conclusion axioms are entailed
		if (notEntailedAxioms.isEmpty()){
			log.info("Entailment succesfull: All axioms inferred.");
		} else {
			log.info("Entailment UNsuccesfull: " + notEntailedAxioms.size()
				+ " axioms cannot be inferred:");
			for (OWLAxiom notEntailedAxiom: notEntailedAxioms)
				log.info(notEntailedAxiom.toString());
		}
		
		Util.printTail(log);
	}
}