/*

    Copyright (C) 2008  Markus Michael Geipel

    This program is free software: you can redistribute it and/or modify
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
import javax.swing.JComboBox;
import javax.swing.JLabel;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;


public class OptionsPanel extends BrowserWidget  {



	private static final long serialVersionUID = 1L;
	private JButton updateButton = null;
	private JLabel jLabel1 = null;

	private JComboBox jComboBox = null;

	private int firstYear = 1996, lastYear = 2008;

	/**
	 * This is the default constructor
	 */
	public OptionsPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints1.gridx = 2;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.anchor = GridBagConstraints.EAST;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.gridy = 2;
		gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints11.weightx = 1.0;
		gridBagConstraints11.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.gridy = 3;
		gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints5.fill = GridBagConstraints.BOTH;
		gridBagConstraints5.weightx = 1.0;

		jLabel1 = new JLabel();
		jLabel1.setText("Year:");
		jLabel1.setVisible(true);
		
		this.setSize(287, 230);
		this.setLayout(new GridBagLayout());
		//this.setBorder(BorderFactory.createTitledBorder(null, "export", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		//this.add(getJTextField(), gridBagConstraints21);
		this.add(jLabel1, gridBagConstraints1);
		this.add( this.getJComboBox() , gridBagConstraints2 );
		
		this.add(getUpdateButton(), gridBagConstraints5);
	}


	/**
	 * This method initializes adjlistButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getUpdateButton() {
		if (updateButton == null) {
			updateButton = new JButton();
			updateButton.setText("Update");
			updateButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					updateGraph();

				}
			});
		}
		return updateButton;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
		}
		return jComboBox;
	}


	public void updateGraph() {
		Integer selectedYear = (Integer)getJComboBox().getSelectedItem();
		
		System.out.println(selectedYear );
		
	}


	@Override
	public void init() {

		jComboBox.removeAllItems();

		for(int i = firstYear ; i <= lastYear; i++){
			jComboBox.addItem( i );
		}

	}

}  //  @jve:decl-index=0:visual-constraint="0,0"
