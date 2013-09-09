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
import java.awt.geom.Point2D;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

import ch.ethz.sg.cuttlefish.gui.visualization.Constants;

/**
 * Class that stores the information associated with a vertex.
 * 
 * The class wraps the org.gephi.graph.api.Node class which is
 * compatible/offered by Gephi.
 * 
 * @author david
 */
public class Vertex implements Comparable<Vertex> {

	// Attribute keys that extend the NodeData data structure
	private final static String ATTR_ID = "vertex_int_id";
	private final static String ATTR_IS_ROOT = "vertex_boolean_root";
	private final static String ATTR_IS_EXCLUDED = "vertex_boolean_excluded";
	private final static String ATTR_VAR1 = "vertex_string_var1";
	private final static String ATTR_VAR2 = "vertex_string_var2";
	private final static String ATTR_SHAPE_TYPE = "vertex_string_shapeType";
	private final static String ATTR_IS_SHADOWED = "vertex_boolean_shadowed";
	private final static String ATTR_WIDTH = "vertex_int_width";
	private final static String ATTR_BORDER_R = "vertex_float_border_color_R";
	private final static String ATTR_BORDER_G = "vertex_float_border_color_G";
	private final static String ATTR_BORDER_B = "vertex_float_border_color_B";

	static {
		addAttribute(ATTR_ID, AttributeType.INT);
		addAttribute(ATTR_IS_ROOT, AttributeType.BOOLEAN);
		addAttribute(ATTR_IS_EXCLUDED, AttributeType.BOOLEAN);
		addAttribute(ATTR_VAR1, AttributeType.STRING);
		addAttribute(ATTR_VAR2, AttributeType.STRING);
		addAttribute(ATTR_SHAPE_TYPE, AttributeType.STRING);
		addAttribute(ATTR_IS_SHADOWED, AttributeType.BOOLEAN);
		addAttribute(ATTR_WIDTH, AttributeType.INT);
		addAttribute(ATTR_BORDER_R, AttributeType.FLOAT);
		addAttribute(ATTR_BORDER_G, AttributeType.FLOAT);
		addAttribute(ATTR_BORDER_B, AttributeType.FLOAT);
	}

	public static void addAttribute(String name, AttributeType type) {
		Lookup.getDefault().lookup(AttributeController.class).getModel()
				.getNodeTable().addColumn(name, type);
	}

	// Defaults
	private final int DEFAULT_ID = -1;
	private final int DEFAULT_WIDTH = 1;
	private final int DEFAULT_SIZE = 10;
	private final Color DEFAULT_BORDER_COLOR = Color.darkGray;
	private final Color DEFAULT_FILL_COLOR = Color.lightGray;
	private final String DEFAULT_VAR1 = null;
	private final String DEFAULT_VAR2 = null;
	private final String DEFAULT_SHAPE = Constants.SHAPE_DISK;
	private final boolean DEFAULT_ROOT = false;
	private final boolean DEFAULT_EXCLUDED = false;
	private final boolean DEFAULT_SHADOWED = false;

	Node internalNode;

	public Node getInternalNode() {
		return internalNode;
	}

	public void defaults() {
		setId(DEFAULT_ID);
		setIsRoot(DEFAULT_ROOT);
		setExcluded(DEFAULT_EXCLUDED);
		setVar1(DEFAULT_VAR1);
		setVar2(DEFAULT_VAR2);
		setShape(DEFAULT_SHAPE);
		setShadowed(DEFAULT_SHADOWED);
		setWidth(DEFAULT_WIDTH);
		setSize(DEFAULT_SIZE);
		setBorderColor(DEFAULT_BORDER_COLOR);
		setFillColor(DEFAULT_FILL_COLOR);
	}

	/**
	 * General constructor for a vertex
	 * 
	 * @param id
	 */
	public Vertex(int id) {
		this.internalNode = Lookup.getDefault().lookup(GraphController.class)
				.getModel().factory().newNode();

		defaults();
		setId(id);
	}

