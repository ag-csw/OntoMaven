package de.csw.ontomaven;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import de.csw.ontomaven.util.Util;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;

import java.io.File;
import java.util.*;

/**
 * Tests an ontology regarding the syntax and consistency. The
 * result will be printed in the console and written into a
 * specified file.
 * 
 * @goal TestOntology
 * @phase test
 */
public class TestOntology extends AbstractMojo {

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
	 * URL of the catalog where mappings are registered.
	 *
	 * @parameter 	property="catalogURL"
	 * 				default-value="catalog-v001.xml"
	 * @required
	 */
	private String catalogURL;


	/**
	 * Inference tasks to perform
	 *
	 * @parameter   property="inferences"
	 *
	 * @required
	 */
	private List<InferenceType> inferences = new ArrayList( defaultIinferences );

	/**
	 * If true it handles axioms with no aspects as if they would have every aspect, i.e. it will keep axioms that have no aspects.
	 *
	 * @parameter property="keepNonAspectAxioms"
	 * default-value="false"
	 */
	private boolean keepNonAspectAxioms;


	private static final List<InferenceType> defaultIinferences = Arrays.asList( InferenceType.CLASS_HIERARCHY,
	                                                                             InferenceType.CLASS_ASSERTIONS,
	                                                                             InferenceType.OBJECT_PROPERTY_ASSERTIONS,
	                                                                             InferenceType.OBJECT_PROPERTY_HIERARCHY,
	                                                                             InferenceType.DATA_PROPERTY_ASSERTIONS,
	                                                                             InferenceType.DATA_PROPERTY_HIERARCHY,
	                                                                             InferenceType.DISJOINT_CLASSES
	                                                                           );


	public String getOwlDirectory() {
		return owlDirectory;
	}

	public void setOwlDirectory( String owlDirectory ) {
		this.owlDirectory = owlDirectory;
	}

	public String getOwlFileName() {
		return owlFileName;
	}

	public void setOwlFileName( String owlFileName ) {
		this.owlFileName = owlFileName;
	}

	public String getAspectsIRI() {
		return aspectsIRI;
	}

	public void setAspectsIRI( String aspectsIRI ) {
		this.aspectsIRI = aspectsIRI;
	}

	public boolean isIfApplyAspects() {
		return ifApplyAspects;
	}

	public void setIfApplyAspects( boolean ifApplyAspects ) {
		this.ifApplyAspects = ifApplyAspects;
	}

	public String[] getUserAspects() {
		return userAspects;
	}

	public void setUserAspects( String[] userAspects ) {
		this.userAspects = userAspects;
	}

	public String getCatalogURL() {
		return catalogURL;
	}

	public void setCatalogURL( String catalogURL ) {
		this.catalogURL = catalogURL;
	}

	public List<InferenceType> getInferences() {
		return inferences;
	}

	public void setInferences( List<InferenceType> inferences ) {
		this.inferences = inferences;
	}

	/** Tests an ontology regarding syntax and consistency */
	public void execute() throws MojoExecutionException {
		owlDirectory = "target/" + owlDirectory;

		Log log = getLog();
		Util.printHead("Testing ontology...", log);

		// Loading ontology
		File owlFile = new File(owlDirectory + File.separator + owlFileName);
		OWLOntologyManager manager = Util.createManager(Util.createCatalog( Optional.ofNullable( catalogURL ) ) );
		Optional<OWLOntology> oontology = Util.loadOntologyFile( manager, log, owlFile );
		if (!oontology.isPresent()) {
			log.warn("Could not load ontology " + owlFile.getAbsolutePath() );
			return;
		}
		OWLOntology ontology = oontology.get();
		// Printing that the syntax was OK. If it would be not OK, the execution
		// would not come until here
		log.info("Ontology loaded,  syntax OK.");
		
		
		// Applying aspects, if the user have sets the boolean true
		if(ifApplyAspects) {
			Util.applyAspects(manager, aspectsIRI, ontology, userAspects, log, keepNonAspectAxioms);
			ontology = manager.ontologies().findFirst().get();
		}
		
		
		// Getting inconsistent axioms
		ReasonerFactory factory = new ReasonerFactory();

		Configuration configuration=new Configuration();
		configuration.throwInconsistentOntologyException = false;
		configuration.reasonerProgressMonitor = new LogProgressMonitor( log );

		OWLReasoner reasoner = factory.createReasoner( ontology, configuration );
		reasoner.precomputeInferences( inferences.isEmpty() ?
				                               defaultIinferences.toArray( new InferenceType[ defaultIinferences.size()] ) :
				                               inferences.toArray( new InferenceType[ inferences.size()] ) );

		// Printing result of consistency check
		log.info("Testing consistency...");
		log.info("");

		if ( reasoner.isConsistent() ) {
			log.info("Consistency OK, ontology is consistent");
		} else {
			log.info("Ontology inconsistent because of these axioms:");

			BlackBoxExplanation exp = new BlackBoxExplanation( ontology, new ReasonerFactory() {
				@Override
				protected Configuration getProtegeConfiguration( OWLReasonerConfiguration owlAPIConfiguration ) {
					Configuration cfg = super.getProtegeConfiguration( owlAPIConfiguration );
					cfg.throwInconsistentOntologyException = false;
					return cfg;
				}
			}, reasoner );
			HSTExplanationGenerator multExplanator = new HSTExplanationGenerator( exp );

			ontology.classesInSignature( Imports.INCLUDED ).forEach(
					(clax) -> {
						Set<Set<OWLAxiom>> inconsistentAxioms = multExplanator.getExplanations( clax );

						for ( Set<OWLAxiom> set : inconsistentAxioms ) {
							log.info( "Explanation of inconsistency " + clax );
							for ( OWLAxiom inconsistentAxiom : set) {
								log.info( " - " + inconsistentAxiom.toString() );
							}
						}
					}
			                                                       );
		}

		reasoner.dispose();
		
		Util.printTail(log);
	}
}