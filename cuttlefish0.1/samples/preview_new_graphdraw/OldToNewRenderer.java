/*
 * Created on Apr 15, 2004
 */
package samples.preview_new_graphdraw;

import java.awt.Graphics;

import edu.uci.ics.jung.visualization.Renderer;

/**
 * This utility class converts your favorite old-style renderer into the 
 * newfangled NewGraphDraw VertexRenderer and EdgeRenderer.
 *  
 * @author danyelf
 */
public class OldToNewRenderer implements VertexRenderer, EdgeRenderer {

    private Renderer renderer;

    public OldToNewRenderer(Renderer r) {
        this.renderer = r;
    }

    /**
     * @see samples.preview_new_graphdraw.VertexRenderer#renderVertex(java.awt.Graphics,
     *      samples.preview_new_graphdraw.VisVertex)
     */
    public void renderVertex(Graphics g, VisVertex vc) {
        renderer.paintVertex(g, vc.getVertex(), (int) vc.getX(), (int) vc
                .getY());
    }

    /**
     * @see samples.preview_new_graphdraw.EdgeRenderer#renderEdge(java.awt.Graphics,
     *      samples.preview_new_graphdraw.VisEdge)
     */
    public void renderEdge(Graphics g, VisEdge ec) {
        renderer.paintEdge(g, ec.getEdge(), (int) ec.getFront().getX(),
                (int) ec.getFront().getY(), (int) ec.getBack().getX(), (int) ec
                        .getBack().getY());
    }

}