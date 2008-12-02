package test.edu.uci.ics.jung.graph.event;

import java.util.Iterator;
import java.util.LinkedList;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Element;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.graph.event.GraphEventType;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author Scott White and Danyel Fisher
 */
public class TestGraphEventListener extends TestCase {
	Graph mGraph;
	private final static String LABEL = "LABEL";

	public void testSingleEvents() throws InterruptedException {
		SimpleEventListener listener = new SimpleEventListener();
		EventConsistencyChecker listener2 = new EventConsistencyChecker();
		Graph graph = new UndirectedSparseGraph();
		graph.addListener(listener, GraphEventType.ALL_SINGLE_EVENTS);
		graph.addListener(listener2, GraphEventType.ALL_SINGLE_EVENTS);

//		NoisyListener l1 = new NoisyListener();
//		graph.addListener(l1, GraphEventType.ALL_SINGLE_EVENTS);
		
		Vertex v1, v2, v3;

		graph.addVertex(v1 = new SparseVertex());
		v1.setUserDatum(LABEL, "1", UserData.REMOVE);
		expect(listener, GraphEventType.VERTEX_ADDITION, v1, true);

		graph.addVertex(v2 = new SparseVertex());
		v2.setUserDatum(LABEL, "2", UserData.REMOVE);
		expect(listener, GraphEventType.VERTEX_ADDITION, v2, true);

		graph.addVertex(v3 = new SparseVertex());
		v3.setUserDatum(LABEL, "3", UserData.REMOVE);
		expect(listener, GraphEventType.VERTEX_ADDITION, v3, true);

		Edge e1, e2, e3;

		graph.addEdge(e1 = new UndirectedSparseEdge(v1, v2));
		e1.setUserDatum(LABEL, "1->2", UserData.REMOVE);
		expect(listener, GraphEventType.EDGE_ADDITION, e1, true);

		graph.addEdge(e2 = new UndirectedSparseEdge(v1, v3));
		e2.setUserDatum(LABEL, "1->3", UserData.REMOVE);
		expect(listener, GraphEventType.EDGE_ADDITION, e2, true);

		graph.addEdge(e3 = new UndirectedSparseEdge(v2, v3));
		e3.setUserDatum(LABEL, "2->3", UserData.REMOVE);
		expect(listener, GraphEventType.EDGE_ADDITION, e3, true);

		graph.removeVertex(v1);
		expect(listener, GraphEventType.EDGE_REMOVAL, null, false);
		expect(listener, GraphEventType.EDGE_REMOVAL, null, false);
		expect(listener, GraphEventType.VERTEX_REMOVAL, v1, true);
		graph.removeAllVertices();
		expect(listener, GraphEventType.EDGE_REMOVAL, e3, false);
		expect(listener, GraphEventType.VERTEX_REMOVAL, null, false);
		expect(listener, GraphEventType.VERTEX_REMOVAL, null, true);
		assertTrue(graph.getVertices().size() == 0);
	}

	public void testMonoListener() throws InterruptedException {
		SimpleEventListener vertex_add = new SimpleEventListener();
		SimpleEventListener edge_and_vertex_add = new SimpleEventListener();
		SimpleEventListener all_events = new SimpleEventListener();
		SimpleEventListener all_events2 = new SimpleEventListener();
		Graph graph = new UndirectedSparseGraph();
		// listens only to ADD_EDGE
		graph.addListener(vertex_add, GraphEventType.VERTEX_ADDITION);

		// listens to ADD VERTEX and ADD EDGE
		graph.addListener(edge_and_vertex_add, GraphEventType.VERTEX_ADDITION);
		graph.addListener(edge_and_vertex_add, GraphEventType.EDGE_ADDITION);

		graph.addListener(all_events, GraphEventType.ALL_SINGLE_EVENTS);

		graph.addListener(all_events2, GraphEventType.EDGE_ADDITION);
		graph.addListener(all_events2, GraphEventType.EDGE_REMOVAL);
		graph.addListener(all_events2, GraphEventType.VERTEX_ADDITION);
		graph.addListener(all_events2, GraphEventType.VERTEX_REMOVAL);

		Vertex v1;
		graph.addVertex(v1 = new SparseVertex());
		expect(vertex_add, GraphEventType.VERTEX_ADDITION, v1, true);
		expect(edge_and_vertex_add, GraphEventType.VERTEX_ADDITION, v1, true);
		expect(all_events, GraphEventType.VERTEX_ADDITION, v1, true);
		expect(all_events2, GraphEventType.VERTEX_ADDITION, v1, true);

		Vertex v2;
		graph.addVertex(v2 = new SparseVertex());
		expect(vertex_add, GraphEventType.VERTEX_ADDITION, v2, true);
		expect(edge_and_vertex_add, GraphEventType.VERTEX_ADDITION, v2, true);
		expect(all_events, GraphEventType.VERTEX_ADDITION, v2, true);
		expect(all_events2, GraphEventType.VERTEX_ADDITION, v2, true);

		Edge e1;
		graph.addEdge(e1 = new UndirectedSparseEdge(v1, v2));
		assertTrue(vertex_add.events.isEmpty());
		expect(edge_and_vertex_add, GraphEventType.EDGE_ADDITION, e1, true);
		expect(all_events, GraphEventType.EDGE_ADDITION, e1, true);
		expect(all_events2, GraphEventType.EDGE_ADDITION, e1, true);

		graph.removeAllVertices();
		assertTrue(vertex_add.events.isEmpty());
		assertTrue(edge_and_vertex_add.events.isEmpty());
		expect(all_events, GraphEventType.EDGE_REMOVAL, null, false);
		expect(all_events2, GraphEventType.EDGE_REMOVAL, null, false);
		expect(all_events, GraphEventType.VERTEX_REMOVAL, null, false);
		expect(all_events2, GraphEventType.VERTEX_REMOVAL, null, false);
		expect(all_events, GraphEventType.VERTEX_REMOVAL, null, true);
		expect(all_events2, GraphEventType.VERTEX_REMOVAL, null, true);

	}

