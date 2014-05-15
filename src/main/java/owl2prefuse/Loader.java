package owl2prefuse;

import prefuse.data.Graph;
import prefuse.data.Tree;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.TreeMLReader;

/**
 * This class loads a GraphML or a TreeML file and returns a Prefuse graph or a 
 * tree respectively.
 * <p/>
 * Project OWL2Prefuse <br/>
 * Loader.java created 3 januari 2007, 15:57
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
public final class Loader
{
    /**
     * This static method loads a GraphML file and returns a Prefuse Graph.
     * Please note that each node has to have a "name" attribute associated with it 
     * to get properly displayed.
     * @param p_file The filepath to the GraphML file.
     * @return A Prefuse Graph.
     */
    public static Graph loadGraphML(String p_file)
    {
        Graph graph = null;
        try
        {
            graph = new GraphMLReader().readGraph(p_file);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        
        return graph;
    }
    
    /**
     * This static method loads a TreeML file and returns a Prefuse Tree.
     * @param p_file The filepath to the TreeML file.
     * @return A Prefuse Tree.
     */       
    public static Tree loadTreeML(String p_file)
    {
        Tree tree = null;
        try
        {
            tree = (Tree) new TreeMLReader().readGraph(p_file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        
        return tree;
    }
}