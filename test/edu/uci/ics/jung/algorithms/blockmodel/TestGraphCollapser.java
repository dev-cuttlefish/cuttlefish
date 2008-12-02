/*
 * Created on Feb 7, 2004
 */
package test.edu.uci.ics.jung.algorithms.blockmodel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import edu.uci.ics.jung.algorithms.blockmodel.*;
import edu.uci.ics.jung.algorithms.blockmodel.GraphCollapser.CollapsedVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;

/**
 * created Feb 7, 2004
 * @author danyelf
 */
public class TestGraphCollapser extends TestCase {

	GraphCollapser collapser;
	DirectedSparseGraph g;
	Vertex a, b, c, d, e;

	public void setUp() throws UniqueLabelException {
		collapser = GraphCollapser.getInstance();

		g = new DirectedSparseGraph();
		g.addVertex(a = new SparseVertex());
		g.addVertex(b = new SparseVertex());
		g.addVertex(c = new SparseVertex());
		g.addVertex(d = new SparseVertex());
		g.addVertex(e = new SparseVertex());

		StringLabeller sl = StringLabeller.getLabeller(g);

		sl.setLabel(a, "A");
		sl.setLabel(b, "B");
		sl.setLabel(c, "C");
		sl.setLabel(d, "D");
		sl.setLabel(e, "E");

		g.addEdge(new DirectedSparseEdge(a, c));
		g.addEdge(new DirectedSparseEdge(a, d));
		g.addEdge(new DirectedSparseEdge(b, c));
		g.addEdge(new DirectedSparseEdge(b, d));
		g.addEdge(new DirectedSparseEdge(c, e));
		g.addEdge(new DirectedSparseEdge(d, e));
	}


//	public void testFindEdge() {
//		// for each vertex, findEdge to all others and check them
//		assertBothWays( a, c );
//		assertBothWays( a, d );
//		assertBothWays( b, c );
//		assertBothWays( b, d );
//		assertBothWays( c, e );
//		assertBothWays( d, e );				
//	}
//
//	private void assertBothWays(Vertex a2, Vertex c2) {
//		assertNotNull( a2.findEdge( c2 ));
//		assertNotNull( c2.findEdge( a2 ));
//	}


	public void testCollapserNull() throws UniqueLabelException {
		Graph gNew = collapser.getCollapsedGraph(g, new HashSet());
		assertEquals(gNew.getVertices().size(), g.getVertices().size() + 1);
		// find the meta node
		for (Iterator iter = gNew.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			if (g.getVertices().contains(v))
				continue;
			assertEquals(v.degree(), 0);
		}
		// make sure it isn't wired to anything
	}


	public void testCollapserOne() throws UniqueLabelException {
		HashSet hs = new HashSet();
		hs.add(a);
		Graph gNew = collapser.getCollapsedGraph(g, hs);
		assertFalse( gNew == g);
		assertEquals(gNew.getVertices().size(), g.getVertices().size());
		assertNull(a.getEqualVertex(gNew));

		// let's find an edge to the new vertex
		// a connects to c and d
		// c connects to a, b, e
		Vertex cPrime = (Vertex) c.getEqualVertex(gNew);

		assertEquals(3, c.degree());
		assertEquals(3, cPrime.degree());

		assertFalse(cPrime.isNeighborOf(a));
		assertFalse(cPrime.isNeighborOf(d));
		assertTrue(cPrime.isNeighborOf(b));
		assertTrue(cPrime.isNeighborOf(e));

		Set edges = new HashSet(cPrime.getIncidentEdges());

		assertEquals(3, edges.size());
		
		Edge ed = b.findEdge( cPrime );
		edges.remove(ed);
		edges.remove(cPrime.findEdge((Vertex) e.getEqualVertex(gNew)));

		assertEquals(1, edges.size());

		Edge edge = (Edge) edges.iterator().next();
		CollapsedVertex meta = (CollapsedVertex) edge.getOpposite(cPrime);
		assertEquals(meta.getRootSet(), hs);
		assertTrue(meta.isNeighborOf(c));
		assertTrue(meta.isNeighborOf(d));
		assertTrue(meta.isPredecessorOf(c));
		assertTrue(meta.isPredecessorOf(d));
	}

	public void testCollapserTwo() throws UniqueLabelException {
		HashSet hs = new HashSet();
		hs.add(a);
		hs.add(b);
		Graph gNew = collapser.getCollapsedGraph(g, hs);
		assertEquals(gNew.getVertices().size() + 1, g.getVertices().size());
		assertNull(a.getEqualVertex(gNew));
		// let's find an edge to the new vertex
		// a/b connects to c and d
		Vertex cPrime = (Vertex) c.getEqualVertex(gNew);
		Set edges = new HashSet(cPrime.getIncidentEdges());
		assertEquals(edges.size(), 2); // one to a/b, one to e
		edges.remove(cPrime.findEdge(e));
		assertEquals(edges.size(), 1);
		Edge edge = (Edge) edges.iterator().next();
		CollapsedVertex meta = (CollapsedVertex) edge.getOpposite(cPrime);
		assertEquals(meta.getRootSet(), hs);
		assertTrue(meta.isNeighborOf(c));
		assertTrue(meta.isNeighborOf(d));
		assertTrue(meta.isPredecessorOf(c));
		assertTrue(meta.isPredecessorOf(d));
	}
	
	public void testCollapserER() {
		// manually create equivalence relation
		// [AB], [CD]
		
		Set hs_ab = new HashSet(), hs_cd = new HashSet();
		hs_ab.add(a);
		hs_ab.add(b);
		hs_cd.add(c);
		hs_cd.add(d);
		Set ers = new HashSet();
		ers.add( hs_ab );
		ers.add( hs_cd );
	
		EquivalenceRelation er = new EquivalenceRelation(ers, g);
		Graph gNew = collapser.getCollapsedGraph( er );

		// we've removed four vertices, and added two
		assertEquals(gNew.getVertices().size(), g.getVertices().size() - 2);
		assertNull(a.getEqualVertex(gNew));
		assertNull(b.getEqualVertex(gNew));
		assertNull(c.getEqualVertex(gNew));
		assertNull(d.getEqualVertex(gNew));

		CollapsedVertex ab = null, cd = null;

		// corrected graph should be (A,B) -> (C,D) -> E
		for (Iterator iter = gNew.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			if ( v instanceof GraphCollapser.CollapsedVertex) {
				CollapsedVertex cv = (CollapsedVertex) v;
				if( cv.getRootSet().contains(a)) {
					assertTrue( cv.getRootSet().contains(b));
					assertTrue( cv.getRootSet().size() == 2 );
					ab = cv;
				} else { // it's c,d					
					assertTrue( cv.getRootSet().contains(c));
					assertTrue( cv.getRootSet().contains(d));
					assertTrue( cv.getRootSet().size() == 2 );
					cd = cv;
				}
			} else {
				assertEquals( v, e );
			}
		}
		assertTrue( ab.isPredecessorOf( cd ));
		assertTrue( cd.isPredecessorOf( e ));
		
	}

}
