/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package samples.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.GeneralPath;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.utils.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationViewer.Paintable;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.transform.shape.ShapeTransformer;

/**
 * A graph displayed in a main and a satellite view. The satellite
 * view is smaller, always contains the entire graph, and contains
 * a lens shape that shows the boundaries of the visible part of the
 * graph in the main view. Using the mouse, you can pick, translate,
 * layout-scale, view-scale, rotate, shear, and region-select in either
 * view. Using the mouse in either window affects only the main view
 * and the lens shape in the satellite view.
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class SatelliteViewDemo extends JApplet {

    static final String instructions = 
        "<html>"+
        "<b><h2><center>Instructions for Mouse Listeners</center></h2></b>"+
        "<p>There are two modes, Transforming and Picking."+
        "<p>The modes are selected with a combo box."+
        
        "<p><p><b>Transforming Mode:</b>"+
        "<ul>"+
        "<li>Mouse1+drag pans the graph"+
        "<li>Mouse1+Shift+drag rotates the graph"+
        "<li>Mouse1+CTRL(or Command)+drag shears the graph"+
        "</ul>"+
        
        "<b>Picking Mode:</b>"+
        "<ul>"+
        "<li>Mouse1 on a Vertex selects the vertex"+
        "<li>Mouse1 elsewhere unselects all Vertices"+
        "<li>Mouse1+Shift on a Vertex adds/removes Vertex selection"+
        "<li>Mouse1+drag on a Vertex moves all selected Vertices"+
        "<li>Mouse1+drag elsewhere selects Vertices in a region"+
        "<li>Mouse1+Shift+drag adds selection of Vertices in a new region"+
        "<li>Mouse1+CTRL on a Vertex selects the vertex and centers the display on it"+
        "</ul>"+
       "<b>Both Modes:</b>"+
       "<ul>"+
        "<li>Mousewheel scales with a crossover value of 1.0.<p>"+
        "     - scales the graph layout when the combined scale is greater than 1<p>"+
        "     - scales the graph view when the combined scale is less than 1";
    
    JDialog helpDialog;
    
    Paintable viewGrid;
    
    /**
     * create an instance of a simple graph in two views with controls to
     * demo the features.
     * 
     */
    public SatelliteViewDemo() {
        
        // create a simple graph for the demo
        Graph graph = TestGraphs.getOneComponentGraph();
        
        // need separate renderers for each view
        PluggableRenderer pr1 = new PluggableRenderer();
        PluggableRenderer pr2 = new PluggableRenderer();
        
        // the preferred sizes for the two views
        Dimension preferredSize1 = new Dimension(600,600);
        Dimension preferredSize2 = new Dimension(300, 300);
        
        // create one layout for the graph
        FRLayout layout = new FRLayout(graph);
        layout.setMaxIterations(500);
        layout.initialize(new Dimension(600,600));
        
        // create one model that both views will share
        VisualizationModel vm =
            new DefaultVisualizationModel(layout, preferredSize1);
 
        // create 2 views that share the same model
        final VisualizationViewer vv1 = new VisualizationViewer(vm, pr1, preferredSize1);
        final SatelliteVisualizationViewer vv2 = new SatelliteVisualizationViewer(vv1, vm, pr2, preferredSize2);
        
        vv1.setBackground(Color.white);
        vv1.setPickSupport(new ShapePickSupport());
        
        viewGrid = new ViewGrid(vv2, vv1);

        // add default listener for ToolTips
        vv1.setToolTipFunction(new DefaultToolTipFunction());
        vv2.setToolTipFunction(new DefaultToolTipFunction());
        
        ToolTipManager.sharedInstance().setDismissDelay(10000);
        
        Container content = getContentPane();
        Container panel = new JPanel(new BorderLayout());
        Container rightPanel = new JPanel(new GridLayout(2,1));
        
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv1);
        panel.add(gzsp);
        rightPanel.add(new JPanel());
        rightPanel.add(vv2);
        panel.add(rightPanel, BorderLayout.EAST);
        
        helpDialog = new JDialog();
        helpDialog.getContentPane().add(new JLabel(instructions));
        
        // create a GraphMouse for the main view
        // 
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv1.setGraphMouse(graphMouse);
        
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv1, 1.1f, vv1.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv1, 1/1.1f, vv1.getCenter());
            }
        });
        
        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(((DefaultModalGraphMouse)vv2.getGraphMouse()).getModeListener());
        
        JCheckBox gridBox = new JCheckBox("Show Grid");
        gridBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showGrid(vv2, e.getStateChange() == ItemEvent.SELECTED);
			}});
        JButton help = new JButton("Help");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                helpDialog.pack();
                helpDialog.setVisible(true);
            }
        });

        JPanel controls = new JPanel();
        controls.add(plus);
        controls.add(minus);
        controls.add(modeBox);
        controls.add(gridBox);
        controls.add(help);
        content.add(panel);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    protected void showGrid(VisualizationViewer vv, boolean state) {
    		if(state == true) {
    			vv.addPreRenderPaintable(viewGrid);
    		} else {
    			vv.removePreRenderPaintable(viewGrid);
    		}
        vv.repaint();
    }
    
    /**
     * draws a grid on the SatelliteViewer's lens
     * @author Tom Nelson - RABA Technologies
     *
     */
    static class ViewGrid implements Paintable {

        VisualizationViewer master;
        VisualizationViewer vv;
        
        public ViewGrid(VisualizationViewer vv, VisualizationViewer master) {
            this.vv = vv;
            this.master = master;
        }
        public void paint(Graphics g) {
            ShapeTransformer masterViewTransformer = master.getViewTransformer();
            ShapeTransformer masterLayoutTransformer = master.getLayoutTransformer();
            ShapeTransformer vvLayoutTransformer = vv.getLayoutTransformer();

            Rectangle rect = master.getBounds();
            GeneralPath path = new GeneralPath();
            path.moveTo(rect.x, rect.y);
            path.lineTo(rect.width,rect.y);
            path.lineTo(rect.width, rect.height);
            path.lineTo(rect.x, rect.height);
            path.lineTo(rect.x, rect.y);
            
            for(int i=0; i<=rect.width; i+=rect.width/10) {
            		path.moveTo(rect.x+i, rect.y);
            		path.lineTo(rect.x+i, rect.height);
            }
            for(int i=0; i<=rect.height; i+=rect.height/10) {
            		path.moveTo(rect.x, rect.y+i);
            		path.lineTo(rect.width, rect.y+i);
            }
            Shape lens = path;
            lens = masterViewTransformer.inverseTransform(lens);
            lens = masterLayoutTransformer.inverseTransform(lens);
            lens = vvLayoutTransformer.transform(lens);
            Graphics2D g2d = (Graphics2D)g;
            Color old = g.getColor();
            g.setColor(Color.cyan);
            g2d.draw(lens);
            
            path = new GeneralPath();
            path.moveTo((float)rect.getMinX(), (float)rect.getCenterY());
            path.lineTo((float)rect.getMaxX(), (float)rect.getCenterY());
            path.moveTo((float)rect.getCenterX(), (float)rect.getMinY());
            path.lineTo((float)rect.getCenterX(), (float)rect.getMaxY());
            Shape crosshairShape = path;
            crosshairShape = masterViewTransformer.inverseTransform(crosshairShape);
            crosshairShape = masterLayoutTransformer.inverseTransform(crosshairShape);
            crosshairShape = vvLayoutTransformer.transform(crosshairShape);
            g.setColor(Color.black);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(crosshairShape);
            
            g.setColor(old);
        }

        public boolean useTransform() {
            return true;
        }
    }

    
    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new SatelliteViewDemo());
        f.pack();
        f.setVisible(true);
    }
}
