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

package ch.ethz.sg.cuttlefish.gui.widgets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;


import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.networks.StaticCxfNetwork;

public class CxfNetworkPanel extends BrowserWidget {
 
	private static final long serialVersionUID = 1L;
	
	private JFileChooser fileC = null;
	private JButton loadButton = null;
	private JLabel networkNameLabel = null;

	/**
	 * This method initializes 
	 */
	public CxfNetworkPanel() {
		super();
		initialize();
		setNetworkClass(StaticCxfNetwork.class);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints1.gridx = 0;
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 2;

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.gridy = 3;

		this.setLayout(new GridBagLayout());
        this.add(getLoadButton(), gridBagConstraints1);
        this.add(new JLabel("Network name:"), gridBagConstraints);
        this.add(getNameLabel(), gridBagConstraints2);
 	}
	
	private JFileChooser getFileChooser() {
		if (fileC == null) {
			fileC = new JFileChooser();
		}
		return fileC;
	}

	private JLabel getNameLabel() {
		if (networkNameLabel == null)
		{
			StaticCxfNetwork cxfNet = (StaticCxfNetwork) getNetwork();
			if (cxfNet != null)
				networkNameLabel = new JLabel(cxfNet.getCxfName());
			else
				networkNameLabel = new JLabel("");	
		}
		return networkNameLabel;
	}
	
	/**
	 * This method initializes stopButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLoadButton() {
		if (loadButton == null) {
			loadButton = new JButton();
			loadButton.setText("Choose File");
			loadButton.setEnabled(true);
			loadButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    JFileChooser fc = getFileChooser();
				    fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
					int returnVal = fc.showOpenDialog(CxfNetworkPanel.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fc.getSelectedFile();
		                StaticCxfNetwork cxfNet = (StaticCxfNetwork) getNetwork();
		                cxfNet.load(file);
		                setNetwork(cxfNet);
		                getBrowser().onNetworkChange();
		                getBrowser().getNetworkLayout().reset();
		                getBrowser().repaintViewer();
		                getBrowser().stopLayout();
		               	networkNameLabel.setText(cxfNet.getCxfName());
		            } else {
		                System.out.println("Input cancelled by user");
		            }
		       
				}
			});
		}
		return loadButton;
	}

	@Override
	public void init() {
	}

}
