package test.edu.uci.ics.jung.algorithms.cluster;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.cluster.ExactFlowCommunity;
import edu.uci.ics.jung.algorithms.transformation.DirectionTransformer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 */
public class TestExactFlowCommunity extends TestCase {
    Graph mGraph;
    public static Test suite() {
        return new TestSuite(TestExactFlowCommunity.class);
    }

    protected void setUp() {

    }

    public void test() {
        UndirectedSparseGraph graph = new UndirectedSparseGraph();
        GraphUtils.addVertices( graph, 10 );
		Indexer id = Indexer.getIndexer( graph );

        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(4),(Vertex)id.getVertex(5));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(4),(Vertex)id.getVertex(6));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(5),(Vertex)id.getVertex(6));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(7),(Vertex)id.getVertex(9));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(7),(Vertex)id.getVertex(6));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(6),(Vertex)id.getVertex(9));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(5));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(7));

        Assert.assertEquals(graph.numVertices(),10);
        Assert.assertEquals(graph.numEdges(),12);

        DirectedGraph dGraph = DirectionTransformer.toDirected(graph);
        Assert.assertEquals(dGraph.numVertices(),10);
        Assert.assertEquals(dGraph.numEdges(),24);

//        id = Indexer.getIndexer(dGraph);

        ExactFlowCommunity efc = new ExactFlowCommunity(2);
        Set rootSet = new HashSet();
        rootSet.add(id.getVertex(1).getEqualVertex(dGraph));
        rootSet.add(id.getVertex(2).getEqualVertex(dGraph));

        Set communityMembers = efc.extract(dGraph,rootSet);
        assertEquals(3,communityMembers.size());
        for (Iterator vIt = communityMembers.iterator(); vIt.hasNext();) {
            Vertex v = (Vertex) vIt.next();
            assertTrue(id.getIndex(v) == 0 || id.getIndex(v) == 1 || id.getIndex(v) == 2);
            //System.out.println(id.getIndex(v));
        }

        rootSet = new HashSet();
        rootSet.add(id.getVertex(5));
        rootSet.add(id.getVertex(7));

        communityMembers = efc.extract(dGraph,rootSet);
        assertEquals(6,communityMembers.size());
    }

    public void test2() {
        DirectedSparseGraph graph = new DirectedSparseGraph();
        GraphUtils.addVertices( graph, 10 );
		Indexer id = Indexer.getIndexer( graph );

        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(2));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(4),(Vertex)id.getVertex(5));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(4),(Vertex)id.getVertex(6));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(5),(Vertex)id.getVertex(6));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(7),(Vertex)id.getVertex(9));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(7),(Vertex)id.getVertex(6));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(6),(Vertex)id.getVertex(9));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(3));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(5));
        GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(7));

        Assert.assertEquals(graph.numVertices(),10);
        Assert.assertEquals(graph.numEdges(),12);

        ExactFlowCommunity efc = new ExactFlowCommunity(2);
        Set rootSet = new HashSet();

        rootSet.add(id.getVertex(1));
        rootSet.add(id.getVertex(2));

        Set communityMembers = efc.extract(graph,rootSet);
        assertEquals(3,communityMembers.size());
        for (Iterator vIt = communityMembers.iterator(); vIt.hasNext();) {
            Vertex v = (Vertex) vIt.next();
            assertTrue(id.getIndex(v) == 0 || id.getIndex(v) == 1 || id.getIndex(v) == 2);
            //System.out.println(id.getIndex(v));
        }

        rootSet = new HashSet();
        rootSet.add(id.getVertex(5));
        rootSet.add(id.getVertex(7));

        communityMembers = efc.extract(graph,rootSet);
        assertEquals(6,communityMembers.size());
    }

}
