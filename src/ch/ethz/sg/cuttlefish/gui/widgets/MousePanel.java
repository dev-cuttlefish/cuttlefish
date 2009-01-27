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

import javax.swing.JComboBox;
import javax.swing.JLabel;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;

public class MousePanel extends BrowserWidget  {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel1 = null;

	private JComboBox jComboBox = null;

	/**
	 * This is the default constructor
	 */
	public MousePanel() {
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
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.anchor = GridBagConstraints.CENTER;
		gridBagConstraints1.fill = GridBagConstraints.VERTICAL;

		jLabel1 = new JLabel();
		jLabel1.setText("Mode");
		jLabel1.setVisible(true);
		this.setLayout(new GridBagLayout());
		this.add(jLabel1, gridBagConstraints1);
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	@SuppressWarnings("unchecked")
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			EditingModalGraphMouse gm = getBrowser().getMouse();
			gm.getModeComboBox().setSelectedIndex(0);
			jComboBox = gm.getModeComboBox();
		}
		return jComboBox;
	}

	@Override
	public void init() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.anchor = GridBagConstraints.CENTER;
		gridBagConstraints2.fill = GridBagConstraints.VERTICAL;
		this.add( this.getJComboBox() , gridBagConstraints2 );
	}

}  //  @jve:decl-index=0:visual-constraint="0,0"
