/*
 * Created on Apr 30, 2004
 *
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package test.edu.uci.ics.jung.algorithms.transformations;

import java.util.Collection;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.commons.collections.Predicate;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.KPartiteGraph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.KPartiteSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.predicates.UserDatumVertexPredicate;
import edu.uci.ics.jung.utils.UserData;


/**
 * 
 * @author Joshua O'Madadhain
 */
public class KPartiteTaggerTest extends TestCase
{
    private KPartiteGraph kpg;
    private Graph g;
    private Vertex a1, a2, a3, b1, b2, b3, c1;
    private Predicate a_pred, b_pred;
    private Collection partitions;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        a_pred = new UserDatumVertexPredicate("key", "a");
        b_pred = new UserDatumVertexPredicate("key", "b");
        partitions = new LinkedList();
        partitions.add(a_pred);       
        partitions.add(b_pred);
        
        a1 = new SparseVertex();
        a1.addUserDatum("key", "a", UserData.SHARED);
        a2 = new SparseVertex();
        a2.addUserDatum("key", "a", UserData.SHARED);
        a3 = new SparseVertex();
        a3.addUserDatum("key", "a", UserData.SHARED);
        
        b1 = new SparseVertex();
        b1.addUserDatum("key", "b", UserData.SHARED);
        b2 = new SparseVertex();
        b2.addUserDatum("key", "b", UserData.SHARED);
        b3 = new SparseVertex();
        b3.addUserDatum("key", "b", UserData.SHARED);
        
        c1 = new SparseVertex();
        c1.addUserDatum("key", "c", UserData.SHARED);
        
        g = new SparseGraph();

        g.addVertex(a1);
        g.addVertex(a2);
        g.addVertex(b1);
        g.addVertex(b2);
        Edge a1_b1 = new DirectedSparseEdge(a1, b1);
        Edge b1_a1 = new UndirectedSparseEdge(b1, a1);
        Edge a2_b1 = new DirectedSparseEdge(a2, b1);
        
        g.addEdge(a1_b1);
        g.addEdge(b1_a1);
        g.addEdge(a2_b1);
    }

    public void testGoodGraph()
    {
        kpg = new KPartiteSparseGraph(g, partitions, true);
        assertEquals(kpg.getPartitions().size(), 2);
        assertEquals(kpg.getVertices(), g.getVertices());
        assertEquals(kpg.getEdges(), g.getEdges());
    }
    
    public void testBadVertex()
    {
        g.addVertex(c1);

        try
        {
            kpg = new KPartiteSparseGraph(g, partitions, true);
            fail("did not reject bad vertex");
        }
        catch (IllegalArgumentException iae) { }
    }
    
    public void testBadEdges()
    {
        Edge a1_a1 = new DirectedSparseEdge(a1, a1);
        Edge b1_b2 = new DirectedSparseEdge(b1, b2);
        g.addEdge(a1_a1);
        
        try
        {
            kpg = new KPartiteSparseGraph(g, partitions, true);
            fail("did not reject bad self-loop");
        }
        catch (IllegalArgumentException iae) { }
        
        g.removeEdge(a1_a1);
        g.addEdge(b1_b2);
        
        try
        {
            kpg = new KPartiteSparseGraph(g, partitions, true);
            fail("did not reject bad intra-partition edge");
        }
        catch (IllegalArgumentException iae) { }
    }
}
