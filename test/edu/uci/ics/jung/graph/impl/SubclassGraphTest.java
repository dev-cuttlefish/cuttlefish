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
 * Created on Jun 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.edu.uci.ics.jung.graph.impl;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author danyelf
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SubclassGraphTest extends TestCase {

	private class SubclassVertex extends SparseVertex {
		int i = -10;
		public SubclassVertex(int i) {
			this.i = i;
		}
		public int getVal() {
			return i;
		}
	}

	public void testSubclass() {
		UndirectedGraph g = new UndirectedSparseGraph();
		SubclassVertex[] scv = new SubclassVertex[10];
		for (int i = 0; i < scv.length; i++) {
			scv[i] = new SubclassVertex(i);
			g.addVertex(scv[i]);
			if (i == 0)
				continue;
			GraphUtils.addEdge(g, scv[i], scv[i - 1]);
		}
		Set s = g.getVertices();
		assertTrue(s.size() == scv.length);
		for (Iterator iter = s.iterator(); iter.hasNext();) {
			SubclassVertex v = (SubclassVertex) iter.next();
			assertTrue(v.getVal() >= 0 && v.getVal() < 10);
		}
		Graph g2 = (Graph) g.copy();
		// all the vertices in this are also SubclassVertices
		s = g2.getVertices();
		for (Iterator iter = s.iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			assertTrue(v.getClass() == SubclassVertex.class);
			SubclassVertex scvx = (SubclassVertex) v;
			assertTrue(scvx.getVal() >= 0 && scvx.getVal() < 10);
			SubclassVertex orig = (SubclassVertex) scvx.getEqualVertex(g);
			assertTrue(g.getVertices().contains(orig));
		}
	}

}
