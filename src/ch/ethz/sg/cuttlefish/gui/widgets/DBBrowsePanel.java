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
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;

/**
 * Widget for the database browsing
 * @author dgarcia
 */
public class DBBrowsePanel extends BrowserWidget {

	private static final long serialVersionUID = 1L;
	
	private JButton exploreButton = null;
	private JTextField idField = null;
	private JTextField distanceField = null;
	private JButton clearButton = null;
	
	/**
	 * This method initializes the DataBase browser panel
	 */
	public DBBrowsePanel() {
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
        this.add(getExploreButton(), gridBagConstraints1);
        this.add(getIdField(), gridBagConstraints);
        this.add(getDistanceField(), gridBagConstraints2);
        this.add(getClearButton(), gridBagConstraints3);
 	}
	/**
	 * Getter for the Vertex id text field
	 * @return JTextField
	 */

	private JTextField getIdField() {
		if (idField == null)
		{
			idField = new JTextField("Vertex id");
			idField.setToolTipText("Vertex id");
		}
		return idField;
	}

	/**
	 * Getter for the distance text field
	 * @return JTextField
	 */
	private JTextField getDistanceField() {
		if (distanceField == null)
		{
			distanceField = new JTextField("Distance");
			distanceField.setToolTipText("Distance");
		}
		return distanceField;
	}

	/**
	 * This method initializes exploreButton	
	 * @return javax.swing.JButton	
	 */
	private JButton getExploreButton() {
		if (exploreButton == null) {
			exploreButton = new JButton();
			exploreButton.setText("Explore");
			exploreButton.setEnabled(true);
			exploreButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if ( ((DBNetwork)getNetwork() ).isConnected() ) {
						boolean forward = true;
						int distance = Integer.parseInt(distanceField.getText());
						if (distance < 0) // negative distance input means backwards exploration
						{
							forward = false;
							distance = (-1) * distance;		//extendNeighborhood gets abs value of distance
						}
					  // ((DBNetwork) getNetwork()).extendNeighborhood(Integer.parseInt(idField.getText()), distance, forward);
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
		return exploreButton;
	}

	/**
	 * This method initializes clearButton	
	 * @return javax.swing.JButton	
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setText("Clear network");
			clearButton.setEnabled(true);
			clearButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				   ((DBNetwork) getNetwork()).emptyNetwork(); //network emptied through the DBNetwork interface
				   getBrowser().onNetworkChange();
	               getBrowser().repaintViewer();
				}
			});
		}
		return clearButton;
	}

	
	@Override
	public void init() {
	}

}
