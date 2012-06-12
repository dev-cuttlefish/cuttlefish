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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import ch.ethz.sg.cuttlefish.layout.ARF2Layout;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.GraphMLExporter;
import ch.ethz.sg.cuttlefish.misc.GraphMLImporter;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.GraphMLNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;

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

	@Test
	public void testGraphMLExport() {
		log("Testing GraphML Export/Import");

		RandomNetworkGenerator rgg = new RandomNetworkGenerator();
		BrowsableNetwork network = rgg.generateNetwork();
		Layout<Vertex, Edge> layout = new ARF2Layout<Vertex, Edge>(network,
				true, Integer.MAX_VALUE);

		File testFile = new File("graphml_export_test.graphml");

		log("Test graph has " + network.getVertexCount() + " nodes, "
				+ network.getEdgeCount() + " edges");

		// Export the network to GraphML
		log("Exporting network to GraphML");
		GraphMLExporter exporter = new GraphMLExporter(network, layout);
		exporter.export(testFile);

		// Import the network
		log("Importing network back");
		GraphMLNetwork importedNetwork = new GraphMLNetwork();
		Layout<Vertex, Edge> importedLayout = new ARF2Layout<Vertex, Edge>(
				importedNetwork, true, Integer.MAX_VALUE);
		GraphMLImporter importer = new GraphMLImporter(testFile);
		importer.importGraph(importedNetwork);

		// Step 1: Validate Topology
		log("Validating topology");
		validateTopology(layout.getGraph(), importedLayout.getGraph());

		// Step 2: Validate Edge weights
		log("Validating edge weights");
		validateEdgeWeights(layout.getGraph(), importedLayout.getGraph());

		testFile.delete();
		log("All tests have been completed successfully!");
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
	private void validateTopology(Graph<Vertex, Edge> expected,
			Graph<Vertex, Edge> actual) {

		// Assert network size
		Assert.assertEquals(expected.getEdgeCount(), actual.getEdgeCount());
		Assert.assertEquals(expected.getVertexCount(), actual.getVertexCount());

		// Sort & Assert Vertex set
		List<Vertex> expVertices = new ArrayList<Vertex>(expected.getVertices());
		List<Vertex> actVertices = new ArrayList<Vertex>(actual.getVertices());

		Collections.sort(expVertices);
		Collections.sort(actVertices);
		Assert.assertEquals(expVertices, actVertices);

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
		Assert.assertEquals(expEdges, actEdges);
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
	private void validateEdgeWeights(Graph<Vertex, Edge> expected,
			Graph<Vertex, Edge> actual) {
		List<Edge> expEdges = new ArrayList<Edge>(expected.getEdges());
		List<Edge> actEdges = new ArrayList<Edge>(actual.getEdges());

		Collections.sort(expEdges);
		Collections.sort(actEdges);
		Assert.assertEquals(expEdges, actEdges);
	}

	private void log(String message) {
		System.out.println(this.getClass().getSimpleName() + ": " + message);
	}

}
