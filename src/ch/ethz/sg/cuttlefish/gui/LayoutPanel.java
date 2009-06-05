/*

Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

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

package ch.ethz.sg.cuttlefish.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ch.ethz.sg.cuttlefish.misc.CxfSaver;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

/**
 * Class for the JPanel that appears always in the bottom of the window with tools like saving and 
 * stopping the layout or selecting the mouse mode
 * @author david
 */
public class LayoutPanel extends JPanel{
	
private JComboBox layoutComboBox = null;
private JButton writeLayoutButton = null;
private JButton stopLayoutButton = null;
private JButton restartLayoutButton = null;
private JFileChooser fileC = null;

private CuttlefishPanel parentPanel = null;

private static final long serialVersionUID = 1L;

/**
 * Constructor depending on the parent CuttlefishPanel
 * @param parentPanel where the LayoutPanel will be added
 */
public LayoutPanel(CuttlefishPanel parentPanel){
	this.parentPanel = parentPanel;
	setLayout(new GridBagLayout());
	add(getLayoutComboBox(), new GridBagConstraints());
	setBackground(Color.gray);
	add(getWriteLayoutButton(), new GridBagConstraints());
	add(getStopLayoutButton(), new GridBagConstraints());
	add(getRestartLayoutButton(), new GridBagConstraints());
}

/**
 * This method initializes layoutComboBox	
 * @return javax.swing.JComboBox	
 */
private JComboBox getLayoutComboBox() {
	
	String[] layoutNames = {"ARFLayout", "FixedLayout", "WeightedARFLayout", "SpringLayout", "Kamada-Kawai", 
			"Fruchterman-Reingold", "ISOMLayout", "CircleLayout"};
	/*This array should keep the names of the layouts that are used to define the layout
	in the method setLayout*/
	
	if (layoutComboBox == null) {
		layoutComboBox = new JComboBox(layoutNames);
		layoutComboBox.setName("Layout");
		layoutComboBox.setBackground(Color.gray);
		layoutComboBox.setForeground(Color.orange);
		layoutComboBox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				parentPanel.setLayout((String)layoutComboBox.getSelectedItem());
			}
			
		});
	}
	return layoutComboBox;
}

/**
 * This method initializes the fileChooser for saving
 * @return
 */
private JFileChooser getFileChooser() {
	if (fileC == null) {
		fileC = new JFileChooser();
	}
	return fileC;
}

/**
 * This method initializes writeLayoutButton	
 * @return javax.swing.JButton	
 */
private JButton getWriteLayoutButton() {
	if (writeLayoutButton == null) {
		writeLayoutButton = new JButton();
		writeLayoutButton.setText("Save Network");
		writeLayoutButton.addActionListener(new java.awt.event.ActionListener() {
			
			public void actionPerformed(java.awt.event.ActionEvent e) {
				//The action is opening a dialog and saving in Cxf on the selected file
				JFileChooser fc = getFileChooser();
				fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
				int returnVal = fc.showSaveDialog(parentPanel);

	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                try {
						file.createNewFile();
						CxfSaver saver = new CxfSaver((BrowsableNetwork)parentPanel.getNetwork(), parentPanel.getNetworkLayout());
						saver.save(file);
					} catch (IOException ioEx) {
						JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
						System.err.println("Impossible to write");
						ioEx.printStackTrace();
					}
	            } else {
	                System.out.println("Input cancelled by user");
	            }
			}
		});
	}
	return writeLayoutButton;
}


/**
 * This method initializes stopLayoutButton	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getStopLayoutButton() {
	if (stopLayoutButton == null) {
		stopLayoutButton = new JButton();
		stopLayoutButton.setText("Stop Layout");
		stopLayoutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				parentPanel.stopLayout();
				stopLayoutButton.setEnabled(false);
				getRestartLayoutButton().setEnabled(true);
			}
		});
	}
	return stopLayoutButton;
}

/**
 * This method initializes restartLayoutButton	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getRestartLayoutButton() {
	if (restartLayoutButton == null) {
		restartLayoutButton = new JButton();
		restartLayoutButton.setText("Restart Layout");
		restartLayoutButton.setEnabled(false);
		restartLayoutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				parentPanel.resumeLayout();
				restartLayoutButton.setEnabled(false);
				stopLayoutButton.setEnabled(true);
			}
		});
	}
	return restartLayoutButton;
}
}