	/**
	 * Constructor for a vertex with a label.
	 * 
	 * @param id
	 * @param label
	 */
	public Vertex(int id, String label) {
		this.internalNode = Lookup.getDefault().lookup(GraphController.class)
				.getModel().factory().newNode(label);

		defaults();
		setId(id);
		setLabel(label);
	}

	/**
	 * Constructor for an anonymous vertex
	 */
	public Vertex() {
		this.internalNode = Lookup.getDefault().lookup(GraphController.class)
				.getModel().factory().newNode();

		defaults();
	}

	// Used to wrap Gephi nodes to Cuttlefish vertices
	public Vertex(Node node) {
		this.internalNode = node;
	}

	// TODO ilias: Static access to internal node?
	/*
	 * It might be useful to provide static access to a node's attributes by
	 * passing the Node argument to a static method. For example, when the
	 * BrowsableNetwork needs to modify Nodes.
	 */

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.internalNode.getAttributes().setValue(ATTR_ID, id);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return (Integer) this.internalNode.getAttributes().getValue(ATTR_ID);
	}

	/**
	 * @param b
	 *            boolean specifying whether the vertex is a root vertex
	 */
	public void setIsRoot(boolean b) {
		this.internalNode.getAttributes().setValue(ATTR_IS_ROOT, b);
	}

	/**
	 * @return isRoot
	 */
	public boolean isRoot() {
		return (Boolean) this.internalNode.getAttributes().getValue(
				ATTR_IS_ROOT);
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		setLabel(internalNode, label);
	}

	public static void setLabel(Node node, String label) {
		node.getNodeData().setLabel(label);
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return this.internalNode.getNodeData().getLabel();
	}

	public static void copyIDToLabel(Node node) {
		Integer id = (Integer) node.getAttributes().getValue(ATTR_ID);
		setLabel(node, id.toString());
	}

	/**
	 * @param fillColor
	 *            the fillColor to set
	 */
	public void setFillColor(Color fillColor) {
		setFillColor(internalNode, fillColor);
	}

	public static void setFillColor(Node node, Color fillColor) {
		float[] comp = fillColor.getRGBColorComponents(null);

		node.getNodeData().setR(comp[0]);
		node.getNodeData().setG(comp[1]);
		node.getNodeData().setB(comp[2]);
	}

	/**
	 * @return the fillColor
	 */
	public Color getFillColor() {
		float r = this.internalNode.getNodeData().r();
		float g = this.internalNode.getNodeData().g();
		float b = this.internalNode.getNodeData().b();
		return new Color(r, g, b);
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setBorderColor(Color borderColor) {
		setBorderColor(internalNode, borderColor);
	}

	public static void setBorderColor(Node node, Color borderColor) {
		float[] comp = borderColor.getRGBColorComponents(null);

		node.getAttributes().setValue(ATTR_BORDER_R, comp[0]);
		node.getAttributes().setValue(ATTR_BORDER_G, comp[1]);
		node.getAttributes().setValue(ATTR_BORDER_B, comp[2]);
	}

	/**
	 * @return the color
	 */
	public Color getBorderColor() {
		float r = (Float) this.internalNode.getAttributes().getValue(
				ATTR_BORDER_R);
		float g = (Float) this.internalNode.getAttributes().getValue(
				ATTR_BORDER_G);
		float b = (Float) this.internalNode.getAttributes().getValue(
				ATTR_BORDER_B);

		return new Color(r, g, b);
	}

	/**
	 * @param shadowed
	 *            the shadowed to set
	 */
	public void setShadowed(boolean shadowed) {
		this.internalNode.getAttributes().setValue(ATTR_IS_SHADOWED, shadowed);
	}

	/**
	 * @return the shadowed
	 */
	public boolean isShadowed() {
		return (Boolean) this.internalNode.getAttributes().getValue(
				ATTR_IS_SHADOWED);
	}

	/**
	 * @param excluded
	 *            the excluded to set
	 */
	public void setExcluded(boolean excluded) {
		this.internalNode.getAttributes().setValue(ATTR_IS_EXCLUDED, excluded);
	}

	/**
	 * @return the exclude
	 */
	public boolean isExcluded() {
		return (Boolean) this.internalNode.getAttributes().getValue(
				ATTR_IS_EXCLUDED);
	}

	/**
	 * @param fixed
	 *            the fixed to set
	 */
	public void setFixed(boolean fixed) {
		this.internalNode.getNodeData().setFixed(fixed);
	}

	/**
	 * @return if it is fixed
	 */
	public boolean isFixed() {
		return this.internalNode.getNodeData().isFixed();
	}

	/**
	 * @return the size of the vertex
	 */
	public double getSize() {
		return this.internalNode.getNodeData().getSize();
	}

	/**
	 * Sets the size of the vertex
	 * 
	 * @param size
	 */
	public void setSize(double size) {
		this.internalNode.getNodeData().setSize((float) size);
	}

	/**
	 * Creates the shape of the vertex given its description in a string
	 * 
	 * @param shapeString
	 *            "square" or "circle"
	 */

	public void setShape(String shapeType) {
		if (shapeType.equalsIgnoreCase(Constants.SHAPE_DISK)
				|| shapeType.equalsIgnoreCase(Constants.SHAPE_SQUARE)) {
			this.internalNode.getAttributes().setValue(ATTR_SHAPE_TYPE,
					shapeType);
		}
	}

	public String getShape() {
		return (String) this.internalNode.getAttributes().getValue(
				ATTR_SHAPE_TYPE);
	}

	/**
	 * General purpose variable 1 getter
	 * 
	 * @return
	 */
	public String getVar1() {
		return (String) this.internalNode.getAttributes().getValue(ATTR_VAR1);
	}

	/**
	 * General purpose variable 1 setter
	 * 
	 * @return
	 */
	public void setVar1(String var1) {
		this.internalNode.getAttributes().setValue(ATTR_VAR1, var1);
	}

	/**
	 * General purpose variable 2 getter
	 * 
	 * @return
	 */
	public String getVar2() {
		return (String) this.internalNode.getAttributes().getValue(ATTR_VAR2);
	}

	/**
	 * General purpose variable 2 setter
	 * 
	 * @return
	 */
	public void setVar2(String var2) {
		this.internalNode.getAttributes().setValue(ATTR_VAR2, var2);
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.internalNode.getAttributes().setValue(ATTR_WIDTH, width);
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return (Integer) this.internalNode.getAttributes().getValue(ATTR_WIDTH);
	}

	/**
	 * @return a Point2D with the position of the vertex
	 */
	public Point2D getPosition() {
		float x, y;
		x = this.internalNode.getNodeData().x();
		y = this.internalNode.getNodeData().y();
		return new Point2D.Float(x, y);
	}

	/**
	 * Sets the position of the vertex to an already created one
	 * 
	 * @param position
	 */
	public void setPosition(Point2D position) {
		this.internalNode.getNodeData().setX((float) position.getX());
		this.internalNode.getNodeData().setY((float) position.getY());
		this.internalNode.getNodeData().setZ(0);
	}

	/**
	 * Sets the position of the vertex to the coordinates
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y) {
		this.internalNode.getNodeData().setX((float) x);
		this.internalNode.getNodeData().setY((float) y);
	}

	public String toString() {
		return Integer.toString(getId());
	}

	@Override
	public int compareTo(Vertex v) {
		// Comparison based on ID

		if (this.getId() < v.getId())
			return -1;
		else if (this.getId() > v.getId())
			return 1;

		return 0;
	}

	@Override
	public int hashCode() {
		return internalNode.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Vertex))
			return false;

		Vertex other = (Vertex) obj;
		return internalNode.equals(other.internalNode);
	}
}
