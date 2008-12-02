package test.edu.uci.ics.jung.utils;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.transformation.DirectionTransformer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 */
public class TestGraphUtils extends TestCase {

	public static Test suite() {
		return new TestSuite(TestGraphUtils.class);
	}

	public void testTransformToDirected() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
		buffer.append("<graph edgedefault=\"undirected\">\n");
		buffer.append("<node id=\"1\" label=\"a\"/>\n");
		buffer.append("<node id=\"2\" label=\"b\"/>\n");
		buffer.append("<node id=\"3\" label=\"c\"/>\n");
		buffer.append("<node id=\"4\" label=\"d\"/>\n");
		buffer.append("<node id=\"5\" label=\"e\"/>\n");
		buffer.append("<edge source=\"1\" target=\"2\"/>\n");
		buffer.append("<edge source=\"1\" target=\"3\"/>\n");
		buffer.append("<edge source=\"1\" target=\"4\"/>\n");
		buffer.append("<edge source=\"2\" target=\"3\"/>\n");
		buffer.append("<edge source=\"2\" target=\"5\"/>\n");
		buffer.append("<edge source=\"4\" target=\"5\"/>\n");
		buffer.append("<edge source=\"5\" target=\"5\"/>\n");
		buffer.append("</graph>\n");

		GraphMLFile graphmlFile = new GraphMLFile();
		Graph graph =
			graphmlFile.load(new StringReader(buffer.toString()));
		DirectedGraph dGraph = DirectionTransformer.toDirected(graph);

