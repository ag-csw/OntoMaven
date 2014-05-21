package owl2prefuse;

import java.awt.Color;
import prefuse.util.ColorLib;

/**
 * This class contains all the instances which are used in the program.
 * <p/>
 * Project OWL2Prefuse <br/>
 * Constants.java created 3 januari 2007, 15:05
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
public final class Constants
{
    /**
     * The color of nodes in the Prefuse visualization which represent OWL Classes.
     */
    public final static int NODE_COLOR_CLASS = ColorLib.rgb(253, 211, 100);
    
    /**
     * The color of nodes in the Prefuse visualization which represent OWL Individuals.
     */
    public final static int NODE_COLOR_INDIVIDUAL = ColorLib.rgb(220, 70, 217);
    
    /** 
     * The color of selected nodes in the Prefuse visualization.
     */
    public final static int NODE_COLOR_SELECTED = ColorLib.rgb(255, 100, 100);
    
    /**
     * The color of highlighted nodes in the Prefuse visualization.
     */
    public final static int NODE_COLOR_HIGHLIGHTED = ColorLib.rgb(144, 253, 126);
    
    /**
     * The color of nodes in the keyword search resultset in the Prefuse visualization.
     */
    public final static int NODE_COLOR_SEARCH = ColorLib.rgb(255,190,190);
    
    /**
     * The default color for nodes (if nothing else applies).
     */
    public final static int NODE_DEFAULT_COLOR = ColorLib.rgb(200, 200, 255);
    
    /**
     * The name of the data group which represents a graph in Prefuse.
     */
    public static final String GRAPH = "graph";
    
    /**
     * The name of the data group which represents nodes in a graph in Prefuse.
     */
    public static final String GRAPH_NODES = "graph.nodes";
    
    /**
     * The name of the data group which represents edges in a graph in Prefuse.
     */
    public static final String GRAPH_EDGES = "graph.edges";
    
    /**
     * The name of the data group which represents decorators of edges in a graph 
     * in Prefuse.
     */
    public static final String EDGE_DECORATORS = "edgeDecorators";
    
    /**
     * The label of nodes in a tree.
     */
    public static final String TREE_NODE_LABEL = "name";
    
    /**
     * The name of the data group which represents a tree in Prefuse.
     */
    public static final String TREE = "tree";
    
    /**
     * The name of the data group which represents nodes in a tree in Prefuse.
     */
    public static final String TREE_NODES = "tree.nodes";
    
    /**
     * The name of the data group which represents edges in a tree in Prefuse.
     */
    public static final String TREE_EDGES = "tree.edges";
    
    /**
     * The background color of the visualizations.
     */
    public static final Color BACKGROUND = Color.WHITE;
    
    /**
     * The foreground color of the visualizations.
     */
    public static final Color FOREGROUND = Color.BLACK;
}