package ch.ethz.sg.cuttlefish.gui2.undoable.actions;

import ch.ethz.sg.cuttlefish.gui2.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.misc.Edge;

public class SetEdgeWidthUndoableAction extends UndoableAction {

	private Edge edge;
	private double oldWidth;
	private double newWidth;

	public SetEdgeWidthUndoableAction(Edge edge, double width) {
		this.edge = edge;
		this.newWidth = width;
		this.oldWidth = edge.getWidth();
	}

	@Override
	public void execute() {
		edge.setWidth(newWidth);
	}

	@Override
	public void revert() {
		edge.setWidth(oldWidth);
	}

}
