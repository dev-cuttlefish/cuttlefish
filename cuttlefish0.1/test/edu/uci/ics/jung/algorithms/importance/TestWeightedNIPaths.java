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

import junit.framework.*;
import edu.uci.ics.jung.algorithms.importance.NodeRanking;
import edu.uci.ics.jung.algorithms.importance.WeightedNIPaths;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.NumericalPrecision;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author Scott White
 */
public class TestWeightedNIPaths extends TestCase {

    public static Test suite() {
        return new TestSuite(TestWeightedNIPaths.class);
    }

    protected void setUp() {

    }

    public void testRanker() {

        DirectedSparseGraph graph = new DirectedSparseGraph();
//        graph.addVertices(5);
		GraphUtils.addVertices( graph, 5 );
		Indexer id = Indexer.getIndexer( graph );
        id.getVertex(0).setUserDatum("LABEL","A",UserData.REMOVE);
        id.getVertex(1).setUserDatum("LABEL","B",UserData.REMOVE);
        id.getVertex(2).setUserDatum("LABEL","C",UserData.REMOVE);
        id.getVertex(3).setUserDatum("LABEL","D",UserData.REMOVE);
        id.getVertex(4).setUserDatum("LABEL","E",UserData.REMOVE);
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(0));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(0));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(0));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(4));

        Set priors = new HashSet();
        priors.add(id.getVertex(0));

        WeightedNIPaths ranker = new WeightedNIPaths(graph,2.0,3,priors);
        ranker.evaluate();

        //System.out.println("WeightedNIPathsII Rankings: ");
        //ranker.printRankings(true,true);

        Assert.assertTrue(NumericalPrecision.equal(((NodeRanking)ranker.getRankings().get(0)).rankScore,0.277787,.0001));
        Assert.assertTrue(NumericalPrecision.equal(((NodeRanking)ranker.getRankings().get(1)).rankScore,0.222222,.0001));
        Assert.assertTrue(NumericalPrecision.equal(((NodeRanking)ranker.getRankings().get(2)).rankScore,0.166676,.0001));
        Assert.assertTrue(NumericalPrecision.equal(((NodeRanking)ranker.getRankings().get(3)).rankScore,0.166676,.0001));
        Assert.assertTrue(NumericalPrecision.equal(((NodeRanking)ranker.getRankings().get(4)).rankScore,0.166676,.0001));

    }
}