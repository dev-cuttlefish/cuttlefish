/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.random.generators;

import java.util.Iterator;

import junit.framework.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.random.generators.KleinbergSmallWorldGenerator;

/**
 * @author Scott White
 */
public class TestKleinbergSmallWorldGenerator extends TestCase {
	public static Test suite() {
		return new TestSuite(TestKleinbergSmallWorldGenerator.class);
	}

	protected void setUp() {
	}

	public void testSimpleConditions() {

		for (int i = 0; i < 10; i++) {

			KleinbergSmallWorldGenerator generator =
				new KleinbergSmallWorldGenerator(5, 2.0);

			Graph graph = (Graph) generator.generateGraph();
			Assert.assertTrue(graph.numVertices() >= 25.0);
			Assert.assertTrue(graph.numEdges() >= 100.0);
//			boolean hasOtherEdges = false;
			for (Iterator vIt = graph.getVertices().iterator();
				vIt.hasNext();
				) {
				Vertex v = (Vertex) vIt.next();
				Assert.assertTrue(v.outDegree() >= 4);
                Assert.assertTrue(v.outDegree() <= 5);
			}
		}

	}
}
