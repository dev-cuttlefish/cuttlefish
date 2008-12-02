/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Jun 25, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.edu.uci.ics.jung.graph.impl;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.PredicateUtils;
/**
 * @author danyelf
 */
public abstract class BasicGraphTest extends TestCase {

	/**
	 * @param string
	 */
	public BasicGraphTest(String string) {
		super(string);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("BasicGraphTest");
		suite.addTestSuite(BasicGraphSparseTest.class);
		return suite;
	}
	
	public static class BasicGraphSparseTest extends BasicGraphTest {
		public BasicGraphSparseTest(String string) {
			super(string);
		}

		public Vertex getVertex() {
			return new SparseVertex();
		}
	}

    public void setUp() {
	}
	public void tearDown() {
	}
	public abstract Vertex getVertex();

	protected final void testGraph(Graph sag, int edges, int vertices) {
		assertEquals(sag.getEdges().size(), edges);
		assertEquals(sag.getVertices().size(), vertices);
		assertEquals(sag.numVertices(), vertices);
		assertEquals(sag.numEdges(), edges);
		assertTrue(sag.toString().startsWith("G"));
		assertTrue(sag.toString().endsWith("]"));
	}

	public final void testPredecessorConsistency() {
		Graph sug = new UndirectedSparseGraph();
		assertTrue(PredicateUtils.enforcesEdgeConstraint(sug, Graph.UNDIRECTED_EDGE));
		testGraph(sug, 0, 0);
		Vertex v0 = (Vertex) sug.addVertex(getVertex());
		Vertex v1 = (Vertex) sug.addVertex(getVertex());
		testGraph(sug, 0, 2);
		sug.addEdge( new UndirectedSparseEdge( v0, v1 ) );

		Vertex v2 = (Vertex) sug.addVertex(getVertex());
		testGraph(sug, 1, 3);
		assertTrue( v0.isNeighborOf( v1 ));
		assertTrue( v1.isNeighborOf( v0 ));
		assertTrue( v0.isPredecessorOf( v1 ));
		assertTrue( v1.isSuccessorOf( v0 ));	

		assertFalse( v0.isPredecessorOf( v2 ));
		assertFalse( v2.isSuccessorOf( v0 ));
		assertFalse( v0.isNeighborOf(v2));
	}
	
	public final void testPredecessorConsistencyDirected() {
		Graph sug = new DirectedSparseGraph();
		assertTrue(PredicateUtils.enforcesEdgeConstraint(sug, Graph.DIRECTED_EDGE));
		testGraph(sug, 0, 0);
		Vertex v0 = (Vertex) sug.addVertex(getVertex());
		Vertex v1 = (Vertex) sug.addVertex(getVertex());
		testGraph(sug, 0, 2);
		sug.addEdge( new DirectedSparseEdge( v0, v1 ) );

		Vertex v2 = (Vertex) sug.addVertex(getVertex());
		testGraph(sug, 1, 3);
		assertTrue( v0.isNeighborOf( v1 ));
		assertTrue( v1.isNeighborOf( v0 ));
		assertTrue( v0.isPredecessorOf( v1 ));
		assertTrue( v1.isSuccessorOf( v0 ));	

		assertFalse( v0.isPredecessorOf( v2 ));
		assertFalse( v2.isSuccessorOf( v0 ));
		assertFalse( v0.isNeighborOf(v2));
	}

