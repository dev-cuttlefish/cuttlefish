package ch.ethz.sg.cuttlefish.networks;

import java.util.ArrayList;
import java.util.Collection;
import ch.ethz.sg.cuttlefish.misc.DelegateForest;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.MinimumSpanningForest;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * A BrowsableNetwork that implements JUNG's Forest interface.
 * Used to link BrowsableNetwork with JUNG's tree layouts. 
 * @author ptsankov
 *
 */
public class BrowsableForestNetwork extends BrowsableNetwork implements ISimulation, Forest<Vertex,Edge> {

	Forest<Vertex, Edge> forest;
	BrowsableNetwork originalNetwork;
	String edgeShape = "line";
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	public BrowsableForestNetwork(BrowsableNetwork originalNetwork) {
		if(originalNetwork instanceof BrowsableForestNetwork) {
			// if the original network is a forest, we are interested in the original network
			this.originalNetwork = ((BrowsableForestNetwork)originalNetwork).getOriginalNetwork();
		} else {
			this.originalNetwork = originalNetwork;
		}
		Forest<Vertex, Edge> forest = new DelegateForest<Vertex, Edge>();		
		new MinimumSpanningForest<Vertex, Edge>(originalNetwork, forest, getRoots(this.originalNetwork));
		this.forest = forest;
	}	
	
	public BrowsableNetwork getOriginalNetwork() {
		return originalNetwork;
	}
	
	@Override
	public int getEdgeCount() {
		return forest.getEdgeCount();
	}
	
	public String getEdgeShape() {
		return edgeShape;
	}
	
	public void setEdgeShape(String s) {
		edgeShape = s;
	}
	
	@Override
	public boolean addEdge(Edge edge, Collection<? extends Vertex> vertices) {
		// TODO Auto-generated method stub
		return forest.addEdge(edge, vertices);
	}
	
	@Override
	public boolean addEdge(Edge arg0, Collection<? extends Vertex> arg1,
			EdgeType arg2) {
		// TODO Auto-generated method stub
		return forest.addEdge(arg0, arg1, arg2);
	}
	
	@Override
	public boolean addEdge(Edge e, Vertex v1, Vertex v2) {
		// TODO Auto-generated method stub
		return forest.addEdge(e, v1, v2);
	}
	
/*	private void deepCopyTree(Map<Vertex, List<Vertex> > visited, Vertex v) {
		if(visited.containsKey(v)) return;
		visited.put(v, new LinkedList<Vertex>() );
		for(Edge e : getIncidentEdges(v) ) {
			Vertex child = getDest(e);
			if(child != v) {
				visited.get(v).add(child);
				deepCopyTree(visited, child);
			}
		}
		forest.removeVertex(v);
	}
	
	private void deepInsertTree(Map<Vertex, List<Vertex> > visited, Vertex v) {
		for(Vertex v2 : visited.get(v) ) {			
			Edge e = new Edge();
			forest.addEdge(e, v, v2, EdgeType.DIRECTED);
			deepInsertTree(visited, v2);
		}
	}*/
	
	@Override
	public boolean addEdge(Edge e, Vertex v1, Vertex v2, EdgeType edge_type) {
		//Map<Vertex, List<Vertex> > visited = new HashMap<Vertex, List<Vertex> >();
		//deepCopyTree(visited, v2);
		forest.addEdge(e, v1, v2, EdgeType.DIRECTED);
		//deepInsertTree(visited, v2);
		return true;
	}
	@Override
	public boolean containsEdge(Edge edge) {
		// TODO Auto-generated method stub
		return forest.containsEdge(edge);
	}
	@Override
	public boolean containsVertex(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.containsVertex(vertex);
	}
	@Override
	public int degree(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.degree(vertex);
	}
	@Override
	public Edge findEdge(Vertex v1, Vertex v2) {
		// TODO Auto-generated method stub
		return forest.findEdge(v1, v2);
	}
	@Override
	public Collection<Edge> findEdgeSet(Vertex v1, Vertex v2) {
		// TODO Auto-generated method stub
		return forest.findEdgeSet(v1, v2);
	}
	@Override
	public EdgeType getDefaultEdgeType() {
		// TODO Auto-generated method stub
		return forest.getDefaultEdgeType();
	}
	@Override
	public Vertex getDest(Edge directed_edge) {
		// TODO Auto-generated method stub
		return forest.getDest(directed_edge);
	}
	@Override
	public int getEdgeCount(EdgeType edge_type) {
		// TODO Auto-generated method stub
		return forest.getEdgeCount(edge_type);
	}
	@Override
	public Collection<Edge> getEdges() {
		// TODO Auto-generated method stub
		return forest.getEdges();
	}
	@Override
	public Collection<Edge> getEdges(EdgeType edgeType) {
		// TODO Auto-generated method stub
		return forest.getEdges(edgeType);
	}
	@Override
	public EdgeType getEdgeType(Edge edge) {
		// TODO Auto-generated method stub
		return forest.getEdgeType(edge);
	}
	@Override
	public Pair<Vertex> getEndpoints(Edge edge) {
		// TODO Auto-generated method stub
		return forest.getEndpoints(edge);
	}
	@Override
	public int getIncidentCount(Edge edge) {
		// TODO Auto-generated method stub
		return forest.getIncidentCount(edge);
	}	
	
