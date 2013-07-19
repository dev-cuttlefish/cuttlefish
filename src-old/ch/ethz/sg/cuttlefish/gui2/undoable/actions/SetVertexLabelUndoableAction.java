package ch.ethz.sg.cuttlefish.gui2.undoable.actions;

import ch.ethz.sg.cuttlefish.gui2.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.misc.Vertex;

public class SetVertexLabelUndoableAction extends UndoableAction {

	private Vertex vertex;
	private String oldLabel;
	private String newLabel;

	public SetVertexLabelUndoableAction(Vertex vertex, String label) {
		this.vertex = vertex;
		this.newLabel = label;
		this.oldLabel = vertex.getLabel();
	}

	@Override
	public void execute() {
		vertex.setLabel(newLabel);
	}

	@Override
	public void revert() {
		vertex.setLabel(oldLabel);
	}

}
