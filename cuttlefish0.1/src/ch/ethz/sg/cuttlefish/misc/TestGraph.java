package ch.ethz.sg.cuttlefish.misc;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class TestGraph extends DirectedSparseGraph<Vertex,Edge> {

	private static final long serialVersionUID = 1L;

	public TestGraph() {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		for(int i =0; i<10; i++){
			Vertex v = new Vertex(i);
			addVertex(v);
			vertices.add(v);
		}
		
		addEdge(new Edge(),vertices.get(0), vertices.get(1));
		addEdge(new Edge(), vertices.get(1), vertices.get(2));
		addEdge(new Edge(), vertices.get(2), vertices.get(0));
		
		addEdge(new Edge(), vertices.get(2), vertices.get(4));
		addEdge(new Edge(), vertices.get(4), vertices.get(2));
		
		addEdge(new Edge(), vertices.get(6), vertices.get(7));
		addEdge(new Edge(), vertices.get(7), vertices.get(6));
		
		addEdge(new Edge(), vertices.get(8), vertices.get(1));
		addEdge(new Edge(), vertices.get(1), vertices.get(9));
	}

}
