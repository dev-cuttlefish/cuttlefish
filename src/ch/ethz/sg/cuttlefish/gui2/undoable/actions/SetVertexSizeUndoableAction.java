package ch.ethz.sg.cuttlefish.gui2.undoable.actions;

import ch.ethz.sg.cuttlefish.gui2.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.misc.Vertex;

public class SetVertexSizeUndoableAction extends UndoableAction {

	private Vertex vertex;
	private double oldSize;
	private double newSize;

	public SetVertexSizeUndoableAction(Vertex vertex, double size) {
		this.vertex = vertex;
		this.newSize = size;
		this.oldSize = vertex.getSize();
	}

	@Override
	public void execute() {
		vertex.setSize(newSize);
	}

	@Override
	public void revert() {
		vertex.setSize(oldSize);
	}

}
