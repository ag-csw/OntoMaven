package owl2prefuse.tree;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;
import owl2prefuse.*;
import prefuse.data.Node;
import prefuse.data.Tree;

/**
 * This class converts the given OWL Model to a Prefuse tree datastructure.
 * <p/>
 * Project OWL2Prefuse <br/>
 * OWLTreeConverter.java created 2 januari 2007, 11:43
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 * 
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
public class OWLTreeConverter extends Converter
{
    /**
     * The Prefuse tree.
     */
    private Tree m_tree;
    
    /**
     * Creates a new instance of OWLTreeConverter.
     * @param p_OWLFile The path to the OWL file that needs to be converted.
     */
    public OWLTreeConverter(String p_OWLFile)
    {
        super(p_OWLFile);
        createTree();
    }
    
    /**
     * Creates a new instance of OWLTreeConverter.
     * @param p_model The Jena model that needs to be converted.
     */
    public OWLTreeConverter(OntModel p_model)
    {
        super(p_model);
        createTree();
    }
    
    /**
     * Return the created Prefuse tree.
     * @return The created Prefuse tree.
     */
    public Tree getTree()
    {
        return m_tree;
    }
    
    /**
     * Create the Prefuse tree. This method creates an empty tree and adds the 
     * appropriate columns to it. After that it gets the root class (owl:Thing) 
     * of the OWL graph and recursively starts building the tree from there.
     * This method is automatically called from the constructors of this converter.
     */
    private void createTree()
    {
        // Create a new empty tree.
        m_tree = new Tree();
        
        // Add the appropriate columns.
        m_tree.addColumn("URI", String.class);
        m_tree.addColumn("name", String.class);
        m_tree.addColumn("type", String.class);
        
        // Get the root node.
        OntClass rootClass = m_model.getOntClass("http://www.w3.org/2002/07/owl#Thing");

        // Build the entire tree.
        buildTree(null, rootClass);
    }
    
    /**
     * Build the Prefuse tree, this method is called recursively.
     * @param p_parent The parent node of the class that is being added to the graph.
     * @param p_currentClass The class which is being added to the graph.
     */
    private void buildTree(Node p_parent, OntClass p_currentClass)
    {
        // If there is no root node yet, one is created.
        Node currNode = null;
        if (p_parent == null) currNode = m_tree.addRoot();
        else currNode = m_tree.addChild(p_parent);
        
        currNode.setString("URI", p_currentClass.getURI());
        currNode.setString("name", p_currentClass.getLocalName());
        currNode.setString("type", "class");
            
        // Walk trough the subclasses of the current class.
        ExtendedIterator itClasses = p_currentClass.listSubClasses(true);
        while(itClasses.hasNext())
        {
            // Recurse trough the subclasses of the current node.
            buildTree(currNode, (OntClass) itClasses.next());
        }

        // Walk trough the instances of the current class.
        ExtendedIterator itIndividuals = p_currentClass.listInstances();
        while(itIndividuals.hasNext())
        {
            Individual foundIndividual = (Individual) itIndividuals.next();

            // Create the node for this instance.
            Node node = m_tree.addChild(currNode);
            node.setString("URI", foundIndividual.getURI());
            node.setString("name", foundIndividual.getLocalName());
            node.setString("type", "individual");
        }
    }
}