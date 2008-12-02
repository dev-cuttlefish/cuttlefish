/*
 * 
 * Created on Oct 30, 2003
 */
package test.edu.uci.ics.jung.algorithms;

import java.util.Iterator;

import junit.framework.TestCase;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import edu.uci.ics.jung.algorithms.GraphMatrixOperations;
import edu.uci.ics.jung.algorithms.MatrixElementOperations;
import edu.uci.ics.jung.algorithms.RealMatrixElementOperations;
import edu.uci.ics.jung.exceptions.FatalException;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.MutableDouble;
import edu.uci.ics.jung.utils.UserData;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class GraphMatrixOperationsTest extends TestCase
{
    private Graph g;
    StringLabeller sl;
    private MatrixElementOperations meo;
    private final static int NUM_VERTICES = 10;
    protected int[][] edges = 
        {{1,2,2}, {1,4,1}, 
         {2,4,3}, {2,5,10},
         {3,1,4}, {3,6,5},
         {4,3,2}, {4,5,2}, {4,6,8}, {4,7,4},
         {5,7,6},
         {7,6,1},
         {8,9,4}, // these three edges define a second connected component
         {9,10,1},
         {10,8,2}};
    
    protected void setUp()
    {
    	g = new DirectedSparseGraph();
        meo = new RealMatrixElementOperations("weight");
        // graph based on Weiss, _Data Structures and Algorithm Analysis_,
        // 1992, p. 292
        GraphUtils.addVertices(g, NUM_VERTICES);
        sl = StringLabeller.getLabeller(g);
        Indexer.newIndexer(g, 1); // create a 1-based index for g
        addEdges(g, edges);        

    }

    public void testMatrixToGraphToMatrixDirected()
    {
        DoubleMatrix2D m = new SparseDoubleMatrix2D(g.numVertices(), g.numVertices());
        for (int i = 0; i < edges.length; i++)
            m.setQuick(edges[i][0] - 1, edges[i][1] - 1, edges[i][2]);
        
        Graph g2 = GraphMatrixOperations.matrixToGraph(m, "weight");
        
        DoubleMatrix2D m2 = GraphMatrixOperations.graphToSparseMatrix(g2, "weight");
        
        assertEquals(m, m2);
    }
    
    public void testMatrixToGraphToMatrixUndirected()
    {
        DoubleMatrix2D m = new SparseDoubleMatrix2D(g.numVertices(), g.numVertices());
        for (int i = 0; i < edges.length; i++)
        {
            m.setQuick(edges[i][0] - 1, edges[i][1] - 1, edges[i][2]);
            m.setQuick(edges[i][1] - 1, edges[i][0] - 1, edges[i][2]);
        }
        
        Graph g2 = GraphMatrixOperations.matrixToGraph(m, "weight");
        
        DoubleMatrix2D m2 = GraphMatrixOperations.graphToSparseMatrix(g2, "weight");
        
        assertEquals(m, m2);
    }

    public void testSquare()
    {
        int[][] g3_edges = 
            {{1,3,2}, {1,4,6}, {1,5,22}, {1,6,8}, {1,7,4},
             {2,3,6}, {2,5,6}, {2,6,24}, {2,7,72},
             {3,2,8}, {3,4,4}, 
             {4,1,8}, {4,6,14}, {4,7,12},
             {5,6,6},
             {8,10,4},
             {9,8,2},
             {10,9,8}
            };
        
        
        Graph g2 = GraphMatrixOperations.square(g, meo);
        
        Graph g3 = new DirectedSparseGraph();
        for (Iterator v_iter = g.getVertices().iterator(); v_iter.hasNext(); )
        {
        	 Vertex v = (Vertex)v_iter.next();
             v.copy(g3);    // establishes equivalence between g's vertices and g3's;
                            // we know that g2's and g's are equivalent, so they're
                            // all mutually equivalent
        }
        addEdges(g3, g3_edges);
        
        // check vertex/edge set sizes
        assertTrue(g2.numVertices() == g3.numVertices());
        assertTrue(g2.numEdges() == g3.numEdges());      
        
        // check vertex sets
        assertEquals(g2.getVertices(), g3.getVertices());
        
        // check for equivalent vertices, edges, and edge weights
        for (int i = 0; i < g3_edges.length; i++)
        {
        	int src_idx = g3_edges[i][0];
            int dst_idx = g3_edges[i][1];
            int g3_weight = g3_edges[i][2];
            
            Vertex g3_src = sl.getVertex(new Integer(src_idx).toString());
            Vertex g3_dst = sl.getVertex(new Integer(dst_idx).toString());
            
            Vertex g2_src = (Vertex)g3_src.getEqualVertex(g2);
            assertNotNull(g2_src);
            Vertex g2_dst = (Vertex)g3_dst.getEqualVertex(g2);
            assertNotNull(g2_dst);
            
            Edge e = g2_src.findEdge(g2_dst); 
            assertNotNull(e);
            // we already know that g3 has a corresponding edge, 
            // because we got it from g3_edges

            int g2_weight = ((MutableDouble)e.getUserDatum("weight")).intValue();

            assertEquals(g2_weight, g3_weight);
        }
        
    }
    
    private void addEdges(Graph graph, int[][] edges)
    {
        Indexer id = Indexer.getIndexer(graph);
        for (int i = 0; i < edges.length; i++)
        {
            int[] edge = edges[i];
            try
			{
				String s1 = new Integer(edge[0]).toString();
				Vertex v1 = sl.getVertex(s1);
				if (v1 == null)
                {                
                    v1 = (Vertex)id.getVertex(edge[0]);
                    sl.setLabel(v1, s1);
                }
                v1 = (Vertex)v1.getEqualVertex(graph);
                
				String s2 = new Integer(edge[1]).toString();
				Vertex v2 = sl.getVertex(s2);
				if (v2 == null)
                { 
                    v2 = (Vertex)id.getVertex(edge[1]);
                    sl.setLabel(v2, s2);
                }
                v2 = (Vertex)v2.getEqualVertex(graph);
                
                Edge e = graph.addEdge(new DirectedSparseEdge(v1, v2));
                if (edge.length > 2)
                    e.addUserDatum("weight", new MutableDouble(edge[2]), UserData.REMOVE);
                }
			catch (UniqueLabelException ule)
			{
                throw new FatalException("Unique label problem: ", ule);
			}
        }
    }

}
