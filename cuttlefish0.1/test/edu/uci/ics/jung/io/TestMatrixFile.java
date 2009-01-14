/*
 * Created on Jan 6, 2002
 *
 */
package test.edu.uci.ics.jung.io;
import java.io.File;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.io.MatrixFile;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.UserData;
/**
 * @author Scott
 *
 */
public class TestMatrixFile extends TestCase {
	public static Test suite() {
		return new TestSuite(TestMatrixFile.class);
	}
	public DirectedGraph createSimpleDirectedGraph() {
		DirectedSparseGraph graph1 = new DirectedSparseGraph();
		GraphUtils.addVertices(graph1, 5);
		Indexer id = Indexer.getIndexer(graph1);
		Edge e =
			GraphUtils.addEdge(
				graph1,
				(Vertex) id.getVertex(0),
				(Vertex) id.getVertex(1));
		e.addUserDatum("WEIGHT", new Double(5.0), UserData.REMOVE);
		e =
			GraphUtils.addEdge(
				graph1,
				(Vertex) id.getVertex(0),
				(Vertex) id.getVertex(2));
		e.addUserDatum("WEIGHT", new Double(10.0), UserData.REMOVE);
		e =
			GraphUtils.addEdge(
				graph1,
				(Vertex) id.getVertex(1),
				(Vertex) id.getVertex(2));
		e.addUserDatum("WEIGHT", new Double(3.0), UserData.REMOVE);
		e =
			GraphUtils.addEdge(
				graph1,
				(Vertex) id.getVertex(1),
				(Vertex) id.getVertex(3));
		e.addUserDatum("WEIGHT", new Double(700.0), UserData.REMOVE);
		e =
			GraphUtils.addEdge(
				graph1,
				(Vertex) id.getVertex(1),
				(Vertex) id.getVertex(4));
		e.addUserDatum("WEIGHT", new Double(0.5), UserData.REMOVE);
		e =
			GraphUtils.addEdge(
				graph1,
				(Vertex) id.getVertex(4),
				(Vertex) id.getVertex(3));
		e.addUserDatum("WEIGHT", new Double(5.0), UserData.REMOVE);
		return graph1;
	}
	public void testUnweightedLoadSave() {
		MatrixFile mf = new MatrixFile(null);
		DirectedGraph dg = createSimpleDirectedGraph();
		String filename = "testMatrixLoadSaveUW.mat";
		mf.save(dg, filename);
		Graph g = mf.load(filename);
		Assert.assertEquals(dg.numVertices(), g.numVertices());
		Assert.assertEquals(dg.numEdges(), g.numEdges());
		File file = new File(filename);
		file.delete();
	}
	
	public void testWeightedLoadSave() {
		MatrixFile mf = new MatrixFile("WEIGHT");
		DirectedGraph dg = createSimpleDirectedGraph();
		String filename = "testMatrixLoadSaveW.mat";
		mf.save(dg, filename);
		Graph g = mf.load(filename);
		Assert.assertEquals(dg.numVertices(), g.numVertices());
		Assert.assertEquals(dg.numEdges(), g.numEdges());
		File file = new File(filename);
		file.delete();
	}
}
