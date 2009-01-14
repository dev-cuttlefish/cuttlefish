/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package samples.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.utils.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.MultiPickedState;
import edu.uci.ics.jung.visualization.PickSupport;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

/**
 * Demonstrates a single graph with 2 layouts in 2 views.
 * They share picking, transforms, and a pluggable renderer
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class TwoModelDemo extends JApplet {

     /**
     * the graph
     */
    Graph graph;

    /**
     * the visual components and renderers for the graph
     */
    VisualizationViewer vv1;
    VisualizationViewer vv2;
    
    /**
     * the normal transformer
     */
    MutableTransformer layoutTransformer;
    
    Dimension preferredSize = new Dimension(300,300);
    
    /**
     * create an instance of a simple graph in two views with controls to
     * demo the zoom features.
     * 
     */
    public TwoModelDemo() {
        
        // create a simple graph for the demo
        // both models will share one graph
        graph = TestGraphs.getOneComponentGraph();
        
        // both views will share the same renderer
        PluggableRenderer pr = new PluggableRenderer();
        
        // create two layouts for the one graph, one layout for each model
        Layout layout1 = new FRLayout(graph);
        Layout layout2 = new ISOMLayout(graph);

        // create the two models, each with a different layout
        VisualizationModel vm1 =
            new DefaultVisualizationModel(layout1, preferredSize);
        VisualizationModel vm2 = 
            new DefaultVisualizationModel(layout2, preferredSize);


        // create the two views, one for each model
        // they share the same renderer
        vv1 = new VisualizationViewer(vm1, pr, preferredSize);
        vv2 = new VisualizationViewer(vm2, pr, preferredSize);
        
        // share the model transformer between the two models
        layoutTransformer = vv1.getLayoutTransformer();
        vv2.setLayoutTransformer(layoutTransformer);
        
        // share the view transformer between the two models
        vv2.setViewTransformer(vv1.getViewTransformer());

        vv1.setBackground(Color.white);
        vv2.setBackground(Color.white);
        
        // create and set a separate pickSupport for each view
        PickSupport pickSupport1 = new ShapePickSupport();
        vv1.setPickSupport(pickSupport1);
        PickSupport pickSupport2 = new ShapePickSupport();
        vv2.setPickSupport(pickSupport2);

        // share one PickedState between the two views
        PickedState ps = new MultiPickedState();
        vv1.setPickedState(ps);
        vv2.setPickedState(ps);
        
        // set an edge paint function that will show picking for edges
        pr.setEdgePaintFunction(new PickableEdgePaintFunction(ps, Color.black, Color.red));

        // add default listeners for ToolTips
        vv1.setToolTipFunction(new DefaultToolTipFunction());
        vv2.setToolTipFunction(new DefaultToolTipFunction());
        
        Container content = getContentPane();
        JPanel panel = new JPanel(new GridLayout(1,0));
        panel.add(new GraphZoomScrollPane(vv1));
        panel.add(new GraphZoomScrollPane(vv2));

        content.add(panel);
        
        // create a GraphMouse for each view
        final DefaultModalGraphMouse gm1 = new DefaultModalGraphMouse();

        DefaultModalGraphMouse gm2 = new DefaultModalGraphMouse();

        vv1.setGraphMouse(gm1);
        vv2.setGraphMouse(gm2);

        // create zoom buttons for scaling the transformer that is
        // shared between the two models.
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
        
        JPanel zoomPanel = new JPanel(new GridLayout(1,2));
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        
        JPanel modePanel = new JPanel();
        modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        gm1.getModeComboBox().addItemListener(gm2.getModeListener());
        modePanel.add(gm1.getModeComboBox());

        JPanel controls = new JPanel();
        zoomPanel.add(plus);
        zoomPanel.add(minus);
        controls.add(zoomPanel);
        controls.add(modePanel);
        content.add(controls, BorderLayout.SOUTH);
    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new TwoModelDemo());
        f.pack();
        f.setVisible(true);
    }
}
