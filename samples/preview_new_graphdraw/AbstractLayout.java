/*
 * Created on Dec 8, 2003
 */
package samples.preview_new_graphdraw;

import java.awt.Dimension;
import java.util.*;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

/**
 * This class is essentially a decorator: for each vertex or edge, it must return a
 * VisVertex or VisEdge object. Note that it also must know about the
 * Dimensions of the current space (although whether it does anything about
 * them is another question entirely). This abstract class is the superclass
 * for just one subclass:
 * <ul>
 * <li/>The <i>StaticLayout</i>, which creates a new layout from
 * scratch.
 * </ul>
 * 
 * @author Danyel Fisher, Scott White
 */
public abstract class AbstractLayout implements Cloneable
{

	public Dimension screenSize;

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

	/**
	 * @param dimension
	 */
	public void setDimensions(Dimension dimension)
	{
		Dimension oldScreenSize = screenSize;
		this.screenSize = dimension;
		// let's also offset every vertex by a notch
		repositionLayout(oldScreenSize, screenSize);
	}

	/*------------- DISTANCE UTIL METHODS ----------------------- */

	/**
	 * @param oldScreenSize
	 * @param screenSize2
	 */
	protected void repositionLayout(
		Dimension oldScreenSize,
		Dimension screenSize2)
	{
		for (Iterator iter = visVertexMap.values().iterator(); iter.hasNext();)
		{
			VisVertex v = (VisVertex) iter.next();
			v.offset(
				(screenSize2.width - oldScreenSize.width) / 2,
				(screenSize2.height - oldScreenSize.height) / 2);
		}
	}

//	public Vertex getNearestVertex(Set vertices, double x, double y)
//	{
//		Vertex closest = null;
//		double minDistance = Double.MAX_VALUE;
//		for (Iterator iter = vertices.iterator(); iter.hasNext();)
//		{
//			Vertex v = (Vertex) iter.next();
//			VisVertex vc = getVisVertex(v);
//			double dist = vc.getSquareDistance(x, y);
//			if (dist < minDistance)
//			{
//				minDistance = dist;
//				closest = v;
//			}
//		}
//		return closest;
//	}

//	public Edge getNearestEdge(Set edges, double x, double y)
//	{
//		double minDistance = Double.MAX_VALUE;
//		Edge closest = null;
//		for (Iterator iter = edges.iterator(); iter.hasNext();)
//		{
//			Edge e = (Edge) iter.next();
//			VisEdge ec = getVisEdge(e);
//
//			double distance2 = ec.getSquareDistance(x, y);
//
//			if (distance2 < minDistance)
//			{
//				minDistance = distance2;
//				closest = e;
//			}
//		}
//		return closest;
//	}

	/**
	 * @return the current dimensions of the screen size
	 */
	public Dimension getScreenSize()
	{
		return screenSize;
	}

}