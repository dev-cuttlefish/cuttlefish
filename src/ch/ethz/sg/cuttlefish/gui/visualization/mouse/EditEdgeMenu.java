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
import ch.ethz.sg.cuttlefish.gui.undoable.actions.DeleteEdgeUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetEdgeColorUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetEdgeLabelUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetEdgeWeightUndoableAction;
import ch.ethz.sg.cuttlefish.gui.undoable.actions.SetEdgeWidthUndoableAction;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;

public class EditEdgeMenu {

	private JPopupMenu menu;
	private JMenuItem mLabel, mWeight, mWidth, mColor;
	private JMenuItem mDelete;

	private Edge edge;
	private BrowsableNetwork network;
	private MouseEvent event;

	public EditEdgeMenu(Edge edge, BrowsableNetwork network, MouseEvent event) {
		this.edge = edge;
		this.event = event;
		this.network = network;

		// Menu items
		mLabel = new JMenuItem("Label: " + edge.getLabel());
		mWeight = new JMenuItem("Set weight...");
		mWidth = new JMenuItem("Set width...");
		mColor = new JMenuItem("Set color...");
		mDelete = new JMenuItem("Delete");

		// Add items to popup menu
		menu = new JPopupMenu();
		menu.add(mLabel);
		menu.add(new JSeparator());
		menu.add(mWeight);
		menu.add(mWidth);
		menu.add(mColor);
		menu.add(new JSeparator());
		menu.add(mDelete);

		addListeners();
	}

	private void addListeners() {

		mLabel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Enter new label",
						edge.getLabel());

				if (input != null) {
					UndoableAction action = new SetEdgeLabelUndoableAction(
							edge, input);
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		});

		mWeight.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Enter new weight",
						edge.getWeight());

				if (input != null) {
					UndoableAction action = new SetEdgeWeightUndoableAction(
							edge, Double.parseDouble(input));
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		});

		mWidth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Enter new width",
						edge.getWidth());

				if (input != null) {
					UndoableAction action = new SetEdgeWidthUndoableAction(
							edge, Integer.parseInt(input));
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		});

		mColor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(
						(Component) event.getSource(), "Set Color",
						edge.getColor());

				if (newColor != null) {
					UndoableAction action = new SetEdgeColorUndoableAction(
							edge, newColor);
					action.execute();
					UndoableControl.getController().actionExecuted(action);
				}
			}
		});

		mDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UndoableAction action = new DeleteEdgeUndoableAction(network,
						edge);
				action.execute();
				UndoableControl.getController().actionExecuted(action);
			}
		});

	}
	
	public void show() {
		menu.show(event.getComponent(), event.getPoint().x, event.getPoint().y);
	}
}
