package ch.ethz.sg.cuttlefish.gui.visualization;

import java.awt.geom.Point2D;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Collection;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.gui.visualization.mouse.GraphMouse;
import ch.ethz.sg.cuttlefish.gui.visualization.renderers.EdgeRenderer;
import ch.ethz.sg.cuttlefish.gui.visualization.renderers.LabelRenderer;
import ch.ethz.sg.cuttlefish.gui.visualization.renderers.VertexRenderer;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

import com.jogamp.opengl.util.FPSAnimator;

public class NetworkRenderer implements GLEventListener {

	// Rendered objects
	private NetworkPanel networkPanel;

	// Rendering Dimensions & Transformations
	private double scaleFactor = 1;
	private Point2D zoomPos = new Point2D.Double(0, 0);
	private int width, height;
	private Point2D origin;

	// Projecting / UnProjecting
	private DoubleBuffer modelview = DoubleBuffer.allocate(16);
	private DoubleBuffer projection = DoubleBuffer.allocate(16);
	private IntBuffer viewport = IntBuffer.allocate(4);

	// Mouse Panning, Zooming and Moving
	private GraphMouse graphMouse = null;

	// Rendering
	private VertexRenderer vertexRenderer;
	private EdgeRenderer edgeRenderer;
	private LabelRenderer labelRenderer;
	private GLAutoDrawable drawable;
	private GLCanvas canvas;
	private FPSAnimator animator = null;
	private boolean animateLabels = false;

	private final int ANIMATION_FPS = 60;
	private final int RENDER_MARGIN = 50;
	private final int ALPHA_BITS = 16;

	// Anti-aliasing configuration
	private final boolean AA_MULTISAMPLING = false;
	private final boolean AA_SMOOTHING = true;
	private final boolean AA_SMOOTH_LINES = true;
	private final boolean AA_SMOOTH_POLYGONS = false;
	private final boolean AA_SMOOTH_POINTS = true;

	private GLCapabilities getCaps() {
		GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());

		// Anti-aliasing using Multisampling
		if (AA_MULTISAMPLING) {
			try {
				caps.setAlphaBits(ALPHA_BITS);
				caps.setDoubleBuffered(true);
				caps.setHardwareAccelerated(true);
				caps.setSampleBuffers(true);
				caps.setNumSamples(8);

				caps.setAccumAlphaBits(ALPHA_BITS);
				caps.setAccumBlueBits(ALPHA_BITS);
				caps.setAccumGreenBits(ALPHA_BITS);
				caps.setAccumRedBits(ALPHA_BITS);

			} catch (javax.media.opengl.GLException ex) {
				ex.printStackTrace();
			}
		}

