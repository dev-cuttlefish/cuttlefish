package ch.ethz.sg.cuttlefish.misc;

import java.awt.Color;

public class Vertex {
	
	
	private int idTemp;
	private int id;
	private String label = null;
	private Color fillColor = Color.BLACK;
	private Color color  = Color.BLACK;
	private double weight = 1;
	private double radius = 10;
	private boolean shadowed;
	private double width = 1;
	private boolean excluded;
	private boolean fixed;
	
	public Vertex(int id){
		this.id = id;
	}
	
	public Vertex() {
		this.id = -1;  //Anonymous vertex
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
	 * @param weight the weight to set
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
	 * @param radius the radius to set
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}
	/**
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
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
	 * @param width the width to set
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


}
