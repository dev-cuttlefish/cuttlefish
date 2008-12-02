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
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.utils.TestGraphs;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.DefaultSettableVertexLocationFunction;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.StaticLayout;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.control.LensMagnificationGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.transform.HyperbolicTransformer;
import edu.uci.ics.jung.visualization.transform.LayoutLensSupport;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.MagnifyTransformer;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.MagnifyShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;

/**
 * Demonstrates the use of <code>HyperbolicTransform</code>
 * and <code>MagnifyTransform</code>
 * applied to either the model (graph layout) or the view
 * (VisualizationViewer)
 * The hyperbolic transform is applied in an elliptical lens
 * that affects that part of the visualization.
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class LensDemo extends JApplet {

    /**
     * the graph
     */
    Graph graph;
    
    Layout graphLayout;
    
    /**
     * a grid shaped graph
     */
    Graph grid;
    
    Layout gridLayout;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;

    /**
     * provides a Hyperbolic lens for the view
     */
    LensSupport hyperbolicViewSupport;
    /**
     * provides a magnification lens for the view
     */
    LensSupport magnifyViewSupport;
    
    /**
     * provides a Hyperbolic lens for the model
     */
    LensSupport hyperbolicLayoutSupport;
    /**
     * provides a magnification lens for the model
     */
    LensSupport magnifyLayoutSupport;
    
    ScalingControl scaler;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoomand hyperbolic features.
     * 
     */
    public LensDemo() {
        
        // create a simple graph for the demo
        graph = TestGraphs.getOneComponentGraph();
        
        final PluggableRenderer pr = new PluggableRenderer();
        graphLayout = new FRLayout(graph);
        ((FRLayout)graphLayout).setMaxIterations(1000);

        Dimension preferredSize = new Dimension(400,400);
        DefaultSettableVertexLocationFunction vlf =
            new DefaultSettableVertexLocationFunction();
        grid = this.generateVertexGrid(vlf, preferredSize, 25);
        gridLayout = new StaticLayout(grid);
        ((AbstractLayout)gridLayout).initialize(preferredSize, vlf);
        
        final VisualizationModel visualizationModel = 
            new DefaultVisualizationModel(graphLayout, preferredSize);
        vv =  new VisualizationViewer(visualizationModel, pr, preferredSize);
        vv.setPickSupport(new ShapePickSupport());
        pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
        PickedState ps = vv.getPickedState();
        pr.setEdgePaintFunction(new PickableEdgePaintFunction(ps, Color.black, Color.red));
        vv.setBackground(Color.white);
        
        final VertexShapeFunction ovals = pr.getVertexShapeFunction();
        final VertexShapeFunction squares = new VertexShapeFunction() {

            public Shape getShape(Vertex v) {
                return new Rectangle2D.Float(-10,-10,20,20);
            }};

        // add a listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        Container content = getContentPane();
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        content.add(gzsp);
        
        /**
         * the regular graph mouse for the normal view
         */
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        
        hyperbolicViewSupport = 
            new ViewLensSupport(vv, new HyperbolicShapeTransformer(vv), 
                    new ModalLensGraphMouse());
        hyperbolicLayoutSupport = 
            new LayoutLensSupport(vv, new HyperbolicTransformer(vv, vv.getLayoutTransformer()),
                    new ModalLensGraphMouse());
        magnifyViewSupport = 
            new ViewLensSupport(vv, new MagnifyShapeTransformer(vv),
                    new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(1.f, 6.f, .2f)));
        magnifyLayoutSupport = 
            new LayoutLensSupport(vv, new MagnifyTransformer(vv, vv.getLayoutTransformer()),
                    new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(1.f, 6.f, .2f)));
        hyperbolicLayoutSupport.getLensTransformer().setEllipse(hyperbolicViewSupport.getLensTransformer().getEllipse());
        magnifyViewSupport.getLensTransformer().setEllipse(hyperbolicLayoutSupport.getLensTransformer().getEllipse());
        magnifyLayoutSupport.getLensTransformer().setEllipse(magnifyViewSupport.getLensTransformer().getEllipse());
        
        final ScalingControl crossoverScaler = new CrossoverScalingControl();
        final ScalingControl layoutScaler = new LayoutScalingControl();
        scaler = crossoverScaler;

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
        
        ButtonGroup radio = new ButtonGroup();
        JRadioButton normal = new JRadioButton("None");
        normal.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    if(hyperbolicViewSupport != null) {
                        hyperbolicViewSupport.deactivate();
                    }
                    if(hyperbolicLayoutSupport != null) {
                        hyperbolicLayoutSupport.deactivate();
                    }
                    if(magnifyViewSupport != null) {
                        magnifyViewSupport.deactivate();
                    }
                    if(magnifyLayoutSupport != null) {
                        magnifyLayoutSupport.deactivate();
                    }
                    scaler = crossoverScaler;
                }
            }
        });

        final JRadioButton hyperView = new JRadioButton("Hyperbolic View");
        hyperView.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                hyperbolicViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
                scaler = layoutScaler;
            }
        });
        final JRadioButton hyperModel = new JRadioButton("Hyperbolic Layout");
        hyperModel.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                hyperbolicLayoutSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
                scaler = layoutScaler;
            }
        });
        final JRadioButton magnifyView = new JRadioButton("Magnified View");
        magnifyView.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                magnifyViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
                scaler = layoutScaler;
            }
        });
        final JRadioButton magnifyModel = new JRadioButton("Magnified Layout");
        magnifyModel.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                magnifyLayoutSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
                scaler = layoutScaler;
            }
        });


        radio.add(normal);
        radio.add(hyperModel);
        radio.add(hyperView);
        radio.add(magnifyModel);
        radio.add(magnifyView);
        normal.setSelected(true);
        
        graphMouse.addItemListener(hyperbolicLayoutSupport.getGraphMouse().getModeListener());
        graphMouse.addItemListener(hyperbolicViewSupport.getGraphMouse().getModeListener());
        graphMouse.addItemListener(magnifyLayoutSupport.getGraphMouse().getModeListener());
        graphMouse.addItemListener(magnifyViewSupport.getGraphMouse().getModeListener());
        
        
        ButtonGroup graphRadio = new ButtonGroup();
        JRadioButton graphButton = new JRadioButton("Graph");
        graphButton.setSelected(true);
        graphButton.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    visualizationModel.setGraphLayout(graphLayout);
                    pr.setVertexShapeFunction(ovals);
                    vv.repaint();
                }
            }});
        JRadioButton gridButton = new JRadioButton("Grid");
        gridButton.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    visualizationModel.setGraphLayout(gridLayout);
                    pr.setVertexShapeFunction(squares);
                    vv.repaint();
                }
            }});
        graphRadio.add(graphButton);
        graphRadio.add(gridButton);
        
        JPanel modePanel = new JPanel(new GridLayout(2,1));
        modePanel.setBorder(BorderFactory.createTitledBorder("Display"));
        modePanel.add(graphButton);
        modePanel.add(gridButton);
        
        JMenuBar menubar = new JMenuBar();
        menubar.add(graphMouse.getModeMenu());
        gzsp.setCorner(menubar);
        

        JPanel controls = new JPanel();
        JPanel zoomControls = new JPanel(new GridLayout(2,1));
        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        JPanel hyperControls = new JPanel(new GridLayout(3,2));
        hyperControls.setBorder(BorderFactory.createTitledBorder("Examiner Lens"));
        zoomControls.add(plus);
        zoomControls.add(minus);
        
        hyperControls.add(normal);
        hyperControls.add(new JLabel());

        hyperControls.add(hyperModel);
        hyperControls.add(magnifyModel);
        
        hyperControls.add(hyperView);
        hyperControls.add(magnifyView);
        
        controls.add(zoomControls);
        controls.add(hyperControls);
        controls.add(modePanel);
        content.add(controls, BorderLayout.SOUTH);
    }

    private Graph generateVertexGrid(DefaultSettableVertexLocationFunction vlf,
            Dimension d, int interval) {
        int count = d.width/interval * d.height/interval;
        Graph graph = new SparseGraph();
        Vertex[] v = new Vertex[count];
        for(int i=0; i<count; i++) {
            int x = interval*i;
            int y = x / d.width * interval;
            x %= d.width;
            
            Point2D location = new Point2D.Float(x, y);
            Vertex vertex = new SparseVertex();
            vlf.setLocation(vertex, location);
            v[i] = graph.addVertex(vertex);
        }
        return graph;
    }
    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new LensDemo());
        f.pack();
        f.setVisible(true);
    }
}
