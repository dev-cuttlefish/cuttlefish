/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved. This software is open-source under the BSD
 * license; see either "license.txt" or http://jung.sourceforge.net/license.txt
 * for a description.
 */
package test.edu.uci.ics.jung.io;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.exceptions.FatalException;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.io.PajekNetFile;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author Scott White
 * @author Joshua O'Madadhain
 */
public class PajekNetFileTest extends TestCase
{

    public static Test suite()
    {
        return new TestSuite(PajekNetFileTest.class);
    }

    protected void setUp()
    {

    }

    public void testFileNotFound()
    {
        PajekNetFile pnf = new PajekNetFile();
        try
        {
            pnf.load("/dev/null/wugga");
            fail("File load did not fail on nonexistent file");
        }
        catch (FatalException npe)
        {
        }
    }

    public void testDirectedSaveLoadSave()
    {
        DirectedSparseGraph graph1 = new DirectedSparseGraph();
        GraphUtils.addVertices(graph1, 5);
        Indexer id = Indexer.getIndexer(graph1);
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(0), (Vertex) id.getVertex(1));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(0), (Vertex) id.getVertex(2));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(2));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(3));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(4));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(4), (Vertex) id.getVertex(3));

        assertEquals(graph1.numEdges(), 6);

        String testFilename = "dtest.net";
        String testFilename2 = testFilename + "2";

        PajekNetFile pajekFile = new PajekNetFile();
        pajekFile.save(graph1, testFilename);

        Graph graph2 = pajekFile.load(testFilename);

        assertEquals(graph1.numEdges(), graph2.numEdges());

        pajekFile.save(graph2, testFilename2);

        assertTrue(compareIndexedGraphs(graph1, graph2));

        Graph graph3 = pajekFile.load(testFilename2);

        assertTrue(compareIndexedGraphs(graph2, graph3));

        File file1 = new File(testFilename);
        File file2 = new File(testFilename2);

        Assert.assertEquals(52, file1.length());
        Assert.assertTrue(file1.length() == file2.length());
        file1.delete();
        file2.delete();
    }

    public void testUndirectedSaveLoadSave()
    {
        UndirectedSparseGraph graph1 = new UndirectedSparseGraph();
        GraphUtils.addVertices(graph1, 5);
        Indexer id = Indexer.getIndexer(graph1);
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(0), (Vertex) id.getVertex(1));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(0), (Vertex) id.getVertex(2));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(2));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(3));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(4));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(4), (Vertex) id.getVertex(3));

        assertEquals(graph1.numEdges(), 6);

        String testFilename = "utest.net";
        String testFilename2 = testFilename + "2";

        PajekNetFile pajekFile = new PajekNetFile();
        pajekFile.save(graph1, testFilename);

        Graph graph2 = pajekFile.load(testFilename);

        assertEquals(graph2.numEdges(), graph1.numEdges());

        pajekFile.save(graph2, testFilename2);
        assertTrue(compareIndexedGraphs(graph1, graph2));

        Graph graph3 = pajekFile.load(testFilename2);
        assertTrue(compareIndexedGraphs(graph2, graph3));

        File file1 = new File(testFilename);
        File file2 = new File(testFilename2);

        Assert.assertEquals(53, file1.length());
        Assert.assertTrue(file1.length() == file2.length());
        file1.delete();
        file2.delete();
    }

    public void testMixedSave()
    {
        SparseGraph graph1 = new SparseGraph();
        GraphUtils.addVertices(graph1, 5);
        Indexer id = Indexer.getIndexer(graph1);
        graph1.addEdge(new DirectedSparseEdge((Vertex) id.getVertex(0), (Vertex) id.getVertex(1)));
        graph1.addEdge(new DirectedSparseEdge((Vertex) id.getVertex(0), (Vertex) id.getVertex(2)));
        graph1.addEdge(new DirectedSparseEdge((Vertex) id.getVertex(1), (Vertex) id.getVertex(2)));
        graph1.addEdge(new UndirectedSparseEdge((Vertex) id.getVertex(1), (Vertex) id.getVertex(3)));
        graph1.addEdge(new UndirectedSparseEdge((Vertex) id.getVertex(1), (Vertex) id.getVertex(4)));
        graph1.addEdge(new UndirectedSparseEdge((Vertex) id.getVertex(4), (Vertex) id.getVertex(3)));
        
        assertEquals(graph1.numEdges(), 6);

        String testFilename = "mtest.net";
        String testFilename2 = testFilename + "2";

        PajekNetFile pajekFile = new PajekNetFile();
        pajekFile.save(graph1, testFilename);

        Graph graph2 = pajekFile.load(testFilename);
//
//        assertEquals(graph1.numEdges(), graph2.numEdges());
//
//        pajekFile.save(graph2, testFilename2);
//
//        assertTrue(compareIndexedGraphs(graph1, graph2));
//
//        Graph graph3 = pajekFile.load(testFilename2);
//
//        assertTrue(compareIndexedGraphs(graph2, graph3));
//
//        File file1 = new File(testFilename);
//        File file2 = new File(testFilename2);
//
//        Assert.assertEquals(56, file1.length());
//        Assert.assertTrue(file1.length() == file2.length());
//        file1.delete();
//        file2.delete();
        
    }
    
    /**
     * Tests to see whether these two graphs are structurally equivalent, based
     * on the connectivity of the vertices with matching indices in each graph.
     * Assumes a 1-based index. TODO: These tests don't test vertex labeling or
     * edge weights.
     * 
     * @param g1
     * @param g2
     * @return
     */
    private boolean compareIndexedGraphs(Graph g1, Graph g2)
    {
        int n1 = g1.numVertices();
        int n2 = g2.numVertices();

        if (n1 != n2)
            return false;

        if (g1.numEdges() != g2.numEdges())
            return false;

        Indexer id1 = Indexer.getIndexer(g1);
        Indexer id2 = Indexer.getIndexer(g2);

        for (int i = 1; i <= n1; i++)
        {
            Vertex v1 = (Vertex) id1.getVertex(i);
            Vertex v2 = (Vertex) id2.getVertex(i);
            if (v1 == null || v2 == null)
                return false;

            if (!checkSets(v1.getPredecessors(), v2.getPredecessors(), id1, id2))
                return false;

            if (!checkSets(v1.getSuccessors(), v2.getSuccessors(), id1, id2))
                return false;
        }
        return true;
    }

    private boolean checkSets(Set s1, Set s2, Indexer id1, Indexer id2)
    {
        for (Iterator p_iter = s1.iterator(); p_iter.hasNext();)
        {
            Vertex u = (Vertex) p_iter.next();
            int j = id1.getIndex(u);
            if (!(s2.contains(id2.getVertex(j))))
                return false;
        }
        return true;
    }
}