/*
 * Created on Feb 8, 2004
 */
package test.edu.uci.ics.jung.algorithms.blockmodel;

import java.util.Arrays;
import java.util.Set;

import junit.framework.TestCase;
import edu.uci.ics.jung.algorithms.blockmodel.*;
import edu.uci.ics.jung.graph.impl.BipartiteEdge;
import edu.uci.ics.jung.graph.impl.BipartiteGraph;
import edu.uci.ics.jung.graph.impl.BipartiteVertex;

/**
 * created Feb 8, 2004
 * @author danyelf
 */
public class TestBipartiteEquivalent extends TestCase {
	BipartiteGraph bpg;
	BipartiteVertex[] aa, mm;
	BipartiteVertex outsider;
	
	public void setUp() {
		bpg = new BipartiteGraph();
		aa = new BipartiteVertex[4];
		mm = new BipartiteVertex[2];

		for (int i = 0; i < mm.length; i++) {
			mm[i] = bpg.addVertex(new BipartiteVertex(), BipartiteGraph.CLASSB);			
		}
		for (int i = 0; i < aa.length; i++) {
			aa[i] = bpg.addVertex(new BipartiteVertex(), BipartiteGraph.CLASSA);
			bpg.addBipartiteEdge( new BipartiteEdge( aa[i], mm[0] ) );
			bpg.addBipartiteEdge( new BipartiteEdge( aa[i], mm[1] ) );
		}
	}

	public void testBipartiteEquivalentSimple() {
		// the graph is K(4,2)
		// all AA should be identical, all MM should be

		StructurallyEquivalent se = StructurallyEquivalent.getInstance();
		EquivalenceRelation er = se.getEquivalences(bpg);
		Set s0 = er.getEquivalenceRelationContaining(mm[0]);
		assertTrue( s0.containsAll( Arrays.asList( mm )));
		assertFalse( s0.containsAll( Arrays.asList( aa )));
		
		Set s = er.getEquivalenceRelationContaining(aa[0]);
		assertTrue( s.containsAll( Arrays.asList( aa )));
	}

	
	public void testBipartiteEquivalent() {
		// all of AA should wind up in one class
		// outsider in another
		// and mm stays as is
		outsider = bpg.addVertex( new BipartiteVertex(), BipartiteGraph.CLASSA);	
		bpg.addBipartiteEdge( new BipartiteEdge( outsider, mm[0]));		

		StructurallyEquivalent se = StructurallyEquivalent.getInstance();
		EquivalenceRelation er = se.getEquivalences(bpg);
		assertTrue( er.getSingletonVertices().contains( mm[0]));
		assertTrue( er.getSingletonVertices().contains( mm[1]));
		assertTrue( er.getSingletonVertices().contains( outsider ));
		
		Set s = er.getEquivalenceRelationContaining(aa[0]);
		assertTrue( s.containsAll( Arrays.asList( aa )));
	}
	
	public void testCompressingBiparite() {
		outsider = bpg.addVertex( new BipartiteVertex(), BipartiteGraph.CLASSA);	
		bpg.addBipartiteEdge( new BipartiteEdge( outsider, mm[0]));		

		StructurallyEquivalent se = StructurallyEquivalent.getInstance();
		EquivalenceRelation er = se.getEquivalences(bpg);

		BipartiteGraphCollapser collapser = new BipartiteGraphCollapser();
		BipartiteGraph bpgNew = (BipartiteGraph) collapser.getCollapsedGraph(er);
		
		assertEquals( bpgNew.getVertices().size(), bpg.getVertices().size() - aa.length + 1);		
	}

}
