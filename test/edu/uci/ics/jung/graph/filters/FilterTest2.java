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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.exceptions.FatalException;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeWeightLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.filters.impl.WeightedEdgeGraphFilter;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author danyelf
 */
public class FilterTest2 extends TestCase {

//	private static final Object LABEL = "FilterTest Label";
	static String[][] pairs = { { "a", "b", "3" }, {
			"a", "c", "4" }, {
			"a", "d", "5" }, {
			"d", "c", "6" }, {
			"d", "e", "7" }, {
			"e", "f", "8" }, {
			"f", "g", "9" }, {
			"h", "i", "1" }
	};

	public static Test suite() {
		return new TestSuite(FilterTest2.class);
	}

	UndirectedSparseGraph g;
	WeightedEdgeGraphFilter wgf;
	StringLabeller sl;

	protected void setUp() {
		g = new UndirectedSparseGraph();
		sl = StringLabeller.getLabeller(g);
		EdgeWeightLabeller el = EdgeWeightLabeller.getLabeller(g);
		for (int i = 0; i < pairs.length; i++) {
			String[] pair = pairs[i];
			createEdge(g, sl, el, pair[0], pair[1], Integer.parseInt(pair[2]));
		}
		wgf = new WeightedEdgeGraphFilter(0, el);
//		fg = wgf.filter(g);
	}

	private static void createEdge(
		final UndirectedSparseGraph g,
		StringLabeller sl,
		EdgeWeightLabeller el,
		String v1Label,
		String v2Label,
		int weight) {

		try {
			Vertex v1 = sl.getVertex(v1Label);
			if (v1 == null) {
//				v1 = (AbstractSparseVertex) g.addVertex();
				v1 = (Vertex) g.addVertex( new SparseVertex());
				sl.setLabel(v1, v1Label);
			}
			Vertex v2 = sl.getVertex(v2Label);
			if (v2 == null) {
				v2 = (Vertex) g.addVertex( new SparseVertex());
				sl.setLabel(v2, v2Label);
			}
			UndirectedSparseEdge e = (UndirectedSparseEdge) GraphUtils.addEdge(g, v1, v2);
			el.setWeight(e, weight);
		} catch (StringLabeller.UniqueLabelException e) {
			throw new FatalException("This should not happen " + e);
		}
	}

	protected void tearDown() {
		g = null;
//		fg = null;
		wgf = null;
		sl = null;
	}

	public void testFilterOp() {
		// check some graph invariants
		Graph fg = wgf.filter(g).assemble();
		
		BetweennessCentrality bc = new BetweennessCentrality(fg,true);
		bc.setRemoveRankScoresOnFinalize(false);
		bc.evaluate();
		
		wgf.setValue( 5 );
		fg = wgf.filter(g).assemble();

		bc = new BetweennessCentrality(fg,true);
		bc.setRemoveRankScoresOnFinalize(false);
		bc.evaluate();
			
	}


}
