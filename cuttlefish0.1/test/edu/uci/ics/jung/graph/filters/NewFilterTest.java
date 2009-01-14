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
 * Created on Jul 1, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.edu.uci.ics.jung.graph.filters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import edu.uci.ics.jung.exceptions.FatalException;
import edu.uci.ics.jung.graph.ArchetypeGraph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeWeightLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.filters.EfficientFilter;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.filters.GraphAssemblyRecord;
import edu.uci.ics.jung.graph.filters.SerialFilter;
import edu.uci.ics.jung.graph.filters.UnassembledGraph;
import edu.uci.ics.jung.graph.filters.impl.AlphabeticVertexFilter;
import edu.uci.ics.jung.graph.filters.impl.DropSoloNodesFilter;
import edu.uci.ics.jung.graph.filters.impl.WeightedEdgeGraphFilter;
import edu.uci.ics.jung.graph.impl.AbstractSparseGraph;
import edu.uci.ics.jung.utils.PredicateUtils;
import edu.uci.ics.jung.utils.TestGraphs;

/**
 * @author danyelf
 *
 */
public class NewFilterTest extends TestCase {

	AbstractSparseGraph g;
	StringLabeller sl;
	EdgeWeightLabeller el;

	protected void setUp() {
		g = TestGraphs.createTestGraph(true);
		el = EdgeWeightLabeller.getLabeller(g);
		sl = StringLabeller.getLabeller(g);
	}

	protected void tearDown() {
		el = null;
		g = null;
		sl = null;
	}

	public void testBasicFilter() {
		WeightedEdgeGraphFilter wgf = new WeightedEdgeGraphFilter(0, el);
		for (int threshold = 0; threshold < 10; threshold++) {
			wgf.setValue(threshold);
			assertEquals(wgf.getValue(), threshold);
			doFilterTest(threshold, wgf);
		}
	}

