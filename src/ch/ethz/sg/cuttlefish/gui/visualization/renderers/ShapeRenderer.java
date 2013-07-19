package ch.ethz.sg.cuttlefish.gui.visualization.renderers;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import ch.ethz.sg.cuttlefish.gui.visualization.NetworkRenderer;

public abstract class ShapeRenderer {

	protected GL2 gl;
	protected GLAutoDrawable drawable;
	protected double scaleFactor = 1;
	protected double panX = 0;
	protected double panY = 0;
	protected NetworkRenderer networkRenderer;

	private Map<String, Integer> displayList;

	public ShapeRenderer(GLAutoDrawable drawable,
			NetworkRenderer networkRenderer) {
		this.drawable = drawable;
		this.networkRenderer = networkRenderer;
		gl = drawable.getGL().getGL2();

		displayList = new HashMap<String, Integer>();
	}

	protected int getDisplayListIndex(String shapeName) {

		if (!displayList.containsKey(shapeName)) {
			int i = gl.glGenLists(1);
			displayList.put(shapeName, i);

			return i;

		}
		return displayList.get(shapeName);
	}

	public void transform(double translateX, double translateY,
			double scaleFactor) {
		this.scaleFactor = scaleFactor;
		this.panX = translateX;
		this.panY = translateY;
	}

	public abstract void render();
}
