package ch.ethz.sg.cuttlefish.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

public class KCoreLayout<V, E>  extends AbstractLayout<V,E> implements IterativeContext {

	private static Map<Vertex, Double> rho;
	private static Map<Vertex, Integer> coreness;
	private static List<Vertex> shell;
	private static Map< Integer, List<Vertex> > cluster;
	
	protected KCoreLayout(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	public void initialize() {
	
	}

	@Override
	public void reset() {
		System.out.println("Reset");
	}

	@Override
	public boolean done() {
		System.out.println("Done");
		return false;
	}

	@Override
	public void step() {
		System.out.println("Step");
	}
	
	private static List<Vertex> getNeighborsWithHigherCoreness(Graph<Vertex, Edge> g, Vertex v) {
		List<Vertex> neighborsWithHigherCoreness = new ArrayList<Vertex>();
		int neighborsWithHeigherCoreness = 0;
		for( Edge e : g.getIncidentEdges(v) ) {					
			Vertex n = g.getOpposite(v, e);
			if( coreness.get(n) > coreness.get(v) ) {
				neighborsWithHigherCoreness.add(n);
			}
				
		}
		return neighborsWithHigherCoreness;
	}
	
	public static void main(String args[]) {
		System.out.println("KCore test");
		Graph<Vertex, Edge> g = new SparseGraph<Vertex, Edge>();
		Vertex[] vertices = new Vertex[12];		
		for(int i = 0; i <= 11; ++i) {
			if (i == 7) continue;
			vertices[i] = new Vertex(i);			
			g.addVertex(vertices[i]);
		}
		Edge[] edges = new Edge[14];
		for(int i = 0; i < 14; ++i) {
			edges[i] = new Edge();
		}
		g.addEdge(edges[0], vertices[0], vertices[1]);
		g.addEdge(edges[1], vertices[0], vertices[2]);
		g.addEdge(edges[2], vertices[0], vertices[3]);
		g.addEdge(edges[3], vertices[1], vertices[2]);
		g.addEdge(edges[4], vertices[1], vertices[3]);
		g.addEdge(edges[5], vertices[1], vertices[8]);
		g.addEdge(edges[6], vertices[1], vertices[9]);
		g.addEdge(edges[7], vertices[1], vertices[10]);
		g.addEdge(edges[8], vertices[2], vertices[3]);
		g.addEdge(edges[9], vertices[2], vertices[4]);
		g.addEdge(edges[10], vertices[2], vertices[5]);
		g.addEdge(edges[11], vertices[3], vertices[6]);		
		g.addEdge(edges[12], vertices[8], vertices[11]);
		g.addEdge(edges[13], vertices[9], vertices[10]);
		coreness = new HashedMap<Vertex, Integer>();
		coreness.put(vertices[0], 3);
		coreness.put(vertices[1], 3);
		coreness.put(vertices[2], 3);
		coreness.put(vertices[3], 3);
		coreness.put(vertices[8], 2);
		coreness.put(vertices[9], 2);
		coreness.put(vertices[10], 2);
		coreness.put(vertices[4], 1);
		coreness.put(vertices[5], 1);
		coreness.put(vertices[6], 1);
		//coreness.put(vertices[7], 1);
		coreness.put(vertices[11], 1);
		double epsilon = 0.18;
		int cmax = 3;
		rho = new HashMap<Vertex, Double>();
		
		for(Vertex v : g.getVertices() ) {
			int sum = 0;
			for(Vertex n : getNeighborsWithHigherCoreness(g, v) ) {
				sum += cmax - coreness.get(n); 
			}
			double r = (1 - epsilon) * (cmax - coreness.get(v) ) + 
				(epsilon / getNeighborsWithHigherCoreness(g, v).size() ) * sum;
			rho.put(v, r);
		}
		for(Vertex v : g.getVertices() ) {
			System.out.println(v.getId() + " " + rho.get(v) );
		} 
	}

}
