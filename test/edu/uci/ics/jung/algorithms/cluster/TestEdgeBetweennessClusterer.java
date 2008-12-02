/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.algorithms.cluster;

import java.io.StringReader;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLFile;

/**
 * @author Scott White
 */
public class TestEdgeBetweennessClusterer extends TestCase {
    public static Test suite() {
        return new TestSuite(TestEdgeBetweennessClusterer.class);
    }

    protected void setUp() {

    }

    public void testRanker() {
        StringBuffer buffer= new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
        buffer.append("<graph edgedefault=\"undirected\">\n");
        buffer.append("<node id=\"1\" label=\"1\"/>\n");
        buffer.append("<node id=\"2\" label=\"2\"/>\n");
        buffer.append("<node id=\"3\" label=\"3\"/>\n");
        buffer.append("<node id=\"4\" label=\"4\"/>\n");
        buffer.append("<node id=\"5\" label=\"5\"/>\n");
        buffer.append("<node id=\"6\" label=\"6\"/>\n");
        buffer.append("<node id=\"7\" label=\"7\"/>\n");
        buffer.append("<node id=\"8\" label=\"8\"/>\n");
        buffer.append("<node id=\"9\" label=\"9\"/>\n");
        buffer.append("<node id=\"10\" label=\"1\"/>\n");
        buffer.append("<edge source=\"1\" target=\"2\" label=\"1_2\"/>\n");
        buffer.append("<edge source=\"1\" target=\"3\" label=\"1_3\"/>\n");
        buffer.append("<edge source=\"2\" target=\"3\" label=\"2_3\"/>\n");
        buffer.append("<edge source=\"5\" target=\"6\" label=\"5_6\"/>\n");
        buffer.append("<edge source=\"5\" target=\"7\" label=\"5_7\"/>\n");
        buffer.append("<edge source=\"6\" target=\"7\" label=\"6_7\"/>\n");
        buffer.append("<edge source=\"8\" target=\"10\" label=\"8_10\"/>\n");
        buffer.append("<edge source=\"8\" target=\"7\" label=\"7_8\"/>\n");
        buffer.append("<edge source=\"7\" target=\"10\" label=\"7_10\"/>\n");
        buffer.append("<edge source=\"3\" target=\"4\" label=\"3_4\"/>\n");
        buffer.append("<edge source=\"4\" target=\"6\" label=\"4_6\"/>\n");
        buffer.append("<edge source=\"4\" target=\"8\" label=\"4_8\"/>\n");
        buffer.append("</graph>\n");

        GraphMLFile graphmlFile = new GraphMLFile();
        Graph graph = graphmlFile.load(new StringReader(buffer.toString()));

        Assert.assertEquals(graph.numVertices(),10);
        Assert.assertEquals(graph.numEdges(),12);

        EdgeBetweennessClusterer clusterer = new EdgeBetweennessClusterer(3);
        ClusterSet clusters = clusterer.extract(graph);

//        System.out.println("");
        
        Assert.assertEquals(clusters.size(),3);




    }
}
