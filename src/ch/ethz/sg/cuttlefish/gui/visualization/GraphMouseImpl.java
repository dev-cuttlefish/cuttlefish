package ch.ethz.sg.cuttlefish.gui.visualization;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.UndoableControl;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.CreateEdgeUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.CreateVertexUndoableAction;
import ch.ethz.sg.cuttlefish.gui.visualization.geometry.ClosestShapePickSupport;
import ch.ethz.sg.cuttlefish.gui.visualization.mouse.EditVertexMenu;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public final class GraphMouseImpl implements GraphMouse {

	private double zoom = 1;
	private Point2D panStart = null;
	private Point2D zoomPos = new Point2D.Float(0, 0);
	private Mode mouseMode = Mode.NONE;

	private Vertex grabbed = null;
	private double dx = 0, dy = 0;
	private boolean isPanning = false;
	private boolean isMoving = false;
	private boolean isCreatingEdge = false;
	private boolean isCreatingVertex = false;

	private NetworkPanel networkPanel = null;
	private NetworkRenderer renderer = null;
	private ClosestShapePickSupport shapePickSupport;

	private static final double ZOOM_SENSITIVITY = 20;
	private static final boolean ANIMATE_LABELS = false;

	public GraphMouseImpl(NetworkPanel networkPanel) {
		this.networkPanel = networkPanel;
		this.renderer = networkPanel.getNetworkRenderer();
		this.renderer.setGraphMouse(this);
	}

	public Point2D getPanning() {
		return new Point2D.Double(dx, dy);
	}

	public Point2D getZoomPosition() {
		return (Point2D) zoomPos.clone();
	}

	public void setZoomPosition(Point2D position) {
		zoomPos = (Point2D) position.clone();
	}

	public double getZoomFactor() {
		return zoom;
	}

	public void setZoomFactor(double zoomFactor) {
		zoom = zoomFactor;
	}

	public void setMode(Mode mode) {
		mouseMode = mode;
	}

	public Mode getMode() {
		return mouseMode;
	}

	public boolean mouseInMode(Mode mode) {
		return mouseMode == mode;
	}

	public ClosestShapePickSupport getShapePickSupport() {
		return shapePickSupport;
	}

	public void setShapePickSupport(ClosestShapePickSupport pickSupport) {
		shapePickSupport = pickSupport;
	}

	/*
	 * MouseListener
	 */

	public void mouseClicked(MouseEvent e) {
		Point2D p = e.getPoint();

		if (p == null)
			return;

		if (e.getButton() == MouseEvent.BUTTON1 && mouseInMode(Mode.EDITING)) {
			// create a node
			isCreatingVertex = true;
			Vertex vertex = new Vertex();
			vertex.setPosition(p);
			UndoableAction action = new CreateVertexUndoableAction(
					networkPanel.getNetwork(), vertex);
			action.execute();
			UndoableControl.getController().actionExecuted(action);

		} else if (e.getButton() == MouseEvent.BUTTON3) {
			// && mouseInMode(Mode.EDITING)
			Vertex v = shapePickSupport.selectVertexByPoint(renderer
					.screenToWorld(p));

			if (v != null)
				new EditVertexMenu(v, e).show();
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		Point2D p = e.getPoint();

		if (p == null)
			return;

		if (e.getButton() == MouseEvent.BUTTON1) {

			if (mouseInMode(Mode.TRANSFORMING)) {
				isPanning = true;
				panStart = p;

			} else if (mouseInMode(Mode.INTERACTING)) {
				if (shapePickSupport != null) {
					grabbed = shapePickSupport.selectVertexByPoint(renderer
							.screenToWorld(p));
					isMoving = (grabbed != null);
				}

			} else if (mouseInMode(Mode.EDITING)) {
				e.getComponent().setCursor(
						Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				if (shapePickSupport != null) {
					grabbed = shapePickSupport.selectVertexByPoint(renderer
							.screenToWorld(p));

					if (grabbed != null) {
						// create an edge
						isCreatingEdge = true;
					}
				}

			}

			renderer.animate((isPanning || isMoving || isCreatingEdge),
					ANIMATE_LABELS);
			e.getComponent().setCursor(
					Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {

			if (isCreatingEdge && shapePickSupport != null) {
				Point2D p = e.getPoint();
				Vertex source = grabbed;
				Vertex target = shapePickSupport.selectVertexByPoint(renderer
						.screenToWorld(p));

				if (target != null) {
					// Find the new edge and wrap it in an UndoableAction
					Edge newEdge = networkPanel.getNetwork().findEdge(source,
							target);

					if (newEdge == null) {
						newEdge = new Edge(source, target, networkPanel
								.getNetwork().isDirected());

						UndoableAction action = new CreateEdgeUndoableAction(
								networkPanel.getNetwork(), newEdge);
						action.execute();
						UndoableControl.getController().actionExecuted(action);
					}
				}
				renderer.getEdgeRenderer().drawEdgeToPoint(null, null);
			}

			renderer.animate(false, ANIMATE_LABELS);
			e.getComponent().setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			// vertex moved; must recompute layout
			if (isMoving || isCreatingEdge || isCreatingVertex)
				networkPanel.resumeLayout();

			grabbed = null;
			isPanning = false;
			isMoving = false;
			isCreatingEdge = false;
			isCreatingVertex = false;
			dx = 0;
			dy = 0;
		}
	}

	/*
	 * MouseMotionListener
	 */

	public void mouseDragged(MouseEvent e) {
		Point2D p = e.getPoint();
		Point2D s = renderer.screenToWorld(p);

		if (isPanning && mouseInMode(Mode.TRANSFORMING)) {
			dx = p.getX() - panStart.getX();
			dy = p.getY() - panStart.getY();
			renderer.translate(dx, dy);
			panStart.setLocation(p);
		}

		if (isMoving && mouseInMode(Mode.INTERACTING)) {
			grabbed.setPosition(s);
		}

		if (isCreatingEdge) {
			renderer.getEdgeRenderer()
					.drawEdgeToPoint(grabbed.getPosition(), s);
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	/*
	 * MouseWheelListener
	 */

	public void mouseWheelMoved(MouseWheelEvent e) {
		int wheelRotation = e.getWheelRotation();
		zoom += (double) wheelRotation / ZOOM_SENSITIVITY;
		zoomPos = renderer.screenToWorld(e.getPoint());
		renderer.scale(zoom, zoomPos);
		renderer.repaint();
	}
}
