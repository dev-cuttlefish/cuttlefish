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
import java.io.IOException;

import org.gephi.io.exporter.spi.Exporter;

import ch.ethz.sg.cuttlefish.exporter.NetworkExportController;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

/**
 * @author Ilias Rinis
 * 
 *         The RandomNetworkGenerator class generates a random network, by
 *         selecting random number of vertices, random number of edges, and
 *         random connections between vertices. The graph is a BrowsableNetwork,
 *         for compatibility with unit tests.
 */

public class RandomNetworkGenerator {

	private final int MINNODES = 1500, MAXNODES = 2000;
	private final int MINEDGES = 3000, MAXEDGES = 4000;

	private final double MINWEIGHT = 1.0;
	private final double MAXWEIGHT = 20.0;

	private double allEdgesWeight = -1;
	private boolean allowLoopEdges = false;
	private boolean directedEdges = false;

	/**
	 * generates a random network with default parameter values
	 * 
	 * @return a randomized BrowsableNetwork instance
	 */
	public BrowsableNetwork generateNetwork() {
		return generateNetwork(MINNODES, MAXNODES, MINEDGES, MAXEDGES);
	}

	/**
	 * generates a random network with the specified parameters
	 * 
	 * @param numNodes
	 *            The number of nodes in the random network
	 * @param numEdges
	 *            The number of edges in the random network
	 * @return
	 */
	public BrowsableNetwork generateNetwork(int numNodes, int numEdges) {
		return generateNetwork(numNodes, numNodes, numEdges, numEdges);
	}

	/**
	 * Generates a random network, governed by the node and edge count
	 * limitations specified in the RandomNetworkGenerator instance.
	 * 
	 * @return a randomized network as an instance of BrowsableNetwork
	 */
	public BrowsableNetwork generateNetwork(int minNodes, int maxNodes,
			int minEdges, int maxEdges) {

		BrowsableNetwork network = new BrowsableNetwork();
		int numNodes = randomInRange(minNodes, maxNodes);
		int numEdges = randomInRange(minEdges, maxEdges);
		Vertex[] nodes = new Vertex[numNodes];

		// Create and add vertices to network;
		for (int id = 0; id < numNodes; ++id) {
			Vertex v = new Vertex(id, Integer.toString(id));
			double x = randomInRange(0, 1000);
			double y = randomInRange(0, 1000);
			v.setPosition(x, y);

			if (!network.containsVertex(v)) {
				network.addVertex(v);
				nodes[id] = v;
			}
		}

		// Create and add edges to network
		for (int id = 0; id < numEdges; ++id) {
			int s;
			int t;
			s = randomInRange(0, numNodes - 1);
			t = randomInRange(0, numNodes - 1);

			while (!allowLoopEdges && s == t) {
				s = randomInRange(0, numNodes - 1);
				t = randomInRange(0, numNodes - 1);
			}

			while (network.findEdge(nodes[s], nodes[t]) != null) {
				s = randomInRange(0, numNodes - 1);
				t = randomInRange(0, numNodes - 1);
			}

			float w;
			w = (float) ((allEdgesWeight < 0) ? randomInRange(MINWEIGHT,
					MAXWEIGHT) : allEdgesWeight);
			Edge e = new Edge(nodes[s], nodes[t], w, directedEdges);
			e.setId(id);
		}

		return network;
	}

	public String saveAsCxf(BrowsableNetwork network) throws Exception {
		File saveTo = new File("random_network.cxf");
		String fname = saveTo.getCanonicalPath();

		if (saveTo.exists())
			saveTo.delete();
		try {
			saveTo.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Exporter exporter = NetworkExportController.getExporter("cxf");
		NetworkExportController.export(network, saveTo, exporter);

		return fname;
	}

	/**
	 * returns a random integer in the range [min, max] (inclusive).
	 * 
	 * @param min
	 * @param max
	 * @return a random integer in [min, max] or [min] if min == max
	 */
	private int randomInRange(int min, int max) {
		if (min == max)
			return min;
		else
			return min + (int) (Math.random() * ((max - min) + 1));
	}

	/**
	 * returns a random double in the range [min, max] (inclusive).
	 * 
	 * @param min
	 * @param max
	 * @return a random double in [min, max] or [min] if min == max
	 */
	private double randomInRange(double min, double max) {
		if (min == max)
			return min;
		else
			return min + (Math.random() * ((max - min) + 1));
	}

	/**
	 * All edges generated by the generator will have a weight equal to the one
	 * specified with this method.
	 * 
	 * @param weight
	 *            The weight that all edges of the resulting random network will
	 *            have
	 */
	public void setAllEdgesWeight(double weight) {
		this.allEdgesWeight = weight;
	}

	/**
	 * Specifies if loop edges will be allowed in the random network.
	 * 
	 * @param allow
	 */
	public void allowLoopEdges(boolean allow) {
		this.allowLoopEdges = allow;
	}

	/**
	 * Specifies if directed or undirected edges will be used in the random
	 * network.
	 * 
	 * @param directed
	 */
	public void directedEdges(boolean directed) {
		this.directedEdges = directed;
	}

}
