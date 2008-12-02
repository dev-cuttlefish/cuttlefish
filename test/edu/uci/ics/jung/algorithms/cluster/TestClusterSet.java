/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.algorithms.cluster;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.Assert;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.algorithms.cluster.EdgeClusterSet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Scott White
 */
public class TestClusterSet extends TestCase {
	public static Test suite() {
		return new TestSuite(TestClusterSet.class);
	}

	protected void setUp() {

	}

    public void testEdgeClusterSet() {

        DirectedSparseGraph graph = new DirectedSparseGraph();
        GraphUtils.addVertices( graph, 4);
		Indexer id = Indexer.getIndexer( graph );
        Edge e1 = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
        Edge e2 = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(2));
        Edge e3 = GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(2));
        Edge e4 = GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(3));

        EdgeClusterSet clusterSet = new EdgeClusterSet(graph);
        Set cluster1 = new HashSet();
        cluster1.add(e1);
        clusterSet.addCluster(cluster1);

        Set cluster2 = new HashSet();
        cluster2.add(e2);
        cluster2.add(e3);
        cluster2.add(e4);
        clusterSet.addCluster(cluster2);

        Assert.assertEquals(2,clusterSet.size());

        Assert.assertEquals(1,clusterSet.getCluster(0).size());
        Assert.assertEquals(3,clusterSet.getCluster(1).size());

        Assert.assertEquals(1,clusterSet.getClusters(e1).size());


    }
}
