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

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;

/**
 * Widget for node expansion in a database network
 * @author dgarcia
 *
 */
public class DBExpandPanel extends BrowserWidget {

	private static final long serialVersionUID = 1L;
	
	private JButton expandButton = null;
	private JButton backExpandButton = null;
	private JButton shrinkButton = null;
	private JButton backShrinkButton = null;
	
	/**
	 * Basic constructor for the expand panel 
	 */
	public DBExpandPanel() {
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
        this.add(getExpandButton(), gridBagConstraints1);
        this.add(getBackExpandButton(), gridBagConstraints);
        this.add(getShrinkButton(), gridBagConstraints2);
        this.add(getBackShrinkButton(), gridBagConstraints3);
        
 	}

	/**
	 * This method initializes expandButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getExpandButton() {
		if (expandButton == null) {
			expandButton = new JButton();
			expandButton.setText("Expand");
			expandButton.setEnabled(true);
			expandButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					for (Vertex vertex : getBrowser().getPickedVertices())
						//((DBNetwork) getNetwork()).extendNeighborhood(vertex.getId(), 1, true);
					getBrowser().onNetworkChange();
	                getBrowser().repaintViewer();
	           }
			});
		}
		return expandButton;
	}

	/**
	 * This method initializes backExpandButton
	 * @return javax.swing.JButton	
	 */
	private JButton getBackExpandButton() {
		if (backExpandButton == null) {
			backExpandButton = new JButton();
			backExpandButton.setText("Expand Backwards");
			backExpandButton.setEnabled(true);
			backExpandButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					for (Vertex vertex : getBrowser().getPickedVertices())
						//((DBNetwork) getNetwork()).extendNeighborhood(vertex.getId(), 1, false);
					getBrowser().onNetworkChange();
	                getBrowser().repaintViewer();
	           }
			});
		}
		return backExpandButton;
	}
	
	/**
	 * This method initializes shrinkButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShrinkButton() {
		if (shrinkButton == null) {
			shrinkButton = new JButton();
			shrinkButton.setText("Shrink");
			shrinkButton.setEnabled(true);
			shrinkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					for (Vertex vertex : getBrowser().getPickedVertices())
						((DBNetwork) getNetwork()).shrinkVertex(vertex);
					getBrowser().onNetworkChange();
	                getBrowser().repaintViewer();
	    		}
			});
		}
		return shrinkButton;
	}

	/**
	 * This method initializes backShrinkButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBackShrinkButton() {
		if (backShrinkButton == null) {
			backShrinkButton = new JButton();
			backShrinkButton.setText("Shrink Backwards");
			backShrinkButton.setEnabled(true);
			backShrinkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					for (Vertex vertex : getBrowser().getPickedVertices())
						((DBNetwork) getNetwork()).backShrinkVertex(vertex);
					getBrowser().onNetworkChange();
	                getBrowser().repaintViewer();
	    		}
			});
		}
		return backShrinkButton;
	}


	
	@Override
	public void init() {
	}

}
