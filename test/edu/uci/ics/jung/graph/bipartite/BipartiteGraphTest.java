/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
* 
* Created on Aug 15, 2003
*/
package test.edu.uci.ics.jung.graph.bipartite;

import java.util.Collection;

import junit.framework.TestCase;
import edu.uci.ics.jung.exceptions.FatalException;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.BipartiteEdge;
import edu.uci.ics.jung.graph.impl.BipartiteGraph;
import edu.uci.ics.jung.graph.impl.BipartiteVertex;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.utils.PredicateUtils;

/**
 * @author danyelf
 */
public class BipartiteGraphTest extends TestCase {

	/**
	 * 
	 */
	public BipartiteGraphTest() {
		super();
	}

	public void testSimpleBipartiteGraph() {
		BipartiteGraph bpg = new BipartiteGraph();
		assertTrue(PredicateUtils.enforcesEdgeConstraint(bpg, Graph.UNDIRECTED_EDGE));

		BipartiteVertex a =
			(BipartiteVertex) bpg.addVertex(
				new BipartiteVertex(),
				BipartiteGraph.CLASSA);
		assertTrue(bpg.getPartition(a) == BipartiteGraph.CLASSA);
		assertFalse(bpg.getPartition(a) == BipartiteGraph.CLASSB);
		BipartiteVertex b =
			(BipartiteVertex) bpg.addVertex(
				new BipartiteVertex(),
				BipartiteGraph.CLASSB);
		assertTrue(bpg.getPartition(b) == BipartiteGraph.CLASSB);
		assertFalse(bpg.getPartition(b) == BipartiteGraph.CLASSA);
		try {
			BipartiteVertex v = new BipartiteVertex();
			bpg.getPartition(v);
			fail("getPartition should throw if given a vertex not in partition");
		} catch (IllegalArgumentException iae) {
		}

		bpg.addBipartiteEdge(new BipartiteEdge(a, b));
		assertTrue(a.isNeighborOf(b));
		assertTrue(bpg.getAllVertices(BipartiteGraph.CLASSA).contains(a));
		assertTrue(bpg.getAllVertices(BipartiteGraph.CLASSB).contains(b));

		assertEquals(1, bpg.getEdges().size());
		assertEquals(2, bpg.getVertices().size());
		assertEquals(1, bpg.getAllVertices(BipartiteGraph.CLASSA).size());
		assertEquals(1, bpg.getAllVertices(BipartiteGraph.CLASSB).size());
	}

	public void testCopy() {
		BipartiteGraph bpg = new BipartiteGraph();
		assertTrue(PredicateUtils.enforcesEdgeConstraint(bpg, Graph.UNDIRECTED_EDGE));

		BipartiteVertex a =
			(BipartiteVertex) bpg.addVertex(
				new BipartiteVertex(),
				BipartiteGraph.CLASSA);
		BipartiteVertex b =
			(BipartiteVertex) bpg.addVertex(
				new BipartiteVertex(),
				BipartiteGraph.CLASSB);

		bpg.addBipartiteEdge(new BipartiteEdge(a, b));

		BipartiteGraph bpg2 = (BipartiteGraph) bpg.copy();

		assertTrue(bpg2.getPartition(a) == BipartiteGraph.CLASSA);
		assertFalse(bpg2.getPartition(a) == BipartiteGraph.CLASSB);

		assertTrue(bpg2.getPartition(b) == BipartiteGraph.CLASSB);
		assertFalse(bpg2.getPartition(b) == BipartiteGraph.CLASSA);

		assertEquals("not enough edges", 1, bpg2.getEdges().size());
		assertEquals("not enough vertices", 2, bpg2.getVertices().size());

		assertEquals(
			"not in class B",
			1,
			bpg2.getAllVertices(BipartiteGraph.CLASSB).size());
		assertEquals(
			"not in class a",
			1,
			bpg2.getAllVertices(BipartiteGraph.CLASSA).size());

	}

	public void testBipartiteCreationFail2() {
		BipartiteGraph bpg = new BipartiteGraph();
		BipartiteVertex v1 = new BipartiteVertex();
		BipartiteVertex v2 = new BipartiteVertex();
		bpg.addVertex(v1, BipartiteGraph.CLASSA);
		bpg.addVertex(v2, BipartiteGraph.CLASSA);
		Edge e = new UndirectedSparseEdge(v1, v2);
		try {
			bpg.addEdge(e);
			fail("Should only add BipartiteEdges");
		} catch (FatalException fe) {
		}
	}

	public void testBipartiteCreationFail() {
		BipartiteGraph bpg = new BipartiteGraph();
		Vertex v1 = new SparseVertex();
		//		Vertex v2 = new SparseVertex();
		try {
			bpg.addVertex(v1);
			fail("Doesn't stop addVertex calls ");
		} catch (FatalException fe) {
		}
	}

