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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.LayoutMutable;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.SpringLayout.LengthFunction;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

/*
 * Created on May 10, 2004
 */

/**
 * Thanks to Brad Allen for an original inspiration for this.
 * 
 * @author danyelf
 */
public class AddNodeDemo extends javax.swing.JApplet {

    private Graph g = null;

    private VisualizationViewer vv = null;

    private LayoutMutable layout = null;

    private StringLabeller labler = null;

    Timer timer;

    protected JButton switchLayout;

    public static final LengthFunction UNITLENGTHFUNCTION = new SpringLayout.UnitLengthFunction(
            100);

    public void init() {

        //create a graph
        g = new DirectedSparseGraph();

        //create a graphdraw
        layout = new FRLayout(g);
        
        vv = new VisualizationViewer(layout, new PluggableRenderer());

        JRootPane rp = this.getRootPane();
        rp.putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(java.awt.Color.lightGray);
        getContentPane().setFont(new Font("Serif", Font.PLAIN, 12));

        //define my layout for dynamic graphing
        // info can be found at
        // https://sourceforge.net/forum/forum.php?thread_id=1021284&forum_id=252062

        //set a visualization viewer
        
        vv.getModel().setRelaxerThreadSleepTime(500);
        vv.setPickSupport(new ShapePickSupport());
        vv.setGraphMouse(new DefaultModalGraphMouse());

        getContentPane().add(vv);
        switchLayout = new JButton("Switch to SpringLayout");
        switchLayout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (switchLayout.getText().indexOf("Spring") > 0) {
                    switchLayout.setText("Switch to FRLayout");
                    layout = new SpringLayout(g, UNITLENGTHFUNCTION);
                    vv.getModel().setGraphLayout(layout);
                } else {
                    switchLayout.setText("Switch to SpringLayout");
                    layout = new FRLayout(g);
                    vv.getModel().setGraphLayout(layout);
                }
                if (!vv.isVisRunnerRunning())
                    vv.init();
            }
        });

        getContentPane().add(switchLayout, BorderLayout.SOUTH);

        labler = StringLabeller.getLabeller(g);

        timer = new Timer();

    }

    public void start() {
        validate();
        //set timer so applet will change
        timer.schedule(new RemindTask(), 1000, 1000); //subsequent rate
        vv.repaint();
    }

    Vertex v_prev = null;

    public void process() {

        System.out.println("-[----------------------------");
        int label_number = 0;

        boolean redraw = false;

        //run in loop populating data on graph as it goes into the database
        //        while (t != -1) {
        redraw = true;
        try {

            int vertices = 0;
            if (vertices < 100) {
                redraw = true;
                vertices++;

                //pull out last record processed and label
                label_number = (int) (Math.random() * 10000);
                long eid = (int) (Math.random() * 10000);

                System.out.println("P: adding a node " + label_number);

                //add a vertex
                Vertex v1 = g.addVertex(new SparseVertex());

                // wire it to some edges
                if (v_prev != null) {
                    g.addEdge(new DirectedSparseEdge(v_prev, v1));
                    // let's connect to a random vertex, too!
                    Indexer i = Indexer.getAndUpdateIndexer(g);
                    int rand = (int) (Math.random() * g.numVertices());
                    Vertex v_rand = (Vertex) i.getVertex(rand);
                    g.addEdge(new DirectedSparseEdge(v1, v_rand));
                }

                v_prev = v1;

                //set the labeler
                labler.setLabel(v1, "" + eid);

            }

            if (redraw) {
                System.out.println("P: Updating");
                //update the layout
                // see
                // https://sourceforge.net/forum/forum.php?thread_id=1021284&forum_id=252062
                
                layout.update();
                if (!vv.isVisRunnerRunning())
                    vv.init();
                vv.repaint();
            }

        } catch (Exception e) {
            System.out.println(e);

        }
        System.out.println("------------end process------------");
    }

    class RemindTask extends TimerTask {

        public void run() {
            process();

        }
    }
    public static void main(String[] args) {
    	AddNodeDemo and = new AddNodeDemo();
    	JFrame frame = new JFrame();
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add(and);

    	and.init();
    	and.start();
    	frame.pack();
    	frame.setVisible(true);
    }

}