/*
  
    Copyright (C) 2011  Markus Michael Geipel, David Garcia Becerra,
    Petar Tsankov

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

package ch.ethz.sg.cuttlefish.gui2.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.CxfDBNetwork;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;

public class DBToolbar extends AbstractToolbar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 479370821385607442L;
	private JButton exploreNetwork = null;
	private JButton exploreNode = null;
	private JButton expand = null;
	private JButton expandBack = null;
	private JButton shrink = null;
	private JButton shrinkBack = null;
	private JComboBox networkNames = null;
	private JFrame exploreNodeFrame = null;
	private JFrame exploreNetworkFrame = null;
	private boolean enabled = false;

	private static String expandIcon = "icons/plus.png";
	private static String expandBackIcon = "icons/plus_back.png";
	private static String shrinkIcon = "icons/minus.png";
	private static String shrinkBackIcon = "icons/minus_back.png";

	public DBToolbar(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
	}

	@Override
	public void setVisible(boolean b) {
		if (networkPanel.getNetwork() instanceof DBNetwork) {
			super.setVisible(b);
			if(networkPanel.getNetwork() instanceof CxfDBNetwork) {
				networkNames.setVisible(false);
				setExploreButtonsEnabled(true);
				setNetworkButtonsEnabled(true);
			}
			enabled = true;
		} else {
			super.setVisible(false);
			enabled = false;
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	private void setExploreButtonsEnabled(boolean b) {
		expand.setEnabled(b);
		expandBack.setEnabled(b);
		shrink.setEnabled(b);
		shrinkBack.setEnabled(b);
	}

	private void setNetworkButtonsEnabled(boolean b) {
		exploreNetwork.setEnabled(b);
		exploreNode.setEnabled(b);
	}

	private void checkPickedVertices() {
		if (networkPanel.getPickedVertices().size() < 1) {
			JOptionPane.showMessageDialog(networkPanel,
					"You need to select at least one node",
					"No nodes selected", JOptionPane.WARNING_MESSAGE);
		}
	}

	private JFrame getExploreNodeFrame() {
		if (exploreNodeFrame == null)
			exploreNodeFrame = new DBExploreNode(networkPanel);
		return exploreNodeFrame;
	}

	private JFrame getExploreNetworkFrame() {
		if (exploreNetworkFrame == null)
			exploreNetworkFrame = new DBExploreNetwork(networkPanel);
		return exploreNetworkFrame;
	}

	public void findDBTables() {
		networkNames.removeAllItems();
		networkNames.insertItemAt("<Select a network>", 0);
		networkNames.setSelectedIndex(0);
		Collection<String> networks = ((DBNetwork) networkPanel.getNetwork())
				.getNetworkNames(((DBNetwork) networkPanel.getNetwork())
						.getSchemaName());
		int itemCount = 1;
		for (String networkName : networks) {
			networkNames.insertItemAt(networkName, itemCount);
			itemCount++;
		}
	}

	private void initialize() {
		exploreNetwork = new JButton("Explore Network");
		exploreNode = new JButton("Explore Node");
		expand = new JButton(new ImageIcon(getClass().getResource(expandIcon)));
		expandBack = new JButton(new ImageIcon(getClass().getResource(
				expandBackIcon)));
		shrink = new JButton(new ImageIcon(getClass().getResource(shrinkIcon)));
		shrinkBack = new JButton(new ImageIcon(getClass().getResource(
				shrinkBackIcon)));
		setExploreButtonsEnabled(true);
		setNetworkButtonsEnabled(false);
		networkNames = new JComboBox();
		networkNames.setEditable(false);

		networkNames.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (networkNames.getSelectedIndex() > 0) {
					if (((DBNetwork) networkPanel.getNetwork()).getEdgeTable()
							.compareToIgnoreCase(
									(String) networkNames.getSelectedItem()) != 0) {
						setNetworkButtonsEnabled(true);
						((DBNetwork) networkPanel.getNetwork())
								.setNetwork((String) networkNames
										.getSelectedItem());
						if (((DBNetwork) networkPanel.getNetwork()).nodeTableIsDerived()) {
							Object selected = JOptionPane.showInputDialog(networkPanel, "Could not find a matching table with nodes for the selected network.\n\nSelect a table with nodes from the drop down menu,\nor click Cancel if there isn't one", "Could not find a table with nodes", JOptionPane.QUESTION_MESSAGE, null, ((DBNetwork)networkPanel.getNetwork()).availableNodeTables().toArray(), null);
							String selectedNodeTable = (String)selected;
							if(selectedNodeTable != null)
								((DBNetwork)networkPanel.getNetwork()).setNodeTable(selectedNodeTable);
						}
					}
				} else {
					setNetworkButtonsEnabled(false);
				}
			}
		});

		exploreNetwork.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getExploreNetworkFrame().setVisible(true);
			}
		});

		exploreNode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getExploreNodeFrame().setVisible(true);
			}
		});

		this.add(networkNames);
		this.add(expand);
		this.add(expandBack);
		this.add(shrink);
		this.add(shrinkBack);
		this.add(exploreNetwork);
		this.add(exploreNode);

		expand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				checkPickedVertices();
				for (Vertex vertex : networkPanel.getPickedVertices())
					((DBNetwork) networkPanel.getNetwork()).extendNeighborhood(
							vertex.getId(), 1, true);
				networkPanel.onNetworkChange();
				networkPanel.repaintViewer();
			}
		});

		expandBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				checkPickedVertices();
				for (Vertex vertex : networkPanel.getPickedVertices())
					((DBNetwork) networkPanel.getNetwork()).extendNeighborhood(
							vertex.getId(), 1, false);
				networkPanel.onNetworkChange();
				networkPanel.repaintViewer();
			}
		});

		shrink.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				checkPickedVertices();
				for (Vertex vertex : networkPanel.getPickedVertices())
					((DBNetwork) networkPanel.getNetwork())
							.shrinkVertex(vertex);
				networkPanel.onNetworkChange();
				networkPanel.repaintViewer();
			}
		});

		shrinkBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				checkPickedVertices();
				for (Vertex vertex : networkPanel.getPickedVertices())
					((DBNetwork) networkPanel.getNetwork())
							.backShrinkVertex(vertex);
				networkPanel.onNetworkChange();
				networkPanel.repaintViewer();
			}
		});

	}

}
