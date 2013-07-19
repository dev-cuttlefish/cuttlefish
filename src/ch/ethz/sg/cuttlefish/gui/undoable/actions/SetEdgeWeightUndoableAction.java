package ch.ethz.sg.cuttlefish.gui.undoable.actions;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.networks.Edge;

public class SetEdgeWeightUndoableAction extends UndoableAction {

	private Edge edge;
	private double oldWeight;
	private double newWeight;

	public SetEdgeWeightUndoableAction(Edge edge, double weight) {
		this.edge = edge;
		this.newWeight = weight;
		this.oldWeight = edge.getWeight();
	}

	@Override
	public void execute() {
		edge.setWeight(newWeight);
	}

	@Override
	public void revert() {
		edge.setWeight(oldWeight);
	}

}
