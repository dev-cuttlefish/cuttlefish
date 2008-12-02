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
 * Created on Jul 10, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.edu.uci.ics.jung.graph.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.exceptions.ConstraintViolationException;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;

/**
 * @author danyelf
 */
public abstract class FailingGraphTest extends TestCase {

	public abstract Vertex getVertex();
	
	public static Test suite() {
		TestSuite suite = new TestSuite("FailingGraphTest");
		suite.addTestSuite(SparseTest.class);
		return suite;
	}
	
	public static class SparseTest extends FailingGraphTest {

		public Vertex getVertex() {
			return new SparseVertex();
		}
	}

	
	public void testSillyGraph() {
		Graph g = new DirectedSparseGraph();
		Vertex v = getVertex();
		g.addVertex(v);
		try {
			g.addVertex(v);
			fail("Doesn't catch orphan double adding");
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testSillyGraph2() {
		Graph g = new DirectedSparseGraph();
		Graph g2 = new DirectedSparseGraph();
		Vertex v = getVertex();
		g.addVertex(v);
		try {
			g2.addVertex(v);
			fail("Doesn't catch orphan adding twice");
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testSillyGraph3() {
		Graph g = new DirectedSparseGraph();
		Vertex v = getVertex();
		Vertex v2 = getVertex();
		g.addVertex(v);
		g.addVertex(v2);
		Edge e = new DirectedSparseEdge(v, v2);
		g.addEdge(e);
		try {
			g.addEdge(e);
			fail("Doesn't catch adding twice");
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testSillyGraph4() {
		Graph g = new DirectedSparseGraph();
		Graph g2 = (Graph) g.copy();
		Vertex v = getVertex();
		Vertex v2 = getVertex();
		g.addVertex(v);
		g.addVertex(v2);
		Edge e = new DirectedSparseEdge(v, v2);
		try {
			g2.addEdge(e);
			fail("Doesn't catch orphan adding");
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testSillyGraph5() {
		Graph g = new DirectedSparseGraph();
		Graph g2 = (Graph) g.copy();
		Vertex v = getVertex();
		Vertex v2 = getVertex();
		g.addVertex(v);
		g2.addVertex(v2);
		try {
			Edge e = new DirectedSparseEdge(v, v2);
			g.addEdge(e);
			fail("Doesn't catch malformed (two-graph) edge");
		} catch (IllegalArgumentException fe) {
		}
	}

	public void testSillyGraph6() {
		Graph g = new DirectedSparseGraph();
		Graph g2 = (Graph) g.copy();
		Vertex v = getVertex();
		Vertex v2 = getVertex();
		g.addVertex(v);
		g.addVertex(v2);
		Edge e = new DirectedSparseEdge(v, v2);
		g.removeVertex(v);
		g2.addVertex(v);
		try {
			g.addEdge(e);
			fail("Doesn't catch orphan adding twice");
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testSillyGraph7() {
		Graph g = new DirectedSparseGraph();
		Vertex v1 = getVertex();
		Vertex v2 = getVertex();
		g.addVertex(v1);
		try {
			new DirectedSparseEdge(v1, v2);
			fail("Doesn't catch orphan edge creation");
		} catch (IllegalArgumentException fe) {
		}
	}

	public void testSillyGraph8() {
		Graph g = new DirectedSparseGraph();
		Vertex v1 = getVertex();
		Vertex v2 = getVertex();
		g.addVertex(v1);
		try {
			new DirectedSparseEdge(v2, v1);
			fail("Doesn't catch orphan edge creation");
		} catch (IllegalArgumentException fe) {
		}
	}

	public void testSillyGraph9() {
		Graph g = new DirectedSparseGraph();
		try {
			g.addVertex(null);
			fail("Doesn't stop null addition");
		} catch (RuntimeException re) {
		}
	}

	public void testSillyGraph10A() {
		Graph g = new DirectedSparseGraph();
		Vertex v1 = getVertex();
		g.addVertex(v1);
		try {
			new DirectedSparseEdge(null, v1);
			fail("Doesn't catch null edge creation");
		} catch (IllegalArgumentException fe) {
		}
	}

	public void testSillyGraph10B() {
		Graph g = new DirectedSparseGraph();
		Vertex v1 = getVertex();
		g.addVertex(v1);
		try {
			new DirectedSparseEdge(v1, null);
			fail("Doesn't catch null edge creation");
		} catch (IllegalArgumentException fe) {
		}
	}

	public void testSillyGraph11() {
		Graph g = new DirectedSparseGraph();
		try {
			g.addEdge(null);
			fail("Doesn't catch null edge addition");
		} 
        catch (NullPointerException fe) {}
        catch (ConstraintViolationException cve) {}
    }
	
}
