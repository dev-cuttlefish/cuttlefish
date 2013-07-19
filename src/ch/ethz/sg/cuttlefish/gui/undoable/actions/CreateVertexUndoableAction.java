package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class CreateVertexUndoableAction extends UndoableAction {

	private BrowsableNetwork network;
	private Vertex vertex;

	public CreateVertexUndoableAction(BrowsableNetwork network, Vertex vertex) {
		this.network = network;
		this.vertex = vertex;
	}

	@Override
	public void execute() {
		network.addVertex(vertex);
	}

	@Override
	public void revert() {
		network.removeVertex(vertex);
	}

}