	public final void testSimpleUndirectedGraph() {
		UndirectedSparseGraph sug = new UndirectedSparseGraph();
		assertTrue(PredicateUtils.enforcesEdgeConstraint(sug, Graph.UNDIRECTED_EDGE));
		testGraph(sug, 0, 0);
		Vertex sd1 = (Vertex) sug.addVertex(getVertex());
		Vertex sd2 = (Vertex) sug.addVertex(getVertex());
		testGraph(sug, 0, 2);
		Edge e = new UndirectedSparseEdge(sd1, sd2);
		sug.addEdge(e);
		assertEquals(e.isIncident(sd1), true);
		assertEquals(e.isIncident(sd2), true);
		assertEquals(e.numVertices(), 2);
		assertTrue(PredicateUtils.enforcesEdgeConstraint(sug, Graph.UNDIRECTED_EDGE));
		testGraph(sug, 1, 2);
		assertFalse("Distinct IDs", sd1.getEqualVertex(sug) == sd2);
		assertFalse("Distinct IDs", sd2.getEqualVertex(sug) == sd1);
		assertSame(sd1, sd1.getEqualVertex(sug));
		Vertex sd3 = (Vertex) sug.addVertex((getVertex()));
		GraphUtils.addEdge(sug, sd2, sd3);
		testGraph(sug, 2, 3);
		GraphUtils.addEdge(sug, sd1, sd3);
		testGraph(sug, 3, 3);
		assertSame(e.getOpposite(sd1), sd2);
		try {
			e.getOpposite(sd3);
			fail("Doesn't fail on bad getOpposite test ");
		} catch (IllegalArgumentException iae) {
			return;
		}
	}
	/**
	 * Incrementally creates a graph( 3, 3 ) and checks that the edges add up
	 * correctly.
	 */
	public final void testSimpleDirectedGraph() {
		DirectedSparseGraph sdg = new DirectedSparseGraph();
		assertTrue(PredicateUtils.enforcesEdgeConstraint(sdg, Graph.DIRECTED_EDGE));
		testGraph(sdg, 0, 0);
		Vertex sd1 = (Vertex) sdg.addVertex(getVertex());
		Vertex sd2 = (Vertex) sdg.addVertex(getVertex());
		testGraph(sdg, 0, 2);
		Edge e = new DirectedSparseEdge(sd1, sd2);
		sdg.addEdge(e);
		assertEquals(e.isIncident(sd1), true);
		assertEquals(e.isIncident(sd2), true);
		assertEquals(e.numVertices(), 2);
		assertTrue(PredicateUtils.enforcesEdgeConstraint(sdg, Graph.DIRECTED_EDGE));
		testGraph(sdg, 1, 2);
		//		assertSame( sd1.findEdge( sd2) , e );
		// no longer valid tests: IDs not publically available
		//		int idLabel = sdg.getVertexID(sd1);
		//		assertSame(sd1, sdg.getVertexByID(idLabel));
		//		assertFalse(
		//			"Distinct IDs",
		//			sdg.getVertexID(sd1) == sdg.getVertexID(sd2));
		assertFalse("Distinct IDs", sd1.getEqualVertex(sdg) == sd2);
		assertFalse("Distinct IDs", sd2.getEqualVertex(sdg) == sd1);
		assertSame(sd1, sd1.getEqualVertex(sdg));
		Vertex sd3 = (Vertex) sdg.addVertex(getVertex());
		GraphUtils.addEdge(sdg, sd2, sd3);
		testGraph(sdg, 2, 3);
		GraphUtils.addEdge(sdg, sd1, sd3);
		testGraph(sdg, 3, 3);
		assertSame(e.getOpposite(sd1), sd2);
		try {
			e.getOpposite(sd3);
			fail("Doesn't fail on bad getOpposite test ");
		} catch (IllegalArgumentException iae) {
			return;
		}
	}
	
	public final void testSimpleDirectedGraphEdges() {
		DirectedSparseGraph sdg = new DirectedSparseGraph();
		Vertex sd1 = (Vertex) sdg.addVertex(getVertex());
		Vertex sd2 = (Vertex) sdg.addVertex(getVertex());
		assertEquals(sd1.getIncidentEdges().size(), 0);
		assertEquals(sd1.getNeighbors().size(), 0);
		assertEquals(sd1.inDegree(), 0);
		assertEquals(sd1.outDegree(), 0);
		DirectedEdge e = (DirectedEdge) GraphUtils.addEdge(sdg, sd1, sd2);
		assertEquals(sd1.inDegree(), 0);
		assertEquals(sd1.outDegree(), 1);
		assertEquals(sd2.inDegree(), 1);
		assertEquals(sd2.outDegree(), 0);
		assertEquals(sd1.getNeighbors().size(), 1);
		assertEquals(sd1.getSuccessors().size(), 1);
		assertEquals(e.getGraph(), sdg);
		DirectedEdge e2 = (DirectedEdge) GraphUtils.addEdge(sdg, sd2, sd1);
		// it's directed, so it's legal
		assertEquals(sd1.inDegree(), 1);
		assertEquals(sd1.outDegree(), 1);
		assertEquals(sd2.inDegree(), 1);
		assertEquals(sd2.outDegree(), 1);
		assertEquals(sd1.getNeighbors().size(), 1);
		assertEquals(sd1.getSuccessors().size(), 1);
		assertTrue(e2.getIncidentVertices().contains(sd1));
		assertTrue(e2.getIncidentVertices().contains(sd2));
		assertSame(e2.getOpposite(sd1), sd2);
		assertSame(e2.getOpposite(sd2), sd1);
	}
	
	public final void testGoodEdgesOpposite() {
		DirectedSparseGraph sdg = new DirectedSparseGraph();
		Vertex sd1 = (Vertex) sdg.addVertex(getVertex());
		Vertex sd2 = (Vertex) sdg.addVertex(getVertex());
		GraphUtils.addEdge(sdg, sd1, sd2);
		GraphUtils.addEdge(sdg, sd2, sd1);
	}
	public final void testBadEdgesParallel() {
		DirectedSparseGraph sdg = new DirectedSparseGraph();
		Vertex sd1 = (Vertex) sdg.addVertex(getVertex());
		Vertex sd2 = (Vertex) sdg.addVertex(getVertex());
		GraphUtils.addEdge(sdg, sd1, sd2);
		try {
			GraphUtils.addEdge(sdg, sd1, sd2);
			fail("Doesn't reject redundant edges");
		} catch (IllegalArgumentException iae) {
		}
	}
	public final void testEdgesOkSelfLoop() {
		// Self-loops shoudl be ok.
		DirectedSparseGraph sdg = new DirectedSparseGraph();
		Vertex sd1 = (Vertex) sdg.addVertex(getVertex());
		GraphUtils.addEdge(sdg, sd1, sd1);
	}
    