		Assert.assertEquals(dGraph.numVertices(), 5);
		Assert.assertEquals(dGraph.numEdges(), 13);

	}

	public void testTransformToUndirected() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
		buffer.append("<graph edgedefault=\"directed\">\n");
		buffer.append("<node id=\"1\" label=\"a\"/>\n");
		buffer.append("<node id=\"2\" label=\"b\"/>\n");
		buffer.append("<node id=\"3\" label=\"c\"/>\n");
		buffer.append("<node id=\"4\" label=\"d\"/>\n");
		buffer.append("<node id=\"5\" label=\"e\"/>\n");
		buffer.append("<edge source=\"1\" target=\"2\"/>\n");
		buffer.append("<edge source=\"2\" target=\"1\"/>\n");
		buffer.append("<edge source=\"1\" target=\"3\"/>\n");
		buffer.append("<edge source=\"1\" target=\"4\"/>\n");
		buffer.append("<edge source=\"2\" target=\"3\"/>\n");
		buffer.append("<edge source=\"2\" target=\"5\"/>\n");
		buffer.append("<edge source=\"4\" target=\"5\"/>\n");
		buffer.append("<edge source=\"5\" target=\"4\"/>\n");
		buffer.append("<edge source=\"5\" target=\"5\"/>\n");
		buffer.append("</graph>\n");

		GraphMLFile graphmlFile = new GraphMLFile();
		Graph graph =
			graphmlFile.load(new StringReader(buffer.toString()));
		UndirectedGraph uGraph = DirectionTransformer.toUndirected(graph);

		Assert.assertEquals(uGraph.numVertices(), 5);
		Assert.assertEquals(uGraph.numEdges(), 7);

	}

	public void testCopyLabels() throws UniqueLabelException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
		buffer.append("<graph edgedefault=\"undirected\">\n");
		buffer.append("<node id=\"1\" label=\"a\"/>\n");
		buffer.append("<node id=\"2\" label=\"b\"/>\n");
		buffer.append("<node id=\"3\" label=\"c\"/>\n");
		buffer.append("<node id=\"4\" label=\"d\"/>\n");
		buffer.append("<node id=\"5\" label=\"e\"/>\n");
		buffer.append("<edge source=\"1\" target=\"2\"/>\n");
		buffer.append("<edge source=\"1\" target=\"3\"/>\n");
		buffer.append("<edge source=\"1\" target=\"4\"/>\n");
		buffer.append("<edge source=\"2\" target=\"3\"/>\n");
		buffer.append("<edge source=\"2\" target=\"5\"/>\n");
		buffer.append("<edge source=\"4\" target=\"5\"/>\n");
		buffer.append("<edge source=\"5\" target=\"5\"/>\n");
		buffer.append("</graph>\n");

		GraphMLFile graphmlFile = new GraphMLFile();
		Graph graph =
			graphmlFile.load(new StringReader(buffer.toString()));

		// ok, graph is now a graph
		StringLabeller sl = StringLabeller.getLabeller(graph);
		char c = 0;
		for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			sl.setLabel(v, "" + (char) (c + 'a' ));
			c++;
		}
		Vertex v1 = sl.getVertex("b");
		assertNotNull("Something is seriously wrong", v1);
		
		Vertex vertex_to_remove = sl.getVertex("a");
		
		// copy doesn't replicate labels.
		Graph graph2 = (Graph) graph.copy();
		
		graph2.removeVertex((Vertex) vertex_to_remove.getEqualVertex(graph2));
		assertNull(vertex_to_remove.getEqualVertex(graph2));
		
		StringLabeller sl2 = StringLabeller.getLabeller(graph2);
		assertNull(sl2.getVertex("b"));

		GraphUtils.copyLabels(sl, sl2);
		assertNotNull(sl2.getVertex("b"));
		assertNull(sl2.getVertex("a"));
		Vertex v2 = sl2.getVertex("b");
		assertSame( v2.getGraph(), graph2 );
		assertEquals( v1, v2 );
	}

    public void testTranslateAll()
    {
        Graph g = new SparseGraph();
        Vertex v1 = g.addVertex(new SparseVertex());
        Vertex v2 = g.addVertex(new SparseVertex());
        
        Graph g2 = new SparseGraph();
        Vertex v1_c = (Vertex)v1.copy(g2);
        Vertex v3 = g2.addVertex(new SparseVertex());
        
        Set s_g = new HashSet();
        s_g.add(v1);
        s_g.add(v2);
        
        Set s_g2 = GraphUtils.getEqualVertices(s_g, g2);
        assertTrue(s_g2.contains(v1_c));
        assertFalse(s_g2.contains(v3));
        assertEquals(s_g2.size(), 1);
        
        s_g.remove(v1);
        
        s_g2 = GraphUtils.getEqualVertices(s_g, g2);
        assertTrue(s_g2.isEmpty());
        
        // now try the other direction (g2 -> g)
        s_g2.clear();
        s_g2.add(v1_c);
        s_g2.add(v3);
        s_g = GraphUtils.getEqualVertices(s_g2, g);
        assertTrue(s_g.contains(v1));
        assertFalse(s_g.contains(v2));
        assertEquals(s_g.size(), 1);
        
        s_g2.remove(v1_c);
        s_g = GraphUtils.getEqualVertices(s_g2, g);
        assertTrue(s_g.isEmpty());
    }
    
    public void testTranslateAllEdges()
    {
        Graph g = new SparseGraph();
        Vertex v1 = g.addVertex(new SparseVertex());
        Vertex v2 = g.addVertex(new SparseVertex());
        Edge e1 = g.addEdge(new DirectedSparseEdge(v1, v2));
        Edge e2 = g.addEdge(new DirectedSparseEdge(v2, v1));
        
        Graph g2 = new SparseGraph();
        Vertex v1_c = (Vertex)v1.copy(g2);
        v2.copy(g2);
        Edge e1_c = (Edge)e1.copy(g2);
        Edge e3 = g2.addEdge(new DirectedSparseEdge(v1_c, v1_c));
        
        Set s_g = new HashSet();
        s_g.add(e1);
        s_g.add(e2);
        
        Set s_g2 = GraphUtils.getEqualEdges(s_g, g2);
        assertTrue(s_g2.contains(e1_c));
        assertFalse(s_g2.contains(e3));
        assertEquals(s_g2.size(), 1);
        
        s_g.remove(e1);
        
        s_g2 = GraphUtils.getEqualEdges(s_g, g2);
        assertTrue(s_g2.isEmpty());
        
        // now try the other direction (g2 -> g)
        s_g2.clear();
        s_g2.add(e1_c);
        s_g2.add(e3);
        s_g = GraphUtils.getEqualEdges(s_g2, g);
        assertTrue(s_g.contains(e1));
        assertFalse(s_g.contains(e2));
        assertEquals(s_g.size(), 1);
        
        s_g2.remove(e1_c);
        s_g = GraphUtils.getEqualEdges(s_g2, g);
        assertTrue(s_g.isEmpty());
    }
}
