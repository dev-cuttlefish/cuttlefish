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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.PersistentLayout;
import edu.uci.ics.jung.visualization.PersistentLayoutImpl;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

/**
 * Demonstrates the use of <code>PersistentLayout</code>
 * and <code>PersistentLayoutImpl</code>.
 * This demo also shows ToolTips on graph vertices.
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class PersistentLayoutDemo {

    /**
     * the graph
     */
    Graph graph;

    /**
     * the name of the file where the layout is saved
     */
    String fileName;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;

    /**
     * create an instance of a simple graph with controls to
     * demo the persistence and zoom features.
     * 
     * @param fileName where to save/restore the graph positions
     */
    public PersistentLayoutDemo(final String fileName) {
        this.fileName = fileName;
        
        // create a simple graph for the demo
        graph = new DirectedSparseGraph();
        Vertex[] v = createVertices(10);
        createEdges(v);
        final PersistentLayout layout = 
            new PersistentLayoutImpl(new FRLayout(graph));

        // the PersistentLayout delegates to another GraphLayout until you
        // perform a 'restore' 
        PluggableRenderer pr = new PluggableRenderer();
        
        vv = new VisualizationViewer(layout, pr);
        
        // add my listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        vv.setPickSupport(new ShapePickSupport());
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        vv.setGraphMouse(gm);
        
        // create a frome to hold the graph
        final JFrame frame = new JFrame();
        frame.getContentPane().add(new GraphZoomScrollPane(vv));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create a control panel and buttons for demo
        // functions
        JPanel p = new JPanel();
        
        JButton persist = new JButton("Save Layout");
        // saves the graph vertex positions to a file
        persist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PersistentLayout pl = (PersistentLayout) vv.getGraphLayout();
                try {
                    pl.persist(fileName);
                } catch (IOException e1) {
                    System.err.println("got "+e1);
                }
            }
        });
        p.add(persist);

        JButton restore = new JButton("Restore Layout");
        // restores the graph vertex positions from a file
        // if new vertices were added since the last 'persist',
        // they will be placed at random locations
        restore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PersistentLayout pl = (PersistentLayout) vv.getGraphLayout();
                try {
                    pl.restore(fileName);
                    vv.repaint();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        p.add(restore);
        p.add(gm.getModeComboBox());

        frame.getContentPane().add(p, BorderLayout.SOUTH);
        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    /**
     * create some vertices
     * @param count how many to create
     * @return the Vertices in an array
     */
    private Vertex[] createVertices(int count) {
        Vertex[] v = new Vertex[count];
        for (int i = 0; i < count; i++) {
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
        graph.addEdge(new DirectedSparseEdge(v[2], v[3]));
        graph.addEdge(new DirectedSparseEdge(v[0], v[4]));
        graph.addEdge(new DirectedSparseEdge(v[4], v[5]));
        graph.addEdge(new DirectedSparseEdge(v[3], v[9]));
        graph.addEdge(new DirectedSparseEdge(v[6], v[2]));
        graph.addEdge(new DirectedSparseEdge(v[7], v[1]));
        graph.addEdge(new DirectedSparseEdge(v[8], v[2]));
        graph.addEdge(new DirectedSparseEdge(v[3], v[8]));
        graph.addEdge(new DirectedSparseEdge(v[6], v[7]));
        graph.addEdge(new DirectedSparseEdge(v[7], v[5]));
        graph.addEdge(new DirectedSparseEdge(v[0], v[9]));
    }

    /**
     * a driver for this demo
     * @param args should hold the filename for the persistence demo
     */
    public static void main(String[] args) 
    {
        String filename;
        if (args.length >= 1)
            filename = args[0];
        else
            filename = "PersistentLayoutDemo.out";
        new PersistentLayoutDemo(filename);
    }
}

