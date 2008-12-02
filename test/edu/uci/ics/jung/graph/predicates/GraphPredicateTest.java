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

import junit.framework.TestCase;

import org.apache.commons.collections.Predicate;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.predicates.ConnectedGraphPredicate;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class GraphPredicateTest extends TestCase
{
    private Graph g1;
    private Graph g2;
    private Graph g3;
    
    public void setUp()
    {
        g1 = new DirectedSparseGraph();
        g2 = new UndirectedSparseGraph();
        g3 = new DirectedSparseGraph();
        
        GraphUtils.addVertices(g1, 3);
        GraphUtils.addVertices(g2, 3);
        GraphUtils.addVertices(g3, 3);
        
        Indexer id = Indexer.getIndexer(g1);
        GraphUtils.addEdge(g1, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        GraphUtils.addEdge(g1, (Vertex)id.getVertex(2), (Vertex)id.getVertex(1));

        id = Indexer.getIndexer(g2);
        GraphUtils.addEdge(g2, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        GraphUtils.addEdge(g2, (Vertex)id.getVertex(2), (Vertex)id.getVertex(1));

        id = Indexer.getIndexer(g3);
        GraphUtils.addEdge(g3, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
        GraphUtils.addEdge(g3, (Vertex)id.getVertex(2), (Vertex)id.getVertex(2));
    }
    
    public void testConnectedGraphPredicate()
    {
        Predicate p = ConnectedGraphPredicate.getInstance();
        assertTrue(p.evaluate(g1));
        assertTrue(p.evaluate(g2));
        assertFalse(p.evaluate(g3));
    }
    
}
