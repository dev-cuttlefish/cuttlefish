package ch.ethz.sg.cuttlefish.gui2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.sg.cuttlefish.misc.FileChooser;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.ExploreNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;
import ch.ethz.sg.cuttlefish.networks.GraphMLNetwork;
import ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork;
import ch.ethz.sg.cuttlefish.networks.PajekNetwork;
import ch.ethz.sg.cuttlefish.networks.UserNetwork;

public class NetworkInitializer {
	
	public NetworkInitializer() {}
	
	//Store db connect settings
	private static String dbUsername = "";
	private static String dbPassword = "";
	private static String dbSchemaName = "";
	private static String dbUrl = "";
	private int driverIndex = 0;
	
	public void initBrowsableNetwork(BrowsableNetwork network) {
		network.setNetworkLoaded(true);
	}
	
	
	
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
             cxfNetwork.setNetworkLoaded(true);
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
            pajekNetwork.setNetworkLoaded(true);
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
            interactiveCxfNetwork.setNetworkLoaded(true);
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
            graphmlNetwork.setNetworkLoaded(true);
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
            userNetwork.setNetworkLoaded(true);
        } else {
            System.out.println("Input cancelled by user");
        }
	}
	
	public void initCxfDBNetwork(ExploreNetwork _cxfDBNetwork) {
		FileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a CXF file");
		fc.setFileFilter(new FileNameExtensionFilter(".cxf files", "cxf"));
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			ch.ethz.sg.cuttlefish.gui2.Cuttlefish.currentDirectory = fc
					.getCurrentDirectory();
			System.out
					.println("Current directory: " + fc.getCurrentDirectory());
			File file = fc.getSelectedFile();
			_cxfDBNetwork.connect(new CxfNetwork(file) );
			_cxfDBNetwork.setNetworkLoaded(true);
		} else {
			System.out.println("Input cancelled by user");
		}
		return;
	}
	
	public void initDBNetwork(DBNetwork _dbNetwork) {		
		final DBNetwork dbNetwork = _dbNetwork;
	    final JButton connectButton;
	    JButton cancelButton;
	    JLabel urlLabel;
	    JLabel usernameLabel;
	    JLabel passwordLabel;
	    JLabel driverLabel;
	    JLabel schemaNameLabel;
	    final JTextField urlTextField;
	    final JTextField usernameTextField;
	    final JTextField schemaNameTextField;
	    final JPasswordField passwordTextField;
	    final JComboBox driverComboBox;
	    driverComboBox = new JComboBox(new String[]{"MySQL", "PostgreSQL", "SQLite"});
	    driverComboBox.setSelectedIndex(driverIndex);
		urlLabel = new javax.swing.JLabel();
	    schemaNameLabel = new javax.swing.JLabel();
	    usernameLabel = new javax.swing.JLabel();
	    passwordLabel = new javax.swing.JLabel();
	    driverLabel = new JLabel();
	    connectButton = new javax.swing.JButton();
	    cancelButton = new javax.swing.JButton();
	    urlTextField = new javax.swing.JTextField(dbUrl);
	    urlTextField.setText(dbUrl);
	    schemaNameTextField = new javax.swing.JTextField(dbSchemaName);
	    usernameTextField = new javax.swing.JTextField(dbUsername);
	    passwordTextField = new javax.swing.JPasswordField(dbPassword);
	    if (urlTextField.getText().length() > 0 && usernameTextField.getText().length() > 0) {
			connectButton.setEnabled(true);
		} else {
			connectButton.setEnabled(false);
		}
	    final JFrame connectWindow = new JFrame();
		JPanel connectPanel = new JPanel();
	    
		driverLabel.setText("Database Driver");
        urlLabel.setText("Database URL"); 
        schemaNameLabel.setText("Schema name");
        usernameLabel.setText("Username"); 
        passwordLabel.setText("Password");
        

        connectButton.setText("Connect");         
        connectButton.setPreferredSize(new java.awt.Dimension(85, 25));
        cancelButton.setText("Cancel"); 
        cancelButton.setPreferredSize(new java.awt.Dimension(85, 25));
      
        GroupLayout layout = new GroupLayout(connectPanel);
	    connectPanel.setLayout(layout);
	    layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addContainerGap()
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(driverLabel)
	                            .addComponent(urlLabel)
	                            .addComponent(schemaNameLabel)
	                            .addComponent(usernameLabel)
	                            .addComponent(passwordLabel))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	//                            .addComponent(passwordTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
//	                            .addComponent(usernameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
	                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                                .addComponent(urlTextField)
	                                .addComponent(passwordTextField)
	                                .addComponent(usernameTextField)
	                                .addComponent(schemaNameTextField)
	                                .addComponent(driverComboBox, 0, 153, Short.MAX_VALUE))))
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(56, 56, 56)
	                        .addComponent(connectButton)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                        .addComponent(cancelButton)))
	                .addContainerGap())
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(driverLabel)
	                    .addComponent(driverComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(urlLabel)
	                    .addComponent(urlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
   	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(schemaNameLabel)
	                    .addComponent(schemaNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)

	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(usernameLabel)
	                    .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(passwordLabel)
	                    .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(18, 18, 18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(connectButton)
	                    .addComponent(cancelButton))
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
		cancelButton.setEnabled(true);
		connectButton.addActionListener(new ActionListener() {			
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				String driverName;
				String urlName;
				String schemaName;
				dbUsername = usernameTextField.getText();
				dbPassword = passwordTextField.getText();
				driverIndex = driverComboBox.getSelectedIndex();
				dbUrl = urlTextField.getText();
				dbSchemaName = schemaNameTextField.getText();
				if(driverIndex == 0) {
					driverName = "com.mysql.jdbc.Driver";
					urlName = "jdbc:mysql://";
					schemaName = "";					
				} else if (driverIndex == 1) {
					driverName = "org.postgresql.Driver";
					urlName = "jdbc:postgresql://";
					schemaName = schemaNameTextField.getText() + '.';
				} else {
					driverName = "org.sqlite.JDBC";
					urlName = "jdbc:sqlite::/";
					schemaName = "";
				}
				 if (dbNetwork.connect(driverName,  urlName, schemaName, dbUrl + "/" + dbSchemaName,
						   dbUsername, dbPassword)
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
	    connectWindow.setSize(300, 244);
	    connectWindow.setResizable(true);
	    connectWindow.setTitle("Connect to database");
	    connectWindow.setVisible(true);
	}
	
		
}
