package ch.ethz.sg.cuttlefish.gui.visualization.renderers;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import ch.ethz.sg.cuttlefish.gui.visualization.Constants;
import ch.ethz.sg.cuttlefish.gui.visualization.NetworkRenderer;
import ch.ethz.sg.cuttlefish.gui.visualization.geometry.Utilities;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

import com.jogamp.opengl.util.gl2.GLUT;

public class LabelRenderer {

	private final int VERTEX_LABEL_OFFSET = 10;
	private final int TEXT_FONT = GLUT.STROKE_ROMAN;
	private final double TEXT_SIZE = 9;
	private final boolean HIDE_LABELS = false;

	private GL2 gl;
	private GLUT glut;
	private NetworkRenderer renderer;
	private double scaleFactor = 1;

	public LabelRenderer(GLAutoDrawable drawable, NetworkRenderer renderer) {
		this.renderer = renderer;
		gl = drawable.getGL().getGL2();
		glut = new GLUT();
	}

	public void drawLabels() {
		if (HIDE_LABELS)
			return;

		for (Vertex v : renderer.getVertices()) {
			if (!v.isExcluded() && v.getLabel() != null
					&& !v.getLabel().isEmpty())
				drawLabel(v);
		}

		for (Edge e : renderer.getEdges()) {
			if (!e.isExcluded() && e.getLabel() != null
					&& !e.getLabel().isEmpty() && !e.isLoop())
				drawLabel(e);
		}
	}

	private void drawLabel(Vertex v) {
		String text = v.getLabel();
		float[] c = v.getBorderColor().getColorComponents(null);
		Point2D pos = v.getPosition();
		double x = pos.getX() + v.getSize() / scaleFactor;
		double y = pos.getY() + (v.getSize() + VERTEX_LABEL_OFFSET)
				/ scaleFactor;
		pos.setLocation(x, y);

		gl.glColor3f(c[0], c[1], c[2]);
		renderText(text, pos);
	}

	private void drawLabel(Edge e) {
		double lineWidth = e.getWidth();
		if (EdgeRenderer.EDGE_WEIGHT_AS_WIDTH && e.getWeight() > lineWidth)
			lineWidth = e.getWeight();

		Point2D from, to;
		if (e.getSource().getPosition().getX() < e.getTarget().getPosition()
				.getX()) {
			from = e.getSource().getPosition();
			to = e.getTarget().getPosition();
		} else {
			from = e.getTarget().getPosition();
			to = e.getSource().getPosition();
		}

		double angle = Utilities.calculateAngle(from, to);
		double l = getTextLength(e.getLabel());
		double d = from.distance(to);
		double x = 0.5 * (d - l);
		double y = 0;

		if (e.getShape().equalsIgnoreCase(Constants.LINE_STRAIGHT)) {
			y = 0.5 + (0.5 * lineWidth / scaleFactor);
		}

		float[] c = e.getColor().getColorComponents(null);
		gl.glColor3f(c[0], c[1], c[2]);
		gl.glPushMatrix();
		gl.glTranslated(from.getX(), from.getY(), 0);
		gl.glRotated(Math.toDegrees(angle), 0, 0, 1);
		renderText(e.getLabel(), new Point2D.Double(x, -y));
		gl.glPopMatrix();
	}

	private void renderText(String text, Point2D pos) {
		double scale = (TEXT_SIZE * 0.01) / scaleFactor;

		gl.glPushMatrix();
		gl.glLineWidth(1f);
		gl.glTranslated(pos.getX(), pos.getY(), 0);
		gl.glRotated(180, 1, 0, 0);
		gl.glScaled(scale, scale, 1);
		glut.glutStrokeString(TEXT_FONT, text);
		gl.glPopMatrix();
	}

	private double getTextLength(String text) {
		return glut.glutStrokeLength(TEXT_FONT, text) * (TEXT_SIZE * 0.01)
				/ scaleFactor;
	}

	public void scale(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}
}
