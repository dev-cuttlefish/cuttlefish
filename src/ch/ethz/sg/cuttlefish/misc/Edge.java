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

/**
 * Class that stores the information related to the edges of the network.
 * 
 * @author David Garcia Becerra
 */
public class Edge implements Comparable<Edge> {

	private int idTemp;
	private int id;
	private String label;
	private Color color = null;
	private double weight;
	private double width = 1;
	private boolean excluded = false;
	private String shape = "curved";
	private String var1 = null;
	private String var2 = null;

	public Edge() {
	}

	/**
	 * @param idTemp
	 *            the idTemp to set
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
	 * @param id
	 *            the id to set
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
	 * @param label
	 *            the label to set
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
	 * @param color
	 *            the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		if (color == null)
			return new Color(0, 0, 0);
		return color;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * /**
	 * 
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param excluded
	 *            the excluded to set
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
	 * @param fixed
	 *            the fixed to set
	 */

	/**
	 * @param shape
	 *            the shape to set
	 */
	public void setShape(String shape) {
		this.shape = shape;
	}

	/**
	 * @return the shape
	 */
	public String getShape() {
		return shape;
	}

	/**
	 * @param var1
	 *            the var1 to set
	 */
	public void setVar1(String var1) {
		this.var1 = var1;
	}

	/**
	 * @return the var1
	 */
	public String getVar1() {
		return var1;
	}

	/**
	 * @param var2
	 *            the var2 to set
	 */
	public void setVar2(String var2) {
		this.var2 = var2;
	}

	/**
	 * @return the var2
	 */
	public String getVar2() {
		return var2;
	}

	@Override
	/**
	 * Compare method to order the edges by weight
	 */
	public int compareTo(Edge o) {
		if (this.getWeight() > o.getWeight())
			return 1;
		else if (this.getWeight() < o.getWeight())
			return -1;
		return 0;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Edge))
			return false;

		if (this.id != ((Edge) o).getId())
			return false;

		return true;
	}

	public String toString() {
		return "Edge { ID=" + id + " | Weight="+weight+" }";
	}

}
