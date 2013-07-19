package ch.ethz.sg.cuttlefish.gui2.undoable.actions;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.sg.cuttlefish.gui2.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class DeleteVertexUndoableAction extends UndoableAction {

	private Layout<Vertex, Edge> layout;
	private Graph<Vertex, Edge> graph;
	private Vertex vertex;
	private Point2D position;
	private List<IncidentEdge> incidentEdges;

	public DeleteVertexUndoableAction(Layout<Vertex, Edge> layout, Vertex vertex) {
		this.layout = layout;
		this.graph = layout.getGraph();
		this.vertex = vertex;
		this.position = layout.transform(vertex);
		this.incidentEdges = new ArrayList<IncidentEdge>();

		for (Edge e : graph.getIncidentEdges(vertex)) {
			this.incidentEdges.add(new IncidentEdge(e, graph.getEdgeType(e), graph.getEndpoints(e)));
		}
	}

	@Override
	public void execute() {
		if (graph.containsVertex(vertex)) {
			graph.removeVertex(vertex);
		}
	}

	@Override
	public void revert() {
		if (!graph.containsVertex(vertex)) {
			graph.addVertex(vertex);
			layout.setLocation(vertex, position);

			for (IncidentEdge ie : incidentEdges) {
				graph.addEdge(ie.edge, ie.endpoints, ie.type);
			}
		}
	}
}

class IncidentEdge {
	Edge edge;
	EdgeType type;
	Pair<Vertex> endpoints;

	public IncidentEdge(Edge edge, EdgeType type, Pair<Vertex> endpoints) {
		this.edge = edge;
		this.type = type;
		this.endpoints = endpoints;
	}
}
