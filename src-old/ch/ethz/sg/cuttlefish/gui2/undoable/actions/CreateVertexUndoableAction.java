package ch.ethz.sg.cuttlefish.gui2.undoable.actions;

import java.awt.geom.Point2D;

import ch.ethz.sg.cuttlefish.gui2.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;

public class CreateVertexUndoableAction extends UndoableAction {

	private Layout<Vertex, Edge> layout;
	private Graph<Vertex, Edge> graph;
	private Vertex vertex;
	private Point2D location;

	public CreateVertexUndoableAction(Layout<Vertex, Edge> layout, Vertex vertex, Point2D location) {
		this.layout = layout;
		this.graph = layout.getGraph();
		this.vertex = vertex;
		this.location = location;
	}

	@Override
	public void execute() {
		graph.addVertex(vertex);
		layout.setLocation(vertex, location);
	}

	@Override
	public void revert() {
		graph.removeVertex(vertex);
	}

}
