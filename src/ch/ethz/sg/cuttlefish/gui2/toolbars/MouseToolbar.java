/*
  
    Copyright (C) 2011  Markus Michael Geipel, David Garcia Becerra,
    Petar Tsankov

	This file is part of Cuttlefish.
	
 	Cuttlefish is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
*/

package ch.ethz.sg.cuttlefish.gui2.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;

import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

public class MouseToolbar extends AbstractToolbar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6548020765695598900L;
	private JButton transformingButton;
	private JButton pickingButton;
	private JButton editingButton;
	
	private static String transformingIconFile = "icons/transforming.png";
	private static String pickingIconFile = "icons/picking.png";
	private static String editingIconFile = "icons/editing.png";
	
	public MouseToolbar(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
		pickingButton.doClick();
		editingButton.doClick();
		transformingButton.doClick();
	}
	
	private void initialize() {
		transformingButton = new JButton(new ImageIcon(getClass().getResource(transformingIconFile)));
		pickingButton = new JButton(new ImageIcon(getClass().getResource(pickingIconFile)));
		editingButton = new JButton(new ImageIcon(getClass().getResource(editingIconFile)));
		
		this.add(transformingButton);
		this.add(pickingButton);
		this.add(editingButton);
		
		transformingButton.setToolTipText("Mouse mode used to move the network around the screen");
		pickingButton.setToolTipText("Mouse mode used to select nodes");
		editingButton.setToolTipText("Mouse mode used to add new nodes");		
		
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
