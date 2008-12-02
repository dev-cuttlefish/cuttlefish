/*
 * Created on Dec 8, 2003
 */
package samples.preview_new_graphdraw;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;

/**
 * @author Danyel Fisher, Scott White
 */
public abstract class StaticLayout extends AbstractLayout implements
        LayoutEmitter {

    //	/**
    //	 * Assigns XY positions for each vertex on the graph. For non-iterative
    //	 * layouts, this is all that there is!
    //	 *
    //	 * @param vertices
    //	 * @param edges
    //	 */
    public StaticLayout initializeLocations(Dimension d, Graph g) {
        setDimensions(d);
        createVisVertices(g.getVertices());
        createVisEdges(g.getEdges());
        return this;
    }

    protected void createVisVertices(Set vertices) {
        for (Iterator iter = vertices.iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            VisVertex vv = createVisVertex(v);
            visVertexMap.put(v, vv);
        }
    }

    protected void createVisEdges(Set edges) {
        for (Iterator iter = edges.iterator(); iter.hasNext();) {
            Edge e = (Edge) iter.next();
            Pair p = e.getEndpoints();
            Vertex v1 = (Vertex) p.getFirst();
            VisVertex vc1 = getVisVertex(v1);
            Vertex v2 = (Vertex) p.getSecond();
            VisVertex vc2 = getVisVertex(v2);
            visEdgeMap.put(e, createVisEdge(e, vc1, vc2));
        }
    }

    protected abstract VisVertex createVisVertex(Vertex v);

    protected VisEdge createVisEdge(Edge e, VisVertex vv1, VisVertex vv2) {
        return new VisEdge(e, vv1, vv2);
    }

    /**
     * this is a copy of this layout that the code can freely stomp on.
     * in this case, it's just this.
     * 
     * @see samples.preview_new_graphdraw.LayoutEmitter#emit()
     */
    public EmittedLayout emit() {
        EmittedLayout el = new EmittedLayout();
        el.visEdgeMap = visEdgeMap;
        el.visVertexMap = visVertexMap;
        el.screenSize = screenSize;
        return el;
    }

    //	/**
    //	 * Creates an uninitialized layout of the same type.
    //	 */
    //	public StaticLayout getInstance() {
    //		try {
    //			StaticLayout instance = (StaticLayout) this.clone();
    //			instance.screenSize = null;
    //			instance.visEdgeMap = new HashMap();
    //			instance.visVertexMap = new HashMap();
    //			return instance;
    //		} catch (CloneNotSupportedException clne) {
    //			throw new FatalException(
    //					"Clone should be supported for StaticLayouts! ", clne);
    //		}
    //	}
}
