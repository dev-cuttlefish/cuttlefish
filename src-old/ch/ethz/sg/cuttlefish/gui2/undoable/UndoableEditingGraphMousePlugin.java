package ch.ethz.sg.cuttlefish.gui2.undoable;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Factory;

import ch.ethz.sg.cuttlefish.gui2.undoable.actions.CreateEdgeUndoableAction;
import ch.ethz.sg.cuttlefish.gui2.undoable.actions.CreateVertexUndoableAction;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingGraphMousePlugin;

public class UndoableEditingGraphMousePlugin<V, E> extends EditingGraphMousePlugin<V, E> {

	private boolean creatingAnEdge = false;
	private EdgeType edgeType = EdgeType.UNDIRECTED;
	private UndoableAction action = null;

	public UndoableEditingGraphMousePlugin(Factory<V> vertexFactory, Factory<E> edgeFactory, BrowsableNetwork network) {
		super(vertexFactory, edgeFactory);
	}

	/**
     * If the mouse is pressed in an empty area, create a new vertex there.
     * If the mouse is pressed on an existing vertex, prepare to create
     * an edge from that vertex to another
     */
	@SuppressWarnings("unchecked")
	@Override
	public void mousePressed(MouseEvent e) {
		if (checkModifiers(e)) {
			final VisualizationViewer<Vertex, Edge> vv = (VisualizationViewer<Vertex, Edge>) e.getSource();
			final Point2D p = e.getPoint();
			GraphElementAccessor<Vertex, Edge> pickSupport = vv.getPickSupport();

			if (pickSupport != null) {
				final Vertex vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());

				if (vertex != null) { // get ready to make an edge
					creatingAnEdge = true;
					Graph<Vertex, Edge> graph = vv.getModel().getGraphLayout().getGraph();
					edgeType = (graph instanceof DirectedGraph) ? EdgeType.DIRECTED : EdgeType.UNDIRECTED;
					if ((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0 && graph instanceof UndirectedGraph == false) {
						edgeType = EdgeType.DIRECTED;
					}

					super.mousePressed(e);

				} else { // make a new vertex
					creatingAnEdge = false;
					Vertex newVertex = (Vertex) vertexFactory.create();
					action = new CreateVertexUndoableAction(vv.getGraphLayout(), newVertex, vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p));
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		}

	}

	/**
     * If startVertex is non-null, and the mouse is released over an
     * existing vertex, create an undirected edge from startVertex to
     * the vertex under the mouse pointer. If shift was also pressed,
     * create a directed edge instead.
     */
	@SuppressWarnings("unchecked")
	@Override
	public void mouseReleased(MouseEvent e) {
		
		if (checkModifiers(e)) {
			final VisualizationViewer<Vertex, Edge> vv = (VisualizationViewer<Vertex, Edge>) e.getSource();
			final Point2D p = e.getPoint();
			Layout<Vertex, Edge> layout = vv.getGraphLayout();
			GraphElementAccessor<Vertex, Edge> pickSupport = vv.getPickSupport();
			
			if (pickSupport != null) {
				if (creatingAnEdge) {
					if (startVertex != null) {
						Vertex from = (Vertex) startVertex;
						Vertex to = pickSupport.getVertex(layout, p.getX(), p.getY());

						// Create the new edge
						super.mouseReleased(e);

						// Find the new edge and wrap it in an UndoableAction
						Edge newEdge = layout.getGraph().findEdge(from, to);
						action = new CreateEdgeUndoableAction(layout.getGraph(), newEdge, from, to, edgeType);
						UndoableControl.getController().actionExecuted(action);
					}
				}
			}
		}
	}
}
