/*
 * Created on Jan 7, 2004
 */
package samples.preview_new_graphdraw.impl.crooked;

import samples.preview_new_graphdraw.*;
import samples.preview_new_graphdraw.iterablelayouts.SpringLayout;
import edu.uci.ics.jung.graph.Edge;

/**
 * 
 * @author danyelf
 */
public class CrookedSpringLayout extends SpringLayout {

	protected static class CrookedSpringEdge extends SpringEdge {

		public VisEdge copy( VisVertex front, VisVertex back ) {
			return new CrookedSpringEdge( this.mEdge, front, back );
		}
		
		public CrookedSpringEdge(Edge ve, VisVertex front, VisVertex back) {
			super( ve, front, back );
		}
		
		public Coordinates calcMidpoint() {
			Coordinates m = CoordinateUtil.midpoint(getFront(), getBack());
			Coordinates c = new Coordinates(getFront().getX(), getBack().getY());
			return CoordinateUtil.midpoint(m, c);
		}

		/**
		 * @see samples.preview_new_graphdraw.VisEdge#getSquareDistance(double, double)
		 */
		public double getSquareDistance(double x, double y) {
			Coordinates crook = calcMidpoint();
			// we have two segments to worry about.
			// one edge runs from front to midpoint, the other from midpoint to back
			double dist1 = CoordinateUtil.squareDistanceLineToPoint(getFront(), crook, x, y);
			double dist2 = CoordinateUtil.squareDistanceLineToPoint(getBack(), crook, x, y);
			return Math.min( dist1, dist2 );
		}
		
		
	}
	
	protected VisEdge createVisEdge(Edge ve, VisVertex front, VisVertex back) {
		return new CrookedSpringEdge(ve, front, back);
	}
		
}
