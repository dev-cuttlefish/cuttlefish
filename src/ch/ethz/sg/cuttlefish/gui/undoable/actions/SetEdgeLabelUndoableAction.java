package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.Edge;

public class SetEdgeLabelUndoableAction extends UndoableAction {

	private Edge edge;
	private String oldLabel;
	private String newLabel;

	public SetEdgeLabelUndoableAction(Edge edge, String label) {
		this.edge = edge;
		this.newLabel = label;
		this.oldLabel = edge.getLabel();
	}

	@Override
	public void execute() {
		edge.setLabel(newLabel);
	}

	@Override
	public void revert() {
		edge.setLabel(oldLabel);
	}

}