	public final void testGraphAppropriateness() {
		DirectedSparseGraph sdg = new DirectedSparseGraph();
		Vertex sd1 = (Vertex) sdg.addVertex(getVertex());
		Vertex sd2 = (Vertex) sdg.addVertex(getVertex());
		DirectedSparseGraph sdx = new DirectedSparseGraph();
		Vertex sdx1 = (Vertex) sdx.addVertex(getVertex());
		Vertex sdx2 = (Vertex) sdx.addVertex(getVertex());
		assertTrue(sdx.getVertices().contains(sdx1));
		assertTrue(sdx.getVertices().contains(sdx2));
		assertNotSame(sdx, sdg);
		assertNotSame(sdx1, sd1);
		assertSame(sdx1.getGraph(), sdx2.getGraph());
		assertNotSame(sdx1.getGraph(), sd1.getGraph());
		GraphUtils.addEdge(sdg, sd1, sd2);
		GraphUtils.addEdge(sdx, sdx1, sdx2);
		assertFalse("Checking distinct ids", sdx1 == sd1
				.getEqualVertex(sdx));
		assertFalse("Checking distinct ids", sd1 == sdx1
				.getEqualVertex(sdg));
		assertFalse("Checking distinct ids", sdx2 == sd2
				.getEqualVertex(sdx));
		assertFalse("Checking distinct ids", sd2 == sdx2
				.getEqualVertex(sdg));
		try {
			GraphUtils.addEdge(sdg, sd1, sdx2);
			fail("Shouldn't allow adding edges of different graphs.");
		} catch (IllegalArgumentException iae) {
		}
	}
    
	public final void testParallelEdgeAdd() {
		Graph g = new DirectedSparseGraph();
		Vertex v1 = (Vertex) g.addVertex(getVertex());
		Vertex v2 = (Vertex) g.addVertex(getVertex());
		Edge e1 = new DirectedSparseEdge(v1, v2);
		Edge e2 = new DirectedSparseEdge(v1, v2);
		g.addEdge(e1);
		try {
			g.addEdge(e2);
			fail("Should not allow parallel edges to be added");
		} catch (IllegalArgumentException iae) {
		}
	}
    
    // created v. 1.4 to show bug (union didn't handle null arguments)
    public void testFindEdgeSet()
    {
        Graph g = new SparseGraph();
        Vertex v1 = (Vertex) g.addVertex(getVertex());
        Vertex v2 = (Vertex) g.addVertex(getVertex());
        Set s = v1.findEdgeSet(v2);
        assertTrue(s.isEmpty());
        Edge e1 = g.addEdge(new DirectedSparseEdge(v1, v2));
        s = v1.findEdgeSet(v2);
        assertFalse(s.isEmpty());
        assertTrue(s.contains(e1));
        assertTrue(s.size() == 1);
        Edge e2 = g.addEdge(new DirectedSparseEdge(v1, v2));
        s = v1.findEdgeSet(v2);
        assertTrue(s.size() == 2);
        assertTrue(s.contains(e1));
        assertTrue(s.contains(e2));
        
        // created 1.4.4 to turn bug in SimpleSparseVertex implementation
        // (wasn't returning both directed and undirected edges)
//        Graph g2 = new SparseGraph();
        Vertex v3 = (Vertex) g.addVertex(new SimpleSparseVertex());
        Vertex v4 = (Vertex) g.addVertex(new SimpleSparseVertex());
        Edge e3 = g.addEdge(new DirectedSparseEdge(v3, v4));
        Edge e4 = g.addEdge(new UndirectedSparseEdge(v3, v4));
        s = v3.findEdgeSet(v4);
        assertTrue(s.size() == 2);
        assertTrue(s.contains(e3));
        assertTrue(s.contains(e4));
        s = v4.findEdgeSet(v3);
        assertTrue(s.size() == 1);
        assertTrue(s.contains(e4));
    }

    // written to turn bug in v. 1.3 AbstractSparseVertex implementation,
    // which looks at incident edges rather than outgoing edges
    public void testFindEdge()
    {
        Graph g = new SparseGraph();
        Vertex v1 = (Vertex) g.addVertex(getVertex());
        Vertex v2 = (Vertex) g.addVertex(getVertex());
        g.addEdge(new DirectedSparseEdge(v1, v2));
        assertNull(v2.findEdge(v1));
    }

