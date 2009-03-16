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
import java.awt.geom.Rectangle2D;

public class Vertex {
	
	
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
	
	public Vertex(int id){
		this.id = id;
		Ellipse2D ellipse = new Ellipse2D.Float();
		ellipse.setFrameFromCenter(0,0,size,size);
		shape = ellipse;
	}

	public Vertex(int id, String label){
		this.id = id;
		this.label = label;
		Ellipse2D ellipse = new Ellipse2D.Float();
		ellipse.setFrameFromCenter(0,0,size,size);
		shape = ellipse;
	}

	public Vertex() {
		this.id = -1;  //Anonymous vertex
		Ellipse2D ellipse = new Ellipse2D.Float();
		ellipse.setFrameFromCenter(0,0,size,size);
		shape = ellipse;
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
	 * @return the fixed
	 */
	public boolean isFixed() {
		return fixed;
	}
	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Shape getShape() {
		return shape;
	}

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
	
	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public String getVar1() {
		return var1;
	}

	public void setVar1(String var1) {
		this.var1 = var1;
	}

	public String getVar2() {
		return var2;
	}

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


}
