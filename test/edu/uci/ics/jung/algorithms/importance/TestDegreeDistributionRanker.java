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
import edu.uci.ics.jung.algorithms.importance.DegreeDistributionRanker;
import edu.uci.ics.jung.algorithms.importance.Ranking;
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
public class TestDegreeDistributionRanker extends TestCase {
    Graph graph;
    public static Test suite() {
        return new TestSuite(TestDegreeDistributionRanker.class);
    }

    protected void setUp() {
        graph = new DirectedSparseGraph();
        GraphUtils.addVertices( graph, 5 );
		Indexer id = Indexer.getIndexer( graph );
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(4),(Vertex)id.getVertex(3));

    }

    public void testEmptyUndirectedGraph() 
    {
        graph = new UndirectedSparseGraph();
        GraphUtils.addVertices(graph, 10000);
        DegreeDistributionRanker ranker = new DegreeDistributionRanker(graph, true);
        ranker.evaluate();
    }

    public void testEmptyDirectedGraph()
    {
        graph = new DirectedSparseGraph();
        GraphUtils.addVertices(graph, 10000);
        DegreeDistributionRanker ranker = new DegreeDistributionRanker(graph, true);
        ranker.evaluate();
    }
    
    public void testRankerDefault() {
        DegreeDistributionRanker ranker = new DegreeDistributionRanker(graph);
        ranker.evaluate();

        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(0)).rankScore,0.333333,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(1)).rankScore,0.333333,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(2)).rankScore,0.166676,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(3)).rankScore,0.166676,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(4)).rankScore,0.000000,.001));

    }

    public void testRankerIndegree() {
        DegreeDistributionRanker ranker = new DegreeDistributionRanker(graph,true);
        ranker.evaluate();

        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(0)).rankScore,0.333333,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(1)).rankScore,0.333333,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(2)).rankScore,0.166676,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(3)).rankScore,0.166676,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(4)).rankScore,0.000000,.001));

    }

    public void testRankerOutdegree() {
        DegreeDistributionRanker ranker = new DegreeDistributionRanker(graph,false);
        ranker.evaluate();

        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(0)).rankScore,0.5,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(1)).rankScore,0.333333,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(2)).rankScore,0.166676,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(3)).rankScore,0,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(4)).rankScore,0.000000,.001));

    }
}
