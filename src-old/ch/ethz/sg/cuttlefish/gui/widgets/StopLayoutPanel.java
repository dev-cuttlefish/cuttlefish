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

import javax.swing.JButton;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;

public class StopLayoutPanel extends BrowserWidget {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton stopButton = null;

	/**
	 * This method initializes 
	 * 
	 */
	public StopLayoutPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.anchor = GridBagConstraints.CENTER;
		gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
		
		this.setLayout(new GridBagLayout());
        this.add(getStopButton(), gridBagConstraints1);
			
	}
	
	/**
	 * This method initializes stopButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton();
			stopButton.setText("Stop Layout");
			stopButton.setEnabled(true);
			stopButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getBrowser().stopLayout();
				}
			});
		}
		return stopButton;
	}


	
	@Override
	public void init() {
	}

}
