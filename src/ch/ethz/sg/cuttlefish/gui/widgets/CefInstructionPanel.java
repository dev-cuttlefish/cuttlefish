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
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;


import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork;
import ch.ethz.sg.cuttlefish.networks.UserNetwork;

public class CefInstructionPanel extends BrowserWidget {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JFileChooser fileC = null;
	private JButton loadButton = null;

	/**
	 * This method initializes 
	 * 
	 */
	public CefInstructionPanel() {
		super();
		initialize();
		setNetworkClass(InteractiveCxfNetwork.class);
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
        this.add(getLoadButton(), gridBagConstraints1);
			
	}
	
	private JFileChooser getFileChooser() {
		if (fileC == null) {
			fileC = new JFileChooser();
		}
		return fileC;
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
					int returnVal = fc.showOpenDialog(CefInstructionPanel.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fc.getSelectedFile();
		                InteractiveCxfNetwork cxfNet = (InteractiveCxfNetwork) getNetwork();
		                cxfNet.loadInstructions(file);
		                setNetwork(cxfNet);
		                getBrowser().onNetworkChange();
		                getBrowser().getNetworkLayout().reset();
		                getBrowser().repaintViewer();
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
		// TODO Auto-generated method stub
		
	}

}
