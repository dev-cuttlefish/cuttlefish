package test.edu.uci.ics.jung.random.generators;

/**
 * @author W. Giordano, Scott White
 */

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.random.generators.SimpleRandomGenerator;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.Assert;

import java.util.Random;


public class TestSimpleRandom extends TestCase {
	public static Test suite() {
		return new TestSuite(TestErdosRenyi.class);
	}

	protected void setUp() {
	}

	public void test() {
        Random rand = new Random();

		for (int i = 1; i <= 10; i++) {
            int numVertices = i*20;
            int numPossibleVertices = numVertices*(numVertices-1)/2;
            int numEdges = rand.nextInt(numPossibleVertices);
			SimpleRandomGenerator generator = new SimpleRandomGenerator(numVertices,numEdges);

			Graph graph = (Graph) generator.generateGraph();
			Assert.assertTrue(graph.numVertices() == numVertices);
			Assert.assertTrue(graph.numEdges() == numEdges);
		}
	}


}
