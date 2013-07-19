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
import javax.swing.JButton;
import javax.swing.JTextField;
import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;

/**
 * Widget for filtering the network information of a database
 * @author dgarcia
 *
 */
public class DBFilterPanel extends BrowserWidget {

	private static final long serialVersionUID = 1L;
	
	private JTextField nodeFilterField = null;
	private JButton nodeFilterButton = null;
	private JTextField edgeFilterField = null;
	private JButton edgeFilterButton = null;
	
	/**
	 * Basic constructor
	 * 
	 */
	public DBFilterPanel() {
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
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
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
        this.add(getNodeFilterField(), gridBagConstraints1);
        this.add(getNodeFilterButton(), gridBagConstraints);
        this.add(getEdgeFilterField(), gridBagConstraints2);
        this.add(getEdgeFilterButton(), gridBagConstraints3);
 	}
	
	/**
	 * Getter for the node filter field
	 * @return JTextField
	 */
	private JTextField getNodeFilterField() {
		if (nodeFilterField == null)
		{
			nodeFilterField = new JTextField("");
			nodeFilterField.setToolTipText("Node filter");
		}
		return nodeFilterField;
	}

	/**
	 * Getter for the edge filter field
	 * @return JTextField
	 */
	private JTextField getEdgeFilterField() {
		if (edgeFilterField == null)
		{
			edgeFilterField = new JTextField("");
			edgeFilterField.setToolTipText("Edge filter");
		}
		return edgeFilterField;
	}
	/**
	 * This method initializes getNodeFilterButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNodeFilterButton() {
		if (nodeFilterButton == null) {
			nodeFilterButton = new JButton();
			nodeFilterButton.setText("Filter Nodes");
			nodeFilterButton.setEnabled(true);
			nodeFilterButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					((DBNetwork) getNetwork()).setNodeFilter(getNodeFilterField().getText());
				}
			});
		}
		return nodeFilterButton;
	}

	/**
	 * This method initializes edgeFilterButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getEdgeFilterButton() {
		if (edgeFilterButton == null) {
			edgeFilterButton = new JButton();
			edgeFilterButton.setText("Filter Edges");
			edgeFilterButton.setEnabled(true);
			edgeFilterButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					((DBNetwork) getNetwork()).setEdgeFilter(getEdgeFilterField().getText());
				}
			});
		}
		return edgeFilterButton;
	}

	
	@Override
	public void init() {
	}

}
