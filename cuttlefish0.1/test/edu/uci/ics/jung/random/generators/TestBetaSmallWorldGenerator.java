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

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.random.generators.WattsBetaSmallWorldGenerator;

/**
 * @author Scott White
 */
public class TestBetaSmallWorldGenerator extends TestCase {
	public static Test suite() {
		return new TestSuite(TestBetaSmallWorldGenerator.class);
	}

	protected void setUp() {
	}

	public void testDimensions() {

		for (int i = 1; i <= 10; i++) {
			WattsBetaSmallWorldGenerator generator = new WattsBetaSmallWorldGenerator(i*20,Math.random(),4);

			Graph graph = (Graph) generator.generateGraph();
			Assert.assertTrue(graph.numVertices() == i*20);
			Assert.assertTrue(graph.numEdges() == i*20*2);
		}
	}

    public void testLatticeStructure() {
        for (int i = 50; i <= 100; i++) {
			WattsBetaSmallWorldGenerator generator = new WattsBetaSmallWorldGenerator(i,0,4);

			Graph graph = (Graph) generator.generateGraph();
			Assert.assertTrue(graph.numVertices() == i);
			Assert.assertTrue(graph.numEdges() == i*2);
            for (Iterator vIt = graph.getVertices().iterator();	vIt.hasNext();) {
				Vertex v = (Vertex) vIt.next();
				Assert.assertTrue(v.degree() == 4);
			}
		}

    }
}
