package de.csw.ontomaven.util;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.maven.plugin.logging.Log;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.owlapi.XMLCatalogIRIMapper;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.OWLOntologyImportsClosureSetProvider;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class Util {

	public static String[] knownExtensions = new String[] { "owl", "rdf", "rdfs", "ofn", "ttl" };

	/**
	 * Loads an ontology from a given owl file.
	 * 
	 * @param ontologyManager to load the ontology
	 * @param log for printing reports
	 * @param owlFile from which the ontology will be loaded
	 * @return loaded ontology
	 */
	public static Optional<OWLOntology> loadOntologyFile(
			OWLOntologyManager ontologyManager, Log log, File owlFile) {
		
		owlFile = resolveFile(owlFile);
		if (owlFile == null || !owlFile.exists()){
			log.error(owlFile.getAbsolutePath() + " not existing");
			return Optional.empty();
		}

		return loadOntology(ontologyManager, log, new FileDocumentSource(owlFile));
	}

	public static File resolveFile(File owlFile) {
		 if (owlFile != null && !owlFile.exists()) {
			URL url = Thread.currentThread().getContextClassLoader().getResource(owlFile.getPath());
			if (url != null) {
				try {
					owlFile = new File(url.toURI());
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
		return owlFile;
	}

	public static Optional<URL> resolveURL(String string) {
		URL url = null;
		try {
			url = new URL( string );
		} catch ( MalformedURLException e ) {
			url = Thread.currentThread().getContextClassLoader().getResource(string);
			if (url == null) {
				File f = new File( string );
				if ( f.exists() ) {
					try {
						url = f.toURI().toURL();
					} catch ( MalformedURLException e1 ) {
						//
					}
				}
			}
		}
		return Optional.ofNullable( url );
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
	public static Optional<OWLOntology> loadOntologyByIgnoringMissingImports( OWLOntologyManager manager,
	                                                                          Log log,
	                                                                          OWLOntologyDocumentSource source) {
		OWLOntologyLoaderConfiguration cfg = manager.getOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		manager.setOntologyLoaderConfiguration(cfg);

		return loadOntology(manager, log, source);
	}

	/**
	 * Loads an ontology, if not already in memory
	 *
	 * @param manager which will load the ontology
	 * @param log for logging messages and errors
	 * @param ontologyIRI the IRI of the ontology to be loaded
	 * @param source from that the ontology will be loaded
	 *
	 * @return loaded ontology
	 */
	public static Optional<OWLOntology> loadOntologyByIgnoringMissingImports( OWLOntologyManager manager,
	                                                                          Log log,
	                                                                          Optional<IRI> ontologyIRI,
	                                                                          OWLOntologyDocumentSource source) {
		if ( ontologyIRI.isPresent() ) {
			OWLOntology o = manager.getOntology( ontologyIRI.get() );
			if ( o != null ) {
				log.info( "Ontology already loaded : " + ontologyIRI.get().toString() );
				return Optional.of( o );
			}
		}
		return loadOntologyByIgnoringMissingImports( manager, log, source );
	}

	/**
	 * Loads mappings from a Catalog file, and adds them to an Ontology Manager
	 * @param manager The manager to be configured
	 * @param catalog A catalog defining some ontology mappings
	 */
	private static void configureMappingsFromCatalog( OWLOntologyManager manager, Optional<XMLCatalog> catalog ) {
		if ( catalog.isPresent() ) {
			XMLCatalogIRIMapper mapper = new XMLCatalogIRIMapper( catalog.get() );
			manager.setIRIMappers( Collections.<OWLOntologyIRIMapper>singleton( mapper ) );
		}
	}

	/**
	 * Loads an ontology from a given  {@link OWLOntologyDocumentSource}.
	 * 
	 * @param manager for loading the ontology
	 * @param log for printing result and error messages
	 * @param source from that the ontology will be loaded
	 * @return the loaded ontology
	 */
	public static Optional<OWLOntology> loadOntology(OWLOntologyManager manager,
	                                                 Log log,
	                                                 OWLOntologyDocumentSource source) {

		if (manager == null)
			manager = OWLManager.createOWLOntologyManager();

		// Step 3: Loading ontology from file
		OWLOntology ontology = null;
		try {
			ontology = manager.loadOntologyFromOntologyDocument(source);

			log.info("Ontology " + ontology.getOntologyID().getOntologyIRI().
					toString() + " has been succesfull loaded.");
		} catch (UnloadableImportException e) {
			log.error("Import not succesfull taken place.", e);
		} catch (OWLOntologyCreationException e) {
			log.error("Ontology cannot be created.", e);
		}

		return Optional.ofNullable( ontology );
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

	public static void saveOntology( OWLOntology ontology, File saveFile, Log log ) {
		saveOntology( ontology, saveFile, log, true );
	}

		/**
		 * Save a given ontology into a given file.
		 *  @param ontology to be saved
		 * @param saveFile in which the ontology will be saved
		 * @param log for printing result and error messages
		 * @param forceRefresh overwrite files even when present
		 */
	public static void saveOntology( OWLOntology ontology, File saveFile,
	                                 Log log, boolean forceRefresh ) {
		if ( saveFile.exists() && ! forceRefresh ) {
			return;
		}
		OWLOntologyManager manager = Util.createManager();
		IRI iri = ontology.getOntologyID().getOntologyIRI().get();
		log.info("");
		log.info("Saving ontology " + iri);
		log.info("to");
		log.info(saveFile.getAbsolutePath());
		log.info("");
		try {
			if ( !saveFile.getParentFile().exists() ) {
				saveFile.getParentFile().mkdirs();
			}
			OWLDocumentFormat format = new OWLXMLDocumentFormat();
			manager.saveOntology(ontology, format, IRI.create(saveFile));
		} catch (OWLOntologyStorageException e) {
			log.error("Cannot store ontology " + iri, e);
		}
		
		log.info(saveFile + " has been saved.");
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

		new InferredOntologyGenerator( new ReasonerFactory().createReasoner( ontology ) ).fillOntology(manager.getOWLDataFactory(), ontology);
		log.info((ontology.getAxiomCount() - originalAxiomsCount) + " axioms successfully inferred.");
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
			Log log, boolean keepNonAspectAxioms) {
		applyAspects(new AspectManager(manager, aspectsIRI, ontology,
				userAspects, keepNonAspectAxioms), log, ontology);
	}
	
	public static void applyAspects(AspectManager aspectManager, Log log, OWLOntology ontology){
		log.info("");
		log.info("Axioms count: " + ontology.getAxiomCount());
		log.info("Entities count: " + ontology.getSignature().size());
		log.info("Applying aspects...");
		aspectManager.applyAllAspects();
		ontology = aspectManager.getManager().ontologies().findFirst().get();
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
	 * @param withImports Include imports.
	 * @param log The logger.
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


	/**
	 * Aligns the (relative) path of a File to the absolute path of another one
	 *
	 * E.g. given /absolute/relative/source.x and relative/target.y,
	 * this method returns /absolute/relative/target.y
	 *
	 * @param absolutePathFile
	 * @param relativePathFile
	 * @return an absolute-path file based on the maximum overlap between the absolute and relative paths of the two input argument files
	 */
	public static File relativeToFile(File relativePathFile, File absolutePathFile) {
		if ( (! absolutePathFile.isAbsolute()) || relativePathFile.isAbsolute() ) {
			return relativePathFile;
		}
		return new File(concatMerge(absolutePathFile.getParent(),relativePathFile.getPath()));
	}


	public static String concatMerge(String left, String right) {
		int max = Math.min(left.length(),right.length());
		for (int j=max; j>0; j--) {
			if (left.regionMatches(left.length() - j, right, 0, j)) {
				return left + right.substring(j, right.length());
			}
		}
		return left + right;
	}


	public static void fetchOntologyDocumentFromURL( String owlFileURL,
	                                                 String targetFileName,
	                                                 Log log,
	                                                 boolean forceRefresh ) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		URL sourceUrl = null;
		try {
			sourceUrl = new URL( owlFileURL );
		} catch ( MalformedURLException e ) {
			// not a valid URL, possibly a relative path
			File f = Util.resolveFile( new File( owlFileURL ) );
			if ( f.exists() ) {
				try {
					sourceUrl = f.toURI().toURL();
				} catch ( MalformedURLException e1 ) {
					e1.addSuppressed( e );
					e1.printStackTrace();
				}
			}
		}
		if ( sourceUrl != null ) {
			Optional<OWLOntology> onto = loadOntologyByIgnoringMissingImports( createManager(),
			                                                                   log,
			                                                                   new StreamDocumentSource( sourceUrl.openStream() ) );
			File t = new File( targetFileName );
			saveOntology( onto.get(), t, log, forceRefresh );
		}
	}

	/**
	 * Creates an ontology manager with an optional catalog
	 * @param mappingsCatalog
	 * @return the Ontology Manager
	 */
	public static OWLOntologyManager createManager( Optional<XMLCatalog> mappingsCatalog ) {
		OWLOntologyManager manager = createManager();
		if ( mappingsCatalog.isPresent() ) {
			configureMappingsFromCatalog( manager, mappingsCatalog );
		}
		return manager;
	}

	/**
	 * Uses a catalog to map IRIs.
	 * Looks up an IRI in the catalog and returns the redirected IRI, if any.
	 * If no mapping is found, the original IRI is returned
	 * @param catalog   The catalog containing the IRIs
	 * @param iri   The IRI to look up in the catalog
	 * @return  The mapped IRI, if any, or the original IRI
	 */
	public static IRI resolveMappings( Optional<XMLCatalog> catalog, IRI iri ) {
		if ( ! catalog.isPresent() ) {
			return iri;
		}
		XMLCatalogIRIMapper mapper = new XMLCatalogIRIMapper( catalog.get() );
		IRI mapped = mapper.getDocumentIRI( iri );
		return mapped != null ? mapped : iri;
	}

	/**
	 * Creates a catalog from a URL
	 * @param mappingsCatalogURL    the URL where the catalog is available
	 * @return  An XML Catalog object with ontology URL mappings
	 */
	public static Optional<XMLCatalog> createCatalog( Optional<String> mappingsCatalogURL ) {
		XMLCatalog catalog = null;
		try {
			if ( mappingsCatalogURL.isPresent() ) {
				Optional<URL> mappings = resolveURL( mappingsCatalogURL.get() );
				if ( mappings.isPresent() ) {
					catalog = CatalogUtilities.parseDocument(mappings.get());
				}
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return Optional.ofNullable( catalog );
	}

	/**
	 * Checks whether a file name ends with one of the known OWL serialization extensions
	 * @param fileName  The name of the file to be tested
	 * @return true if the name ends with a known extension (.owl, .rdf(s), .ttl, .ofn )
	 */
	public static boolean hasKnwownExtension( String fileName ) {
		return Arrays.stream(knownExtensions).anyMatch( (ext) -> fileName.endsWith( ext ) );
	}
}