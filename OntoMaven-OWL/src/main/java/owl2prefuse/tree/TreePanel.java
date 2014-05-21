package owl2prefuse.tree;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import owl2prefuse.ExportableGraphic;
import prefuse.Constants;

/**
 * This class represents a tree in a JPanel.
 * <p/>
 * Project OWL2Prefuse <br/>
 * TreePanel.java created 2 januari 2007, 14:05
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 *
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
public class TreePanel extends JPanel implements ActionListener, ExportableGraphic
{
    /**
     * The Display which takes care of displaying the Prefuse tree.
     */
    protected TreeDisplay m_display;
    
    /**
     * The radiobutton for the left-to-right orientation.
     */
    private JRadioButton m_rbLtr;
    
    /**
     * The radiobutton for the top-to-bottom orientation.
     */
    private JRadioButton m_rbTtb;
    
    /**
     * The radiobutton for the right-to-left orientation.
     */
    private JRadioButton m_rbRtl;
    
    /**
     * The radiobutton for the bottom-to-top orientation.
     */
    private JRadioButton m_rbBtt;
    
    /**
     * Creates a new instance of TreePanel
     * @param p_display The Display which takes care of displaying the Prefuse tree.
     * @param p_legend Indicates whether or not a legend should be added to the 
     * panel.
     * @param p_orientationControl Indicates whether or not a widget for controlling the
     * orientation of the tree should be added to the panel.
     */
    public TreePanel(TreeDisplay p_display, boolean p_legend, boolean p_orientationControl)
    {
        super(new BorderLayout());
        m_display = p_display;
        initPanel(p_legend, p_orientationControl);
    }
    
    /**
     * Export the display of the tree, to the given file type.
     * @param p_file The file which is going to contain the export, valid file types 
     * are: "png" and "jpg".
     * @param p_fileType The file type of the image to be created.
     */
    public void export(File p_file, String p_fileType)
    {
        try
        {
            // Export the display to an image.
            OutputStream out = new BufferedOutputStream(new FileOutputStream(p_file));
            m_display.saveImage(out, p_fileType, 1.0);
            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize this panel.
     * @param p_legend Indicates whether or not a legend should be added to the 
     * panel.
     * @param p_orientationControl Indicates whether or not a widget for controlling the
     * orientation of the tree should be added to the panel.
     */
    protected void initPanel(boolean p_legend, boolean p_orientationControl)
    {
        setBackground(m_display.getBackground());
        setForeground(m_display.getForeground());
        
        add(m_display, BorderLayout.CENTER);
        add(createBox(), BorderLayout.SOUTH);
        if (p_legend || p_orientationControl) add(getSideBar(p_legend, p_orientationControl), BorderLayout.EAST);
    }
    
    /**
     * Initialize the sidebar of the tree panel.
     * @param p_legend Indicates whether or not a legend should be added to the 
     * panel.
     * @param p_orientationControl Indicates whether or not a widget for controlling the
     * orientation of the tree should be added to the panel.
     * @return The side bar.
     */
    private JPanel getSideBar(boolean p_legend, boolean p_orientationControl)
    {
        // Create the panel.
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        
        int y = 0;
        
        constraints.gridx = 0;
        constraints.gridy = y++;
        if (!p_orientationControl) constraints.weighty = 1;
        if (p_legend) panel.add(getLegend(), constraints);
        if (p_orientationControl)
        {
            constraints.gridy = 1;
            constraints.weighty = y++;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            panel.add(getOrientationWidgets(), constraints);
        }
        
        // Set the background of the panel.
        panel.setBackground(m_display.getBackground());
        
        return panel;
    }
    
    /**
     * Create a legend pane and return it.
     * @return The legend in an editor pane.
     */
    public JEditorPane getLegend()
    {
        String content = "<html><body>" +
                "<table>" +
                "<tr><td bgcolor=\"" + Integer.toHexString(owl2prefuse.Constants.NODE_COLOR_CLASS & 0x00ffffff) + "\" width=\"20px\"></td><td>OWL class</td></tr>" +
                "<tr><td bgcolor=\"" + Integer.toHexString(owl2prefuse.Constants.NODE_COLOR_INDIVIDUAL & 0x00ffffff) + "\"></td><td>OWL individual</td></tr>" +
                "<tr><td bgcolor=\"" + Integer.toHexString(owl2prefuse.Constants.NODE_COLOR_SELECTED & 0x00ffffff) + "\"></td><td>Selected node</td></tr>" +
                "<tr><td bgcolor=\"" + Integer.toHexString(owl2prefuse.Constants.NODE_COLOR_HIGHLIGHTED & 0x00ffffff) + "\"></td><td>Path to selected node</td></tr>" +
                "<tr><td bgcolor=\"" + Integer.toHexString(owl2prefuse.Constants.NODE_COLOR_SEARCH & 0x00ffffff) + "\"></td><td>Node in search result set</td></tr>" +
                "<tr><td bgcolor=\"" + Integer.toHexString(owl2prefuse.Constants.NODE_DEFAULT_COLOR & 0x00ffffff) + "\"></td><td>Default color</td></tr>" +
                "</body></html>";
    
        JEditorPane legend = new JEditorPane("text/html", content);
        legend.setEditable(false);
        legend.setBorder(new TitledBorder("Legend"));
        
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Legend"));
        panel.add(legend);
        
        return legend;
    }
    
    /**
     * Get the radio buttons which enable the user to change the orientation of
     * the graph.
     * @return The orientation radio buttons in a JPanel.
     */
    private JPanel getOrientationWidgets()
    {
        // Create the panel.
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        
        // Create a label.
        JLabel label = new JLabel("Orientation: ");
        
        // Create a button group.
        ButtonGroup btnGroup = new ButtonGroup();
        
        // Create the radiobuttons.
        m_rbLtr = new JRadioButton("left-to-right", true);
        m_rbLtr.setBackground(m_display.getBackground());
        m_rbLtr.addActionListener(this);
        m_rbTtb = new JRadioButton("top-to-bottom");
        m_rbTtb.setBackground(m_display.getBackground());
        m_rbTtb.addActionListener(this);
        m_rbRtl = new JRadioButton("right-to-left");
        m_rbRtl.setBackground(m_display.getBackground());
        m_rbRtl.addActionListener(this);
        m_rbBtt = new JRadioButton("bottom-to-top");
        m_rbBtt.setBackground(m_display.getBackground());
        m_rbBtt.addActionListener(this);
        
        // Add the radiobuttons to the button group.
        btnGroup.add(m_rbLtr);
        btnGroup.add(m_rbTtb);
        btnGroup.add(m_rbRtl);
        btnGroup.add(m_rbBtt);
        
        // Add the label and the radiobuttons to the panel.
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(label, constraints);
        constraints.gridy = 1;
        panel.add(m_rbLtr, constraints);
        constraints.gridy = 2;
        panel.add(m_rbTtb, constraints);
        constraints.gridy = 3;
        panel.add(m_rbRtl, constraints);
        constraints.gridy = 4;
        constraints.weighty = 1;
        panel.add(m_rbBtt, constraints);
        
        // Set the background of the panel.
        panel.setBackground(m_display.getBackground());
        
        // Add a titled border to the panel.
        panel.setBorder(new TitledBorder("Orientation control"));
        
        return panel;
    }
    
    /**
     * Create the title and search panel widgets which are displayed at the bottom
     * of the graph panel.
     * @return The title and searchpanel widgets in a Box.
     */
    private Box createBox()
    {
        // Create a box to display the node title label and the search box.
        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalStrut(10));
        box.add(m_display.getTitleLabel());
        box.add(Box.createHorizontalGlue());
        box.add(m_display.getSearchPanel());
        box.add(Box.createHorizontalStrut(3));
        box.setBackground(m_display.getBackground());
        box.setForeground(m_display.getForeground());
        
        return box;
    }
    
    /**
     * This method is implemented because this class implements the ActionListener
     * interface. It takes care of the event that user click on one of the orientation
     * radio buttons. It changes the orientation appropriately.
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e)
    {
        int orientation = 0;
        if (e.getSource() == m_rbLtr) orientation = Constants.ORIENT_LEFT_RIGHT;
        else if (e.getSource() == m_rbTtb) orientation = Constants.ORIENT_TOP_BOTTOM;
        else if (e.getSource() == m_rbRtl) orientation = Constants.ORIENT_RIGHT_LEFT;
        else if (e.getSource() == m_rbBtt) orientation = Constants.ORIENT_BOTTOM_TOP;
        
        m_display.setOrientation(orientation);
        m_display.getVisualization().cancel("orient");
        m_display.getVisualization().run("treeLayout");
        m_display.getVisualization().run("orient");
    }
}