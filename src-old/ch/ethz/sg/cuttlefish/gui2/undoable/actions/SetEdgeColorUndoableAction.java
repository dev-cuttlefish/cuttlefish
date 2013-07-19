package ch.ethz.sg.cuttlefish.gui2.undoable.actions;

import java.awt.Color;

import ch.ethz.sg.cuttlefish.gui2.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.misc.Edge;

public class SetEdgeColorUndoableAction extends UndoableAction {

	private Edge edge;
	private Color oldColor;
	private Color newColor;

	public SetEdgeColorUndoableAction(Edge edge, Color color) {
		this.edge = edge;
		this.newColor = color;
		this.oldColor = edge.getColor();
	}

	public void execute() {
		edge.setColor(newColor);
	}

	public void revert() {
		edge.setColor(oldColor);
	}

}
