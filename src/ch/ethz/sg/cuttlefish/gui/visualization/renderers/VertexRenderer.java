package ch.ethz.sg.cuttlefish.gui.visualization.renderers;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import ch.ethz.sg.cuttlefish.gui.visualization.Constants;
import ch.ethz.sg.cuttlefish.gui.visualization.NetworkRenderer;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class VertexRenderer extends ShapeRenderer {

	private GLU glu;
	private GLUquadric quadric;
	private final int DISK_SLICES = 32;
	private final int DISK_RINGS = 2;
	private final int DLIST_DISK;
	private final int DLIST_SQUARE;

	public VertexRenderer(GLAutoDrawable drawable,
			NetworkRenderer networkRenderer) {
		super(drawable, networkRenderer);
		this.DLIST_DISK = getDisplayListIndex(Constants.SHAPE_DISK);
		this.DLIST_SQUARE = getDisplayListIndex(Constants.SHAPE_SQUARE);

		glu = new GLU();
		quadric = glu.gluNewQuadric();

		// Display List initialization
		gl.glNewList(DLIST_DISK, GL2.GL_COMPILE);
		glu.gluDisk(quadric, 0, 1, DISK_SLICES, DISK_RINGS);
		gl.glEndList();

		gl.glNewList(DLIST_SQUARE, GL2.GL_COMPILE);
		gl.glRectd(-1, -1, 1, 1);
		gl.glEndList();
	}

	@Override
	public void render() {

		for (Vertex v : renderer.getVertices()) {
			if (!v.isExcluded()) {
				drawVertex(v);
			}
		}
	}

	private void drawVertex(Vertex vertex) {
		double x = vertex.getPosition().getX();
		double y = vertex.getPosition().getY();
		float[] bc = vertex.getBorderColor().getColorComponents(null);
		float[] fc = vertex.getFillColor().getColorComponents(null);
		int listIndex = getDisplayListIndex(vertex.getShape());

		double bs = vertex.getSize() / scaleFactor;
		double fs = bs - vertex.getWidth() / scaleFactor;

		// border
		gl.glPushMatrix();
		gl.glTranslated(x, y, 0);
		gl.glScaled(bs, bs, 1);
		gl.glColor3f(bc[0], bc[1], bc[2]);

		gl.glCallList(listIndex);

		gl.glPopMatrix();

		// fill
		gl.glPushMatrix();
		gl.glTranslated(x, y, 0);
		gl.glScaled(fs, fs, 1);
		gl.glColor3f(fc[0], fc[1], fc[2]);

		gl.glCallList(listIndex);

		gl.glPopMatrix();
		gl.glFlush();
	}
}
