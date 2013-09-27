package ch.ethz.sg.cuttlefish.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.gephi.graph.api.Node;
import org.gephi.io.exporter.spi.Exporter;

import ch.ethz.sg.cuttlefish.exporter.NetworkExportController;
import ch.ethz.sg.cuttlefish.layout.LayoutLoader;
import ch.ethz.sg.cuttlefish.layout.kcore.KCoreLayout;
import ch.ethz.sg.cuttlefish.layout.kcore.WeightedKCoreLayout;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class TestWeightedKCore {

	private boolean TEST_WITH_VALIDATION_FILES = true;
	private boolean TEST_COMPARE_WITH_KCORE = false;

	private LayoutLoader layoutLoader;

	/**
	 * This test validates the Weighted KCore algorithm against examples stored
	 * in a list of files. There is one file that describes the network and one
	 * file that describes the correct Weighted KCore layout. The test computes
	 * the layout and checks it against the provided result.
	 * 
	 * The test is successful when the core assignments returned by the
	 * algorithm are the same as the ones provided in files.
	 * 
	 * @throws Exception
	 */
	public void testWithValidationFiles() throws Exception {
		if (!TEST_WITH_VALIDATION_FILES) {
			return;
		}
		log("Test: Weighted KCore Layout with existing examples");

		String[] networkFiles = { "ORBIS.CON.dat" };
		String[] resultFiles = { "ORBIS.CON-Results.dat" };
		int successCount = 0;

		for (int i = 0; i < networkFiles.length; ++i) {
			log("   Testing network \"" + networkFiles[i] + "\"...");
			WeightedKCoreFileTester tester = new WeightedKCoreFileTester(
					networkFiles[i], resultFiles[i], layoutLoader);

			if (tester.assertAlgorithm()) {
				successCount++;
				log("       Test OK.");
			} else {
				tester.saveNetwork(networkFiles[i] + ".cxf");
				log("       Network " + networkFiles[i]
						+ " failed. Saved as \'" + networkFiles[i] + ".cxf\'");
			}
		}

		if (networkFiles.length == successCount)
			log("   " + successCount + "/" + networkFiles.length
					+ " networks were validated successfully!\n");

	}

	/**
	 * This test validates the Weighted KCore algorithm against the simple KCore
	 * algorithm. The Weighted KCore algorithm should return exactly the same
	 * core assignment as the simple KCore, if all edge weights are set to 1.
	 * The test creates a random network, runs both algorithms for unit edge
	 * weights and compares the result.
	 * 
	 * The test is successful if the two core assignments returned by the two
	 * algorithms are identical.
	 */
	public void testCompareWithKCore() {
		if (!TEST_COMPARE_WITH_KCORE) {
			return;
		}

		boolean saveToFile = false;

		log("Test: Weighted KCore Layout (comparison with KCore)");

		RandomNetworkGenerator rng = new RandomNetworkGenerator();
		rng.directedEdges(false);
		rng.setAllEdgesWeight(1.0);
		rng.allowLoopEdges(false);

		BrowsableNetwork net = rng.generateNetwork();
		Set<Integer> indices = new HashSet<Integer>();
		Map<Node, Integer> kcoreness;
		Map<Node, Integer> wcoreness;

		KCoreLayout kcore = (KCoreLayout) layoutLoader.getLayout("kcore");
		kcoreness = kcore.getCoreness();

		WeightedKCoreLayout kcoreWeighted = (WeightedKCoreLayout) layoutLoader
				.getLayout("weighted-kcore");
		wcoreness = kcoreWeighted.getCoreness();

		if (saveToFile) {
			try {
				String fname = rng.saveAsCxf(net);
				log("   Weighted KCore layout saved as \"" + fname + "\".");
			} catch (Exception e) {
				log("   Could not save layout.");
				e.printStackTrace();
			}
		}

		log("   Validating the match of layouts with unit weights...");
		for (Node v : kcoreness.keySet()) {
			if (!wcoreness.containsKey(v)) {
				System.err.println("Node doesn't exist: " + v);
				System.exit(-1);
			}

			if (kcoreness.get(v) != wcoreness.get(v)) {
				System.err.println("Different coreness for node " + v);
				System.exit(-1);
			}

			indices.add(wcoreness.get(v));
		}

		int max = Collections.max(indices);
		int min = Collections.min(indices);
		Set<Integer> sequence = new HashSet<Integer>();
		for (int i = min; i <= max; ++i) {
			sequence.add(i);
		}

		if (sequence == indices) 
			log("   Completed successfully!\n");
	}

	private void log(String message) {
		StringBuilder sb = new StringBuilder();

		sb.append(this.getClass().getSimpleName());
		sb.append(": ");
		sb.append(message);

		System.out.println(sb.toString());
	}
}

class WeightedKCoreFileTester {

