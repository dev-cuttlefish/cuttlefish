package ch.ethz.sg.cuttlefish.networks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.plugin.AbstractAttributeFilter;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.HierarchicalDirectedGraph;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;


/**
 * A BrowsableNetwork that implements JUNG's Forest interface. Used to link
 * BrowsableNetwork with JUNG's tree layouts.
 * 
 * @author ptsankov, irinis
 * 
 */
public class BrowsableForestNetwork extends BrowsableNetwork implements
		ISimulation {

	private static final long serialVersionUID = -6962915001515847468L;

	private BrowsableNetwork originalNetwork;

	/*
	 * A Graph that enables a hierarchy of nodes, and that supports only
	 * directed edges.
	 */
	private HierarchicalDirectedGraph forest;

	public BrowsableForestNetwork(BrowsableNetwork originalNetwork) {
		this(originalNetwork, null);
	}

	public BrowsableForestNetwork(BrowsableNetwork originalNetwork,
			Collection<Node> roots) {
		super(false);

		this.forest = graphModel.getHierarchicalDirectedGraphVisible();
		this.originalNetwork = originalNetwork;

		if (roots == null)
			roots = getRoots(this.forest);

		createTree(roots);
		System.out.println("ILIAS: Creating tree!");
	}

	private void createTree(Collection<Node> roots) {
		MinimumSpanningTree mst = new MinimumSpanningTree(this.forest, roots);
		this.forest = mst.create();

		// Filter only the edges that belong to the spanning tree
		AbstractAttributeFilter edgeFilter = mst.getEdgeFilter();
		edgeFilter.init(forest);

		FilterController filterController = Lookup.getDefault().lookup(
				FilterController.class);

		Query query = filterController.createQuery(edgeFilter);
		GraphView view = filterController.filter(query);

		graphModel.setVisibleView(view);
		forest = graphModel.getHierarchicalDirectedGraphVisible();
	}

	public BrowsableNetwork getOriginalNetwork() {

		// reset the Visible graph view; create a new one, duplicate of the
		// default
		graphModel.destroyView(graphModel.getVisibleView());
		graphModel.setVisibleView(graphModel.newView());

		// create new wrapper object for the graph
		originalNetwork = new BrowsableNetwork(originalNetwork);
		return originalNetwork;
	}

	public int getDegree(Vertex vertex) {
		return forest.getDegree(vertex.getInternalNode());
	}

	@Override
	public int getEdgeCount() {
		return forest.getEdgesTree().toArray().length;
	}

	@Override
	public Collection<Edge> getEdges() {
		Collection<Edge> edgesTree = new HashSet<Edge>();

		for (org.gephi.graph.api.Edge e : forest.getEdgesTree()) {
			edgesTree.add(new Edge(e));
		}

		return edgesTree;
	}

	public int getPredecessorCount(Vertex vertex) {
		int cnt = 0;
		Node n = forest.getParent(vertex.getInternalNode());

		while (n != null) {
			cnt++;
			n = forest.getParent(n);
		}

		return cnt;
	}

	public Collection<Vertex> getPredecessors(Vertex vertex) {
		Collection<Vertex> predecessors = new HashSet<Vertex>();
		Node n = forest.getParent(vertex.getInternalNode());

		while (n != null) {
			predecessors.add(new Vertex(n));
			n = forest.getParent(n);
		}

		return predecessors;
	}

	public Collection<Vertex> getSuccessors(Vertex vertex) {
		Collection<Vertex> successors = new HashSet<Vertex>();

		for (Node n : forest.getDescendant(vertex.getInternalNode())) {
			successors.add(new Vertex(n));
		}

		return successors;
	}

	public int getSuccessorCount(Vertex vertex) {
		return forest.getDescendantCount(vertex.getInternalNode());
	}

	@Override
	public int getVertexCount() {
		return forest.getNodesTree().toArray().length;
	}

	@Override
	public Collection<Vertex> getVertices() {
		Collection<Vertex> vertices = new ArrayList<Vertex>(
				forest.getNodeCount());

		for (Node node : forest.getNodesTree())
			vertices.add(new Vertex(node));

		return vertices;
	}

	public boolean isPredecessor(Vertex v1, Vertex v2) {
		return forest.isAncestor(v1.getInternalNode(), v2.getInternalNode());
	}

	public boolean isSuccessor(Vertex v1, Vertex v2) {
		return forest.isDescendant(v1.getInternalNode(), v2.getInternalNode());
	}

	@Override
	public void applyShadows() {
		super.applyShadows();
	}

	@Override
	public boolean isIncremental() {
		return super.isIncremental();
	}

	public Collection<Vertex> getTreeRoots() {
		Collection<Vertex> roots = new HashSet<Vertex>();

		for (Node n : forest.getTopNodes()) {
			roots.add(new Vertex(n));
		}

		return roots;
	}

	public int getChildCount(Vertex v) {
		return forest.getChildrenCount(v.getInternalNode());
	}

	public Collection<Edge> getChildEdges(Vertex v) {
		Collection<Edge> childEdges = new HashSet<Edge>();

		Node p = v.getInternalNode();
		for (Node n : forest.getChildren(p)) {
			org.gephi.graph.api.Edge internal = forest.getEdge(p, n);

			if (internal != null) {
				Edge e = new Edge(internal);
				childEdges.add(e);
			}
		}

		return childEdges;
	}

	public Collection<Vertex> getChildren(Vertex v) {
		Collection<Vertex> children = new HashSet<Vertex>();

		for (Node n : forest.getChildren(v.getInternalNode())) {
			children.add(new Vertex(n));
		}

		return children;
	}

	public Vertex getParent(Vertex v) {
		return new Vertex(forest.getParent(v.getInternalNode()));
	}

	public Edge getParentEdge(Vertex v) {
		Node n = v.getInternalNode();
		Node p = forest.getParent(n);

		return new Edge(forest.getEdge(p, n));
	}

	/**
	 * This is a private method that checks all vertices and extracts the root
	 * vertices into a collection.
	 * 
	 * @return A collection with the root nodes of the Forest
	 */
	private static Collection<Node> getRoots(Graph graph) {
		Collection<Node> roots = new ArrayList<Node>();

		for (Node n : graph.getNodes().toArray()) {
			boolean isRoot = true;
			for (org.gephi.graph.api.Edge e : graph.getEdges(n).toArray()) {
				if (e.getTarget().equals(n)) {
					isRoot = false;
					break;
				}
			}

			if (isRoot)
				roots.add(n);
		}
		return roots;
	}

	// TODO ilias: Check these two methods
	public boolean update(long passedTime) {
		boolean result = false;
		if (originalNetwork instanceof ISimulation) {
			result = ((ISimulation) originalNetwork).update(200);

			createTree(getRoots(forest));
			/*
			 * HierarchicalDirectedGraph updatedForest = Lookup.getDefault()
			 * .lookup(GraphController.class).getModel()
			 * .getHierarchicalDirectedGraph(); new
			 * MinimumSpanningForest(updatedForest, getRoots(forest)); forest =
			 * updatedForest;
			 */
		}
		return result;
	}

	public void reset() {
		if (originalNetwork instanceof ISimulation) {
			((ISimulation) originalNetwork).reset();

			createTree(getRoots(forest));
			/*
			 * HierarchicalDirectedGraph updatedForest = Lookup.getDefault()
			 * .lookup(GraphController.class).getModel()
			 * .getHierarchicalDirectedGraph(); new
			 * MinimumSpanningForest(updatedForest, getRoots(forest)); forest =
			 * updatedForest;
			 */
		}
	}
}
