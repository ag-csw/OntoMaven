package owl2prefuse.graph;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import owl2prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 * This class creates a display for a graph.
 * <p/>
 * Project OWL2Prefuse <br/>
 * SimpleGraphjava created 3 januari 2007, 11:17
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
public class GraphDisplay extends Display
{
    /**
     * Create data description of labels, setting colors, and fonts ahead of time
     */
    private static final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();
    static
    {
        DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
        DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(0));
        DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 10));
    }
    
    /**
     * The searchpanel, used for the keyword search in the graph.
     */
    private JSearchPanel m_search;
    
    /**
     * The label which displays the URI of the node under the mouse.
     */
    private JFastLabel m_URILabel;
    
    /**
     * The GraphDistanceFilter, which makes sure that only the nodes with a certain
     * number of hops away from the currently selected node, are displayed.
     */
    private GraphDistanceFilter m_filter;
    
    /**
     * The focus control of the graph.
     */
    private FocusControl m_focusControl;
    
    /**
     * The force directed layout.
     */
    private ForceDirectedLayout m_fdl;
    
    /**
     * The force simulator of the force directed layout.
     */
    private ForceSimulator m_fsim;
    
    /**
     * Creates a new instance of GraphDisplay
     * @param p_graph The Prefuse Graph to be displayed.
     * @param p_distancefilter A boolean, indicating whether or not a GraphDistance
     * filter should be used with this display.
     */
    public GraphDisplay(Graph p_graph, boolean p_distancefilter)
    {
        // Create a new Display with an empty visualization.
        super(new Visualization());
        
        initVisualization(p_graph, p_distancefilter);
        initDisplay();
        
        // Create the search panel.
        createSearchPanel();
        
        // Create the title label.
        createTitleLabel();
        
        m_vis.run("draw");
    }
    
    /**
     * Add the graph to the visualization as the data group "graph"
     * nodes and edges are accessible as "graph.nodes" and "graph.edges". A
     * renderer is created to render the edges and the nodes in the graph.
     * @param p_graph The Prefuse Graph to be displayed.
     * @param p_distancefilter A boolean, indicating whether or not a GraphDistance
     * filter should be used with this display.
     */
    private void initVisualization(Graph p_graph, boolean p_distancefilter)
    {
        // Add the graph to the visualization.
        VisualGraph vg = m_vis.addGraph(Constants.GRAPH, p_graph);
        
        // Set up a label renderer for the labels on the nodes and add it to the
        // visualization.
        LabelRenderer nodeRenderer = new LabelRenderer("name");
        nodeRenderer.setRoundedCorner(8, 8);
        
        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.add(new InGroupPredicate(Constants.GRAPH_NODES), nodeRenderer);
        drf.add(new InGroupPredicate(Constants.EDGE_DECORATORS), new LabelRenderer("label"));
        drf.add(new InGroupPredicate(Constants.GRAPH_EDGES), new EdgeRenderer(prefuse.Constants.EDGE_TYPE_LINE, prefuse.Constants.EDGE_ARROW_FORWARD));
        m_vis.setRendererFactory(drf);
        
        // Add the decorator for the labels.
        m_vis.addDecorators(Constants.EDGE_DECORATORS, Constants.GRAPH_EDGES, DECORATOR_SCHEMA);
        
        // Set the interactive value of the edges to false.
        m_vis.setValue(Constants.GRAPH_EDGES, null, VisualItem.INTERACTIVE, Boolean.FALSE);
        
        // Get the first node and give it focus, this triggers the distance filter
        // to at least show all nodes with a maximum of 4 hops away from this one.
        if (p_distancefilter)
        {
            VisualItem f = (VisualItem) vg.getNode(0);
            m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
            
            // The position of the first node is not fixed.
            f.setFixed(false);
        }
        
        // Create a focus listener which fixex the position of the selected nodes.
        TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS);
        focusGroup.addTupleSetListener(getFocusedItemsListner());
        
        // Finally, we register our ActionList with the Visualization.
        // We can later execute our Actions by invoking a method on our Visualization,
        // using the name we have chosen below.
        m_vis.putAction("draw", getDrawActions(p_distancefilter));
        m_vis.putAction("layout", getLayoutActions());
        m_vis.runAfter("draw", "layout");
    }
    
    /**
     * Initialize this display. This method adds several control listeners, sets
     * the size and sets the foreground and background colors.
     */
    private void initDisplay()
    {
        // Set the display size.
        setSize(500,500);
        
        // Set the foreground color.
        setForeground(Constants.FOREGROUND);
        
        // Set the background color.
        setBackground(Constants.BACKGROUND);
        
        // Drag items around.
        addControlListener(new DragControl());
        
        // Pan with background left-drag.
        addControlListener(new PanControl());
        
        // Zoom with vertical right-drag.
        addControlListener(new ZoomControl());
        
        // Zoom using the scroll wheel.
        addControlListener(new WheelZoomControl());
        
        // Double click for zooming to fit the graph.
        addControlListener(new ZoomToFitControl());
        
        // Highlight the neighbours.
        addControlListener(new NeighborHighlightControl());
        
        // Conrol which nodes are in focus.
        m_focusControl = new FocusControl(1);
        addControlListener(m_focusControl);
    }

    /**
     * Get the focus control of the graph, so it can be adjusted or even removed
     * to implement a custom made focus control.
     * @return The focus control of the graph.
     */
    public FocusControl getFocusControl()
    {
        return m_focusControl;
    }
    
    /**
     * This metodh creates a TupleSetListener which listens for changes in a tuple
     * set.
     * @return A TupleSetListener.
     */
    private TupleSetListener getFocusedItemsListner()
    {
        TupleSetListener listner = new TupleSetListener()
        {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
            {
                // Set the fixed attribute for the nodes that are no longer in focus
                // to false.
                for (int i = 0; i < rem.length; ++i) ((VisualItem)rem[i]).setFixed(false);
                
                // Set the fixed attribute for the nodes that are added to the focus
                // to true.
                for (int i = 0; i < add.length; ++i)
                {
                    ((VisualItem)add[i]).setFixed(false);
                    ((VisualItem)add[i]).setFixed(true);
                }
                
                // If there are no nodes in focus, get the first one that is to
                // be removed, add it to the tuple set en set it' fixed attribute
                // to false. Thereby intializing the tupleset with one node.
                if (ts.getTupleCount() == 0)
                {
                    ts.addTuple(rem[0]);
                    ((VisualItem)rem[0]).setFixed(false);
                }
                
                // Run the draw action.
                m_vis.run("draw");
            }
        };
        
        return listner;
    }
    
    /**
     * Returns the actionlist for the colors of our graph.
     * @param p_distancefilter A boolean, indicating whether or not a GraphDistance
     * filter should be used with this display.
     * @return The actionlist containing the actions for the colors.
     */
    private ActionList getDrawActions(boolean p_distancefilter)
    {
        // Create an action list containing all color assignments.
        ActionList draw = new ActionList();
        
        // Create the graph distance filter, if wanted.
        if (p_distancefilter)
        {
            m_filter = new GraphDistanceFilter(Constants.GRAPH, 4);
            draw.add(m_filter);
        }
        
        // Use black for the text on the nodes.
        draw.add(new ColorAction(Constants.GRAPH_NODES, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));
        draw.add(new ColorAction(Constants.GRAPH_NODES, VisualItem.STROKECOLOR, ColorLib.gray(0)));
        
        // Use light grey for edges.
        draw.add(new ColorAction(Constants.GRAPH_EDGES, VisualItem.STROKECOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(Constants.GRAPH_EDGES, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        
        return draw;
    }
    
    /**
     * This method sets the parameter of the force directed layout that determines 
     * the distance between the nodes (e.g. the length of the edges).
     * @param p_value The new value of the force directed layout.
     */
    public void setForceDirectedParameter(float p_value)
    {
        m_fsim.getForces()[0].setParameter(0, p_value);
    }
    
    /**
     * Returns the actionlist for the layout of our graph.
     * @return The actionlist containing the layout of the graph.
     */
    private ActionList getLayoutActions()
    {
        // Make sure the nodes to not get to close together.
        m_fdl = new ForceDirectedLayout(Constants.GRAPH);
        m_fsim = m_fdl.getForceSimulator();
        m_fsim.getForces()[0].setParameter(0, -10f);
        
        // Create an action list with an animated layout, the INFINITY parameter
        // tells the action list to run indefinitely.
        ActionList layout = new ActionList(Activity.INFINITY);
        
        // Add the force directed layout.
        layout.add(m_fdl);
        
        layout.add(new HideDecoratorAction(m_vis));
        
        // Add the repaint action.
        layout.add(new RepaintAction());
        
        // Create te action which takes care of the coloring of the nodes which
        // are under the mouse.
        layout.add(new NodeColorAction(Constants.GRAPH_NODES, m_vis));
        
        // Add the LabelLayout for the labels of the edges.
        layout.add(new LabelLayout(Constants.EDGE_DECORATORS, m_vis));
        
        return layout;
    }
    
    /**
     * This method creates the keyword searchpanel.
     */
    private void createSearchPanel()
    {
        // Create a search panel for the tree.
        m_search = new JSearchPanel(m_vis, Constants.GRAPH_NODES, Visualization.SEARCH_ITEMS, Constants.TREE_NODE_LABEL, true, true);
        m_search.setShowResultCount(true);
        m_search.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
        m_search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
        m_search.setBackground(Constants.BACKGROUND);
        m_search.setForeground(Constants.FOREGROUND);
    }
    
    /**
     * This method creates the title label.
     */
    private void createTitleLabel()
    {
        // Create a label for the title of the nodes.
        m_URILabel = new JFastLabel("                 ");
        m_URILabel.setPreferredSize(new Dimension(350, 20));
        m_URILabel.setVerticalAlignment(SwingConstants.BOTTOM);
        m_URILabel.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
        m_URILabel.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 12));
        m_URILabel.setBackground(Constants.BACKGROUND);
        m_URILabel.setForeground(Constants.FOREGROUND);
        
        // The control listener for changing the title of a node at a mouseover event.
        this.addControlListener(new ControlAdapter()
        {
            public void itemEntered(VisualItem item, MouseEvent e)
            {
                if (item.canGetString("URI")) m_URILabel.setText(item.getString("URI"));
            }
            public void itemExited(VisualItem item, MouseEvent e)
            {
                m_URILabel.setText(null);
            }
        });
    }
    
    /**
     * Returns the keyword searchpanel.
     * @return The keyword searchpanel.
     */
    public JSearchPanel getSearchPanel()
    {
        return m_search;
    }
    
    /**
     * Returns the title label.
     * @return The title label.
     */
    public JFastLabel getTitleLabel()
    {
        return m_URILabel;
    }
    
    /**
     * Return the GraphDistanceFilter.
     * @return The GraphDistanceFilter.
     */
    public GraphDistanceFilter getDistanceFilter()
    {
        return m_filter;
    }
}