/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Mar 3, 2004
 */
package test.edu.uci.ics.jung.graph.predicates;

import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.collections.Predicate;

import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.predicates.ReciprocatedDirectedEdgePredicate;
import edu.uci.ics.jung.graph.predicates.EdgePredicate;
import edu.uci.ics.jung.graph.predicates.ParallelEdgePredicate;
import edu.uci.ics.jung.graph.predicates.SelfLoopEdgePredicate;
import edu.uci.ics.jung.graph.predicates.SimpleEdgePredicate;
import edu.uci.ics.jung.graph.predicates.UserDatumEdgePredicate;
import edu.uci.ics.jung.utils.PredicateUtils;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author Joshua O'Madadhain
 */
public class EdgePredicateTest extends TestCase {

    private SparseGraph g;

    private Vertex v1;

    private Vertex v2;

    private Vertex v3;

    public void setUp() {
        g = new SparseGraph();
        v1 = new SparseVertex();
        v2 = new SparseVertex();
        v3 = new SparseVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
    }

    public void tearDown() {
        g.removeAllVertices();
    }

    public void testSelfLoop() {
        Predicate p = SelfLoopEdgePredicate.getInstance();
        Edge e1 = g.addEdge(new DirectedSparseEdge(v1, v2));
        Edge e2 = g.addEdge(new DirectedSparseEdge(v3, v3));
        assertFalse(p.evaluate(e1));
        assertTrue(p.evaluate(e2));
    }

    public void testParallelEdgeConstraint() {
        Predicate p = Graph.NOT_PARALLEL_EDGE;
        Edge e1 = new DirectedSparseEdge(v1, v2);
        assertTrue(p.evaluate(e1));
        g.addEdge(e1);
        Edge e2 = new DirectedSparseEdge(v1, v2);
        Edge e3 = new DirectedSparseEdge(v2, v1);
        assertFalse(p.evaluate(e2));
        assertTrue(p.evaluate(e3));
        g.removeEdge(e1);
        assertTrue(p.evaluate(e2));
    }

    public void testParallelEdge() {
        Predicate p = ParallelEdgePredicate.getInstance();
        Edge e1 = g.addEdge(new DirectedSparseEdge(v1, v2));
        Edge e2 = g.addEdge(new DirectedSparseEdge(v1, v2));
        Edge e3 = g.addEdge(new DirectedSparseEdge(v3, v3));
        Edge e4 = g.addEdge(new DirectedSparseEdge(v2, v1));
        Edge e5 = g.addEdge(new DirectedSparseEdge(v3, v1));
        Edge e6 = g.addEdge(new UndirectedSparseEdge(v3, v3));
        Edge e7 = g.addEdge(new UndirectedSparseEdge(v1, v2));
        Edge e8 = g.addEdge(new UndirectedSparseEdge(v2, v1));
        Edge e9 = g.addEdge(new UndirectedSparseEdge(v3, v1));
        assertTrue(p.evaluate(e1));
        assertTrue(p.evaluate(e2));
        assertFalse(p.evaluate(e3));
        assertFalse(p.evaluate(e4));
        assertFalse(p.evaluate(e5));
        assertFalse(p.evaluate(e6));
        assertTrue(p.evaluate(e7));
        assertTrue(p.evaluate(e8));
        assertFalse(p.evaluate(e9));
    }

    public void testSimpleEdge() {
        Predicate p = SimpleEdgePredicate.getInstance();
        Edge e1 = new DirectedSparseEdge(v1, v2); // neither || nor self-loop
        assertTrue(p.evaluate(e1));
        g.addEdge(e1);
        Edge e2 = new DirectedSparseEdge(v1, v2);
        Edge e3 = new DirectedSparseEdge(v2, v1);
        assertFalse(p.evaluate(e2)); // parallel
        assertTrue(p.evaluate(e3)); // antiparallel (neither)
        g.removeEdge(e1);
        assertTrue(p.evaluate(e2));
        Edge e4 = new DirectedSparseEdge(v3, v3); // self loop
        assertFalse(p.evaluate(e4));
        g.addEdge(e4);
        assertFalse(p.evaluate(e4)); // both || & self-loop
    }

    public void testDirectedEdge() {
        g.removeAllVertices();
        g.getEdgeConstraints().add(Graph.DIRECTED_EDGE);
        v1 = new SparseVertex();
        v2 = new SparseVertex();
        v3 = new SparseVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        Edge e1 = new DirectedSparseEdge(v1, v2);
        Edge e2 = new UndirectedSparseEdge(v1, v2);
        g.addEdge(e1);
        try {
            g.addEdge(e2);
            fail("This graph should not accept undirected edges");
        } catch (IllegalArgumentException iae) {
        }
        assertEquals(g.numEdges(), 1);
    }

