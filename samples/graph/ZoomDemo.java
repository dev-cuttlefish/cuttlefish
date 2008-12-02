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
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.utils.TestGraphs;
import edu.uci.ics.jung.visualization.BirdsEyeVisualizationViewer;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ViewScalingControl;

/**
 * Demonstrates the use of: <p>
 * <ul>BirdsEyeGraphDraw
 * <ul>BirdsEyeVisualizationViewer
 * <li>Lens
 * </ul>
 * This demo also shows ToolTips on graph vertices.
 * 
 * The birds eye affects the view transform only.
 * You can still affect the layout transform using the
 * mouse.
 * 
 * @deprecated See the SatelliteViewDemo for a similar demo with more features
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class ZoomDemo {

    /**
     * the graph
     */
    Graph graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;
    
    JDialog dialog;

    /**
     * create an instance of a simple graph with controls to
     * demo the zoom features.
     * 
     */
    public ZoomDemo() {
        
        // create a simple graph for the demo
        graph = TestGraphs.getOneComponentGraph();

        final Layout layout = new ISOMLayout(graph);
        PluggableRenderer pr = new PluggableRenderer();
        
        vv = new VisualizationViewer(layout, pr);

        // add my listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
      //  vv.setGraphMouse(new KSGraphMouse(vv));
        vv.setPickSupport(new ShapePickSupport());
        
        // create a frome to hold the graph
        final JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(vv);
        content.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dialog = new JDialog(frame);
       // Container 
        content = dialog.getContentPane();
        
        // create the BirdsEyeView for zoom/pan
        final BirdsEyeVisualizationViewer bird =
            new BirdsEyeVisualizationViewer(vv, 0.25f, 0.25f);
        
        JButton reset = new JButton("No Zoom");
        // 'reset' unzooms the graph via the Lens
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bird.resetLens();
            }
        });
        final ScalingControl scaler = new ViewScalingControl();
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
                String zoomHelp = "<html><center>Drag the rectangle to pan<p>"+
                "Drag one side of the rectangle to zoom</center></html>";
                JOptionPane.showMessageDialog(dialog, zoomHelp);
            }
        });
        JPanel controls = new JPanel(new GridLayout(2,2));
        controls.add(plus);
        controls.add(minus);
        controls.add(reset);
        controls.add(help);
        content.add(bird);
        content.add(controls, BorderLayout.SOUTH);
        
        JButton zoomer = new JButton("Show Zoom Window");
        // create a dialog to show a birds-eye-view style
        // zoom and pan tool.
        zoomer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.pack();
                dialog.setLocation((int) (frame.getLocationOnScreen().getX()+frame.getWidth()), 
                        (int) frame.getLocationOnScreen().getY());
                dialog.show();
                bird.initLens();
            }
        });

        JPanel p = new JPanel();
        p.add(zoomer);

        frame.getContentPane().add(p, BorderLayout.SOUTH);
        frame.setSize(600, 600);
        frame.show();
    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        new ZoomDemo();
    }
}

