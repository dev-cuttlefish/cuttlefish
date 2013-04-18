package ch.ethz.sg.cuttlefish.gui2.undoable;

import org.apache.commons.collections15.Factory;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;

public class UndoableEditingModalGraphMouse<V, E> extends EditingModalGraphMouse<V, E> {

	protected UndoableEditingGraphMousePlugin<V, E> undoableEditingPlugin;

	public UndoableEditingModalGraphMouse(RenderContext<V, E> rc, Factory<V> vertexFactory, Factory<E> edgeFactory, BrowsableNetwork network) {
		super(rc, vertexFactory, edgeFactory);
		undoableEditingPlugin = new UndoableEditingGraphMousePlugin<V, E>(vertexFactory, edgeFactory, network);
	}

	@Override
	protected void loadPlugins() {
		super.loadPlugins();
		remove(editingPlugin);
	}

	@Override
	protected void setPickingMode() {
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(undoableEditingPlugin);
		remove(annotatingPlugin);
		add(pickingPlugin);
		add(animatedPickingPlugin);
		add(labelEditingPlugin);
		add(popupEditingPlugin);
	}

	@Override
	protected void setTransformingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(undoableEditingPlugin);
		remove(annotatingPlugin);
		add(translatingPlugin);
		add(rotatingPlugin);
		add(shearingPlugin);
		add(labelEditingPlugin);
		add(popupEditingPlugin);
	}

	@Override
	protected void setEditingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(labelEditingPlugin);
		remove(annotatingPlugin);
		add(undoableEditingPlugin);
		add(popupEditingPlugin);
	}

	@Override
	protected void setAnnotatingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(labelEditingPlugin);
		remove(undoableEditingPlugin);
		remove(popupEditingPlugin);
		add(annotatingPlugin);
	}

}
