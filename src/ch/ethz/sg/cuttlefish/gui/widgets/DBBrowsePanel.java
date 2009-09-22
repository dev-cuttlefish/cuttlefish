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
import javax.swing.JPasswordField;
import javax.swing.JTextField;


import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;
import ch.ethz.sg.cuttlefish.networks.StaticCxfNetwork;

public class DBBrowsePanel extends BrowserWidget {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton exploreButton = null;
	private JTextField idField = null;
	private JTextField distanceField = null;
	private JButton clearButton = null;
	
	/**
	 * This method initializes 
	 * 
	 */
	public DBBrowsePanel() {
		super();
		initialize();
		setNetworkClass(DBNetwork.class);
	}

	/**
	 * This method initializes this
	 * 
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

		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.gridy = 4;

		this.setLayout(new GridBagLayout());
        this.add(getExploreButton(), gridBagConstraints1);
        this.add(getIdField(), gridBagConstraints);
        this.add(getDistanceField(), gridBagConstraints2);
        this.add(getClearButton(), gridBagConstraints3);
 	}

	private JTextField getIdField() {
		if (idField == null)
		{
			idField = new JTextField("Vertex id");
			idField.setToolTipText("Vertex id");
		}
		return idField;
	}

	private JTextField getDistanceField() {
		if (distanceField == null)
		{
			distanceField = new JTextField("Distance");
			distanceField.setToolTipText("Distance");
		}
		return distanceField;
	}

	/**
	 * This method initializes stopButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getExploreButton() {
		if (exploreButton == null) {
			exploreButton = new JButton();
			exploreButton.setText("Explore");
			exploreButton.setEnabled(true);
			exploreButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				   ((DBNetwork) getNetwork()).extendNeighborhood(Integer.parseInt(idField.getText()), Integer.parseInt(distanceField.getText()));
				   ((DBNetwork) getNetwork()).extendEdges();
					getBrowser().onNetworkChange();
	                getBrowser().getNetworkLayout().reset();
	                getBrowser().repaintViewer();
	                getBrowser().stopLayout();
				}
			});
		}
		return exploreButton;
	}

	/**
	 * This method initializes stopButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setText("Clear network");
			clearButton.setEnabled(true);
			clearButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				   ((DBNetwork) getNetwork()).clearGraph();
				   getBrowser().onNetworkChange();
	               getBrowser().getNetworkLayout().reset();
	               getBrowser().repaintViewer();
	               getBrowser().stopLayout();
				}
			});
		}
		return clearButton;
	}

	
	@Override
	public void init() {
	}

}
