package test.edu.uci.ics.jung.random.generators;

/**
 * @author W. Giordano, Scott White
 */

import junit.framework.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.random.generators.ErdosRenyiGenerator;


public class TestErdosRenyi extends TestCase {
	public static Test suite() {
		return new TestSuite(TestErdosRenyi.class);
	}

	protected void setUp() {
	}

	public void test() {

        int numVertices = 100;
        int total = 0;
		for (int i = 1; i <= 10; i++) {
			ErdosRenyiGenerator generator = new ErdosRenyiGenerator(numVertices,0.1);
            generator.setSeed(0);

			Graph graph = (Graph) generator.generateGraph();
			Assert.assertTrue(graph.numVertices() == numVertices);
            total += graph.numEdges();
//            System.out.println(graph.numEdges());
		}
        total /= 10.0;
        Assert.assertTrue(total > 495-50 && total < 495+50);

	}
	  
  
}
