/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

	This file is part of Cuttlefish.
	
 	Cuttlefish is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
 */

package ch.ethz.sg.cuttlefish.networks;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.MixedGraph;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

import ch.ethz.sg.cuttlefish.misc.Pair;

public class BrowsableNetwork implements Serializable {

	private static final long serialVersionUID = -6058687290083932065L;

	private static String name = BrowsableNetwork.class.getName();

	public static BrowsableNetwork loadExistingNetwork() {
		return new BrowsableNetwork(false);
	}

	/*
	 * The GraphModel interface contains the graph data structure and is used to
	 * create graph objects on demand. BrowsableNetwork is modelled as a
	 * MixedGraph, while BrowsableForestNetwork as a HierarchicalDirectedGraph.
	 * Nevertheless, the data structure is the same.
	 */
	protected GraphModel graphModel;

	/*
	 * A MixedGraph enables access to the graph and allows both directed and
	 * undirected edges.
	 */
	private MixedGraph graph;

	private Hashtable<String, String> arguments = new Hashtable<String, String>();
	protected boolean networkLoaded = false;
	private boolean incremental = false;
	private boolean directed = false;

	/**
	 * Creates a new Network accessor interface by clearing the underlying graph
	 * structure.
	 */
	public BrowsableNetwork() {
		init();
	}

	/**
	 * Creates a Network accessor object without modifying the underlying graph
	 * structure.
	 * 
	 * @param clearGraph
	 */
	public BrowsableNetwork(boolean clearGraph) {
		this.graphModel = Lookup.getDefault().lookup(GraphController.class)
				.getModel();

		this.graph = graphModel.getMixedGraphVisible();

		if (clearGraph)
			this.graph.clear();
	}

	protected BrowsableNetwork(BrowsableNetwork other) {
		this(false);
		this.arguments = other.arguments;
		this.incremental = other.incremental;
		this.networkLoaded = other.networkLoaded;
	}

	public void init() {
		this.graphModel = Lookup.getDefault().lookup(GraphController.class)
				.getModel();
		this.graph = graphModel.getMixedGraphVisible();
		this.graph.clear();
	}

	public void updateAnnotations() {
	}

	public final void setArguments(Hashtable<String, String> args) {
		arguments = args;
	}

	public final String getArgument(String name) {
		return arguments.get(name);
	}

	public final String getName() {
		return name;
	}

	public final void colorAll(Color color) {
		for (Node node : graph.getNodes())
			Vertex.setFillColor(node, color);
	}

	protected final void setColor(Vertex v, Color c2) {
		if (v != null) {
			v.setBorderColor(c2);
		}
	}

	protected final void addColor(Vertex v, Color c2) {
		if (v != null) {
			Color c = v.getFillColor();
			Color nc;
			if (c != null)
				nc = maxColor(c2, c);
			else
				nc = c2;

			v.setFillColor(nc);
		}
	}

	protected final Color maxColor(Color c1, Color c2) {
		int red = Math.max(c1.getRed(), c2.getRed());
		int green = Math.max(c1.getGreen(), c2.getGreen());
		int blue = Math.max(c1.getBlue(), c2.getBlue());

		return new Color(red, green, blue);
	}

	protected final void setShadowed(Vertex v, boolean b) {
		v.setShadowed(b);
	}

	public final void copyIDsToLabels() {
		for (Node node : graph.getNodes())
			Vertex.copyIDToLabel(node);
	}

	public void applyShadows() {
		for (Vertex vertex : getVertices()) {
			boolean isShadowed = vertex.isShadowed();
			if (isShadowed) {
				Color shadowColor = Color.white;
				Color c = vertex.getFillColor();
				if (c != null)
					shadowColor = c;

				shadowColor = new Color(shadowColor.getRed(),
						shadowColor.getGreen(), shadowColor.getBlue(), 200);

				vertex.setFillColor(Color.LIGHT_GRAY);
				vertex.setSize(5);

				for (org.gephi.graph.api.Edge edge : graph.getEdges()) {
					Edge.setColor(edge, Color.LIGHT_GRAY);
					Edge.setWidth(edge, 0.5);
				}
			}
		}

	}

	public boolean isDirected(Edge e) {
		return graph.isDirected(e.getInternalEdge());
	}

	public boolean isIncremental() {
		return incremental;
	}

	public void setIncremental(boolean inc) {
		incremental = inc;
	}

	public boolean isNetworkLoaded() {
		return networkLoaded;
	}

	public void setNetworkLoaded(boolean networkLoaded) {
		this.networkLoaded = networkLoaded;
	}

	public int getVertexCount() {
		return graph.getNodeCount();
	}

	public boolean isEmpty() {
		return (graph.getNodeCount() == 0);
	}

	public int getEdgeCount() {
		return graph.getEdgeCount();
	}

	public int getEdgeCount(boolean directed) {
		int d = 0;
		int u = 0;

		for (org.gephi.graph.api.Edge e : graph.getEdges()) {
			if (e.isDirected())
				d++;
			else
				u++;
		}

		return directed ? d : u;
	}

	public Edge randomEdge() {
		int edgeIndex = (int) (Math.random() * getEdgeCount());
		Iterator<Edge> itEdge = getEdges().iterator();
		Edge e = null;

		if (edgeIndex == 0)
			return itEdge.next();

		while ((edgeIndex >= 0) && itEdge.hasNext()) {
			edgeIndex--;
			e = itEdge.next();
		}

		return e;
	}

