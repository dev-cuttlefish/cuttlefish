/*
 * Created on Apr 4, 2004
 *
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package test.edu.uci.ics.jung.graph.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SimpleSparseVertex;
import edu.uci.ics.jung.graph.impl.SimpleUndirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseVertex;
import edu.uci.ics.jung.utils.PredicateUtils;

/**
 * 
 * @author Danyel Fisher & Joshua O'Madadhain
 */
public class VertexTypeTest2 extends TestCase {
	private Graph g;
	private Vertex v1, v2, v3, v4;
	protected boolean[] params;

	protected boolean supportsDirected;
	private boolean supportsUndirected;
	private boolean supportsParallel;

	private Class vertexCtor;
	private Class graphCtor;
    private boolean graphSupportsParallel;
	

	public VertexTypeTest2(Class graphCtor, Class vertexCtor, boolean supportsDirected, boolean supportsUndirected, boolean supportsParallel, boolean gSupportsParallel) {
		super( "VertexTest:" + shorten(graphCtor.getName()) + ":" + shorten(vertexCtor.getName()) );
		this.graphCtor = graphCtor;
		this.vertexCtor = vertexCtor;
		this.supportsDirected = supportsDirected;
		this.supportsUndirected = supportsUndirected;
		this.supportsParallel = supportsParallel;
		this.graphSupportsParallel = gSupportsParallel;
	}


	/**
	 * @param name
	 * @return
	 */
	private static String shorten(String name) {
		return name.substring( name.lastIndexOf('.')+1);
	}


	public static Test suite() {
		TestSuite suite= new TestSuite("VertexTypeTest2");
		suite.addTest( new VertexTypeTest2( SparseGraph.class, SimpleSparseVertex.class, true, true, false, true )) ;
		suite.addTest( new VertexTypeTest2( SparseGraph.class, SparseVertex.class, true, true, true, true )) ;
		suite.addTest( new VertexTypeTest2( SparseGraph.class, SimpleDirectedSparseVertex.class, true, false, false, true )) ;
		suite.addTest( new VertexTypeTest2( SparseGraph.class, DirectedSparseVertex.class, true, false, true, true)) ;
		suite.addTest( new VertexTypeTest2( SparseGraph.class, SimpleUndirectedSparseVertex.class, false, true , false , true)) ;
		suite.addTest( new VertexTypeTest2( SparseGraph.class, UndirectedSparseVertex.class, false, true , true , true)) ;
		
		suite.addTest( new VertexTypeTest2( DirectedSparseGraph.class, SimpleSparseVertex.class, true, false, false , false )) ;
		suite.addTest( new VertexTypeTest2( DirectedSparseGraph.class, SparseVertex.class, true, false, true, false )) ;
		suite.addTest( new VertexTypeTest2( DirectedSparseGraph.class, SimpleDirectedSparseVertex.class, true, false, false , false )) ;
		suite.addTest( new VertexTypeTest2( DirectedSparseGraph.class, DirectedSparseVertex.class, true, false, true, false )) ;

		suite.addTest( new VertexTypeTest2( UndirectedSparseGraph.class, SimpleSparseVertex.class, false, true, false , false)) ;
		suite.addTest( new VertexTypeTest2( UndirectedSparseGraph.class, SparseVertex.class, false, true, true , false )) ;
		suite.addTest( new VertexTypeTest2( UndirectedSparseGraph.class, SimpleUndirectedSparseVertex.class, false, true , false , false)) ;
		suite.addTest( new VertexTypeTest2( UndirectedSparseGraph.class, UndirectedSparseVertex.class, false, true , true , false )) ;

		
		return suite;
	}

	public void setUp() throws Exception {
		g = (Graph) graphCtor.newInstance();
	}
	
	public void runTest() throws Exception {
		setUp();
		testAddVertexAndEdge();
		tearDown();
	}
	
	public void testAddVertexAndEdge() throws InstantiationException, IllegalAccessException {
		v2 = getVertexInstance();
		v1 = getVertexInstance();
		v3 = getVertexInstance();
		v4 = getVertexInstance();
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);

		try 
        {
			Edge e1_2 = new DirectedSparseEdge(v1, v2);
            Edge e1_1 = new DirectedSparseEdge(v1, v1);
			g.addEdge(e1_2);
            g.addEdge(e1_1);
			if (!supportsDirected) {
				fail(getVertexType()
						+ " should prevent directed edge from being created");
			}
			assertTrue( g.getEdges().contains( e1_2));
            assertTrue( g.getEdges().contains( e1_1));
            g.removeEdge(e1_2);
            g.removeEdge(e1_1);
            g.addEdge(e1_2);
            g.addEdge(e1_1);
		} 
        catch (IllegalArgumentException fe) {
			if (supportsDirected) {
				fail(getVertexType()
						+ " should allow directed edge to be created");
			}
		}

		try {
			Edge e3_4 = new UndirectedSparseEdge(v3, v4);
            Edge e3_3 = new UndirectedSparseEdge(v3, v3);
			g.addEdge(e3_4);
            g.addEdge(e3_3);
			if (!supportsUndirected) {
				fail(getVertexType()
						+ " should prevent undirected edge from being created");
			}
			assertTrue( g.getEdges().contains( e3_4));
            assertTrue( g.getEdges().contains( e3_3));
            g.removeEdge(e3_4);
            g.removeEdge(e3_3);
            g.addEdge(e3_4);
            g.addEdge(e3_3);
		} catch (IllegalArgumentException fe) {
			if (supportsUndirected) {
				fail(getVertexType()
						+ " should allow undirected edge to be created");
			}
		}

		assertTrue( g.getEdges().size() > 0);

        
        
		assertEquals( graphSupportsParallel, !PredicateUtils.enforcesNotParallel(g) );
		
		// check for parallel
		Edge parallelEdge = null;
		try {
			if ( supportsDirected) {
				parallelEdge = new DirectedSparseEdge(v1, v2);
				g.addEdge(parallelEdge);				
			} else if ( supportsUndirected ){
				parallelEdge = new UndirectedSparseEdge(v3, v4);
				g.addEdge(parallelEdge);								
			} else {
				fail("No legal edges!");
			}
			if (!supportsParallel || !graphSupportsParallel) {
				fail(getVertexType()
						+ " should prevent parallel edge from being created");
			}
		} catch ( IllegalArgumentException iae ) {
			if (supportsParallel && graphSupportsParallel) {
				fail(getVertexType()
						+ " should allow parallel edge to be created");
			}
		}		
	}

	protected Class getVertexType() {
		return vertexCtor;
	}
	protected final Vertex getVertexInstance() throws InstantiationException, IllegalAccessException {
		return (Vertex) getVertexType().newInstance();
	}

}