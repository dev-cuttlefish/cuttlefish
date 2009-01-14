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
 * Created on Jun 30, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.edu.uci.ics.jung.graph.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author danyelf
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class RemovalGraphTest extends TestCase {

	Graph g, g2;
	Indexer id;

	public abstract Vertex getVertex();
	
	public static Test suite() {
		TestSuite suite = new TestSuite("RemovalGraphTest");
		suite.addTestSuite(SparseTest.class);
		return suite;
	}
	
	public static class SparseTest extends RemovalGraphTest {
		public Vertex getVertex() {
			return new SparseVertex();
		}
	}
	
	public void setUp1() {
		g = new DirectedSparseGraph();
		for(int i = 0 ; i < 5; i++){
			g.addVertex( getVertex() );
		}
		id = Indexer.getIndexer(g);
		for (int i = 0; i < 5; i++) {
			for (int j = i; j < 5; j++) {
				GraphUtils.addEdge(g, (Vertex)id.getVertex(i), (Vertex)id.getVertex(j));
			}
		}
	}

	public void setUpUndir() {
		g = new UndirectedSparseGraph();
		for(int i = 0 ; i < 5; i++){
			g.addVertex( getVertex() );
		}
		id = Indexer.getIndexer(g);
		for (int i = 0; i < 5; i++) {
			for (int j = i; j < 5; j++) {
				GraphUtils.addEdge(g, (Vertex)id.getVertex(i), (Vertex)id.getVertex(j));
			}
		}
	}

	public void tearDown() {
		g = null;
		id = null;
	}

	private void doTestRemoveEdge() {
		Vertex v1 = (Vertex) id.getVertex(1);
		Edge ed = (Edge) v1.getOutEdges().iterator().next();
		assertEquals(5, v1.degree());
		assertEquals(5, g.getVertices().size());
		assertEquals(15, g.getEdges().size());

		// pre-tests. If these fail, something Major is Wrong.

		g.removeEdge(ed);
		assertEquals(5, g.getVertices().size());
		assertEquals(14, g.getEdges().size());
	}

	public void testRemoveEdgeDirected() {
		setUp1();
		doTestRemoveEdge();
	}

	public void testRemoveEdgeUndirected() {
		setUpUndir();
		doTestRemoveEdge();
	}

	private void doRemoveVertex() {
		Vertex v1 = (Vertex) id.getVertex(1);
		Vertex v2 = (Vertex) id.getVertex(2);
		assertEquals(5, v1.degree());
		assertEquals(5, v2.degree());
		assertEquals(5, g.getVertices().size());
		assertEquals(15, g.getEdges().size());

		// pre-tests. If these fail, something Major is Wrong.

		g.removeVertex(v1);
		assertEquals(4, g.getVertices().size());
		assertEquals(4, v2.degree());
		assertEquals(10, g.getEdges().size());
	}

	public void testRemoveVertexDirected() {
		setUp1();
		doRemoveVertex();
	}

	public void testRemoveVertexUndirected() {
		setUpUndir();
		doRemoveVertex();
	}

	private void doRemoveAllEdges() {
		assertEquals(5, g.getVertices().size());
		g.removeAllEdges();
		assertEquals(5, g.getVertices().size());
		assertEquals(0, g.getEdges().size());
	}

	public void testRemoveAllEdgesDirected() {
		setUp1();
		doRemoveAllEdges();
	}

	public void testRemoveAllEdgesUndirected() {
		setUpUndir();
		doRemoveAllEdges();
	}

	private void doRemoveAllVertices() {
		assertEquals(5, g.getVertices().size());
		g.removeAllVertices();
		assertEquals(0, g.getVertices().size());
		assertEquals(0, g.getEdges().size());
	}

	public void testRemoveAllVerticesDirected() {
		setUp1();
		doRemoveAllVertices();
	}

	private void doRemoveWrongVertexFail() {
		Graph g2 = (Graph) g.copy();
		Vertex v1 = (Vertex) id.getVertex(1);
		Edge ed = (Edge) v1.getOutEdges().iterator().next();
		try {
			g2.removeEdge(ed);
		} catch (IllegalArgumentException fe) {
			return;
		}
		fail("Allowed me to remove an edge not in the graph");
	}

	public void testRemoveWrongVertexFail() {
		setUp1();
		doRemoveWrongVertexFail();
	}

	public void testRemoveWrongVertexFailUndir() {
		setUpUndir();
		doRemoveWrongVertexFail();
	}

	public void testRemoveAllVerticesUndirected() {
		setUpUndir();
		doRemoveAllVertices();
	}

	public void testRemoveAllFromAVertexUndir() {
		setUpUndir();
		doRemoveAllFromAVertex();
	}

	public void testRemoveAllFromAVertex() {
		setUp1();
		doRemoveAllFromAVertex();
	}


	private void doRemoveAllFromAVertex() {
		for (int i = 0; i < g.numVertices(); i++) {
			Vertex vertex = (Vertex) id.getVertex(i);
            GraphUtils.removeEdges(g, vertex.getInEdges());
			assertEquals(vertex.inDegree(), 0);
			GraphUtils.removeEdges(g, vertex.getOutEdges());
			assertEquals(vertex.outDegree(), 0);
		}

	}
    
    public void testVertexAdditionAndRemoval()
    {
        DirectedSparseGraph sdg = new DirectedSparseGraph();
        Vertex v = sdg.addVertex( getVertex() );
        assertNotNull(v.getGraph());
        
        sdg.removeVertex(v);
        assertNull(v.getGraph());
        
        sdg.addVertex(v);        
        assertNotNull(v.getGraph());
    }
    
    public void testEdgeAdditionAndRemoval() 
    {
        // create vertices, check
        DirectedSparseGraph sdg = new DirectedSparseGraph();
        Vertex sd1 = 
            (Vertex) sdg.addVertex( getVertex() );
        Vertex sd2 =
            (Vertex) sdg.addVertex( getVertex() );
        assertNotNull(sd1.getGraph());
        assertNotNull(sd2.getGraph());
        assertFalse(sd1.isNeighborOf(sd2));
        assertFalse(sd2.isNeighborOf(sd1));
        assertFalse(sd1.isPredecessorOf(sd2));
        assertFalse(sd2.isPredecessorOf(sd1));
        assertFalse(sd1.isSuccessorOf(sd2));
        assertFalse(sd2.isSuccessorOf(sd1));
        
        // create & add edge, check
        DirectedSparseEdge e = 
            (DirectedSparseEdge) sdg.addEdge(new DirectedSparseEdge(sd1, sd2));
        assertNotNull(e.getGraph());
        assertTrue(e.isIncident(sd1));
        assertTrue(e.isIncident(sd2));
        assertTrue(sd1.isIncident(e));
        assertTrue(sd2.isIncident(e));
        assertTrue(sd1.isNeighborOf(sd2));
        assertTrue(sd2.isNeighborOf(sd1));
        assertTrue(sd1.isPredecessorOf(sd2));
        assertFalse(sd2.isPredecessorOf(sd1));
        assertFalse(sd1.isSuccessorOf(sd2));
        assertTrue(sd2.isSuccessorOf(sd1));
        
        // remove edge, check
        sdg.removeEdge(e);
        assertNull(e.getGraph());
        assertTrue(e.isIncident(sd1));
        assertTrue(e.isIncident(sd2));
        assertFalse(sd1.isIncident(e));
        assertFalse(sd2.isIncident(e));
        assertFalse(sd1.isNeighborOf(sd2));
        assertFalse(sd2.isNeighborOf(sd1));
        assertFalse(sd1.isPredecessorOf(sd2));
        assertFalse(sd2.isPredecessorOf(sd1));
        assertFalse(sd1.isSuccessorOf(sd2));
        assertFalse(sd2.isSuccessorOf(sd1));
        
        // add edge back in, check
        sdg.addEdge(e);
        assertNotNull(e.getGraph());
        assertTrue(e.isIncident(sd1));
        assertTrue(e.isIncident(sd2));
        assertTrue(sd1.isIncident(e));
        assertTrue(sd2.isIncident(e));
        assertTrue(sd1.isNeighborOf(sd2));
        assertTrue(sd2.isNeighborOf(sd1));
        assertTrue(sd1.isPredecessorOf(sd2));
        assertFalse(sd2.isPredecessorOf(sd1));
        assertFalse(sd1.isSuccessorOf(sd2));
        assertTrue(sd2.isSuccessorOf(sd1));
    }
}