package owl2prefuse.tree;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

import owl2prefuse.Constants;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 * This class creates a display for a tree.
 * <p/>
 * Project OWL2Prefuse <br/>
 * TreeDisplay.java created 2 januari 2007, 13:44
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 * 
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
public class TreeDisplay extends Display
{
    /**
     * The label renderer for nodes. This renderer shows the nodes with a lable 
     * on them.
     */
    private LabelRenderer m_nodeRenderer;
    
    /**
     * The renderer for the edges.
     */
    private EdgeRenderer m_edgeRenderer;
    
    /**
     * The orientation of the tree, this is left-to-right by default.
     */
    private int m_orientation = prefuse.Constants.ORIENT_LEFT_RIGHT;
    
    /**
     * The searchpanel, used for the keyword search in the tree.
     */
    private JSearchPanel m_search;
    
    /**
     * The label which displays the URI of the node under the mouse.
     */
    private JFastLabel m_URILabel;
    
    /**
     * The focus control of the tree.
     */
    private FocusControl m_focusControl;
    
    /**
     * Creates a new instance of TreeDisplay.
     * 
     * @param p_tree The Prefuse tree that has to be displayed.
     */
    public TreeDisplay(Tree p_tree)
    {
        // Create a new Display with an empty visualization.
        super(new Visualization());
        
        // Initialize the visualization.
        initVisualization(p_tree);
        
        // Initialize this display.
        initDisplay();
        
        // Create the search panel.
        createSearchPanel();
        
        // Create the title label.
        createTitleLabel();
        
        // Filter graph and perform layout.
        setOrientation(m_orientation);
        m_vis.run("filter");
    }
    
    /**
     * Add the tree to the visualization as the data group "tree"
     * nodes and edges are accessible as "tree.nodes" and "tree.edges". A
     * renderer is created to render the edges and the nodes in the tree.
     * @param p_tree The Prefuse Tree to be displayed.
     */
    public void initVisualization(Tree p_tree)
    {
        // Add the tree to the visualization.
        m_vis.add(Constants.TREE, p_tree);
        
        // Create a renderer for the nodes which draws the labels on them.
        m_nodeRenderer = new LabelRenderer(Constants.TREE_NODE_LABEL);
        m_nodeRenderer.setRenderType(ShapeRenderer.RENDER_TYPE_FILL);
        m_nodeRenderer.setHorizontalAlignment(prefuse.Constants.LEFT);
        m_nodeRenderer.setRoundedCorner(8,8);
        
        // Create a renderer for the edges.
        m_edgeRenderer = new EdgeRenderer(prefuse.Constants.EDGE_TYPE_CURVE);
        
        // Create a render factory with the two previously created renderers.
        DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(Constants.TREE_EDGES), m_edgeRenderer);
        
        // Add the render factory to the visualization.
        m_vis.setRendererFactory(rf);
        
