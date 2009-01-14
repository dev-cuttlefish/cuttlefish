/*
 * Created on Jan 7, 2004
 */
package samples.preview_new_graphdraw.test;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFrame;

import samples.preview_new_graphdraw.event.ClickEvent;
import samples.preview_new_graphdraw.event.ClickListener;
import samples.preview_new_graphdraw.impl.GraphLayoutPanel;
import samples.preview_new_graphdraw.iter.IterableLayout;
import samples.preview_new_graphdraw.iter.LocalGraphDraw;
import samples.preview_new_graphdraw.iterablelayouts.SpringLayout;
import samples.preview_new_graphdraw.staticlayouts.CircleLayout;
import samples.preview_new_graphdraw.transform.CrookedLineTransformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.utils.TestGraphs;

/**
 * @author danyelf
 */
public class AnimatedGraphDraw {

    protected GraphLayoutPanel jgp;

    /**
     * @param g
     * @param startup
     * @param layout
     * @param d
     */
    public static void main(String[] s) throws InterruptedException {
        CircleLayout cl = new CircleLayout();
        //        KKLayout il = new KKLayout();
        IterableLayout il = new SpringLayout();
        Graph g = TestGraphs.getDemoGraph();
        Dimension d = new Dimension(200, 200);
        CircleRenderer cr = new CircleRenderer();
        LocalGraphDraw lgd = new LocalGraphDraw(g, cl, il, cr, cr, d, true);
        lgd.start();
        lgd.addToPipeline(new CrookedLineTransformer());
        //		lgd.setMouseDrag(true);
        lgd.getPanel().addClickListener(new ClickListener() {

            public void edgeClicked(ClickEvent ece) {
            }

            public void vertexClicked(ClickEvent vce) {
                Vertex v = (Vertex) vce.getGraphObject();
                System.out.println("Clicked " + v);
            }
        });
        JFrame jf = new JFrame();
        jf.getContentPane().add(lgd.getPanel());
        jf.pack();
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            Thread.sleep(1000);
            System.out.println("Now adding new vertices and edges");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Indexer id = Indexer.getIndexer(g);
        Set toRemove = new HashSet();
        for (int i = 0; i < 20; i += 2) {
            toRemove.add(id.getVertex(i));
        }
        for (Iterator iter = toRemove.iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            g.removeVertex(v);
            lgd.updateGraphTo(g);
            Thread.sleep(500);
        }

        Vertex[] v = new Vertex[5];
        // let's throw in a clique, too
        for (int i = 0; i < 5; i++) {
            v[i] = g.addVertex(new SparseVertex());
            for (int j = 0; j <= i; j++) {
                g.addEdge(new UndirectedSparseEdge(v[i], v[j]));
                lgd.updateGraphTo(g);
                Thread.sleep(1000);
            }
        }
    }
}
