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
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.utils.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.MultiPickedState;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.subLayout.CircularSubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;

/**
 * Demonstrates the Cluster, CircularCluster, and ClusteringLayout
 * classes. In this demo, vertices are visually clustered as they
 * are selected. The cluster is formed in a circle centered at the
 * location of the first vertex selected.
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class SubLayoutDemo extends JApplet {

    String instructions =
        "<html>Use the mouse to select multiple vertices"+
        "<p>either by dragging a region, or by shift-clicking"+
        "<p>on multiple vertices."+
        "<p>As you select vertices, they become part of a"+
        "<p>cluster, centered at the location of the first"+
        "<p>selected vertex." +
        "<p>You can drag the cluster with the mouse." +
        "<p>Use the 'Picking'/'Transforming' combo-box to switch"+
        "<p>between picking and transforming mode.</html>";
    /**
     * the graph
     */
    Graph graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;

    SubLayoutDecorator clusteringLayout;
    
    PickedState ps;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoomand hyperbolic features.
     * 
     */
    public SubLayoutDemo() {
        
        // create a simple graph for the demo
        graph = TestGraphs.getOneComponentGraph();
        
        PluggableRenderer pr = new PluggableRenderer();
        // ClusteringLayout is a decorator class that delegates
        // to another layout, but can also sepately manage the
        // layout of sub-sets of vertices in circular clusters.
        clusteringLayout = new SubLayoutDecorator(new FRLayout(graph));
       

        Dimension preferredSize = new Dimension(400,400);
        final VisualizationModel visualizationModel = 
            new DefaultVisualizationModel(clusteringLayout, preferredSize);
        vv =  new VisualizationViewer(visualizationModel, pr, preferredSize);
        vv.setPickSupport(new ShapePickSupport());
        pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
        
        vv.setPickedState(new ClusterListener(clusteringLayout));
        ps = vv.getPickedState();
        pr.setEdgePaintFunction(new PickableEdgePaintFunction(ps, Color.black, Color.red));
        vv.setBackground(Color.white);
        
        // add a listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        /**
         * the regular graph mouse for the normal view
         */
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        
        Container content = getContentPane();
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        content.add(gzsp);
        
        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });
        
        JButton help = new JButton("Help");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog((JComponent)e.getSource(), instructions, "Help", JOptionPane.PLAIN_MESSAGE);
            }
        });

        JPanel controls = new JPanel();
        JPanel zoomControls = new JPanel(new GridLayout(2,1));
        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        zoomControls.add(plus);
        zoomControls.add(minus);
        controls.add(zoomControls);
        controls.add(modeBox);
        controls.add(help);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    class ClusterListener extends MultiPickedState {

        SubLayoutDecorator layout;
        Point2D center;
        /* (non-Javadoc)
         * @see edu.uci.ics.jung.visualization.MultiPickedState#pick(edu.uci.ics.jung.graph.ArchetypeVertex, boolean)
         */
        public boolean pick(ArchetypeVertex v, boolean picked) {
            boolean result = super.pick(v, picked);
            if(picked) {
                vertexPicked(v);
            } else {
                vertexUnpicked(v);
            }
            return result;
        }
        public ClusterListener(SubLayoutDecorator layout) {
            this.layout = layout;
        }
        public void vertexPicked(ArchetypeVertex v) {
            if(center == null) {
                center = layout.getLocation(v);
            }
            layout.removeAllSubLayouts();
            SubLayout subLayout = new CircularSubLayout(getPickedVertices(), 20, center);
            layout.addSubLayout(subLayout);
        }

        public void vertexUnpicked(ArchetypeVertex v) {
            layout.removeAllSubLayouts();
            if(getPickedVertices().isEmpty() == false) {
                SubLayout subLayout = new CircularSubLayout(getPickedVertices(), 20, center);
                layout.addSubLayout(subLayout);
            } else {
                center = null;
            }
        }
    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new SubLayoutDemo());
        f.pack();
        f.setVisible(true);
    }
}
