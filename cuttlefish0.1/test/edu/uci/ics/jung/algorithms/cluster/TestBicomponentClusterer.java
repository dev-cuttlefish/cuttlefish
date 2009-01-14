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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.cluster.BicomponentClusterer;
import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.SimpleUndirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;


/**
 * @author Scott White
 */
public class TestBicomponentClusterer extends TestCase {
	public static Test suite() {
		return new TestSuite(TestBicomponentClusterer.class);
	}

	protected void setUp() {

	}

    public void testExtract0() throws UniqueLabelException
    {
        Graph graph = new UndirectedSparseGraph();
        Vertex[] v = {new SimpleUndirectedSparseVertex()};
        graph.addVertex(v[0]);
        StringLabeller sl = StringLabeller.getLabeller(graph);
        sl.setLabel(v[0], "0");
        
        Set[] c = {new HashSet()};
        
        c[0].add(v[0]);
     
        testComponents(graph, v, c, sl);
    }

    public void testExtractEdge() throws UniqueLabelException
    {
        Graph graph = new UndirectedSparseGraph();
        Vertex[] v = {new SimpleUndirectedSparseVertex(), 
                new SimpleUndirectedSparseVertex()};
        graph.addVertex(v[0]);
        graph.addVertex(v[1]);
        graph.addEdge(new UndirectedSparseEdge(v[0], v[1]));
        StringLabeller sl = StringLabeller.getLabeller(graph);
        sl.setLabel(v[0], "0");
        sl.setLabel(v[1], "1");
        
        Set[] c = {new HashSet()};
        
        c[0].add(v[0]);
        c[0].add(v[1]);
     
        testComponents(graph, v, c, sl);
        
    }
    
    public void testExtractV() throws UniqueLabelException
    {
        Graph graph = new UndirectedSparseGraph();
        StringLabeller sl = StringLabeller.getLabeller(graph);
        Vertex[] v = new Vertex[3];
        for (int i = 0; i < 3; i++)
        {
            v[i] = new SimpleUndirectedSparseVertex();
            graph.addVertex(v[i]);
            sl.setLabel(v[i], i + "");
        }

        GraphUtils.addEdge(graph, v[0], v[1]);
        GraphUtils.addEdge(graph, v[0], v[2]);
        
        Set[] c = {new HashSet(), new HashSet()};
              
        c[0].add(v[0]);
        c[0].add(v[1]);
        
        c[1].add(v[0]);
        c[1].add(v[2]);
           
        testComponents(graph, v, c, sl);
       
    }
    
    public void createEdges(Vertex[] v, int[][] edge_array, Graph g)
    {
        for (int k = 0; k < edge_array.length; k++)
        {
            int i = edge_array[k][0];
            int j = edge_array[k][1];
            Vertex v1 = getVertex(v, i, g);
            Vertex v2 = getVertex(v, j, g);
            GraphUtils.addEdge(g, v1, v2);
        }
    }
    
    public Vertex getVertex(Vertex[] v_array, int i, Graph g)
    {
        Vertex v = v_array[i];
        if (v == null)
        {
            v = g.addVertex(new SparseVertex());
            v_array[i] = v;
        }
        return v;
    }
    
	public void testExtract1() {
        Vertex[] v = new Vertex[6];
        int[][] edges1 = {{0,1}, {0,5}, {0,3}, {0,4}, {1,5}, {3,4}, {2,3}};
        Graph graph = new UndirectedSparseGraph();
        createEdges(v, edges1, graph);
        
//		StringBuffer buffer= new StringBuffer();
//        buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
//        buffer.append("<graph edgedefault=\"undirected\">\n");
//        buffer.append("<node id=\"0\"/>\n");
//        buffer.append("<node id=\"1\"/>\n");
//        buffer.append("<node id=\"2\"/>\n");
//        buffer.append("<node id=\"3\"/>\n");
//        buffer.append("<node id=\"4\"/>\n");
//        buffer.append("<node id=\"5\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"1\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"5\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"3\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"4\"/>\n");
//        buffer.append("<edge source=\"1\" target=\"5\"/>\n");
//        buffer.append("<edge source=\"3\" target=\"4\"/>\n");
//        buffer.append("<edge source=\"2\" target=\"3\"/>\n");
//        buffer.append("</graph>\n");
//
//        GraphMLFile graphmlFile = new GraphMLFile();
//        Graph graph = graphmlFile.load(new StringReader(buffer.toString()));

        StringLabeller sl = StringLabeller.getLabeller(graph);
//        Vertex[] v = getVerticesByLabel(graph, sl);
        
        Set[] c = new Set[3];
        for (int i = 0; i < c.length; i++)
            c[i] = new HashSet();
        
        c[0].add(v[0]);
        c[0].add(v[1]);
        c[0].add(v[5]);
        
        c[1].add(v[0]);
        c[1].add(v[3]);
        c[1].add(v[4]);
        
        c[2].add(v[2]);
        c[2].add(v[3]);
        
        testComponents(graph, v, c, sl);
	}
    
