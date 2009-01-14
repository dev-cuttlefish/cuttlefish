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
package test.edu.uci.ics.jung.graph.decorators;

import java.util.HashSet;
import java.util.Iterator;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.ToStringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author danyelf
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestMultiLabeller extends TestCase {

    Graph graph;

    Vertex v1, v2;

    protected void setUp() {
        graph = new DirectedSparseGraph();
        GraphUtils.addVertices(graph, 10);

        int count = 0;
        // grab two random vertices. We'll use them later.
        for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            count++;
            if (count == 3) v1 = v;
            if (count == 7) v2 = v;
        }
    }

    protected void tearDown() {
        graph = null;
        v1 = null;
        v2 = null;
    }

    public void testToLabeller() throws UniqueLabelException {
        StringLabeller sl = ToStringLabeller.setLabellerTo(graph);
        assertTrue(sl instanceof ToStringLabeller);
        StringLabeller sl2 = ToStringLabeller.getLabeller(graph);
        assertTrue(sl2 instanceof ToStringLabeller);
        sl2 = StringLabeller.getLabeller(graph);
        assertTrue(sl2 instanceof ToStringLabeller);
        sl2 = StringLabeller.getLabeller(graph, "foo");
        assertFalse(sl2 instanceof ToStringLabeller);
        for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            assertEquals(sl.getLabel(v), v.toString());
        }
        
        try {
        	sl.assignDefaultLabels(new HashSet(), 0);
            fail("ToStringLabeller should not allow set default labels");        	
        } catch (IllegalArgumentException iae) {
        }

        try {
        	sl.removeLabel( "foo ");
        	fail("ToStringLabeller should not allow removing labels");
        } catch (IllegalArgumentException iae) {
        }

        for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            String s = sl.getLabel( v );
            assertNull( sl.getVertex(s));
        }
        
        try {
            for (Iterator iter = graph.getVertices().iterator(); iter.hasNext();) {
                Vertex v = (Vertex) iter.next();
                sl.setLabel(v, "foo");
                fail("ToStringLabeller should not allow setting the label");
            }
        } catch (IllegalArgumentException iae) {
        }

    }

}