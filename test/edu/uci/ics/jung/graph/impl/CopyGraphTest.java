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

import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author danyelf
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class CopyGraphTest extends TestCase {

	DirectedSparseGraph g;
	Indexer id;
	
	public static Test suite() {
		TestSuite suite = new TestSuite("CopyGraphTest");
		suite.addTestSuite(CopyGraphSparseTest.class);
		return suite;
	}
	
	public static class CopyGraphSparseTest extends CopyGraphTest {
		public Vertex getVertex() {
			return new SparseVertex();
		}
	}

	public abstract Vertex getVertex();
	
	public final void setUp() {
		g = new DirectedSparseGraph();
		for (int i = 0; i < 5; i++)
			g.addVertex(new SparseVertex());
		id = Indexer.getIndexer(g);
		for(int i = 0; i < 5; i++ ) {
			for (int j = i ; j < 5; j++ ) {
				GraphUtils.addEdge( g, (Vertex)id.getVertex(i), (Vertex)id.getVertex(j) );
			}
		}
	}

	public void tearDown() {
		g = null;
	}

	public void testVertexCopy() {
		Graph newSubGraph = (Graph) g.newInstance();
		assertEquals( 0, newSubGraph.getVertices().size() );
		assertEquals( 5, g.getVertices().size() );

		for (int i = 0; i < 3; i++ ) {
			Vertex v = (Vertex)id.getVertex( i );
			Vertex v2 = (Vertex) v.copy( newSubGraph );
			assertEquals( 0, v2.degree() );
			assertEquals( 0, v2.getNeighbors().size() );
			assertEquals( 0, v2.getIncidentEdges().size() );
            assertEquals(v2, v);
            assertSame(v2, v.getEqualVertex(v2.getGraph()));
            assertSame(v, v2.getEqualVertex(v.getGraph()));
//			assertEquals( v2.id(), v.id() );
//			assertSame( v2, newSubGraph.getVertexByID( v.id() ));
			assertSame( newSubGraph, v2.getGraph() );
		}
	}

	public void testPredicateCopy() {
		Graph newCopy = (Graph) g.copy();
		assertEquals(newCopy.getEdgeConstraints().size(), g.getEdgeConstraints().size() );
		assertEquals(newCopy.getVertexConstraints().size(), g.getVertexConstraints().size() );
	}

	
	public void testEdgeCopy() {
		Graph newSubGraph = (Graph) g.newInstance();
		assertEquals( 0, newSubGraph.getVertices().size() );
		assertEquals( 5, g.getVertices().size() );

		Vertex v_orig = (Vertex) id.getVertex( 1 );
		Vertex v_orig_2 = (Vertex) id.getVertex( 2 );

        Edge e = v_orig.findEdge( v_orig_2 );
        assertNotNull( e );
        assertEquals(0 , newSubGraph.getEdges().size());

        // try copying edge: should fail because incident vertices not
        // yet copied
        boolean copy_executed = true;
        try
        {
            e.copy(newSubGraph);
        }
        catch (IllegalArgumentException iae)
        {
            copy_executed = false;  // this is correct behavior
            
            Vertex v_copy_2 = (Vertex) v_orig_2.copy( newSubGraph );
            Vertex v_copy = (Vertex) v_orig.copy( newSubGraph );                

            assertTrue( v_orig.isNeighborOf( v_orig_2 ));
            assertFalse( v_copy.isNeighborOf( v_copy_2));

            Edge copy = (Edge) e.copy( newSubGraph );
            assertNotNull(copy);
            assertEquals(1 , newSubGraph.getEdges().size());
            assertTrue( v_copy.isNeighborOf( v_copy_2));            
        }
        if (copy_executed)
            fail("Edge copy should have failed: incident vertices had " + 
                "not yet been copied into target graph");

	}

	public void testGraphCopy() {
		g.setUserDatum("KEY", "VAL", UserData.REMOVE);
		assertEquals( "VAL", g.getUserDatum("KEY"));

		Graph newSubGraph = (Graph) g.copy();

        // test structure: are all vertices and edges in copy?
        assertTrue(GraphUtils.areEquivalent(g, newSubGraph));
        
        // test 
		assertEquals( "VAL", g.getUserDatum("KEY"));
		assertNull(newSubGraph.getUserDatum( "KEY" ));

		assertEquals( 5, newSubGraph.getVertices().size() );
		assertEquals( 5, g.getVertices().size() );
	}

	// created to satisfy comment in release notes
	public void testBadVertexRepeatedCopy() {
		Graph newGraph = (Graph) g.newInstance();
		Vertex v = (Vertex) g.getVertices().iterator().next();
		// copies v1 into newGraph
		Vertex v_copy = (Vertex)v.copy( newGraph );
		try {
			v.copy( newGraph );
            fail("Shouldn't be able to copy one vertex into a graph twice");
		} catch (IllegalArgumentException iae ) {}

        // if we remove it first, then we should be able to copy it in again
        newGraph.removeVertex(v_copy);
        try
        {
            v.copy(newGraph);
		}
        catch (IllegalArgumentException iae)
        {
            fail("Should have been able to copy vertex again after first copy removed");
        }
	}

	// created to satisfy comment in release notes
	public void testBadVertexDuplicationCopy() {
		Vertex v1 = (Vertex) g.getVertices().iterator().next();
		// copies v1 into copy
		try {
			v1.copy( g);
		} catch (IllegalArgumentException iae ) {
			return;
		}
		fail("Shouldn't be able to create a copy of a vertex in the same graph");
	}

    public void testBadEdgeRepeatedCopy() {
        Graph newGraph = (Graph) g.newInstance();
        Vertex v1 = (Vertex)id.getVertex(1);
        Vertex v2 = (Vertex)id.getVertex(2);
        Edge e = v1.findEdge(v2);
        
        v1.copy(newGraph);
        v2.copy(newGraph);
        Edge e_copy = (Edge)e.copy( newGraph );
        try {
            e.copy( newGraph );
        } catch (IllegalArgumentException iae ) {
            return;
        }
        fail("Shouldn't be able to copy one edge into a graph twice");
        
        // if we remove it first, then we should be able to copy it in again
        newGraph.removeEdge(e_copy);
        try
        {
            e.copy(newGraph);
        }
        catch (IllegalArgumentException iae)
        {
            fail("Should have been able to copy edge again after first copy removed");
        }
    }

    public void testBadEdgeDuplicationCopy() 
    {
        Edge e = (Edge) g.getEdges().iterator().next();
        try 
        {
            e.copy(g);
            fail("Should not be able to create a copy of an edge in the same graph");
        }
        catch (IllegalArgumentException iae) {}
    }

	public void testEqualsAndEquivalent() {
		Graph graph2 = (Graph) g.newInstance();
        Vertex v_orig = (Vertex) id.getVertex( 1 );
        assertNull(v_orig.getEqualVertex(graph2));
        
        Vertex v_copy = (Vertex) v_orig.copy( graph2 );
        assertEquals(v_copy, v_orig.getEqualVertex(graph2));
        assertEquals(v_orig, v_copy.getEqualVertex(g));

        Vertex v_orig_2 = (Vertex) id.getVertex( 2 );
        Vertex v_copy_2 = (Vertex) v_orig_2.copy( graph2 );

        assertTrue( v_orig.isNeighborOf( v_orig_2 ));
        assertFalse( v_copy.isNeighborOf( v_copy_2));

        Edge e = v_orig.findEdge( v_orig_2 );
        assertNotNull( e );
        assertNull( e.getEqualEdge( graph2 ) );

        Edge copy = (Edge) e.copy( graph2 );
        assertNotNull(copy);
        assertEquals(copy, e.getEqualEdge(graph2));
        assertEquals(e, copy.getEqualEdge(g));
	}

	public void testEdgeEquivalent2() {
		Graph graph2 = (Graph) g.copy();
		Set s = new HashSet( g.getEdges() );
		s.removeAll( graph2.getEdges() );
		assertEquals( s.size(), 0 );
	}


}