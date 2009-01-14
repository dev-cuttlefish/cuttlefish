/*
 * Created on Mar 31, 2004
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;

import edu.uci.ics.jung.algorithms.transformation.FoldingTransformer;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.KPartiteGraph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.KPartiteSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.predicates.UserDatumVertexPredicate;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.PredicateUtils;
import edu.uci.ics.jung.utils.UserData;


/**
 * @author Joshua O'Madadhain
 */
public class KPartiteTest extends TestCase
{
    private KPartiteSparseGraph g;
    private Vertex a1, a2, a3, b1, b2, b3, c1;
    private Predicate a_pred, b_pred;
    private Collection partitions;
    
    public void setUp()
    {
        a_pred = new UserDatumVertexPredicate("key", "a");
        b_pred = new UserDatumVertexPredicate("key", "b");
        partitions = new LinkedList();
        partitions.add(a_pred);       
        partitions.add(b_pred);
        
        g = new KPartiteSparseGraph(partitions, true);

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
    }
    
    public void testCopy() {
        KPartiteGraph g_copy = (KPartiteGraph) g.copy();
        assertEquals( g.getVertexConstraints().size(), g_copy.getVertexConstraints().size() );
        assertEquals( g.getEdgeConstraints().size(), g_copy.getEdgeConstraints().size() );
        assertEquals( g.getPartitions(), g_copy.getPartitions());
        assertTrue( GraphUtils.areEquivalent(g, g_copy) );
        
        g.addVertex(a1);
        g.addVertex(a2);
        g.addVertex(b1);
        g.addVertex(b2);
        Edge a1_b1 = new DirectedSparseEdge(a1, b1);
        Edge b1_a1 = new DirectedSparseEdge(b1, a1);
        Edge a2_b1 = new DirectedSparseEdge(a2, b1);
        
        g.addEdge(a1_b1);
        g.addEdge(b1_a1);
        g.addEdge(a2_b1);

        g_copy = (KPartiteGraph) g.copy();
        assertTrue( GraphUtils.areEquivalent(g, g_copy) );
    }
    
    public void testNonMutex() 
    { 
        	a_pred = TruePredicate.INSTANCE;
        	b_pred = TruePredicate.INSTANCE;
        partitions = new LinkedList();
        partitions.add(a_pred);       
        partitions.add(b_pred);
    
        g = new KPartiteSparseGraph(partitions, true);

        try 
        {
            g.addVertex( new SparseVertex() );
            fail("Mutex not enforced!");
        } 
        catch( IllegalArgumentException iae ) { }
    	
    }
    
    public void testAddVertex()
    {
        g.addVertex(a1);
        g.addVertex(a2);
        g.addVertex(b1);
        g.addVertex(b2);
        try
        {
            g.addVertex(c1);
            fail("should not be able to add vertex which does not pass predicates");
        }
        catch (IllegalArgumentException iae) {}
        
        Set a_set = new HashSet();
        a_set.add(a1);
        a_set.add(a2);
        assertTrue(a_set.equals(PredicateUtils.getVertices(g, a_pred)));
        
        Set b_set = new HashSet();
        b_set.add(b1);
        b_set.add(b2);
        assertTrue(b_set.equals(PredicateUtils.getVertices(g, b_pred)));
    }
    
    public void testAddEdge()
    {
        g.addVertex(a1);
        g.addVertex(a2);
        g.addVertex(b1);
        g.addVertex(b2);
        Edge a1_b1 = new DirectedSparseEdge(a1, b1);
        Edge b1_a1 = new DirectedSparseEdge(b1, a1);
        Edge a1_a1 = new DirectedSparseEdge(a1, a1);
        Edge a2_b1 = new DirectedSparseEdge(a2, b1);
        Edge b1_b2 = new DirectedSparseEdge(b1, b2);
        
        g.addEdge(a1_b1);
        g.addEdge(b1_a1);
        g.addEdge(a2_b1);
        
        try
        {
            g.addEdge(a1_a1);
            fail("should not be able to add edge whose endpoints are in the same partition");
        }
        catch (IllegalArgumentException iae) {}
        
        try
        {
            g.addEdge(b1_b2);
            fail("should not be able to add edge whose endpoints are in the same partition");
        }
        catch (IllegalArgumentException iae) {}
    }
    
