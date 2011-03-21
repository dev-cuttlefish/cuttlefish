/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra, Petar Tsankov
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;

/**
 * Widget for the input of a query to a database network
 */
public class DBQueryPanel extends BrowserWidget {
	
	private static final long serialVersionUID = 1L;
	
	private JButton executeButton = null;
	private JTextField queryField = null;
	
	/**
	 * Basic constructor
	 */
	public DBQueryPanel() {
		super();
		initialize();
		setNetworkClass(DBNetwork.class);
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
		gridBagConstraints2.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints2.gridy = 3;

		this.setLayout(new GridBagLayout());
        this.add(getExecuteButton(), gridBagConstraints1);
        this.add(new JLabel("Node query:"), gridBagConstraints);
        this.add(getQueryField(), gridBagConstraints2);
 	}

	/**
	 * Getter for the query text field
	 * @return JTextField
	 */
	private JTextField getQueryField() {
		if (queryField == null)
		{
			queryField = new JTextField("                          SQL query                          ");
			queryField.setToolTipText("SQL query");
		}
		return queryField;
	}
	
	/**
	 * This method initializes executeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getExecuteButton() {
		if (executeButton == null) {
			executeButton = new JButton();
			executeButton.setText("Execute query");
			executeButton.setEnabled(true);
			executeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(((DBNetwork)getNetwork()).isConnected() ) {
					   ((DBNetwork) getNetwork()).nodeQuery(getQueryField().getText());
					   	getBrowser().onNetworkChange();
		                getBrowser().getNetworkLayout().reset();
		                getBrowser().repaintViewer();
		                getBrowser().stopLayout();
					} else {
						JOptionPane.showMessageDialog(null, "You are not connected to a database");
					}
				}					
			});
		}
		return executeButton;
	}

	@Override
	public void init() {
	}

}
