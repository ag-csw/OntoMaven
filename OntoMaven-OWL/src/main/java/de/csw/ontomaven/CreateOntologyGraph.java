package de.csw.ontomaven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owl2prefuse.graph.OWLGraphConverter;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLWriter;

import com.hp.hpl.jena.ontology.OntModel;

import de.csw.ontomaven.util.Util;

/**
 * Creates a graphml file of the ontology. This file can be visualized
 * with any tool, which supports graphml files.
 * 
 * @goal CreateOntologyGraph
 * @phase site
 */
public class CreateOntologyGraph extends AbstractMojo {
	
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
	 * The output directory for the graph files. There will be saved
	 * the graph file. It should be relative path.
	 * 
	 * @parameter property="graphDirectory"
	 *            default-value="target/site/graph"
	 */
	private String graphDirectory;
	
	/**
	 * The name of the produced graph file. It should be name like
	 * "myGraph.graphml"
	 * 
	 * @parameter property="graphFileName"
	 *            default-value="owlGraph.graphml"
	 */
	private String graphFileName;
	
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
	 * Executes the creation of the graph file.
	 */
	public void execute() throws MojoExecutionException {
		
		Log log = getLog();
		Util.printHead("Creating graph...", log);
		
		// Loading ontology
		OWLOntologyManager manager = Util.createManager();
		File owlFile = new File(owlDirectory + File.separator + owlFileName);
		OWLOntology ontology = Util.loadOntologyFile(manager, log, owlFile);
		if (ontology == null) return; // Ontology not loaded
		
		
		// Applying aspects
		if(ifApplyAspects)
			Util.applyAspects(manager, aspectsIRI, ontology, userAspects, log);
		
		
		// Converting the ontology into the Jena OntModel format because
		// the owl2prefuse library only works with OntModel ontologies
		OntModel ontologyAsOntModel = Util.owlOntologyToJenaModel(ontology, false,
				log);		
		
		// Loading ontology and building graph
		OWLGraphConverter graphConverter = new OWLGraphConverter(
				ontologyAsOntModel, false);
		Graph graph = graphConverter.getGraph();
		log.info("Ontology is converted to graph.");
		
		
		// Creating output directory, if not existing
		new File(graphDirectory).mkdirs();

		
		// Saving graph in GraphML format
		log.info("Writing graph into file...");
		File graphFile = new File(graphDirectory + File.separator
				+ graphFileName);
		try {
			new GraphMLWriter().writeGraph(graph, graphFile);
			log.info("Graph written in " + graphFile.getAbsolutePath());
		} catch (DataIOException e) {
			log.error("Cannot create graph file.", e);
		}
		
		Util.printTail(log);
	}
	
	
}