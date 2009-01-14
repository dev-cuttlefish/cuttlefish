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
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 */
public class TestBetweennessCentrality extends TestCase {
    public static Test suite() {
        return new TestSuite(TestBetweennessCentrality.class);
    }

    protected void setUp() {

    }

    private Edge getEdge(int v1Index, int v2Index,Indexer id) {
        Vertex v1 = (Vertex) id.getVertex(v1Index);
        Vertex v2 = (Vertex) id.getVertex(v2Index);
         return (Edge) v1.findEdge(v2);

    }

    public void testRanker() {
        UndirectedSparseGraph graph = new UndirectedSparseGraph();
        GraphUtils.addVertices( graph, 9 );
		Indexer id = Indexer.getIndexer( graph );

        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(6));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(4));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(4),(Vertex)id.getVertex(5));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(5),(Vertex)id.getVertex(8));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(7),(Vertex)id.getVertex(8));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(6),(Vertex)id.getVertex(7));

        BetweennessCentrality bc = new BetweennessCentrality(graph);
        bc.setRemoveRankScoresOnFinalize(false);
        bc.evaluate();

        Assert.assertEquals(bc.getRankScore(id.getVertex(0))/28.0,0.2142,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(1))/28.0,0.2797,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(2))/28.0,0.0892,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(3))/28.0,0.0892,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(4))/28.0,0.2797,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(5))/28.0,0.2142,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(6))/28.0,0.1666,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(7))/28.0,0.1428,.001);
        Assert.assertEquals(bc.getRankScore(id.getVertex(8))/28.0,0.1666,.001);

        Assert.assertEquals(bc.getRankScore(getEdge(0,1,id)),10.66666,.001);
        Assert.assertEquals(bc.getRankScore(getEdge(0,6,id)),9.33333,.001);
        Assert.assertEquals(bc.getRankScore(getEdge(1,2,id)),6.5,.001);
        Assert.assertEquals(bc.getRankScore(getEdge(1,3,id)),6.5,.001);
        Assert.assertEquals(bc.getRankScore(getEdge(2,4,id)),6.5,.001);
        Assert.assertEquals(bc.getRankScore(getEdge(3,4,id)),6.5,.001);
        Assert.assertEquals(bc.getRankScore(getEdge(4,5,id)),10.66666,.001);
        Assert.assertEquals(bc.getRankScore(getEdge(5,8,id)),9.33333,.001);
        Assert.assertEquals(bc.getRankScore(getEdge(6,7,id)),8.0,.001);
        Assert.assertEquals(bc.getRankScore(getEdge(7,8,id)),8.0,.001);

    }
    
	public void testRankerDirected() {
			DirectedSparseGraph graph = new DirectedSparseGraph();
			GraphUtils.addVertices( graph, 5 );
			Indexer id = Indexer.getIndexer( graph );

			GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
			GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(2));
			GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(1));
			GraphUtils.addEdge(graph, (Vertex)id.getVertex(4),(Vertex)id.getVertex(2));

			BetweennessCentrality bc = new BetweennessCentrality(graph);
			bc.setRemoveRankScoresOnFinalize(false);
			bc.evaluate();


		Assert.assertEquals(bc.getRankScore(id.getVertex(0)),0,.001);
		Assert.assertEquals(bc.getRankScore(id.getVertex(1)),2,.001);
		Assert.assertEquals(bc.getRankScore(id.getVertex(2)),0,.001);
		Assert.assertEquals(bc.getRankScore(id.getVertex(3)),0,.001);
		Assert.assertEquals(bc.getRankScore(id.getVertex(4)),0,.001);
		
		Assert.assertEquals(bc.getRankScore(getEdge(0,1,id)),2,.001);
		Assert.assertEquals(bc.getRankScore(getEdge(1,2,id)),3,.001);
		Assert.assertEquals(bc.getRankScore(getEdge(3,1,id)),2,.001);
		Assert.assertEquals(bc.getRankScore(getEdge(4,2,id)),1,.001);

		}
		
//		void print(double x, double y, double z) {
//			System.out.println(x);
//		}
		
}
