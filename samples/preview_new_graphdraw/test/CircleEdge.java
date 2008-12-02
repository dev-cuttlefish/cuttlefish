/*
 * Created on Mar 23, 2004
 */
package samples.preview_new_graphdraw.test;

import samples.preview_new_graphdraw.*;
import edu.uci.ics.jung.graph.Edge;


public class CircleEdge extends VisEdge {

		public VisEdge copy(VisVertex front, VisVertex back) {
			return new CircleEdge(this.mEdge, front, back);
		}

		public CircleEdge(Edge ve, VisVertex front, VisVertex back) {
			super(ve, front, back);
		}

		public Coordinates getCenter() {
			return CoordinateUtil.midpoint(getFront(), getBack());
		}

		public double getRadius() {
			return CoordinateUtil.distance(getFront(), getBack()) / 2;
		}

		public double getStartAngle() {
			double theta = CoordinateUtil.angleBetween( getFront(), getBack().getX(), getBack().getY() );
//			if (square) {
				return theta;
//			} else {
//				return theta + Math.PI;
//			}
		}

		/**
		 * If it's in the half-plane that contains the half-circle,
		 * return the distance, else return infinity. (You shouldn't
		 * be clicking on the wrong side.) 
		 * @see samples.preview_new_graphdraw.VisEdge#getSquareDistance(double,
		 *      double)
		 */
		public double getSquareDistance(double x, double y) {
			
			// which half-plane are we in?
			double theta = CoordinateUtil.angleBetween( getCenter(), x, y );

			// runs from here to theta_of_circle + Math.PI;
			double theta_of_circle = getStartAngle();
			
			if ( theta_of_circle > Math.PI ) {
				theta_of_circle -= Math.PI * 2;
			}

			if ( theta > Math.PI ) {
				theta -= Math.PI * 2;
			}
						
			if ( theta > theta_of_circle && theta < theta_of_circle + Math.PI ) {
				double toCenter = CoordinateUtil.distance(getCenter(), x, y);
				double actual = Math.abs(toCenter - getRadius());
				return actual * actual;				
			} else {
				System.out.println(getEdge() + " wrong half plane " );
				return Double.POSITIVE_INFINITY;
			}
			//			return CoordinateUtil.getSquareDistance( );
		}

	}