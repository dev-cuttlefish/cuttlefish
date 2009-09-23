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
        this.add(getConnectButton(), gridBagConstraints1);
        this.add(getUrlField(), gridBagConstraints);
        this.add(getUserNameField(), gridBagConstraints2);
        this.add(getPasswordField(), gridBagConstraints3);
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
				   ((DBNetwork) getNetwork()).connect(getUrlField().getText(),
						   getUserNameField().getText(), getPasswordField().getText());
				}
			});
		}
		return connectButton;
	}

	@Override
	public void init() {
	}

}
