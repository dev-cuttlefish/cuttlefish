package ch.ethz.sg.cuttlefish.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;

import ch.ethz.sg.cuttlefish.layout.ARF2Layout;
import ch.ethz.sg.cuttlefish.layout.KCoreLayout;
import ch.ethz.sg.cuttlefish.layout.WeightedKCoreLayout;
import ch.ethz.sg.cuttlefish.misc.CxfSaver;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.util.EdgeType;

public class TestWeightedKCore {

	private boolean TEST_WITH_VALIDATION_FILES = true;

	/*
	 * Does not work now; Weighted kcore assigns increasing core indices while
	 * simple kcore does not
	 */
	private boolean TEST_COMPARE_WITH_KCORE = false;

	/**
	 * This test validates the Weighted KCore algorithm against examples stored
	 * in a list of files. There is one file that describes the network and one
	 * file that describes the correct Weighted KCore layout. The test computes
	 * the layout and checks it against the provided result.
	 * 
	 * The test is successful when the core assignments returned by the
	 * algorithm are the same as the ones provided in files.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testWithValidationFiles() throws IOException {
		if (!TEST_WITH_VALIDATION_FILES) {
			return;
		}
		log("\tTest: Weighted KCore Layout with existing examples");

		String[] networkFiles = { /*
								 * "ORBIS.CON.dat",
								 * "US.500.Largest.Airports.dat",
								 * "RND_network_2009.dat",
								 */
		"CEL.dat" };

		String[] resultFiles = { /*
								 * "ORBIS.CON-Results.dat",
								 * "US.500.Largest.Airports-Results.dat",
								 * "RND_network_2009-Results.dat",
								 */"CEL-Results.dat" };

		int successCount = 0;

		for (int i = 0; i < networkFiles.length; ++i) {
			log("\t\t* " + networkFiles[i] + " ...");
			WeightedKCoreFileTester tester = new WeightedKCoreFileTester(
					networkFiles[i], resultFiles[i]);

			if (tester.assertAlgorithm()) {
				successCount++;
				log("\t\t\tTest succeeded.");
			} else {
				tester.saveNetwork(networkFiles[i] + ".cxf");
				log("\t\t\tTest failed. Network saved as \'" + networkFiles[i]
						+ ".cxf\'");
			}
		}

		log("\t\t" + successCount + "/" + networkFiles.length
				+ " networks were tested successfully.\n");
		Assert.assertEquals(networkFiles.length, successCount);
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
	@Test
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
		Layout<Vertex, Edge> layout = new ARF2Layout<Vertex, Edge>(net, true,
				Integer.MAX_VALUE);
		Set<Integer> indices = new HashSet<Integer>();
		Map<Vertex, Integer> kcoreness, wcoreness;

		KCoreLayout<Vertex, Edge> kcore = new KCoreLayout<Vertex, Edge>(
				layout.getGraph(), layout);
		kcoreness = kcore.getCoreness();

		WeightedKCoreLayout<Vertex, Edge> kcoreWeighted = new WeightedKCoreLayout<Vertex, Edge>(
				layout.getGraph(), layout, 1, 1);
		wcoreness = kcoreWeighted.getCoreness();

		if (saveToFile) {
			try {
				String fname = rng.saveAsCxf(net, kcoreWeighted);
				log("   Weighted KCore layout saved as \"" + fname + "\".");
			} catch (IOException e1) {
				log("   Could not save layout.");
				e1.printStackTrace();
			}
		}

		log("   Validating the match of layouts with unit weights...");
		for (Vertex v : kcoreness.keySet()) {
			Assert.assertTrue(wcoreness.containsKey(v));
			Assert.assertEquals(kcoreness.get(v), wcoreness.get(v));
			indices.add(wcoreness.get(v));
		}

		int max = Collections.max(indices);
		int min = Collections.min(indices);
		Set<Integer> sequence = new HashSet<Integer>();
		for (int i = min; i <= max; ++i) {
			sequence.add(i);
		}

		Assert.assertEquals(sequence, indices);
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

	private static final String DIR_PREFIX = "test_wkcore/";
	private static final boolean DEBUG_PRINT = true;

	private BrowsableNetwork network;
	private WeightedKCoreLayout<Vertex, Edge> kcoreWeightedLayout;
	private Map<Vertex, Integer> computedCoreness, expectedCoreness;
	private Map<String, Vertex> vertices;

	public WeightedKCoreFileTester(String networkFile, String resultFile)
			throws IOException {
		expectedCoreness = new HashMap<Vertex, Integer>();
		vertices = new HashMap<String, Vertex>();
		network = new BrowsableNetwork();

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
				int exp = expectedCoreness.get(v);
				int comp = computedCoreness.get(v);
				sameCores = (exp == comp);
				if (!sameCores)
					break;
			}
		}

		if (DEBUG_PRINT)
			System.out.println("\n\n=========  DEBUG  =========");

		if (!sameCores && DEBUG_PRINT) {
			printCores(true);
			printCoreIndices();
		} else if (!sameSize && DEBUG_PRINT) {
			System.out.println("Coreness size\n\nExpected: "
					+ expectedCoreness.size() + "\nComputed: "
					+ computedCoreness.size());
		}

		if (DEBUG_PRINT)
			System.out.println("=======  DEBUG END  =======\n\n");

		return sameSize && sameCores;
	}

	public void saveNetwork(String networkFile) {
		CxfSaver saver = new CxfSaver(network, kcoreWeightedLayout);
		saver.save(new File(DIR_PREFIX + networkFile));
	}

	private void loadNetwork(String networkFile) throws IOException {
		int vID = 0, eID = 0;
		String line, fromLab, toLab;
		double weight;
		String[] tokens;

		BufferedReader reader = new BufferedReader(new FileReader(DIR_PREFIX
				+ networkFile));
		while ((line = reader.readLine()) != null) {
			line = line.replaceAll("\t", " ");
			line = line.replaceAll("\\s+", " ");

			tokens = line.split(" ");
			fromLab = tokens[0];
			toLab = tokens[1];
			weight = Double.parseDouble(tokens[2]);
			Vertex from = getVertex(vID++, fromLab, true);
			Vertex to = getVertex(vID++, toLab, true);
			Edge e = new Edge();
			e.setId(eID++);
			e.setWeight(weight);

			if (fromLab.equalsIgnoreCase(toLab))
				System.out.println("WARNING: Loop edge at " + toLab);

			network.addEdge(e, from, to, EdgeType.UNDIRECTED);
		}
		reader.close();
	}

	private void loadResult(String resultFile) throws IOException {
		String line;
		String core, name;
		int maxCore = 0;

		BufferedReader reader = new BufferedReader(new FileReader(DIR_PREFIX
				+ resultFile));

		// Always skip the first line containing headers
		reader.readLine();

		while ((line = reader.readLine()) != null) {
			/*
			 * if (line.contains("#")) { continue; }
			 */

			// Replace all tabs and spaces with a single space
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
		Layout<Vertex, Edge> layout = new ARF2Layout<Vertex, Edge>(network,
				true, Integer.MAX_VALUE);
		kcoreWeightedLayout = new WeightedKCoreLayout<Vertex, Edge>(
				layout.getGraph(), layout, 1, 1);
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
		System.out.println("\nFound " + cnt + " diverging core assignments\n");
		System.out.println("Node\tExp\tComp\n" + msg);
	}

	private void printCoreIndices() {
		Set<Integer> cores = new TreeSet<Integer>();
		System.out.print("Core sets\n\nExpected: ");
		for (Integer i : expectedCoreness.values())
			cores.add(i);
		System.out.println(cores);

		System.out.print("Computed: ");
		cores.clear();
		for (Integer i : computedCoreness.values())
			cores.add(i);
		System.out.println(cores + "\n");
	}
}
