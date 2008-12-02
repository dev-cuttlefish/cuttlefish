/*
 * Created on Jan 7, 2004
 */
package samples.preview_new_graphdraw.test;

import java.awt.Dimension;

import javax.swing.JFrame;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.StaticLayout;
import samples.preview_new_graphdraw.event.ClickEvent;
import samples.preview_new_graphdraw.event.ClickListener;
import samples.preview_new_graphdraw.impl.GraphLayoutPanel;
import samples.preview_new_graphdraw.impl.SimpleEdgeRenderer;
import samples.preview_new_graphdraw.impl.SimpleVertexRenderer;
import samples.preview_new_graphdraw.iter.IterableLayout;
import samples.preview_new_graphdraw.iter.LocalGraphDraw;
import samples.preview_new_graphdraw.iterablelayouts.KKLayout;
import samples.preview_new_graphdraw.iterablelayouts.SpringLayout;
import samples.preview_new_graphdraw.iterablelayouts.WrappedIterableLayout;
import samples.preview_new_graphdraw.staticlayouts.CircleLayout;
import samples.preview_new_graphdraw.staticlayouts.IterableToStaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.utils.TestGraphs;

/**
 * @author danyelf
 */
public class NiftyAnimationDemo {

    protected GraphLayoutPanel jgp;

//    private Graph graph;

    /**
     * @param g
     * @param startup
     * @param layout
     * @param d
     */
    public static void main( String[] s ) {
        Graph g = TestGraphs.getDemoGraph(); 
        Dimension d = new Dimension( 200, 200);
        
        StaticLayout start = new CircleLayout();
        StaticLayout end = new IterableToStaticLayout(new CircleLayout(), new KKLayout( 1 ));

        EmittedLayout endE = end.initializeLocations(  d, g ).emit();
        
        IterableLayout interpolation = new LinearInterpolatingLayout( endE, 100 );
        IterableLayout nudge = new SpringLayout();
        IterableLayout displayFromInterpolateToNudge = new WrappedIterableLayout(interpolation, nudge, 10 );
                
        SimpleVertexRenderer svr = new SimpleVertexRenderer();
        SimpleEdgeRenderer ser = new SimpleEdgeRenderer();

        LocalGraphDraw lgd = new LocalGraphDraw(g, start, displayFromInterpolateToNudge, svr, ser, d, false  );
        lgd.start();
                
        GraphLayoutPanel jgp = lgd.getPanel();

        jgp.addClickListener(new ClickListener() {

            public void edgeClicked(ClickEvent ece) {
                System.out.println( ece );
            }

            public void vertexClicked(ClickEvent vce) {
                System.out.println( vce );
            }           
            
        });
        
        JFrame jf = new JFrame();
        jf.getContentPane().add( jgp );
        jf.pack();
        jf.show();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }


}