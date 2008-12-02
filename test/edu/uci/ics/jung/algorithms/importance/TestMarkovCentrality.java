/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.algorithms.importance;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.Assert;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.NumericalPrecision;
import edu.uci.ics.jung.algorithms.importance.MarkovCentrality;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Scott White
 */
public class TestMarkovCentrality extends TestCase {

    public static Test suite() {
        return new TestSuite(TestMarkovCentrality.class);
    }

    protected void setUp() {

    }

    public void testSimple() {

        DirectedSparseGraph graph = new DirectedSparseGraph();
 		GraphUtils.addVertices( graph, 4 );
		Indexer id = Indexer.getIndexer( graph );

		GraphUtils.addEdge(graph, (Vertex)id.getVertex(0),(Vertex)id.getVertex(1));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(1),(Vertex)id.getVertex(2));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(3));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(3),(Vertex)id.getVertex(0));
		GraphUtils.addEdge(graph, (Vertex)id.getVertex(2),(Vertex)id.getVertex(1));

        Set priors = new HashSet();
        priors.add(id.getVertex(2));

        MarkovCentrality ranker = new MarkovCentrality(graph,priors,null);
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.setMaximumIterations(500);

        ranker.evaluate();

        Assert.assertTrue(NumericalPrecision.equal(ranker.getRankScore(id.getVertex(0)),0.1764,.001));
        Assert.assertTrue(NumericalPrecision.equal(ranker.getRankScore(id.getVertex(1)),0.3529,.001));
        Assert.assertTrue(NumericalPrecision.equal(ranker.getRankScore(id.getVertex(2)),0.2352,.001));
        Assert.assertTrue(NumericalPrecision.equal(ranker.getRankScore(id.getVertex(3)),0.2352,.001));

    }
}
