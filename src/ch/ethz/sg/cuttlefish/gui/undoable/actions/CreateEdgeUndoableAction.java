package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;

public class CreateEdgeUndoableAction extends UndoableAction {

	private BrowsableNetwork network;
	private Edge edge;

	public CreateEdgeUndoableAction(BrowsableNetwork network,
			ch.ethz.sg.cuttlefish.networks.Edge newEdge) {
		this.network = network;
		this.edge = newEdge;
	}

	@Override
	public void execute() {
		network.addEdge(edge);
	}

	@Override
	public void revert() {
		network.removeEdge(edge);
	}
}
