package test.edu.uci.ics.jung.random.generators;

/**
 * @author W. Giordano, Scott White
 */

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.random.generators.BarabasiAlbertGenerator;


public class TestBarabasiAlbert extends TestCase {
	public static Test suite() {
		return new TestSuite(TestBarabasiAlbert.class);
	}

	protected void setUp() {
	}

	public void test() 
    {
        int init_vertices = 1;
        int edges_to_add_per_timestep = 1;
        int random_seed = 0;
        int num_tests = 10;
        int num_timesteps = 10;
        
	    BarabasiAlbertGenerator generator = 
            new BarabasiAlbertGenerator(init_vertices,edges_to_add_per_timestep,random_seed);
	    for (int i = 1; i <= num_tests; i++) {
	        
	        generator.evolveGraph(num_timesteps);
	        Graph graph = (Graph) generator.generateGraph();
	        assertEquals(graph.numVertices(), (i*num_timesteps) + init_vertices);
	        assertEquals(graph.numEdges(), edges_to_add_per_timestep * (i*num_timesteps));
	    }
	    
	}


}
