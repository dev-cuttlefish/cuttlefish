package ch.ethz.sg.cuttlefish.gui.visualization.mouse;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

import ch.ethz.sg.cuttlefish.gui.visualization.Constants;
import ch.ethz.sg.cuttlefish.gui.visualization.NetworkRenderer;
import ch.ethz.sg.cuttlefish.gui.visualization.Utilities;
import ch.ethz.sg.cuttlefish.gui.visualization.renderers.EdgeRenderer;
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
			if (containsPoint(v, p, nr.getScaleFactor())) {
				picked = v;
				break;
			}
		}

		return picked;
	}

	public Edge pickEdge(Point2D p) {
		Edge picked = null;

		for (Edge e : nr.getEdges()) {
			if (containsPoint(e, p, nr.getScaleFactor())) {
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
			Point2D from = edge.getSource().getPosition();
			Point2D to = edge.getTarget().getPosition();
			double fx = from.getX();
			double fy = from.getY();
			double tx = to.getX();
			double ty = to.getY();
			double a = Utilities.calculateAngle(from, to);
			double h = EdgeRenderer.QUADCURVE_CTRL_POINT.getY() / scaleFactor;

			// Calculate Control Point
			double cx = (fx + tx) / 2 - h * Math.sin(a);
			double cy = (fy + ty) / 2 + h * Math.cos(a);

			edgeShape = new QuadCurve2D.Double(fx, fy, cx, cy, tx, ty);

		} else if (edge.getShape().equalsIgnoreCase(Constants.LINE_LOOP)) {
			// TODO ilias: Calculate shape for loop
		}

		// Create a small rectangular area around point, and use it
		// to check for intersection with the line of the edge
		double pickRadius = 1;
		double px = p.getX();
		double py = p.getY();
		Rectangle2D pickArea = new Rectangle2D.Double(px - pickRadius, py
				- pickRadius, 2 * pickRadius, 2 * pickRadius);

		return edgeShape.intersects(pickArea);
	}

	public static boolean containsPoint(Vertex vertex, Point2D p,
			double scaleFactor) {
		Shape vertexShape = null;
		double size = vertex.getSize() / scaleFactor;
		double x = vertex.getPosition().getX() - size;
		double y = vertex.getPosition().getY() - size;

		if (vertex.getShape().equals(Constants.SHAPE_DISK)) {
			vertexShape = new Ellipse2D.Double(x, y, 2 * size, 2 * size);

		} else if (vertex.getShape().equals(Constants.SHAPE_SQUARE)) {
			vertexShape = new Rectangle2D.Double(x, y, 2 * size, 2 * size);
		}

		return vertexShape.contains(p);
	}

}
