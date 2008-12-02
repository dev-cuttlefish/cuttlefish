/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.algorithms.cluster;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.collections.CollectionUtils;

import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 */
public abstract class TestWeakComponentClusterer extends TestCase {
    public static Test suite() {
    	TestSuite t = new TestSuite(TestWeakComponentClusterer.class.getName());
    	t.addTest( new TestSuite( TestDirected.class ));
    	t.addTest( new TestSuite( TestUndirected.class ));
    	return t;
    }
    
    public static class TestDirected extends TestWeakComponentClusterer {

		public Graph getGraph() {
			return new DirectedSparseGraph();
		}
    	
    }

	public static class TestUndirected extends TestWeakComponentClusterer {

		public Graph getGraph() {
			return new UndirectedSparseGraph();
		}
    	
	}


    protected void setUp() {

    }

	public abstract Graph getGraph();

    public void testExtractOneComponent() {
        Graph graph = getGraph();
		GraphUtils.addVertices( graph, 4 );
//		  graph.addVertices(4);
		Indexer id = Indexer.getIndexer( graph );
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(2));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(2));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(0));

        WeakComponentClusterer wcSearch = new WeakComponentClusterer();
        ClusterSet componentList = wcSearch.extract(graph);
        Assert.assertEquals(componentList.size(),1);
        // specifically, 0, 1, 2, 3 should all be in the component
		Set c = componentList.getCluster(0);
		assertEquals( c, graph.getVertices() );
    }


	public void testComponentHasIsolate() {
		Graph graph = getGraph();
		GraphUtils.addVertices( graph, 5 );
		Indexer id = Indexer.getIndexer( graph );
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(2));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(3));

		WeakComponentClusterer wcSearch = new WeakComponentClusterer();
		ClusterSet componentList = wcSearch.extract(graph);
		Assert.assertEquals(componentList.size(),2);
		Set z = componentList.getCluster( 0 );
		Set z1 = componentList.getCluster( 1 );
		assertFalse( CollectionUtils.containsAny( z , z1 ));
		assertEquals( graph.getVertices(), new HashSet( CollectionUtils.union(z, z1)));		
	}

	public void testComponentAllIsolates() {
		Graph graph = getGraph();
		GraphUtils.addVertices( graph, 2 );

		WeakComponentClusterer wcSearch = new WeakComponentClusterer();
		ClusterSet componentList = wcSearch.extract(graph);
		Assert.assertEquals(componentList.size(),2);
		Set z = componentList.getCluster( 0 );
		Set z1 = componentList.getCluster( 1 );
		assertFalse( CollectionUtils.containsAny( z , z1 ));
		assertEquals( graph.getVertices(), new HashSet( CollectionUtils.union(z, z1)));		
	}

    public void testExtractTwoComponents() {
		Graph graph = getGraph();
		GraphUtils.addVertices( graph, 5 );
		Indexer id = Indexer.getIndexer( graph );
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(2));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(4));

        WeakComponentClusterer wcSearch = new WeakComponentClusterer();
        ClusterSet componentList = wcSearch.extract(graph);
        Assert.assertEquals(componentList.size(),2);
        Set z = componentList.getCluster( 0 );
        Set z1 = componentList.getCluster( 1 );
        assertFalse( CollectionUtils.containsAny( z , z1 ));
        assertEquals( graph.getVertices(), new HashSet( CollectionUtils.union(z, z1)));
    }
}
