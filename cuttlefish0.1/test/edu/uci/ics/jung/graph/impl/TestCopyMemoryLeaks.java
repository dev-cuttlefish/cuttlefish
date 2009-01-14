/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Jun 13, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.edu.uci.ics.jung.graph.impl;

import java.lang.ref.WeakReference;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.ArchetypeGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.GlobalStringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.filters.TrivialFilter;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author danyelf
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestCopyMemoryLeaks extends TestCase {

	static boolean debug;

    static class CountedGraph extends SparseGraph {

        public static int count = 0;
        public static int id_counter = 0;
        public int id;
        
        CountedGraph() 
        {
            count++;
            id = id_counter++;
			if (debug)
				System.out.println(">>graph " + this + ": " + id);
        }

        public ArchetypeGraph newInstance() 
        {
            count++;
            return super.newInstance();
        }

        public void finalize() throws Throwable 
        {
            if (debug)
                System.out.println("<<graph " + this + ": " + id);
            count--;
            try 
            {
                super.finalize();
            } 
            catch (Throwable t) {
                t.printStackTrace();
            } 
            finally 
            { }
        }
    }

    // trying to untangle some of the mess that happens with graph.copy and
    // labellers
    public synchronized void testMemoryCopy() throws Exception {
		debug = false;
        ForceGC.gc();
        int count = CountedGraph.count;
        Graph graph = new CountedGraph();
		GraphUtils.addVertices(graph, 1);
        assertEquals(count + 1, CountedGraph.count);

        Object o = graph.copy();
        assertEquals(count + 2, CountedGraph.count);
        graph = null;
        ForceGC.gc();
        assertEquals("The copy is holding a ref to the graph: ", count + 1,
                CountedGraph.count);
    }

	public synchronized void testMemoryVertexUserData() throws Exception {
		debug = false;
		ForceGC.gc();
		int count = CountedGraph.count;
		Graph graph = new CountedGraph();
		graph.addUserDatum("VTestKey", "VTestValue", UserData.REMOVE);
		assertEquals(count + 1, CountedGraph.count);

		graph = null;
		ForceGC.gc();
		assertEquals("The user data is holding a ref to the graph: ", count,
				CountedGraph.count);
	}


	public synchronized void testMemoryCopyWithGraphUserData()
			throws Exception {
		debug = false;
		ForceGC.gc();
		int count = CountedGraph.count;
		Graph graph = new CountedGraph();
		graph.addUserDatum("TestKey", "TestValue", UserData.REMOVE);
		assertEquals(count + 1, CountedGraph.count);

		Object o = graph.copy();
		assertEquals(count + 2, CountedGraph.count);
		graph = null;
		ForceGC.gc();
		assertEquals("The copy is holding a ref to the graph: ", count + 1,
				CountedGraph.count);
	}

    /**
     * Confirms that a stringlabeller is removed from the graph, so that it
     * doesn't hold a hook.
     */
	public synchronized void testMemoryStringLabel() throws Exception {
		debug = false;
        ForceGC.gc();
        int count = CountedGraph.count;
        Graph graph = new CountedGraph();
		Vertex v = graph.addVertex(new SparseVertex());
        StringLabeller sl = StringLabeller.getLabeller(graph);
		sl.setLabel(v, "truth");
        assertEquals(count + 1, CountedGraph.count);

        Object o = graph.copy();
        graph.removeAllVertices();
        assertEquals(count + 2, CountedGraph.count);
        graph = null;
        sl = null;
		v = null;
        ForceGC.gc();
        assertEquals("The string labeller is holding a ref to the graph: ",
                count + 1, CountedGraph.count);
    }

    public synchronized void testMemoryMutualReference()
        throws Exception
    {
        ForceGC.gc();
        int count = CountedGraph.count;
        Graph graph = new CountedGraph();
        DummyClass dc = new DummyClass(graph);
        graph.addUserDatum("dummy!", dc, UserData.REMOVE);
        assertEquals(count + 1, CountedGraph.count);

        graph = null;
        dc = null;
        ForceGC.gc();
        assertEquals("The dummy class is holding a ref to the graph: ",
                count, CountedGraph.count);
    }
    
    public static class DummyClass
    {
        protected WeakReference container;
        
        public DummyClass(Object container)
        {
            this.container = new WeakReference(container);
        }
    }
    
    /**
     * Confirms that a stringlabeller is removed from the graph, so that it
     * doesn't hold a hook.
     */
	public synchronized void testMemoryGlobalStringLabel() throws Exception {
		debug = false;
        ForceGC.gc();
        int count = CountedGraph.count;
        Graph graph = new CountedGraph();
        Vertex v = graph.addVertex(new SparseVertex());
        StringLabeller sl = GlobalStringLabeller.getLabeller(graph);
        sl.setLabel(v, "truth");
        assertEquals(count + 1, CountedGraph.count);

        Object o = graph.copy();
        assertEquals(count + 2, CountedGraph.count);
        graph = null;
        sl = null;
        v = null;
        ForceGC.gc();
        assertEquals("The string labeller is holding a ref to the graph: ",
                count + 1, CountedGraph.count);
    }

    /**
     * Confirms that a filtered graph doesn't have a ref to the original ...
     * unless we want it to
     *  
     */
    public synchronized void testMemoryFiltered() throws Exception {
        ForceGC.gc();
        int count = CountedGraph.count;
        Graph graph = new CountedGraph();
        Filter nullFilter = TrivialFilter.getInstance();
        assertEquals(count + 1, CountedGraph.count);

        Object o = nullFilter.filter(graph).assemble();
        assertEquals(count + 2, CountedGraph.count);
        graph = null;
        ForceGC.gc();
        assertEquals("The filter is holding a ref to the graph: ", count + 1,
                CountedGraph.count);
    }

	public synchronized void testMemoryFilteredKeep() throws Exception {
		debug = false;
        ForceGC.gc();
        int count = CountedGraph.count;
        Graph graph = new CountedGraph();
        Filter nullFilter = TrivialFilter.getInstance();
        assertEquals(count + 1, CountedGraph.count);

        Object o = nullFilter.filter(graph).assemble(true);
        assertEquals(count + 2, CountedGraph.count);
        graph = null;
        ForceGC.gc();
        assertEquals("The filter should hold a ref to the graph: ", count + 2,
                CountedGraph.count);
    }

    public synchronized void testMemoryFilteredCopy()
            throws Exception {
        ForceGC.gc();
        int count = CountedGraph.count;
        Graph graph = new CountedGraph();
        Filter nullFilter = TrivialFilter.getInstance();
        assertEquals(count + 1, CountedGraph.count);

        Graph filtered = nullFilter.filter(graph).assemble(true);
        Graph g2 = (Graph) filtered.copy();
        assertEquals(count + 3, CountedGraph.count);
        graph = null;
        filtered = null;
        ForceGC.gc();
        assertEquals(
                "The copy of the filter oughtn't hold a ref to the graph: ",
                count + 1, CountedGraph.count);
    }

}