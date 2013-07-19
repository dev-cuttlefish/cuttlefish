package ch.ethz.sg.cuttlefish.layout;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections15.map.HashedMap;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;

public class KCoreLayout<V, E>  extends AbstractLayout<V,E> {

	//private static final double EPSILON = 0.18;
	private static final double EPSILON = 0.18;
	private static final double RHO_SCALE = 400;
	
	private Map<V, Integer> coreness;
	private Map<V, Double> rho;
	private Map<V, Double> alpha;
	private int cmax;
	private double cmaxRadius = Double.MAX_VALUE;
	
	public KCoreLayout(Graph<V, E> graph, Layout<Vertex,Edge> layout) {
		super(graph);
		initialize();
	}

	@Override
	public void initialize() {
		coreness = new HashedMap<V, Integer>();
		rho = new HashMap<V, Double>();
		alpha = new HashMap<V, Double>();

		computeGraphCoreness(getGraph());
		//check if all vertices have the same coreness
		//and if yes, randomly places the nodes
		if(singleCoreCheck())
			return;
		cmax = -1;
		int maxDegree = -1;
		double maxSize = 10;
		for(V v : getGraph().getVertices() ) {			
			if(cmax < coreness.get(v))
				cmax = coreness.get(v);
			if(getGraph().degree(v) > maxDegree)
				maxDegree = getGraph().degree(v);			 
		}
		computeRho(getGraph(), cmax);		
		computeAlpha(getGraph(), cmax);
		for(V v : getGraph().getVertices()) {			
			double r;
			if(rho.get(v).equals(java.lang.Double.NaN) || rho.get(v) == 0d) {	
				r = (new Random()).nextDouble()/2 * cmaxRadius;
			} else {
				r = rho.get(v);
			}
			double x = r*Math.cos(alpha.get(v));
			double y = r*Math.sin(alpha.get(v));
			Vertex vertex = (Vertex)v;			
			double origDegree = Math.log(getGraph().degree(v));
			if(origDegree < 1)
				origDegree = 1;
			vertex.setSize(maxSize * origDegree / Math.log(maxDegree) );
			float hue = (float)coreness.get(v)/(float)cmax;
			vertex.setFillColor(Color.getHSBColor(hue, 1f, 1f));
			locations.put(v, new Point2D.Double(x*RHO_SCALE/cmax,y*RHO_SCALE/cmax));
		}
	}

	@Override
	public void reset() {
		System.out.println("Reset");
		initialize();
	}
	
	private List<V> getNeighborsWithHigherCoreness(Graph<V, E> g, V v) {
		List<V> neighborsWithHigherCoreness = new ArrayList<V>();
		for( E e : g.getIncidentEdges(v) ) {					
			V n = g.getOpposite(v, e);
			if( coreness.get(n) >= coreness.get(v) ) {
				neighborsWithHigherCoreness.add(n);
			}
				
		}
		return neighborsWithHigherCoreness;
	}
	
	private void computeRho(Graph<V, E> g, int cmax) {
		for(V v : g.getVertices() ) {
			int sum = 0;
			for(V n : getNeighborsWithHigherCoreness(g, v) ) {
				sum += cmax - coreness.get(n); 
			}
			double r = (1 - EPSILON) * (cmax - coreness.get(v) ) + 
				(EPSILON / getNeighborsWithHigherCoreness(g, v).size() ) * sum;
			if(r > 0 && cmaxRadius > r)
				cmaxRadius = r;
			rho.put(v, r);
		}
	}
	
	private boolean singleCoreCheck() {
		Set<Integer> cores = new HashSet<Integer>();
		for(V v : coreness.keySet()) {
			if(!cores.contains(coreness.get(v)) ) {
				cores.add(coreness.get(v));
			}
		}
		if(cores.size() == 1) {
			Random r = new Random();
			for(V v : getGraph().getVertices() ) {
				locations.put(v, new Point2D.Double(r.nextDouble()*500*(r.nextBoolean() ? 1 : 0),r.nextDouble()*500*(r.nextBoolean() ? 1 : 0)));
			}
			return true;
		}
		return false;
	}
	
	private void computeAlpha(Graph<V, E> g, int cmax) {
		for(int shellId = 0; shellId <= cmax; shellId++) {
			//compute vertices that belong to this shell
			List<V> shell = new ArrayList<V>();
			for(V v : g.getVertices() ) {
				if(coreness.get(v) == shellId)
					shell.add(v);
			}			
			
			//compute clusters for that shell
			List< List<V> > clusters = new ArrayList< List<V> >();
			for(V v : shell) {
				List<V> newCluster = new ArrayList<V>();
				newCluster.add(v);
				List< List<V> > merge = new ArrayList< List<V> >();
				for(List<V> c : clusters) {
					boolean merged = false;
					for(V cv : c) {
						if(g.findEdge(v, cv) != null) {
							merge.add(c);
							merged = true;
							break;
						}
					}
					if(merged)
						continue;
				}
				for(List<V> c : merge) {
					newCluster.addAll(c);
					clusters.remove(c);
				}
				clusters.add(newCluster);
			}
			
			//compute alphas
			for(V v : shell) {
				double a = 0;
				int ci = 0;
				for(int clusterId = 0; clusterId < clusters.size(); ++clusterId ) {
					if(clusters.get(clusterId).contains(v) ) {
						ci = clusterId;
						break;
					}
				}
				assert(ci != -1);
				for(int clusterId = 0; clusterId < ci; ++clusterId) {
					a += (double)clusters.get(clusterId).size() / (double)shell.size();
				}
				a = a * 2 * Math.PI;
				Random rand = new Random();
				double gaussianRandom = rand.nextGaussian();
				gaussianRandom *= Math.PI * clusters.get(ci).size() / shell.size();
				gaussianRandom += clusters.get(ci).size() / (2*shell.size());
				a += gaussianRandom;
				alpha.put(v, a);
			}
		}

	}
	
	private void computeGraphCoreness(Graph<V, E> g) {
		Map<V, Integer> degree = new HashMap<V, Integer>();
		for(V v : g.getVertices() ) {
			degree.put(v, g.degree(v));
		}
				

		while(!degree.isEmpty()) {
			// Get a vertex of minimum degree
			V minDegreeV = null;
			for(V v : degree.keySet() ) {
				if(minDegreeV == null || degree.get(minDegreeV) > degree.get(v) ) {
					minDegreeV = v;					
				}
			}
			computeCore(degree, degree.get(minDegreeV));
		}
	}
	
	private void computeCore(Map<V, Integer> degree, int core) {
		Color c = new Color(new Random().nextInt());
		List<V> shell = new ArrayList<V>();
		for(V v : degree.keySet() ) {
			if(degree.get(v) <= core) {
				coreness.put(v, core);
				shell.add(v);
				((Vertex)v).setFillColor(c);
			}
		}		
		for(V v : shell) {
			degree.remove(v);
			for(E e : getGraph().getIncidentEdges(v) ) {
				V adjacentV = getGraph().getOpposite(v, e);
				if(degree.containsKey(adjacentV) ) {
					int newDegree = degree.get(adjacentV) - 1;
					degree.remove(adjacentV);
					degree.put(adjacentV, newDegree);
				}
			}
		}		
	}

	public Map<V, Integer> getCoreness() {
		Map<V, Integer> coreness = new HashMap<V, Integer>();
		coreness.putAll(this.coreness);
		
		return coreness;
	}
}