	/**
	 * @param listener
	 * @param addition
	 */
	private void expect(
		SimpleEventListener sel,
		GraphEventType type,
		Element udc,
		boolean queueIsEmpty)
		throws InterruptedException {

		assertFalse( "No events were generated!", sel.events.isEmpty());
		assertFalse( sel.eventTypes.isEmpty() );
		
		assertEquals("The event is of the wrong type!", sel.eventTypes.removeFirst(), type);

		GraphEvent ge = (GraphEvent) sel.events.removeFirst();

		assertEquals(sel.eventTypes.isEmpty(), queueIsEmpty);
		assertEquals(sel.events.isEmpty(), queueIsEmpty);

		if (udc == null)
			return;

		assertSame(ge.getGraphElement(), udc);

	}

	public class EventConsistencyChecker implements GraphEventListener {

		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#vertexAdded(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void vertexAdded(GraphEvent event) {
			Vertex v = (Vertex) event.getGraphElement();
			// the vertex should belong to this graph
			assertTrue(v.getGraph() == event.getGraph());
			// it's new: no adjacent edges
			assertTrue(v.getIncidentEdges().size() == 0);
			assertTrue(event.getGraph().getVertices().contains(v));
		}

		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#vertexRemoved(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void vertexRemoved(GraphEvent event) {
			Vertex v = (Vertex) event.getGraphElement();
			// the vertex should NOT belong to this graph
			assertSame(v.getGraph(), null);
			// it's being gotten rid of: no adjacent edges
			assertTrue(v.getIncidentEdges().size() == 0);
			assertFalse(event.getGraph().getVertices().contains(v));
		}

		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#edgeAdded(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void edgeAdded(GraphEvent event) {
			Edge e = (Edge) event.getGraphElement();
			// the edge should belong to this graph
			assertSame(e.getGraph(), event.getGraph());
			// it's being gotten rid of: no adjacent edges
			assertEquals(e.getIncidentVertices().size(), 2);
			assertTrue(event.getGraph().getEdges().contains(e));
		}

		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#edgeRemoved(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void edgeRemoved(GraphEvent event) {
			Edge e = (Edge) event.getGraphElement();
			// the edge should NOT belong to this graph
			assertSame(e.getGraph(), null);
			assertFalse(event.getGraph().getEdges().contains(e));
			// but both its vertices should still exist
			for (Iterator iter = e.getIncidentVertices().iterator();
				iter.hasNext();
				) {
				Vertex v = (Vertex) iter.next();
				assertSame(v.getGraph(), event.getGraph());
				assertFalse(v.getIncidentEdges().contains(e));
			}
		}

	}

	class NoisyListener implements GraphEventListener {

		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#vertexAdded(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void vertexAdded(GraphEvent event) {
			System.out.println("Vertex added!");
		}

		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#vertexRemoved(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void vertexRemoved(GraphEvent event) {
			System.out.println("Vertex removed!");
		}

		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#edgeAdded(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void edgeAdded(GraphEvent event) {
			System.out.println("edge added!");
		}

		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#edgeRemoved(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void edgeRemoved(GraphEvent event) {
			System.out.println("edge removed!");
		}
		
	}
	
	class SimpleEventListener implements GraphEventListener {
		public LinkedList eventTypes;
		public LinkedList events;

		public SimpleEventListener() {
			eventTypes = new LinkedList();
			events = new LinkedList();
		}
        
		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#edgeAdded(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void edgeAdded(GraphEvent event) {
			eventTypes.addLast(GraphEventType.EDGE_ADDITION);
			events.addLast(event);

		}

		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#edgeRemoved(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void edgeRemoved(GraphEvent event) {
			eventTypes.addLast(GraphEventType.EDGE_REMOVAL);
			events.addLast(event);
		}

		/**
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#vertexAdded(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void vertexAdded(GraphEvent event) {
			eventTypes.addLast(GraphEventType.VERTEX_ADDITION);
			events.addLast(event);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.uci.ics.jung.graph.event.GraphEventListener#vertexRemoved(edu.uci.ics.jung.graph.event.GraphEvent)
		 */
		public void vertexRemoved(GraphEvent event) {
			eventTypes.addLast(GraphEventType.VERTEX_REMOVAL);
			events.addLast(event);
		}

	}

}
