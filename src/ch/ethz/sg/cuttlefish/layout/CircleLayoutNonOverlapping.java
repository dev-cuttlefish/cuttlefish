package ch.ethz.sg.cuttlefish.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import ch.ethz.sg.cuttlefish.misc.Vertex;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;

public class CircleLayoutNonOverlapping<V, E> extends AbstractLayout<V, E> {

	private double radius;
	private List<V> vertexOrderedList;

	Map<V, CircleVertexData> circleVertexDataMap = LazyMap.decorate(
			new HashMap<V, CircleVertexData>(),
			new Factory<CircleVertexData>() {
				public CircleVertexData create() {
					return new CircleVertexData();
				}
			});

	/**
	 * Creates an instance for the specified graph.
	 */
	public CircleLayoutNonOverlapping(Graph<V, E> g) {
		super(g);
	}

	/**
	 * Returns the radius of the circle.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets the radius of the circle. Must be called before {@code initialize()}
	 * is called.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Sets the order of the vertices in the layout according to the ordering
	 * specified by {@code comparator}.
	 */
	public void setVertexOrder(Comparator<V> comparator) {
		if (vertexOrderedList == null)
			vertexOrderedList = new ArrayList<V>(getGraph().getVertices());
		Collections.sort(vertexOrderedList, comparator);
	}

	/**
	 * Sets the order of the vertices in the layout according to the ordering of
	 * {@code vertex_list}.
	 */
	public void setVertexOrder(List<V> vertex_list) {
		if (!vertex_list.containsAll(getGraph().getVertices()))
			throw new IllegalArgumentException("Supplied list must include "
					+ "all vertices of the graph");
		this.vertexOrderedList = vertex_list;
	}

	public void reset() {
		initialize();
	}

	public void initialize() {
		Dimension d = getSize();
		boolean layoutFinished = false;

		while (d != null && !layoutFinished) {
			if (vertexOrderedList == null)
				setVertexOrder(new ArrayList<V>(getGraph().getVertices()));

			double height = d.getHeight();
			double width = d.getWidth();

			if (radius <= 0) {
				radius = 0.45 * (height < width ? height : width);
			}

			double angle, angleSum = 0;
			for (V v : vertexOrderedList) {
				double r = ((Vertex) v).getSize() * Math.sqrt(2);
				angle = Math.asin(r / radius) * 2;
				angleSum += angle;
				getCircleData(v).setAngle(angle);
			}

			double delta = (2 * Math.PI - angleSum) / vertexOrderedList.size();
			
			if(delta < 0) {
				// Vertices overlap within the current circle.
				// Radius must be increased.
				radius *= 1.1;
				continue;
			}
			
			int count = 0;
			angle = 0;
			for (V v : vertexOrderedList) {
				Point2D coord = transform(v);
				
				if(count++ > 0) {
					angle += getCircleData(v).getAngle() / 2;
				}

				coord.setLocation(Math.cos(angle) * radius + width / 2,
						Math.sin(angle) * radius + height / 2);

				angle += getCircleData(v).getAngle() / 2 + delta;
			}
			
			layoutFinished = true;
		}
	}

	protected CircleVertexData getCircleData(V v) {
		return circleVertexDataMap.get(v);
	}

	protected static class CircleVertexData {
		private double angle;

		protected double getAngle() {
			return angle;
		}

		protected void setAngle(double angle) {
			this.angle = angle;
		}

		@Override
		public String toString() {
			return "CircleVertexData: angle = " + angle;
		}
	}
}
