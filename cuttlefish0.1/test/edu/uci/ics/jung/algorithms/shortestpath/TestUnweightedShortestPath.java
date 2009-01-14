/*
 * Created on Aug 22, 2003
 *
 */
package test.edu.uci.ics.jung.algorithms.shortestpath;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 */
public class TestUnweightedShortestPath extends TestCase
{
	public static Test suite()
	{
		return new TestSuite(TestUnweightedShortestPath.class);
	}
	
	public void testUndirected() {
		UndirectedGraph ug = new UndirectedSparseGraph();
		GraphUtils.addVertices(ug,5);
		Indexer id = Indexer.getIndexer(ug);
		GraphUtils.addEdge(ug,(Vertex) id.getVertex(0),(Vertex) id.getVertex(1));
		GraphUtils.addEdge(ug,(Vertex) id.getVertex(1),(Vertex) id.getVertex(2));
		GraphUtils.addEdge(ug,(Vertex) id.getVertex(2),(Vertex) id.getVertex(3));
		GraphUtils.addEdge(ug,(Vertex) id.getVertex(0),(Vertex) id.getVertex(4));
		GraphUtils.addEdge(ug,(Vertex) id.getVertex(4),(Vertex) id.getVertex(3));
		
		UnweightedShortestPath usp = new UnweightedShortestPath(ug);
		Assert.assertEquals(usp.getDistance((Vertex) id.getVertex(0),(Vertex) id.getVertex(3)).intValue(),2);
		Assert.assertEquals(((Number) usp.getDistanceMap((Vertex) id.getVertex(0)).get(id.getVertex(3))).intValue(),2);
		Assert.assertNull(usp.getIncomingEdgeMap((Vertex) id.getVertex(0)).get(id.getVertex(0)));
		Assert.assertNotNull(usp.getIncomingEdgeMap((Vertex) id.getVertex(0)).get(id.getVertex(3)));
	}
	
	public void testDirected() {
			DirectedGraph dg = new DirectedSparseGraph();
			GraphUtils.addVertices(dg,5);
			Indexer id = Indexer.getIndexer(dg);
			GraphUtils.addEdge(dg,(Vertex) id.getVertex(0),(Vertex) id.getVertex(1));
			GraphUtils.addEdge(dg,(Vertex) id.getVertex(1),(Vertex) id.getVertex(2));
			GraphUtils.addEdge(dg,(Vertex) id.getVertex(2),(Vertex) id.getVertex(3));
			GraphUtils.addEdge(dg,(Vertex) id.getVertex(0),(Vertex) id.getVertex(4));
			GraphUtils.addEdge(dg,(Vertex) id.getVertex(4),(Vertex) id.getVertex(3));
			GraphUtils.addEdge(dg,(Vertex) id.getVertex(3),(Vertex) id.getVertex(0));
		
			UnweightedShortestPath usp = new UnweightedShortestPath(dg);
			Assert.assertEquals(usp.getDistance((Vertex) id.getVertex(0),(Vertex) id.getVertex(3)).intValue(),2);
			Assert.assertEquals(((Number) usp.getDistanceMap((Vertex) id.getVertex(0)).get(id.getVertex(3))).intValue(),2);
			Assert.assertNull(usp.getIncomingEdgeMap((Vertex) id.getVertex(0)).get(id.getVertex(0)));
			Assert.assertNotNull(usp.getIncomingEdgeMap((Vertex) id.getVertex(0)).get(id.getVertex(3)));

		}
}
