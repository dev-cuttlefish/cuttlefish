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
import edu.uci.ics.jung.algorithms.importance.RandomWalkBetweenness;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 */
public class TestRandomWalkBetweenness extends TestCase {
    public static Test suite() {
        return new TestSuite(TestRandomWalkBetweenness.class);
    }

    protected void setUp() {

    }

//    private Edge getEdge(int v1Index, int v2Index,Indexer id) {
//        Vertex v1 = (Vertex) id.getVertex(v1Index);
//        Vertex v2 = (Vertex) id.getVertex(v2Index);
//         return (Edge) v1.findEdge(v2);
//
//    }

    public void testRanker() {
        UndirectedSparseGraph graph = new UndirectedSparseGraph();
        GraphUtils.addVertices( graph, 11 );
		Indexer id = Indexer.getIndexer( graph );

        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(4),(Vertex)id.getVertex(5));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(4),(Vertex)id.getVertex(6));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(5),(Vertex)id.getVertex(6));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(6),(Vertex)id.getVertex(7));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(6),(Vertex)id.getVertex(8));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(6),(Vertex)id.getVertex(9));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(6),(Vertex)id.getVertex(10));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(7),(Vertex)id.getVertex(8));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(7),(Vertex)id.getVertex(9));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(7),(Vertex)id.getVertex(10));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(8),(Vertex)id.getVertex(9));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(8),(Vertex)id.getVertex(10));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(9),(Vertex)id.getVertex(10));

        RandomWalkBetweenness bc = new RandomWalkBetweenness(graph);
        bc.setRemoveRankScoresOnFinalize(false);
        bc.evaluate();

        /*
        System.out.println("C: " + bc.getRankScore(id.getVertex(5)));
        System.out.println("B: " + bc.getRankScore(id.getVertex(6)));
        System.out.println("A: " + bc.getRankScore(id.getVertex(4)));
        System.out.println("X: " + bc.getRankScore(id.getVertex(0)));
        System.out.println("Y: " + bc.getRankScore(id.getVertex(10)));
        */

        Assert.assertEquals(bc.getRankScore(id.getVertex(5)),0.333,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(6)),0.67,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(4)),0.67,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(0)),0.269,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(10)),0.269,.001);



    }
}
