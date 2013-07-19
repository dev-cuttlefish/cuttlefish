package ch.ethz.sg.cuttlefish.gui.undoable;

public abstract class UndoableAction {

	public abstract void execute();

	public abstract void revert();

	public Object getResult() {
		return null;
	}

}
