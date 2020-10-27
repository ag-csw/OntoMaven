package owl2prefuse.graph;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import java.util.ArrayList;
import java.util.Hashtable;
import owl2prefuse.Converter;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;

/**
 * This class converts the given OWL Model to a Prefuse graph datastructure.
 * <p/>
 * Project OWL2Prefuse <br/>
 * OWLGraphConverter.java created 3 januari 2007, 9:58
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 * 
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
public class OWLGraphConverter extends Converter
{
    /**
     * The created Prefuse graph.
     */
    private Graph m_graph;
    
    /**
     * An ArrayList containing all the edges that have to be added to the Prefuse 
     * graph.
     */
    private ArrayList<String[]> m_edges;
    
    /**
     * An Hashtable containing all the nodes in Prefuse graph.
     */
    private Hashtable<String, Node> m_nodes;
    
    /**
     * An ArrayList containing the URI's of OWL classes that should not be converted 
     * into the Prefuse graph.
     */
    private ArrayList<String> m_uselessType;
    
    /**
     * Creates a new instance of OWLGraphConverter
     * @param p_OWLFile The path to the OWL file that needs to be converted.
     * @param p_directed A boolean indicating whether the Prefuse graph needs to 
     * be directed.
     */
    public OWLGraphConverter(String p_OWLFile, boolean p_directed)
    {
        super(p_OWLFile);
        init(p_directed);
    }
    
    /**
     * Creates a new instance of OWLGraphConverter
     * @param p_model The Jena model that needs to be converted.
     * @param p_directed A boolean indicating whether the Prefuse graph needs to 
     * be directed.
     */
    public OWLGraphConverter(OntModel p_model, boolean p_directed)
    {
        super(p_model);
        init(p_directed);
    }
    
    /**
     * Initialize the graph converter.
     * @param p_directed A boolean indicating whether the Prefuse graph needs to 
     * be directed.
     */
    private void init(boolean p_directed)
    {
        m_edges = new ArrayList<String[]>();
        m_nodes = new Hashtable<String, Node>();
        
        // Create an ArrayList which contains URI's of nodes we do not want to 
        // visualize, because they are to general.
        m_uselessType = new ArrayList<String>();
        m_uselessType.add("http://www.w3.org/2002/07/owl#Class");
        m_uselessType.add("http://www.w3.org/2000/01/rdf-schema#Class");
        
        // Create the graph.
        createGraph(p_directed);
    }
    
    /**
     * Return the created Prefuse graph.
     * @return The created Prefuse graph.
     */
    public Graph getGraph()
    {
        return m_graph;
    }
    
    /**
     * Create the Prefuse graph. This method creates an empty graph and adds the 
     * appropriate columns to the node- and edgestable. After that it gets the root 
     * class (owl:Thing) of the OWL graph and recursively starts building the graph 
     * from there.
     * This method is automatically called from the constructors of this converter.
     * @param p_directed A boolean indicating whether the Prefuse graph needs to 
     * be directed.
     */
    private void createGraph(boolean p_directed)
    {
        // Create a new empty graph.
        m_graph = new Graph(p_directed);
        
        // Add the appropriate columns.
        m_graph.getNodeTable().addColumn("URI", String.class);
        m_graph.getNodeTable().addColumn("name", String.class);
        m_graph.getNodeTable().addColumn("type", String.class);
        m_graph.getEdgeTable().addColumn("label", String.class);
        
        // Get the root node.
        OntClass rootClass = m_model.getOntClass("http://www.w3.org/2002/07/owl#Thing");

        // Build the entire tree.
        buildGraph(rootClass);
        
        // All the edges are stored in an ArrayList, because they can only be added
        // if all the appropriate nodes exist. At this point this is the case, so
        // all the nodes are created.
        createEdges();
    }
    
    /**
     * Build the Prefuse graph, this method is called recursively.
     * @param p_currentClass The class which is being added to the graph.
     */
    private void buildGraph(OntClass p_currentClass)
    {
        // If there is no root node yet, one is created.
        Node currNode = m_graph.addNode();
        currNode.setString("URI", p_currentClass.getURI());
        currNode.setString("name", p_currentClass.getLocalName());
        currNode.setString("type", "class");
        
        // Walk trough the subclasses of the current class.
        ExtendedIterator itClasses = p_currentClass.listSubClasses(true);
        while(itClasses.hasNext())
        {
            // Recurse trough the subclasses of the current node.
            buildGraph((OntClass) itClasses.next());
        }
        
        // Walk trough the instances of the current class.
        ExtendedIterator itIndividuals = p_currentClass.listInstances();
        while(itIndividuals.hasNext())
        {
            Individual foundIndividual = (Individual) itIndividuals.next();
            
            // Only visualize nodes which have a (valid) URI. So no blank nodes.
            if (foundIndividual.getURI() != null)
            {
                // Create the node for this instance.
                Node node = m_graph.addNode();
                node.setString("URI", foundIndividual.getURI());
                node.setString("name", foundIndividual.getLocalName());
                node.setString("type", "individual");
            
                // Add this node to the nodes ArrayList.
                m_nodes.put(foundIndividual.getURI(), node);

                // Add the edges, connected to this node, to the edges ArrayList.
                storeEdges(foundIndividual);
            }
        }
        
        // Add this node to the nodes ArrayList.
        m_nodes.put(p_currentClass.getURI(), currNode);
        
        // Add the edges, connected to this node, to the edges ArrayList.
        storeEdges(p_currentClass);
    }
    
    /**
     * Temporarily store the edges which need to be added the graph. All the edges 
     * are stored in an ArrayList, because they can only be added if all the 
     * appropriate nodes exist. At this point this is the case, so all the nodes 
     * are created.
     * @param p_resource The Jena OntResource of which the edges need to be stored.
     */
    private void storeEdges(OntResource p_resource)
    {
        String sourceURI = p_resource.getURI();
        
        if (!m_uselessType.contains(sourceURI))
        {
            StmtIterator itProperties = p_resource.listProperties();
            while (itProperties.hasNext())
            {
                Statement property = itProperties.nextStatement();
                String localName = property.getPredicate().getLocalName();

                if (property.getObject().isResource())
                {
                    String targetURI = ((Resource) property.getObject()).getURI();
                    if (!m_uselessType.contains(targetURI) && targetURI != null)
                    {
                        String[] edge = new String[3];
                        edge[0] = sourceURI;
                        edge[1] = localName;
                        edge[2] = targetURI;
                        m_edges.add(edge);
                    }
                }
            }
        }
    }
    
    /**
     * Create edges for the relevant properties of the resource.
     */
    private void createEdges()
    {
        for (int i = 0; i < m_edges.size(); i++)
        {
            String[] strEdge = m_edges.get(i);
            
            // Get the source and the target node.
            Node source = m_nodes.get(strEdge[0]);
            if (source == null) continue;
            Node target = m_nodes.get(strEdge[2]);
            if (target == null) continue;
            
            Edge edge = m_graph.addEdge(source, target);
            edge.setString("label", strEdge[1]);
        }
    }
}