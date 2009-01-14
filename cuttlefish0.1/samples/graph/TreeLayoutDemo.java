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
import java.awt.event.InputEvent;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.PickableVertexPaintFunction;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseTree;
import edu.uci.ics.jung.visualization.DefaultGraphLabelRenderer;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.TreeLayout;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ViewScalingControl;

/**
 * Demonstrates the use of images to represent graph vertices.
 * The images are supplied via the VertexShapeFunction so that
 * both the image and its shape can be utilized.
 * 
 * The images used in this demo (courtesy of slashdot.org) are
 * rectangular but with a transparent background. When vertices
 * are represented by these images, it looks better if the actual
 * shape of the opaque part of the image is computed so that the
 * edge arrowheads follow the visual shape of the image. This demo
 * uses the FourPassImageShaper class to compute the Shape from
 * an image with transparent background.
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class TreeLayoutDemo extends JApplet {

    /**
     * the graph
     */
    SparseTree graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;
    
    Vertex root;

    public TreeLayoutDemo() {
        
        root = new DirectedSparseVertex();
        // create a simple graph for the demo
        graph = new SparseTree(root);
        Vertex[] vertices = createVertices(root, 11);
        
        createEdges(vertices);
        
        final PluggableRenderer pr = new PluggableRenderer();

        pr.setVertexPaintFunction(new PickableVertexPaintFunction(pr, Color.black, Color.white, Color.yellow));
        pr.setEdgePaintFunction(new PickableEdgePaintFunction(pr, Color.black, Color.cyan));
        pr.setGraphLabelRenderer(new DefaultGraphLabelRenderer(Color.cyan, Color.cyan));
        
        pr.setVertexShapeFunction(new EllipseVertexShapeFunction());
        
        Layout layout = new TreeLayout(graph);

        vv =  new VisualizationViewer(layout, pr, new Dimension(400,400));
        vv.setPickSupport(new ShapePickSupport());
        pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
        vv.setBackground(Color.white);
        
        // add a listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        
        final PluggableGraphMouse graphMouse = new PluggableGraphMouse();
        graphMouse.add(new PickingGraphMousePlugin());
        graphMouse.add(new ScalingGraphMousePlugin(new ViewScalingControl(), InputEvent.CTRL_MASK));
        graphMouse.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0));

        vv.setGraphMouse(graphMouse);
        
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

        JPanel scaleGrid = new JPanel(new GridLayout(1,0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

        JPanel controls = new JPanel();
        scaleGrid.add(plus);
        scaleGrid.add(minus);
        controls.add(scaleGrid);

        content.add(controls, BorderLayout.SOUTH);
    }
    
    /**
     * create some vertices
     * @param count how many to create
     * @return the Vertices in an array
     */
    private Vertex[] createVertices(Vertex root, int count) {
        Vertex[] v = new Vertex[count];
        v[0] = root;
        for (int i = 1; i < count; i++) {
            v[i] = graph.addVertex(new DirectedSparseVertex());
        }
        return v;
    }

    /**
     * create edges for this demo graph
     * @param v an array of Vertices to connect
     */
    void createEdges(Vertex[] v) {
        graph.addEdge(new DirectedSparseEdge(v[0], v[1]));
        graph.addEdge(new DirectedSparseEdge(v[0], v[2]));
        graph.addEdge(new DirectedSparseEdge(v[2], v[3]));
        graph.addEdge(new DirectedSparseEdge(v[1], v[4]));
        graph.addEdge(new DirectedSparseEdge(v[2], v[5]));
        graph.addEdge(new DirectedSparseEdge(v[4], v[6]));
        graph.addEdge(new DirectedSparseEdge(v[4], v[7]));
        graph.addEdge(new DirectedSparseEdge(v[3], v[8]));
        graph.addEdge(new DirectedSparseEdge(v[6], v[9]));
        graph.addEdge(new DirectedSparseEdge(v[4], v[10]));
    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        content.add(new TreeLayoutDemo());
        frame.pack();
        frame.setVisible(true);
    }
}
