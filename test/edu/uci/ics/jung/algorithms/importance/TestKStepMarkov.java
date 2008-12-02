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

import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.importance.KStepMarkov;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.MutableDouble;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author Scott White
 */
public class TestKStepMarkov extends TestCase {
    public final static String EDGE_WEIGHT = "edu.uci.ics.jung.edge_weight";
	DirectedSparseGraph mGraph;
    double[][] mTransitionMatrix;

    public static Test suite() {
        return new TestSuite(TestKStepMarkov.class);
    }

    protected void setUp()
    {
        mGraph = new DirectedSparseGraph();
        mTransitionMatrix = new double[][]
           {{0.0, 0.5, 0.5},
            {1.0/3.0, 0.0, 2.0/3.0},
            {1.0/3.0, 2.0/3.0, 0.0}};

        for (int i = 0; i < mTransitionMatrix.length; i++)
        	mGraph.addVertex( new SparseVertex( ));
		Indexer id = Indexer.getIndexer( mGraph );

        for (int i = 0; i < mTransitionMatrix.length; i++)
            for (int j = 0; j < mTransitionMatrix[i].length; j++)
            {
                if (mTransitionMatrix[i][j] > 0)
                {
                    createEdge(mGraph, (Vertex)id.getVertex(i),
                        (Vertex)id.getVertex(j), mTransitionMatrix[i][j]);
                }
            }
    }

    private Edge createEdge(Graph G, Vertex v1, Vertex v2, double weight)
    {
        Edge e = GraphUtils.addEdge( G, v1, v2);
        e.addUserDatum(EDGE_WEIGHT, new MutableDouble(weight), UserData.SHARED);
        return e;
    }

    public void testRanker() {

        Set priors = new HashSet();
		Indexer id = Indexer.getIndexer( mGraph );
        priors.add(id.getVertex(1));
        priors.add(id.getVertex(2));
        KStepMarkov ranker = new KStepMarkov(mGraph,priors,2,EDGE_WEIGHT);
        ranker.evaluate();
//        List rankings = ranker.getRankings();
        //System.out.println("New version:");
        //System.out.println(rankings);
    }
}