    public void testFoldUndirectedNoParallel()
    {
        g.getEdgeConstraints().add(Graph.UNDIRECTED_EDGE);
        
        g.addVertex(a1);
        g.addVertex(a2);
        g.addVertex(a3);
        Vertex[] vA = new Vertex[] {a1, a2, a3};
        g.addVertex(b1);
        g.addVertex(b2);
        g.addVertex(b3);
        
        // A0 is connected to all three Bs
        g.addEdge(new UndirectedSparseEdge(a1, b1));
        g.addEdge(new UndirectedSparseEdge(a1, b2));
        g.addEdge(new UndirectedSparseEdge(a1, b3));
        // A1 is connected to B0 and B1
        g.addEdge(new UndirectedSparseEdge(a2, b1));
        g.addEdge(new UndirectedSparseEdge(a2, b2));
        // A2 isn't connected at all
        FoldingTransformer kpf = new FoldingTransformer( false );
        Graph newGraph = kpf.fold(g, a_pred);
        assertTrue(newGraph.getVertices().size() == 3);
        Vertex[] vG = new Vertex[3];
        for (int i = 0; i < 3; i++) {
            vG[i] = (Vertex) vA[i].getEqualVertex(newGraph);
            assertNotNull(vG[i]);
        }
        assertTrue( vG[0].isNeighborOf(vG[1]));
        assertEquals(1, vG[0].degree());
        assertEquals(1, vG[1].degree());
        assertEquals(0, vG[2].degree());
        assertTrue(vG[0].getNeighbors().contains(vG[1]));
        assertTrue(vG[1].getNeighbors().contains(vG[0]));
        assertFalse(vG[1].getNeighbors().contains(vG[1]));
        Edge e = vG[0].findEdge(vG[1]);
        Collection tags =
            (Collection) e.getUserDatum(FoldingTransformer.FOLDED_DATA);
        assertEquals(2, tags.size());
        assertTrue(tags.contains(b1));
        assertTrue(tags.contains(b2));
        
    }
    
    public void testFoldDirectedParallel()
    {
        g.getEdgeConstraints().add(Graph.DIRECTED_EDGE);
        
        g.addVertex(a1);
        g.addVertex(a2);
        g.addVertex(a3);
        Vertex[] vA = new Vertex[] {a1, a2, a3};
        g.addVertex(b1);
        g.addVertex(b2);
        g.addVertex(b3);
        
        g.addEdge(new DirectedSparseEdge(a1, b2));

        g.addEdge(new DirectedSparseEdge(a2, b1));
        g.addEdge(new DirectedSparseEdge(a2, b2));
        g.addEdge(new DirectedSparseEdge(a2, b3));

        g.addEdge(new DirectedSparseEdge(a3, b3));
        
        g.addEdge(new DirectedSparseEdge(b1, a1));
        g.addEdge(new DirectedSparseEdge(b1, a2));

        g.addEdge(new DirectedSparseEdge(b2, a2));

        FoldingTransformer kpf = new FoldingTransformer( true );
        Graph newGraph = kpf.fold(g, a_pred);
        assertTrue(newGraph.getVertices().size() == 3);
        Vertex[] vG = new Vertex[3];
        for (int i = 0; i < 3; i++) {
            vG[i] = (Vertex) vA[i].getEqualVertex(newGraph);
            assertNotNull(vG[i]);
        }
        assertTrue(vG[0].degree() == 2);
        assertTrue(vG[1].degree() == 4);
        assertTrue(vG[2].degree() == 0);
        assertTrue(vG[0].inDegree() == 1);
        assertTrue(vG[1].inDegree() == 3);
        assertTrue(vG[0].outDegree() == 1);
        assertTrue(vG[1].outDegree() == 3);
        assertTrue(vG[0].getSuccessors().contains(vG[1]));
        assertTrue(vG[1].getSuccessors().contains(vG[0]));
        assertTrue(vG[1].getSuccessors().contains(vG[1]));

        Edge e = vG[1].findEdge(vG[0]);
        Vertex tag = (Vertex)e.getUserDatum(FoldingTransformer.FOLDED_DATA);
        assertSame(tag, b1);
        
        e = vG[0].findEdge(vG[1]);
        tag = (Vertex)e.getUserDatum(FoldingTransformer.FOLDED_DATA);
        assertSame(tag, b2);
        
        Set s = vG[1].findEdgeSet(vG[1]);
        assertTrue(s.size() == 2);
        Iterator iter = s.iterator();
        e = (Edge)iter.next();
        tag = (Vertex)e.getUserDatum(FoldingTransformer.FOLDED_DATA);
        e = (Edge)iter.next();
        Vertex tag2 = (Vertex)e.getUserDatum(FoldingTransformer.FOLDED_DATA);
        assertTrue((tag == b2 && tag2 == b1) || (tag == b1 && tag2 == b2));
    }

}
