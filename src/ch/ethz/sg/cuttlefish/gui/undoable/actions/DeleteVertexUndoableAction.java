package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class DeleteVertexUndoableAction extends UndoableAction {

	private BrowsableNetwork network;
	private Vertex vertex;
	private List<Edge> incidentEdges;

	public DeleteVertexUndoableAction(BrowsableNetwork network, Vertex vertex) {
		this.network = network;
		this.vertex = vertex;
		this.incidentEdges = new ArrayList<Edge>();

		for (Edge e : network.getIncidentEdges(vertex)) {
			this.incidentEdges.add(e);
		}
	}

	@Override
	public void execute() {
		if (network.containsVertex(vertex)) {
			network.removeVertex(vertex);
		}
	}

	@Override
	public void revert() {
		if (!network.containsVertex(vertex)) {
			network.addVertex(vertex);

			for (Edge e : incidentEdges) {
				network.addEdge(e);
			}
		}
	}
}
