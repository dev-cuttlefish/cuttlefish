package ch.ethz.sg.cuttlefish.gui.visualization;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;

public class GephiTest {

	public static void main(String[] args) {

		// Init a project - and therefore a workspace
		ProjectController pc = Lookup.getDefault().lookup(
				ProjectController.class);
		pc.newProject();
		pc.getCurrentWorkspace();

		// Get a graph model - it exists because we have a workspace
		GraphModel graphModel = Lookup.getDefault()
				.lookup(GraphController.class).getModel();

		// Create three nodes
		Node n0 = graphModel.factory().newNode("n0");
		n0.getNodeData().setLabel("Node 0");
		Node n1 = graphModel.factory().newNode("n1");
		n1.getNodeData().setLabel("Node 1");
		Node n2 = graphModel.factory().newNode("n2");
		n2.getNodeData().setLabel("Node 2");

		// Create three edges
		Edge e1 = graphModel.factory().newEdge(n1, n2, 1f, true);
		Edge e2 = graphModel.factory().newEdge(n0, n2, 2f, true);
		Edge e3 = graphModel.factory().newEdge(n2, n0, 2f, true); // This is
																	// e2's
																	// mutual
																	// edge

		// Append as a Directed Graph
		DirectedGraph directedGraph = graphModel.getDirectedGraph();
		directedGraph.addNode(n0);
		directedGraph.addNode(n1);
		directedGraph.addNode(n2);
		directedGraph.addEdge(e1);
		directedGraph.addEdge(e2);
		directedGraph.addEdge(e3);

		// Count nodes and edges
		System.out.println("Nodes: " + directedGraph.getNodeCount()
				+ " Edges: " + directedGraph.getEdgeCount());

		// Get a UndirectedGraph now and count edges
		UndirectedGraph undirectedGraph = graphModel.getUndirectedGraph();
		System.out.println("Edges: " + undirectedGraph.getEdgeCount()); // The
																		// mutual
																		// edge
																		// is
																		// automatically
																		// merged

		// Iterate over nodes
		for (Node n : directedGraph.getNodes()) {
			Node[] neighbors = directedGraph.getNeighbors(n).toArray();
			System.out.println(n.getNodeData().getLabel() + " has "
					+ neighbors.length + " neighbors");
			System.out.println(n.getNodeData().getRadius() + " "
					+ n.getNodeData().x() + ", " + n.getNodeData().y());
		}

		// Iterate over edges
		for (Edge e : directedGraph.getEdges()) {
			System.out.println(e.getSource().getNodeData().getId() + " -> "
					+ e.getTarget().getNodeData().getId());
		}

		// Find node by id
		Node node2 = directedGraph.getNode("n2");

		// Get degree
		System.out.println("Node2 degree: " + directedGraph.getDegree(node2));

		// Modify the graph while reading
		// Due to locking, you need to use toArray() on Iterable to be able to
		// modify
		// the graph in a read loop
		for (Node n : directedGraph.getNodes().toArray()) {
			directedGraph.removeNode(n);
		}
	}

}
