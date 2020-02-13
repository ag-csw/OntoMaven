package de.csw.ontomaven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import de.csw.ontomaven.util.AspectManager;
import de.csw.ontomaven.util.Util;

/**
 * Exports the axioms of the ontology into a file. The name of the
 * If wanted, export file can be set. Exports also inferred axioms
 * can be printed and aspects can be applied.
 * 
 * @goal ExportAxioms
 */
public class ExportAxioms extends AbstractMojo {
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
	 * @parameter 	parameter="ifApplyAspects"
	 *				defaul-value="true"
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
	 * @parameter 	property="ifExportInferredAxioms"
	 * 				default-value="false"
	 */
	private boolean ifExportInferredAxioms;
	
	/**
	 * If original axioms should be printed.
	 * @parameter 	property="ifExportOriginalAxioms"
	 * 				default-value="true"
	 */
	private boolean ifExportOriginalAxioms;
	
	/**
	 * The directory, where the files for exported axioms 
	 * @parameter	property="axiomsExportDirectory"
	 * 				default-value="target/exportedAxioms"
	 */
	private String axiomsExportDirectory;
	
	/**
	 * Name of axioms export file. It will be a text file.
	 * @parameter	property="axiomsExportFileName"
	 * 				default-value="exportedAxioms.txt"
	 */
	private String axiomsExportFileName;

	/** Tests an ontology regarding syntax and consistency */
	public void execute() throws MojoExecutionException {

		Log log = getLog();
		Util.printHead("Exporting axioms ..", log);

		// Loading ontology
		File owlFile = new File(owlDirectory + File.separator + owlFileName);
		OWLOntologyManager manager = Util.createManager();
		Optional<OWLOntology> oontology = Util.loadOntologyFile( manager, log, owlFile );
		if (!oontology.isPresent()) {
			log.warn("Could not load ontology " + owlFile.getAbsolutePath() );
			return;
		}
		OWLOntology ontology = oontology.get();



		// Applying aspects, if the user have sets the boolean true
		AspectManager aspectManager = new AspectManager(manager, aspectsIRI,
				ontology, userAspects);
		if (ifApplyAspects)
			Util.applyAspects(aspectManager, log, ontology);
		
		
		// Preparing export file
		File exportFile = new File(axiomsExportDirectory + File.separator + 
				axiomsExportFileName);
		
		
		// Case 1: Original and inferred axioms will be exported
		if (ifExportInferredAxioms && ifExportOriginalAxioms){
			log.info("Original and inferred axioms will be exported.");
			Util.inferAxioms(ontology, manager, log);
			Util.removeTwiceAxiomsWithAnnotations(ontology, manager);
			exportAxioms(ontology.getAxioms(), exportFile, log,
					aspectManager);
			
		// Case 2: Only Inferred Axioms will be exported
		// Method: Select after inferring axioms, which was not at beginning there
		} else if (ifExportInferredAxioms){
			log.info("Only inferred axioms will be exported.");
			Set<OWLAxiom> originalAxioms = ontology.getAxioms();
			Set<OWLAxiom> inferredAxioms = new HashSet<OWLAxiom>();
			Util.inferAxioms(ontology, manager, log);
			Util.removeTwiceAxiomsWithAnnotations(ontology, manager);
			for (OWLAxiom axiom: ontology.getAxioms()){
				if (!originalAxioms.contains(axiom))
					inferredAxioms.add(axiom);
			}
			exportAxioms(inferredAxioms, exportFile, log,
					aspectManager);
			
		// Case 3: Export only original axioms, nothing will be inferred
		} else if (ifExportOriginalAxioms){
			log.info("Only original axioms will be exported.");
			exportAxioms(ontology.getAxioms(), exportFile, log,
					aspectManager);
			
		// Case 4: Nothing will be exported, because user doesn't want
		} else {
			log.error("No axioms will be exported. You should set the parameter"
					+ " for inferred, original or both type of axioms on true.");
		}
		
		Util.printTail(log);
	}
	
	/**
	 * Writes a set of axioms in a given file. The toString() method
	 * of the axioms will be used, to get a human readable form of the
	 * axioms. A given aspect manager prepares the axioms by writing
	 * the aspect names, if they are only with this aspect valid.
	 * 
	 * @param axioms to write
	 * @param fileToSave save file
	 * @param log for printing messages
	 * @param aspectManager for getting 
	 */
	private static void exportAxioms(Set<OWLAxiom> axioms, File fileToSave,
			Log log, AspectManager aspectManager) {
		try {
			log.info("Exporting axioms...");
			fileToSave.getParentFile().mkdirs();
			FileWriter fileWriter = new FileWriter(fileToSave);
			String result = "";
			for (OWLAxiom axiom: axioms){
				String currentResult = prepareAxiomForExport(axiom, aspectManager);
				if (!result.contains(currentResult)){
				result += currentResult + System.lineSeparator()
						+ System.lineSeparator() + System.lineSeparator()
						+ System.lineSeparator();
				}
			}
			
			fileWriter.write(result);
			fileWriter.close();
			log.info("Axioms succesfully exported.");
		} catch (IOException e) {
			log.error("Axioms cannot be exported to " + fileToSave.
					getAbsolutePath(), e);
		}
	}
	
	private static String prepareAxiomForExport(OWLAxiom axiom,
			AspectManager aspectManager) {
		
		// Preparing result string and aspects
		String result = "";
		List<String> aspectNamesOfThisAxiom = aspectManager
				.getAspectNamesOfAxiom(axiom);

		// Adding Aspect names
		for (String aspect: aspectNamesOfThisAxiom)
			result += "Aspect: " + aspect + System.lineSeparator();

		// Handling object and data property axioms
		if (axiom.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_ASSERTION) ||
				axiom.getAxiomType().equals(AxiomType.DATA_PROPERTY_ASSERTION)){
			@SuppressWarnings("rawtypes")
			OWLPropertyAssertionAxiom objectPropAxiom = (OWLPropertyAssertionAxiom) axiom;
			
			String subject = objectPropAxiom.getSubject().toString();
			String property = objectPropAxiom.getProperty().toString();
			String object = objectPropAxiom.getObject().toString();
			
			result += "Property Axiom" + System.lineSeparator() +
					"Subject: " + subject + System.lineSeparator() + 
					"Property: " + property + System.lineSeparator() +
					"Object: " + object;
		}
		
		// Handling subclass axioms
		else if (axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)){
			OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
			
			String superClass = subClassAxiom.getSuperClass().toString();
			String subClass = subClassAxiom.getSubClass().toString();
			result += superClass + System.lineSeparator() +
					"is sub class of" + System.lineSeparator() + 
					subClass;
			
		}
		
		// Handling class axioms
		else if (axiom.getAxiomType().equals(AxiomType.CLASS_ASSERTION)){
			OWLClassAssertionAxiom classAxiom = (OWLClassAssertionAxiom) axiom;
			
			result += "Class: " + classAxiom.getClassExpression().toString();
		}
		
		// Handling other declarations than class declarations
		else if (axiom.getAxiomType().equals(AxiomType.DECLARATION)){
			OWLDeclarationAxiom declarationAxiom = (OWLDeclarationAxiom) axiom;
			String entityName = declarationAxiom.getEntity().toString();
			String entityType = declarationAxiom.getEntity().getEntityType().getName();
			result += entityType + ": " + entityName;
		}
		
		// Returning the standard (predefined in owl api) string represantation
		// of the axiom.
		else {
			result += axiom.toString();
		}
		
		return result;
	}
}