	public Vertex randomVertex() {
		if (isEmpty())
			return null;

		int vertexIndex = (int) (Math.random() * getVertexCount());
		Iterator<Vertex> itVertex = getVertices().iterator();
		Vertex v = null;

		if (vertexIndex == 0)
			return itVertex.next();

		while ((vertexIndex >= 0) && itVertex.hasNext()) {
			vertexIndex--;
			v = itVertex.next();
		}

		return v;
	}

	public Collection<Vertex> getVertices() {
		Collection<Vertex> vertices = new HashSet<Vertex>();

		for (Node node : graph.getNodes())
			vertices.add(new Vertex(node));

		return vertices;
	}

	/**
	 * For compatibility reasons
	 * 
	 * @param e
	 * @return
	 */
	public Vertex getSource(Edge e) {
		return e.getSource();
	}

	/**
	 * For compatibility reasons
	 * 
	 * @param e
	 * @return
	 */
	public Vertex getDest(Edge e) {
		return e.getTarget();
	}

	public Collection<Edge> getIncidentEdges(Vertex vertex) {
		Collection<Edge> edges = new ArrayList<Edge>(graph.getEdgeCount());

		for (org.gephi.graph.api.Edge e : graph.getEdges(vertex
				.getInternalNode())) {
			edges.add(new Edge(e));
		}

		return edges;
	}

	public Collection<Edge> getOutEdges(Vertex v) {
		Collection<Edge> outEdges = new ArrayList<Edge>();

		for (org.gephi.graph.api.Edge e : graph.getEdges(v.getInternalNode())) {

			if (e.getSource().equals(v.getInternalNode())) {
				outEdges.add(new Edge(e));
			}
		}

		return outEdges;
	}

	public Collection<Edge> getInEdges(Vertex v) {
		Collection<Edge> inEdges = new ArrayList<Edge>();

		for (org.gephi.graph.api.Edge e : graph.getEdges(v.getInternalNode())) {

			if (e.getTarget().equals(v.getInternalNode())) {
				inEdges.add(new Edge(e));
			}
		}

		return inEdges;
	}

	public Vertex getOpposite(Vertex v, Edge e) {
		Node opp = graph.getOpposite(v.getInternalNode(), e.getInternalEdge());
		return new Vertex(opp);
	}

	public final int getMaxDegree() {
		int degree = 0;

		for (Node n : graph.getNodes()) {
			degree = Math.max(degree, graph.getDegree(n));
		}

		return degree;
	}

	public void clearGraph() {
		graph.clear();
	}

	public void clearEdges() {
		graph.clearEdges();
	}

	public void clearEdges(Vertex v) {
		graph.clearEdges(v.getInternalNode());
	}

	public Collection<Vertex> getNeighbors(Vertex v) {
		Collection<Vertex> neighbors = new ArrayList<Vertex>();

		for (Node n : graph.getNeighbors(v.getInternalNode()))
			neighbors.add(new Vertex(n));

		return neighbors;
	}

	public int getNeighborCount(Vertex v) {
		return graph.getNeighbors(v.getInternalNode()).toArray().length;
	}

	public void removeVertex(Vertex v) {
		graph.removeNode(v.getInternalNode());

		if (getVertexCount() == 0)
			clearGraph();
	}

	public Collection<Edge> getEdges() {
		Collection<Edge> edges = new HashSet<Edge>();

		for (org.gephi.graph.api.Edge e : graph.getEdges()) {
			edges.add(new Edge(e));
		}

		return edges;
	}

	public Pair<Vertex> getEndpoints(Edge e) {
		return new Pair<Vertex>(e.getSource(), e.getTarget());
	}

	public boolean containsVertex(Vertex v) {
		return graph.contains(v.getInternalNode());
	}

	public boolean containsEdge(Edge e) {
		return graph.contains(e.getInternalEdge());
	}

	public void addVertices(Collection<Vertex> vertices) {
		for (Vertex v : vertices) {
			graph.addNode(v.getInternalNode());
		}
	}

	public void addVertex(Vertex v) {
		graph.addNode(v.getInternalNode());
	}

	public Edge findEdge(Vertex v1, Vertex v2) {
		if (graph.getEdge(v1.getInternalNode(), v2.getInternalNode()) != null)
			return new Edge(graph.getEdge(v1.getInternalNode(),
					v2.getInternalNode()));
		else
			return null;
	}

	public void addEdges(Collection<Edge> edges) {
		for (Edge e : edges) {
			addEdge(e);
		}
	}

	public void addEdge(Edge e) {
		graph.addEdge(e.getInternalEdge());

		if (e.isDirected()) {
			this.directed = true;
		}
	}

	public void addEdge(Vertex v1, Vertex v2) {
		if (v1 == null || v2 == null)
			throw new RuntimeException("The edge endpoints cannot be null!");

		if (!containsVertex(v1)) {
			addVertex(v1);
		}

		if (!containsVertex(v2)) {
			addVertex(v2);
		}

		addEdge(new Edge(v1, v2));
	}

	public void removeEdge(Edge e) {
		graph.removeEdge(e.getInternalEdge());

		if (getEdgeCount() == 0)
			clearEdges();
	}

	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	public void fixVertices(boolean fixed) {
		for (Node n : graph.getNodes())
			n.getNodeData().setFixed(fixed);
	}

	public String toString() {
		return name;
	}

	public void printNetwork() {

		System.out.println("Nodes: " + getVertexCount());
		System.out.println(getVertices());

		System.out.println("Edges: " + getEdgeCount());
		System.out.println(getEdges());
	}
}
