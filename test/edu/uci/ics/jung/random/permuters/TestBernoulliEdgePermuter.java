/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.random.permuters;

import java.io.StringReader;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.random.permuters.BernoulliEdgePermuter;

/**
 * @author Scott White
 */
public class TestBernoulliEdgePermuter extends TestCase {
	public static Test suite() {
		return new TestSuite(TestBernoulliEdgePermuter.class);
	}

	protected void setUp() {
	}

    public void testSimplePermutation1() {

        StringBuffer buffer= new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
        buffer.append("<graph edgedefault=\"undirected\">\n");
        buffer.append("<node id=\"1\"/>\n");
        buffer.append("<node id=\"2\"/>\n");
        buffer.append("<node id=\"3\"/>\n");
        buffer.append("<edge source=\"1\" target=\"2\"/>\n");
        buffer.append("<edge source=\"1\" target=\"3\"/>\n");
        buffer.append("<edge source=\"2\" target=\"3\"/>\n");
        buffer.append("</graph>\n");

        GraphMLFile graphmlFile = new GraphMLFile();
        Graph graph = graphmlFile.load(new StringReader(buffer.toString()));

        BernoulliEdgePermuter permuter = new BernoulliEdgePermuter(1);
        Assert.assertEquals(3,graph.numEdges());
        permuter.permuteEdges(graph);
        Assert.assertEquals(2,graph.numEdges());

    }

    public void testSimplePermutation2() {

        StringBuffer buffer= new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
        buffer.append("<graph edgedefault=\"undirected\">\n");
        buffer.append("<node id=\"1\"/>\n");
        buffer.append("<node id=\"2\"/>\n");
        buffer.append("<node id=\"3\"/>\n");
        buffer.append("</graph>\n");

        GraphMLFile graphmlFile = new GraphMLFile();
        Graph graph = graphmlFile.load(new StringReader(buffer.toString()));

        BernoulliEdgePermuter permuter = new BernoulliEdgePermuter(1);
        Assert.assertEquals(0,graph.numEdges());
        permuter.permuteEdges(graph);
        Assert.assertEquals(1,graph.numEdges());

    }
}
