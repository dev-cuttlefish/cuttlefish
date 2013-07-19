package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.Vertex;

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
