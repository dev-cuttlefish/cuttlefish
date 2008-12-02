/*
 * Created on May 3, 2004
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

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.decorators.NumberEdgeValue;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.io.PajekNetWriter;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.visualization.RandomVertexLocationDecorator;
import edu.uci.ics.jung.visualization.VertexLocationFunction;


/**
 * Needed tests:
 * - edgeslist, arcslist
 * - unit test to catch bug in readArcsOrEdges() [was skipping until e_pred, not c_pred]
 * 
 * @author Joshua O'Madadhain
 */
public class PajekNetIOTest extends TestCase
{
    protected String[] vertex_labels = {"alpha", "beta", "gamma", "delta", "epsilon"};
    
    public void testFileNotFound()
    {
        PajekNetReader pnf = new PajekNetReader();
        try
        {
            pnf.load("/dev/null/foo");
            fail("File load did not fail on nonexistent file");
        }
        catch (FileNotFoundException fnfe)
        {
        }
        catch (IOException ioe)
        {
            fail("unexpected IOException");
        }
    }

    public void testNoLabels() throws IOException
    {
        String test = "*Vertices 3\n1\n2\n3\n*Edges\n1 2\n2 2";
        Reader r = new StringReader(test);
        
        PajekNetReader pnr = new PajekNetReader();
        
        Graph g = pnr.load(r);
        
        assertEquals(g.numVertices(), 3);
        assertEquals(g.numEdges(), 2);
    }
    
