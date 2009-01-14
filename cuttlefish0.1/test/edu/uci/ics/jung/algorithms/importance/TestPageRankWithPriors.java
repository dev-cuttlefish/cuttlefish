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
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.importance.PageRankWithPriors;
import edu.uci.ics.jung.algorithms.transformation.DirectionTransformer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.NumericalPrecision;

/**
 * @author Scott White
 */
public class TestPageRankWithPriors extends TestCase {

    public static Test suite() {
        return new TestSuite(TestPageRankWithPriors.class);
    }

    protected void setUp() {

    }

    public void testRanker() {

        DirectedSparseGraph graph = new DirectedSparseGraph();
 		GraphUtils.addVertices( graph, 4);
		Indexer id = Indexer.getIndexer( graph );

		GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(2));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(3));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(0));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(1));

        Set priors = new HashSet();
        priors.add(id.getVertex(2));

        PageRankWithPriors ranker = new PageRankWithPriors(graph,0.3,priors,null);
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.setMaximumIterations(500);

        for (Iterator vIt = graph.getVertices().iterator(); vIt.hasNext();) {
            Vertex v = (Vertex) vIt.next();
            double totalSum = 0;
            for (Iterator eIt = v.getOutEdges().iterator(); eIt.hasNext();) {
                Edge e = (Edge) eIt.next();
                Number weightVal = (Number) e.getUserDatum(ranker.getEdgeWeightKeyName());
                totalSum += weightVal.doubleValue();
            }
            Assert.assertTrue(NumericalPrecision.equal(1.0,totalSum,.0001));

        }

        ranker.evaluate();

        Assert.assertTrue(NumericalPrecision.equal(ranker.getRankScore(id.getVertex(0)),0.1157,.001));
        Assert.assertTrue(NumericalPrecision.equal(ranker.getRankScore(id.getVertex(1)),0.2463,.001));
        Assert.assertTrue(NumericalPrecision.equal(ranker.getRankScore(id.getVertex(2)),0.4724,.001));
        Assert.assertTrue(NumericalPrecision.equal(ranker.getRankScore(id.getVertex(3)),0.1653,.001));

    }

    public void test2() {

        Graph graph = new UndirectedSparseGraph();
        GraphUtils.addVertices(graph, 10);
        Indexer id = Indexer.getIndexer(graph);

        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1), (Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(0));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(4), (Vertex)id.getVertex(5));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(5), (Vertex)id.getVertex(6));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(6), (Vertex)id.getVertex(7));

        graph = DirectionTransformer.toDirected(graph);

        id = Indexer.getIndexer(graph);
        Set priors = new HashSet();
        priors.add(id.getVertex(2));
        priors.add(id.getVertex(3));

        PageRankWithPriors ranker = new PageRankWithPriors((DirectedGraph) graph, 0.3, priors, null);
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.setMaximumIterations(500);

        for (Iterator vIt = graph.getVertices().iterator(); vIt.hasNext();) {
            Vertex v = (Vertex) vIt.next();
            double totalSum = 0;
            for (Iterator eIt = v.getOutEdges().iterator(); eIt.hasNext();) {
                Edge e = (Edge) eIt.next();
                Number weightVal = (Number) e.getUserDatum(ranker.getEdgeWeightKeyName());
                totalSum += weightVal.doubleValue();
            }
            //Assert.assertTrue(NumericalPrecision.equal(1.0,totalSum,.0001));

        }

        ranker.evaluate();
        //ranker.printRankings(true,true);
    }
}
