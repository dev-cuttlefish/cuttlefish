/*
 * Created on Jun 14, 2004
 */
package test.edu.uci.ics.jung.graph.decorators;

import java.util.Iterator;

import junit.framework.TestCase;
import edu.uci.ics.jung.exceptions.FatalException;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.GlobalStringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * 
 * @author danyelf
 */
public class TestGlobalStringLabeller extends TestCase {

	Graph graph;
	Vertex v1, v2;

	protected void setUp() {
		graph = new DirectedSparseGraph();
		GraphUtils.addVertices(graph, 10);

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
		StringLabeller sl = GlobalStringLabeller.getInstance();
		sl.clear();
		graph = null;
		v1 = null;
		v2 = null;
	}

	public void testGlobalLabeller() throws UniqueLabelException {
		StringLabeller sl = GlobalStringLabeller.getLabeller(graph);
		Graph g2 = (Graph) graph.copy();
		Vertex vx = g2.addVertex(new SparseVertex());
		sl.setLabel(vx, "label");
		assertTrue(sl instanceof GlobalStringLabeller);
		int count = 0;
		for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			Vertex v2 = (Vertex) v.getEqualVertex(g2);
			sl.setLabel(v, "" + ('a' + count));
			assertEquals(sl.getLabel(v), "" + ('a' + count));
			assertEquals(sl.getLabel(v2), "" + ('a' + count));
			count++;
		}
		Vertex v0 = graph.addVertex(new SparseVertex());
		sl.setLabel(v0, "label_x");
		assertEquals("label_x", sl.getLabel(v0));
		assertNull(v0.getEqualVertex(g2));

		assertEquals("label", sl.getLabel(vx));
		assertNull(vx.getEqualVertex(graph));
	}

	/**
	 * JMadden pointed out that this is a problem: if you forget to clear out
	 * the table between runs, it'll fail.
	 * 
	 * @throws UniqueLabelException
	 */
	public void testLabelRelabel() throws UniqueLabelException {
		StringLabeller sl = GlobalStringLabeller.getLabeller(graph);
		sl.setLabel(v1, "truth");
		sl.clear();
		try {
			sl.getLabel(v1);
			fail("Shouldn't be able to get an unlabelled vertex");
		} catch (FatalException fe) {

		}
	}

	public void testChangeInGlobalLabeller() throws UniqueLabelException {
		StringLabeller sl = GlobalStringLabeller.getLabeller(graph);

		sl.setLabel(v1, "truth");
		assertEquals(sl.getLabel(v1), "truth");
		assertEquals(v1, sl.getVertex("truth"));

		try {
			sl.setLabel(v2, "truth");
			fail("Can't add a second vertex");
		} catch (Exception e) {
		}

		sl.removeLabel("truth");
		sl.setLabel(v2, "truth");
		assertEquals(sl.getLabel(v2), "truth");
		assertEquals(sl.getVertex("truth"), v2);
	}

	public void testGlobalLabellerChange() throws UniqueLabelException {
		StringLabeller sl = GlobalStringLabeller.getLabeller(graph);
		String test = "Test1";
		sl.setLabel(v1, test);
		assertEquals(sl.getLabel(v1), test);
		sl.setLabel(v1, "Test2 ");
		assertNotSame(sl.getLabel(v1), test);
		sl.setLabel(v1, test);
		assertEquals(sl.getLabel(v1), test);
	}

}