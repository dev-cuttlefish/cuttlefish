package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class SetVertexShapeUndoableAction extends UndoableAction {

	private Vertex vertex;
	private String oldShape;
	private String newShape;

	public SetVertexShapeUndoableAction(Vertex vertex, String shape) {
		this.vertex = vertex;
		this.newShape = shape;
		this.oldShape = vertex.getShape();
	}

	@Override
	public void execute() {
		vertex.setShape(newShape);
	}

	@Override
	public void revert() {
		vertex.setShape(oldShape);
	}

}
