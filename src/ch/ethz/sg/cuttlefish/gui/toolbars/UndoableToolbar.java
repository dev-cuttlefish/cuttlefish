package ch.ethz.sg.cuttlefish.gui.toolbars;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.gui.undoable.UndoableControl;

public class UndoableToolbar extends AbstractToolbar implements Observer {

	private static final long serialVersionUID = -7647215282831375986L;
	private JButton undoButton;
	private JButton redoButton;
	private static final Dimension BUTTON_PREFERRED_SIZE = new Dimension(30, 30);

	private static String undoIconFile = "icons/undo.png";
	private static String redoIconFile = "icons/redo.png";

	public UndoableToolbar(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
		UndoableControl.getController().addObserver(this);
	}

	private void initialize() {
		undoButton = new JButton(new ImageIcon(getClass().getResource(
				undoIconFile)));
		undoButton.setToolTipText("Undo (Ctrl+Z)");
		undoButton.setPreferredSize(BUTTON_PREFERRED_SIZE);
		this.add(undoButton);

		redoButton = new JButton(new ImageIcon(getClass().getResource(
				redoIconFile)));
		redoButton.setToolTipText("Redo (Ctrl+Y)");
		redoButton.setPreferredSize(BUTTON_PREFERRED_SIZE);
		this.add(redoButton);

		this.update(UndoableControl.getController(), null);

		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UndoableControl.getController().undo();
			}
		});

		redoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UndoableControl.getController().redo();
			}
		});

	}

	@Override
	public void update(Observable observable, Object obj) {

		// UndoableControl was updated; refresh icons
		if (observable instanceof UndoableControl) {
			undoButton.setEnabled(((UndoableControl) observable).canUndo());
			redoButton.setEnabled(((UndoableControl) observable).canRedo());
		}
	}
}
