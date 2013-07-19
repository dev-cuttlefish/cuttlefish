package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class SetVertexBorderWidthUndoableAction extends UndoableAction {

	private Vertex vertex;
	private int oldWidth;
	private int newWidth;

	public SetVertexBorderWidthUndoableAction(Vertex vertex, int width) {
		this.vertex = vertex;
		this.newWidth = width;
		this.oldWidth = vertex.getWidth();
	}

	@Override
	public void execute() {
		vertex.setWidth(newWidth);
	}

	@Override
	public void revert() {
		vertex.setWidth(oldWidth);
	}

}
