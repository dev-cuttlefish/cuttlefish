/*
 * Created on Dec 8, 2003
 */
package samples.preview_new_graphdraw.impl.crooked;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

import samples.preview_new_graphdraw.event.ClickEvent;
import samples.preview_new_graphdraw.event.ClickListener;
import samples.preview_new_graphdraw.impl.GraphLayoutPanel;
import samples.preview_new_graphdraw.impl.GraphLayoutPanelMouseListener;
import samples.preview_new_graphdraw.iter.LocalGraphDraw;
import samples.preview_new_graphdraw.staticlayouts.RandomLayout;
import edu.uci.ics.jung.graph.Element;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author danyelf
 */
public class CrookedRun extends Applet {

    static JLabel msgBox;

    public void start() {
        begin();
    }

    /**
     * @return
     */
    private Component begin() {
        Graph g = new UndirectedSparseGraph();
        Vertex v1, v2, v3;
        g.addVertex(v1 = new SparseVertex());
        g.addVertex(v2 = new SparseVertex());
        g.addVertex(v3 = new SparseVertex());
        g.addEdge(new UndirectedSparseEdge(v1, v2));
        g.addEdge(new UndirectedSparseEdge(v1, v3));
        g.addEdge(new UndirectedSparseEdge(v3, v2));

        //		TestGraphs.getOneComponentGraph();
        //		Graph g = TestGraphs.getDemoGraph();

        GraphLayoutPanelMouseListener.VERTEX_CLICK_THRESHOLD = 1000;

        CrookedRenderer vr = new CrookedRenderer();
        Dimension d = new Dimension(400, 400);

        LocalGraphDraw lgd = new LocalGraphDraw(g, new RandomLayout(),
                new CrookedSpringLayout(), vr, vr, d, true);

        lgd.getPanel().setClickPolicy(GraphLayoutPanel.EDGE_AND_VERTEX_POLICY);
        Selecter selecter;
        lgd.getPanel().addClickListener(selecter = new Selecter());
        lgd.getPanel().addMouseListener(selecter);

        lgd.start();

        msgBox = new JLabel(" ");

        this.setLayout(new BorderLayout());
        add(lgd.getPanel(), BorderLayout.CENTER);
        add(msgBox, BorderLayout.SOUTH);
        return this;
    }

    public static void main(String[] s) {

        JFrame jf = new JFrame();
        jf.getContentPane().add(new CrookedRun().begin());
        jf.pack();
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * @author danyelf
     */
    public static class Selecter extends MouseAdapter implements ClickListener {

        Element selected = null;

        public void edgeClicked(ClickEvent ece) {
            select(ece);
        }

        public void vertexClicked(ClickEvent vce) {
            select(vce);
        }

        private synchronized void select(ClickEvent vce) {
            if (selected != null) selected.removeUserDatum("SELECTED");
            selected = vce.getGraphObject();
            selected.setUserDatum("SELECTED", "SELECTED", UserData.SHARED);
            msgBox.setText("SELECTED: " + vce.getGraphObject());
        }

        public synchronized void mouseClicked(MouseEvent e) {
            if (!e.isConsumed()) {
                if (selected != null) selected.removeUserDatum("SELECTED");
                selected = null;
                msgBox.setText("---");
            }
        }

    }

}
