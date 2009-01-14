/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.algorithms.flows;

import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.decorators.NumericDecorator;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.MutableInteger;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author Scott White, Joshua O'Madadhain
 */
public class TestEdmondsKarpMaxFlow extends TestCase {
//	private static final String EDGE_WEIGHT_KEY = "EDGE_WEIGHT";

	public static Test suite() {
		return new TestSuite(TestEdmondsKarpMaxFlow.class);
	}

	protected void setUp() {

	}

    public void testSanityChecks() 
    {
        DirectedGraph g = new DirectedSparseGraph();
        Vertex source = g.addVertex(new SimpleDirectedSparseVertex());
        Vertex sink = g.addVertex(new SimpleDirectedSparseVertex());
        
        Vertex v = new SimpleDirectedSparseVertex();
        
        DirectedGraph h = new DirectedSparseGraph();
        Vertex w = g.addVertex(new SimpleDirectedSparseVertex());
        
        String key1 = "key1";
        String key2 = "key2";
        
        EdmondsKarpMaxFlow ek = new EdmondsKarpMaxFlow(g, source, sink, key1, key2);
        
        try
        {
            ek = new EdmondsKarpMaxFlow(g, source, source, key1, key2);
            fail("source and sink vertices not distinct");
        }
        catch (IllegalArgumentException iae) {}

        try
        {
            ek = new EdmondsKarpMaxFlow(h, source, w, key1, key2);
            fail("source and sink vertices not both part of specified graph");
        }
        catch (IllegalArgumentException iae) {}

        try
        {
            ek = new EdmondsKarpMaxFlow(g, source, v, key1, key2);
            fail("source and sink vertices not both part of specified graph");
        }
        catch (IllegalArgumentException iae) {}

    }
    
	public void testSimpleFlow() {
		DirectedSparseGraph graph = new DirectedSparseGraph();
		GraphUtils.addVertices(graph, 6);

		String capacityKey = "Capacity";
		Indexer id = Indexer.getIndexer(graph);

		Edge edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
		edge.setUserDatum(capacityKey, new MutableInteger(16), UserData.SHARED);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(2));
		edge.setUserDatum(capacityKey, new MutableInteger(13), UserData.SHARED);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(1), (Vertex)id.getVertex(2));
		edge.setUserDatum(capacityKey, new MutableInteger(6), UserData.SHARED);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(1), (Vertex)id.getVertex(3));
		edge.setUserDatum(capacityKey, new MutableInteger(12), UserData.SHARED);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(4));
		edge.setUserDatum(capacityKey, new MutableInteger(14), UserData.SHARED);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(2));
		edge.setUserDatum(capacityKey, new MutableInteger(9), UserData.SHARED);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(5));
		edge.setUserDatum(capacityKey, new MutableInteger(20), UserData.SHARED);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(4), (Vertex)id.getVertex(3));
		edge.setUserDatum(capacityKey, new MutableInteger(7), UserData.SHARED);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(4), (Vertex)id.getVertex(5));
		edge.setUserDatum(capacityKey, new MutableInteger(4), UserData.SHARED);

		EdmondsKarpMaxFlow ek =
			new EdmondsKarpMaxFlow(
				graph,
				(Vertex) id.getVertex(0),
				(Vertex) id.getVertex(5),
				capacityKey,
				"FLOW");
		ek.evaluate();

		assertTrue(ek.getMaxFlow() == 23);
        Set nodesInS = ek.getNodesInSourcePartition();
        assertEquals(4,nodesInS.size());
        //System.out.println("Nodes in S: " + nodesInS.size()) ;
        for (Iterator vIt = nodesInS.iterator();vIt.hasNext();) {
            Vertex v = (Vertex) vIt.next();
            Assert.assertTrue(id.getIndex(v) != 3 && id.getIndex(v) != 5);
            //System.out.println(id.getIndex(v));
        }

        Set nodesInT = ek.getNodesInSinkPartition();
        assertEquals(2,nodesInT.size());
        //System.out.println("\nNodes in T: " + nodesInT.size()) ;
        for (Iterator vIt = nodesInT.iterator();vIt.hasNext();) {
            Vertex v = (Vertex) vIt.next();
            Assert.assertTrue(id.getIndex(v) == 3 || id.getIndex(v) == 5);
            //System.out.println(id.getIndex(v));
        }

        Set minCutEdges = ek.getMinCutEdges();
        int maxFlow = 0;
        for (Iterator eIt = minCutEdges.iterator();eIt.hasNext();) {
            DirectedEdge e = (DirectedEdge) eIt.next();
            DirectedEdge flowEdge = (DirectedEdge) e.getEqualEdge(ek.getFlowGraph());
            Number flow = (Number) flowEdge.getUserDatum("FLOW");
            maxFlow += flow.intValue();
        }
        Assert.assertEquals(23,maxFlow);
        Assert.assertEquals(3,minCutEdges.size());
	}

	public void testAnotherSimpleFlow() {
		DirectedSparseGraph graph = new DirectedSparseGraph();
		GraphUtils.addVertices(graph, 6);
		Indexer id = Indexer.getIndexer(graph);

		String capacityKey = "Capacity";
		NumericDecorator decorator =
			new NumericDecorator(capacityKey, UserData.SHARED);
		Edge edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
		decorator.setValue(new MutableInteger(5), edge);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(2));
		decorator.setValue(new MutableInteger(3), edge);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(1), (Vertex)id.getVertex(5));
		decorator.setValue(new MutableInteger(2), edge);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(1), (Vertex)id.getVertex(2));
		decorator.setValue(new MutableInteger(8), edge);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(3));
		decorator.setValue(new MutableInteger(4), edge);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(4));
		decorator.setValue(new MutableInteger(2), edge);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(4));
		decorator.setValue(new MutableInteger(3), edge);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(5));
		decorator.setValue(new MutableInteger(6), edge);
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(4), (Vertex)id.getVertex(5));
		decorator.setValue(new MutableInteger(1), edge);

		EdmondsKarpMaxFlow ek =
			new EdmondsKarpMaxFlow(
				graph,
				(Vertex) id.getVertex(0),
				(Vertex) id.getVertex(5),
				capacityKey,
				"FLOW");
		ek.evaluate();

		assertTrue(ek.getMaxFlow() == 7);

	}

}
