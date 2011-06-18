package ch.ethz.sg.cuttlefish.gui2.toolbars;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;

public class DBExploreNode extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8502348690688977278L;
	private NetworkPanel networkPanel;
	private DBNetwork dbNetwork;
	private JFrame exploreNode;
	private JLabel nodeInfo;
	private JLabel distanceInfo;
	private JLabel nodesCountLabel;
	private JLabel linksCountLabel;
	private JLabel warningLabel;
	private boolean nodeInputValid;
	private boolean distanceInputValid;
	private JLabel nodeLabel;
	private JTextField nodeField;
	private JLabel distanceLabel;
	private JTextField distanceField;
	private JButton ok;
	private JButton cancel;
	
	
	public DBExploreNode(NetworkPanel networkPanel) {
		this.networkPanel = networkPanel;
		exploreNode = this;
		nodeInputValid = false;
		distanceInputValid = false;
		dbNetwork = (DBNetwork)networkPanel.getNetwork();
		this.setTitle("Explore node");
		this.setSize(284, 211);
		initialize();		
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
	private void countReachableNodes() {
		(new NodesCounter(dbNetwork, nodesCountLabel, linksCountLabel, warningLabel, Integer.parseInt(nodeField.getText()), Integer.parseInt(distanceField.getText() ))).execute();
	}
	
	/**
	 * This method checks if the entered distance is
	 * a number, e.g., and integer greater than 0
	 */
	private void checkDistanceInput() {
		int distance = -1;
		distanceInputValid = false;
		try {
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
	
	/**
	 * This method checks if the Node ID selected exists
	 * in the database. If not, it displays a message
	 * "Invalid ID".
	 */
	private void checkNodeInput() {
		boolean nodeExists  = dbNetwork.checkNodeId(nodeField.getText());
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
				dbNetwork.setNodeFilter("");
				dbNetwork.setEdgeFilter("");
				dbNetwork.emptyNetwork();
				boolean forward = true;
				((DBNetwork) networkPanel.getNetwork()).emptyNetwork();
				int distance = Integer.parseInt(distanceField.getText());				
				((DBNetwork) networkPanel.getNetwork()).extendNeighborhood(Integer.parseInt(nodeField.getText()), distance, forward);
				networkPanel.onNetworkChange();
				networkPanel.getNetworkLayout().reset();
				networkPanel.repaintViewer();
				networkPanel.stopLayout();
				exploreNode.setVisible(false);
			}
		});
				
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

	        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
	        nodeLabel.setText("Node Id");
	        distanceLabel.setText("Distance");
	        nodeField.setText("");
	        distanceField.setText("");
	        distanceField.setName("distanceField");

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


	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                .addGap(34, 34, 34)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(nodeLabel)
	                            .addComponent(distanceLabel))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(distanceField, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
	                            .addComponent(nodeField, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
	                            .addGroup(layout.createSequentialGroup()
	                                .addComponent(nodeInfo)
	                                .addGap(38, 38, 38))
	                            .addComponent(distanceInfo)))
	                    .addComponent(nodesCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(linksCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(warningLabel))
	                .addContainerGap())
	            .addGroup(layout.createSequentialGroup()
	                .addGap(71, 71, 71)
	                .addComponent(ok)
	                .addGap(18, 18, 18)
	                .addComponent(cancel)
	                .addContainerGap(74, Short.MAX_VALUE))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
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
	                .addComponent(nodesCountLabel)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(linksCountLabel)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(warningLabel)
	                .addGap(18, 18, 18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(cancel)
	                    .addComponent(ok))
	                .addContainerGap(25, Short.MAX_VALUE))
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
			System.out.println("Worker counted " + reachable.size());
			nodeInfo.setText("Selected nodes: " + reachable.size() );
			linksInfo.setText("Selected links: " + dbNetwork.countEdges(lastReachable));
			if(reachable.size() > 500)
				warningInfo.setText("Warning: Selecting more than 500 nodes may take a long time to visualize");
			else
				warningInfo.setText("");
			return null;
		}
	}
}