    public void testExtract2() {
        Vertex[] v = new Vertex[9];
        int[][] edges1 = {{0,2}, {0,4}, {1,0}, {2,1}, {3,0}, {4,3}, {5,3}, {6,7}, {6,8}, {8,7}};
        Graph graph = new UndirectedSparseGraph();
        createEdges(v, edges1, graph);
        
        
        
//		StringBuffer buffer= new StringBuffer();
//        buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
//        buffer.append("<graph edgedefault=\"undirected\">\n");
//        buffer.append("<node id=\"0\"/>\n");
//        buffer.append("<node id=\"1\"/>\n");
//        buffer.append("<node id=\"2\"/>\n");
//        buffer.append("<node id=\"3\"/>\n");
//        buffer.append("<node id=\"4\"/>\n");
//        buffer.append("<node id=\"5\"/>\n");
//        buffer.append("<node id=\"6\"/>\n");
//        buffer.append("<node id=\"7\"/>\n");
//        buffer.append("<node id=\"8\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"2\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"4\"/>\n");
//        buffer.append("<edge source=\"1\" target=\"0\"/>\n");
//        buffer.append("<edge source=\"2\" target=\"1\"/>\n");
//        buffer.append("<edge source=\"3\" target=\"0\"/>\n");
//        buffer.append("<edge source=\"4\" target=\"3\"/>\n");
//        buffer.append("<edge source=\"5\" target=\"3\"/>\n");
//        buffer.append("<edge source=\"6\" target=\"7\"/>\n");
//        buffer.append("<edge source=\"6\" target=\"8\"/>\n");
//        buffer.append("<edge source=\"8\" target=\"7\"/>\n");
//        buffer.append("</graph>\n");

//        GraphMLFile graphmlFile = new GraphMLFile();
//        Graph graph = graphmlFile.load(new StringReader(buffer.toString()));

        StringLabeller sl = StringLabeller.getLabeller(graph);
//        Vertex[] v = getVerticesByLabel(graph, sl);
        
        Set[] c = new Set[4];
        for (int i = 0; i < c.length; i++)
            c[i] = new HashSet();
        
        c[0].add(v[0]);
        c[0].add(v[1]);
        c[0].add(v[2]);
        
        c[1].add(v[0]);
        c[1].add(v[3]);
        c[1].add(v[4]);
        
        c[2].add(v[5]);
        c[2].add(v[3]);
        
        c[3].add(v[6]);
        c[3].add(v[7]);
        c[3].add(v[8]);

        testComponents(graph, v, c, sl);
	}

    public Vertex[] getVerticesByLabel(Graph graph, StringLabeller sl)
    {
        Vertex[] v = new Vertex[graph.numVertices()];
        for (int i = 0; i < v.length; i++)
        {
            Vertex vert = sl.getVertex(i + "");
            assertNotNull(v);
            v[i] = vert;
        }
        
        return v;
    }

    public void testComponents(Graph graph, Vertex[] vertices, Set[] c, StringLabeller sl)
    {
        BicomponentClusterer finder = new BicomponentClusterer();
        ClusterSet bicomponents = finder.extract(graph);
        
        // check number of components
        assertEquals(bicomponents.size(), c.length);

        // diagnostic; should be commented out for typical unit tests
//        for (int i = 0; i < bicomponents.size(); i++)
//        {
//            System.out.print("Component " + i + ": ");
//            Set bicomponent = bicomponents.getCluster(i);
//            for (Iterator iter = bicomponent.iterator(); iter.hasNext(); )
//            {
//                Vertex w = (Vertex)iter.next();
//                System.out.print(sl.getLabel(w) + " ");
//            }
//            System.out.println();
//        }
//        System.out.println();
        
        // make sure that each set in c[] is found in bicomponents
        boolean found = false;
        for (int i = 0; i < c.length; i++)
        {
            for (int j = 0; j < bicomponents.size(); j++)
                if (bicomponents.getCluster(j).equals(c[i]))
                {
                    found = true;
                    break;
                }
            assertTrue(found);
        }
        
        // make sure that each vertex is represented in >=1 element of bicomponents 
        for (Iterator iter = graph.getVertices().iterator(); iter.hasNext(); )
        {
            Vertex v = (Vertex)iter.next(); 
            assertFalse(bicomponents.getClusters(v).isEmpty());
        }
    }
    
}
