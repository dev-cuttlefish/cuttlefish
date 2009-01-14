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
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author danyelf
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestLabeller extends TestCase {

	Graph graph;
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

	// test triggered by Jie Ren, who points out
	// that if you remove a vertex from a graph, you
	// still can't access its label
	public void testStringLabeller() throws Exception {
		Vertex v3 = null;
		int count = 0;
		for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			count++;
			if (count == 3)
				v1 = v;
			if ( count == 4 )
				v2 = v;
			if (count == 7)
				v3 = v;
		}
		assertNotNull( v1 );
		assertNotNull( v2 );
		assertNotNull( v3 );
		StringLabeller sl = StringLabeller.getLabeller(graph);
		sl.setLabel(v1, "truth");
		assertEquals( sl.getLabel(v1), "truth");
		assertSame( sl.getVertex("truth"), v1);
		// the joshua fix
//		sl.setLabel(v1, null);
		graph.removeVertex(v1);
		
		sl.removeLabel("truth");

		sl.setLabel(v2, "truth");
		assertEquals( sl.getLabel(v2), "truth");
		assertSame( sl.getVertex("truth"), v2);
		// the joshua fix
//		sl.setLabel(v2, null);

		sl.removeLabel("truth");

		try {
			sl.setLabel(v3, "truth");		
		} catch (UniqueLabelException ule ) {
			fail("no vertex with this label!");
		}
	}

	public void testClear() throws UniqueLabelException {
		StringLabeller sl = StringLabeller.getLabeller(graph);

		int count = 0;
		for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			sl.setLabel( v, "" + ('a' + count) );
			count++;
		}	
		sl.clear();
		for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
				String label = sl.getLabel( v );
				assertNull( label );
		}

	}
	
	// Confirms that the labeller reads and writes from each vertex
	public void testLabeller() throws UniqueLabelException {
		StringLabeller sl = StringLabeller.getLabeller(graph);

		assertSame( sl.getGraph() , graph ); 
		int count = 0;
		for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			sl.setLabel( v, "" + ('a' + count) );
			assertEquals(sl.getLabel(v), ""+('a'+count));
			count++;
		}	
	}

	
	public void testLabellerFail() throws UniqueLabelException {
		StringLabeller sl = StringLabeller.getLabeller(graph);
		int count = 0;
		try {
		for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			sl.setLabel( v, "" + 'a' );
			count++;
		}	
		} catch( StringLabeller.UniqueLabelException e ) {
			return;
		}
		fail( "Didn't throw an exception!");
	}

	public void testLabellerFail2() throws UniqueLabelException {
		StringLabeller sl = StringLabeller.getLabeller(graph);
		String test = "Test1";
		sl.setLabel( v1, test );
		assertEquals( sl.getLabel( v1 ) , test);
		sl.setLabel( v1, "Test2 ");
		assertNotSame( sl.getLabel( v1 ) , test);
		sl.setLabel( v1, test );
		assertEquals( sl.getLabel( v1 ) , test);			
	}

	
}
