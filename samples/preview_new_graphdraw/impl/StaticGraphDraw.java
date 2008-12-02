/*
 * Created on Jan 7, 2004
 */
package samples.preview_new_graphdraw.impl;

import java.awt.Dimension;

import javax.swing.JFrame;

import samples.preview_new_graphdraw.EdgeRenderer;
import samples.preview_new_graphdraw.VertexRenderer;
import samples.preview_new_graphdraw.event.ClickEvent;
import samples.preview_new_graphdraw.event.ClickListener;
import samples.preview_new_graphdraw.staticlayouts.CircleLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.utils.TestGraphs;

/**
 * @author danyelf
 */
public class StaticGraphDraw {

    protected GraphLayoutPanel jgp;

//    private Graph graph;

    /**
     * @param g
     * @param startup
     * @param layout
     * @param d
     */
    public static void main( String[] s ) {
        CircleLayout cl = new CircleLayout();
        Graph g = TestGraphs.getDemoGraph(); 
        Dimension d = new Dimension( 200, 200);
        cl.initializeLocations(d, g);
        VertexRenderer vr = new SimpleVertexRenderer();
        EdgeRenderer er = new SimpleEdgeRenderer();
        
        GraphLayoutPanel jgp = new GraphLayoutPanel(g, d, vr, er);
        jgp.addClickListener(new ClickListener() {

            public void edgeClicked(ClickEvent ece) {
                System.out.println( ece );
            }

            public void vertexClicked(ClickEvent vce) {
                System.out.println( vce );
            }           
            
        });
        jgp.setLayoutDisplay( cl.emit() );
        
        JFrame jf = new JFrame();
        jf.getContentPane().add( jgp );
        jf.pack();
        jf.setVisible(true);
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }


}