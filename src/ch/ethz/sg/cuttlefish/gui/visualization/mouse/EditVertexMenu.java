package ch.ethz.sg.cuttlefish.gui.visualization.mouse;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import ch.ethz.sg.cuttlefish.gui.undoable.UndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.UndoableControl;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetVertexBorderColorUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetVertexBorderWidthUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetVertexFillColorUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetVertexLabelUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetVertexShapeUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetVertexSizeUndoableAction;
import ch.ethz.sg.cuttlefish.gui.visualization.Constants;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class EditVertexMenu {

	private JPopupMenu menu;
	private JMenuItem mID, mFillColor, mBorderColor, mSize, mBorderWidth,
			mLabel, mShape;

	private Vertex vertex;
	private MouseEvent event;

	public EditVertexMenu(Vertex v, MouseEvent e) {

		this.vertex = v;
		this.event = e;

		// Menu items
		mID = new JMenuItem("ID: " + v.getId());
		mLabel = new JMenuItem("Label: " + v.getLabel());
		mSize = new JMenuItem("Size: " + v.getSize());
		mFillColor = new JMenuItem("Set fill color...");
		mBorderColor = new JMenuItem("Set border color...");
		mBorderWidth = new JMenuItem("Set border width...");
		mShape = new JMenuItem("Set shape...");

		// Add items to popup
		menu = new JPopupMenu();
		menu.add(mID);
		menu.add(mLabel);
		menu.add(mSize);
		menu.add(new JSeparator());

		menu.add(mFillColor);
		menu.add(mBorderColor);
		menu.add(mBorderWidth);
		menu.add(mShape);

		addListeners();
		// TODO ilias: undoable actions
	}

	private void addListeners() {
		mLabel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Enter new label",
						vertex.getLabel());

				if (input != null) {
					UndoableAction action = new SetVertexLabelUndoableAction(
							vertex, input);
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		});

		mSize.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Enter new size",
						vertex.getSize());

				if (input != null) {
					UndoableAction action = new SetVertexSizeUndoableAction(
							vertex, Double.parseDouble(input));
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		});

		mFillColor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(
						(Component) e.getSource(), "Set Fill Color",
						vertex.getFillColor());

				if (newColor != null) {
					UndoableAction action = new SetVertexFillColorUndoableAction(
							vertex, newColor);
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		});

		mBorderColor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(
						(Component) e.getSource(), "Set Border Color",
						vertex.getBorderColor());

				if (newColor != null) {
					UndoableAction action = new SetVertexBorderColorUndoableAction(
							vertex, newColor);
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		});

		mBorderWidth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(
						"Enter new border width", vertex.getWidth());

				if (input != null) {
					UndoableAction action = new SetVertexBorderWidthUndoableAction(
							vertex, Integer.parseInt(input));
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		});

		mShape.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Options for the combo box dialog
				String[] choices = { Constants.SHAPE_DISK,
						Constants.SHAPE_SQUARE };

				// Input dialog with a combo box
				String input = (String) JOptionPane.showInputDialog(
						(Component) e.getSource(), "Select shape", "Shape",
						JOptionPane.QUESTION_MESSAGE, null, choices,
						vertex.getShape());

				if (input != null) {
					UndoableAction action = new SetVertexShapeUndoableAction(
							vertex, input);
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		});
	}

	public void show() {
		menu.show(event.getComponent(), event.getPoint().x, event.getPoint().y);
	}
}