    public void testUndirectedEdge() {
        g.removeAllVertices();
        g.getEdgeConstraints().add(Graph.UNDIRECTED_EDGE);
        v1 = g.addVertex(new SparseVertex());
        v2 = g.addVertex(new SparseVertex());
        v3 = g.addVertex(new SparseVertex());
        g.getEdgeConstraints().add(Graph.UNDIRECTED_EDGE);
        Edge e1 = new DirectedSparseEdge(v1, v2);
        Edge e2 = new UndirectedSparseEdge(v1, v2);
        g.addEdge(e2);
        try {
            g.addEdge(e1);
            fail("This graph should not accept undirected edges");
        } catch (IllegalArgumentException iae) {
        }
        assertEquals(g.numEdges(), 1);
    }

    public void testMixedGraph() {
        Edge e1 = new DirectedSparseEdge(v1, v2);
        Edge e2 = new UndirectedSparseEdge(v1, v2);
        g.addEdge(e2);
        g.addEdge(e1);
        assertEquals(g.numEdges(), 2);
    }

    public void testUserDatumEdge() {
        EdgePredicate p = new UserDatumEdgePredicate("key", "a");
        Edge e2 = g.addEdge(new DirectedSparseEdge(v1, v2));
        Edge e3 = g.addEdge(new DirectedSparseEdge(v3, v3));
        Edge e4 = g.addEdge(new DirectedSparseEdge(v2, v1));

        assertFalse(p.evaluateEdge(e2));
        assertFalse(p.evaluateEdge(e3));
        assertFalse(p.evaluateEdge(e4));

        e3.addUserDatum("key", "a", UserData.SHARED);
        e4.addUserDatum("key", "a", UserData.SHARED);

        assertFalse(p.evaluateEdge(e2));
        assertTrue(p.evaluateEdge(e3));
        assertTrue(p.evaluateEdge(e4));
    }

    public void testEnforcesEdgePredicate() {
        Predicate p = new UserDatumEdgePredicate("key", "a");
        Collection predicates = g.getEdgeConstraints();
        assertFalse(PredicateUtils.enforcesEdgeConstraint(g, p));
        v1 = g.addVertex(new SparseVertex());
        try {
            predicates.add(p);
            fail("should not allow new predicates in a non-empty graph " + p);
        } catch (IllegalArgumentException iae) {
        }
        g.removeAllVertices();
        predicates.add(p);
        v1 = g.addVertex(new SparseVertex());
        v2 = g.addVertex(new SparseVertex());
        v3 = g.addVertex(new SparseVertex());
        Edge e2 = new DirectedSparseEdge(v1, v2);
        Edge e3 = new DirectedSparseEdge(v3, v3);
        Edge e4 = new DirectedSparseEdge(v2, v1);
        e2.addUserDatum("key", "a", UserData.SHARED);
        e3.addUserDatum("key", "a", UserData.SHARED);
        e4.addUserDatum("key", "a", UserData.SHARED);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);

        assertTrue(PredicateUtils.enforcesEdgeConstraint(g, p));
        try {
            Edge e5 = new DirectedSparseEdge(v2, v3);
            g.addEdge(e5);
            fail(p.toString());
        } catch (IllegalArgumentException iae) {
        }
        Edge e6 = new DirectedSparseEdge(v3, v2);
        e6.addUserDatum("key", "a", UserData.SHARED);
        g.addEdge(e6);

        assertTrue(predicates.remove(p));
        assertFalse(predicates.remove(p));
        assertTrue(PredicateUtils.satisfiesEdgeConstraint(g, p));
    }

    public void testSatisfiesEdgePredicate() {
        Predicate p = new UserDatumEdgePredicate("key", "a");
        assertTrue(PredicateUtils.satisfiesEdgeConstraint(g, p));
        Edge e2 = g.addEdge(new DirectedSparseEdge(v1, v2));
        Edge e3 = g.addEdge(new DirectedSparseEdge(v3, v3));
        Edge e4 = g.addEdge(new DirectedSparseEdge(v2, v1));
        assertFalse(PredicateUtils.satisfiesEdgeConstraint(g, p));
        e2.addUserDatum("key", "a", UserData.SHARED);
        e3.addUserDatum("key", "a", UserData.SHARED);
        e4.addUserDatum("key", "a", UserData.SHARED);
        assertTrue(PredicateUtils.satisfiesEdgeConstraint(g, p));
        Edge e5 = new DirectedSparseEdge(v2, v3);
        e5.addUserDatum("key", "a", UserData.SHARED);
        g.addEdge(e5);
        assertTrue(PredicateUtils.satisfiesEdgeConstraint(g, p));
    }

    public void testNotInGraphPredicate() {
        Graph gCopy = (Graph) g.copy();
        Vertex v_new = new SparseVertex();
        gCopy.addVertex(v_new);
        try {
            gCopy.addVertex(v_new);
        } catch (IllegalArgumentException iae) {
            return;
        }
        fail("Should not allow two of the same vertex");
    }

    public void testAntiParallelDirectedEdgePredicate() 
    {
        EdgePredicate p = ReciprocatedDirectedEdgePredicate.getInstance();
        DirectedEdge e1 = new DirectedSparseEdge(v1, v2);
        assertFalse(p.evaluate(e1));
        g.addEdge(e1);
        DirectedEdge e2 = new DirectedSparseEdge(v2, v1);
        g.addEdge(e2);
        assertTrue(p.evaluate(e1));
        assertTrue(p.evaluate(e2));
    }
}