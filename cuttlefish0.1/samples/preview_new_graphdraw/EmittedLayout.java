/*
 * Created on Jan 6, 2004
 */
package samples.preview_new_graphdraw;

import java.awt.Dimension;
import java.util.*;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;


/**
 * @author Danyel Fisher, Scott White
 */
public class EmittedLayout  {

	static int count = 0;
	
//	public EmittedLayout() {
//		count++;
//		System.out.println("Constructions: " + count);
//	}
	
	public Dimension screenSize = new Dimension();

	public Map visVertexMap = new HashMap();
	public Map visEdgeMap = new HashMap();

	/*------------- FUNDAMENTAL METHODS ----------------------- */

	public VisVertex getVisVertex(Vertex v)
	{
		return (VisVertex) visVertexMap.get(v);
	}

	public VisEdge getVisEdge(Edge e)
	{
		return (VisEdge) visEdgeMap.get(e);
	}

	public Dimension getDimension() {
	    return screenSize;
	}
    
//	/**
//	 * @param vertex
//	 * @param vertex2
//	 */
//	public void addVisVertex(VisVertex vertex) {
//		visVertexMap.put(vertex.getVertex(), vertex.copy() );
//	}
//
//	/**
//	 * @param edge
//	 * @param edge2
//	 */
//	public void addVisEdge(VisEdge edge) {
//		VisVertex front = (VisVertex) visVertexMap.get(edge.getEdge().getEndpoints().getFirst());
//		VisVertex back = (VisVertex) visVertexMap.get(edge.getEdge().getEndpoints().getSecond());
//		visEdgeMap.put(edge.getEdge(), edge.copy( front, back ) );
//	}

	public Vertex getNearestVertex(double x, double y)
	{
		Vertex closest = null;
		double minDistance = Double.MAX_VALUE;
		for (Iterator iter = visVertexMap.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry me = (Map.Entry) iter.next();
			VisVertex vc = (VisVertex) me.getValue();
			double dist = vc.getSquareDistance(x, y);
			if (dist < minDistance)
			{
				minDistance = dist;
				closest = (Vertex) me.getKey();
			}
		}
		return closest;
	}

	public Edge getNearestEdge(double x, double y)
	{
		double minDistance = Double.MAX_VALUE;
		Edge closest = null;
		for (Iterator iter = visEdgeMap.entrySet().iterator(); iter.hasNext();)
		{
		    Map.Entry me = (Map.Entry) iter.next();
			VisEdge ec = (VisEdge) me.getValue();

			double distance2 = ec.getSquareDistance(x, y);

			if (distance2 < minDistance)
			{
				minDistance = distance2;
				closest = (Edge) me.getKey();
			}
		}
		return closest;
	}

    /**
     * @return
     */
    public Dimension getScreenSize() {
        return screenSize;
    }

}
