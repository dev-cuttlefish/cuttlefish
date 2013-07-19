package ch.ethz.sg.cuttlefish.gui2.undoable;

public abstract class UndoableAction {

	public abstract void execute();

	public abstract void revert();

	public Object getResult() {
		return null;
	}

}
