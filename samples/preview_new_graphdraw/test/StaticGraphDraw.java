/*
 * Created on Jan 7, 2004
 */
package samples.preview_new_graphdraw.test;

import java.awt.Dimension;

import javax.swing.JFrame;

import samples.preview_new_graphdraw.EdgeRenderer;
import samples.preview_new_graphdraw.StaticLayout;
import samples.preview_new_graphdraw.VertexRenderer;
import samples.preview_new_graphdraw.event.ClickEvent;
import samples.preview_new_graphdraw.event.ClickListener;
import samples.preview_new_graphdraw.impl.*;
import samples.preview_new_graphdraw.iterablelayouts.KKLayout;
import samples.preview_new_graphdraw.staticlayouts.*;
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
//        StaticLayout start = new CircleLayout();
        StaticLayout end = new IterableToStaticLayout(new CircleLayout(), new KKLayout());
        Graph g = TestGraphs.getDemoGraph(); 
        Dimension d = new Dimension( 200, 200);
        end.initializeLocations(d, g);
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
        jgp.setLayoutDisplay( end.emit() );
        
        JFrame jf = new JFrame();
        jf.getContentPane().add( jgp );
        jf.pack();
        jf.show();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }


}