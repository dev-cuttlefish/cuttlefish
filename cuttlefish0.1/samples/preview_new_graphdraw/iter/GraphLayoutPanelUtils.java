/*
 * Created on Jan 7, 2004
 */
package samples.preview_new_graphdraw.iter;

import java.util.Iterator;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.VisEdge;
import samples.preview_new_graphdraw.VisVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;


/**
 * @author danyelf
 */
public class GraphLayoutPanelUtils {

	/**
	 * Pre-relaxes a layout. Iterates forward for millis milliseconds
	 */
	public static void prerelax(long millis, IterableLayout la) {
		long timeNow = System.currentTimeMillis();
		while (System.currentTimeMillis() - timeNow < millis)
			la.advance();
	}

	/**
	 * Copies a layout into a destination layout (whose values are cleared)
	 * @param dest	the destination layout
	 * @param layoutToCopy the original layout.
	 * @return
	 */
	public static EmittedLayout copy( EmittedLayout dest, EmittedLayout layoutToCopy ) {
		dest.screenSize.setSize( layoutToCopy.screenSize );
		dest.visEdgeMap.clear();
		dest.visVertexMap.clear();
	    for (Iterator iter = layoutToCopy.visVertexMap.values().iterator(); iter
	            .hasNext();) {
	        VisVertex vv = (VisVertex) iter.next();
	        Vertex v = vv.getVertex();
	        // and copy it in
	        dest.visVertexMap.put(v, vv.copy());
	    }
	    for (Iterator iter = layoutToCopy.visEdgeMap.values().iterator(); iter
	            .hasNext();) {
	        VisEdge ve = (VisEdge) iter.next();
	        Edge e = ve.getEdge();
	        Pair p = e.getEndpoints();
	        VisVertex front = dest.getVisVertex((Vertex) p.getFirst());
	        VisVertex back = dest.getVisVertex((Vertex) p.getSecond());
	        // and copy it in
	        dest.visEdgeMap.put(e, ve.copy(front, back));
	    }        
	    return dest;
	}
	
	/**
	 * Copies a layout into a new layout, which is returned.
	 * @return
	 */
	public static EmittedLayout copy(EmittedLayout layoutToCopy) {
	    EmittedLayout sl = new EmittedLayout();
	    return copy( sl, layoutToCopy) ;
	}

}