		return caps;
	}

	public NetworkRenderer(NetworkPanel networkPanel, int width, int height) {
		this.networkPanel = networkPanel;
		this.width = width;
		this.height = height;
		this.origin = new Point2D.Double(0, 0);
		this.canvas = new GLCanvas(getCaps());
		this.drawable = canvas;
		this.animator = new FPSAnimator(drawable, ANIMATION_FPS);
	}

	/*
	 * GLEventListener methods
	 */

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		this.drawable = drawable;

		if (AA_SMOOTHING) {
			// Anti-aliasing using Smoothing
			if (AA_SMOOTH_LINES) {
				gl.glEnable(GL2.GL_LINE_SMOOTH);
				gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
			}

			// TODO: This AA method creates "transparency" in GLU polygons
			if (AA_SMOOTH_POLYGONS) {
				gl.glEnable(GL2.GL_POLYGON_SMOOTH);
				gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
			}

			if (AA_SMOOTH_POINTS) {
				gl.glEnable(GL2.GL_POINT_SMOOTH);
				gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
			}

			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		} else if (AA_MULTISAMPLING) {
			// Anti-aliasing using Multisampling
			gl.glEnable(GL2.GL_MULTISAMPLE);
		}

		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		// Renderers
		this.vertexRenderer = new VertexRenderer(drawable, this);
		this.edgeRenderer = new EdgeRenderer(drawable, this);
		this.labelRenderer = new LabelRenderer(drawable, this);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();
		this.drawable = drawable;
		this.width = width;
		this.height = height;

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, width, height, 0, -1, 1);
		gl.glViewport(0, 0, width, height);

		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
	}

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		this.drawable = drawable;

		// Apply transformations
		double transx = -origin.getX(), transy = -origin.getY();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, width, height, 0, -1, 1);
		gl.glViewport(0, 0, width, height);

		gl.glTranslated(transx + zoomPos.getX(), transy + zoomPos.getY(), 0);
		gl.glScaled(scaleFactor, scaleFactor, 1);
		gl.glTranslated(-zoomPos.getX(), -zoomPos.getY(), 0);

		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		// Draw edges, arrows and vertices
		edgeRenderer.transform(transx, transy, scaleFactor);
		edgeRenderer.render();

		if (animateLabels) {
			labelRenderer.scale(scaleFactor);
			labelRenderer.drawLabels();
		}

		vertexRenderer.transform(transx, transy, scaleFactor);
		vertexRenderer.render();
	}

	public void dispose(GLAutoDrawable drawable) {

	}

	public void repaint() {
		if (drawable == null) {
			return;
		}

		drawable.display();
	}

	/**
	 * Enables animation while the network is updated. Used when layout is
	 * computed, when panel is panned/zoomed, etc.
	 * 
	 * @param enable
	 *            Controls animation
	 * @param animateLabels
	 *            Controls animation of labels; the last frame of the animation
	 *            always displays labels.
	 */
	private boolean animationAlreadyActive = false;

	public void animate(boolean enable, boolean animateLabels) {
		if (animator == null || drawable == null)
			return;

		if (enable) {
			if (!animator.isAnimating())
				animator.start();
			else
				animationAlreadyActive = true;
			this.animateLabels = animateLabels;

		} else {
			if (animator.isAnimating() && !animationAlreadyActive) {
				animator.stop();
			}

			this.animateLabels = true;
			animationAlreadyActive = false;
			drawable.display(); // draw last frame
		}
	}

	/**
	 * This method finds the location of the graph relative to the viewer and
	 * shifts is so that it appears in the center of JUNG's VisualizationViewer.
	 */
	public void centerNetwork() {
		double top = Double.MAX_VALUE;
		double bottom = Double.MAX_VALUE;
		double left = Double.MAX_VALUE;
		double right = Double.MAX_VALUE;

		for (Vertex v : getVertices()) {
			Point2D p = v.getPosition();

			if (top < p.getY() || top == Double.MAX_VALUE)
				top = p.getY();
			if (bottom > p.getY() || bottom == Double.MAX_VALUE)
				bottom = p.getY();
			if (left > p.getX() || left == Double.MAX_VALUE)
				left = p.getX();
			if (right < p.getX() || right == Double.MAX_VALUE)
				right = p.getX();
		}

		double deltaX = this.getCenter().getX() - (right + left) / 2;
		double deltaY = this.getCenter().getY() - (top + bottom) / 2;
		double zoom = height / (top - bottom + RENDER_MARGIN);

		if (!Double.isInfinite(deltaX) && !Double.isInfinite(deltaY)
				&& !Double.isNaN(deltaX) && !Double.isNaN(deltaY)) {

			translate(deltaX, deltaY);
			scale(zoom, getCenter());

			graphMouse.setZoomFactor(zoom);
			graphMouse.setZoomPosition(getCenter());
		}
	}

	public Point2D screenToWorld(Point2D mouse) {
		double[] wcoord = new double[4];
		double x = mouse.getX();
		double y = viewport.get(3) - mouse.getY();
		GLU glu = new GLU();

		glu.gluUnProject(x, y, 0, modelview.array(), 0, projection.array(), 0,
				viewport.array(), 0, wcoord, 0);

		Point2D p = new Point2D.Float();
		p.setLocation(wcoord[0], wcoord[1]);
		return p;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public Point2D getCenter() {
		return new Point2D.Double(origin.getX() + width * 0.5, origin.getY()
				+ height * 0.5);
	}

	public void translate(double x, double y) {
		double cx = origin.getX();
		double cy = origin.getY();

		origin.setLocation(cx - x, cy - y);
	}

	public void scale(double scaleFactor, Point2D zoomCenter) {
		this.scaleFactor = scaleFactor;
		this.zoomPos = zoomCenter;
	}

	public double getScaleFactor() {
		return scaleFactor;
	}

	public void setGraphMouse(GraphMouse graphMouse) {
		this.graphMouse = graphMouse;
	}

	public GraphMouse getGraphMouse() {
		return this.graphMouse;
	}

	public Collection<Vertex> getVertices() {
		return networkPanel.getNetwork().getVertices();
	}

	public Collection<Edge> getEdges() {
		return networkPanel.getNetwork().getEdges();
	}

	public BrowsableNetwork getNetwork() {
		return networkPanel.getNetwork();
	}

	public GLCanvas getCanvas() {
		return canvas;
	}

	public EdgeRenderer getEdgeRenderer() {
		return edgeRenderer;
	}
	
	public GLAutoDrawable getDrawable() {
		return drawable;
	}
}
