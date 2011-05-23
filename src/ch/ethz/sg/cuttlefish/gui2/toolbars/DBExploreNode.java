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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
		dbNetwork = (DBNetwork)networkPanel.getNetwork();
		initialize();
		exploreNode = this;
		nodeInputValid = false;
		distanceInputValid = false;
	}
	
	private void checkOkEnabled() {		
		if( nodeInputValid && distanceInputValid ) {
			ok.setEnabled(true);
		} else {
			ok.setEnabled(false);
		}
	}
	
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
			distanceInfo.setText("Distance must be a number greater than 0");
			distanceInfo.setForeground(Color.red);
		} else {
			distanceInputValid = true;
			distanceInfo.setText("");
			distanceField.setBorder(BorderFactory.createLineBorder(Color.green));
		}
		checkOkEnabled();
	}
	
	private void checkNodeInput() {
		boolean nodeExists  = dbNetwork.checkNodeId(nodeField.getText());
		if(nodeExists == false) {
			nodeInputValid = false;
			nodeField.setBorder(BorderFactory.createLineBorder(Color.red));
			nodeInfo.setText("Node id does not exist");
			nodeInfo.setForeground(Color.red);
		} else {
			nodeInputValid = true;
			nodeField.setBorder(BorderFactory.createLineBorder(Color.green));
			nodeInfo.setText("");			
		}
		checkOkEnabled();
	}
	
	private void initialize() {	
		nodeLabel = new JLabel("Node ID");
		nodeField = new JTextField("Enter node id");
		nodeInfo = new JLabel("");
		distanceLabel = new JLabel("Distance");
		distanceField = new JTextField("Enter explore distance");
		distanceInfo = new JLabel("Distance must be a number greater than 0");
		ok = new JButton("OK");
		cancel = new JButton("Cancel");
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
		
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints x0y0 = new GridBagConstraints();
		x0y0.fill = GridBagConstraints.HORIZONTAL;
		x0y0.gridy = 0;
		x0y0.insets = new Insets(2, 2, 2, 2);
		x0y0.gridx = 0;
		
		GridBagConstraints x0y1 = new GridBagConstraints();
		x0y0.fill = GridBagConstraints.HORIZONTAL;
		x0y0.gridy = 1;
		x0y0.insets = new Insets(2, 2, 2, 2);
		x0y0.gridx = 0;
		
		GridBagConstraints x0y2 = new GridBagConstraints();
		x0y0.fill = GridBagConstraints.HORIZONTAL;
		x0y0.gridy = 2;
		x0y0.insets = new Insets(2, 2, 2, 2);
		x0y0.gridx = 0;
		
		GridBagConstraints x1y0 = new GridBagConstraints();
		x0y0.fill = GridBagConstraints.HORIZONTAL;
		x0y0.gridy = 0;
		x0y0.insets = new Insets(2, 2, 2, 2);
		x0y0.gridx = 1;
		
		GridBagConstraints x1y1 = new GridBagConstraints();
		x0y0.fill = GridBagConstraints.HORIZONTAL;
		x0y0.gridy = 1;
		x0y0.insets = new Insets(2, 2, 2, 2);
		x0y0.gridx = 1;
		
		GridBagConstraints x1y2 = new GridBagConstraints();
		x0y0.fill = GridBagConstraints.HORIZONTAL;
		x0y0.gridy = 2;
		x0y0.insets = new Insets(2, 2, 2, 2);
		x0y0.gridx = 1;
		
		this.add(nodeLabel, x0y0);
		this.add(distanceLabel, x0y1);
		this.add(ok, x0y2);		
		this.add(nodeField, x1y0);
		this.add(distanceField, x1y1);
		this.add(cancel, x1y2);
		//this.add(nodeInfo);				
		//this.add(distanceInfo);
		//this.setResizable(false);
		this.setSize(300, 300);
	}	
}