    public void testDirectedGraph()
    {
        DirectedGraph dg = new DirectedSparseGraph();
        Vertex v1 = (Vertex) dg.addVertex(getVertex());
        Vertex v2 = (Vertex) dg.addVertex(getVertex());
        Edge e = dg.addEdge(new DirectedSparseEdge(v1, v2));
        dg.removeEdge(e);
        try
        {
            dg.addEdge(new UndirectedSparseEdge(v1, v2));
            fail("should not allow undirected edges");
        }
        catch (IllegalArgumentException iae) {}
    }

    public void testUndirectedGraph()
    {
        UndirectedGraph ug = new UndirectedSparseGraph();
        Vertex v1 = (Vertex) ug.addVertex(getVertex());
        Vertex v2 = (Vertex) ug.addVertex(getVertex());
        Edge e = ug.addEdge(new UndirectedSparseEdge(v1, v2));
        ug.removeEdge(e);
        try
        {
            ug.addEdge(new DirectedSparseEdge(v1, v2));
            fail("should not allow directed edges");
        }
        catch (IllegalArgumentException iae) {}
    }
    
    /**
     * make sure that vertices and edges are equal to themselves,
     * even if they're not in a graph.
     */
    public void testEquality()
    {
        Vertex v1 = new SparseVertex();
        assertTrue(v1.equals(v1));
        Vertex v2 = new SparseVertex();
        assertTrue(v2.equals(v2));
        Graph g = new SparseGraph();
        assertTrue(g.equals(g));
        g.addVertex(v1);
        assertTrue(v1.equals(v1));
        g.addVertex(v2);
        assertTrue(v2.equals(v2));
        Edge e = new DirectedSparseEdge(v1, v2);
        assertTrue(e.equals(e));
        g.addEdge(e);
        assertTrue(e.equals(e));
    }
    
    /*
	 * public void testEquivalency() { UndirectedGraph ug = new
	 * UndirectedSparseGraph(); GraphUtils.addUndirectedVertices(ug,5); Indexer
	 * id = Indexer.getIndexer(ug);
	 * GraphUtils.addEdge(ug,id.getVertex(0),id.getVertex(2));
	 * GraphUtils.addEdge(ug,id.getVertex(2),id.getVertex(1));
	 * GraphUtils.addEdge(ug,id.getVertex(3),id.getVertex(4)); DirectedGraph dg =
	 * GraphUtils.transform(ug); Vertex v0 = (Vertex)
	 * id.getVertex(0).getEquivalentVertex(dg); Vertex v1 = (Vertex)
	 * id.getVertex(1).getEquivalentVertex(dg); Vertex v2 = (Vertex)
	 * id.getVertex(2).getEquivalentVertex(dg); Vertex v3 = (Vertex)
	 * id.getVertex(3).getEquivalentVertex(dg); Vertex v4 = (Vertex)
	 * id.getVertex(4).getEquivalentVertex(dg); Assert.assertTrue(dg.numEdges() ==
	 * 6); Assert.assertTrue(v0.isPredecessorOf(v2));
	 * Assert.assertTrue(v2.isPredecessorOf(v0));
	 * Assert.assertTrue(v1.isPredecessorOf(v2));
	 * Assert.assertTrue(v2.isPredecessorOf(v1));
	 * Assert.assertTrue(v3.isPredecessorOf(v4));
	 * Assert.assertTrue(v4.isPredecessorOf(v3)); }
	 */
	//	public void testCliqueGraphEdges() {
	//		DirectedSparseGraph sdg = new DirectedSparseGraph();
	//		for (int i = 0; i < 6; i++) {
	//			new SparseVertex(sdg);
	//		}
	//		assertEquals(sdg.getVertices().size(), 6);
	//		Set vertices = sdg.getVertices();
	//		// let's order them
	//		List orderedVertices = new ArrayList();
	//		orderedVertices.addAll(vertices);
	//		for (Iterator iter = orderedVertices.iterator(); iter.hasNext();) {
	//			Vertex v1 = (Vertex) iter.next();
	//
	//		}
	//	}
	//
	//	public void testSimpleUndirectedGraph() {
	//		UndirectedSparseGraph sdg = new UndirectedSparseGraph();
	//		testGraph( sdg, 0, 0);
	//		SparseVertex sd1 = new SparseVertex( sdg );
	//		SparseVertex sd2 = new SparseVertex( sdg );
	//
	//		testGraph( sdg, 0, 2);
	//		sdg.addEdge( sd1, sd2 );
	//		testGraph( sdg, 1, 2);
	//		int idLabel = sdg.getVertexID( sd1 );
	//		assertSame( sd1, sdg.getVertexByID( idLabel ) );
	//	}
}
