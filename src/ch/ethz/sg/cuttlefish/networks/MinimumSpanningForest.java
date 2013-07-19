package ch.ethz.sg.cuttlefish.networks;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.HierarchicalDirectedGraph;
import org.gephi.graph.api.Node;

/**
 * For the input Graph, creates a MinimumSpanningTree using a variation of
 * Prim's algorithm.
 * 
 * NOTE (by Ilias Rinis): This algorithm is adapted to compute a MST on the
 * Graph structure of the Gephi Toolkit 0.8.3. An important point is that there
 * do not exist multiple graph/forest instances, but rather a single Singleton.
 * For this reason the input structure should not be empty, since the MST will
 * be computed on the existing graph. The algorithm keeps the set of edges and
 * nodes but clears the graph structure, and inserts all nodes and the necessary
 * edges to build a MST.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 * @author Ilias Rinis - iliasr@gmail.com
 * 
 */
public class MinimumSpanningForest {

	protected HierarchicalDirectedGraph forest;

	private Collection<Node> roots;
	private Collection<Node> allNodes;
	private Collection<Edge> unfinishedEdges;

	/**
	 * Creates a minimum spanning forest from the supplied graph, populating the
	 * supplied Forest, which must be empty. If the supplied root is null, or
	 * not present in the Graph, then an arbitrary Graph vertex will be selected
	 * as the root. If the Minimum Spanning Tree does not include all vertices
	 * of the Graph, then a leftover vertex is selected as a root, and another
	 * tree is created
	 * 
	 * @param forest
	 *            the Forest to populate, that also contains the Graph
	 *            structure. Must *not* be empty (see Note above)
	 * 
	 * @param roots
	 *            a collection with the roots that the MST will have
	 * 
	 */
	public MinimumSpanningForest(HierarchicalDirectedGraph forest,
			Collection<Node> roots) {

		this.forest = forest;
		this.unfinishedEdges = new HashSet<Edge>(Arrays.asList(forest
				.getEdges().toArray()));
		this.allNodes = new HashSet<Node>(Arrays.asList(forest.getNodes()
				.toArray()));
		this.roots = roots;

		Node root = null;
		if (this.roots != null && !this.roots.isEmpty()) {
			root = this.roots.iterator().next();
			this.roots.remove(root);
			System.out.println("MST Root Vertex: " + root);
		} else {
			// TODO ilias: remove exception
			throw new RuntimeException("No root(s) specified for the MST!");
		}

		if (this.forest.getNodeCount() != 0) {
			this.forest.clear();
			this.forest.addNode(root, null);
		}

		updateForest(getNodes());
	}

	/**
	 * Returns the generated forest.
	 */
	public HierarchicalDirectedGraph getForest() {
		return forest;
	}

	public Collection<Node> getNodes() {
		return Arrays.asList(forest.getNodesTree().toArray());
	}

	public Collection<Edge> getEdges() {
		return Arrays.asList(forest.getEdgesTree().toArray());
	}

	// TODO ilias: needs testing
	protected void updateForest(Collection<Node> tv) {
		Edge nextEdge = null;
		Node nextVertex = null;
		Node currentVertex = null;
		for (Edge e : unfinishedEdges) {

			if (forest.contains(e))
				continue;

			// find the lowest cost edge, get its opposite endpoint,
			// and then update forest from its Successors
			Node first = e.getSource();
			Node second = e.getTarget();

			if (tv.contains(first) == true && tv.contains(second) == false) {
				nextEdge = e;
				currentVertex = first;
				nextVertex = second;

			} else if (!e.isDirected() && tv.contains(second) == true
					&& tv.contains(first) == false) {
				nextEdge = e;
				currentVertex = second;
				nextVertex = first;
			}
		}

		if (nextVertex != null && nextEdge != null) {
			unfinishedEdges.remove(nextEdge);
			allNodes.remove(nextVertex);
			allNodes.remove(currentVertex);

			forest.addNode(nextVertex, currentVertex);
			// TODO ilias: this creates a new edge so the old one will be
			// discarded. might need to change this to maintain edge
			// attributes!!
			forest.addEdge(currentVertex, nextVertex);
			// forest.addEdge(nextEdge);

			updateForest(getNodes());
		}

		Collection<Node> leftovers = new HashSet<Node>(allNodes);
		leftovers.removeAll(getNodes());

		if (leftovers.size() > 0) {
			Node root;
			if (roots != null && !roots.isEmpty()) {
				root = roots.iterator().next();
				System.out.println(root);
				roots.remove(root);
			} else {
				root = leftovers.iterator().next();
			}
			forest.addNode(root, null);
			updateForest(getNodes());
		}
	}
}
