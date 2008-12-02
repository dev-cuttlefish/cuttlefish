/*
 * Created on Mar 22, 2004
 */
package test.edu.uci.ics.jung.graph.predicates;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.predicates.CliqueGraphPredicate;

/**
 * @author danyelf
 */
public class CliquePredicateTest extends TestCase {

    public void testOneVertex() {
        Graph g = new DirectedSparseGraph();
        Vertex v1 = g.addVertex(new SparseVertex());
        CliqueGraphPredicate icp = CliqueGraphPredicate.getInstance();
        assertTrue(icp.evaluate(g));
        g.addEdge(new DirectedSparseEdge(v1, v1));
        assertTrue(icp.evaluate(g));
    }

    public Graph makeKClique(int k, boolean selfLoop) {
        Graph g = new DirectedSparseGraph();
        Vertex[] vs = new Vertex[k];
        for (int i = 0; i < vs.length; i++) {
            vs[i] = g.addVertex(new SparseVertex());
        }
        for (int i = 0; i < vs.length; i++) {
            for (int j = i; j < vs.length; j++) {
                if (!selfLoop && i == j) continue;
                g.addEdge(new DirectedSparseEdge(vs[i], vs[j]));
            }
        }
        return g;
    }

    public void test3Vertices() {
        Graph g = makeKClique(3, true);
        assertTrue(CliqueGraphPredicate.getInstance().evaluate(g));
        g = makeKClique(3, false);
        assertTrue(CliqueGraphPredicate.getInstance().evaluate(g));
        g.addVertex(new SparseVertex());
        assertFalse(CliqueGraphPredicate.getInstance().evaluate(g));
    }

    public void test7Vertices() {
        Graph g = makeKClique(7, true);
        assertTrue(CliqueGraphPredicate.getInstance().evaluate(g));
        g = makeKClique(7, false);
        assertTrue(CliqueGraphPredicate.getInstance().evaluate(g));
        g.addVertex(new SparseVertex());
        assertFalse(CliqueGraphPredicate.getInstance().evaluate(g));
    }

    public void test9Vertices() {
        Graph g = makeKClique(9, true);
        assertTrue(CliqueGraphPredicate.getInstance().evaluate(g));
        g = makeKClique(9, false);
        assertTrue(CliqueGraphPredicate.getInstance().evaluate(g));
        g.addVertex(new SparseVertex());
        assertFalse(CliqueGraphPredicate.getInstance().evaluate(g));
    }

}
