package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import java.awt.geom.Point2D;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class SetVertexPositionUndoableAction extends UndoableAction {

	private Vertex vertex;
	private Point2D oldPosition, newPosition;

	public SetVertexPositionUndoableAction(Vertex vertex, Point2D newPosition,
			Point2D oldPosition) {
		this.vertex = vertex;
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}

	@Override
	public void execute() {
		vertex.setPosition(newPosition);
	}

	@Override
	public void revert() {
		vertex.setPosition(oldPosition);
	}

}
