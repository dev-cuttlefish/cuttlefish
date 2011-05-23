package ch.ethz.sg.cuttlefish.gui2.toolbars;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import ch.ethz.sg.cuttlefish.gui.applet.Cuttlefish;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;

import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

public class MouseToolbar extends AbstractToolbar {

	private JButton transformingButton;
	private JButton pickingButton;
	private JButton editingButton;
	
	private static String transformingIconFile = "icons/transforming.png";
	private static String pickingIconFile = "icons/picking.png";
	private static String editingIconFile = "icons/editing.png";
	
	public MouseToolbar(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
		transformingButton.doClick();
	}
	
	private void initialize() {
		transformingButton = new JButton(new ImageIcon(getClass().getResource(transformingIconFile)));
		pickingButton = new JButton(new ImageIcon(getClass().getResource(pickingIconFile)));
		editingButton = new JButton(new ImageIcon(getClass().getResource(editingIconFile)));
		
		this.add(transformingButton);
		this.add(pickingButton);
		this.add(editingButton);
		
		transformingButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				networkPanel.getMouse().setMode(ModalGraphMouse.Mode.TRANSFORMING);
				transformingButton.setEnabled(false);
				pickingButton.setEnabled(true);
				editingButton.setEnabled(true);
			}
		});
		
		pickingButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				networkPanel.getMouse().setMode(ModalGraphMouse.Mode.PICKING);
				transformingButton.setEnabled(true);
				pickingButton.setEnabled(false);
				editingButton.setEnabled(true);
			}
		});
		
		editingButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				networkPanel.getMouse().setMode(ModalGraphMouse.Mode.EDITING);
				transformingButton.setEnabled(true);
				pickingButton.setEnabled(true);
				editingButton.setEnabled(false);
			}
		});
	}

}
