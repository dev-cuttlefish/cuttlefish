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

package ch.ethz.sg.cuttlefish.gui.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.networks.BrowsableForestNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.ExploreNetwork;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;
import ch.ethz.sg.cuttlefish.networks.Vertex;

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
	private JButton run = null;
	private JButton step = null;
	private JButton settingsButton;
	private boolean running = false;
	private long sleepTime = 200; // in milliseconds
	private JComboBox networkNames = null;
	private DBExploreNode exploreNodeFrame = new DBExploreNode(networkPanel);;
	private DBExploreNetwork exploreNetworkFrame = null;
	private boolean enabled = false;
	private Icon runIcon = null;
	private Icon pauseIcon = null;
	private static String expandIcon = "icons/plus.png";
	private static String expandBackIcon = "icons/plus_back.png";
	private static String shrinkIcon = "icons/minus.png";
	private static String shrinkBackIcon = "icons/minus_back.png";
	private static String runIconFile = "icons/run.png";
	private static String pauseIconFile = "icons/pause.png";
	private static String stepIcon = "icons/step.png";

	public DBToolbar(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
	}

	@Override
	public void setVisible(boolean b) {
		if (networkPanel.getNetwork() instanceof DBNetwork) {			
			super.setVisible(b);
			networkNames.setVisible(true);
			if(networkPanel.getNetwork() instanceof ExploreNetwork) {
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
		step.setEnabled(b);
		run.setEnabled(b);
	}

	private void checkPickedVertices() {
		if (networkPanel.getPickedVertices().size() < 1) {
			JOptionPane.showMessageDialog(networkPanel,
					"You need to select at least one node",
					"No nodes selected", JOptionPane.WARNING_MESSAGE);
		}
	}

	private DBExploreNode getExploreNodeFrame() {
		if (exploreNodeFrame == null)
			exploreNodeFrame = new DBExploreNode(networkPanel);			
		return exploreNodeFrame;
	}

	private DBExploreNetwork getExploreNetworkFrame() {
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
		run = new JButton(getRunIcon());
		step = new JButton(new ImageIcon(getClass().getResource(stepIcon)));
		setExploreButtonsEnabled(true);
		setNetworkButtonsEnabled(false);
		networkNames = new JComboBox();
		networkNames.setEditable(false);
		settingsButton = new JButton("Settings");
		settingsButton.setToolTipText("Change the update time interval");
				
		expand.setToolTipText("Expand node (add all nodes connected to the selected node)");
		expandBack.setToolTipText("Expand back node (add all nodes that are connected to the selected node");
		shrink.setToolTipText("Shrink node (remove all nodes connected to the selected node)");
		shrinkBack.setToolTipText("Shrink back node (remove all nodes that are connected to the selected node)");
		exploreNetwork.setToolTipText("Filter a network using node and edge filters");
		exploreNode.setToolTipText("Explore the neighborhood of a node");
		run.setToolTipText("Update the network upon data changes");
		step.setToolTipText("Check the data for changes");
		
		run.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				runButtonEvent();	
			}
		});
		
		step.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				stepButtonEvent();	
			}
		});

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
				getExploreNetworkFrame().update();
				getExploreNetworkFrame().setVisible(true);
				getExploreNetworkFrame().toFront();
			}
		});

		exploreNode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getExploreNodeFrame().update();
				getExploreNodeFrame().setVisible(true);
				getExploreNodeFrame().toFront();
			}
		});

		this.add(networkNames);
		this.add(expand);
		this.add(expandBack);
		this.add(shrink);
		this.add(shrinkBack);
		this.add(exploreNetwork);
		this.add(exploreNode);
		this.add(run);
		this.add(step);	
		this.add(settingsButton);
		
		settingsButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				settingsButtonEvent();	
			}
		});
		
		expand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				checkPickedVertices();
				for (Vertex vertex : networkPanel.getPickedVertices())
					((DBNetwork) networkPanel.getNetwork()).extendNeighborhood(
							vertex.getId(), 1, true, new HashSet<Vertex>(), new HashSet<Edge>());
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
							vertex.getId(), 1, false, new HashSet<Vertex>(), new HashSet<Edge>());
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
	
	private void runButtonEvent() {
		if(!running) {
			// go to running mode
			running = true;
			step.setEnabled(false);
			run.setIcon(getPauseIcon());
			new Thread(new Runnable() {				
				@Override			
				public void run() {
					while(running) {
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						stepButtonEvent();
					}
				}
			}).start();
		} else {
			// pause
			running = false;
			run.setIcon(getRunIcon());
			step.setEnabled(true);
		}
	}
	
	private DBNetwork getDBNetwork() {
		if(networkPanel.getNetwork() instanceof DBNetwork) {
			return (DBNetwork)networkPanel.getNetwork();
		} else {
			// this is a special case when the network is delegated to a forest... required for the
			// tree layouts			
			BrowsableForestNetwork forest = (BrowsableForestNetwork)networkPanel.getNetwork();
			return (DBNetwork)forest.getOriginalNetwork();
		}
	}

	private void stepButtonEvent() {		
		if(getDBNetwork().getLastAction().compareToIgnoreCase("exploreNode") == 0) {
			getExploreNodeFrame().refresh();
		} else if(getDBNetwork().getLastAction().compareToIgnoreCase("exploreNetwork") == 0) {
			getExploreNetworkFrame().refresh();
		}
	}
	
	public DBExploreNode exploreNodeFrame(){
		return exploreNodeFrame;
	}
	
	private Icon getRunIcon() {
		if(runIcon == null)
			runIcon = new ImageIcon(getClass().getResource(runIconFile));
		return runIcon;
	}
	
	private Icon getPauseIcon() {
		if(pauseIcon == null)
			pauseIcon = new ImageIcon(getClass().getResource(pauseIconFile));
		return pauseIcon;
	}
	
	private void settingsButtonEvent() {
		String sleepTimeStr = (String)JOptionPane.showInputDialog(networkPanel, "Enter time between updates in milliseconds", "Time between updates", JOptionPane.QUESTION_MESSAGE, null, null, sleepTime);
		if(sleepTimeStr != null) {
			try {
				sleepTime = Long.parseLong(sleepTimeStr);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(networkPanel, "The value that you enter is not an integer", "Incorrect input", JOptionPane.WARNING_MESSAGE, null);
			}
		}
	}
	
}
