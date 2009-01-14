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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.PickableVertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexIconAndShapeFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.visualization.DefaultGraphLabelRenderer;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;

/**
 * Demonstrates the use of <code>GraphZoomScrollPane</code>.
 * This class shows off the <code>VisualizationViewer</code> zooming
 * and panning capabilities, using horizontal and
 * vertical scrollbars.
 *
 * <p>This demo also shows ToolTips on graph vertices.</p>
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class UnicodeLabelDemo {

    /**
     * the graph
     */
    Graph graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;
    
    boolean showLabels;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoom features.
     * 
     */
    public UnicodeLabelDemo() {
        
        // create a simple graph for the demo
        graph = new DirectedSparseGraph();
        Vertex[] v = createVertices(10);
        createEdges(v);
        
        PluggableRenderer pr = new PluggableRenderer();
        pr.setVertexStringer(new UnicodeVertexStringer(v));
        pr.setVertexPaintFunction(new PickableVertexPaintFunction(pr, Color.lightGray, Color.white,  Color.yellow));
        pr.setGraphLabelRenderer(new DefaultGraphLabelRenderer(Color.cyan, Color.cyan));
        VertexIconAndShapeFunction dvisf =
            new VertexIconAndShapeFunction(new EllipseVertexShapeFunction());
        pr.setVertexShapeFunction(dvisf);
        pr.setVertexIconFunction(dvisf);
        loadImages(v, dvisf.getIconMap());
        vv =  new VisualizationViewer(new FRLayout(graph), pr);
        vv.setPickSupport(new ShapePickSupport());
        pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
        vv.setBackground(Color.white);

        // add my listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        // create a frome to hold the graph
        final JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        final ModalGraphMouse gm = new DefaultModalGraphMouse();
        vv.setGraphMouse(gm);
        
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

        JCheckBox lo = new JCheckBox("Show Labels");
        lo.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                showLabels = e.getStateChange() == ItemEvent.SELECTED;
                vv.repaint();
            }
        });
        lo.setSelected(true);
        
        JPanel controls = new JPanel();
        controls.add(plus);
        controls.add(minus);
        controls.add(lo);
        controls.add(((DefaultModalGraphMouse) gm).getModeComboBox());
        content.add(controls, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
    
    
    class UnicodeVertexStringer implements VertexStringer {

        Map map = new HashMap();
        Map iconMap = new HashMap();
        String[] labels = {
                "\u0057\u0065\u006C\u0063\u006F\u006D\u0065\u0020\u0074\u006F\u0020Jung\u0021",               
                "\u6B22\u8FCE\u4F7F\u7528\u0020\u0020Jung\u0021",
                "\u0414\u043E\u0431\u0440\u043E\u0020\u043F\u043E\u0436\u0430\u043B\u043E\u0432\u0430\u0422\u044A\u0020\u0432\u0020Jung\u0021",
                "\u0042\u0069\u0065\u006E\u0076\u0065\u006E\u0075\u0065\u0020\u0061\u0075\u0020Jung\u0021",
                "\u0057\u0069\u006C\u006B\u006F\u006D\u006D\u0065\u006E\u0020\u007A\u0075\u0020Jung\u0021",
                "Jung\u3078\u3087\u3045\u3053\u305D\u0021",
//                "\u0053\u00E9\u006A\u0061\u0020\u0042\u0065\u006D\u0076\u0069\u006E\u0064\u006F\u0020Jung\u0021",
               "\u0042\u0069\u0065\u006E\u0076\u0065\u006E\u0069\u0064\u0061\u0020\u0061\u0020Jung\u0021"
        };
        
        public UnicodeVertexStringer(Vertex[] vertices) {
            for(int i=0; i<vertices.length; i++) {
                map.put(vertices[i], labels[i%labels.length]);
            }
        }
        
        /* (non-Javadoc)
         * @see edu.uci.ics.jung.graph.decorators.VertexStringer#getLabel(edu.uci.ics.jung.graph.Vertex)
         */
        public String getLabel(ArchetypeVertex v) {
            if(showLabels) {
                return (String)map.get(v);
            } else {
                return "";
            }
        }
        
        public Icon getIcon(Vertex v) {
            return (Icon)iconMap.get(v);
        }
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
        graph.addEdge(new DirectedSparseEdge(v[0], v[3]));
        graph.addEdge(new DirectedSparseEdge(v[0], v[4]));
        graph.addEdge(new DirectedSparseEdge(v[4], v[5]));
        graph.addEdge(new DirectedSparseEdge(v[3], v[5]));
        graph.addEdge(new DirectedSparseEdge(v[1], v[2]));
        graph.addEdge(new DirectedSparseEdge(v[1], v[4]));
        graph.addEdge(new DirectedSparseEdge(v[8], v[2]));
        graph.addEdge(new DirectedSparseEdge(v[3], v[8]));
        graph.addEdge(new DirectedSparseEdge(v[6], v[7]));
        graph.addEdge(new DirectedSparseEdge(v[7], v[5]));
        graph.addEdge(new DirectedSparseEdge(v[0], v[9]));
        graph.addEdge(new DirectedSparseEdge(v[9], v[8]));
        graph.addEdge(new DirectedSparseEdge(v[7], v[6]));
        graph.addEdge(new DirectedSparseEdge(v[6], v[5]));
        graph.addEdge(new DirectedSparseEdge(v[4], v[2]));
        graph.addEdge(new DirectedSparseEdge(v[5], v[4]));
    }

    /**
     * A nested class to demo ToolTips
     */
    
    protected void loadImages(Vertex[] vertices, Map imageMap) {
        
        ImageIcon[] icons = null;
        try {
            icons = new ImageIcon[] {
                    new ImageIcon(getClass().getResource("/united-states.gif")),
                    new ImageIcon(getClass().getResource("/china.gif")),
                    new ImageIcon(getClass().getResource("/russia.gif")),
                    new ImageIcon(getClass().getResource("/france.gif")),
                    new ImageIcon(getClass().getResource("/germany.gif")),
                    new ImageIcon(getClass().getResource("/japan.gif")),
                    new ImageIcon(getClass().getResource("/spain.gif"))
            };
        } catch(Exception ex) {
            System.err.println("You need flags.jar in your classpath to see the flag icons.");
        }
        for(int i=0; icons != null && i<vertices.length; i++) {
            imageMap.put(vertices[i],icons[i%icons.length]);
        }
    }
    /**
     * a driver for this demo
     */
    public static void main(String[] args) 
    {
        new UnicodeLabelDemo();
    }
}