	public void testSimpleBipartiteGraphBadIII() {
		BipartiteGraph bpg = new BipartiteGraph();

		BipartiteVertex a =
			bpg.addVertex(new BipartiteVertex(), BipartiteGraph.CLASSA);
		try {
			bpg.addVertex(a, BipartiteGraph.CLASSA);
			fail("Can't add the same vertex twice");
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testSimpleBipartiteGraphBadIV() {
		BipartiteGraph bpg = new BipartiteGraph();

		BipartiteVertex a =
			(BipartiteVertex) bpg.addVertex(
				new BipartiteVertex(),
				BipartiteGraph.CLASSA);
		BipartiteVertex b =
			(BipartiteVertex) bpg.addVertex(
				new BipartiteVertex(),
				BipartiteGraph.CLASSA);
		try {
			bpg.addEdge(new BipartiteEdge(a, b));
			fail("Should only add edges through addBipartiteEdge");
		} catch (FatalException fe) {
		}
	}

	public void testSimpleBipartiteGraphBadII() {
		BipartiteGraph bpg = new BipartiteGraph();

		BipartiteVertex a =
			(BipartiteVertex) bpg.addVertex(
				new BipartiteVertex(),
				BipartiteGraph.CLASSA);
		BipartiteVertex aa =
			(BipartiteVertex) bpg.addVertex(
				new BipartiteVertex(),
				BipartiteGraph.CLASSA);
		try {
			new BipartiteEdge(a, aa);
			fail("Edges must connect pieces in different partitions");
		} catch (FatalException fe) {
		}
	}

	public void testSimpleBipartiteGraphBadI() {
		BipartiteGraph bpg = new BipartiteGraph();

		try {
			bpg.addVertex(new SparseVertex());
			fail("Must add only bipartite vertices through correct channels!!");
		} catch (FatalException fe) {
		}
		// this test is now obsolete 
		/*
		        try {
					bpg.addVertex(new DirectedSparseVertex());
					fail("Must add only bipartite vertices through correct channels!!");
				} catch (FatalException fe) {
				}
		        */
	}

	public void testBipartiteFold() {
		BipartiteGraph bpg = new BipartiteGraph();
		BipartiteVertex[] vA = new BipartiteVertex[3];
		BipartiteVertex[] vB = new BipartiteVertex[3];
		for (int i = 0; i < 3; i++) {
			vA[i] = new BipartiteVertex();
			vB[i] = new BipartiteVertex();
			bpg.addVertex(vA[i], BipartiteGraph.CLASSA);
			bpg.addVertex(vB[i], BipartiteGraph.CLASSB);
		}
		// A0 is connected to all three Bs
		bpg.addBipartiteEdge(new BipartiteEdge(vA[0], vB[0]));
		bpg.addBipartiteEdge(new BipartiteEdge(vA[0], vB[1]));
		bpg.addBipartiteEdge(new BipartiteEdge(vA[0], vB[2]));
		// A1 is connected to B0 and B1
		bpg.addBipartiteEdge(new BipartiteEdge(vA[1], vB[0]));
		bpg.addBipartiteEdge(new BipartiteEdge(vA[1], vB[1]));
		// A2 isn't connected at all
		Graph g = BipartiteGraph.fold(bpg, BipartiteGraph.CLASSA);
		assertTrue(g.getVertices().size() == 3);
		Vertex[] vG = new Vertex[3];
		for (int i = 0; i < 3; i++) {
			vG[i] = (Vertex) vA[i].getEqualVertex(g);
			assertNotNull(vG[i]);
		}
		assertTrue(vG[0].degree() == 1);
		assertTrue(vG[1].degree() == 1);
		assertTrue(vG[2].degree() == 0);
		assertTrue(vG[0].getNeighbors().contains(vG[1]));
		assertTrue(vG[1].getNeighbors().contains(vG[0]));
		assertFalse(vG[1].getNeighbors().contains(vG[1]));
		Edge e = vG[0].findEdge(vG[1]);
		Collection tags =
			(Collection) e.getUserDatum(BipartiteGraph.BIPARTITE_USER_TAG);
		assertTrue(tags.size() == 2);
		assertTrue(tags.contains(vB[0]));
		assertTrue(tags.contains(vB[1]));
	}

	public void testBipartiteRemoveVertex() {
		BipartiteGraph bpg = new BipartiteGraph();
		BipartiteVertex[] vA = new BipartiteVertex[3];
		BipartiteVertex[] vB = new BipartiteVertex[3];
		for (int i = 0; i < 3; i++) {
			vA[i] = new BipartiteVertex();
			vB[i] = new BipartiteVertex();
			bpg.addVertex(vA[i], BipartiteGraph.CLASSA);
			bpg.addVertex(vB[i], BipartiteGraph.CLASSB);
		}
		// A0 is connected to all three Bs
		bpg.addBipartiteEdge(new BipartiteEdge(vA[0], vB[0]));
		bpg.addBipartiteEdge(new BipartiteEdge(vA[0], vB[1]));
		bpg.addBipartiteEdge(new BipartiteEdge(vA[0], vB[2]));
		// A1 is connected to B0 and B1
		bpg.addBipartiteEdge(new BipartiteEdge(vA[1], vB[0]));
		bpg.addBipartiteEdge(new BipartiteEdge(vA[1], vB[1]));
		// A2 isn't connected at all

		int v = bpg.getVertices().size();
		int a = bpg.getAllVertices( BipartiteGraph.CLASSA ).size();
		int b = bpg.getAllVertices( BipartiteGraph.CLASSB ).size();
		assertEquals("default condition", v, a + b );
	
		bpg.removeVertex(vA[0]);		
		// ok, va[0] is no longer connected to vb[0], vb[1], vb[2]
		assertEquals( vB[0].getNeighbors().size(), 1);

		v = bpg.getVertices().size();
		a = bpg.getAllVertices( BipartiteGraph.CLASSA ).size();
		b = bpg.getAllVertices( BipartiteGraph.CLASSB ).size();
		assertEquals("changed condition", v, a + b );
		
	}

}