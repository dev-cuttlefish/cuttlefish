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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;

/**
 * Widget for the connection to the database 
 */

public class DBConnectPanel extends BrowserWidget {
	 
	private static final long serialVersionUID = 1L;
	
	private JButton connectButton = null;
	private JTextField urlField = null;
	private JTextField userNameField = null;
	private JPasswordField passwordField = null;
	private JComboBox nodeTables = null;
	private JComboBox edgeTables = null;
	/**
	 * Basic constructor for the connect panel
	 */
	public DBConnectPanel() {
		super();
		initialize();
		setNetworkClass(DBNetwork.class);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints1.gridx = 0;
		
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.gridy = 1;

		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.gridy = 2;

		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.gridy = 3;

		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.gridy = 0;
		
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 1;
		gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints6.gridy = 1;
		
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 1;
		gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints7.gridy = 2;
		
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.gridx = 1;
		gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.gridy = 3;
		
		this.setLayout(new GridBagLayout());
        this.add(getConnectButton(), gridBagConstraints1);
        this.add(getUrlField(), gridBagConstraints2);
        this.add(getUserNameField(), gridBagConstraints3);
        this.add(getPasswordField(), gridBagConstraints4);
        
        this.add(new JLabel("Nodes table"), gridBagConstraints5);
        this.add(getNodeTables(), gridBagConstraints6);
        this.add(new JLabel("Edges table"), gridBagConstraints7);
        this.add(getEdgeTables(), gridBagConstraints8);
 	}

	
	/**
	 * Getter for Node Tables
	 * @return JComboBox
	 */
	private JComboBox getNodeTables() {
		if( nodeTables == null ) {
			nodeTables = new JComboBox();
			nodeTables.addActionListener(
				new ActionListener() {				
					@Override
					public void actionPerformed(ActionEvent e) {
						//((DBNetwork)getNetwork()).setNodeTable((String)nodeTables.getSelectedItem());
					}
				}
			);
		}
		return nodeTables;
	}
	
	/**
	 * Getter for Edge Tables
	 * @return JComboBox
	 */
	private JComboBox getEdgeTables() {
		if( edgeTables == null ) {
			edgeTables = new JComboBox();
			edgeTables.addActionListener(
				new ActionListener() {				
					@Override
					public void actionPerformed(ActionEvent e) {
						//((DBNetwork)getNetwork()).setEdgeTable((String)edgeTables.getSelectedItem());
					}
				}
			);
		}
		return edgeTables;
	}
	
	/**
	 * Getter for the URL field
	 * @return JTextField
	 */
	private JTextField getUrlField() {
		if (urlField == null)
		{
			String initialContent = getArgument("url");
			if (initialContent != null)
				urlField = new JTextField(initialContent);
			else
				urlField = new JTextField("Database URL");
			urlField.setToolTipText("Database URL");
		}
		return urlField;
	}

	/**
	 * Getter for the user name field
	 * @return JTextField
	 */
	private JTextField getUserNameField() {
		if (userNameField == null)
		{
			String initialContent = getArgument("userName");
			if (initialContent != null)
				userNameField = new JTextField(initialContent);
			else
				userNameField = new JTextField("User name");
			userNameField.setToolTipText("User name");
		}
		return userNameField;
	}

	/**
	 * Getter for the password field
	 * @return JPasswordField
	 */
	private JTextField getPasswordField() {
		if (passwordField == null)
		{
			String initialContent = getArgument("password");
			if (initialContent != null)
				passwordField = new JPasswordField(initialContent);
			else
				passwordField = new JPasswordField("Password");
			passwordField.setToolTipText("Password");
		}
		return passwordField;
	}

	
	/**
	 * This method initializes connectButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getConnectButton() {
		if (connectButton == null) {
			connectButton = new JButton();
			connectButton.setText("Connect");
			connectButton.setEnabled(true);
			connectButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//((DBNetwork) getNetwork()).connect("com.mysql.jdbc.Driver", "jdbc:mysql://", getUrlField().getText(),
					//	   getUserNameField().getText(), getPasswordField().getText());
				   nodeTables.removeAllItems();
				   edgeTables.removeAllItems();
				   //for(String edgeTable : dbNetwork.getEdgeTables(dbNetwork.getSchemaName())) {
					   //edgeTables.insertItemAt(edgeTable, itemCount);
				   //}
				}
			});
		}
		return connectButton;
	}

	@Override
	public void init() {
	}

}
