package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import java.awt.Color;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class SetVertexFillColorUndoableAction extends UndoableAction {

	private Vertex vertex;
	private Color oldColor;
	private Color newColor;

	public SetVertexFillColorUndoableAction(Vertex vertex, Color color) {
		this.vertex = vertex;
		this.newColor = color;
		this.oldColor = vertex.getFillColor();
	}

	@Override
	public void execute() {
		vertex.setFillColor(newColor);
	}

	@Override
	public void revert() {
		vertex.setFillColor(oldColor);
	}

}
