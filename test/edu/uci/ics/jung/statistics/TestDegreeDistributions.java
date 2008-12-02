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

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.statistics.DegreeDistributions;
import edu.uci.ics.jung.statistics.Histogram;

/**
 * @author Scott White
 */
public class TestDegreeDistributions extends TestCase {
    Graph mGraph;
	public static Test suite() {
		return new TestSuite(TestDegreeDistributions.class);
	}

	protected void setUp() {

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
        mGraph = graphmlFile.load(new StringReader(buffer.toString()));

	}

	public void testGetIndegreeValues() {

		DoubleArrayList indegrees = DegreeDistributions.getIndegreeValues(mGraph.getVertices());
        Assert.assertEquals(indegrees.size(),6);
        Assert.assertEquals((0.0+1.0+2.0+1.0+2.0+2.0)/6.0,0.1,Descriptive.mean(indegrees));
        Assert.assertTrue(indegrees.contains(0.0));
        Assert.assertTrue(indegrees.contains(1.0));
        Assert.assertTrue(indegrees.contains(2.0));

	}

    public void testGetOutdegreeValues() {

		DoubleArrayList outdegrees = DegreeDistributions.getOutdegreeValues(mGraph.getVertices());
        Assert.assertEquals(outdegrees.size(),6);
        Assert.assertEquals((4.0+1.0+0.0+2.0+0.0+1.0)/6.0,0.1,Descriptive.mean(outdegrees));
        Assert.assertTrue(outdegrees.contains(0.0));
        Assert.assertTrue(outdegrees.contains(1.0));
        Assert.assertTrue(outdegrees.contains(2.0));
        Assert.assertTrue(outdegrees.contains(4.0));
	}

    public void testGetIndegreeHistogram() {
        Histogram inHist = DegreeDistributions.getIndegreeHistogram(mGraph.getVertices(),0,2.0,3);
        Assert.assertEquals((0.0+1.0+2.0+1.0+2.0+2.0)/6.0,0.1,inHist.average());
        Assert.assertEquals(inHist.size(),3.0,0.1);
        Assert.assertEquals(inHist.binHeight(0),1.0,0.1);
        Assert.assertEquals(inHist.binHeight(1),2.0,0.1);
        Assert.assertEquals(inHist.binHeight(2),3.0,0.1);

    }

    public void testGetOutdegreeHistogram() {
        Histogram outHist = DegreeDistributions.getOutdegreeHistogram(mGraph.getVertices(),0,4.0,5);
        Assert.assertEquals((4.0+1.0+0.0+2.0+0.0+1.0)/6.0/6.0,0.1,outHist.average());
        Assert.assertEquals(outHist.binHeight(0),2.0,0.1);
        Assert.assertEquals(outHist.binHeight(1),2.0,0.1);
        Assert.assertEquals(outHist.binHeight(2),1.0,0.1);
        Assert.assertEquals(outHist.binHeight(3),0.0,0.1);
        Assert.assertEquals(outHist.binHeight(4),1.0,0.1);

    }
}
