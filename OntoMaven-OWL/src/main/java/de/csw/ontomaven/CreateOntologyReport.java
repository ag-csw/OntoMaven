package de.csw.ontomaven;

import com.google.common.collect.Multimap;
import de.csw.ontomaven.util.Util;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2Profile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.io.File;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates a report for the ontology. This report will contain
 * information about the ontology and its elements like classes,
 * properties, individuals etc.
 *
 * @goal CreateOntologyReport
 * @phase site
 * @configurator include-project-dependencies
 * @requiresDependencyResolution compile+runtime
 */
@SuppressWarnings( "ConstantConditions" )
public class CreateOntologyReport extends AbstractMavenReport {

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
	 * The output directory for the report files. It should
	 * be relative path in the maven project directory.
	 *
	 * @parameter property="ontologyReportOutputDirectory"
	 *            default-value="target/site"
	 */
	private String ontologyReportOutputDirectory;

	/**
	 * The name of the report file
	 *
	 * @parameter property="outputName"
	 * 			  default-value="ontologyReport/owl-reporting"
	 */
	private String outputName;
	
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
	 * <i>Maven Internal</i>: The Doxia Site Renderer.
	 * This parameter does not need to be setted. Maven does
	 * it itself.
	 *
	 * @component
	 */
	private Renderer siteRenderer;

