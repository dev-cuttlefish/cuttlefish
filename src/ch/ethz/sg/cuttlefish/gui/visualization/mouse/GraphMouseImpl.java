package ch.ethz.sg.cuttlefish.gui.visualization.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.UndoableControl;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.CreateEdgeUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.CreateVertexUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetVertexPositionUndoableAction;
import ch.ethz.sg.cuttlefish.gui.visualization.NetworkRenderer;
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
	private PickSupport pickSupport = null;

	private static final boolean ANIMATE_LABELS = true;

	public GraphMouseImpl(NetworkPanel networkPanel) {
		this.networkPanel = networkPanel;
		this.renderer = networkPanel.getNetworkRenderer();
		this.renderer.setGraphMouse(this);
		this.pickSupport = new IntersectingShapePickSupport(this.renderer);
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

	/*
	 * MouseListener
	 */

	public void mouseClicked(MouseEvent e) {
		Point2D p = e.getPoint();

		if (p == null)
			return;

		p = renderer.screenToWorld(p);

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
			Vertex v = pickSupport.pickVertex(p);

			if (v != null) {
				new EditVertexMenu(v, networkPanel.getNetwork(), e).show();

			} else {
				Edge edge = pickSupport.pickEdge(p);

				if (edge != null) {
					new EditEdgeMenu(edge, networkPanel.getNetwork(), e).show();
				}
			}
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
				grabbed = pickSupport.pickVertex(renderer.screenToWorld(p));

				if (grabbed != null) {
					isMoving = true;

				} else {
					isPanning = true;
					panStart = p;
				}

			} else if (mouseInMode(Mode.SELECTING)) {
				Vertex vertex = pickSupport.pickVertex(renderer
						.screenToWorld(p));

				if (vertex != null) {
					networkPanel.selectVertex(vertex);

				} else {
					Edge edge = pickSupport.pickEdge(renderer.screenToWorld(p));

					if (edge != null)
						networkPanel.selectEdge(edge);
				}

			} else if (mouseInMode(Mode.EDITING)) {
				// e.getComponent().setCursor(
				// Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				grabbed = pickSupport.pickVertex(renderer.screenToWorld(p));

				if (grabbed != null) {
					// create an edge
					isCreatingEdge = true;
				}

			}

			renderer.animate((isPanning || isMoving || isCreatingEdge),
					ANIMATE_LABELS);
			// e.getComponent().setCursor(
			// Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {

			if (isCreatingEdge) {
				Point2D p = renderer.screenToWorld(e.getPoint());
				Vertex source = grabbed;
				Vertex target = pickSupport.pickVertex(p);

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
			// e.getComponent().setCursor(
			// Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			if (isMoving) {
				UndoableAction action = new SetVertexPositionUndoableAction(
						grabbed, grabbed.getPosition(), oldPosition);
				action.execute();
				UndoableControl.getController().actionExecuted(action);
				oldPosition = null;
			}

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

	private Point2D oldPosition;

	public void mouseDragged(MouseEvent e) {
		Point2D p = e.getPoint();
		Point2D s = renderer.screenToWorld(p);

		if (isPanning && mouseInMode(Mode.TRANSFORMING)) {
			dx = p.getX() - panStart.getX();
			dy = p.getY() - panStart.getY();
			renderer.translate(dx, dy);
			panStart.setLocation(p);
		}

		if (isMoving) {

			if (oldPosition == null)
				oldPosition = grabbed.getPosition();
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
		double rotation = e.getWheelRotation();
		double sensitivity = 0.05;
		double zx, zy;
		double scaling = zoom > 1 ? 1 : renderer.getScaleFactor();

		zoom += rotation * sensitivity * scaling;
		zoomPos = renderer.screenToWorld(e.getPoint());
		zx = zoomPos.getX() * scaling;
		zy = zoomPos.getY() * scaling;
		zoomPos.setLocation(zx, zy);

		renderer.scale(zoom, zoomPos);
		renderer.repaint();
	}
}
