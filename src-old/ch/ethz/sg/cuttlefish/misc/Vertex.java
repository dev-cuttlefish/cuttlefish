/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

	This file is part of Cuttlefish.
	
 	Cuttlefish is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
*/

package ch.ethz.sg.cuttlefish.misc;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 * Class that stores the information associated with a vertex
 * @author david
 */
public class Vertex implements Comparable<Vertex> {
	
	
	private int idTemp;
	private int id;
	private int width = 1;
	private String label = null;
	private Color fillColor = Color.BLACK;
	private Color color  = Color.BLACK;
	private double size = 10;
	private boolean shadowed = false;
	private Shape shape = null;
	private String var1 = null;
	private String var2 = null;
	private boolean excluded = false;
	private boolean fixed = false;
	private boolean isRoot = false;
	private Point2D position;
	
	private static int idSeed = 0;
	
	/**
	 * General constructor for a vertex
	 * @param id
	 */
	public Vertex(int id){
		this.id = generateID(id);
		Ellipse2D ellipse = new Ellipse2D.Float();
		ellipse.setFrameFromCenter(0,0,size,size);
		shape = ellipse;
		position = null;
	}

	/**
	 * Constructor for a vertex with a label.
	 * @param id
	 * @param label
	 */
	public Vertex(int id, String label){
		this.id = generateID(id);
		this.label = label;
		Ellipse2D ellipse = new Ellipse2D.Float();
		ellipse.setFrameFromCenter(0,0,size,size);
		shape = ellipse;
		position = null;
		}

	/**
	 * Constructor for an annonymous vertex
	 */
	public Vertex() {
		this.id = generateID(-1);
		Ellipse2D ellipse = new Ellipse2D.Float();
		ellipse.setFrameFromCenter(0,0,size,size);
		shape = ellipse;
		position = null;
	}
	
	private static int generateID(int id) {
		
		if (id < 0 ) {
			return idSeed++;
		}
		
		if (id > idSeed) {
			idSeed = id;
		}
		
		return id;
	}

	/**
	 * @param idTemp the idTemp to set
	 */
	public void setIdTemp(int idTemp) {
		this.idTemp = idTemp;
	}
	/**
	 * @return the idTemp
	 */
	public int getIdTemp() {
		return idTemp;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param b boolean specifying whether the vertex is a root vertex
	 */
	public void setIsRoot(boolean b) {
		this.isRoot = b;
	}
	/**
	 * @return isRoot
	 */
	public boolean isRoot() {
		return isRoot;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param fillColor the fillColor to set
	 */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}
	/**
	 * @return the fillColor
	 */
	public Color getFillColor() {
		return fillColor;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * @param shadowed the shadowed to set
	 */
	public void setShadowed(boolean shadowed) {
		this.shadowed = shadowed;
	}
	/**
	 * @return the shadowed
	 */
	public boolean isShadowed() {
		return shadowed;
	}

	/**
	 * @param excluded the excluded to set
	 */
	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
	}
	/**
	 * @return the exclude
	 */
	public boolean isExcluded() {
		return excluded;
	}
	/**
	 * @param fixed the fixed to set
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	/**
	 * @return if it is fixed
	 */
	public boolean isFixed() {
		return fixed;
	}
	/**
	 * @return the size of the vertex
	 */
	public double getSize() {
		return size;
	}

	/**
	 * Sets the size of the vertex
	 * @param size
	 */
	public void setSize(double size) {
		this.size = size;
		((RectangularShape) this.shape).setFrameFromCenter(0,0,size,size);
	}

	/**
	 * @return the shape of the vertex
	 */
	public Shape getShape() {
		return shape;
	}
	
	public String getShapeString() {
		if (this.shape instanceof Rectangle2D) {
			return "square";
		}
		
		return "circle";
	}

	/**
	 * Creates the shape of the vertex given its description in a string
	 * @param shapeString "square" or "circle"
	 */
	public void setShape(String shapeString) {
		Shape newShape;
		if (shapeString.startsWith("square")){
			Rectangle2D rectangle = new Rectangle2D.Float();
			rectangle.setFrameFromCenter(0,0,size,size);
			newShape = rectangle;
		}
		else
		{
			Ellipse2D ellipse = new Ellipse2D.Float();
			ellipse.setFrameFromCenter(0,0,size,size);
			newShape = ellipse; 
		}
		this.shape = newShape;
	}
	
	/**
	 * Sets the shape of the vertex to an already created one
	 * @param shape
	 */
	public void setShape(Shape shape) {
		this.shape = shape;
	}

	/**
	 * General purpose variable 1 getter
	 * @return
	 */
	public String getVar1() {
		return var1;
	}
	
	/**
	 * General purpose variable 1 setter
	 * @return
	 */
	public void setVar1(String var1) {
		this.var1 = var1;
	}

	/**
	 * General purpose variable 2 getter
	 * @return
	 */
	public String getVar2() {
		return var2;
	}

	/**
	 * General purpose variable 2 setter
	 * @return
	 */
	public void setVar2(String var2) {
		this.var2 = var2;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return a Point2D with the position of the vertex
	 */
	public Point2D getPosition(){
		return position;
	}
	
	/**
	 * Sets the position of the vertex to an already created one
	 * @param position
	 */
	public void setPosition(Point2D position){
		this.position = (Point2D) position.clone();
	}
	
	/**
	 * Sets the position of the vertex to the coordinates
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y){
		this.position = new Point2D.Double(x,y);
	}

	public String toString() {
		return Integer.toString(id);
	}

	@Override
	public int compareTo(Vertex v) {
		// Comparison based on ID
		
		if(this.id < v.getId())
			return -1;
		else if(this.id > v.getId())
			return 1;
		
		return 0;
	}
	
	public boolean equals(Object o) {
		if( !(o instanceof Vertex) || compareTo((Vertex) o) != 0 )
			return false;
		
		return true;
	}

}
