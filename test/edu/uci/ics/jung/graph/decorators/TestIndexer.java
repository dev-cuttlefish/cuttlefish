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
 * Created on Jun 13, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.edu.uci.ics.jung.graph.decorators;

import java.util.Iterator;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author danyelf
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestIndexer extends TestCase {

	DirectedSparseGraph graph;
	Vertex v1, v2;

	protected void setUp() {
		graph = new DirectedSparseGraph();
		GraphUtils.addVertices( graph, 10 );

		int count = 0;
		// grab two random vertices. We'll use them later.
		for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			count++;
			if (count == 3)
				v1 = v;
			if (count == 7)
				v2 = v;
		}
	}

	protected void tearDown() {
		graph = null;
		v1 = null;
		v2 = null;
	}

	public void testIndexerSetup() {
		assertFalse(Indexer.hasIndexer(graph));
		Indexer ind = Indexer.getIndexer(graph);
		assertNotNull(ind);
		Indexer ind2 = Indexer.getIndexer(graph);
		assertNotNull(ind2);
		assertTrue(ind == ind2);

		Indexer ind3 = Indexer.getIndexer(graph, "CUSTOM_KEY ");
		assertNotNull(ind3);
		assertFalse(ind == ind3);
	}

	public void testIndexerSetup2() {
		assertFalse(Indexer.hasIndexer(graph));
		Indexer ind = Indexer.getIndexer(graph);
		Vertex vert0 = (Vertex)ind.getVertex(0);
		Vertex vert1 = (Vertex)ind.getVertex(1);
		assertTrue(vert0.getGraph() == graph);
		assertTrue(vert1.getGraph() == graph);
		assertFalse(vert0 == vert1);

		int i = ind.getIndex(vert0);
		assertTrue(i == 0);
		i = ind.getIndex(vert1);
		assertTrue(i == 1);
	}

	public void testIndexerSimple() {
		Indexer ind = Indexer.getIndexer(graph);
		int index = ind.getIndex(v1);
		int index2 = ind.getIndex(v2);
		assertFalse(index == index2);
		assertFalse(v1 == v2);
		assertEquals(ind.getVertex(index), v1);
		assertEquals(ind.getVertex(index2), v2);
	}

	public void testEnoughInIndex() {
		Indexer ind = Indexer.getIndexer(graph);
		int count = 0;
		for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			int i = ind.getIndex(v);
			Vertex compare = (Vertex)ind.getVertex(i);
			int backtest = ind.getIndex(compare);
			assertSame(
				"at iteration " + count + " and index " + i,
				ind.getVertex(i),
				v);
			assertEquals( "at iteration " + count + " and index " + i,
					i, backtest);
			count++;
		}
	}


}
