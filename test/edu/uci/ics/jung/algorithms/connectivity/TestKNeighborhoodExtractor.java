/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.algorithms.connectivity;

import java.util.HashSet;
import java.util.Set;

import junit.framework.*;
import edu.uci.ics.jung.algorithms.connectivity.KNeighborhoodExtractor;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 */
public class TestKNeighborhoodExtractor extends TestCase {
    public static Test suite() {
        return new TestSuite(TestKNeighborhoodExtractor.class);
    }

    protected void setUp() {

    }

    public void testExtract() {
        DirectedSparseGraph graph = new DirectedSparseGraph();
		GraphUtils.addVertices( graph, 5 );
		Indexer id = Indexer.getIndexer( graph );
//        graph.addVertices(5);
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(3));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(4));

        Set rootNodes = new HashSet();
        rootNodes.add(id.getVertex(0));

        Graph extractedGraph = KNeighborhoodExtractor.extractNeighborhood(graph,rootNodes,2);

        Assert.assertEquals(extractedGraph.numVertices(),3);

    }
}
