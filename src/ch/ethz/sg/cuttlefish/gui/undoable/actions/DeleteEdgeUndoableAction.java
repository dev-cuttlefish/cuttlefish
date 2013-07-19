package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;

public class DeleteEdgeUndoableAction extends UndoableAction {

	private BrowsableNetwork network;
	private Edge edge;

	public DeleteEdgeUndoableAction(BrowsableNetwork network, Edge edge) {
		this.network = network;
		this.edge = edge;
	}

	@Override
	public void execute() {
		network.removeEdge(edge);
	}

	@Override
	public void revert() {
		network.addEdge(edge);
	}

}
