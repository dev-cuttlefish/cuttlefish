/*
 * Created on Apr 4, 2004
 *
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package test.edu.uci.ics.jung.graph.impl;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SimpleSparseVertex;
import edu.uci.ics.jung.graph.impl.SimpleUndirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseVertex;


/**
 * 
 * @author Joshua O'Madadhain
 */
public class VertexTypeTest extends TestCase
{
    Graph g;
    Vertex v1, v2, v3, v4;
    
    public void setUp()
    {
        g = new SparseGraph();
    }
    
    public void testSparseVertex()
    {
        v1 = new SparseVertex();
        v2 = new SparseVertex();
        v3 = new SparseVertex();
        v4 = new SparseVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        
        Edge e1_1 = new UndirectedSparseEdge(v1, v1);
        Edge e1_2 = new DirectedSparseEdge(v1, v2);
        Edge e2_1 = new DirectedSparseEdge(v2, v1);
        Edge e1_1d = new DirectedSparseEdge(v1, v1);
        Edge e1_3_1 = new DirectedSparseEdge(v1, v3);
        Edge e1_3_2 = new DirectedSparseEdge(v1, v3);
        Edge e1_4 = new UndirectedSparseEdge(v1, v4);
        
        g.addEdge(e1_2);
        g.addEdge(e2_1);
        g.addEdge(e1_1);
        g.addEdge(e1_1d);
        g.addEdge(e1_3_1);
        g.addEdge(e1_3_2);
        g.addEdge(e1_4);
        
        assertTrue(v1.isDest(e1_1));
        assertTrue(v1.isSource(e1_1));
        assertFalse(v1.isDest(e1_2));
        assertTrue(v1.isSource(e1_2));
        assertTrue(v1.isDest(e2_1));
        assertFalse(v1.isSource(e2_1));
        assertTrue(v1.isDest(e1_1d));
        assertTrue(v1.isSource(e1_1d));
        assertFalse(v1.isDest(e1_3_1));
        assertTrue(v1.isSource(e1_3_1));
        assertFalse(v1.isDest(e1_3_2));
        assertTrue(v1.isSource(e1_3_2));
        assertTrue(v1.isDest(e1_4));
        assertTrue(v1.isSource(e1_4));

    }
    
    public void testSimpleSparseVertex()
    {
        v1 = new SimpleSparseVertex();
        v2 = new SimpleSparseVertex();
        v3 = new SimpleSparseVertex();
        v4 = new SimpleSparseVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        
        g.addEdge(new DirectedSparseEdge(v2, v1));
        g.addEdge(new UndirectedSparseEdge(v1, v1));
        g.addEdge(new DirectedSparseEdge(v1, v3));
        g.addEdge(new UndirectedSparseEdge(v1, v4));
    }
    
    public void testDirectedSparseVertexExp()
    {
        v1 = new DirectedSparseVertex();
        v2 = new DirectedSparseVertex();
        v3 = new DirectedSparseVertex();
        v4 = new DirectedSparseVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        
        g.addEdge(new DirectedSparseEdge(v1, v2));
        g.addEdge(new DirectedSparseEdge(v2, v1));
        g.addEdge(new DirectedSparseEdge(v1, v1));
        g.addEdge(new DirectedSparseEdge(v1, v3));
        g.addEdge(new DirectedSparseEdge(v1, v3));
        g.addEdge(new DirectedSparseEdge(v1, v4));
    }
    
    public void testSimpleDirectedSparseVertex()
    {
        v1 = new SimpleDirectedSparseVertex();
        v2 = new SimpleDirectedSparseVertex();
        v3 = new SimpleDirectedSparseVertex();
        v4 = new SimpleDirectedSparseVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        
        g.addEdge(new DirectedSparseEdge(v2, v1));
        g.addEdge(new DirectedSparseEdge(v1, v1));
        g.addEdge(new DirectedSparseEdge(v1, v3));
        g.addEdge(new DirectedSparseEdge(v1, v4));
    }
    
    public void testUndirectedSparseVertexExp()
    {
        v1 = new UndirectedSparseVertex();
        v2 = new UndirectedSparseVertex();
        v3 = new UndirectedSparseVertex();
        v4 = new UndirectedSparseVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        
        g.addEdge(new UndirectedSparseEdge(v1, v2));
        g.addEdge(new UndirectedSparseEdge(v1, v1));
        g.addEdge(new UndirectedSparseEdge(v1, v3));
        g.addEdge(new UndirectedSparseEdge(v3, v1));
        g.addEdge(new UndirectedSparseEdge(v4, v1));
    }
    
    public void testSimpleUndirectedSparseVertex()
    {
        v1 = new SimpleUndirectedSparseVertex();
        v2 = new SimpleUndirectedSparseVertex();
        v3 = new SimpleUndirectedSparseVertex();
        v4 = new SimpleUndirectedSparseVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        
        g.addEdge(new UndirectedSparseEdge(v2, v1));
        g.addEdge(new UndirectedSparseEdge(v1, v1));
        g.addEdge(new UndirectedSparseEdge(v1, v3));
        g.addEdge(new UndirectedSparseEdge(v1, v4));
    }
    
}
