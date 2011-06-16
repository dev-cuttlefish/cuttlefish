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

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	private JPanel exploreNodePanel;
	private JLabel nodeInfo;
	private JLabel distanceInfo;
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
		this.setSize(285, 180);
		initialize();		
	}
	
	/**
	 * This method checks if the OK button should be
	 * enabled or not
	 */
	private void checkOkEnabled() {		
		if( nodeInputValid && distanceInputValid ) {
			ok.setEnabled(true);
		} else {
			ok.setEnabled(false);
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
		exploreNodePanel = new JPanel();
		nodeLabel = new javax.swing.JLabel();
		distanceLabel = new javax.swing.JLabel();
		ok = new javax.swing.JButton();
		cancel = new javax.swing.JButton();
		nodeField = new javax.swing.JTextField();
		distanceField = new javax.swing.JTextField();
		nodeInfo = new javax.swing.JLabel();
		distanceInfo = new javax.swing.JLabel();

		nodeLabel.setText("Node Id");
		distanceLabel.setText("Distance");
		ok.setText("OK");
		cancel.setText("Cancel");
		nodeField.setText("");
		nodeField.setPreferredSize(new java.awt.Dimension(80, 19));
		distanceField.setText("");
		distanceField.setPreferredSize(new java.awt.Dimension(80, 19));
		nodeInfo.setText("");
		distanceInfo.setText("");

		GroupLayout layout = new GroupLayout(exploreNodePanel);
		this.add(exploreNodePanel);
		exploreNodePanel.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addGap(20, 20,
																		20)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup(
																						layout.createSequentialGroup()
																								.addComponent(
																										distanceLabel)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addComponent(
																										distanceField,
																										javax.swing.GroupLayout.PREFERRED_SIZE,
																										javax.swing.GroupLayout.DEFAULT_SIZE,
																										javax.swing.GroupLayout.PREFERRED_SIZE)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addComponent(
																										distanceInfo))
																				.addGroup(
																						layout.createSequentialGroup()
																								.addComponent(
																										nodeLabel)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																								.addComponent(
																										nodeField,
																										javax.swing.GroupLayout.PREFERRED_SIZE,
																										javax.swing.GroupLayout.DEFAULT_SIZE,
																										javax.swing.GroupLayout.PREFERRED_SIZE)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addComponent(
																										nodeInfo))))
												.addGroup(
														layout.createSequentialGroup()
																.addGap(55, 55,
																		55)
																.addComponent(
																		ok)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		cancel)))
								.addContainerGap(26, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(31, 31, 31)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(nodeLabel)
												.addComponent(
														nodeField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(nodeInfo))
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(distanceLabel)
												.addGroup(
														layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		distanceField,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		distanceInfo)))
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(ok)
												.addComponent(cancel))
								.addContainerGap(34, Short.MAX_VALUE)));
	}
}
