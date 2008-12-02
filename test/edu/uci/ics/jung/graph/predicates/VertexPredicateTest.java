/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
* 
* Created on Mar 3, 2004
*/
package test.edu.uci.ics.jung.graph.predicates;

import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.collections.Predicate;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.predicates.IsolatedVertexPredicate;
import edu.uci.ics.jung.graph.predicates.UserDatumVertexPredicate;
import edu.uci.ics.jung.graph.predicates.VertexPredicate;
import edu.uci.ics.jung.utils.PredicateUtils;
import edu.uci.ics.jung.utils.UserData;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class VertexPredicateTest extends TestCase
{
    private Graph g;
    private Vertex v1;
    private Vertex v2;
    private Vertex v3;
    
    public void setUp()
    {
        g = new SparseGraph();
        v1 = new SparseVertex();
        v2 = new SparseVertex();
        v3 = new SparseVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addEdge(new DirectedSparseEdge(v1, v2));
    }
    
    public void testIsolatedVertex()
    {
        VertexPredicate p = IsolatedVertexPredicate.getInstance();
        VertexPredicate p2 = IsolatedVertexPredicate.getInstance();
        assertFalse(p.evaluateVertex(v1));
        assertFalse(p.evaluateVertex(v2));
        assertTrue(p.evaluateVertex(v3));
        assertEquals( p, p2);
    }
    
    public void testUserDatumVertex()
    {
        VertexPredicate p = new UserDatumVertexPredicate("key", "a");
        VertexPredicate p2 = new UserDatumVertexPredicate("key", "a");
        assertFalse(p.evaluateVertex(v1));
        assertFalse(p.evaluateVertex(v2));
        assertFalse(p.evaluateVertex(v3));

        v2.addUserDatum("key", "a", UserData.SHARED);
        v3.addUserDatum("key", "a", UserData.SHARED);

        assertFalse(p.evaluateVertex(v1));
        assertTrue(p.evaluateVertex(v2));
        assertTrue(p.evaluateVertex(v3));
        assertEquals( p, p2);

    }

    public void testEnforcesVertexPredicate()
    {
        Predicate p = new UserDatumVertexPredicate("key", "a");
        Collection predicates = g.getVertexConstraints();
        assertFalse(predicates.contains(p));
        try
        {
            predicates.add(p);
            fail("should not be able to add predicates to a non-empty graph");
        }
        catch (IllegalArgumentException iae) {}
        g.removeAllVertices();
        predicates.add(p);
        v1 = new SparseVertex();
        v2 = new SparseVertex();
        v3 = new SparseVertex();
        v1.addUserDatum("key", "a", UserData.SHARED);
        v2.addUserDatum("key", "a", UserData.SHARED);
        v3.addUserDatum("key", "a", UserData.SHARED);
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        assertTrue(predicates.contains(p));
        try
        {
            Vertex v4 = new SparseVertex();
            g.addVertex(v4);
            fail(p.toString());
        }
        catch (IllegalArgumentException iae) {}
        Vertex v5 = new SparseVertex();
        v5.addUserDatum("key", "a", UserData.SHARED);
        g.addVertex(v5);
        
        assertTrue(predicates.remove(p));
        assertFalse(predicates.remove(p));
        assertTrue(PredicateUtils.satisfiesVertexConstraint(g,p));
    }
    
    public void testSatisfiesVertexPredicate()
    {
        Predicate p = new UserDatumVertexPredicate("key", "a");
        assertFalse(PredicateUtils.satisfiesVertexConstraint(g,p));
        v1.addUserDatum("key", "a", UserData.SHARED);
        v2.addUserDatum("key", "a", UserData.SHARED);
        v3.addUserDatum("key", "a", UserData.SHARED);
        assertTrue(PredicateUtils.satisfiesVertexConstraint(g,p));
        Vertex v4 = new SparseVertex();
        g.addVertex(v4);
        assertFalse(PredicateUtils.satisfiesVertexConstraint(g,p));
        v4.addUserDatum("key", "a", UserData.SHARED);
        assertTrue(PredicateUtils.satisfiesVertexConstraint(g,p));
        Vertex v5 = new SparseVertex();
        v5.addUserDatum("key", "a", UserData.SHARED);
        g.addVertex(v5);
        assertTrue(PredicateUtils.satisfiesVertexConstraint(g,p));
        
        Graph g2 = new SparseGraph();
        assertTrue(PredicateUtils.satisfiesVertexConstraint(g2,p));
    }
}
