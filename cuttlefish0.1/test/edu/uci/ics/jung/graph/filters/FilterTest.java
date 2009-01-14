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
 * (c) 2002 Danyel Fisher, Paul Dourish, 
 * and the Regents of the University of California
 *
 * Created on May 8, 2003
 *
 */
package test.edu.uci.ics.jung.graph.filters;

//import java.util.Iterator;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeWeightLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.filters.UnassembledGraph;
import edu.uci.ics.jung.graph.filters.impl.WeightedEdgeGraphFilter;
import edu.uci.ics.jung.graph.impl.AbstractSparseGraph;
import edu.uci.ics.jung.utils.TestGraphs;
import edu.uci.ics.jung.utils.UserData;
//import edu.uci.ics.jung.visualization.demo.WeightedGraphImpl;
//import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;

/**
 * @author danyelf
 */
public class FilterTest extends TestCase {

//	private static final Object LABEL = "FilterTest Label";

	public static Test suite() {
		return new TestSuite(FilterTest.class);
	}

	AbstractSparseGraph g;
	WeightedEdgeGraphFilter wgf;
	StringLabeller sl;

	protected void setUp(boolean directed) {
		g = TestGraphs.createTestGraph( directed );
		wgf = new WeightedEdgeGraphFilter( 0, EdgeWeightLabeller.getLabeller( g ) );
		sl = StringLabeller.getLabeller( g );
	}

	protected void tearDown() {
		g = null;
		wgf = null;
		sl = null;
	}

	private void doBasicFilter() {
		// check some graph invariants
		assertEquals(g.getVertices().size(), 9);
		assertEquals(g.getEdges().size(), TestGraphs.pairs.length);

		UnassembledGraph fg = wgf.filter(g);

		assertEquals(fg.getUntouchedVertices(), g.getVertices());
		assertEquals(fg.getUntouchedEdges(), g.getEdges());
	}

	public void testBasicFilterUndirected() {
		setUp(false);
		doBasicFilter();
	}

	public void testBasicFilterDirected() {
		setUp(true);
		doBasicFilter();
	}

	private void doSizes() {
		wgf.setValue(2);
		Graph fg = wgf.filter(g).assemble();
		assertEquals(7, fg.getEdges().size());

		wgf.setValue(3);
		fg = wgf.filter(g).assemble();
		assertEquals(7, fg.getEdges().size());

		wgf.setValue(4);
		fg = wgf.filter(g).assemble();
		assertEquals(6, fg.getEdges().size());

		wgf.setValue(100);
		fg = wgf.filter(g).assemble();
		assertEquals(0, fg.getEdges().size());
		assertEquals(fg.getVertices().size(), g.getVertices().size());

	}

	public void testSizesUndirected() {
		setUp(false);
		doSizes();
	}

	public void testSizesDirected() {
		setUp(true);
		doSizes();
	}

	public void testLabelsAreDifferent() {
		setUp(true);
		assertSame(sl, g.getUserDatum(StringLabeller.DEFAULT_STRING_LABELER_KEY));
		assertSame(
			UserData.REMOVE,
			g.getUserDatumCopyAction(StringLabeller.DEFAULT_STRING_LABELER_KEY));

		Graph fg = wgf.filter(g).assemble();
		assertNotSame(fg, g);

		assertNull(fg.getUserDatum(StringLabeller.DEFAULT_STRING_LABELER_KEY));

		StringLabeller sl2 = StringLabeller.getLabeller(fg);
		assertNotSame(sl, sl2);
	}

	// it should be the case that the filtedred variant of a vertex is
	// unlabelled. Um, I think.
	public void testLabels() {
		setUp(true);
		Graph fg = wgf.filter(g).assemble();

		StringLabeller sl2 = StringLabeller.getLabeller(fg);
		Vertex front = (Vertex) g.getVertices().iterator().next();
		Vertex front_prime = (Vertex) front.getEqualVertex(fg);
		Assert.assertNotNull(front_prime);
		Assert.assertEquals(front.degree(), front_prime.degree());
		Assert.assertNotSame(sl.getLabel(front), sl2.getLabel(front_prime));
	}

}
