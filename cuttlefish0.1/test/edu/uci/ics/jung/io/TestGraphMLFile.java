/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.io.GraphMLFile;

/**
 * @author Scott White
 */
public class TestGraphMLFile extends TestCase {
    public static Test suite() {
        return new TestSuite(TestGraphMLFile.class);
    }

    protected void setUp() {

    }

    public void testLoad() {
        String testFilename = "toy_graph.ml";

        Graph graph = loadGraph(testFilename);

        Assert.assertEquals(graph.numVertices(),3);
        Assert.assertEquals(graph.numEdges(),3);

        StringLabeller labeller = StringLabeller.getLabeller(graph);

        Vertex joe = labeller.getVertex("1");
        Vertex bob = labeller.getVertex("2");
        Vertex sue = labeller.getVertex("3");
        Assert.assertEquals(joe.getUserDatum("name"),"Joe");
        Assert.assertEquals(bob.getUserDatum("name"),"Bob");
        Assert.assertEquals(sue.getUserDatum("name"),"Sue");

        Assert.assertTrue(bob.isPredecessorOf(joe));
        Assert.assertTrue(joe.isPredecessorOf(bob));
        Assert.assertTrue(joe.isPredecessorOf(sue));
        Assert.assertFalse(sue.isPredecessorOf(joe));
        Assert.assertFalse(bob.isPredecessorOf(sue));
        Assert.assertFalse(sue.isPredecessorOf(bob));


        File testFile = new File(testFilename);
        testFile.delete();
    }

    private Graph loadGraph(String testFilename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFilename));
            writer.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
             writer.write("<?meta name=\"GENERATOR\" content=\"XML::Smart 1.3.1\" ?>\n");
            writer.write("<graph edgedefault=\"directed\">\n");
            writer.write("<node id=\"1\" name=\"Joe\"/>\n");
            writer.write("<node id=\"2\" name=\"Bob\"/>\n");
            writer.write("<node id=\"3\" name=\"Sue\"/>\n");
            writer.write("<edge source=\"1\" target=\"2\"/>\n");
            writer.write("<edge source=\"2\" target=\"1\"/>\n");
            writer.write("<edge source=\"1\" target=\"3\"/>\n");
             writer.write("</graph>\n");
            writer.close();

        } catch (IOException ioe) {

        }

        GraphMLFile graphmlFile = new GraphMLFile();
        Graph graph = graphmlFile.load(testFilename);
        return graph;
    }

    public void testSave() {
        String testFilename = "toy_graph.ml";
        Graph oldGraph = loadGraph(testFilename);
        GraphMLFile graphmlFile = new GraphMLFile();
        String newFilename = testFilename + "_save";
        graphmlFile.save(oldGraph,newFilename);
		Graph newGraph = graphmlFile.load(newFilename);
        Assert.assertEquals(oldGraph.numVertices(),newGraph.numVertices());
        Assert.assertEquals(oldGraph.numEdges(),newGraph.numEdges());
        File testFile = new File(testFilename);
        testFile.delete();
        File newFile = new File(newFilename);
        newFile.delete();


    }
}
