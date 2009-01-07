package ch.ethz.sg.cuttlefish.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.misc.Edge;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class StronglyConnectedComponentCompressor {
	public static final String LEVEL = "__"+Class.class.toString() + "_level";

	public static final String CLUSTER = "__"+Class.class.toString() + "_cluster";

	Set<Vertex> verteces = new HashSet<Vertex>();
	
	Set<Vertex> currentComponent;
	private DirectedSparseGraph<Vertex,Edge> graph;


	
	@SuppressWarnings("unchecked")
	public static void main(String[] args){
		DirectedSparseGraph graph = new TestGraph();
		StronglyConnectedComponentCompressor compressor = new StronglyConnectedComponentCompressor(graph);
		Set<Set<Vertex>> components =   compressor.compress();
		
		int compInd = 0;
		for (Set<Vertex> comp : components){
			compInd++;
			for (Vertex v : comp){
				System.out.println(v + " component="+compInd);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Vertex> topologicalSort(DirectedSparseGraph<Vertex, Edge> graph){
		ArrayList<Vertex> list = new ArrayList<Vertex>();
		Hashtable<Integer, Vertex> vertexMap = new Hashtable<Integer, Vertex>();
			for(Vertex v: (Set<Vertex>)graph.getVertices()){
				Integer key =  v.getId();
//				if(key==null){
//					v.addUserDatum(SGUserData.ID, v.toString(), UserData.SHARED);
//					key = v.toString();
//				}
				vertexMap.put(key, v);
			}
			
			
			// we clone the graph by hand
			DirectedSparseGraph clonedGraph = new DirectedSparseGraph<Vertex, Edge>();
			
			for (Vertex vertex : graph.getVertices()){
				clonedGraph.addVertex(vertex);
			}
			for (Edge edge : graph.getEdges()){
				clonedGraph.addVertex(edge);
			}
			
			
			StronglyConnectedComponentCompressor compressor = new StronglyConnectedComponentCompressor(clonedGraph);
			compressor.compress();
			
			int level=0;
			int size=0;
			
			while(clonedGraph.getVertices().size() > 0){
				
				//System.out.println(level+""+ clonedGraph.getVertices());
				
				Set<Vertex> sources = getSourceVertices(clonedGraph);
				
				for (Vertex rVert : sources)
					clonedGraph.removeVertex(rVert);
				List<Vertex> tempList = compressor.expand(sources);
				if(size == clonedGraph.getVertexCount()){
					System.out.println("ERROR:" + clonedGraph.getVertices());
					break;
				}
				size = clonedGraph.getVertexCount();
				
				for(Vertex v:tempList){
					Vertex realVertex = vertexMap.get(v.getId()); 
					list.add(realVertex);
				}
				level++;
			}
		
		System.gc();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private List<Vertex> expand(Set<Vertex> vertices){
		ArrayList<Vertex> list = new ArrayList<Vertex>();
		for(Vertex v: vertices){
			List<Vertex> merged = (List<Vertex>)v.getUserDatum(this);
			if(merged!=null){
				list.addAll(merged);
			}else{
				list.add(v);
			}
			
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private static Set<Vertex> getSourceVertices(DirectedSparseGraph graph){
		HashSet<Vertex> list = new HashSet<Vertex>();
		for(Vertex v: (Set<Vertex>)graph.getVertices()){
			if(v.getPredecessors().size()==0){
				list.add(v);
			}
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public StronglyConnectedComponentCompressor(DirectedSparseGraph graph) {
	
		this.graph = graph;
		verteces.addAll(graph.getVertices());
	}

	@SuppressWarnings("unchecked")
	public Set<Set<Vertex>> compress(){
		for(Vertex root: verteces){
			if(graph.getVertices().contains(root)){
				compress(root);
				//System.out.println(root);
			}
		}
		
		HashSet<Set<Vertex>> clusters = new HashSet<Set<Vertex>>();
		
		int cluster = 0;
		for(Vertex vertex: (Set<Vertex>)graph.getVertices()){
			vertex.setUserDatum(StronglyConnectedComponentCompressor.CLUSTER, new Integer(cluster), UserData.CLONE);
			HashSet<Vertex> clusterVertices = new HashSet<Vertex>();
			for(Vertex mergedVertex: (List<Vertex>)vertex.getUserDatum(this)){
				mergedVertex.setUserDatum(StronglyConnectedComponentCompressor.CLUSTER, new Integer(cluster), UserData.CLONE);
				clusterVertices.add(mergedVertex);
			}
			clusters.add(clusterVertices);
			cluster++;
		}
		
		return clusters;
	}

	private void compress(Vertex root) {
		currentComponent = new HashSet<Vertex>();
		currentComponent.add(root);
		DFS(root);
		merge(currentComponent);
	}
	private void DFS(Vertex vertex){
		DFS(vertex, new ArrayList<Vertex>(), new HashSet<Vertex>());
	}
	@SuppressWarnings("unchecked")
	private void DFS(Vertex vertex, List<Vertex> path, Set<Vertex> set){
		//Set<Vertex> pathTemp = new HashSet<Vertex>();
		//pathTemp.addAll(path);
		
		if(set.contains(vertex)){
			if(currentComponent.contains(vertex)){
			    currentComponent.addAll(path);
			  //  System.out.println("found one: " + path);
			}
			//path.remove(path.size()-1);
			return;
		}else{
			path.add(vertex);
			set.add(vertex);
		}
		
		for(Vertex successor: (Set<Vertex>)vertex.getSuccessors()){
			DFS(successor, path,set);
		}
		path.remove(path.size()-1);
		set.remove(path.size()-1);
	}
	
	@SuppressWarnings("unchecked")
	private void merge(Set<Vertex> vertexSet) {
		Set<Vertex> in = new HashSet<Vertex>();
		Set<Vertex> out = new HashSet<Vertex>();
		Vertex newVertex = new DirectedSparseVertex();
		
		
		//Set<Vertex> allAlreadyMergedVerteces = new HashSet<Vertex>();
		//Set<Vertex> tempVerteces = new HashSet<Vertex>();
		
		for(Vertex vertex:vertexSet){
			in.addAll(vertex.getPredecessors());
			out.addAll(vertex.getSuccessors());
			//Set<Vertex> alreadyMergedVerteces = (Set<Vertex>) vertex.getUserDatum(this);
			
			//if(alreadyMergedVerteces!=null){
			//	allAlreadyMergedVerteces.addAll(alreadyMergedVerteces);
			//	tempVerteces.add(vertex);
			//}
			try {
				graph.removeVertex(vertex);
			} catch (Exception e) {
				System.err.println(vertex);
				e.printStackTrace();
			}
			
		}
		in.removeAll(vertexSet);
		out.removeAll(vertexSet);
		
		graph.addVertex(newVertex);
		
		for(Vertex vertex:in){
			graph.addEdge(new DirectedSparseEdge(vertex, newVertex));
		}		
		for(Vertex vertex:out){
			graph.addEdge(new DirectedSparseEdge(newVertex, vertex));
		}
		//vertexSet.addAll(allAlreadyMergedVerteces);
		//vertexSet.removeAll(tempVerteces);
		//newVertex.setUserDatum(this, vertexSet, UserData.CLONE);
		newVertex.setUserDatum(this, expand(vertexSet), UserData.CLONE);
	}
	
	
}
