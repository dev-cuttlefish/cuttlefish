package ch.ethz.sg.cuttlefish.layout.kcore;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;

import ch.ethz.sg.cuttlefish.Cuttlefish;
import ch.ethz.sg.cuttlefish.layout.LayoutLoader;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class WeightedKCoreLayout implements Layout {

	private LayoutBuilder layoutBuilder = null;
	private GraphModel graphModel = null;
	private Graph graph = null;

	private static final double EPSILON = 0.18;
	private static final double RHO_SCALE = 400;

	private Map<Node, Integer> coreness;
	private Map<Node, Double> rho;
	private Map<Node, Double> alpha;
	private int cmax;
	private double cmaxRadius = Double.MAX_VALUE;

	private int coreIndex = 0;
	private double lastCore = 0;
	private Map<Edge, Float> normalizedWeight = new HashMap<Edge, Float>();
	private double paramAlpha;
	private double paramBeta;
	private int maxDegree;

	private boolean finished = false;

	public WeightedKCoreLayout(LayoutBuilder layoutBuilder, double paramAlpha,
			double paramBeta) {
		this.layoutBuilder = layoutBuilder;
		this.paramAlpha = paramAlpha;
		this.paramBeta = paramBeta;
	}

	@Override
	public void initAlgo() {
		if (graphModel == null) {
			throw new RuntimeException(
					"The GraphModel for this layout cannot be null!");
		}
		graph = graphModel.getGraphVisible();

		coreness = new HashMap<Node, Integer>();
		rho = new HashMap<Node, Double>();
		alpha = new HashMap<Node, Double>();

		computeGraphCoreness();

		// check if all vertices have the same coreness
		// and if yes, randomly places the nodes
		if (singleCoreCheck()) {
			return;
		}

		cmax = -1;
		maxDegree = -1;
		finished = false;

		for (Node v : graph.getNodes()) {
			if (cmax < coreness.get(v))
				cmax = coreness.get(v);

			if (graph.getDegree(v) > maxDegree)
				maxDegree = graph.getDegree(v);
		}

		computeRho(graph, cmax);
		computeAlpha(graph, cmax);

		if (LayoutLoader.VERBOSE_LAYOUT)
			Cuttlefish.debug(this, "Layout initialized with parameters: a = "
					+ paramAlpha + ", b = " + paramBeta);
	}

	@Override
	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
	}

	@Override
	public void goAlgo() {
		double maxSize = 10;

		for (Node v : graph.getNodes()) {
			double r;
			
			if (!rho.containsKey(v))
				continue;
			
			if (rho.get(v).equals(java.lang.Double.NaN) || rho.get(v) == 0d) {
				r = (new Random()).nextDouble() / 2 * cmaxRadius;
			} else {
				r = rho.get(v);
			}
			double x = r * Math.cos(alpha.get(v));
			double y = r * Math.sin(alpha.get(v));
			Vertex vertex = new Vertex(v);
			double origDegree = Math.log(graph.getDegree(v));
			if (origDegree < 1)
				origDegree = 1;
			float hue = (float) coreness.get(v) / (float) cmax;
			
			vertex.setSize((float) (maxSize * origDegree / Math.log(maxDegree)));
			vertex.setFillColor(Color.getHSBColor(hue, 1f, 1f));
			vertex.setPosition(x * RHO_SCALE / cmax, y * RHO_SCALE / cmax);
		}

		finished = true;

		if (LayoutLoader.VERBOSE_LAYOUT)
			Cuttlefish.debug(this, "Layout completed");
	}

	@Override
	public boolean canAlgo() {
		return !finished;
	}

	@Override
	public void endAlgo() {
		if (LayoutLoader.VERBOSE_LAYOUT) {
			Cuttlefish.debug(this, "Layout ended");

			for (Node n : graphModel.getGraph().getNodes()) {
				Cuttlefish.debug(this, n + ": " + n.getNodeData().x() + ", "
						+ n.getNodeData().y());
			}
		}
	}

	@Override
	public LayoutProperty[] getProperties() {
		List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
		return properties.toArray(new LayoutProperty[0]);
	}

	@Override
	public void resetPropertiesValues() {
		cmax = -1;
		cmaxRadius = Double.MAX_VALUE;
		coreIndex = 0;
		lastCore = 0;
		finished = false;
		maxDegree = -1;
		paramAlpha = 1;
		paramBeta = 1;
	}

	@Override
	public LayoutBuilder getBuilder() {
		return layoutBuilder;
	}

	private void computeGraphCoreness() {
		Map<Node, Integer> degree = new HashMap<Node, Integer>();
		Map<Node, Integer> weightedDegree = new HashMap<Node, Integer>();
		coreIndex = 0;

		for (Node v : graph.getNodes()) {
			degree.put(v, graph.getDegree(v));
		}

		normalizeWeights(graph.getEdges());
		computeWeightedDegree(degree, weightedDegree, paramAlpha, paramBeta);

		int minWeight;
		while (!weightedDegree.isEmpty()) {
			// Get a vertex of minimum degree

			minWeight = Collections.min(weightedDegree.values());
			computeCore(weightedDegree, degree, minWeight);
		}
	}

	private void normalizeWeights(final EdgeIterable edges) {
		normalizedWeight = new HashMap<Edge, Float>();

		for (Edge e : edges) {
			normalizedWeight.put(e, ((Edge) e).getWeight());
		}

		float minimumWeight = Collections.min(normalizedWeight.values());

		for (Edge e : normalizedWeight.keySet()) {
			float w = normalizedWeight.get(e) / minimumWeight;
			normalizedWeight.put(e, w);
		}
	}

	private void computeWeightedDegree(final Map<Node, Integer> degree,
			Map<Node, Integer> weightedDegree, final double alpha,
			final double beta) {

		double weightSum;
		double w;

		weightedDegree.clear();
		for (Node v : degree.keySet()) {
			weightSum = 0;

			for (Edge e : graph.getEdges(v)) {
				Node opposite = graph.getOpposite(v, e);
				if (degree.containsKey(opposite)) {
					weightSum += normalizedWeight.get(e);
				}
			}

			w = Math.pow(degree.get(v), alpha) * Math.pow(weightSum, beta);
			w = Math.pow(w, 1 / (alpha + beta));
			weightedDegree.put(v, (int) Math.round(w));
		}
	}

	private void computeCore(Map<Node, Integer> weightedDegree,
			Map<Node, Integer> degree, final double core) {
		List<Node> shell = new ArrayList<Node>();

		if (core > lastCore) {
			coreIndex++;
			lastCore = core;
		}

		for (Node v : weightedDegree.keySet()) {
			if (weightedDegree.get(v) <= core) {
				coreness.put(v, coreIndex);
				shell.add(v);
			}
		}

		for (Node v : shell) {
			degree.remove(v);
			weightedDegree.remove(v);

			for (Edge e : graph.getEdges(v)) {
				Node adjacentV = graph.getOpposite(v, e);

				if (degree.containsKey(adjacentV)) {
					int newDegree = degree.get(adjacentV) - 1;
					degree.put(adjacentV, newDegree);
				}
			}
		}

		computeWeightedDegree(degree, weightedDegree, paramAlpha, paramBeta);
	}

	private boolean singleCoreCheck() {
		Set<Integer> cores = new HashSet<Integer>();

		for (Node v : coreness.keySet()) {
			if (!cores.contains(coreness.get(v))) {
				cores.add(coreness.get(v));
			}
		}

		if (cores.size() == 1) {
			Random r = new Random();
			for (Node v : graph.getNodes()) {
				v.getNodeData().setX(
						(float) r.nextDouble() * 500
								* (r.nextBoolean() ? 1 : 0));

				v.getNodeData().setY(
						(float) r.nextDouble() * 500
								* (r.nextBoolean() ? 1 : 0));
			}
			return true;
		}
		return false;
	}

	private void computeRho(Graph g, int cmax) {
		for (Node v : g.getNodes()) {
			int sum = 0;

			List<Node> neighbors = getNeighborsWithHigherCoreness(g, v);

			for (Node n : neighbors) {
				sum += cmax - coreness.get(n);
			}
			double r = (1 - EPSILON) * (cmax - coreness.get(v))
					+ (EPSILON / neighbors.size()) * sum;
			if (r > 0 && cmaxRadius > r)
				cmaxRadius = r;
			rho.put(v, r);
		}
	}

	private List<Node> getNeighborsWithHigherCoreness(Graph graph, Node v) {
		List<Node> neighborsWithHigherCoreness = new ArrayList<Node>();

		for (Edge e : graph.getEdges(v)) {
			Node n = graph.getOpposite(v, e);
			if (coreness.get(n) >= coreness.get(v)) {
				neighborsWithHigherCoreness.add(n);
			}

		}
		return neighborsWithHigherCoreness;
	}

	private void computeAlpha(Graph g, int cmax) {
		for (int shellId = 0; shellId <= cmax; shellId++) {

			// compute vertices that belong to this shell
			List<Node> shell = new ArrayList<Node>();
			for (Node v : g.getNodes()) {
				if (coreness.get(v) == shellId)
					shell.add(v);
			}

			// compute clusters for that shell
			List<List<Node>> clusters = new ArrayList<List<Node>>();
			for (Node v : shell) {
				List<Node> newCluster = new ArrayList<Node>();
				newCluster.add(v);
				List<List<Node>> merge = new ArrayList<List<Node>>();

				for (List<Node> c : clusters) {
					boolean merged = false;
					for (Node cv : c) {
						if (g.getEdge(v, cv) != null) {
							merge.add(c);
							merged = true;
							break;
						}
					}
					if (merged)
						continue;
				}
				for (List<Node> c : merge) {
					newCluster.addAll(c);
					clusters.remove(c);
				}
				clusters.add(newCluster);
			}

			// compute alphas
			for (Node v : shell) {

				double a = 0;
				int ci = 0;
				for (int clusterId = 0; clusterId < clusters.size(); ++clusterId) {
					if (clusters.get(clusterId).contains(v)) {
						ci = clusterId;
						break;
					}
				}

				assert (ci != -1);
				for (int clusterId = 0; clusterId < ci; ++clusterId) {
					a += (double) clusters.get(clusterId).size()
							/ (double) shell.size();
				}

				a = a * 2 * Math.PI;
				Random rand = new Random();
				double gaussianRandom = rand.nextGaussian();
				gaussianRandom *= Math.PI * clusters.get(ci).size()
						/ shell.size();
				gaussianRandom += clusters.get(ci).size() / (2 * shell.size());
				a += gaussianRandom;
				alpha.put(v, a);
			}
		}
	}

	public void setAlpha(double alpha) {
		this.paramAlpha = alpha;
	}

	public void setBeta(double beta) {
		this.paramBeta = beta;
	}

	public Map<Node, Integer> getCoreness() {
		return coreness;
	}
}
