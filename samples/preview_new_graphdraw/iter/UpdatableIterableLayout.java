/*
 * Created on Mar 24, 2004
 */
package samples.preview_new_graphdraw.iter;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;

import samples.preview_new_graphdraw.VisEdge;
import samples.preview_new_graphdraw.VisVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;

/**
 * @author danyelf
 */
public abstract class UpdatableIterableLayout extends IterableLayout {

    /**
     * Updates the stored set of of vertices and edges to match the input
     * graph.
     * This trivial default implementation removes all the vertices in the set
     * that aren't in g, and calls new VisVertex for each that should be.
     * Calls updateGraph and cleanupReferences.
     * A reasonable implementation should probably care about internal
     * data structures that may need to be updated.
     * 
     * @param g
     */
    public void updateGraphToMatch(Graph g) {
        Pair oldAndNewVertices = getSymmetricDifference(
                currentLayout.visVertexMap.keySet(), g.getVertices());
        Pair oldAndNewEdges = getSymmetricDifference(currentLayout.visEdgeMap
                .keySet(), g.getEdges());
        updateGraph(oldAndNewVertices, oldAndNewEdges);
//        cleanupReferences(g);
    }

    /**
     * This OPTIONAL OPTIMIZATION allows the java garbage collector to work. In
     * particular, it ensures that all the vertices in the visVertexMap are in
     * the CURRENT graph, not in some past graph. (Yes, I think it's a brutal
     * and hackish way to solve this problem. I guess I could index by the
     * non-public "id" field of the vertex, but that's probably unfair.)
     */
    protected void cleanupReferences(Graph g) {
        for (Iterator iter = currentLayout.visVertexMap.entrySet().iterator(); iter
                .hasNext();) {
            Map.Entry vertexEntry = (Map.Entry) iter.next();
            Vertex v = (Vertex) vertexEntry.getKey();
            if (v.getGraph() != g) {
                currentLayout.visVertexMap.remove(v);
                currentLayout.visVertexMap.put(v.getEqualVertex(g), vertexEntry
                        .getValue());
            }

        }

        for (Iterator iter = currentLayout.visEdgeMap.entrySet().iterator(); iter
                .hasNext();) {
            Map.Entry edgeEntry = (Map.Entry) iter.next();
            Edge e = (Edge) edgeEntry.getKey();
            if (e.getGraph() != g) {
                currentLayout.visEdgeMap.remove(e);
                currentLayout.visEdgeMap.put(e.getEqualEdge(g), edgeEntry
                        .getValue());
            }

        }
    }

    /**
     * This DEFAULT IMPLEMENTATION immediately removes all OLD items, and
     * immediate creates new items for the NEW. Calls createVisVertex and
     * createVisEdge to handle the new locations. Override if you don't need
     * them.
     * 
     * @param oldAndNewVertices
     * @param oldAndNewVertices
     */
    protected void updateGraph(Pair oldAndNewVertices, Pair oldAndNewEdges) {
        Collection oldVertices = (Collection) oldAndNewVertices.getFirst();
        Collection newVertices = (Collection) oldAndNewVertices.getSecond();
        for (Iterator iter = oldVertices.iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            removeVertex( v );
        }
        for (Iterator iter = newVertices.iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            currentLayout.visVertexMap.put( v, addVisVertex( v ) ) ;            
        }

        Collection oldEdges = (Collection) oldAndNewEdges.getFirst();
        Collection newEdges = (Collection) oldAndNewEdges.getSecond();
        
        for (Iterator iter = oldEdges.iterator(); iter.hasNext();) {
            Edge e = (Edge) iter.next();
            removeEdge( e );
        }
        for (Iterator iter = newEdges.iterator(); iter.hasNext();) {
            Edge e = (Edge) iter.next();
            currentLayout.visEdgeMap.put( e, addVisEdge( e ) ) ;            
        }

    
    }


    /**
     * Removes <code>v</code> from the layout. Override to 
     * do something different. (For example, if you want
     * vertices to resurface later where they currently
     * are, then override this method to do nothing.)
     */
    protected void removeVertex(Vertex v) {
        currentLayout.visVertexMap.remove( v ) ;                    
    }
    
    /**
     * Removes <code>e</code> from the layout. Override to 
     * do something different. (For example, if you want
     * edges to resurface later where they currently
     * are, then override this method to do nothing.)
     */
    protected void removeEdge(Edge e) {
        currentLayout.visEdgeMap.remove( e ) ;            
    }

    /**
     * Creates and returns a default VisEdge based on <code>e</code>. 
     * Note that if you have overridden removeEdge, then an
     * entry MAY ALREADY EXIST! (This version does not check)
     * // TODO THIS VERSION SHOUDL CHECK 
     * @param e
     * @return
     */
    protected VisEdge addVisEdge(Edge e) {
        // get the two vertices at either end
        // and create a visEdge between them
        Pair p = e.getEndpoints();
        Vertex v1 = (Vertex) p.getFirst();
        Vertex v2 = (Vertex) p.getSecond();
        VisVertex vv1 = currentLayout.getVisVertex(v1);
        VisVertex vv2 = currentLayout.getVisVertex(v2);        
        System.out.println("New visedge for " + e );
        return new VisEdge(e, vv1, vv2);
    }

    /**
     * Creates and returns a default VisVertex based on <code>v</code>. 
     * Default implementation places the new vertex at a RANDOM location.
     * Override to place someplace more sensible.
     * Note that if you have overridden removeEdge, then an
     * entry MAY ALREADY EXIST! (This version does not check)
      * // TODO THIS VERSION SHOUDL CHECK 
     */
    protected VisVertex addVisVertex(Vertex v) {
		double x = Math.random() * currentLayout.getScreenSize().getWidth();
		double y = Math.random() * currentLayout.getScreenSize().getHeight();
        VisVertex vv = new VisVertex( v, x, y );
        System.out.println("New vertex " + v + " at "  + x + " " + y );
        return vv;
    }

    /**
     * Returns a pair consisting of two collections: set1 - set2, and set2-set1.
     */
    public static Pair getSymmetricDifference(Set set1, Set set2) {
        Collection old = CollectionUtils.subtract(set1, set2);
        Collection newP = CollectionUtils.subtract(set2, set1);
        return new Pair(old, newP);
    }
}
