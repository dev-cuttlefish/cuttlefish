/*
 * Created on Dec 8, 2003
 */
package samples.preview_new_graphdraw.iter;

import java.awt.Dimension;

import samples.preview_new_graphdraw.*;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

/**
 * An IterableLayout
 * 
 * @author Danyel Fisher
 */
public abstract class IterableLayout implements LayoutEmitter {

    protected EmittedLayout currentLayout;

    protected EmittedLayout returnableLayout;

    /**
     * Copies the current layout into the retunrable layout, 
     * then moves the visualization one step forward 
     * (by calling calculate). This method should probably
     * not be overridden in most cases.
     *
     */
    public void advance() {
        returnableLayout = GraphLayoutPanelUtils.copy(currentLayout);
        calculate();
    }

    /**
     * Gives the outlining algorithm a sense of how long to run. The algorithm
     * is responsible for setting its own thresholds. Note that this should return
     * a meaningful value (probably "false") even before initialize
     * has been called.
     */
    public abstract boolean iterationsAreDone();
    
    /**
     * Says whether this algorthms' iterations will ever end. (If not, there's
     * no real use to waiting.) 
     */
    public abstract boolean isFinite();
    
    /**
     * Transforms currentLayout according to current layout algorithm. This may
     * change the state of the currentLayout object.
     */
    protected abstract void calculate();

    /**
     * Creates some sort of layout that has all the same vertex and edge
     * locations. This copy can be visualized while the main thread moves
     * forward. [It's not necessary that this use clone!]
     */
    public EmittedLayout emit() {
        return returnableLayout;
    }

    /**
     * Starts up this layout based on a previous layout. It is reasonable
     * to assume that this method will be called only once. After this
     * method is complete, a CURRENT LAYOUT should be defined with the
     * current states of the vertices, and a RETURNABLE LAYOUT should be
     * defined for emit() to return. In general, emit() will just return
     * this copy of the layout stored in returnableLayout, so this
     * method and advance() are both respon sible for advancing
     * correctly.
     * 
     * @param sla
     */
    public void initializeLocationsFromLayout(EmittedLayout inputLayout) {
        this.currentLayout = inputLayout;
        returnableLayout = GraphLayoutPanelUtils.copy(currentLayout);
    }

    //	/**
    //	 * Given a VisVertex, creates a VisVertex for this particular graph at
    // that
    //	 * location. The default implemenation merely calls the VisVertex copier.
    //	 *
    //	 * @param vertex
    //	 * @return
    //	 */
    //	protected VisVertex createVisVertex(VisVertex vertex) {
    //		return new VisVertex(vertex.getVertex(), vertex.getX(), vertex.getY());
    //	}
    //
    //	/**
    //	 * Given a VisVertex, creates a VisVertex for this particular graph at
    // that
    //	 * location. The default implemenation merely calls the VisVertex copier.
    //	 *
    //	 * @param edge
    //	 * @return
    //	 */
    //	protected VisEdge createVisEdge(VisEdge edge) {
    //		VisVertex front =
    //			(VisVertex) visVertexMap.get(
    //				edge.getEdge().getEndpoints().getFirst());
    //		VisVertex back =
    //			(VisVertex) visVertexMap.get(
    //				edge.getEdge().getEndpoints().getSecond());
    //		return createVisEdge(edge.getEdge(), front, back);
    //	}
    //
    //	/**
    //	 * @param edge
    //	 * @param front
    //	 * @param back
    //	 * @return
    //	 */
    //	protected VisEdge createVisEdge(Edge edge, VisVertex front, VisVertex
    // back) {
    //		return new VisEdge(edge, front, back);
    //	}

    /**
     * @param v
     */
    protected VisVertex getVisVertex(Vertex v) {
        return currentLayout.getVisVertex(v);
    }

    protected VisEdge getVisEdge(Edge e) {
        return currentLayout.getVisEdge(e);
    }

    protected Dimension getScreenSize() {
        return currentLayout.getDimension();
    }

}
