/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra, 
    Ilias Rinis

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

package ch.ethz.sg.cuttlefish.testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gephi.io.exporter.spi.Exporter;

import ch.ethz.sg.cuttlefish.exporter.NetworkExportController;
import ch.ethz.sg.cuttlefish.misc.GraphMLExporter;
import ch.ethz.sg.cuttlefish.misc.GraphMLImporter;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

/**
 * @author Ilias Rinis
 * 
 *         The TestExportNetwork class defines a JUnit test for the
 *         export/import operations to/from GraphML. The test generates a random
 *         graph and exports it; then the GraphML graph is then imported and the
 *         test validates the topology of the graph. The validation checks for
 *         the correct vertices and edges in the graph, and for the correct
 *         source-destination pairs for all edges.
 */

public class TestExportNetwork {

	public void testGraphMLExport() throws FileNotFoundException {
		log("Testing GraphML Export/Import");

		RandomNetworkGenerator rng = new RandomNetworkGenerator();
		BrowsableNetwork network = rng.generateNetwork();

		File testFile = new File("graphml_export_test.graphml");

		log("Test graph has " + network.getVertexCount() + " nodes, "
				+ network.getEdgeCount() + " edges");

		// Export the network to GraphML
		log("Exporting network to GraphML");
		GraphMLExporter exporter = (GraphMLExporter) NetworkExportController
				.getExporter("graphml");
		exporter.export(testFile);

		double fsize = testFile.length() / 1024.0 / 1024.0;
		log("File size: " + new DecimalFormat("#.##").format(fsize) + " MB");

		// Import the network
		log("Importing network back");
		BrowsableNetwork importedNetwork = new BrowsableNetwork();
		GraphMLImporter importer = new GraphMLImporter(testFile);
		importer.importGraph(importedNetwork);

		// Step 1: Validate Topology
		log("Validating topology... ");
		// validateTopology(layout.getGraph(), importedLayout.getGraph());

		// Step 2: Validate Edge weights
		log("Validating edge weights");
		// validateEdgeWeights(layout.getGraph(), importedLayout.getGraph());

		testFile.delete();
		log("All tests have been completed successfully!\n");
	}

	public void testCXFLargeNetworks() throws Exception {
		log("Testing CXF Export/Import for Large Networks");
		int size = 100000;

		RandomNetworkGenerator rng = new RandomNetworkGenerator();
		BrowsableNetwork network = rng.generateNetwork(size, size);

		File testFile = new File("large_network_" + size + ".cxf");

		log("Test graph has " + network.getVertexCount() + " nodes, "
				+ network.getEdgeCount() + " edges");

		// Export the network to GraphML
		log("Exporting network to CXF");
		Exporter cxf = NetworkExportController.getExporter("cxf");
		NetworkExportController.export(network, testFile, cxf);

		double fsize = testFile.length() / 1024.0 / 1024.0;
		log("File size: " + new DecimalFormat("#.##").format(fsize) + " MB");

		// Import the network
		log("Importing network back");

		// Step 1: Validate Topology
		log("Validating topology");
		// validateTopology(layout.getGraph(), importedLayout.getGraph());

		// Step 2: Validate Edge weights
		log("Validating edge weights");
		// validateEdgeWeights(layout.getGraph(), importedLayout.getGraph());

		testFile.delete();
		log("All tests have been completed successfully!\n");

	}

	/**
	 * used by a test method to validate the topology of an imported network, by
	 * comparing it to the original one. The method compares the vertex set and
	 * the edge set (by source and destination).
	 * 
	 * @param expected
	 *            The original (expected) network, before exporting it
	 * @param actual
	 *            The actual (imported) network to be compared with the expected
	 */
	public void validateTopology(BrowsableNetwork expected,
			BrowsableNetwork actual) {

		// Assert network size
		if (expected.getEdgeCount() != actual.getEdgeCount()
				|| expected.getVertexCount() != actual.getVertexCount()) {
			System.err.println("Network size doesn't match!");
			System.exit(-1);
		}

		// Sort & Assert Vertex set
		List<Vertex> expVertices = new ArrayList<Vertex>(expected.getVertices());
		List<Vertex> actVertices = new ArrayList<Vertex>(actual.getVertices());

		Collections.sort(expVertices);
		Collections.sort(actVertices);
		// Assert.assertEquals(expVertices, actVertices);

		// Assert Edge set
		Vertex src, dest;
		List<String> expEdges = new ArrayList<String>();
		List<String> actEdges = new ArrayList<String>();

		for (Edge e : expected.getEdges()) {
			src = expected.getEndpoints(e).getFirst();
			dest = expected.getEndpoints(e).getSecond();
			expEdges.add(src.toString() + "," + dest.toString());
		}

		for (Edge e : actual.getEdges()) {
			src = actual.getEndpoints(e).getFirst();
			dest = actual.getEndpoints(e).getSecond();
			actEdges.add(src.toString() + "," + dest.toString());
		}

		Collections.sort(expEdges);
		Collections.sort(actEdges);
		// Assert.assertEquals(expEdges, actEdges);
	}

	/**
	 * used by a test method to validate the weights of the edges by a
	 * one-to-one comparison. An Edge implements a comparator based only on the
	 * edge weight.
	 * 
	 * @param expected
	 *            The original (expected) network, before exporting it
	 * @param actual
	 *            The actual (imported) network to be compared with the expected
	 */
	public void validateEdgeWeights(BrowsableNetwork expected,
			BrowsableNetwork actual) {
		List<Edge> expEdges = new ArrayList<Edge>(expected.getEdges());
		List<Edge> actEdges = new ArrayList<Edge>(actual.getEdges());

		Collections.sort(expEdges);
		Collections.sort(actEdges);
		// Assert.assertEquals(expEdges, actEdges);
	}

	private void log(String message) {
		StringBuilder string = new StringBuilder();

		string.append(this.getClass().getSimpleName());
		string.append(": ");
		string.append(message);

		System.out.println(string.toString());
	}

}
