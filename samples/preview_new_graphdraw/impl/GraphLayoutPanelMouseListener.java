/*
 * Created on Dec 9, 2003
 */
package samples.preview_new_graphdraw.impl;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.VisEdge;
import samples.preview_new_graphdraw.VisVertex;
import samples.preview_new_graphdraw.event.ClickEvent;
import samples.preview_new_graphdraw.event.ClickListener;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

/**
 * @author danyelf
 */
public class GraphLayoutPanelMouseListener extends MouseAdapter {

	static interface ClickPolicy {
		boolean shouldCheckVertexDistance();
		boolean shouldCheckEdgeDistance();
		void fireEvents(
			GraphLayoutPanelMouseListener glpml,
			MouseEvent me,
			VisEdge ve,
			double edgeDist,
			VisVertex vv,
			double vertexDistance);
	}

	/** No graph events are fired from this panel */
	public static class NoEventPolicy implements ClickPolicy {

		public String toString() {
			return "no-event-policy";
		}
		public boolean shouldCheckVertexDistance() {
			return false;
		}
		public boolean shouldCheckEdgeDistance() {
			return false;
		}
		public void fireEvents(
			GraphLayoutPanelMouseListener gmpl,
			MouseEvent me,
			VisEdge ve,
			double edgeDist,
			VisVertex vv,
			double vertexDistance) {
			return;
		}
	}

	/** Only vertex events are fired from this panel. If a click isn't
	 * near a vertex, no events are fired.
	 */
	public static class VertexEventPolicy implements ClickPolicy {
		public String toString() {
			return "vertex-event-policy";
		}
		public boolean shouldCheckVertexDistance() {
			return true;
		}
		public boolean shouldCheckEdgeDistance() {
			return false;
		}
		public void fireEvents(
			GraphLayoutPanelMouseListener gmpl,
			MouseEvent me,
			VisEdge ve,
			double edgeDist,
			VisVertex vv,
			double vertexDistance) {
			if (vertexDistance < VERTEX_CLICK_THRESHOLD) {
				gmpl.fireVertexEvent(vv.getVertex(), vertexDistance, me);
			}
		}
	}

	/**
	 *  Only edge events are fired from this panel. If a click isn't
	 * near a edge, no events are fired.
	 */
	public static class EdgeEventPolicy implements ClickPolicy {
		public String toString() {
			return "edge-event-policy";
		}
		public boolean shouldCheckVertexDistance() {
			return false;
		}
		public boolean shouldCheckEdgeDistance() {
			return true;
		}
		public void fireEvents(
			GraphLayoutPanelMouseListener gmpl,
			MouseEvent me,
			VisEdge ve,
			double edgeDist,
			VisVertex vv,
			double vertexDistance) {
			if (edgeDist < EDGE_CLICK_THRESHOLD) {
				gmpl.fireEdgeEvent(ve.getEdge(), edgeDist, me);
			}
		}
	}

	/**
	 * Either an edge event or a vertex event is fired from this panel. 
	 * If the click is close to a vertex, a vertex event is fired;
	 * otherwise, if the click is close to an edge, the edge event.
	 */
	public final static class EdgeAndVertexPolicy implements ClickPolicy {
		public String toString() {
			return "edge-and-vertex-policy";
		}
		public boolean shouldCheckVertexDistance() {
			return true;
		}
		public boolean shouldCheckEdgeDistance() {
			return true;
		}
		public void fireEvents(
			GraphLayoutPanelMouseListener gmpl,
			MouseEvent me,
			VisEdge ve,
			double edgeDist,
			VisVertex vv,
			double vertexDistance) {
			if (vertexDistance < VERTEX_CLICK_THRESHOLD) {
				gmpl.fireVertexEvent(vv.getVertex(), vertexDistance, me);
			} else {
				if (edgeDist < EDGE_CLICK_THRESHOLD) {
					gmpl.fireEdgeEvent(ve.getEdge(), edgeDist, me);
				}
			}
		}
	}

