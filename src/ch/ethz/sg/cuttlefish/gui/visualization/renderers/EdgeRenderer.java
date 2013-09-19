package ch.ethz.sg.cuttlefish.gui.visualization.renderers;

import java.awt.geom.Point2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import ch.ethz.sg.cuttlefish.gui.visualization.Constants;
import ch.ethz.sg.cuttlefish.gui.visualization.NetworkRenderer;
import ch.ethz.sg.cuttlefish.gui.visualization.Utilities;
import ch.ethz.sg.cuttlefish.gui.visualization.mouse.IntersectingShapePickSupport;
import ch.ethz.sg.cuttlefish.networks.Edge;

public class EdgeRenderer extends ShapeRenderer {

	private final int DLIST_LINE;
	private final int DLIST_LOOP;
	private final int DLIST_ARROW;

	public final static Point2D QUADCURVE_CTRL_POINT = new Point2D.Double(0.5,
			40);

	private Point2D incompFrom, incompTo;

	public EdgeRenderer(GLAutoDrawable drawable, NetworkRenderer networkRenderer) {
		super(drawable, networkRenderer);
		this.DLIST_LINE = getDisplayListIndex(Constants.LINE_STRAIGHT);
		this.DLIST_LOOP = getDisplayListIndex(Constants.LINE_LOOP);
		this.DLIST_ARROW = getDisplayListIndex(Constants.SHAPE_ARROW);

		// Display list initialization
		glList_Line();
		glList_Loop();
		glList_Arrow();
	}

