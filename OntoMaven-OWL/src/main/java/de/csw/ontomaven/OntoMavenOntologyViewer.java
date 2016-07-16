package de.csw.ontomaven;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OWLOntologyImportsClosureSetProvider;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import owl2prefuse.graph.GraphDisplay;
import owl2prefuse.graph.GraphPanel;
import owl2prefuse.graph.OWLGraphConverter;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;

public class OntoMavenOntologyViewer {
	public static void main(String[] args) {

		// Creating and opening a fileChooser which accepts only .xml
		// and .graphMl files, Get the file
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"GraphML File or OWL File", "graphml", "owl");
		fileChooser.setFileFilter(filter);
		fileChooser.showOpenDialog(fileChooser);
		fileChooser.setDialogTitle("Please choose an .owl or .graphml file.");
		File fileToVisualize = fileChooser.getSelectedFile();

		
		// If no file choosen, break
		if (fileToVisualize == null) {
			JOptionPane.showMessageDialog(null, "File choosing not succesfull");
			return;
		}
		
		
		// Reading Graph: if owl file choosen, load ontology and create graph
		// else if graphml file choosen, visualize it
		Graph graph = null;
		if (fileToVisualize.getName().toLowerCase().endsWith("graphml")) {
			GraphMLReader graphReader = new GraphMLReader();
			try {
				graph = graphReader.readGraph(fileToVisualize);
			} catch (DataIOException e) {
				e.printStackTrace();
			}
		} else if (fileToVisualize.getName().toLowerCase().endsWith("owl")) {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = null;
			try {
				ontology = manager.loadOntologyFromOntologyDocument(fileToVisualize);
			} catch (OWLOntologyCreationException e1) {
				e1.printStackTrace();
			}

			// Converting the ontology into the Jena OntModel format because
			// the owl2prefuse library only works with OntModel ontologies
			OntModel ontologyAsOntModel = owlOntologyToJenaModel(ontology, false);
			
			System.out.println(ontology.getAxiomCount());
			System.out.println(ontologyAsOntModel.getProfile().toString());
			// Loading ontology and building graph
			OWLGraphConverter graphConverter = new OWLGraphConverter(
					ontologyAsOntModel, false);
			graph = graphConverter.getGraph();
		}
		
		
		// Creating a graph display to visualize graph.
		GraphDisplay graphDisplay = new GraphDisplay(graph, true);

		
		// Creating a graph panel by adding a legend and hops control
		// to the above GraphicDisplay
		GraphPanel graphPanel = new GraphPanel(graphDisplay, true, true);

		
		// Creating window (JFrame) which will contain the graph
		final JFrame window = new JFrame("Ontology Graph View");
		window.add(graphPanel);
		window.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		
		// Adding a save button to the window, with that a screenshot of
		// the graph can be saved.
		JLabel saveButton = new JLabel("Save2Image");
		saveButton.setFont(new Font("Helvetica", Font.BOLD, 25));
		saveButton.setBackground(Color.ORANGE);
		saveButton.setOpaque(true);
		saveButton.addMouseListener(new MouseAdapter(){
	        public void mouseClicked(MouseEvent e){
	            saveScreenShot(window);
	        }
	    });
		graphDisplay.setLayout(new FlowLayout(FlowLayout.RIGHT));
		graphDisplay.add(saveButton);

		
		// Making window visible
		window.pack();
		window.setVisible(true);
	}

	public static void saveScreenShot(Component component) {

		// Getting screenshot of the components
		BufferedImage image = new BufferedImage(component.getWidth(),
				component.getHeight(), BufferedImage.TYPE_INT_RGB);
		component.paint(image.getGraphics());

		// Creating file chooser, with that a file can be choosen, in which
		// the image will be saved.
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.showSaveDialog(null);
		File choosenTargetFile = fileChooser.getSelectedFile();

		// Saving file
		if (choosenTargetFile == null) {
			JOptionPane.showMessageDialog(null, "File choosing not succesfull");
		} else {
			String savePath = choosenTargetFile.getAbsolutePath() + ".png";
			try {
				ImageIO.write(image, "png", new File(savePath));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}

	/**
	 * Converts an OWL API OWLOntology to a Jena OntModel.
	 * @param ontology An OWL API OWLOntology.
	 * @return The corresponding Jena OntModel.
	 * @throws OWLOntologyStorageException
	 * @author Ralph Schäfermeier
	 */
	public static OntModel owlOntologyToJenaModel(OWLOntology owlOntology,
			boolean withImports) {
		
		OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		
		if (withImports) {
			try {
				owlOntology = new OWLOntologyMerger(
						new OWLOntologyImportsClosureSetProvider(om, owlOntology))
						.createMergedOntology(om, null);
			} catch (OWLOntologyCreationException e) {
				System.err.println("Cannot Convert ontology into jena model.");
			}
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			owlOntology.getOWLOntologyManager().saveOntology(owlOntology,
					new RDFXMLDocumentFormat(), baos);
		} catch (OWLOntologyStorageException e) {
			System.err.println("Cannot create jena model of the ontology. "
					+ System.lineSeparator() + e.getMessage());
		}
		byte[] bytes = baos.toByteArray();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);		
		OntModel jenaModel = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM);
		jenaModel.read(bais, null, "RDF/XML");
		return jenaModel;
	}	

}