	/**
	 * Both an edge event or a vertex event is fired from this panel. 
	 * If the click is close to a vertex, a vertex event is fired;
	 * if the click is close to an edge, the edge event. (If both,
	 * then both may be fired: it is up to the developer to choose
	 * a mechansim. For example, some applications will select only
	 * vertices if the user is holding down the control button.)
	 */
	public final static class BothEdgeAndVertexPolicy implements ClickPolicy {
		public String toString() {
			return "both-edge-and-vertex-policy";
		}
		public boolean shouldCheckVertexDistance() {
			return true;
		}
		public boolean shouldCheckEdgeDistance() {
			return true;
		}
		public void fireEvents(
			GraphLayoutPanelMouseListener gmpl,
			MouseEvent me,
			VisEdge ve,
			double edgeDist,
			VisVertex vv,
			double vertexDistance) {
			if (vertexDistance < VERTEX_CLICK_THRESHOLD) {
				gmpl.fireVertexEvent(vv.getVertex(), vertexDistance, me);
            }
			if (edgeDist < EDGE_CLICK_THRESHOLD) {
				gmpl.fireEdgeEvent(ve.getEdge(), edgeDist, me);
			}
		}
	}

	protected final GraphLayoutPanel panel;

	/**
	 * This constant tracks how far a click needs to be before it's too far to
	 * register at all.
	 */
	public static double VERTEX_CLICK_THRESHOLD = 400; // ten pixels!
	public static double EDGE_CLICK_THRESHOLD = 100;

	/**
	 * If the system is set to accept both VERTEX and EDGE clicks, then any
	 * edge click will be just as far from a vertex as a vertex click is.
	 * (After all, each edge terminates in a vertex.) This bias allows the
	 * vertex a little extra radius near it. A value of 2.0 means that a click
	 * can be 2 times as far from the nearest vertex as the nearest edge, and
	 * still be registered.
	 */
	public static double VERTEX_BIAS = 2.0;

	/**
	 * @param panel
	 */
	public GraphLayoutPanelMouseListener(GraphLayoutPanel panel) {
		this.panel = panel;
	}

	protected void fireVertexEvent(
		Vertex v,
		double dist_to_closest_vertex,
		MouseEvent e) {
		ClickEvent vce = new ClickEvent(this, v, dist_to_closest_vertex, e);
		for (Iterator iter = panel.listeners.iterator(); iter.hasNext();) {
			ClickListener cl = (ClickListener) iter.next();
			//			System.out.println("Fire vertex! " + cl);
			cl.vertexClicked(vce);
		}
	}

	protected void fireEdgeEvent(
		Edge e,
		double dist_to_closest_edge,
		MouseEvent me) {
		ClickEvent ece = new ClickEvent(this, e, dist_to_closest_edge, me);
		for (Iterator iter = panel.listeners.iterator(); iter.hasNext();) {
			ClickListener cl = (ClickListener) iter.next();
			//			System.out.println("Fire edge! " + cl );
			cl.edgeClicked(ece);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (panel.listeners == null)
			return;
		if (this.panel.clickPolicy == GraphLayoutPanel.NO_EVENT_POLICY)
			return;
			
		ClickPolicy policy = this.panel.clickPolicy;

		EmittedLayout layout = this.panel.getGraphLayout();

		VisVertex closeVisVertex = null;
		double dist_to_closest_vertex = Double.POSITIVE_INFINITY;

		VisEdge closestVisEdge = null;
		double dist_to_closest_edge = Double.POSITIVE_INFINITY;

		if( policy.shouldCheckVertexDistance() ) {
			Vertex closestVertex =
				layout.getNearestVertex(
					e.getX(),
					e.getY());

			if (closestVertex != null) {
				closeVisVertex = layout.getVisVertex(closestVertex);
				dist_to_closest_vertex = closeVisVertex.getSquareDistance(e.getX(), e.getY());
			}
			
		}

		if( policy.shouldCheckEdgeDistance() ) {
			Edge closestEdge =
				layout.getNearestEdge(
					e.getX(),
					e.getY());

			if (closestEdge != null) {
				closestVisEdge = layout.getVisEdge(closestEdge);
				dist_to_closest_edge = closestVisEdge.getSquareDistance(e.getX(), e.getY());
			}
		}
		
		policy.fireEvents(this, e, closestVisEdge, dist_to_closest_edge, closeVisVertex, dist_to_closest_vertex);
		e.consume();		
	}

}