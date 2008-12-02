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

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.importance.HITSWithPriors;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 */
public class TestHITSWithPriors extends TestCase {

    public static Test suite() {
        return new TestSuite(TestHITSWithPriors.class);
    }

    protected void setUp() {

    }

    public void testAuthoritiesRankings() {

        DirectedSparseGraph graph = new DirectedSparseGraph();
        GraphUtils.addVertices(graph, 4);
        Indexer id = Indexer.getIndexer(graph);

        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1), (Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(0));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(1));

        Set priors = new HashSet();
        priors.add(id.getVertex(2));


        HITSWithPriors ranker = new HITSWithPriors(graph, true, 0.3, priors, null);
        ranker.setMaximumIterations(500);
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.evaluate();

        //System.out.println("# of iterations = " + ranker.getIterations());

        //System.out.println("HitsWithPriors AuthorityRankings: ");
        //ranker.printRankings(true, true);

        Assert.assertEquals(ranker.getRankScore(id.getVertex(0)), 0, .0001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(1)), 0.246074, .0001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(2)), 0.588245, .0001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(3)), 0.165690, .0001);


    }

    public void testHubsRankings() {

        DirectedSparseGraph graph = new DirectedSparseGraph();
//        graph.addVertices(4);
        GraphUtils.addVertices(graph, 4);
        Indexer id = Indexer.getIndexer(graph);

        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1), (Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(0));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(1));

        Set priors = new HashSet();
        priors.add(id.getVertex(2));

        HITSWithPriors ranker = new HITSWithPriors(graph, false, 0.3, priors, null);
        ranker.setMaximumIterations(500);
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.evaluate();

        //System.out.println("# of iterations = " + ranker.getIterations());

        //System.out.println("HitsWithPriors Hubs Rankings: ");
        //ranker.printRankings(true, true);

        Assert.assertEquals(ranker.getRankScore(id.getVertex(0)), 0.114834, .0001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(1)), 0.411764, .0001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(2)), 0.473400, .0001);
        Assert.assertEquals(ranker.getRankScore(id.getVertex(3)), 0.0, .0001);


    }
}
