package owl2prefuse;

import prefuse.data.Graph;
import prefuse.data.Tree;
import prefuse.data.io.GraphMLWriter;
import prefuse.data.io.TreeMLWriter;

/**
 * This class contains the methods for writing GraphML and TreeML.
 * <p/>
 * Project OWL2Prefuse <br/>
 * Writer.java created 4 januari 2007, 11:05
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
public final class Writer
{
    /**
     * Write the given graph to GraphML.
     * @param p_graph The graph to be written to GraphML.
     * @param p_file The file to which the GraphML is written.
     */
    public static void writeGraphML(Graph p_graph, String p_file)
    {
        try
        {
            new GraphMLWriter().writeGraph(p_graph, p_file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Write the given tree to TreeML.
     * @param p_tree The tree to be written to TreeML.
     * @param p_file The file to which the TreeML is written.
     */
    public static void writeTreeML(Tree p_tree, String p_file)
    {
        try
        {
            new TreeMLWriter().writeGraph(p_tree, p_file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
}