	private void glList_Line() {
		gl.glNewList(DLIST_LINE, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(0, 0);
		gl.glVertex2d(1, 0);
		gl.glEnd();
		gl.glEndList();
	}

	private void glList_Loop() {
		double radius = 1;
		double sx = 0;
		double sy = -1;
		double x, y, rad;
		int segments = 50;

		gl.glNewList(DLIST_LOOP, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_LINE_STRIP);

		for (int i = 0; i <= segments; i++) {
			rad = 2 * Math.PI * i / segments;
			x = sx + Math.cos(rad) * radius;
			y = sy + Math.sin(rad) * radius;
			gl.glVertex2d(x, y);
		}

		gl.glEnd();
		gl.glEndList();
	}

	private void glList_Arrow() {
		double w = 14;
		double h = 5.6;
		double t = 2.8;

		// Display List initialization
		gl.glNewList(DLIST_ARROW, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2d(0, 0);
		gl.glVertex2d(-w, -h);
		gl.glVertex2d(t - w, 0);
		gl.glVertex2d(-w, h);
		gl.glEnd();
		gl.glEndList();
	}

	@Override
	public void render() {

		for (Edge edge : renderer.getEdges()) {
			if (edge.isExcluded())
				continue;

			float lineWidth = (float) edge.getWidth();
			float[] color = edge.getColor().getColorComponents(null);
			gl.glColor3f(color[0], color[1], color[2]);
			gl.glLineWidth(lineWidth);

			if (edge.isLoop())
				drawLoop(edge);

			else if (renderer.getNetwork().getEdgeShape()
					.equalsIgnoreCase(Constants.LINE_CURVED))
				drawCurve(edge);

			else
				drawLine(edge);
		}

		if (incompFrom != null && incompTo != null)
			drawIncompleteEdge();
	}

	// Draws a quad curve to represent an edge.
	// http://antoineleclair.ca/2011/08/27/understanding-quadratic-bezier-curves/
	private void drawCurve(Edge e) {
		Point2D from = e.getSource().getPosition();
		Point2D to = e.getTarget().getPosition();
		double fx = from.getX();
		double fy = from.getY();
		double tx = to.getX();
		double ty = to.getY();
		double a = Utilities.calculateAngle(from, to);
		double h = QUADCURVE_CTRL_POINT.getY() / scaleFactor;

		// Calculate Control Point
		double cx = (fx + tx) / 2 - h * Math.sin(a);
		double cy = (fy + ty) / 2 + h * Math.cos(a);
		float t = 0;
		double x = 0, y = 0;
		Point2D c = new Point2D.Double(cx, cy);

		gl.glBegin(GL.GL_LINE_STRIP);
		while (t <= 1) {
			t += 0.01;
			double c1x = fx + (cx - fx) * t;
			double c1y = fy + (cy - fy) * t;
			double c2x = cx + (tx - cx) * t;
			double c2y = cy + (ty - cy) * t;
			x = c1x + (c2x - c1x) * t;
			y = c1y + (c2y - c1y) * t;
			gl.glVertex2d(x, y);

			if (renderer.getNetwork().isDirected()) {
				// store the point to direct the arrow
				if (0.495 < t && t < 0.505)
					c.setLocation(x, y);

				if (IntersectingShapePickSupport.containsPoint(e.getTarget(),
						new Point2D.Double(x, y), scaleFactor)) {
					// hit border!
					break;
				}
			}
		}
		gl.glEnd();

		if (renderer.getNetwork().isDirected()) {
			// draw arrow
			Point2D b = new Point2D.Double(x, y);
			double arrowAngle = Utilities.calculateAngle(c, b);
			gl.glPushMatrix();
			gl.glTranslated(x, y, 0);
			gl.glRotated(Math.toDegrees(arrowAngle), 0, 0, 1);
			gl.glScaled(1 / scaleFactor, 1 / scaleFactor, 1);
			gl.glCallList(DLIST_ARROW);
			gl.glPopMatrix();
		}
	}

	private void drawLine(Edge e) {
		Point2D from = e.getSource().getPosition();
		Point2D to = e.getTarget().getPosition();
		double a = Math.toDegrees(Utilities.calculateAngle(from, to));
		double dist = from.distance(to);

		// draw line
		gl.glPushMatrix();
		gl.glTranslated(from.getX(), from.getY(), 0);
		gl.glScaled(dist, dist, 1);
		gl.glRotated(a, 0, 0, 1);
		gl.glCallList(DLIST_LINE);
		gl.glPopMatrix();

		if (renderer.getNetwork().isDirected()) {
			// draw arrow on line
			Point2D b = Utilities.getBorderPoint(e.getTarget(), from,
					scaleFactor);
			gl.glPushMatrix();
			gl.glTranslated(b.getX(), b.getY(), 0);
			gl.glRotated(a, 0, 0, 1);
			gl.glScaled(1 / scaleFactor, 1 / scaleFactor, 1);
			gl.glCallList(DLIST_ARROW);
			gl.glPopMatrix();
		}
	}

	public void drawEdgeToPoint(Point2D from, Point2D to) {
		incompFrom = from;
		incompTo = to;
	}

	private void drawIncompleteEdge() {
		double a = Math.toDegrees(Utilities
				.calculateAngle(incompFrom, incompTo));
		double dist = incompFrom.distance(incompTo);

		gl.glColor3f((153.0f / 255), 0, 0);
		gl.glLineWidth(1f);

		// draw line
		gl.glPushMatrix();
		gl.glTranslated(incompFrom.getX(), incompFrom.getY(), 0);
		gl.glScaled(dist, dist, 1);
		gl.glRotated(a, 0, 0, 1);
		gl.glCallList(DLIST_LINE);
		gl.glPopMatrix();

		// draw arrow on line
		gl.glPushMatrix();
		gl.glTranslated(incompTo.getX(), incompTo.getY(), 0);
		gl.glRotated(a, 0, 0, 1);
		gl.glScaled(1 / scaleFactor, 1 / scaleFactor, 1);
		gl.glCallList(DLIST_ARROW);
		gl.glPopMatrix();
	}

	private void drawLoop(Edge e) {
		double tx = e.getSource().getPosition().getX();
		double ty = e.getSource().getPosition().getY();
		double sf = e.getSource().getSize() / scaleFactor;

		gl.glPushMatrix();
		gl.glTranslated(tx, ty, 0);
		gl.glScaled(sf, sf, 1);
		gl.glCallList(DLIST_LOOP);
		gl.glPopMatrix();

		if (renderer.getNetwork().isDirected()) {
			// TODO: draw arrow on loop?
		}
	}
}
