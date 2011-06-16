package ch.ethz.sg.cuttlefish.gui2.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;


import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;
import edu.uci.ics.jung.visualization.control.GraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;

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
	private JComboBox nodeTables = null;
	private JComboBox edgeTables = null;
	private boolean enabled = false;
	private boolean nodeTableInitialized = false;
	private boolean edgeTableInitialized = false;
	
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
		if( networkPanel.getNetwork() instanceof DBNetwork) {
			super.setVisible(b);
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
	


	private void initialize() {
		exploreNetwork = new JButton("Explore Network");
		exploreNode = new JButton("Explore Node");
		expand = new JButton(new ImageIcon(getClass().getResource(expandIcon)));
		expandBack = new JButton(new ImageIcon(getClass().getResource(expandBackIcon)));
		shrink = new JButton(new ImageIcon(getClass().getResource(shrinkIcon)));
		shrinkBack = new JButton(new ImageIcon(getClass().getResource(shrinkBackIcon)));
		setExploreButtonsEnabled(false);
		setNetworkButtonsEnabled(false);
		
		nodeTables = new JComboBox();
		nodeTables.setEditable(false);
		nodeTables.insertItemAt("<Select a node table>", 0);
		nodeTables.setSelectedIndex(0);
		edgeTables = new JComboBox();
		edgeTables.setEditable(false);
		edgeTables.insertItemAt("<Select an edge table>", 0);
		edgeTables.setSelectedIndex(0);
		
		nodeTables.addFocusListener(new FocusListener() {			
			@Override
			public void focusLost(FocusEvent e) {		
			}			
			@Override
			public void focusGained(FocusEvent e) {
				if(!nodeTableInitialized) {
					Collection<String> tables = ((DBNetwork)networkPanel.getNetwork()).getNodeTables( ((DBNetwork)networkPanel.getNetwork()).getSchemaName());
					int itemCount = 1;;
					for(String nodeTable : tables) {
						nodeTables.insertItemAt(nodeTable, itemCount);
						itemCount++;
					}
					nodeTableInitialized = true;
				}
			}
		});
		
		nodeTables.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(nodeTables.getSelectedIndex() > 0)
					((DBNetwork)networkPanel.getNetwork()).setNodeTable((String)nodeTables.getSelectedItem());
			}
		});

		edgeTables.addFocusListener(new FocusListener() {			
			@Override
			public void focusLost(FocusEvent e) {		
			}			
			@Override
			public void focusGained(FocusEvent e) {
				if(!edgeTableInitialized) {
					Collection<String> tables = ((DBNetwork)networkPanel.getNetwork()).getEdgeTables( ((DBNetwork)networkPanel.getNetwork()).getSchemaName());					
					int itemCount = 1;
					for(String edgeTable : tables) {
						edgeTables.insertItemAt(edgeTable, itemCount);
						itemCount++;
					}
					edgeTableInitialized = true;
				}
			}
		});
		edgeTables.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(edgeTables.getSelectedIndex() > 0 && nodeTables.getSelectedIndex() > 0) {
					setNetworkButtonsEnabled(true);
				} else {
					setNetworkButtonsEnabled(false);
				}
			}
		});
		
		edgeTables.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(edgeTables.getSelectedIndex() > 0)
					((DBNetwork)networkPanel.getNetwork()).setEdgeTable((String)edgeTables.getSelectedItem());
			}
		});
		nodeTables.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(edgeTables.getSelectedIndex() > 0 && nodeTables.getSelectedIndex() > 0) {
					setNetworkButtonsEnabled(true);
				} else {
					setNetworkButtonsEnabled(false);
				}
			}
		});
		
		exploreNetwork.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame exploreNetworkFrame = new DBExploreNetwork(networkPanel);
				exploreNetworkFrame.setVisible(true);
			}
		});
		
		exploreNode.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame exploreNodeFrame = new DBExploreNode(networkPanel);
				exploreNodeFrame.setVisible(true);				
			}
		});
		
		this.add(nodeTables);
		this.add(edgeTables);
		this.add(expand);
		this.add(expandBack);
		this.add(shrink);
		this.add(shrinkBack);		
		this.add(exploreNetwork);
		this.add(exploreNode);
		
		expand.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				for (Vertex vertex : networkPanel.getPickedVertices())
					((DBNetwork) networkPanel.getNetwork()).extendNeighborhood(vertex.getId(), 1, true);
				networkPanel.onNetworkChange();
                networkPanel.repaintViewer();
           }
		});
		
		expandBack.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				for (Vertex vertex : networkPanel.getPickedVertices())
					((DBNetwork) networkPanel.getNetwork()).extendNeighborhood(vertex.getId(), 1, false);
				networkPanel.onNetworkChange();
				networkPanel.repaintViewer();
           }
		});
		
		
		shrink.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				for (Vertex vertex : networkPanel.getPickedVertices())
					((DBNetwork) networkPanel.getNetwork()).shrinkVertex(vertex);
				networkPanel.onNetworkChange();
				networkPanel.repaintViewer();
    		}
		});
		
		shrinkBack.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				for (Vertex vertex : networkPanel.getPickedVertices())
					((DBNetwork) networkPanel.getNetwork()).backShrinkVertex(vertex);
				networkPanel.onNetworkChange();
				networkPanel.repaintViewer();
    		}
		});

	}


}