    public void testDirectedSaveLoadSave() throws IOException
    {
        DirectedSparseGraph graph1 = new DirectedSparseGraph();
        GraphUtils.addVertices(graph1, 5);
        Indexer id = Indexer.getIndexer(graph1);
        GreekLabels gl = new GreekLabels(id);
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(0), (Vertex) id.getVertex(1));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(0), (Vertex) id.getVertex(2));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(2));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(3));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(4));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(4), (Vertex) id.getVertex(3));

        assertEquals(graph1.numEdges(), 6);

        String testFilename = "dtest.net";
        String testFilename2 = testFilename + "2";

        PajekNetWriter pnw = new PajekNetWriter();
        PajekNetReader pnr = new PajekNetReader();
        pnw.save(graph1, testFilename, gl, null, null);

        Graph graph2 = pnr.load(testFilename);

        assertEquals(graph1.numVertices(), graph2.numVertices());
        assertEquals(graph1.numEdges(), graph2.numEdges());

        pnw.save(graph2, testFilename2, new PajekLabels(), null, null);

        compareIndexedGraphs(graph1, graph2);

        Graph graph3 = pnr.load(testFilename2);

        compareIndexedGraphs(graph2, graph3);

        File file1 = new File(testFilename);
        File file2 = new File(testFilename2);

        Assert.assertTrue(file1.length() == file2.length());
        file1.delete();
        file2.delete();
    }

    public void testUndirectedSaveLoadSave() throws IOException
    {
        UndirectedSparseGraph graph1 = new UndirectedSparseGraph();
        GraphUtils.addVertices(graph1, 5);
        Indexer id = Indexer.getIndexer(graph1);
        GreekLabels gl = new GreekLabels(id);
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(0), (Vertex) id.getVertex(1));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(0), (Vertex) id.getVertex(2));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(2));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(3));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(1), (Vertex) id.getVertex(4));
        GraphUtils.addEdge(graph1, (Vertex) id.getVertex(4), (Vertex) id.getVertex(3));

        assertEquals(graph1.numEdges(), 6);

        String testFilename = "utest.net";
        String testFilename2 = testFilename + "2";

        PajekNetWriter pnw = new PajekNetWriter();
        PajekNetReader pnr = new PajekNetReader();
        pnw.save(graph1, testFilename, gl, null, null);

        Graph graph2 = pnr.load(testFilename);

        assertEquals(graph1.numVertices(), graph2.numVertices());
        assertEquals(graph1.numEdges(), graph2.numEdges());

        pnw.save(graph2, testFilename2, new PajekLabels(), null, null);
        compareIndexedGraphs(graph1, graph2);

        Graph graph3 = pnr.load(testFilename2);

        compareIndexedGraphs(graph2, graph3);

        File file1 = new File(testFilename);
        File file2 = new File(testFilename2);

        Assert.assertTrue(file1.length() == file2.length());
        file1.delete();
        file2.delete();
    }

    public void testMixedSaveLoadSave() throws IOException
    {
        SparseGraph graph1 = new SparseGraph();
        GraphUtils.addVertices(graph1, 5);
        Indexer id = Indexer.getIndexer(graph1);
        GreekLabels gl = new GreekLabels(id);
        Edge[] edges = 
        {
            new DirectedSparseEdge((Vertex) id.getVertex(0), (Vertex) id.getVertex(1)),
            new DirectedSparseEdge((Vertex) id.getVertex(0), (Vertex) id.getVertex(2)),
            new DirectedSparseEdge((Vertex) id.getVertex(1), (Vertex) id.getVertex(2)),
            new UndirectedSparseEdge((Vertex) id.getVertex(1), (Vertex) id.getVertex(3)),
            new UndirectedSparseEdge((Vertex) id.getVertex(1), (Vertex) id.getVertex(4)),
            new UndirectedSparseEdge((Vertex) id.getVertex(4), (Vertex) id.getVertex(3)),
        };

        NumberReader nr = new NumberReader();
        for (int i = 0; i < edges.length; i++)
        {
            graph1.addEdge(edges[i]);
            nr.setNumber(edges[i], new Float(Math.random()));
        }
        
        assertEquals(graph1.numEdges(), 6);

        String testFilename = "mtest.net";
        String testFilename2 = testFilename + "2";

        // lay out network
        Dimension d = new Dimension(100, 200);
        VertexLocationFunction vld = new RandomVertexLocationDecorator(d);
        // make sure the locations are initialized
        for (int i = 0; i < graph1.numVertices(); i++)
            vld.getLocation(id.getVertex(i));
        
        PajekNetWriter pnw = new PajekNetWriter();
        pnw.save(graph1, testFilename, gl, nr, vld);
        
//        PajekNetReader pnr = new PajekNetReader();
        PajekNetReader pnr = new PajekNetReader(false, true);
        NumberReader nr2 = new NumberReader();
        Graph graph2 = pnr.load(testFilename, nr2);
        PajekLabels pl = new PajekLabels();
        Indexer id2 = Indexer.getIndexer(graph2);
        VertexLocationFunction vld2 = (VertexLocationFunction)graph2.getUserDatum(PajekNetReader.LOCATIONS);
        
//        vld2 = VertexLocationUtils.scale(vld2, 100, 200);
        
        assertEquals(graph1.numVertices(), graph2.numVertices());
        assertEquals(graph1.numEdges(), graph2.numEdges());
        
        // test vertex labels and locations
        for (int i = 0; i < graph1.numVertices(); i++)
        {
            Vertex v1 = (Vertex)id.getVertex(i);
            Vertex v2 = (Vertex)id2.getVertex(i);
            assertEquals(gl.getLabel(v1), pl.getLabel(v2));
            assertEquals(vld.getLocation(v1), vld2.getLocation(v2));
        }
        
        // test edge weights
        for (Iterator e_iter = graph2.getEdges().iterator(); e_iter.hasNext(); )
        {
            Edge e2 = (Edge)e_iter.next();
            Pair endpoints = e2.getEndpoints();
            Vertex v1_2 = (Vertex)endpoints.getFirst();
            Vertex v2_2 = (Vertex)endpoints.getSecond();
            Vertex v1_1 = (Vertex)id.getVertex(id2.getIndex(v1_2));
            Vertex v2_1 = (Vertex)id.getVertex(id2.getIndex(v2_2));
            Edge e1 = v1_1.findEdge(v2_1);
            assertNotNull(e1);
            assertEquals(nr.getNumber(e1).floatValue(), nr2.getNumber(e2).floatValue(), 0.0001);
        }

        pnw.save(graph2, testFilename2, pl, nr2, vld2);

        compareIndexedGraphs(graph1, graph2);

        pnr.setGetLocations(false);
        Graph graph3 = pnr.load(testFilename2);

        compareIndexedGraphs(graph2, graph3);

        File file1 = new File(testFilename);
        File file2 = new File(testFilename2);

        Assert.assertTrue(file1.length() == file2.length());
        file1.delete();
        file2.delete();
        
    }
    
    /**
     * Tests to see whether these two graphs are structurally equivalent, based
     * on the connectivity of the vertices with matching indices in each graph.
     * Assumes a 0-based index. 
     * 
     * @param g1
     * @param g2
     * @return
     */
    private void compareIndexedGraphs(Graph g1, Graph g2)
    {
        int n1 = g1.numVertices();
        int n2 = g2.numVertices();

        assertEquals(n1, n2);

        assertEquals(g1.numEdges(), g2.numEdges());

        Indexer id1 = Indexer.getIndexer(g1);
        Indexer id2 = Indexer.getIndexer(g2);

        for (int i = 0; i < n1; i++)
        {
            Vertex v1 = (Vertex) id1.getVertex(i);
            Vertex v2 = (Vertex) id2.getVertex(i);
            assertNotNull(v1);
            assertNotNull(v2);
            
            checkSets(v1.getPredecessors(), v2.getPredecessors(), id1, id2);
            checkSets(v1.getSuccessors(), v2.getSuccessors(), id1, id2);
        }
    }

    private void checkSets(Set s1, Set s2, Indexer id1, Indexer id2)
    {
        for (Iterator p_iter = s1.iterator(); p_iter.hasNext();)
        {
            Vertex u = (Vertex) p_iter.next();
            int j = id1.getIndex(u);
            assertTrue(s2.contains(id2.getVertex(j)));
        }
    }

    private class GreekLabels implements VertexStringer
    {
        private Indexer id; 
        
        public GreekLabels(Indexer id)
        {
            this.id = id;
        }
        
        /**
         * @see edu.uci.ics.jung.graph.decorators.VertexStringer#getLabel(ArchetypeVertex)
         */
        public String getLabel(ArchetypeVertex v)
        {
            return vertex_labels[id.getIndex(v)];
        }
        
    }
    
    private class PajekLabels implements VertexStringer
    {
        public String getLabel(ArchetypeVertex v)
        {
            return (String)(v.getUserDatum(PajekNetReader.LABEL));
        }
    }
    
    private class NumberReader implements NumberEdgeValue
    {
        private Map edge_weights;
        
        public NumberReader()
        {
            edge_weights = new HashMap();
        }
        
        public Number getNumber(ArchetypeEdge e)
        {
            return (Float)edge_weights.get(e);
        }

        /* (non-Javadoc)
         * @see edu.uci.ics.jung.graph.decorators.NumberEdgeValue#setNumber(edu.uci.ics.jung.graph.ArchetypeEdge, java.lang.Number)
         */
        public void setNumber(ArchetypeEdge e, Number n)
        {
            edge_weights.put(e, n);
        }
    }
    
}