	@Override
	public Collection<Edge> getIncidentEdges(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.getIncidentEdges(vertex);
	}
	@Override
	public Collection<Vertex> getIncidentVertices(Edge edge) {
		// TODO Auto-generated method stub
		return forest.getIncidentVertices(edge);
	}
	@Override
	public Collection<Edge> getInEdges(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.getInEdges(vertex);
	}
	@Override
	public int getNeighborCount(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.getNeighborCount(vertex);
	}
	@Override
	public Collection<Vertex> getNeighbors(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.getNeighbors(vertex);
	}
	@Override
	public Vertex getOpposite(Vertex vertex, Edge edge) {
		// TODO Auto-generated method stub
		return forest.getOpposite(vertex, edge);
	}
	@Override
	public Collection<Edge> getOutEdges(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.getOutEdges(vertex);
	}
	@Override
	public int getPredecessorCount(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.getPredecessorCount(vertex);
	}
	@Override
	public Collection<Vertex> getPredecessors(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.getPredecessors(vertex);
	}
	@Override
	public Vertex getSource(Edge directed_edge) {
		// TODO Auto-generated method stub
		return forest.getSource(directed_edge);
	}
	@Override
	public Collection<Vertex> getSuccessors(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.getSuccessors(vertex);
	}
	@Override
	public int getVertexCount() {
		// TODO Auto-generated method stub
		return forest.getVertexCount();
	}
	@Override
	public Collection<Vertex> getVertices() {
		// TODO Auto-generated method stub
		return forest.getVertices();
	}
	@Override
	public int inDegree(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.inDegree(vertex);
	}
	@Override
	public boolean isDest(Vertex vertex, Edge edge) {
		// TODO Auto-generated method stub
		return forest.isDest(vertex, edge);
	}
	@Override
	public boolean isIncident(Vertex vertex, Edge edge) {
		// TODO Auto-generated method stub
		return forest.isIncident(vertex, edge);
	}
	@Override
	public boolean isNeighbor(Vertex v1, Vertex v2) {
		// TODO Auto-generated method stub
		return forest.isNeighbor(v1, v2);
	}
	@Override
	public boolean isPredecessor(Vertex v1, Vertex v2) {
		// TODO Auto-generated method stub
		return forest.isPredecessor(v1, v2);
	}
	@Override
	public boolean isSource(Vertex vertex, Edge edge) {
		// TODO Auto-generated method stub
		return forest.isSource(vertex, edge);
	}
	@Override
	public boolean isSuccessor(Vertex v1, Vertex v2) {
		// TODO Auto-generated method stub
		return forest.isSuccessor(v1, v2);
	}
	@Override
	public int outDegree(Vertex vertex) {
		// TODO Auto-generated method stub
		return forest.outDegree(vertex);
	}
	@Override
	public boolean removeEdge(Edge edge) {
		// TODO Auto-generated method stub
		return forest.removeEdge(edge);
	}
	@Override
	public boolean removeVertex(Vertex arg0) {
		// TODO Auto-generated method stub
		return forest.removeVertex(arg0);
	}
	@Override
	public boolean addEdge(Edge edge, Pair<? extends Vertex> endpoints) {
		// TODO Auto-generated method stub
		return forest.addEdge(edge, endpoints);
	}
	@Override
	public boolean addEdge(Edge edge, Pair<? extends Vertex> endpoints,
			EdgeType edgeType) {
		// TODO Auto-generated method stub
		return forest.addEdge(edge, endpoints, edgeType);
	}
	@Override
	public void applyShadows() {

		super.applyShadows();
	}

	@Override
	public boolean isIncremental() {

		return super.isIncremental();
	}
	
	
	@Override
	public boolean addVertex(Vertex v) {
		return forest.addVertex(v);		
	}
	
	@Override
	public int getSuccessorCount(Vertex vertex) {		
		return forest.getSuccessorCount(vertex);
	}

	
	@Override
	public Collection< Tree<Vertex,Edge>> getTrees() {
		return forest.getTrees();
	}

	@Override
	public int getChildCount(Vertex v) {
		return forest.getChildCount(v);
	}

	@Override
	public Collection<Edge> getChildEdges(Vertex v) {
		return forest.getChildEdges(v);
	}

	@Override
	public Collection<Vertex> getChildren(Vertex v) {
		return forest.getChildren(v);		
	}

	@Override
	public Vertex getParent(Vertex v) {
		return forest.getParent(v);
	}

	@Override
	public Edge getParentEdge(Vertex v) {
		return forest.getParentEdge(v);
	}
	
	/**
	 * This is a private method that checks all vertices and extracts
	 * the root vertices into a collection.
	 * @return A collection with the root nodes of the Forest
	 */
	private Collection<Vertex> getRoots(BrowsableNetwork network) {
		Collection<Vertex> roots = new ArrayList<Vertex>();
		for(Vertex v : network.getVertices() ) {
			boolean isRoot = true;
			for(Edge e : network.getIncidentEdges(v) ) {
				if(network.getDest(e) == v) {
					isRoot = false;
					break;
				}
			}
			if(isRoot)
				roots.add(v);
		}
		return roots;
	}

	@Override
	public boolean update(long passedTime) {
		boolean result = false;
		if(originalNetwork instanceof ISimulation) {
			result = ((ISimulation) originalNetwork).update(200);
			Forest<Vertex, Edge> updatedForest = new DelegateForest<Vertex, Edge>();
			new MinimumSpanningForest<Vertex, Edge>(originalNetwork, updatedForest , getRoots(originalNetwork));
			forest = updatedForest;
		}
		return result;
	}

	@Override
	public void reset() {
		if(originalNetwork instanceof ISimulation) {
			((ISimulation) originalNetwork).reset();
			Forest<Vertex, Edge> updatedForest = new DelegateForest<Vertex, Edge>();
			new MinimumSpanningForest<Vertex, Edge>(originalNetwork, updatedForest , getRoots(originalNetwork));			
			forest = updatedForest;
		}
	}

}
