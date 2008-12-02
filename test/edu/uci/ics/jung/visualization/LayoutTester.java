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
 * Created on Jul 26, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.edu.uci.ics.jung.visualization;

import java.awt.Dimension;
import java.util.Iterator;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.filters.impl.AlphabeticVertexFilter;
import edu.uci.ics.jung.random.generators.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.visualization.Layout;

/**
 * @author danyelf
 */
public abstract class LayoutTester extends TestCase {

	abstract protected Layout getLayout(Graph g);

	abstract protected int getIters();

	private void iterateGraph(Layout l, int iters, int x, int y) {
		int iterations = 0;
		while (!l.incrementsAreDone() && iterations < iters) {
			iterations++;
			l.advancePositions();
			for (Iterator iter = l.getVisibleVertices().iterator();
				iter.hasNext();
				) {
				Vertex v = (Vertex) iter.next();
				assertFalse(Double.isNaN(l.getX(v)) || Double.isNaN(l.getY(v)));
				assertTrue(l.getX(v) <= x && l.getX(v) >= 0);
				assertTrue(l.getY(v) <= y && l.getY(v) >= 0);
			}
		}
	}

	public void testLayoutCreation() {
		for (int i = 1; i < 3; i++) {
			Graph g = getGraph();
			Layout l = getLayout(g);
			l.initialize(new Dimension(100, 500));

			assertSame(l.getGraph(), g);
			assertEquals(l.getVisibleVertices(), g.getVertices());
			assertEquals(l.getVisibleEdges(), g.getEdges());
			assertTrue(l.isIncremental());
			l.getStatus();

			for (Iterator iter = l.getVisibleVertices().iterator();
				iter.hasNext();
				) {
				Vertex v = (Vertex) iter.next();
				assertTrue(l.getX(v) <= 100 && l.getX(v) >= 0);
				assertTrue(l.getY(v) <= 500 && l.getY(v) >= 0);
			}
		}
	}

	public void testLayoutIterationSmallSpace() {
		for (int i = 0; i < 3; i++) {
			Graph g = getGraph();
			Layout l = getLayout(g);
			l.initialize(new Dimension(50, 50));

			iterateGraph(l, getIters(), 50, 50);
		}
	}

	public void testLayoutIterationBigSpace() {
		Graph g = getGraph();
		Layout l = getLayout(g);
		l.initialize(new Dimension(300, 300));

		iterateGraph(l, getIters(), 300, 300);
	}

	public void testResize() {
		Graph g = getGraph();
		Layout l = getLayout(g);
		l.initialize(new Dimension(150, 150));

		iterateGraph(l, getIters() / 2, 150, 150);

		Dimension d = new Dimension(300, 300);
		l.resize(d);
		assertEquals(d, l.getCurrentSize());

		iterateGraph(l, getIters() / 2, 300, 300);
	}

	public void testRestart() {
		Graph g = getGraph();
		Layout l = getLayout(g);
		l.initialize(new Dimension(150, 150));
		iterateGraph(l, getIters() / 2, 150, 150);
		l.restart();
		iterateGraph(l, getIters() / 2, 150, 150);
	}

	public void testFilter() {
		Graph g = getGraph();
		Layout l = getLayout(g);
		l.initialize(new Dimension(150, 150));
		l.advancePositions();
		// this'll just slice the graph in half across M.
		Filter f =
			new AlphabeticVertexFilter(
				"m",
				StringLabeller.getLabeller(g),
				true);
		Graph subgraph = f.filter(g).assemble();
		l.applyFilter(subgraph);
		assertTrue(subgraph.getVertices().size() < g.getVertices().size());
		assertEquals(subgraph.getVertices(), l.getVisibleVertices());
		assertEquals(subgraph.getEdges(), l.getVisibleEdges());
		// checks for errors immediately on changeover
		l.advancePositions();
		// and errors that hit eventually
		iterateGraph(l, getIters() / 2, 150, 150);
	}

	/**
	 * @return
	 */
	private Graph getGraph() {
		//		PajekNetFile file = new PajekNetFile();
		//		Graph graph = file.load("smyth.net");
		EppsteinPowerLawGenerator eplg =
			new EppsteinPowerLawGenerator(50, 500, 1000);
		Graph g = (Graph) eplg.generateGraph();
		StringLabeller sl = StringLabeller.getLabeller(g);
		int i = 0;
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			char l = (char) ('a' + i % 26);
			char ll = (char) ('a' + (i / 26) % 26);
			char lll = (char) ('a' + (i / 676) % 26);

			try {
				sl.setLabel(v, "" + l + "" + ll + "" + lll);
			} catch (UniqueLabelException e) {
				System.out.println(e + "" + l + "" + ll + "" + lll);
			}
			i++;
		}
		return g;
	}

}
