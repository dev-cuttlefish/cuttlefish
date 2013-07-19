package ch.ethz.sg.cuttlefish.gui2.undoable.actions;

import java.awt.Color;

import ch.ethz.sg.cuttlefish.gui2.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.misc.Vertex;

public class SetVertexBorderColorUndoableAction extends UndoableAction {

	private Vertex vertex;
	private Color oldColor;
	private Color newColor;

	public SetVertexBorderColorUndoableAction(Vertex vertex, Color color) {
		this.vertex = vertex;
		this.newColor = color;
		this.oldColor = vertex.getColor();
	}

	@Override
	public void execute() {
		vertex.setColor(newColor);
	}

	@Override
	public void revert() {
		vertex.setColor(oldColor);
	}

}
