/*
 * Created on Dec 11, 2001
 * 
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package samples.preview_new_graphdraw;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.utils.Pair;

/**
 * This implements an edge between two points. Any subclasses--whether for
 * straight edges or curved--must implement (at least) getBack(), getFront(),
 * getSquareDistance(), and getEdge().
 * 
 * @author Scott and Danyel
 */
public class VisEdge {
	protected Edge mEdge;
	protected VisVertex mFront;
	protected VisVertex mBack;

	public VisEdge(Edge e, VisVertex v1, VisVertex v2) {
		mEdge = e;
		mFront = v1;
		mBack = v2;
		Pair p = e.getEndpoints();
		if (p.getFirst() == v2) {
			mFront = v2;
			mBack = v1;
		}
	}

	/**
	 * Returns the squared distance from this edge to the point <code>(x,y)</code>. 
     * The default implementation takes care of a distance from a straight edge to a point;
	 * more complex edges should have corresponding functions.
	 */
	public double getSquareDistance(double x, double y) {
		Coordinates c1 = mFront;
		Coordinates c2 = mBack;

		return CoordinateUtil.squareDistanceLineToPoint( c1, c2, x, y);
	}

	/**
	 * Returns the <code>Edge</code> which this <code>VisEdge</code>
     * represents.
	 */
	public Edge getEdge() {
		return mEdge;
	}

    /**
     * Returns the location of the second endpoint of the <code>Edge</code> which 
     * this <code>VisEdge</code> represents.
     */
	public Coordinates getFront() {
		return mFront;
	}

	/**
     * Returns the location of the first endpoint of the <code>Edge</code> which 
     * this <code>VisEdge</code> represents.
	 */
	public Coordinates getBack() {
		return mBack;
	}

	/**
     * Creates a new <code>VisEdge</code> from the specified 
     * <code>VisVertex</code> instances and this instance's
     * <code>Edge</code>.
	 */
	public VisEdge copy(VisVertex front, VisVertex back) {
		return new VisEdge( getEdge(), front, back );		
	}

}
