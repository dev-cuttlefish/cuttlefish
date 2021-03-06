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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.networks.BrowsableForestNetwork;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class DBExploreNode extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8502348690688977278L;
	private NetworkPanel networkPanel;
	private JFrame exploreNode;
	private JLabel nodeInfo;
	private JLabel distanceInfo;
	private JLabel nodesCountLabel;
	private JLabel linksCountLabel;
	private JLabel warningLabel;
	private JLabel ignoreDirectionLabel;
	private JLabel info;
	private JLabel info2;
	private boolean nodeInputValid;
	private boolean distanceInputValid;
	private JLabel nodeLabel;
	private JTextField nodeField;
	private JLabel distanceLabel;
	private JTextField distanceField;
	private JButton ok;
	private JButton cancel;
	private JCheckBox ignoreDirection;
	

	
	
	public DBExploreNode(NetworkPanel networkPanel) {
		this.networkPanel = networkPanel;
		setLocationRelativeTo(networkPanel);
		exploreNode = this;
		nodeInputValid = false;
		distanceInputValid = false;
		this.setTitle("Explore node");
		this.setSize(282, 261);
		this.setResizable(false);
		initialize();		
	}
	
	private DBNetwork getDBNetwork() {
		DBNetwork dbNetwork = null;
		if(networkPanel.getNetwork() instanceof DBNetwork) {
			dbNetwork = (DBNetwork)networkPanel.getNetwork();
		} else {
			// this is a special case when the network is delegated to a forest... required for the
			// tree layouts					
			dbNetwork = (DBNetwork)((BrowsableForestNetwork)networkPanel.getNetwork()).getOriginalNetwork();
		}
		return dbNetwork;
	}
	
	/**
	 * This method checks if the OK button should be
	 * enabled or not
	 */
	private void checkOkEnabled() {		
		if( nodeInputValid && distanceInputValid ) {
			ok.setEnabled(true);
			countReachableNodes();
		} else {
			ok.setEnabled(false);
		}
	}
	
	/**
	 * 
	 */
	public void countReachableNodes() {
		try { 
			(new NodesCounter(getDBNetwork(), nodesCountLabel, linksCountLabel, warningLabel, Integer.parseInt(nodeField.getText()), Integer.parseInt(distanceField.getText() ))).execute();
		} catch (NumberFormatException e) {
			nodesCountLabel.setText("Selected nodes: 0");
			linksCountLabel.setText("Selected links: 0");
		}
	}
	
	/**
	 * This method checks if the entered distance is
	 * a number, e.g., and integer greater than 0
	 */
	private void checkDistanceInput() {
		int distance = -1;
		distanceInputValid = false;
		try {
			if (!distanceField.getText().isEmpty())
				distance = Integer.parseInt(distanceField.getText());			
		} catch (NumberFormatException numEx) {
			System.out.println("Distance is not a number");
		}
		
		if(distance <= 0) {
			distanceInputValid = false;
			distanceField.setBorder(BorderFactory.createLineBorder(Color.red));
			distanceInfo.setText("Invalid distance");
			distanceInfo.setForeground(Color.red);
		} else {
			distanceInputValid = true;
			distanceInfo.setText("");
			distanceField.setBorder(BorderFactory.createLineBorder(Color.green));
		}
		checkOkEnabled();
	}
	
	public void update() {
		nodesCountLabel.setText("Selected nodes: 0");
		linksCountLabel.setText("Selected links: 0");
		checkNodeInput();
		checkDistanceInput();
	}
	
	/**
	 * This method checks if the Node ID selected exists
	 * in the database. If not, it displays a message
	 * "Invalid ID".
	 */
	private void checkNodeInput() {
		boolean nodeExists  = getDBNetwork().checkNodeId(nodeField.getText());
		if(nodeExists == false) {
			nodeInputValid = false;
			nodeField.setBorder(BorderFactory.createLineBorder(Color.red));
			nodeInfo.setText("Invalid ID");
			nodeInfo.setForeground(Color.red);
		} else {
			nodeInputValid = true;
			nodeField.setBorder(BorderFactory.createLineBorder(Color.green));
			nodeInfo.setText("");			
		}
		checkOkEnabled();
	}
	
	
	/**
	 * This private method initializes the components
	 */
	private void initialize() {	
		initComponents();
		ok.setEnabled(false);
				
		nodeField.addKeyListener(new KeyListener() {			
			@Override
			public void keyReleased(KeyEvent e) {
				checkNodeInput();
			}			
			@Override
			public void keyPressed(KeyEvent e) {}			
			@Override
			public void keyTyped(KeyEvent e) {}
		});
		distanceField.addKeyListener(new KeyListener() {			
			@Override
			public void keyReleased(KeyEvent e) {
				checkDistanceInput();
			}			
			@Override
			public void keyPressed(KeyEvent e) {}			
			@Override
			public void keyTyped(KeyEvent e) {}
		});
		
		checkOkEnabled();
		
		cancel.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				exploreNode.setVisible(false);
			}
		});
		
		ok.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {			
				getDBNetwork().setNodeFilter("");
				getDBNetwork().setEdgeFilter("");
				getDBNetwork().emptyNetwork();				
				try{
					((DBNetwork) networkPanel.getNetwork()).emptyNetwork();
				} catch(java.lang.ClassCastException ex) {
					//Somebody used the treelayout and changed the
					//network in networkPanel to a forest network, which
					//is needed for the tree layout
					networkPanel.setNetwork(getDBNetwork());					
				}
				
				int distance = Integer.parseInt(distanceField.getText());				
				Set<Vertex> visitedVertices = new HashSet<Vertex>();
				Set<Edge> visitedEdges = new HashSet<Edge>();
				((DBNetwork) networkPanel.getNetwork()).extendNeighborhood(Integer.parseInt(nodeField.getText()), distance, true, visitedVertices, visitedEdges);
				if(ignoreDirection.isSelected()) {
					((DBNetwork) networkPanel.getNetwork()).extendNeighborhood(Integer.parseInt(nodeField.getText()), distance, false, visitedVertices, visitedEdges);
				}
				networkPanel.onNetworkChange();
				networkPanel.getNetworkLayout().resetPropertiesValues();
				networkPanel.repaintViewer();
				networkPanel.stopLayout();
				exploreNode.setVisible(false);
			}
		});
				
	}	
		
	public void refresh() {
		int numNodes = getDBNetwork().getVertexCount();
		int numEdges = getDBNetwork().getEdgeCount();
		getDBNetwork().setNodeFilter("");
		getDBNetwork().setEdgeFilter("");				
		int distance = Integer.parseInt(distanceField.getText());				
		Set<Vertex> visitedVertices = new HashSet<Vertex>();
		Set<Edge> visitedEdges = new HashSet<Edge>();		
		getDBNetwork().extendNeighborhood(Integer.parseInt(nodeField.getText()), distance, true, visitedVertices, visitedEdges);
		if(ignoreDirection.isSelected()) {
			getDBNetwork().extendNeighborhood(Integer.parseInt(nodeField.getText()), distance, false, visitedVertices, visitedEdges);
		}
		if(getDBNetwork().getVertexCount() != numNodes || 
				getDBNetwork().getEdgeCount() != numEdges) {
			networkPanel.onNetworkChange();
		}
	}
	
	/**
	 * NetBeans generated code for aligning the components
	 */
	 private void initComponents() {

	        nodeLabel = new javax.swing.JLabel();
	        distanceLabel = new javax.swing.JLabel();
	        nodeField = new javax.swing.JTextField();
	        distanceField = new javax.swing.JTextField();
	        distanceInfo = new javax.swing.JLabel();
	        nodeInfo = new javax.swing.JLabel();
	        ok = new javax.swing.JButton();
	        cancel = new javax.swing.JButton();
	        nodesCountLabel = new javax.swing.JLabel();
	        linksCountLabel = new javax.swing.JLabel();
	        warningLabel = new javax.swing.JLabel();
	        info = new javax.swing.JLabel();
	        info2 = new javax.swing.JLabel();
	        ignoreDirectionLabel= new javax.swing.JLabel();
	        ignoreDirection = new JCheckBox();
	        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
	        nodeLabel.setText("Source ID");
	        distanceLabel.setText("Distance");
	        nodeField.setText("1");
	        nodeField.setToolTipText("Enter the ID of the source node");
	        distanceField.setText("1");
	        distanceField.setName("distanceField");
	        distanceField.setToolTipText("The degree of separation");
	        ignoreDirectionLabel.setText("Ignore direction");
	        distanceInfo.setForeground(Color.RED);
	        distanceInfo.setText("");
	        nodeInfo.setForeground(Color.RED);
	        nodeInfo.setText("");
	        ok.setText("OK");
	        cancel.setText("Cancel");
	        nodesCountLabel.setText("Selected nodes: ");
	        linksCountLabel.setText("Selected links: ");
	        warningLabel.setForeground(Color.RED);
	        warningLabel.setText("");	 
	        info.setText("Extract the network downstream from a source");
	        info2.setText("node down to a given degree of separation");

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(warningLabel)
	                    .addComponent(linksCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(nodesCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGroup(layout.createSequentialGroup()
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(nodeLabel)
	                            .addComponent(distanceLabel)
	                            .addComponent(ignoreDirectionLabel))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)	                        		
	                            .addComponent(nodeField)
	                            .addComponent(distanceField, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
	                            .addComponent(ignoreDirection))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(nodeInfo)
	                            .addComponent(distanceInfo)))
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(38, 38, 38)
	                        .addComponent(ok)
	                        .addGap(18, 18, 18)
	                        .addComponent(cancel))
	                    .addGroup(layout.createSequentialGroup()
	                    	.addComponent(info))
                    	.addGroup(layout.createSequentialGroup()
    	                    .addComponent(info2)))
	                     
	                .addContainerGap())
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(info))
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(info2))	                    
	                .addGap(18,18,18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(nodeLabel)
	                    .addComponent(nodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(nodeInfo))
	                .addGap(18, 18, 18)	                
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(distanceLabel)
	                    .addComponent(distanceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(distanceInfo))
	                .addGap(18, 18, 18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(ignoreDirection)
	                    .addComponent(ignoreDirectionLabel))
	                .addGap(18, 18, 18)
	                .addComponent(nodesCountLabel)
	                .addGap(9, 9, 9)
	                .addComponent(linksCountLabel)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(warningLabel)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(cancel)
	                    .addComponent(ok))
	                .addContainerGap())
	        );

	        pack();
	    }
	 
	class NodesCounter extends SwingWorker<Object, Object> {
		private DBNetwork dbNetwork;
		private JLabel nodeInfo;
		private int origin;
		private int distance;
		private JLabel warningInfo;
		private JLabel linksInfo;
		public NodesCounter(DBNetwork dbNetwork, JLabel nodeInfo, JLabel linksInfo, JLabel warningInfo, int origin, int distance) {
			this.dbNetwork = dbNetwork;
			this.nodeInfo = nodeInfo;
			this.linksInfo = linksInfo;
			this.warningInfo = warningInfo;
			this.origin = origin;
			this.distance = distance;
		}
		@Override
		protected Object doInBackground() throws Exception {
			Set<Integer> reachable = new HashSet<Integer>();
			reachable.add(origin);
			Set<Integer> lastReachable = new HashSet<Integer>();
			lastReachable.add(origin);
			for(int curDistance = 1; curDistance <= distance; curDistance++) {
				reachable.addAll(dbNetwork.reachableNeighbors(reachable));
				if(curDistance == distance-1)
					lastReachable.addAll(reachable);
			}
			nodeInfo.setText("Selected nodes: " + reachable.size() );
			linksInfo.setText("Selected links: " + dbNetwork.countEdges(lastReachable, reachable));
			if(reachable.size() > 500)
				warningInfo.setText("Warning: Too many nodes selected");
			else
				warningInfo.setText("");
			return null;
		}
	}
	
	// The interface for the functions below can be used for initialization, also used in the applet
	public void setNodeField(String s) {
		nodeField.setText(s);
	}
	
	public void setDistanceField(String s) {
		distanceField.setText(s);
	}
	
	public void clickOk() {
		ok.setEnabled(true);
		ok.doClick();
		ok.setEnabled(false);
	}

}
