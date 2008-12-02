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

import junit.framework.*;
import edu.uci.ics.jung.algorithms.importance.PageRank;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.*;
import edu.uci.ics.jung.utils.MutableDouble;
import edu.uci.ics.jung.utils.NumericalPrecision;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author Scott White
 */
public class TestPageRank extends TestCase {
    private static final String EDGE_WEIGHT_KEY = "EDGE_WEIGHT";

    public static Test suite() {
        return new TestSuite(TestPageRank.class);
    }

    protected void setUp() {

    }

    private void addEdge(Graph G, Vertex v1, Vertex v2, double weight)
    {
        Edge e = GraphUtils.addEdge(G, v1, v2);
        e.addUserDatum(EDGE_WEIGHT_KEY, new MutableDouble(weight), UserData.SHARED);
    }

    public void testRanker() {

       //PajekNetFile pajekFile = new PajekNetFile("LABEL",new String[] {EDGE_WEIGHT_KEY});
        //DirectedSparseGraph graph = pajekFile.load("C:\\research\\networkanalysis\\nets\\t.txt");

        DirectedSparseGraph graph = new DirectedSparseGraph();
		GraphUtils.addVertices( graph, 4 );
		Indexer id = Indexer.getIndexer( graph );
        addEdge(graph,(Vertex)id.getVertex(0),(Vertex)id.getVertex(1),1.0);
        addEdge(graph,(Vertex)id.getVertex(1),(Vertex)id.getVertex(2),1.0);
        addEdge(graph,(Vertex)id.getVertex(2),(Vertex)id.getVertex(3),0.5);
        addEdge(graph,(Vertex)id.getVertex(3),(Vertex)id.getVertex(1),1.0);
        addEdge(graph,(Vertex)id.getVertex(2),(Vertex)id.getVertex(1),0.5);

        /*
        graph.addEdge(graph.getVertex(0),graph.getVertex(1));
        graph.addEdge(graph.getVertex(1),graph.getVertex(2));
        graph.addEdge(graph.getVertex(2),graph.getVertex(3));
        graph.addEdge(graph.getVertex(3),graph.getVertex(1));
        graph.addEdge(graph.getVertex(2),graph.getVertex(1));
        */

        PageRank ranker = new PageRank(graph,0,EDGE_WEIGHT_KEY);
        ranker.setMaximumIterations(500);
        ranker.evaluate();


        //System.out.println("# of iterations = " + ranker.getIterations());
           /*

        System.out.println("PageRank Rankings: ");
        ranker.printRankings(true,true);
        */

        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(0)).rankScore,0.4,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(1)).rankScore,0.4,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(2)).rankScore,0.2,.001));
        Assert.assertTrue(NumericalPrecision.equal(((Ranking)ranker.getRankings().get(3)).rankScore,0,.001));

    }
}
