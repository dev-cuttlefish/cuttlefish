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

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.connectivity.BFSDistanceLabeler;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 */
public class TestBFSDistanceLabeler extends TestCase {
	public static Test suite() {
		return new TestSuite(TestBFSDistanceLabeler.class);
	}

	protected void setUp() {

	}

	public void test() {
        UndirectedSparseGraph graph = new UndirectedSparseGraph();
        GraphUtils.addVertices( graph, 6 );
		Indexer id = Indexer.getIndexer( graph );

        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(5));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(5));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(5),(Vertex)id.getVertex(2));

        Vertex root = (Vertex) id.getVertex(0);

		BFSDistanceLabeler labeler = new BFSDistanceLabeler("distance");
		labeler.labelDistances(graph,root);

		Assert.assertEquals(labeler.getPredecessors(root).size(),0);
        Assert.assertEquals(labeler.getPredecessors((Vertex) id.getVertex(1)).size(),1);
        Assert.assertEquals(labeler.getPredecessors((Vertex) id.getVertex(2)).size(),2);
        Assert.assertEquals(labeler.getPredecessors((Vertex) id.getVertex(3)).size(),1);
        Assert.assertEquals(labeler.getPredecessors((Vertex) id.getVertex(4)).size(),1);
        Assert.assertEquals(labeler.getPredecessors((Vertex) id.getVertex(5)).size(),1);

        Assert.assertEquals(labeler.getDistance(graph,(Vertex) id.getVertex(0)),0);
        Assert.assertEquals(labeler.getDistance(graph,(Vertex) id.getVertex(1)),1);
        Assert.assertEquals(labeler.getDistance(graph,(Vertex) id.getVertex(2)),2);
        Assert.assertEquals(labeler.getDistance(graph,(Vertex) id.getVertex(3)),1);
        Assert.assertEquals(labeler.getDistance(graph,(Vertex) id.getVertex(4)),1);
        Assert.assertEquals(labeler.getDistance(graph,(Vertex) id.getVertex(5)),1);

	}
}