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

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.collections.Predicate;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.predicates.IsolatedVertexPredicate;
import edu.uci.ics.jung.graph.predicates.SelfLoopEdgePredicate;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.PredicateUtils;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class PredicateUtilTest extends TestCase
{
    private Graph g1;
    private Graph g2;
    private Graph g3;
    private Edge e;
    
    public void setUp()
    {
        g1 = new DirectedSparseGraph();
        g2 = new UndirectedSparseGraph();
        g3 = new DirectedSparseGraph();
        
        GraphUtils.addVertices(g1, 3);
        GraphUtils.addVertices(g2, 3);
        GraphUtils.addVertices(g3, 4);
        
        Indexer id = Indexer.getIndexer(g1);
        GraphUtils.addEdge(g1, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        GraphUtils.addEdge(g1, (Vertex)id.getVertex(2), (Vertex)id.getVertex(1));

        id = Indexer.getIndexer(g2);
        GraphUtils.addEdge(g2, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        GraphUtils.addEdge(g2, (Vertex)id.getVertex(2), (Vertex)id.getVertex(1));

        id = Indexer.getIndexer(g3);
        GraphUtils.addEdge(g3, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        e = GraphUtils.addEdge(g3, (Vertex)id.getVertex(2), (Vertex)id.getVertex(2));
    }
    
    public void testGetSatisfyingEdges()
    {
        Predicate p = SelfLoopEdgePredicate.getInstance();
        Set s1 = new HashSet();
        Set s2 = new HashSet();
        Set s3 = new HashSet();
        s3.add(e); // this is the only self-loop in any graph

        assertEquals(PredicateUtils.getEdges(g1, p), s1);
        assertEquals(PredicateUtils.getEdges(g2, p), s2);
        assertEquals(PredicateUtils.getEdges(g3, p), s3);
    }
    
    public void testGetSatisfyingVertices()
    {
        Predicate p = IsolatedVertexPredicate.getInstance();
        Set s1 = new HashSet();
        Set s2 = new HashSet();
        Set s3 = new HashSet();
        Indexer id = Indexer.getIndexer(g3);
        s3.add(id.getVertex(3)); // this is the only isolated vertex in any graph
        
        assertEquals(PredicateUtils.getVertices(g1, p), s1);
        assertEquals(PredicateUtils.getVertices(g2, p), s2);
        assertEquals(PredicateUtils.getVertices(g3, p), s3);
    }

}
