package ch.ethz.sg.cuttlefish.gui2.undoable;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

/**
 * Controller of the Undo/Redo operations. The operations are
 * based on a list of actions, the "history", that can be
 * reverted or re-executed.
 *
 */

public class UndoableControl extends Observable {

	private List<UndoableAction> history;
	private ListIterator<UndoableAction> currentAction;

	private static UndoableControl instance = null;

	public static UndoableControl getController() {
		if (instance == null) {
			instance = new UndoableControl();
		}

		return instance;
	}

	public static void resetController() {
		instance.history.clear();
		instance.currentAction = instance.history.listIterator();
		instance.stateChanged();
	}

	private UndoableControl() {
		this.history = new LinkedList<UndoableAction>();
		this.currentAction = this.history.listIterator();
		this.stateChanged();
	}

	public void actionExecuted(UndoableAction action) {
		if (currentAction.hasNext()) {
			history.subList(currentAction.nextIndex(), history.size()).clear();
		}

		history.add(action);
		currentAction = history.listIterator(history.size());
		stateChanged();
	}

	public void undo() {
		currentAction.previous().revert();
		stateChanged();
	}

	public void redo() {
		currentAction.next().execute();
		stateChanged();
	}

	public boolean canUndo() {
		return currentAction.hasPrevious();
	}

	public boolean canRedo() {
		return currentAction.hasNext();
	}

	private void stateChanged() {
		setChanged();
		notifyObservers();
		clearChanged();
	}
}
