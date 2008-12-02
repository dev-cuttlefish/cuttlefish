/*
 * Created on Jan 8, 2004
 *
 */
package test.edu.uci.ics.jung.random.generators;

import java.util.Iterator;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.random.generators.Lattice1DGenerator;

/**
 * @author Scott
 *
 */
public class TestLattice1DGenerator extends TestCase {
public static Test suite()
{
	return new TestSuite(TestLattice1DGenerator.class);
}

protected void setUp()
{
}

public void test()
{

	Lattice1DGenerator generator =
			new Lattice1DGenerator(5, true);

		Graph graph = (Graph) generator.generateGraph();
		Assert.assertTrue(graph.numVertices() == 5.0);
		Assert.assertTrue(graph.numEdges() == 5.0);
		//		boolean hasOtherEdges = false;
		for (Iterator vIt = graph.getVertices().iterator(); vIt.hasNext();)
		{
			Vertex v = (Vertex) vIt.next();
			Assert.assertTrue(v.outDegree() == 2);
		}
}
	

}
