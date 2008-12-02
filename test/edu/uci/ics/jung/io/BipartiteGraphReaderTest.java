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
package test.edu.uci.ics.jung.io;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.KPartiteGraph;
import edu.uci.ics.jung.graph.UndirectedEdge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeWeightLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.io.BipartiteGraphReader;
import edu.uci.ics.jung.utils.GraphUtils;


/**
 * 
 * @author Joshua O'Madadhain
 */
public class BipartiteGraphReaderTest extends TestCase
{
    public void testAsList() throws IOException
    {
        String test = "a1 b1 b2 b3";
        Reader r = new StringReader(test);
        BipartiteGraphReader bgr = new BipartiteGraphReader(true, false, false);
        KPartiteGraph g = bgr.load(r);
        StringLabeller sl_a = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_A);
        StringLabeller sl_b = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_B);
        assertTrue(g.numVertices() == 4);
        assertTrue(g.numEdges() == 3);
        Vertex a1 = sl_a.getVertex("a1");
        Vertex b1 = sl_b.getVertex("b1");
        Vertex b2 = sl_b.getVertex("b2");
        Vertex b3 = sl_b.getVertex("b3");
        assertTrue(a1.isNeighborOf(b1));
        assertTrue(a1.isNeighborOf(b1));
        assertTrue(a1.isNeighborOf(b2));
        assertTrue(a1.isNeighborOf(b3));
        assertFalse(b1.isNeighborOf(b2));
        
        r = new StringReader(test);
        bgr.setAsList(false);
        g = bgr.load(r);
        assertTrue(g.numVertices() == 2);
        assertTrue(g.numEdges() == 1);
        sl_a = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_A);
        sl_b = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_B);
        a1 = sl_a.getVertex("a1");
        b1 = sl_b.getVertex("b1 b2 b3");
        assertTrue(a1.isNeighborOf(b1));
    }

    public void testParallel() throws IOException
    {
        String test = "a1 b1 b1";
        Reader r = new StringReader(test);
        BipartiteGraphReader bgr = new BipartiteGraphReader(true, false, false);
        KPartiteGraph g = bgr.load(r); // no parallel edges
        StringLabeller sl_a = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_A);
        StringLabeller sl_b = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_B);
        assertTrue(g.numVertices() == 2);
        assertTrue(g.numEdges() == 1);
        Vertex a1 = sl_a.getVertex("a1");
        Vertex b1 = sl_b.getVertex("b1");
        assertTrue(a1.findEdgeSet(b1).size() == 1);
        Edge e = a1.findEdge(b1);
        EdgeWeightLabeller ewl = EdgeWeightLabeller.getLabeller(g);
        assertTrue(ewl.getWeight(e) == 2);
        
        r = new StringReader(test);
        bgr.setParallel(true);
        g = bgr.load(r); // allow parallel edges
        sl_a = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_A);
        sl_b = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_B);
        assertTrue(g.numVertices() == 2);
        assertTrue(g.numEdges() == 2);
        a1 = sl_a.getVertex("a1");
        b1 = sl_b.getVertex("b1");
        assertTrue(a1.findEdgeSet(b1).size() == 2);
    }
    
    public void testDirected() throws IOException
    {
        String test = "a1 b1 b2 b3";
        Reader r = new StringReader(test);
        BipartiteGraphReader bgr = new BipartiteGraphReader(true, false, false);
        KPartiteGraph g = bgr.load(r);
        StringLabeller sl_a = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_A);
        StringLabeller sl_b = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_B);
        assertTrue(g.numVertices() == 4);
        assertTrue(g.numEdges() == 3);
        Vertex a1 = sl_a.getVertex("a1");
        Vertex b1 = sl_b.getVertex("b1");
        Vertex b2 = sl_b.getVertex("b2");
        Vertex b3 = sl_b.getVertex("b3");
        assertTrue(a1.isNeighborOf(b1));
        assertTrue(a1.isNeighborOf(b1));
        assertTrue(a1.isNeighborOf(b2));
        assertTrue(a1.isNeighborOf(b3));
        assertFalse(b1.isNeighborOf(b2));
        Edge e1 = a1.findEdge(b1);
        Edge e2 = a1.findEdge(b2);
        Edge e3 = a1.findEdge(b3);
        assertTrue(e1 instanceof UndirectedEdge);
        assertTrue(e2 instanceof UndirectedEdge);
        assertTrue(e3 instanceof UndirectedEdge);
        
        r = new StringReader(test);
        bgr.setDirected(true);
        g = bgr.load(r);
        assertTrue(g.numVertices() == 4);
        assertTrue(g.numEdges() == 3);
        sl_a = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_A);
        sl_b = StringLabeller.getLabeller(g, BipartiteGraphReader.PART_B);
        a1 = sl_a.getVertex("a1");
        b1 = sl_b.getVertex("b1");
        b2 = sl_b.getVertex("b2");
        b3 = sl_b.getVertex("b3");
        assertTrue(a1.isNeighborOf(b1));
        e1 = a1.findEdge(b1);
        e2 = a1.findEdge(b2);
        e3 = a1.findEdge(b3);
        assertTrue(e1 instanceof DirectedEdge);
        assertTrue(e2 instanceof DirectedEdge);
        assertTrue(e3 instanceof DirectedEdge);
        assertNull(b1.findEdge(a1));
        assertNull(b2.findEdge(a1));
        assertNull(b3.findEdge(a1));
    }
    
    public void testCopy() throws IOException {
        String test = "a1 b1 b2 b3";
        Reader r = new StringReader(test);
        BipartiteGraphReader bgr = new BipartiteGraphReader(true, false, false);
        KPartiteGraph g = bgr.load(r);
        KPartiteGraph g_copy = (KPartiteGraph) g.copy();
        assertTrue( GraphUtils.areEquivalent(g, g_copy) );
        
    }
}
