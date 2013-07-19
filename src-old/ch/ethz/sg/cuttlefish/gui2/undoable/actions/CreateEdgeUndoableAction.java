package ch.ethz.sg.cuttlefish.gui2.undoable.actions;

import ch.ethz.sg.cuttlefish.gui2.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class CreateEdgeUndoableAction extends UndoableAction {

	private Graph<Vertex, Edge> graph;
	private Edge edge;
	private Vertex from, to;
	private EdgeType type;

	public CreateEdgeUndoableAction(Graph<Vertex, Edge> graph, Edge newEdge, Vertex from, Vertex to, EdgeType type) {
		this.graph = graph;
		this.type = type;
		this.edge = newEdge;
		this.from = from;
		this.to = to;
	}

	@Override
	public void execute() {
		graph.addEdge(edge, from, to, type);
	}

	@Override
	public void revert() {
		graph.removeEdge(edge);
	}
}
