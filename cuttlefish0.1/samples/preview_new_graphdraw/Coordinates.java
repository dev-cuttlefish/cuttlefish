/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package samples.preview_new_graphdraw;

import java.awt.Dimension;
import java.awt.Point;

/**
 * 
 * Stores coordinates (X,Y) for vertices being visualized. 
 * 
 * @author Scott White
 */
public class Coordinates {

    /** x location */
    public double x;
    /** y location */
    public double y;

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

	/**
	 * Initializes this coordinate to the value of the passed-in
	 * coordinate.
	 * @param coordinates
	 */
    public Coordinates(Coordinates coordinates) {
        this.x = coordinates.getX();
        this.y = coordinates.getY();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

	/**
	 * Sets the x value to be d;
	 * @param d
	 */
    public void setX(double d) {
        x = d;
    }

	/**
	 * Sets the y value to be d;
	 * @param d
	 */
    public void setY(double d) {
        y = d;
    }

	/**
	 * Increases the x and y values of this
	 * scalar by (delta_x, delta_y).
	 * @param delta_x
	 * @param delta_y
	 */
    public void offset(double delta_x, double delta_y) {
    	this.x += delta_x;
    	this.y += delta_y;
    }

    public void scale(Dimension old, Dimension newDim) {
        this.x *= (newDim.getWidth() / old.getWidth());
        this.y *= (newDim.getHeight() / old.getHeight() );
    }
    
    public boolean equals(Object o)
    {
        double u, v;
        if (o instanceof Coordinates)
        {
            Coordinates c = (Coordinates)o;
            u = c.getX();
            v = c.getY();
        }
        else if (o instanceof Point)
        {
            Point p = (Point)o;
            u = p.getX();
            v = p.getY();
        }
        else
            return false;
        
        return ((Math.abs(u - getX()) < Float.MIN_VALUE) && 
                (Math.abs(v - getY()) < Float.MIN_VALUE));
    }
    
    public int hashCode()
    {
        return (int)(x * 271828 + y);
    }
}
