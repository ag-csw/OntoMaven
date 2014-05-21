package owl2prefuse.graph;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import prefuse.Visualization;
import prefuse.action.layout.Layout;
import prefuse.visual.DecoratorItem;
import prefuse.visual.VisualItem;

/**
 * Set label positions. Labels are assumed to be DecoratorItem instances,
 * decorating their respective nodes. The layout simply gets the bounds
 * of the decorated node and assigns the label coordinates to the center
 * of those bounds.
 * <p/>
 * Project OWL2Prefuse <br/>
 * LabelLayout.java created 3 januari 2007, 14:41
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
class LabelLayout extends Layout
{
    /**
     * Creates a new instance of LabelLayout.
     * @param p_group The data group for which this ColorAction provides the colors.
     * @param p_vis A reference to the visualization processed by this Action.
     */
    public LabelLayout(String p_group, Visualization p_vis)
    {
        super(p_group);
        m_vis = p_vis;
    }
    
    /**
     * This method is an abstract method from Action which is overloaded here. It 
     * places the labels in the center of the VisualItem in the data group.
     * @param frac The fraction of this Action's duration that has elapsed.
     */
    public void run(double frac)
    {
        Iterator iter = m_vis.items(m_group);
        while (iter.hasNext())
        {
            DecoratorItem item = (DecoratorItem) iter.next();
            VisualItem node = item.getDecoratedItem();
            Rectangle2D bounds = node.getBounds();
            setX(item, null, bounds.getCenterX());
            setY(item, null, bounds.getCenterY());
        }
    }
}