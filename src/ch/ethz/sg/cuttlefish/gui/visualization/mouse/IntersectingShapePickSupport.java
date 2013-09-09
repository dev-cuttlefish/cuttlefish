package ch.ethz.sg.cuttlefish.gui.visualization.mouse;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import ch.ethz.sg.cuttlefish.gui.visualization.Constants;
import ch.ethz.sg.cuttlefish.gui.visualization.NetworkRenderer;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class IntersectingShapePickSupport implements PickSupport {

	private NetworkRenderer nr;

	public IntersectingShapePickSupport(NetworkRenderer nr) {
		this.nr = nr;
	}

	public Vertex pickVertex(Point2D p) {
		Vertex picked = null;

		for (Vertex v : nr.getVertices()) {
			if (IntersectingShapePickSupport.containsPoint(v, p,
					nr.getScaleFactor())) {
				picked = v;
				break;
			}
		}

		return picked;
	}

	public Edge pickEdge(Point2D p) {
		Edge picked = null;

		for (Edge e : nr.getEdges()) {
			if (IntersectingShapePickSupport.containsPoint(e, p,
					nr.getScaleFactor())) {
				picked = e;
				break;
			}
		}

		return picked;
	}

	public static boolean containsPoint(Edge edge, Point2D p, double scaleFactor) {
		Shape edgeShape = null;

		if (edge.getShape().equalsIgnoreCase(Constants.LINE_STRAIGHT)) {
			edgeShape = new Line2D.Double(edge.getSource().getPosition(), edge
					.getTarget().getPosition());

		} else if (edge.getShape().equalsIgnoreCase(Constants.LINE_CURVED)) {

		} else if (edge.getShape().equalsIgnoreCase(Constants.LINE_LOOP)) {

		}

		// Create a small circular area around point
		double pickRadius = 2;
		double px = p.getX();
		double py = p.getY();
		Rectangle2D pickArea = new Rectangle2D.Double(px - pickRadius, py
				- pickRadius, 2 * pickRadius, 2 * pickRadius);

		return edgeShape.intersects(pickArea);
	}

	public static boolean containsPoint(Vertex vertex, Point2D p,
			double scaleFactor) {
		RectangularShape shape = null;
		double size = vertex.getSize() / scaleFactor;
		double x = vertex.getPosition().getX() - size;
		double y = vertex.getPosition().getY() - size;

		if (vertex.getShape().equals(Constants.SHAPE_DISK)) {
			shape = new Ellipse2D.Double(x, y, 2 * size, 2 * size);

		} else if (vertex.getShape().equals(Constants.SHAPE_SQUARE)) {
			shape = new Rectangle2D.Double(x, y, 2 * size, 2 * size);
		}

		return shape.contains(p);
	}

}
