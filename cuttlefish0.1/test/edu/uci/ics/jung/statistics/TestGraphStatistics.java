/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.statistics;

import java.io.StringReader;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.statistics.GraphStatistics;

/**
 * @author Scott White
 */
public class TestGraphStatistics extends TestCase {
    Graph mGraph;
	public static Test suite() {
		return new TestSuite(TestGraphStatistics.class);
	}

	protected void setUp() {

        StringBuffer buffer= new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
        buffer.append("<graph edgedefault=\"undirected\">\n");
        buffer.append("<node id=\"1\"/>\n");
        buffer.append("<node id=\"2\"/>\n");
        buffer.append("<node id=\"3\"/>\n");
        buffer.append("<node id=\"4\"/>\n");
        buffer.append("<node id=\"5\"/>\n");
        buffer.append("<node id=\"6\"/>\n");
        buffer.append("<edge source=\"1\" target=\"2\"/>\n");
        buffer.append("<edge source=\"1\" target=\"6\"/>\n");
        buffer.append("<edge source=\"1\" target=\"4\"/>\n");
        buffer.append("<edge source=\"1\" target=\"5\"/>\n");
        buffer.append("<edge source=\"2\" target=\"6\"/>\n");
        buffer.append("<edge source=\"4\" target=\"5\"/>\n");
        buffer.append("<edge source=\"4\" target=\"3\"/>\n");
        buffer.append("<edge source=\"6\" target=\"3\"/>\n");
        buffer.append("</graph>\n");

        GraphMLFile graphmlFile = new GraphMLFile();
        mGraph = graphmlFile.load(new StringReader(buffer.toString()));

	}

    public void testAverageDistancesOnInfiniteDiameterGraph() {
        StringBuffer buffer= new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
        buffer.append("<graph edgedefault=\"directed\">\n");
        buffer.append("<node id=\"1\"/>\n");
        buffer.append("<node id=\"2\"/>\n");
        buffer.append("<node id=\"3\"/>\n");
        buffer.append("<node id=\"4\"/>\n");
        buffer.append("<node id=\"5\"/>\n");
        buffer.append("<node id=\"6\"/>\n");
        buffer.append("<edge source=\"1\" target=\"2\"/>\n");
        buffer.append("<edge source=\"1\" target=\"6\"/>\n");
        buffer.append("<edge source=\"1\" target=\"4\"/>\n");
        buffer.append("<edge source=\"1\" target=\"5\"/>\n");
        buffer.append("<edge source=\"2\" target=\"6\"/>\n");
        buffer.append("<edge source=\"4\" target=\"5\"/>\n");
        buffer.append("<edge source=\"4\" target=\"3\"/>\n");
        buffer.append("<edge source=\"6\" target=\"3\"/>\n");
        buffer.append("</graph>\n");

        GraphMLFile graphmlFile = new GraphMLFile();
        Graph g = graphmlFile.load(new StringReader(buffer.toString()));

//        DoubleArrayList distances = GraphStatistics.averageDistances(g);
//        assertNull(distances);
        
        Map dist_map = GraphStatistics.averageDistances(g, new UnweightedShortestPath(g));
        StringLabeller sl = StringLabeller.getLabeller(g);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("1"))).doubleValue(), 1.2, .001);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("2"))).doubleValue(), Double.POSITIVE_INFINITY, .001);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("3"))).doubleValue(), Double.POSITIVE_INFINITY, .001);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("4"))).doubleValue(), Double.POSITIVE_INFINITY, .001);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("5"))).doubleValue(), Double.POSITIVE_INFINITY, .001);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("6"))).doubleValue(), Double.POSITIVE_INFINITY, .001);
        
    }

    public void testAverageDistancesOnFfiniteDiameterGraph() {
//        DoubleArrayList distances = GraphStatistics.averageDistances(mGraph);
//        Assert.assertNotNull(distances);
//        Assert.assertEquals((1.2+1.6+1.6+1.4+1.6+1.4)/6.0,Descriptive.mean(distances),0.01);
//        Assert.assertTrue(distances.contains(1.2));
//        Assert.assertTrue(distances.contains(1.4));
//        Assert.assertTrue(distances.contains(1.6));

        Map dist_map = GraphStatistics.averageDistances(mGraph, new UnweightedShortestPath(mGraph));
        StringLabeller sl = StringLabeller.getLabeller(mGraph);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("1"))).doubleValue(), 1.2, .001);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("2"))).doubleValue(), 1.6, .001);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("3"))).doubleValue(), 1.6, .001);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("4"))).doubleValue(), 1.4, .001);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("5"))).doubleValue(), 1.6, .001);
        Assert.assertEquals(((Double)dist_map.get(sl.getVertex("6"))).doubleValue(), 1.4, .001);
    }

    public void testClusteringCoefficients() {
//        DoubleArrayList values = GraphStatistics.clusteringCoefficients(mGraph);
//        Assert.assertEquals(0.333+1.0+0.0+0.333+1.0+0.333,Descriptive.sum(values),.01);
        
        Map cc_map = GraphStatistics.clusteringCoefficients((Graph)mGraph);
        StringLabeller sl = StringLabeller.getLabeller(mGraph);
        Assert.assertEquals(((Double)cc_map.get(sl.getVertex("1"))).doubleValue(), 0.333, .001);
        Assert.assertEquals(((Double)cc_map.get(sl.getVertex("2"))).doubleValue(), 1.0, .001);
        Assert.assertEquals(((Double)cc_map.get(sl.getVertex("3"))).doubleValue(), 0, .001);
        Assert.assertEquals(((Double)cc_map.get(sl.getVertex("4"))).doubleValue(), 0.333, .001);
        Assert.assertEquals(((Double)cc_map.get(sl.getVertex("5"))).doubleValue(), 1.0, .001);
        Assert.assertEquals(((Double)cc_map.get(sl.getVertex("6"))).doubleValue(), 0.333, .001);
    }
    
    public void testDiameter() {
        Assert.assertEquals(GraphStatistics.diameter(mGraph),2.0,.001);
    }
}
