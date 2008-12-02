/*
 * Created on Jan 28, 2004
 */
package test.edu.uci.ics.jung.algorithms.blockmodel;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import edu.uci.ics.jung.algorithms.blockmodel.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;

/**
 * created Jan 28, 2004
 * 
 * @author danyelf
 */
public class TestFullyEquivalent extends TestCase
{

	public void testFullyEquivalent() throws UniqueLabelException
	{
		// in the graph A -> B, A -> C, we should find out that B and C are
		// equivalent

		UndirectedGraph g = new UndirectedSparseGraph();
		Vertex a, b, c, d;
		g.addVertex(a = new SparseVertex());
		g.addVertex(b = new SparseVertex());
		g.addVertex(c = new SparseVertex());
		g.addVertex(d = new SparseVertex());

		StringLabeller sl = StringLabeller.getLabeller(g);

		sl.setLabel(a, "A");
		sl.setLabel(b, "B");
		sl.setLabel(c, "C");
		sl.setLabel(d, "D");

		g.addEdge(new UndirectedSparseEdge(a, b));
		g.addEdge(new UndirectedSparseEdge(a, c));
		g.addEdge(new UndirectedSparseEdge(a, d));

		StructurallyEquivalent fe = StructurallyEquivalentII.getInstance();
//		Set pairs = 
            fe.checkEquivalent(g);

		EquivalenceRelation classes = fe.getEquivalences(g);
		assertTrue(classes.numRelations() == 1);
		Set s = (Set) classes.getAllEquivalences().next();
		assertTrue(s.contains(b));
		assertTrue(s.contains(c));
		assertTrue(s.contains(d));
		assertTrue(s.size() == 3);

	}

	public void testFullyEquivalent2() throws UniqueLabelException
	{
		// in the graph A -> B, A -> C, we should find out that B and C are
		// equivalent

		UndirectedGraph g = new UndirectedSparseGraph();
		Vertex a, b, c, d, e;
		g.addVertex(a = new SparseVertex());
		g.addVertex(b = new SparseVertex());
		g.addVertex(c = new SparseVertex());
		g.addVertex(d = new SparseVertex());
		g.addVertex(e = new SparseVertex());

		StringLabeller sl = StringLabeller.getLabeller(g);

		sl.setLabel(a, "A");
		sl.setLabel(b, "B");
		sl.setLabel(c, "C");
		sl.setLabel(d, "D");
		sl.setLabel(e, "E");

		g.addEdge(new UndirectedSparseEdge(a, c));
		g.addEdge(new UndirectedSparseEdge(a, d));
		g.addEdge(new UndirectedSparseEdge(b, c));
		g.addEdge(new UndirectedSparseEdge(b, d));
		g.addEdge(new UndirectedSparseEdge(c, e));
		g.addEdge(new UndirectedSparseEdge(d, e));

		StructurallyEquivalent fe = StructurallyEquivalentII.getInstance();
		EquivalenceRelation pairs = fe.getEquivalences(g);

		assertTrue(pairs.numRelations() == 2);
		for (Iterator iter = pairs.getAllEquivalences(); iter.hasNext();)
		{
			Set s = (Set) iter.next();
//			System.out.println(s);
//			System.out.println(GraphUtils.printVertices(s, sl));
			if (s.contains(a))
			{
				assertTrue(s.contains(b));
				assertTrue(s.contains(e));
				assertTrue(s.size() == 3);
			}
			if (s.contains(c))
			{
				assertTrue(s.size() == 2);
				assertTrue(s.contains(d));
			}
		}
		assertTrue( pairs.getEquivalenceRelationContaining(a).contains(b));
		assertTrue( pairs.getEquivalenceRelationContaining(b).contains(e));
		assertTrue( pairs.getEquivalenceRelationContaining(d).contains(c));
	}

