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

package ch.ethz.sg.cuttlefish.networks;

import java.awt.Color;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.GraphController;
import org.openide.util.Lookup;

import ch.ethz.sg.cuttlefish.gui.visualization.Constants;

/**
 * Class that stores the information related to the edges of the network.
 * 
 * @author David Garcia Becerra
 */
public class Edge implements Comparable<Edge> {

	// Attribute keys that extend the EdgeData data structure
	private final static String ATTR_WIDTH = "edge_double_width";
	private final static String ATTR_CURVE_TYPE = "edge_string_curveType";
	private final static String ATTR_VAR1 = "edge_string_var1";
	private final static String ATTR_VAR2 = "edge_string_var2";
	private final static String ATTR_IS_EXCLUDED = "edge_boolean_excluded";
	private final static String ATTR_ID = "edge_int_id";

	static {
		addAttribute(Edge.ATTR_ID, AttributeType.INT);
		addAttribute(Edge.ATTR_WIDTH, AttributeType.DOUBLE);
		addAttribute(Edge.ATTR_VAR1, AttributeType.STRING);
		addAttribute(Edge.ATTR_VAR2, AttributeType.STRING);
		addAttribute(Edge.ATTR_IS_EXCLUDED, AttributeType.BOOLEAN);
		addAttribute(Edge.ATTR_CURVE_TYPE, AttributeType.STRING);
	}

	public static void addAttribute(String name, AttributeType type) {
		Lookup.getDefault().lookup(AttributeController.class).getModel()
				.getEdgeTable().addColumn(name, type);
	}

	// Defaults
	private final int DEFAULT_ID = -1;
	private final int DEFAULT_WIDTH = 1;
	private final Color DEFAULT_COLOR = Color.darkGray;
	private final String DEFAULT_VAR1 = null;
	private final String DEFAULT_VAR2 = null;
	private final String DEFAULT_EDGE_TYPE = Constants.LINE_STRAIGHT;
	private final boolean DEFAULT_EXCLUDED = false;

	org.gephi.graph.api.Edge internalEdge;

	public org.gephi.graph.api.Edge getInternalEdge() {
		return internalEdge;
	}

	private void defaults() {
		setId(DEFAULT_ID);
		setWidth(DEFAULT_WIDTH);
		setVar1(DEFAULT_VAR1);
		setVar2(DEFAULT_VAR2);
		setExcluded(DEFAULT_EXCLUDED);
		setShape(DEFAULT_EDGE_TYPE);
		setColor(DEFAULT_COLOR);
	}

	public Edge(Vertex source, Vertex target) {
		this.internalEdge = Lookup.getDefault().lookup(GraphController.class)
				.getModel().factory()
				.newEdge(source.internalNode, target.internalNode);

		defaults();
	}

	public Edge(Vertex source, Vertex target, boolean directed) {
		this.internalEdge = Lookup.getDefault().lookup(GraphController.class)
				.getModel().factory()
				.newEdge(source.internalNode, target.internalNode, 1, directed);

		defaults();
	}

	public Edge(Vertex source, Vertex target, float weight, boolean directed) {
		this.internalEdge = Lookup
				.getDefault()
				.lookup(GraphController.class)
				.getModel()
				.factory()
				.newEdge(source.internalNode, target.internalNode, weight,
						directed);
		defaults();
	}

	public Edge(org.gephi.graph.api.Edge internal) {
		this.internalEdge = internal;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.internalEdge.getAttributes().setValue(Edge.ATTR_ID, id);
		this.internalEdge.getAttributes().setValue(Edge.ATTR_CURVE_TYPE,
				DEFAULT_EDGE_TYPE);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return (Integer) this.internalEdge.getAttributes().getValue(
				Edge.ATTR_ID);
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.internalEdge.getEdgeData().setLabel(label);
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return this.internalEdge.getEdgeData().getLabel();
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(Color color) {
		setColor(internalEdge, color);
	}

	public static void setColor(org.gephi.graph.api.Edge edge, Color color) {
		float[] rgb = color.getRGBColorComponents(null);
		edge.getEdgeData().setR(rgb[0]);
		edge.getEdgeData().setG(rgb[1]);
		edge.getEdgeData().setB(rgb[2]);
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		float r = this.internalEdge.getEdgeData().r();
		float g = this.internalEdge.getEdgeData().g();
		float b = this.internalEdge.getEdgeData().b();

		return new Color(r, g, b);
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(double width) {
		setWidth(internalEdge, width);
	}

	public static void setWidth(org.gephi.graph.api.Edge edge, double width) {
		edge.getAttributes().setValue(Edge.ATTR_WIDTH, width);
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return (Double) this.internalEdge.getAttributes().getValue(
				Edge.ATTR_WIDTH);
	}

	/**
	 * /**
	 * 
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(double weight) {
		this.internalEdge.setWeight((float) weight);
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return this.internalEdge.getWeight();
	}

	/**
	 * @param excluded
	 *            the excluded to set
	 */
	public void setExcluded(boolean excluded) {
		this.internalEdge.getAttributes().setValue(Edge.ATTR_IS_EXCLUDED,
				excluded);
	}

	/**
	 * @return the exclude
	 */
	public boolean isExcluded() {
		return (Boolean) this.internalEdge.getAttributes().getValue(
				Edge.ATTR_IS_EXCLUDED);
	}

	/**
	 * @param shape
	 *            the shape to set
	 */
	public void setShape(String lineType) {
		this.internalEdge.getAttributes().setValue(Edge.ATTR_CURVE_TYPE,
				lineType);
	}

	/**
	 * @return the shape
	 */
	public String getShape() {
		return (String) this.internalEdge.getAttributes().getValue(
				Edge.ATTR_CURVE_TYPE);
	}

	public boolean isCurved() {
		return getShape().equalsIgnoreCase(Constants.LINE_CURVED);
	}

	public boolean isLoop() {
		return this.internalEdge.isSelfLoop();
	}

	public boolean isDirected() {
		return this.internalEdge.isDirected();
	}

	public Vertex getSource() {
		return new Vertex(this.internalEdge.getSource());
	}

	public Vertex getTarget() {
		return new Vertex(this.internalEdge.getTarget());
	}

	/**
	 * @param var1
	 *            the var1 to set
	 */
	public void setVar1(String var1) {
		this.internalEdge.getAttributes().setValue(Edge.ATTR_VAR1, var1);
	}

	/**
	 * @return the var1
	 */
	public String getVar1() {
		return (String) this.internalEdge.getAttributes().getValue(
				Edge.ATTR_VAR1);
	}

	/**
	 * @param var2
	 *            the var2 to set
	 */
	public void setVar2(String var2) {
		this.internalEdge.getAttributes().setValue(Edge.ATTR_VAR2, var2);
	}

	/**
	 * @return the var2
	 */
	public String getVar2() {
		return (String) this.internalEdge.getAttributes().getValue(
				Edge.ATTR_VAR2);
	}

	/**
	 * Compare method to order the edges by weight
	 */
	@Override
	public int compareTo(Edge o) {
		if (this.getWeight() > o.getWeight())
			return 1;
		else if (this.getWeight() < o.getWeight())
			return -1;
		return 0;
	}

	@Override
	public int hashCode() {
		return internalEdge.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Edge))
			return false;

		Edge other = (Edge) obj;
		return internalEdge.equals(other.internalEdge);
	}

	public String toString() {
		return this.internalEdge.toString();
	}

}
