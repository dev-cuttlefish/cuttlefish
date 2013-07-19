package ch.ethz.sg.cuttlefish.gui2.undoable.actions;

import ch.ethz.sg.cuttlefish.gui2.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.misc.Vertex;

public class SetVertexShapeUndoableAction extends UndoableAction {

	private Vertex vertex;
	private String oldShape;
	private String newShape;

	public SetVertexShapeUndoableAction(Vertex vertex, String shape) {
		this.vertex = vertex;
		this.newShape = shape;
		this.oldShape = vertex.getShapeString();
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
