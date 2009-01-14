/*
 * Created on Dec 11, 2001
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package samples.preview_new_graphdraw;

import edu.uci.ics.jung.graph.Vertex;

/**
 * @author Scott and Danyel
 *  
 */
public class VisVertex extends Coordinates {
	protected Vertex mVertex;

	public VisVertex(Vertex v, double x, double y) {
		super( x, y );
		mVertex = v;
	}

	public double getSquareDistance(double x, double y) {
		return CoordinateUtil.getSquareDistance( this, x, y);
	}

	/**
	 * @return
	 */
	public Vertex getVertex() {
		return mVertex;
	}

	/**
	 * Creates a new visvertex with this same location 
	 * @return
	 */
	public VisVertex copy() {
		return new VisVertex( this.mVertex, getX(), getY());
	}
}
