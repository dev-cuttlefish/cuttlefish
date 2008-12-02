/*
 * Created on Mar 22, 2004
 */
package samples.preview_new_graphdraw.iterablelayouts;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.VisEdge;
import samples.preview_new_graphdraw.VisVertex;
import samples.preview_new_graphdraw.iter.GraphLayoutPanelUtils;
import samples.preview_new_graphdraw.iter.IterableLayout;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

/**
 * This abstract iterable layout interpolates between two different 
 * emitted layouts over K frames. Override by defining:
 * <ul>
 * <li/>What to do with vertices in start, but not end (at step i of K)
 * <li/>What to do with vertices in end, but not start (...)
 * <li/>What to do with edges in start, but not end (...)
 * <li/>What to do with edges in end, but not start (...)
 * <li/>Where to put a vertex in both start and end at step i  (...)
 * 
 * @author danyelf
 */
public abstract class InterpolatingIterableLayout extends IterableLayout {
    
    protected EmittedLayout start;
    protected EmittedLayout end;
    final protected int numFrames;
    int thisFrame = 0;

    public InterpolatingIterableLayout( EmittedLayout end , int numFrames ) {
        this.end = end;
        this.numFrames = numFrames;
    }
        
    /* (non-Javadoc)
     * @see samples.preview_new_graphdraw.iter.IterableLayout#iterationsAreDone()
     */
    public boolean iterationsAreDone() {
        return thisFrame >= numFrames;
    }

    /* (non-Javadoc)
     * @see samples.preview_new_graphdraw.iter.IterableLayout#isFinite()
     */
    public boolean isFinite() {
        return true;
    }

    /* (non-Javadoc)
     * @see samples.preview_new_graphdraw.iter.IterableLayout#calculate()
     */
    protected void calculate() {
        // should list the k'th step from start to end
        // and set currentLayout to point to it.        
        Set vertices = new HashSet( start.visVertexMap.keySet());
        vertices.addAll( end.visVertexMap.keySet());
        for (Iterator iter = vertices.iterator(); iter.hasNext();) {
             Vertex v = (Vertex) iter.next();
             VisVertex startV = start.getVisVertex(v);
             VisVertex endV = end.getVisVertex(v);
             currentLayout.visVertexMap.put( v, interpolateV( startV, endV, thisFrame, numFrames ));
        }

        Set edges = new HashSet( start.visEdgeMap.keySet());
        edges.addAll( end.visEdgeMap.keySet());
        for (Iterator iter = edges.iterator(); iter.hasNext();) {
             Edge e = (Edge) iter.next();
             VisEdge startE = start.getVisEdge(e);
             VisEdge endE = end.getVisEdge(e);
             VisVertex f = currentLayout.getVisVertex( (Vertex) e.getEndpoints().getFirst() );
             VisVertex s = currentLayout.getVisVertex( (Vertex) e.getEndpoints().getSecond() );
             currentLayout.visEdgeMap.put( e, interpolateE( startE, endE, f, s, thisFrame, numFrames ));
        }

//        System.out.println("Now calculating " + thisFrame );
        thisFrame ++;
    }

    public void initializeLocationsFromLayout(EmittedLayout inputLayout) {
//        System.out.println("Initializing " + this);
        super.initializeLocationsFromLayout(inputLayout);
        // "start" must be a distinct object from currentLayout,
        // or the world will be a confusing place indeed!
        this.start = GraphLayoutPanelUtils.copy(currentLayout);
//        System.out.println("start = " + start.visVertexMap.size());
//        System.out.println("end = " + end.visVertexMap.size());
    }

    /**
     * @param startE
     * @param endE
     * @param thisFrame2
     * @param numFrames2
     * @return
     */
    protected abstract VisEdge interpolateE(VisEdge startE, VisEdge endE, VisVertex f, VisVertex s, int thisFrame2, int numFrames2);

    /**
     * @param startV
     * @param endV
     * @param thisFrame2
     * @param numFrames2
     * @return
     */
    protected abstract VisVertex interpolateV(VisVertex startV, VisVertex endV, int thisFrame2, int numFrames2);

}
