package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import java.awt.Color;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class SetVertexBorderColorUndoableAction extends UndoableAction {

	private Vertex vertex;
	private Color oldColor;
	private Color newColor;

	public SetVertexBorderColorUndoableAction(Vertex vertex, Color color) {
		this.vertex = vertex;
		this.newColor = color;
		this.oldColor = vertex.getBorderColor();
	}

	@Override
	public void execute() {
		vertex.setBorderColor(newColor);
	}

	@Override
	public void revert() {
		vertex.setBorderColor(oldColor);
	}

}
