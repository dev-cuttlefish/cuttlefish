package ch.ethz.sg.cuttlefish.layout.kcore;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;

import ch.ethz.sg.cuttlefish.Cuttlefish;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class KCoreLayout implements Layout {

	private static final double EPSILON = 0.18;
	private static final double RHO_SCALE = 400;

	private LayoutBuilder layoutBuilder = null;
	private GraphModel graphModel = null;
	private Graph graph = null;

	private Map<Node, Integer> coreness;
	private Map<Node, Double> rho;
	private Map<Node, Double> alpha;
	private int cmax;
	private double cmaxRadius;
	private int maxDegree;

	private boolean finished;

	public KCoreLayout(LayoutBuilder layoutBuilder) {
		this.layoutBuilder = layoutBuilder;
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
		if (singleCoreCheck())
			return;

		cmax = -1;
		maxDegree = -1;
		cmaxRadius = Double.MAX_VALUE;
		finished = false;

		for (Node n : graph.getNodes()) {

			if (cmax < coreness.get(n))
				cmax = coreness.get(n);

			if (graph.getDegree(n) > maxDegree)
				maxDegree = graph.getDegree(n);
		}

		computeRho(graph, cmax);
		computeAlpha(graph, cmax);

		if (Cuttlefish.VERBOSE_LAYOUT)
			Cuttlefish.debug(this, "Layout initialized.");
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

			vertex.setSize((float) (maxSize * origDegree / Math.log(maxDegree)));
			float hue = (float) coreness.get(v) / (float) cmax;
			vertex.setFillColor(Color.getHSBColor(hue, 1f, 1f));

			v.getNodeData().setX((float) (x * RHO_SCALE / cmax));
			v.getNodeData().setY((float) (y * RHO_SCALE / cmax));
		}

		finished = true;

		if (Cuttlefish.VERBOSE_LAYOUT)
			Cuttlefish.debug(this, "Layout completed.");
	}

	@Override
	public boolean canAlgo() {
		return !finished;
	}

	@Override
	public void endAlgo() {
		if (Cuttlefish.VERBOSE_LAYOUT) {
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
		maxDegree = -1;
		cmaxRadius = Double.MAX_VALUE;
		finished = false;
	}

	@Override
	public LayoutBuilder getBuilder() {
		return layoutBuilder;
	}

	private void computeGraphCoreness() {
		Map<Node, Integer> degree = new HashMap<Node, Integer>();

		for (Node n : graph.getNodes()) {
			degree.put(n, graph.getDegree(n));
		}

		while (!degree.isEmpty()) {
			// Get a vertex of minimum degree
			Node minDegreeV = null;
			for (Node n : degree.keySet()) {
				if (minDegreeV == null
						|| degree.get(minDegreeV) > degree.get(n)) {
					minDegreeV = n;
				}
			}

			computeCore(degree, degree.get(minDegreeV));
		}
	}

	private void computeCore(Map<Node, Integer> degree, int core) {
		Color c = new Color(new Random().nextInt());
		List<Node> shell = new ArrayList<Node>();

		for (Node n : degree.keySet()) {
			if (degree.get(n) <= core) {
				coreness.put(n, core);
				shell.add(n);
				new Vertex(n).setFillColor(c);
			}
		}

		for (Node v : shell) {
			degree.remove(v);
			for (Edge e : graph.getEdges(v)) {
				Node adjacentV = graph.getOpposite(v, e);

				if (degree.containsKey(adjacentV)) {
					int newDegree = degree.get(adjacentV) - 1;
					degree.remove(adjacentV);
					degree.put(adjacentV, newDegree);
				}
			}
		}
	}

	private boolean singleCoreCheck() {
		Set<Integer> cores = new HashSet<Integer>();
		for (Node n : coreness.keySet()) {
			if (!cores.contains(coreness.get(n))) {
				cores.add(coreness.get(n));
			}
		}
		if (cores.size() == 1) {
			Random r = new Random();

			for (Node n : graph.getNodes()) {
				n.getNodeData().setX(
						r.nextFloat() * 500 * (r.nextBoolean() ? 1 : 0));
				n.getNodeData().setY(
						r.nextFloat() * 500 * (r.nextBoolean() ? 1 : 0));
			}
			return true;
		}
		return false;
	}

	private void computeRho(Graph g, int cmax) {
		for (Node n1 : g.getNodes()) {
			int sum = 0;
			List<Node> neighbors = getNeighborsWithHigherCoreness(g, n1);

			for (Node n2 : neighbors) {
				sum += cmax - coreness.get(n2);
			}

			double r = (1 - EPSILON) * (cmax - coreness.get(n1))
					+ (EPSILON / neighbors.size()) * sum;

			if (r > 0 && cmaxRadius > r)
				cmaxRadius = r;

			rho.put(n1, r);
		}
	}

	private List<Node> getNeighborsWithHigherCoreness(Graph g, Node node) {
		List<Node> neighborsWithHigherCoreness = new ArrayList<Node>();

		for (Edge e : g.getEdges(node)) {
			Node opp = g.getOpposite(node, e);

			if (coreness.get(opp) >= coreness.get(node)) {
				neighborsWithHigherCoreness.add(opp);
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
	
	public Map<Node, Integer> getCoreness() {
		return coreness;
	}
}
