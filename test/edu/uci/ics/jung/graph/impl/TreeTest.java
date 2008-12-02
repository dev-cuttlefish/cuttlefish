/*
 * Created on May 9, 2004
 */
package test.edu.uci.ics.jung.graph.impl;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseTree;
import edu.uci.ics.jung.graph.impl.SparseVertex;


/**
 * 
 * @author danyelf
 */
public class TreeTest extends TestCase {
    
    public void testTreeCreate() {
        Vertex root = new SparseVertex();
        Vertex l = new SparseVertex();
        Vertex r = new SparseVertex();  
        SparseTree st = new SparseTree( root );
        st.addVertex(l);
        st.addVertex(r);
        assertSame( st.getRoot(), root );
        st.addEdge(new DirectedSparseEdge(root, l ));
        st.addEdge(new DirectedSparseEdge(root, r ));
        try {
            st.addEdge( new DirectedSparseEdge( l, r ));
            fail("Only one incoming edge per vertex");
        } catch ( Exception e ) {            
        }
        try {
            st.addEdge( new DirectedSparseEdge( r, root ));
            fail("Edges must go down the tree");
        } catch ( Exception e ) {            
        }
        // islands
        Vertex v1 = st.addVertex( new SparseVertex());
        Vertex v2 = st.addVertex( new SparseVertex());          
        try {
            st.addEdge( new DirectedSparseEdge( v1, v2 ));
            fail("Nasty islands!");            
        } catch (Exception e ) {            
        }
    }

}