	public void testFullyEquivalentDirected() throws UniqueLabelException
	{
		// in the graph A -> B, A -> C, we should find out that B and C are
		// equivalent

		Graph g = new DirectedSparseGraph();
		Vertex a, b, c, d, e;
		g.addVertex(a = new SparseVertex());
		g.addVertex(b = new SparseVertex());
		g.addVertex(c = new SparseVertex());
		g.addVertex(d = new SparseVertex());
		g.addVertex(e = new SparseVertex());

		StringLabeller sl = StringLabeller.getLabeller(g);

		sl.setLabel(a, "A");
		sl.setLabel(b, "B");
		sl.setLabel(c, "C");
		sl.setLabel(d, "D");
		sl.setLabel(e, "E");

		g.addEdge(new DirectedSparseEdge(a, c));
		g.addEdge(new DirectedSparseEdge(a, d));
		g.addEdge(new DirectedSparseEdge(b, c));
		g.addEdge(new DirectedSparseEdge(b, d));
		g.addEdge(new DirectedSparseEdge(c, e));
		g.addEdge(new DirectedSparseEdge(d, e));

		StructurallyEquivalent fe = StructurallyEquivalentII.getInstance();
		EquivalenceRelation pairs = fe.getEquivalences(g);

		assertTrue(pairs.numRelations() == 2);
		for (Iterator iter = pairs.getAllEquivalences(); iter.hasNext();)
		{
			Set s = (Set) iter.next();
			if (s.contains(a))
			{
				assertTrue(s.contains(b));
				assertTrue(s.size() == 2);
			}
			if (s.contains(c))
			{
				assertTrue(s.size() == 2);
				assertTrue(s.contains(d));
			}
		}
		assertTrue( pairs.getEquivalenceRelationContaining(a).contains(b));
		assertTrue( pairs.getEquivalenceRelationContaining(c).contains(d));
		assertTrue( pairs.getSingletonVertices().contains(e) );
	}

	public void testFullyEquivalentDirectedSelfLoop() throws UniqueLabelException
	{
		// in the graph A -> B, A -> C, we should find out that B and C are
		// equivalent

		Graph g = new DirectedSparseGraph();
		Vertex a, b, c, d, e;
		g.addVertex(a = new SparseVertex());
		g.addVertex(b = new SparseVertex());
		g.addVertex(c = new SparseVertex());
		g.addVertex(d = new SparseVertex());
		g.addVertex(e = new SparseVertex());

		StringLabeller sl = StringLabeller.getLabeller(g);

		sl.setLabel(a, "A");
		sl.setLabel(b, "B");
		sl.setLabel(c, "C");
		sl.setLabel(d, "D");
		sl.setLabel(e, "E");

		g.addEdge(new DirectedSparseEdge(a, c));
		g.addEdge(new DirectedSparseEdge(a, d));
		g.addEdge(new DirectedSparseEdge(b, c));
		g.addEdge(new DirectedSparseEdge(b, d));
		g.addEdge(new DirectedSparseEdge(c, e));
		g.addEdge(new DirectedSparseEdge(d, e));
		// this should BREAK Structural equivalence for (c,d)
		g.addEdge(new DirectedSparseEdge(d, d));

		StructurallyEquivalent fe =StructurallyEquivalentII.getInstance();
		EquivalenceRelation pairs = fe.getEquivalences(g);

		assertTrue("D breaks it ", pairs.numRelations() == 1);
		for (Iterator iter = pairs.getAllEquivalences(); iter.hasNext();)
		{
			Set s = (Set) iter.next();
			if (s.contains(a))
			{
				assertTrue(s.contains(b));
				assertTrue(s.size() == 2);
			}
		}
		assertTrue( pairs.getEquivalenceRelationContaining(a).contains(b));
		assertTrue( pairs.getSingletonVertices().contains(e) );
		
		g.addEdge(new DirectedSparseEdge(c, c));

		pairs = fe.getEquivalences(g);
		assertTrue("C also is self-loop", pairs.numRelations() == 2);
		
	}
	
	
}
