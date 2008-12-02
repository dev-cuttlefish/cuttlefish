/*
 * Created on Dec 11, 2003
 */
package test.edu.uci.ics.jung.graph.bipartite;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Hyperedge;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.Hypervertex;
import edu.uci.ics.jung.graph.impl.HyperedgeBPG;
import edu.uci.ics.jung.graph.impl.HypergraphBPG;
import edu.uci.ics.jung.graph.impl.HypervertexBPG;

/**
 * 
 * @author danyelf
 */
public class TestHyperVertex extends TestCase {

	Hypergraph hg;
	Hypervertex hv1, hv2, hv3;
	Hyperedge he1, he2;
	
	public void setUp() {
		hg = new HypergraphBPG();
		hg.addVertex(hv1 = new HypervertexBPG());
		hg.addVertex(hv2 = new HypervertexBPG());
		hg.addVertex(hv3 = new HypervertexBPG());
		hg.addEdge(he1 = new HyperedgeBPG());
		hg.addEdge(he2 = new HyperedgeBPG());

		he1.connectVertex( hv1 );

		he2.connectVertex( hv2 );
		he2.connectVertex( hv3 );
	}
	
	public void testCopy() {
		Hypergraph hg2 = (Hypergraph) hg.copy();
		assertEquals(3,  hg2.getVertices().size() );
		assertEquals(2,  hg2.getEdges().size() );
		// ok, let's build the parallel version
		Hypervertex hv1a, hv2a, hv3a;
		hv1a = (Hypervertex) hv1.getEqualVertex( hg2 );
		hv2a = (Hypervertex) hv2.getEqualVertex( hg2 );
		hv3a = (Hypervertex) hv3.getEqualVertex( hg2 );
		
		assertNotNull(hv1a);
		assertNotNull(hv2a);
		assertNotNull(hv3a);
				
		Hyperedge he1a, he2a;
		he1a = (Hyperedge) he1.getEqualEdge(hg2);
		he2a = (Hyperedge) he2.getEqualEdge(hg2);

		assertNotNull(he1a);
		assertNotNull(he2a);

		assertTrue( hv1a.getIncidentEdges().contains( he1a ));
		assertTrue( hv2a.getIncidentEdges().contains( he2a ));
		assertTrue( hv3a.getIncidentEdges().contains( he2a ));		

 		assertEquals( hv1a.degree() ,1 );
		assertEquals( hv2a.degree() ,1 );
		assertEquals( hv3a.degree() ,1 );
		
 		assertTrue( hv2a.isNeighborOf(hv3a));
		assertFalse( hv1a.isNeighborOf(hv2a));
	}
	
	public void testGetNeighbors() {
		assertEquals( hv1.getNeighbors().size() , 0 );
		assertTrue( hv2.getNeighbors().contains( hv3 ));
		assertTrue( hv3.getNeighbors().contains( hv2 ));
	}

	public void testGetIncidentEdges() {
		assertTrue( hv1.getIncidentEdges().contains( he1 ));
		assertTrue( hv2.getIncidentEdges().contains( he2 ));
		assertTrue( hv3.getIncidentEdges().contains( he2 ));		
	}

	public void testDegree() {
		assertEquals( hv1.degree() ,1  );
		assertEquals( hv2.degree() ,1  );
		assertEquals( hv3.degree() ,1  );
	}
	
	public void testIsNeighborOf() {
		assertTrue( hv2.isNeighborOf(hv3));
		assertFalse( hv1.isNeighborOf(hv2));
	}

	public void testIsIncident() {
		assertTrue( hv1.isIncident(he1));
		assertTrue( hv2.isIncident(he2));
		assertFalse(hv1.isIncident(he2));
	}

	public void testNumNeighbors() {
		assertEquals( hv1.numNeighbors() , 0 );
		assertEquals( hv2.numNeighbors() , 1 );
		assertEquals( hv3.numNeighbors() , 1 );
		he1.connectVertex(hv2);
		assertEquals( hv1.numNeighbors() , 1 );
		assertEquals( hv2.numNeighbors() , 2 );
		assertEquals( hv3.numNeighbors() , 1 );
	}

}
