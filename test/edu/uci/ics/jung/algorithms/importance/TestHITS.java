/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.algorithms.importance;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.importance.HITS;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 */
public class TestHITS extends TestCase {

    public static Test suite() {
        return new TestSuite(TestHITS.class);
    }

    protected void setUp() {

    }

    public void testRankerAuthorities() {

        DirectedSparseGraph graph = new DirectedSparseGraph();
        GraphUtils.addVertices(graph, 5);
        Indexer id = Indexer.getIndexer(graph);

        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1), (Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(0));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(1));


        HITS ranker = new HITS(graph);
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.setMaximumIterations(500);
        ranker.evaluate();
        //System.out.println("# of iterations = " + ranker.getIterations());

        Assert.assertEquals(ranker.getRankScore(id.getVertex(0)), 0, .0001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(1)), 0.618, .001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(2)), 0.0, .0001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(3)), 0.3819, .001);

    }

    public void testRankerHubs() {

        DirectedSparseGraph graph = new DirectedSparseGraph();
        GraphUtils.addVertices(graph, 5);
        Indexer id = Indexer.getIndexer(graph);

        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1), (Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(0));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(1));


        HITS ranker = new HITS(graph,false);
        ranker.setMaximumIterations(500);
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.evaluate();

        Assert.assertEquals(ranker.getRankScore(id.getVertex(0)), 0.38196, .001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(1)), 0.0, .0001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(2)), 0.618, .0001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(3)), 0.0, .0001);

    }
}