	/**
	 * <i>Maven Internal</i>: The Project descriptor.
	 * This parameter does not need to be setted. Maven does
	 * it itself.
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;


	/** Executes the creating of the ontology report. */
	@Override
	protected void executeReport(Locale locale) throws MavenReportException {

		Log log = getLog();
		Util.printHead("Creating ontology report...", log);

		// 1: Loading ontology
		File owlFile = Util.resolveFile(new File(owlDirectory + File.separator + owlFileName));
		OWLOntologyManager manager = Util.createManager();
		OWLOntology ontology = Util.loadOntologyFile(manager, log, owlFile);
		if (ontology == null) return; // Ontology not loaded


		// 1.1: Applying aspects, if wanted
		if(ifApplyAspects)
			Util.applyAspects(manager, aspectsIRI, ontology, userAspects, log);


		// 1.2: Creatin some often needed variables
		Set<OWLClass> classes = ontology.classesInSignature().collect( Collectors.toSet() );
		Set<OWLDataProperty> dataProps = ontology.dataPropertiesInSignature().collect( Collectors.toSet() );
		Set<OWLObjectProperty> objectProps = ontology.objectPropertiesInSignature().collect( Collectors.toSet() );
		Set<OWLImportsDeclaration> importDecls = ontology.importsDeclarations().collect( Collectors.toSet() );
		Set<OWLAnnotationProperty> annotationProps = ontology.annotationPropertiesInSignature().collect( Collectors.toSet() );
		Set<OWLNamedIndividual> individuals = ontology.individualsInSignature().collect( Collectors.toSet() );


		// 2: Preparing document
		log.info("Creating report html file...");
		Sink sink = getSink();
		sink.head();
		sink.title();
		sink.text("Ontology Report");
		sink.title_();
		sink.head_();
		sink.body();


		// 3: Adding title
		log.info("Adding ontology name...");
		String ontologyName = ontology.getOntologyID().getOntologyIRI().isPresent() ? ontology.getOntologyID().getOntologyIRI().get().toString() : "<MISSING NAME>";
		sink.section1();
		sink.sectionTitle1();
		sink.text("Ontology Report");
		sink.lineBreak();
		sink.text(ontologyName);
		sink.sectionTitle1_();


		// 4: Adding description, which is created of the annotations
		log.info("Adding ontology description...");
		sink.paragraph();
		sink.bold();
		sink.text("Description:");
		sink.bold_();
		sink.list();
		ontology.annotations().forEach( (annotation) -> {
			if (annotation.getProperty().isComment()) {
				sink.listItem();
				sink.text(annotation.getValue().toString());
				sink.listItem_();
			}
		});
		sink.list_();
		sink.paragraph_();


		// 5: Adding format name
		log.info("Adding ontology format...");
		sink.paragraph();
		sink.bold();
		sink.text("Format: ");
		sink.bold_();
		sink.text(getFormat(ontology, manager));
		sink.paragraph_();


		// 6: Adding profile information
		log.info("Adding ontology profiles...");
		sink.paragraph();
		sink.bold();
		sink.text("Profile(s): ");
		sink.bold_();
		sink.text(getProfiles(ontology));
		sink.paragraph_();
		sink.section1_();


		// 7: Adding import declarations
		log.info("Adding import declarations...");
		sink.lineBreak();
		sink.lineBreak();
		sink.table();
		sink.tableRow();
		sink.tableHeaderCell();
		sink.bold();
		sink.text("Imports");
		sink.bold_();
		sink.tableHeaderCell_();
		sink.tableRow_();
		if (ontology.imports().count() > 0) {
			for (OWLImportsDeclaration importDecl : importDecls) {
				sink.tableRow();
				sink.tableCell();
				sink.text(importDecl.getIRI().toString());
				sink.tableCell_();
				sink.tableRow_();
			}
		} else {
			sink.tableRow();
			sink.tableCell();
			sink.text("No import declarations found.");
			sink.tableCell_();
			sink.tableRow_();
		}
		sink.table_();


		// 8: Some statistics
		log.info("Adding statistics...");
		sink.lineBreak();
		sink.lineBreak();
		sink.table();
		sink.tableRow();
		sink.tableHeaderCell();
		sink.text("Statistics");
		sink.tableHeaderCell_();
		sink.tableHeaderCell();
		sink.text("");
		sink.tableHeaderCell_();
		sink.tableRow_();
		createCountRow("Classes", classes.size(), sink);
		createCountRow("Data Properties", dataProps.size(), sink);
		createCountRow("Object Properties", objectProps.size(), sink);
		createCountRow("Annotation Properties", annotationProps.size(), sink);
		createCountRow("Individuals", individuals.size(), sink);
		createCountRow("Axioms", ontology.getAxiomCount(), sink);
		sink.table_();


		// 9: Adding Overview of classes
		log.info("Adding classes overview...");
		sink.lineBreak();
		sink.lineBreak();
		sink.table();
		sink.tableRow();
		sink.tableHeaderCell();
		sink.text("Classes Overview");
		sink.tableHeaderCell_();
		sink.tableRow_();
		sink.tableRow();
		sink.tableCell();
		for (OWLClass owlClass : classes) {
			sink.link("#" + owlClass.getIRI().getRemainder().get());
			sink.text(owlClass.getIRI().getRemainder().get());
			sink.link_();
			sink.rawText(" &nbsp; ");
		}
		sink.tableCell_();
		sink.tableRow_();
		sink.table_();


		// 10: Adding Overview of Data Properties
		log.info("Adding data properties overview...");
		sink.lineBreak();
		sink.lineBreak();
		sink.table();
		sink.tableRow();
		sink.tableHeaderCell();
		sink.text("Data Properties Overview");
		sink.tableHeaderCell_();
		sink.tableRow_();
		sink.tableRow();
		sink.tableCell();
		for (OWLDataProperty property : dataProps) {
			sink.link("#" + property.getIRI().getRemainder().get());
			sink.text(property.getIRI().getRemainder().get());
			sink.link_();
			sink.rawText(" &nbsp; ");
		}
		sink.tableCell_();
		sink.tableRow_();
		sink.table_();


		// 11: Adding Overview of Object Properties
		log.info("Adding object properties overview...");
		sink.lineBreak();
		sink.lineBreak();
		sink.table();
		sink.tableRow();
		sink.tableHeaderCell();
		sink.text("Object Properties Overview");
		sink.tableHeaderCell_();
		sink.tableRow_();
		sink.tableRow();
		sink.tableCell();
		for (OWLObjectProperty property : objectProps) {
			sink.link("#" + property.getIRI().getRemainder().get());
			sink.text(property.getIRI().getRemainder().get());
			sink.link_();
			sink.rawText(" &nbsp; ");
		}
		sink.tableCell_();
		sink.tableRow_();
		sink.table_();



		// 12: Adding Overview of Named Individuals
		log.info("Adding named individuals overview...");
		sink.lineBreak();
		sink.lineBreak();
		sink.table();
		sink.tableRow();
		sink.tableHeaderCell();
		sink.text("Named Individuals Overview");
		sink.tableHeaderCell_();
		sink.tableRow_();
		sink.tableRow();
		sink.tableCell();
		for (OWLNamedIndividual individual : individuals) {
			sink.link("#" + individual.getIRI().getRemainder().get());
			sink.text(individual.getIRI().getRemainder().get());
			sink.link_();
			sink.rawText(" &nbsp; ");
		}
		sink.tableCell_();
		sink.tableRow_();
		sink.table_();


		// 13: Adding detailed information about classes
		log.info("Adding all classes with details...");
		sink.lineBreak();
		sink.lineBreak();
		sink.sectionTitle1();
		sink.text("OWL Classes (Details)");
		sink.sectionTitle1_();
		for (OWLClass owlClass : classes) {

			// 13.1: Addding class name
			IRI classIRI = owlClass.getIRI();
			String className = owlClass.getIRI().getRemainder().get();
			sink.table();
			sink.tableRow();
			sink.tableHeaderCell();
			sink.anchor(className);
			sink.text(className);
			sink.anchor_();
			sink.tableHeaderCell_();
			sink.tableRow_();
			sink.tableRow();
			sink.tableCell();
			sink.list();

			// 13.2: Adding super classes
			sink.listItem();
			sink.text("Super classes:");
			sink.list();
			EntitySearcher.getSuperClasses(owlClass, ontology).forEach( ( OWLClassExpression exp ) -> {
				exp.classesInSignature().forEach( (sub) -> {
					sink.listItem();
					sink.link("#" + sub.getIRI().getRemainder().get());
					sink.text(sub.getIRI().getRemainder().get());
					sink.link_();
					sink.listItem_();
				});
			});
			sink.list_();
			sink.listItem_();

			// 13.2: Adding individuals
			sink.listItem();
			sink.text("Individuals:");
			sink.list();
			for (OWLNamedIndividual individual: individuals) {
				EntitySearcher.getTypes(individual, ontology).forEach( (expr) -> {
					OWLClass classOfIndividual = expr.asOWLClass();
					String individualName = individual.getIRI().getRemainder().get();
					if (classOfIndividual.equals(owlClass)
							&& !classOfIndividual.isOWLThing()) {
						sink.listItem();
						sink.link("#" + individualName);
						sink.text(individualName);
						sink.link_();
						sink.listItem_();
					}
				});
			}
			sink.list_();
			sink.listItem_();

			// 13.3: Adding subclasses
			sink.listItem();
			sink.text("Subclasses:");
			sink.list();
			EntitySearcher.getSubClasses(owlClass, ontology).forEach( (exp)-> {
				exp.classesInSignature().forEach( (sub) -> {
					sink.listItem();
					sink.link("#" + sub.getIRI().getRemainder().get());
					sink.text(sub.getIRI().getRemainder().get());
					sink.link_();
					sink.listItem_();
				});
			});
			sink.list_();
			sink.listItem_();

			// 13.4: Adding Data Properties
			sink.listItem();
			sink.text("DataProperties:");
			sink.list();
			for (OWLDataProperty property : dataProps) {
				String propertyName = property.getIRI().getRemainder().get();
				EntitySearcher.getDomains(property, ontology).forEach( (domain) -> {
					if (domain.isClassExpressionLiteral() && classIRI.equals(domain.asOWLClass().getIRI())) {
						sink.listItem();
						sink.link("#" + propertyName);
						sink.text(propertyName);
						sink.link_();
						sink.listItem_();
					}
				});
			}
			sink.list_();
			sink.listItem_();

			// 13.5: Adding object properties
			sink.listItem();
			sink.text("ObjectProperties:");
			sink.list();
			for (OWLObjectProperty property : objectProps) {
				String propertyName = property.getIRI().getRemainder().get();
				EntitySearcher.getDomains(property, ontology).forEach( (domain) -> {
					if (domain.isClassExpressionLiteral() && classIRI.equals(domain.asOWLClass().getIRI())) {
						sink.listItem();
						sink.link("#" + propertyName);
						sink.text(propertyName);
						sink.link_();
						sink.listItem_();
					}
				});
			}
			sink.list_();
			sink.listItem_();

			// 13.6 : Adding class comments
			sink.listItem();
			sink.text("Comments:");
			sink.list();
			EntitySearcher.getAnnotations(owlClass, ontology).forEach( (annotation) -> {
				if (annotation.getProperty().isComment()){
					sink.listItem();
					sink.text(annotation.getValue().toString());
					sink.listItem_();
				}
			});
			sink.list_();
			sink.listItem_();

			// 13.7: Finalizing classes section
			sink.list_();
			sink.tableCell_();
			sink.tableRow_();
			sink.table_();
		}


		// 14: Addding detailed information about object properties
		log.info("Adding all object properties with details...");
		sink.lineBreak();
		sink.lineBreak();
		sink.sectionTitle1();
		sink.text("Object Properties (Details)");
		sink.sectionTitle1_();
		for (OWLObjectProperty property : objectProps) {

			// 14.1: Adding property name
			sink.table();
			sink.tableRow();
			sink.tableHeaderCell();
			sink.anchor(property.getIRI().getRemainder().get());
			sink.text(property.getIRI().getRemainder().get());
			sink.anchor_();
			sink.tableHeaderCell_();
			sink.tableRow_();
			sink.tableRow();
			sink.tableCell();
			sink.list();

			// 14.2: Adding domains of the object properties
			sink.listItem();
			sink.text("Domain");
			sink.list();
			EntitySearcher.getDomains(property, ontology).forEach( (domain) -> {
				sink.listItem();
				sink.link("#" + domain.asOWLClass().getIRI().getRemainder().get());
				sink.text(domain.asOWLClass().getIRI().getRemainder().get());
				sink.link_();
				sink.listItem_();
			});
			sink.list_();
			sink.listItem_();

			// 14.3: Adding range of the object properties
			sink.listItem();
			sink.text("Range");
			sink.list();
			EntitySearcher.getRanges(property, ontology).forEach( (range) -> {
				sink.listItem();
				sink.link("#" + range.asOWLClass().getIRI().getRemainder().get());
				sink.text(range.asOWLClass().getIRI().getRemainder().get());
				sink.link_();
				sink.listItem_();
			});
			sink.list_();
			sink.listItem_();

			// 14.4: Adding comments
			sink.listItem();
			sink.text("Comments");
			sink.list();
			EntitySearcher.getAnnotations(property, ontology).forEach( (annotation) -> {
				if (annotation.getProperty().isComment()){
					sink.listItem();
					sink.text(annotation.getValue().toString());
					sink.listItem_();
				}
			});
			sink.list_();
			sink.listItem_();


			// 14.5: Finalization object properties table
			sink.list_();
			sink.tableCell_();
			sink.tableRow_();
			sink.table_();
		}

		// 15: Addding detailed information about data properties
		log.info("Adding all data properties with details...");
		sink.lineBreak();
		sink.lineBreak();
		sink.sectionTitle1();
		sink.text("Data Properties (Details)");
		sink.sectionTitle1_();
		for (OWLDataProperty property : dataProps) {

			// 15.1: Adding property name
			sink.table();
			sink.tableRow();
			sink.tableHeaderCell();
			sink.anchor(property.getIRI().getRemainder().get());
			sink.text(property.getIRI().getRemainder().get());
			sink.anchor_();
			sink.tableHeaderCell_();
			sink.tableRow_();
			sink.tableRow();
			sink.tableCell();
			sink.list();

			// 15.2: Adding domains of the data properties
			sink.listItem();
			sink.text("Domain");
			sink.list();
			EntitySearcher.getDomains(property, ontology).forEach( (domain) -> {
				sink.listItem();
				sink.link("#" + domain.asOWLClass().getIRI().getRemainder().get());
				sink.text(domain.asOWLClass().getIRI().getRemainder().get());
				sink.link_();
				sink.listItem_();
			});
			sink.list_();
			sink.listItem_();

			// 15.3: Adding ranges of the data properties
			sink.listItem();
			sink.text("DataRange");
			sink.list();
			EntitySearcher.getRanges(property, ontology).forEach( (range) -> {
				sink.listItem();
				sink.link("#" + range.toString());
				sink.text(range.toString());
				sink.link_();
				sink.listItem_();
			});
			sink.list_();
			sink.listItem_();

			// 15.4: Adding comments
			sink.listItem();
			sink.text("Comments");
			sink.list();
			EntitySearcher.getAnnotations(property, ontology).forEach( (annotation) -> {
				if (annotation.getProperty().isComment()){
					sink.listItem();
					sink.text(annotation.getValue().toString());
					sink.listItem_();
				}
			});
			sink.list_();
			sink.listItem_();


			// 15.5: Finalization object properties table
			sink.list_();
			sink.tableCell_();
			sink.tableRow_();
			sink.table_();
		}

		// 16: Adding detailed information about individuals
		log.info("Adding all individuals with details...");
		sink.lineBreak();
		sink.lineBreak();
		sink.sectionTitle1();
		sink.text("Individuals (Details)");
		sink.sectionTitle1_();
		for (OWLNamedIndividual individual : individuals) {

			// 16.1: Adding individual name
			sink.table();
			sink.tableRow();
			sink.tableHeaderCell();
			sink.anchor(individual.getIRI().getRemainder().get());
			sink.text(individual.getIRI().getRemainder().get());
			sink.anchor_();
			sink.tableHeaderCell_();
			sink.tableRow_();
			sink.tableRow();
			sink.tableCell();
			sink.list();

			// 16.2: Adding types of the named individual
			sink.listItem();
			sink.text("Type(s): ");
			sink.list();
			EntitySearcher.getTypes(individual, ontology).forEach( (type) -> {
				if (!type.asOWLClass().isOWLThing()) {
					sink.listItem();
					sink.link("#" + type.asOWLClass().getIRI().getRemainder().get());
					sink.text(type.asOWLClass().getIRI().getRemainder().get());
					sink.link_();
					sink.listItem_();
				}
			});
			sink.list_();
			sink.listItem_();

			// 16.3: Adding data property values
			sink.listItem();
			sink.text("Data Property Values");
			sink.list();
			Multimap<OWLDataPropertyExpression, OWLLiteral> dataValues = EntitySearcher.
					getDataPropertyValues(individual, ontology);
			for (OWLDataPropertyExpression dataProp : dataValues
					.keySet()) {
				sink.listItem();
				sink.link("#" + dataProp.asOWLDataProperty().getIRI().getRemainder().get());
				sink.text(dataProp.asOWLDataProperty().getIRI().getRemainder().get());
				sink.link_();
				sink.list();
				for (OWLLiteral value : dataValues.get(dataProp)){
					sink.listItem();
					sink.text(value.getLiteral());
					sink.listItem_();
				}
				sink.list_();
				sink.link_();
				sink.listItem_();
			}
			sink.list_();
			sink.listItem_();

			// 16.4: Adding ranges of the data properties
			sink.listItem();
			sink.text("Object Property Values");
			sink.list();
			Multimap<OWLObjectPropertyExpression, OWLIndividual> objectValues = EntitySearcher
					.getObjectPropertyValues(individual, ontology);
			for (OWLObjectPropertyExpression objectProp : objectValues
					.keySet()) {
				sink.listItem();
				sink.link("#" + objectProp.asOWLObjectProperty().getIRI().getRemainder().get());
				sink.text(objectProp.asOWLObjectProperty().getIRI().getRemainder().get());
				sink.link_();
				sink.list();
				for (OWLIndividual value: objectValues.get(objectProp)){
					sink.listItem();
					sink.text(value.asOWLNamedIndividual().getIRI().getRemainder().get());
					sink.listItem_();
				}
				sink.list_();
				sink.link_();
				sink.listItem_();
			}
			sink.list_();
			sink.listItem_();

			// 16.5: Adding comments
			sink.listItem();
			sink.text("Comments");
			sink.list();
			EntitySearcher.getAnnotations(individual, ontology).forEach( (annotation) -> {
				if (annotation.getProperty().isComment()){
					sink.listItem();
					sink.text(annotation.getValue().toString());
					sink.listItem_();
				}
			});
			sink.list_();
			sink.listItem_();


			// 16.6: Finalization individuals table
			sink.list_();
			sink.tableCell_();
			sink.tableRow_();
			sink.table_();
		}


		// 17: Finalizing document
		log.info("Finalizing html document...");
		sink.body_();
		sink.flush();

		log.info("Report successfully created...");
		Util.printTail(log);

		//18: Ensure destination folder(s) are created
		if (getOutputName().contains(File.separator)) {
			String target = getOutputDirectory() + File.separator + getOutputName();
			File targetDir = new File(target);
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}
		}
	}

	/** Checks, in which format the ontology is.*/
	private String getFormat(OWLOntology ontology, OWLOntologyManager manager) {
		String formatName = "unknown format";
		try {
			OWLDocumentFormat format = manager.getOntologyFormat(ontology);
			if (format != null)
				formatName = format.toString();
		} catch (UnknownOWLOntologyException e){}

		return formatName;
	}

	/** Detects the names of profiles, in which the ontology is.*/
	private String getProfiles(OWLOntology ontology) {
		String profiles = "";

		// Creating profiles
		OWL2Profile owl2Profile = new OWL2Profile();
		OWL2DLProfile owl2DLProfile = new OWL2DLProfile();
		OWL2ELProfile owl2ELProfile = new  OWL2ELProfile();
		OWL2QLProfile owl2QLProfile = new OWL2QLProfile();
		OWL2RLProfile owl2RLProfile = new OWL2RLProfile();

		// Check for every profile if ontology is in profile
		if (owl2Profile.checkOntology(ontology).isInProfile())
			profiles += owl2Profile.getName();
		if (owl2DLProfile.checkOntology(ontology).isInProfile())
			profiles += ", " + owl2DLProfile.getName();
		if (owl2ELProfile.checkOntology(ontology).isInProfile())
			profiles += ", " + owl2ELProfile.getName();
		if (owl2QLProfile.checkOntology(ontology).isInProfile())
			profiles += ", " + owl2QLProfile.getName();
		if (owl2RLProfile.checkOntology(ontology).isInProfile())
			profiles += ", " + owl2RLProfile.getName();

		return profiles;
	}

	/** Creates a row with a given title and a count (int) in two cells */
	private void createCountRow(String title, int count, Sink sink) {
		sink.tableRow();
		sink.tableCell();
		sink.text(title);
		sink.tableCell_();
		sink.tableCell();
		sink.text(String.valueOf(count));
		sink.tableCell_();
		sink.tableRow_();
	}

	/**
	 * Returns as artefact description the description of the ontology
	 */
	public String getDescription(Locale locale) {
		return "Description";
	}

	/** Returns as artefact name the name of the ontology */
	public String getName(Locale locale) {
		return "Ontology Report";
	}

	/** Returns the name of this output artifact */
	public String getOutputName() {
		return outputName;
	}

	/** Returns the path, where this report have to been saved */
	@Override
	protected String getOutputDirectory() {
		return new File(ontologyReportOutputDirectory).getAbsolutePath();
	}

	/** Sets the directory path, where the report have been to saved */
	public void setReportOutputDirectory(File reportOutputDirectory) {
		super.setReportOutputDirectory(reportOutputDirectory);
	}

	/** Returns the current MavenProject as object. */
	@Override
	protected MavenProject getProject() {
		return project;
	}

	/** Rerturns a SiteRenderer which is used internal by maven */
	@Override
	protected Renderer getSiteRenderer() {
		return siteRenderer;
	}
}