	public void testBasicFilterVertex() {
		String[] thresholds = { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
		AlphabeticVertexFilter wgf =
			new AlphabeticVertexFilter("Z", sl, true);
		for (int threshold = 0; threshold < thresholds.length; threshold++) {
			wgf.setThreshhold(thresholds[threshold]);
			assertEquals(wgf.getThreshhold(), thresholds[threshold]);
			doFilterTest(0, wgf);
		}
	}

	private void doFilterTest(int threshold, Filter wgf) {
		UnassembledGraph ug = wgf.filter(g);
		// all edges in UG should still be parts of G
		for (Iterator iter = ug.getUntouchedEdges().iterator();
			iter.hasNext();
			) {
			Edge element = (Edge) iter.next();
			assertSame(element.getGraph(), g);
			assertTrue(el.getWeight(element) >= threshold);
		}
		// all vertices in UG should still be parts of G
		for (Iterator iter = ug.getUntouchedVertices().iterator();
			iter.hasNext();
			) {
			Vertex v = (Vertex) iter.next();
			assertSame(v.getGraph(), g);
		}

		// check that all remaining edges are below threshold
		for (Iterator iter = g.getEdges().iterator(); iter.hasNext();) {
			Edge e = (Edge) iter.next();
			if (ug.getUntouchedEdges().contains(e))
				continue;
			assertTrue(el.getWeight(e) < threshold);
		}
	}

	public void testAssembly() {
		for (int threshold = 0; threshold < 10; threshold++) {
			WeightedEdgeGraphFilter wgf = new WeightedEdgeGraphFilter(0, el);
			wgf.setValue(threshold);
			doTestAssembly(wgf, true);
		}
	}

	public void testAssemblyVertex() {
		String[] thresholds = { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
		AlphabeticVertexFilter wgf =
			new AlphabeticVertexFilter("Z", sl, true);
		for (int threshold = 0; threshold < thresholds.length; threshold++) {
			wgf.setThreshhold(thresholds[threshold]);
			doTestAssembly(wgf, false);
		}
	}

	private void doTestAssembly(Filter wgf, boolean testSameNumberEdges) {
		UnassembledGraph ug = wgf.filter(g);
		Graph ag = ug.assemble();
		assertNotNull(ag);
		assertTrue(ag instanceof ArchetypeGraph);
		assertEquals(ug.getUntouchedVertices().size(), ag.getVertices().size());
		if (testSameNumberEdges) {
			assertEquals(ag.getEdges().size(), ug.getUntouchedEdges().size());
		}
        assertEquals(PredicateUtils.enforcesEdgeConstraint(ag, Graph.DIRECTED_EDGE),
            PredicateUtils.enforcesEdgeConstraint(g, Graph.DIRECTED_EDGE));
        assertEquals(PredicateUtils.enforcesEdgeConstraint(ag, Graph.UNDIRECTED_EDGE),
            PredicateUtils.enforcesEdgeConstraint(g, Graph.UNDIRECTED_EDGE));
        
//		assertEquals(ag.isDirected(), g.isDirected());
		// all vertices in AG should report to being part of AG
		for (Iterator iter = ag.getEdges().iterator(); iter.hasNext();) {
			assertSame(((Edge) iter.next()).getGraph(), ag);
		}
		for (Iterator iter = ag.getVertices().iterator(); iter.hasNext();) {
			assertSame(((Vertex) iter.next()).getGraph(), ag);
		}
	}

	/**
	 * Each vertex in the original should have a corresponding vertex; these
	 * vertices should be the whole thing.
	 */
	public void testAssemblyII() {
		for (int threshold = 0; threshold < 10; threshold++) {
			WeightedEdgeGraphFilter wgf = new WeightedEdgeGraphFilter(0, el);
			wgf.setValue(threshold);
			doAssemblyII(wgf);
		}
	}

	private void doAssemblyII(WeightedEdgeGraphFilter wgf) {
		Graph ag = wgf.filter(g).assemble();
		// all vertices in AG should report to being part of AG
		Set s = new HashSet();
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			Vertex av = (Vertex) v.getEqualVertex(ag);
			assertSame(av.getGraph(), ag);
			assertEquals(av, v);
			s.add(av);
		}
		assertEquals(s, ag.getVertices());
	}

	/**
	 * Tests an edge filter with a vertex filter serially
	 */
	public void testPairOfFiltersEfficient() {
		EfficientFilter filter1 =
			new AlphabeticVertexFilter("Z", sl, false);
		EfficientFilter filter2 = new WeightedEdgeGraphFilter(0, el);
		UnassembledGraph ug = filter2.filter(filter1.filter(g));
		Graph finalGraph = ug.assemble( true );
		// both filters were neutral
		assertNull(GraphAssemblyRecord.getAssemblyRecord(g));
		GraphAssemblyRecord gar =
			GraphAssemblyRecord.getAssemblyRecord(finalGraph);
		assertNotNull(gar);
//		System.out.println(gar.getName());
	}

	public void testLocalFilterSerialCompliance() {
		try {
			EfficientFilter alphaFilter =
				new AlphabeticVertexFilter("Z", sl, false);

			// note: the compiler would catch this if the user (properly) left it 
			// as a NewFilter, rather than casting it.
			DropSoloNodesFilter dropFilter =
				(DropSoloNodesFilter) DropSoloNodesFilter.getInstance();

			dropFilter.filter(alphaFilter.filter(g));
		} catch (FatalException fe) {
			return;
		}
		fail("Doesn't check for efficiency constraints correctly.");
	}

	public void testLocalFilterSerialComplianceII() {
		EfficientFilter alphaFilter =
			new AlphabeticVertexFilter("Z", sl, false);
		Filter dropFilter = DropSoloNodesFilter.getInstance();

		alphaFilter.filter(dropFilter.filter(g));
	}

	/**
	 * Tests an edge filter against the solo nodes filter serially
	 */
	public void testPairOfFiltersInefficient() {
		EfficientFilter alphaFilter =
			new AlphabeticVertexFilter("Z", sl, true);

		// note: the compiler would catch this if the user (properly) left it 
		// as a NewFilter, rather than casting it.
		Filter dropFilter = DropSoloNodesFilter.getInstance();

		UnassembledGraph ug1 = alphaFilter.filter(g);
		assertEquals( g.getVertices().size(), ug1.getUntouchedVertices().size());

		UnassembledGraph ug = dropFilter.filter(ug1.assemble());
		assertEquals( g.getVertices().size(), ug.getUntouchedVertices().size());

		Graph finalGraph = ug.assemble( true );
		GraphAssemblyRecord gar =
			GraphAssemblyRecord.getAssemblyRecord( finalGraph );
		assertNotNull(gar);
		// should have all its vertices and all its edges
		assertEquals( g.getVertices().size(), finalGraph.getVertices().size());
//		System.out.println( gar.getNameExtended() );
	}

	public void testFilterList() {
		SerialFilter sf = new SerialFilter();
		EfficientFilter alphaFilter =
			new AlphabeticVertexFilter("Z", sl, true);
			
		sf.append( alphaFilter );

		// note: the compiler would catch this if the user (properly) left it 
		// as a NewFilter, rather than casting it.
		Filter dropFilter = DropSoloNodesFilter.getInstance();

		sf.append( dropFilter );

		Filter numericalFilter = new WeightedEdgeGraphFilter(0,el);
		
		sf.append( numericalFilter );

		UnassembledGraph ug = sf.filter( g );

		Graph finalGraph = ug.assemble( true );
		GraphAssemblyRecord gar =
			GraphAssemblyRecord.getAssemblyRecord( finalGraph );
		assertNotNull(gar);

		assertEquals( g.getVertices().size(), finalGraph.getVertices().size());
	}


}