	private BrowsableNetwork network;
	private WeightedKCoreLayout kcoreWeightedLayout;
	Map<Node, Integer> computedCoreness;
	private Map<Vertex, Integer> expectedCoreness;
	private Map<String, Vertex> vertices;
	private LayoutLoader layoutLoader;

	public WeightedKCoreFileTester(String networkFile, String resultFile,
			LayoutLoader layoutLoader) throws IOException {
		expectedCoreness = new HashMap<Vertex, Integer>();
		vertices = new HashMap<String, Vertex>();
		network = new BrowsableNetwork();
		this.layoutLoader = layoutLoader;

		loadNetwork(networkFile);
		loadResult(resultFile);
	}

	public boolean assertAlgorithm() {
		boolean sameSize = true;
		boolean sameCores = true;

		computeCoreness();
		if (computedCoreness.size() != expectedCoreness.size()) {
			sameSize = false;
		} else {
			for (Vertex v : expectedCoreness.keySet()) {
				if (expectedCoreness.get(v) != computedCoreness.get(v)) {
					sameCores = false;
					break;
				}
			}
		}

		if (!sameCores) {
			printCores(true);
			printCoreIndices();
		} else if (!sameSize) {
			System.out.println("Different coreness size: Expected: "
					+ expectedCoreness.size() + ", computed: "
					+ computedCoreness.size());
		}

		return sameSize && sameCores;
	}

	public void saveNetwork(String networkFile) throws Exception {
		Exporter exporter = NetworkExportController.getExporter("cxf");
		NetworkExportController.export(network, networkFile, exporter);
	}

	private void loadNetwork(String networkFile) throws IOException {
		int vID = 0, eID = 0;
		String line, fromLab, toLab;
		float weight;
		String[] tokens;

		BufferedReader reader = new BufferedReader(new FileReader(networkFile));
		while ((line = reader.readLine()) != null) {
			line = line.replaceAll("\t", " ");
			line = line.replaceAll("\\s+", " ");

			tokens = line.split(" ");
			fromLab = tokens[0];
			toLab = tokens[1];
			weight = (float) Double.parseDouble(tokens[2]);
			Vertex from = getVertex(vID++, fromLab, true);
			Vertex to = getVertex(vID++, toLab, true);
			Edge e = new Edge(from, to, weight, false);
			e.setId(eID++);

			if (fromLab.equalsIgnoreCase(toLab))
				System.out.println("WARNING: Loop edge at " + toLab);

			network.addEdge(e);
		}
		reader.close();
	}

	private void loadResult(String resultFile) throws IOException {
		String line;
		String core, name;
		int maxCore = 0;

		BufferedReader reader = new BufferedReader(new FileReader(resultFile));
		while ((line = reader.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}
			line = line.replaceAll("\t", " ");
			line = line.replaceAll("\\s+", " ");
			String tokens[] = line.split(" ");
			core = tokens[1];
			name = tokens[0];

			Vertex v = getVertex(0, name, false);
			int c = Integer.parseInt(core);
			if (v != null) {
				expectedCoreness.put(v, c);
			}

			if (c > maxCore) {
				maxCore = c;
			}
		}

		for (Vertex v : expectedCoreness.keySet()) {
			int c = expectedCoreness.get(v);
			expectedCoreness.put(v, maxCore - c + 1);
		}

		reader.close();
	}

	private Vertex getVertex(int id, String label, boolean create) {
		Vertex v;

		if (vertices.containsKey(label)) {
			v = vertices.get(label);
		} else if (create) {
			v = new Vertex(id, label);
			vertices.put(label, v);
		} else {
			v = null;
		}

		return v;
	}

	private void computeCoreness() {
		kcoreWeightedLayout = (WeightedKCoreLayout) layoutLoader
				.getLayout("arf");
		computedCoreness = kcoreWeightedLayout.getCoreness();
	}

	private void printCores(boolean divergingOnly) {
		int cnt = 0;
		String msg = "";

		for (Vertex v : expectedCoreness.keySet()) {
			int e = expectedCoreness.get(v);
			int c = computedCoreness.get(v);

			if (!divergingOnly || e != c) {
				msg += v.getLabel() + "\t" + e + "\t" + c + "\n";
				++cnt;
			}
		}
		System.out.println("\nFound " + cnt + " diverging core assignments");
		System.out.println("Node\tExp\tComp\n" + msg);
	}

	private void printCoreIndices() {
		Set<Integer> cores = new TreeSet<Integer>();
		System.out.println("Expected cores: ");
		for (Integer i : expectedCoreness.values())
			cores.add(i);
		System.out.println(cores);

		System.out.println("Computed cores: ");
		cores.clear();
		for (Integer i : computedCoreness.values())
			cores.add(i);
		System.out.println(cores);
	}
}
