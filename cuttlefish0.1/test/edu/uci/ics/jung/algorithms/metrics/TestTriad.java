package test.edu.uci.ics.jung.algorithms.metrics;

import junit.framework.TestCase;
import edu.uci.ics.jung.algorithms.metrics.TriadicCensus;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseVertex;

public class TestTriad extends TestCase {

	public void testConfigurationFromPaper() {
		DirectedGraph g = new DirectedSparseGraph();
		Vertex u = g.addVertex(new DirectedSparseVertex());
		Vertex v = g.addVertex(new DirectedSparseVertex());
		Vertex w = g.addVertex(new DirectedSparseVertex());
		g.addEdge(new DirectedSparseEdge(w, u));
		g.addEdge(new DirectedSparseEdge(u, v));
		g.addEdge(new DirectedSparseEdge(v, u));

		assertEquals(35, TriadicCensus.triCode(u, v, w));
		assertEquals(7, TriadicCensus.triType(35));
		assertEquals("111D", TriadicCensus.TRIAD_NAMES[7]);

		assertEquals(7, TriadicCensus.triType(TriadicCensus.triCode(u, w, v)));
		assertEquals(7, TriadicCensus.triType(TriadicCensus.triCode(v, u, w)));

        long[] counts = TriadicCensus.getCounts(g);
//		TriadicCensus c = TriadicCensus.Count(g);
		for (int i = 1; i <= 16; i++) {
			if (i == 7) {
//				assertEquals(1, c.getCount(i));
                assertEquals(1, counts[i]);
			} else {
//				assertEquals(0, c.getCount(i));
                assertEquals(0, counts[i]);
			}

		}
	}

	public void testFourVertexGraph() {
		// we'll set up a graph of
		// t->u
		// u->v
		// and that's it.
		// total count:
		// 2: 1(t, u, w)(u, v, w)
		// 6: 1(t, u, v)
		// 1: 1(u, v, w)
		DirectedGraph g = new DirectedSparseGraph();
		Vertex u = g.addVertex(new DirectedSparseVertex());
		Vertex v = g.addVertex(new DirectedSparseVertex());
		Vertex w = g.addVertex(new DirectedSparseVertex());
		Vertex t = g.addVertex( new DirectedSparseVertex() );
		
		g.addEdge( new DirectedSparseEdge( t, u ));
		g.addEdge( new DirectedSparseEdge( u, v ));
				
        long[] counts = TriadicCensus.getCounts(g);
//		TriadicCensus c = TriadicCensus.Count(g);
		for (int i = 1; i <= 16; i++) {
			if( i == 2 ) {
//				assertEquals("On " + i, 2, c.getCount(i));				
                assertEquals("On " + i, 2, counts[i]);              
			} else if (i == 6 || i == 1  ) {
//				assertEquals("On " + i, 1, c.getCount(i));
                assertEquals("On " + i, 1, counts[i]);
			} else {
//				assertEquals(0, c.getCount(i));
                assertEquals(0, counts[i]);
			}
		}
		
		// now let's tweak to 
		// t->u, u->v, v->t
		// w->u, v->w
		g.addEdge( new DirectedSparseEdge( v, t ));
		g.addEdge( new DirectedSparseEdge( w, u ));
		g.addEdge( new DirectedSparseEdge( v, w ));

		// that's two 030Cs. it's a 021D (v-t, v-w) and an 021U (t-u, w-u)
        counts = TriadicCensus.getCounts(g);
//		c = TriadicCensus.Count(g);
		for (int i = 1; i <= 16; i++) {
			if( i == 10 /* 030C */ ) {
//				assertEquals("On " + i, 2, c.getCount(i));				
                assertEquals("On " + i, 2, counts[i]);              
			} else if (i == 4 || i == 5  ) {
//				assertEquals("On " + i, 1, c.getCount(i));
                assertEquals("On " + i, 1, counts[i]);
			} else {
//				assertEquals("On " + i , 0, c.getCount(i));
                assertEquals("On " + i , 0, counts[i]);
			}
		}
		
	}
	
	public void testThreeDotsThreeDashes() {
		DirectedGraph g = new DirectedSparseGraph();
		Vertex u = g.addVertex(new DirectedSparseVertex());
		Vertex v = g.addVertex(new DirectedSparseVertex());
		Vertex w = g.addVertex(new DirectedSparseVertex());

        long[] counts = TriadicCensus.getCounts(g);
//		TriadicCensus c = TriadicCensus.Count(g);
		for (int i = 1; i <= 16; i++) {
			if (i == 1) {
//				assertEquals(1, c.getCount(i));
                assertEquals(1, counts[i]);
			} else {
//				assertEquals(0, c.getCount(i));
                assertEquals(0, counts[i]);
			}
		}

		g.addEdge(new DirectedSparseEdge(v, u));
		g.addEdge(new DirectedSparseEdge(u, v));
		g.addEdge(new DirectedSparseEdge(v, w));
		g.addEdge(new DirectedSparseEdge(w, v));
		g.addEdge(new DirectedSparseEdge(u, w));
		g.addEdge(new DirectedSparseEdge(w, u));

        counts = TriadicCensus.getCounts(g);
//		c = TriadicCensus.Count(g);
		for (int i = 1; i <= 16; i++) {
			if (i == 16) {
//				assertEquals(1, c.getCount(i));
                assertEquals(1, counts[i]);
			} else {
//				assertEquals("Count on " + i + " failed", 0, c.getCount(i));
                assertEquals("Count on " + i + " failed", 0, counts[i]);
			}
		}
	}

	/** **************Boring accounting for zero graphs*********** */
	public void testNull() {
		DirectedGraph g = new DirectedSparseGraph();
        long[] counts = TriadicCensus.getCounts(g);
//		TriadicCensus t = TriadicCensus.Count(g);
		// t looks like a hashtable for the twelve keys
		for (int i = 1; i < TriadicCensus.MAX_TRIADS; i++) {
//			assertEquals("Empty Graph doesn't have count 0", 0, t.getCount(i));
            assertEquals("Empty Graph doesn't have count 0", 0, counts[i]);
		}
	}

	public void testOneVertex() {
		DirectedGraph g = new DirectedSparseGraph();
		g.addVertex(new SparseVertex());
        long[] counts = TriadicCensus.getCounts(g);
//		TriadicCensus t = TriadicCensus.Count(g);
		// t looks like a hashtable for the twelve keys
		for (int i = 1; i < TriadicCensus.MAX_TRIADS; i++) {
//			assertEquals("One vertex Graph doesn't have count 0", 0, t
//					.getCount(i));
            assertEquals("One vertex Graph doesn't have count 0", 0, counts[i]);
		}
	}

	public void testTwoVertices() {
		DirectedGraph g = new DirectedSparseGraph();
		Vertex v1, v2;
		g.addVertex(v1 = new SparseVertex());
		g.addVertex(v2 = new SparseVertex());
		g.addEdge(new DirectedSparseEdge(v1, v2));
        long[] counts = TriadicCensus.getCounts(g);
//		TriadicCensus t = TriadicCensus.Count(g);
		// t looks like a hashtable for the twelve keys
		for (int i = 1; i < TriadicCensus.MAX_TRIADS; i++) {
//			assertEquals("Two vertex Graph doesn't have count 0", 0, t
//					.getCount(i));
            assertEquals("Two vertex Graph doesn't have count 0", 0, counts[i]);
		}
	}

}
