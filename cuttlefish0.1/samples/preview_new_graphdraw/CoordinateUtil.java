/*
 * Created on Jan 6, 2004
 */
package samples.preview_new_graphdraw;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Danyel Fisher, Scott White
 */
public class CoordinateUtil {

	/**
	 * Computes the euclidean distance between two coordinates
	 * 
	 * @return the euclidean distance
	 */
	public static double distance(Coordinates a, Coordinates b) {
		return distance( a, b.getX(), b.getY());
	}

	/**
	 * @param coordinates
	 * @param x
	 * @param y
	 */
	public static double distance(Coordinates a, double x, double y) {
		double xDelta = a.getX() - x;
		double yDelta = a.getY() - y;
		return Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
	}
	
	/**
	 * Computes the midpoint between the two coordinates
	 * 
	 * @param o
	 *            another coordinates
	 * @return the midpoint
	 */
	public static Coordinates midpoint(Coordinates a, Coordinates b) {
		double midX = (a.getX() + b.getX()) / 2.0;
		double midY = (a.getY() + b.getY()) / 2.0;
		Coordinates midpoint = new Coordinates(midX, midY);
		return midpoint;
	}

	public static Coordinates scale(Coordinates a, double x, double y) {
		return new Coordinates(a.getX() * x, a.getY() * y);
	}

	public static Coordinates add(Coordinates a, double x, double y) {
		return new Coordinates(a.getX() + x, a.getY() + y);
	}

	/**
	 * Calculates the square of the distance from a segment running (x1, y1) to
	 * (x2, y2) to a point (x,y)
	 * 
	 * @param x1
	 *            The front x of a line
	 * @param y1
	 *            The front y of a line
	 * @param x2
	 *            The back x of a line
	 * @param y2
	 *            The back y of a line
	 * @param x
	 *            The point to check distance
	 * @param y
	 *            The point to check distance
	 * @return
	 */
	public static double squareDistanceLineToPoint(
			Coordinates front,
			Coordinates back,
		double x,
		double y) {
		double x1 = front.getX();
		double y1 = front.getY();
		double x2 = back.getX();
		double y2 = back.getY();
		if (x1 == x2 && y1 == y2)
			return getSquareDistance(front, x1, x2);

		double b =
			((y - y1) * (y2 - y1) + (x - x1) * (x2 - x1))
				/ ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		//
		double distance2; // square of the distance
		if (b <= 0)
			distance2 = (x - x1) * (x - x1) + (y - y1) * (y - y1);
		else if (b >= 1)
			distance2 = (x - x2) * (x - x2) + (y - y2) * (y - y2);
		else {
			double x3 = x1 + b * (x2 - x1);
			double y3 = y1 + b * (y2 - y1);
			distance2 = (x - x3) * (x - x3) + (y - y3) * (y - y3);
		}
		return distance2;
	}

	/**
	 * returns the square of the distance between two points.
	 * 
	 * @param x1
	 * @param y1
	 * @param x
	 * @param y
	 * @return
	 */
	public static double getSquareDistance(Coordinates u, double x, double y) {
		double x1 = u.getX();
		double y1 = u.getY();
		return (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y);
	}

	/**
	 * @param coordinates
	 * @param d
	 * @param e
	 * @return
	 */
	public static double angleBetween(Coordinates c, double x, double y) {
		double deltaY = c.getY() - y;
		double deltaX = c.getX() - x;
		return Math.atan2(deltaY, deltaX);
	}

    /**
     * Returns the closest intersection to <code>source</code> of the
     * line defined by <code>source</code> and <code>target</code>, 
     * and the bounding box <code>bounds</code>, or <code>null</code>
     * if there is no intersection.
     * @return
     */
    public static Coordinates getClosestIntersection(Coordinates source,
        Coordinates dest, Rectangle2D bounds)
    {
        Collection intersections = getIntersections(source, dest, bounds);
        if (intersections.isEmpty())
            return null;
        Iterator iter = intersections.iterator();
        Coordinates closest = (Coordinates)iter.next();
        double best = distance(source, closest);
        while (iter.hasNext())
        {
            Coordinates c = (Coordinates)iter.next();
            double c_dist = distance(source, c);
            if (c_dist < best)
            {
                best = c_dist;
                closest = c;
            }
        }
        return closest;
    }

    public static Collection getIntersections(Coordinates source, 
        Coordinates dest, Rectangle2D bounds)
    {
        Set intersections = new HashSet();
        double top = bounds.getMinY();
        double bottom = bounds.getMaxY();
        double left = bounds.getMinX();
        double right = bounds.getMaxX();
        
        // if line from source to dest is vertical...
        if (source.getX() == dest.getX())
        {
            if (source.getX() >= left && source.getX() <= right)
            {
                intersections.add(new Coordinates(source.getX(), top));
                intersections.add(new Coordinates(source.getX(), bottom));
            }
            return intersections;
        }

        Line l = new Line(source, dest);
        double x;
        double y;
        
        y = l.getY(left);
        if (y <= bottom && y >= top)
            intersections.add(new Coordinates(left,y));
        y = l.getY(right);
        if (y <= bottom && y >= top)
            intersections.add(new Coordinates(right,y));
        x = l.getX(top);
        if (x <= right && x >= left)
            intersections.add(new Coordinates(x,top));
        x = l.getX(bottom);
        if (x <= right && x >= left)
            intersections.add(new Coordinates(x,bottom));
        
        return intersections;
    }
    
    /**
     * A class which represents a geometric line.  Note that if the
     * line is vertical, intercept is not defined and the results may
     * not make sense.
     *  
     * @author Joshua O'Madadhain
     */
    protected static class Line
    {
        private double slope;
        private double intercept;
        
        public Line(Coordinates source, Coordinates dest)
        {
            if (source.equals(dest))
                throw new IllegalArgumentException("source and dest must be " +
                        "distinct locations");
            double delta_x = dest.getX() - source.getX();
            double delta_y = dest.getY() - source.getY();
            this.slope = delta_y / delta_x;
            this.intercept = 
                (dest.getX() * source.getY() - source.getX() * dest.getY()) / delta_x;
        }

        public Line(double slope, double intercept)
        {
            this.slope = slope;
            this.intercept = intercept;
        }
        
        public double getY(double x)
        {
            return slope * x + intercept;
        }
        
        public double getX(double y)
        {
            return (y - intercept) / slope;
        }
    }
}
