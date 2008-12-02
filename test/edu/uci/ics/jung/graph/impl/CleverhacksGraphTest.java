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
 * Created on Jul 10, 2003
 *
 */
package test.edu.uci.ics.jung.graph.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.utils.TestGraphs;

/**
 * @author danyelf
 *
 */
public class CleverhacksGraphTest extends TestCase {

	public final void testEquals() {
		Graph graph1 = TestGraphs.createTestGraph( false );	
		Graph graph2 = (Graph) graph1.copy();
		Set s1 = new HashSet( graph1.getVertices() );
		for (Iterator iter = graph2.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex ) iter.next();
			assertTrue( s1.contains( v ));
			s1.remove( v );			
		}
		assertEquals( s1.size(), 0 );		
	}

	public final void testEqualsRemoval() {
		Graph graph1 = TestGraphs.createTestGraph( false );	
		Graph graph2 = (Graph) graph1.copy();
		Indexer il = Indexer.getIndexer( graph2 );
		//remove vertices 1-5
		for(int i =0 ; i < 5; i++ ) {
			graph2.removeVertex( (Vertex) il.getVertex( i ));
		}
		il.updateIndex();
		assertEquals( graph1.numVertices(), graph2.numVertices() + 5 );
		Set s = new HashSet( graph1.getVertices());
		s.removeAll( graph2.getVertices() );
		// s should have five elements, all of which are in S
		assertEquals( s.size() , 5 );

		s = new HashSet( graph1.getVertices());
		s.retainAll( graph2.getVertices() );
		assertEquals( s.size(), graph1.numVertices() - 5 );
	}

}
