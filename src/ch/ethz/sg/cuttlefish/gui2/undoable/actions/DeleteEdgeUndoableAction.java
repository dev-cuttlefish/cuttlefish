package ch.ethz.sg.cuttlefish.gui2.undoable.actions;

import ch.ethz.sg.cuttlefish.gui2.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class DeleteEdgeUndoableAction extends UndoableAction {

	private Graph<Vertex, Edge> graph;
	private Edge edge;
	private Pair<Vertex> endpoints;

	public DeleteEdgeUndoableAction(Graph<Vertex, Edge> graph, Edge edge) {
		this.graph = graph;
		this.edge = edge;
		this.endpoints = graph.getEndpoints(edge);
	}

	@Override
	public void execute() {
		graph.removeEdge(edge);
	}

	@Override
	public void revert() {
		graph.addEdge(edge, endpoints);
	}

}
