package de.csw.ontomaven.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import org.apache.maven.plugin.logging.Log;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.OWLOntologyImportsClosureSetProvider;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Util {

	/**
	 * Loads an ontology from a given owl file.
	 * 
	 * @param ontologyManager to load the ontology
	 * @param log for printing reports
	 * @param owlFile from which the ontology will be loaded
	 * @return loaded ontology
	 */
	public static OWLOntology loadOntologyFile(
			OWLOntologyManager ontologyManager, Log log, File owlFile) {
		
		if (owlFile == null || !owlFile.exists()){
			log.error(owlFile.getAbsolutePath() + " not existing");
			return null;
		}
		
		return loadOntology(ontologyManager, log, new FileDocumentSource(
				owlFile));
	}
	
	/**
	 * This method first creates a configuration for loading ontology.
	 * It is necessary because of missing imports. There could be some
	 * imports, which cannot be loaded. In this case the OWL API
	 * throws an exception.This configuration is to hide the exception.
	 *
	 * @param manager which will load the ontology
	 * @param log for logging messages and errors
	 * @param source from that the ontology will be loaded
	 * 
	 * @return loaded ontology
	 */
	public static OWLOntology loadOntologyByIgnoringMissingImports(
			OWLOntologyManager manager, Log log,
			OWLOntologyDocumentSource source) {
		if (manager == null)
			manager = createManager();

		manager.setOntologyLoaderConfiguration(manager.getOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT));
		return loadOntology(manager, log, source);
	}
	/**
	 * Loads an ontology from a given  {@link OWLOntologyDocumentSource}.
	 * 
	 * @param manager for loading the ontology
	 * @param log for printing result and error messages
	 * @param source from that the ontology will be loaded
	 * @return the loaded ontology
	 */
	public static OWLOntology loadOntology(OWLOntologyManager manager,
			Log log, OWLOntologyDocumentSource source) {

		if (manager == null)
			manager = OWLManager.createOWLOntologyManager();

		// Step 3: Loading ontology from file
		OWLOntology ontology = null;
		try {
			ontology = manager.loadOntologyFromOntologyDocument(source);
		} catch (UnloadableImportException e) {
			log.error("Import not succesfull taken place.", e);
		} catch (OWLOntologyCreationException e) {
			log.error("Ontology cannot be created.", e);
		}
		
		log.info("Ontology " + ontology.getOntologyID().getOntologyIRI().
				toString() + " has been succesfull loaded.");
		
		return ontology;
	}
	
	/**
	 * Prints a head for console programs by printing some stars, underlines
	 * and a title.
	 * 
	 * @param title which will be printed between stars
	 * @param log used to print the head
	 */
	public static void printHead(String title, Log log) {
		log.info("");
		log.info("");
		log.info("");
		log.info("_________________________________________________________________________");
		log.info("*************************************************************************");
		log.info("* " + title);
		log.info("*************************************************************************");
		log.info("");
	}
	
	/**
	 * Prints a tail for console programs consisting in stars and underlines.
	 * @param log used to print the tail
	 */
	public static void  printTail(Log log){
		log.info("");
		log.info("_________________________________________________________________________");
		log.info("*************************************************************************");
		log.info("");
		log.info("");
		log.info("");
	}
	
	/**
	 * Creates and returns a new {@link OWLOntologyManager}, used
	 * to have short names.
	 */
	public static OWLOntologyManager createManager() {
		return OWLManager.createOWLOntologyManager();
	}	
	
	/**
	 * Save a given ontology into a given file.
	 * 
	 * @param ontology to be saved
	 * @param saveFile in which the ontology will be saved
	 * @param log for printing result and error messages
	 */
	public static void saveOntology(OWLOntology ontology, File saveFile,
			Log log) {
		
		OWLOntologyManager manager = Util.createManager();
		IRI iri = ontology.getOntologyID().getOntologyIRI().get();
		log.info("");
		log.info("Saving ontology " + iri);
		log.info("to");
		log.info(saveFile.getAbsolutePath());
		log.info("");
		try {
			OWLDocumentFormat format = new OWLXMLDocumentFormat();
			manager.saveOntology(ontology, format, IRI.create(saveFile));
		} catch (OWLOntologyStorageException e) {
			log.error("Cannot store ontology " + iri, e);
		}
		
		log.info(saveFile + " has been saved.");
	}
	
	/**
	 * Checks, if an ontology can be loaded. For this, it loads
	 * the ontology and returns true, if no exceptions were thrown.
	 * If there is an exception, it returns false;
	 * 
	 * @param iri of the ontology
	 * @return if can load ontology
	 */
	public static boolean canLoadOntology(IRI iri) {
		try {
			createManager().loadOntologyFromOntologyDocument(iri);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean canLoadOntologyByIgnoringMissingImports(IRI iri) {
		try {
			OWLOntologyManager manager = createManager();
			manager.setOntologyLoaderConfiguration(manager.getOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT));
			manager.loadOntologyFromOntologyDocument(
					new IRIDocumentSource(iri));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Returns for an given ontology a structural reasoner.
	 * 
	 * @param ontology for that reasoner will be created
	 * @return reasoner for ontology
	 */
	public static OWLReasoner createReasoner(OWLOntology ontology) {
		return new StructuralReasonerFactory().createReasoner(ontology);
	}
	
	/**
	 * Infers axioms from the axioms of an ontology and fills adds the
	 * inferred axioms to the ontology.
	 *  
	 * @param ontology to be filled
	 * @param manager used for filling
	 */
	public static void inferAxioms(OWLOntology ontology,
			OWLOntologyManager manager, Log log) {
		log.info("");
		log.info("Inferring axioms...");
		int originalAxiomsCount = ontology.getAxiomCount();
		new InferredOntologyGenerator(new PelletReasoner(ontology,
				BufferingMode.BUFFERING)).fillOntology(manager.getOWLDataFactory(), ontology);
		log.info((ontology.getAxiomCount()
				- originalAxiomsCount) + " axioms successfully inferred.");
		log.info("");
	}

	/**
	 * Remove axioms in the ontology, which are existing one time
	 * without annotation and one time with annotation. The content
	 * of both axioms are the same. The version without annotation
	 * will be removed. Such axioms are created during the inferring
	 * processes as the result of a "bug" in some reasoners.
	 * 
	 * @param ontology to be cleaned of twice axioms
	 * @param manager for removing axioms
	 * 
	 * @author Ralph Sch�fermeier
	 */
	public static void removeTwiceAxiomsWithAnnotations(OWLOntology ontology,
			OWLOntologyManager manager) {
		for (OWLAxiom axiom : ontology.getAxioms()) {
			if (!axiom.getAnnotations().isEmpty())
				break;
		OWLAxiom axiomWithoutAnnotations = axiom
				.getAxiomWithoutAnnotations();
		if (ontology.containsAxiom(axiomWithoutAnnotations)) {
			manager.removeAxiom(ontology, axiomWithoutAnnotations);
		}
		}
	}
	

	public static void applyAspects(OWLOntologyManager manager,
			String aspectsIRI, OWLOntology ontology, String[] userAspects,
			Log log) {
		applyAspects(new AspectManager(manager, aspectsIRI, ontology,
				userAspects), log, ontology);
	}
	
	public static void applyAspects(AspectManager aspectManager, Log log, OWLOntology ontology){
		log.info("");
		log.info("Axioms count: " + ontology.getAxiomCount());
		log.info("Entities count: " + ontology.getSignature().size());
		log.info("Applying aspects...");
		aspectManager.applyAllAspects();
		log.info("Aspects were applied...");
		log.info("Removed axioms: " + aspectManager.getRemovedAxiomsCount());
		log.info("Removed entities: " + aspectManager.getRemovedEntitiesCount());
		log.info("Axioms count after applying aspects: "
				+ ontology.getAxiomCount());
		log.info("Entities count after applying aspects: "
				+ ontology.getSignature().size());
		log.info("");
	}
	
	/**
	 * Converts an OWL API OWLOntology to a Jena OntModel.
	 * @param owlOntology An OWL API OWLOntology.
	 * @return The corresponding Jena OntModel.
	 * @throws OWLOntologyStorageException
	 * @author Ralph Sch�fermeier
	 */
	public static OntModel owlOntologyToJenaModel(OWLOntology owlOntology,
			boolean withImports, Log log) {
		
		OWLOntologyManager om = owlOntology.getOWLOntologyManager();
		
		if (withImports) {
			try {
				owlOntology = new OWLOntologyMerger(
						new OWLOntologyImportsClosureSetProvider(om, owlOntology))
						.createMergedOntology(om, null);
			} catch (OWLOntologyCreationException e) {
				log.error("Cannot Convert ontology into jena model.");
			}
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			owlOntology.getOWLOntologyManager().saveOntology(owlOntology,
			                                                 new RDFXMLDocumentFormat(), baos);
		} catch (OWLOntologyStorageException e) {
			log.error("Cannot create jena model of the ontology.", e);
		}
		byte[] bytes = baos.toByteArray();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);		
		OntModel jenaModel = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM);
		jenaModel.read(bais, null, "RDF/XML");
		return jenaModel;
	}	
	
	
}