        // Set the color for the nodes and the text on the nodes.
        ItemAction nodeColor = new NodeColorAction(Constants.TREE_NODES, m_vis);
        ItemAction textColor = new ColorAction(Constants.TREE_NODES, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0));
        
        // Set the color for the edges.
        ItemAction edgeColor = new ColorAction(Constants.TREE_EDGES, VisualItem.STROKECOLOR, ColorLib.rgb(200,200,200));
        
        // Quick repaint
        ActionList repaint = new ActionList();
        repaint.add(nodeColor);
        repaint.add(new RepaintAction());
        m_vis.putAction("repaint", repaint);
        
        // Full paint
        ActionList fullPaint = new ActionList();
        fullPaint.add(nodeColor);
        m_vis.putAction("fullPaint", fullPaint);
        
        // Animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(Constants.TREE_NODES));
        animatePaint.add(new RepaintAction());
        m_vis.putAction("animatePaint", animatePaint);
        
        // Create the tree layout action
        NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(Constants.TREE, m_orientation, 50, 2, 8);
        treeLayout.setLayoutAnchor(new Point2D.Double(25,300));
        m_vis.putAction("treeLayout", treeLayout);
        
        // Create the layout for the collapsed subtree.
        CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(Constants.TREE, m_orientation);
        m_vis.putAction("subLayout", subLayout);
        
        // Create the filtering and layout
        ActionList filter = new ActionList();
        filter.add(new FisheyeTreeFilter(Constants.TREE, 2));
        filter.add(treeLayout);
        filter.add(subLayout);
        filter.add(new FontAction(Constants.TREE_NODES, FontLib.getFont("Tahoma", 12)));        
        filter.add(textColor);
        filter.add(nodeColor);
        filter.add(edgeColor);
        
        m_vis.putAction("filter", filter);
        
        // Animated transition.
        ActionList animate = new ActionList(1000);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(Constants.TREE));
        animate.add(new LocationAnimator(Constants.TREE_NODES));
        animate.add(new ColorAnimator(Constants.TREE_NODES));
        animate.add(new RepaintAction());
        m_vis.putAction("animate", animate);
        m_vis.alwaysRunAfter("filter", "animate");
        
        // Create animator for orientation changes
        ActionList orient = new ActionList(2000);
        orient.setPacingFunction(new SlowInSlowOutPacer());
        orient.add(new QualityControlAnimator());
        orient.add(new LocationAnimator(Constants.TREE_NODES));
        orient.add(new RepaintAction());
        m_vis.putAction("orient", orient);

        // Create a tupleset for the keyword search resultset.
        TupleSet search = new PrefixSearchTupleSet();
        m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
        search.addTupleSetListener(new TupleSetListener()
        {
            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem)
            {
                m_vis.cancel("animatePaint");
                m_vis.run("fullPaint");
                m_vis.run("animatePaint");
            }
        });
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
        
        // Set the iterm so the three depth item sorter.
        setItemSorter(new TreeDepthItemSorter());
        
        // Double click for zooming to fit the graph.
        addControlListener(new ZoomToFitControl());
        
        // Zoom with vertical right-drag.
        addControlListener(new ZoomControl());
        
        // Pan with background left-drag.
        addControlListener(new PanControl());
        
        // Conrol which nodes are in focus.
        m_focusControl = new FocusControl(1, "filter");
        addControlListener(m_focusControl);
    }
    
    /**
     * Get the focus control of the tree, so it can be adjusted or even removed 
     * to implement a custom made focus control.
     * @return The focus control of the tree.
     */
    public FocusControl getFocusControl()
    {
        return m_focusControl;
    }
    
    /**
     * Set the orientation of the tree, this can be:
     * - left-to-right (prefuse.Constants.ORIENT_LEFT_RIGHT)
     * - right-to-left (prefuse.Constants.ORIENT_RIGHT_LEFT)
     * - top-to-bottom (prefuse.Constants.ORIENT_TOP_BOTTOM)
     * - bottom-to-top (prefuse.Constants.ORIENT_BOTTOM_TOP)
     * @param orientation The orientation of the tree.
     */    
    protected void setOrientation(int orientation)
    {
        NodeLinkTreeLayout rtl = (NodeLinkTreeLayout) m_vis.getAction("treeLayout");
        CollapsedSubtreeLayout stl = (CollapsedSubtreeLayout) m_vis.getAction("subLayout");
        switch (orientation)
        {
            case prefuse.Constants.ORIENT_LEFT_RIGHT:
                m_nodeRenderer.setHorizontalAlignment(prefuse.Constants.LEFT);
                m_edgeRenderer.setHorizontalAlignment1(prefuse.Constants.RIGHT);
                m_edgeRenderer.setHorizontalAlignment2(prefuse.Constants.LEFT);
                m_edgeRenderer.setVerticalAlignment1(prefuse.Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment2(prefuse.Constants.CENTER);
                break;
            case prefuse.Constants.ORIENT_RIGHT_LEFT:
                m_nodeRenderer.setHorizontalAlignment(prefuse.Constants.RIGHT);
                m_edgeRenderer.setHorizontalAlignment1(prefuse.Constants.LEFT);
                m_edgeRenderer.setHorizontalAlignment2(prefuse.Constants.RIGHT);
                m_edgeRenderer.setVerticalAlignment1(prefuse.Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment2(prefuse.Constants.CENTER);
                break;
            case prefuse.Constants.ORIENT_TOP_BOTTOM:
                m_nodeRenderer.setHorizontalAlignment(prefuse.Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment1(prefuse.Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment2(prefuse.Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment1(prefuse.Constants.BOTTOM);
                m_edgeRenderer.setVerticalAlignment2(prefuse.Constants.TOP);
                break;
            case prefuse.Constants.ORIENT_BOTTOM_TOP:
                m_nodeRenderer.setHorizontalAlignment(prefuse.Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment1(prefuse.Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment2(prefuse.Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment1(prefuse.Constants.TOP);
                m_edgeRenderer.setVerticalAlignment2(prefuse.Constants.BOTTOM);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized orientation value: " + orientation);
        }
        m_orientation = orientation;
        rtl.setOrientation(orientation);
        stl.setOrientation(orientation);
    }
    
    /**
     * Get the current orientation of the tree display.
     * @return The Prefuse Constant (int) that represents the current orientation.
     */
    protected int getOrientation()
    {
        return m_orientation;
    }
    
    /**
     * This method creates the keyword searchpanel.
     */
    private void createSearchPanel()
    {
        // Create a search panel for the tree.
        m_search = new JSearchPanel(m_vis, Constants.TREE_NODES, Visualization.SEARCH_ITEMS, Constants.TREE_NODE_LABEL, true, true);
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
}