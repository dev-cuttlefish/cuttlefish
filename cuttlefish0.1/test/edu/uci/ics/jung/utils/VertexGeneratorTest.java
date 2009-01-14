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
package test.edu.uci.ics.jung.utils;

import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SimpleSparseVertex;
import edu.uci.ics.jung.graph.impl.SimpleUndirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseVertex;
import edu.uci.ics.jung.utils.TypedVertexGenerator;


/**
 * 
 * @author Joshua O'Madadhain
 */
public class VertexGeneratorTest extends TestCase
{
    public void testTypedVertexGenerator()
    {
        Collection c = new HashSet();
        TypedVertexGenerator vg = new TypedVertexGenerator(c);
        assertTrue(vg.create() instanceof SparseVertex);

        c.add(Graph.NOT_PARALLEL_EDGE);
        vg = new TypedVertexGenerator(c);
        assertTrue(vg.create() instanceof SimpleSparseVertex);

        c.clear();
        c.add(Graph.UNDIRECTED_EDGE);
        vg = new TypedVertexGenerator(c);
        assertTrue(vg.create() instanceof UndirectedSparseVertex);
        
        c.add(Graph.NOT_PARALLEL_EDGE);
        vg = new TypedVertexGenerator(c);
        assertTrue(vg.create() instanceof SimpleUndirectedSparseVertex);
        
        c.clear();
        c.add(Graph.DIRECTED_EDGE);
        vg = new TypedVertexGenerator(c);
        assertTrue(vg.create() instanceof DirectedSparseVertex);
        
        c.add(Graph.NOT_PARALLEL_EDGE);
        vg = new TypedVertexGenerator(c);
        assertTrue(vg.create() instanceof SimpleDirectedSparseVertex);
    }
}
