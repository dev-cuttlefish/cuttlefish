package ch.ethz.sg.cuttlefish.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.sg.cuttlefish.misc.FileChooser;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;
import ch.ethz.sg.cuttlefish.networks.GraphMLNetwork;
import ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork;
import ch.ethz.sg.cuttlefish.networks.PajekNetwork;
import ch.ethz.sg.cuttlefish.networks.UserNetwork;

public class NetworkInitializer {
	
	public NetworkInitializer() {}
	
	public void initBrowsableNetwork(BrowsableNetwork network) {}
	
	
	
	public void initCxfNetwork(CxfNetwork cxfNetwork) {
		 JFileChooser fc = new FileChooser();		 		 
		 fc.setDialogTitle("Select a CXF file");
		 fc.setFileFilter(new FileNameExtensionFilter(".cxf files", "cxf"));
		 int returnVal = fc.showOpenDialog(null);

         if (returnVal == JFileChooser.APPROVE_OPTION) {
        	 ch.ethz.sg.cuttlefish.gui2.Cuttlefish.currentDirectory = fc.getCurrentDirectory();
        	 System.out.println("Current directory: " + fc.getCurrentDirectory());
             File file = fc.getSelectedFile();             
             cxfNetwork.load(file);
         } else {
             System.out.println("Input cancelled by user");
         }
	}
	
	public void initPajekNetwork(PajekNetwork pajekNetwork) {
		 JFileChooser fc = new FileChooser();
		 fc.setDialogTitle("Select a Pajek file");		 
		 int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            pajekNetwork.load(file);
        } else {
            System.out.println("Input cancelled by user");
        }
	}

	public void initInteractiveCxfNetwork(InteractiveCxfNetwork interactiveCxfNetwork) {
		initCxfNetwork(interactiveCxfNetwork);
		JFileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a CEF file");
		fc.setFileFilter(new FileNameExtensionFilter(".cef files", "cef"));
		int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            interactiveCxfNetwork.loadInstructions(file);            
        } else {
            System.out.println("Input cancelled by user");
        }
	}
	
	public void initGraphMLNetwork(GraphMLNetwork graphmlNetwork) {
		JFileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a GraphML file");
		fc.setFileFilter(new FileNameExtensionFilter(".graphml files", "graphml"));
		int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            graphmlNetwork.load(file);
        } else {
            System.out.println("Input cancelled by user");
        }
	}
	
	public void initUserNetwork(UserNetwork userNetwork) {
		JFileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a CFF file");
	    fc.setFileFilter(new FileNameExtensionFilter(".cff files", "cff"));
		int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            userNetwork.load(file);
        } else {
            System.out.println("Input cancelled by user");
        }
	}
	
	
	public void initDBNetwork(DBNetwork _dbNetwork) {
		final DBNetwork dbNetwork = _dbNetwork;

	    final JButton connectButton;
	    JButton cancelButton;
	    JLabel urlLabel;
	    JLabel usernameLabel;
	    JLabel passwordLabel;
	    final JTextField urlTextField;
	    final JTextField usernameTextField;
	    final JPasswordField passwordTextField;
		urlLabel = new javax.swing.JLabel();
	    usernameLabel = new javax.swing.JLabel();
	    passwordLabel = new javax.swing.JLabel();
	    connectButton = new javax.swing.JButton();
	    cancelButton = new javax.swing.JButton();
	    urlTextField = new javax.swing.JTextField();
	    usernameTextField = new javax.swing.JTextField();
	    passwordTextField = new javax.swing.JPasswordField();
	    
	    final JFrame connectWindow = new JFrame();
		JPanel connectPanel = new JPanel();
	    
        urlLabel.setText("Database URL"); 
        usernameLabel.setText("Username"); 
        passwordLabel.setText("Password");         

        connectButton.setText("Connect");  
        connectButton.setPreferredSize(new java.awt.Dimension(85, 25));
        cancelButton.setText("Cancel"); 
        cancelButton.setPreferredSize(new java.awt.Dimension(85, 25));

        urlTextField.setText("");
        usernameTextField.setText("");


        GroupLayout layout = new GroupLayout(connectPanel);
	    connectPanel.setLayout(layout);
	    layout.setHorizontalGroup(
	    		layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    		.addGroup(layout.createSequentialGroup()
	    		.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    		.addGroup(layout.createSequentialGroup()
	    		.addGap(30, 30, 30)
	    		.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    		.addComponent(urlLabel)
	            .addComponent(usernameLabel)
	            .addComponent(passwordLabel))
	            .addGap(18, 18, 18)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
	            .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
	            .addComponent(urlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
	            .addGroup(layout.createSequentialGroup()
	            .addGap(47, 47, 47)
	            .addComponent(connectButton)
	            .addGap(26, 26, 26)
	            .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
	            .addContainerGap(43, Short.MAX_VALUE))
	    );
	    layout.setVerticalGroup(
	    	layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(layout.createSequentialGroup()
	        .addGap(26, 26, 26)
	        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	        .addComponent(urlLabel)
	        .addComponent(urlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	        .addGap(18, 18, 18)
	        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	        .addComponent(usernameLabel)
	        .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	        .addGap(18, 18, 18)
	        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	        .addComponent(passwordLabel)
	        .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	        .addGap(37, 37, 37)
	        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	        .addComponent(connectButton)
	        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	        .addContainerGap(43, Short.MAX_VALUE))
	    );
	    
	    usernameTextField.addKeyListener(new KeyListener() {			
			@Override
			public void keyReleased(KeyEvent e) {
				if (urlTextField.getText().length() > 0 && usernameTextField.getText().length() > 0) {
					connectButton.setEnabled(true);
				} else {
					connectButton.setEnabled(false);
				}
			}			
			@Override
			public void keyPressed(KeyEvent e) {}			
			@Override
			public void keyTyped(KeyEvent e) {}
		});
		urlTextField.addKeyListener(new KeyListener() {			
			@Override
			public void keyReleased(KeyEvent e) {
				if (urlTextField.getText().length() > 0 && usernameTextField.getText().length() > 0) {
					connectButton.setEnabled(true);
				} else {
					connectButton.setEnabled(false);
				}
			}			
			@Override
			public void keyPressed(KeyEvent e) {}			
			@Override
			public void keyTyped(KeyEvent e) {}
		});		
		connectButton.setEnabled(false);
		cancelButton.setEnabled(true);
		connectButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				 if (dbNetwork.connect(urlTextField.getText(),
						   usernameTextField.getText(), passwordTextField.getText())
				 	) {
					 connectWindow.setVisible(false);
					 synchronized (dbNetwork) {
						 dbNetwork.notifyAll();	
					}					 
				 }
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				connectWindow.setVisible(false);
			}
		});
	    
	    connectWindow.add(connectPanel);
	    connectWindow.setSize(330, 220);
	    connectWindow.setTitle("Connect to database");
	    connectWindow.setVisible(true);
	}
	
		
}
