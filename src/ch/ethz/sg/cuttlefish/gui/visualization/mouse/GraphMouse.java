package ch.ethz.sg.cuttlefish.gui.visualization.mouse;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

public interface GraphMouse extends MouseListener, MouseMotionListener,
		MouseWheelListener {

	/**
	 * Describes the mode in which the GraphMouse is set. NONE Disables mouse
	 * interaction. TRANSFORMING Used to zoom in and pan the panel. (left click)
	 * INTERACTING Used to pick up and move vertices. (left click) EDITING Used
	 * to add new vertices and edges. (left click)
	 * 
	 * @author ilias
	 * 
	 */
	public enum Mode {
		NONE, TRANSFORMING, SELECTING, EDITING
	};

	public Point2D getPanning();

	public Point2D getZoomPosition();

	public void setZoomPosition(Point2D position);

	public double getZoomFactor();

	public void setZoomFactor(double zoomFactor);

	public void setMode(Mode mode);

	public Mode getMode();

	public boolean mouseInMode(Mode mode);
}
