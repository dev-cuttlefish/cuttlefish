package ch.ethz.sg.cuttlefish.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

import sun.misc.Regexp;

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
		 JFileChooser fc = new JFileChooser();
		 fc.setDialogTitle("Select a CXF file");
		 fc.setFileFilter(new FileNameExtensionFilter(".cxf files", "cxf"));
		    fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
			int returnVal = fc.showOpenDialog(null);

         if (returnVal == JFileChooser.APPROVE_OPTION) {
             File file = fc.getSelectedFile();
             cxfNetwork.load(file);
         } else {
             System.out.println("Input cancelled by user");
         }
	}
	
	public void initPajekNetwork(PajekNetwork pajekNetwork) {
		 JFileChooser fc = new JFileChooser();
		 fc.setDialogTitle("Select a Pajek file");		 
		 fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
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
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select a CEF file");
		fc.setFileFilter(new FileNameExtensionFilter(".cef files", "cef"));
	    fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
		int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            interactiveCxfNetwork.loadInstructions(file);            
        } else {
            System.out.println("Input cancelled by user");
        }
	}
	
	public void initGraphMLNetwork(GraphMLNetwork graphmlNetwork) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select a GraphML file");
		fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
		fc.setFileFilter(new FileNameExtensionFilter(".graphml files", "graphml"));
		int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
		    fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
            graphmlNetwork.load(file);
        } else {
            System.out.println("Input cancelled by user");
        }
	}
	
	public void initUserNetwork(UserNetwork userNetwork) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select a CFF file");
	    fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
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
		/*final DBNetwork dbNetwork = _dbNetwork;
		System.out.println("Opening a Database network");
		final JFrame connectWindow = new JFrame();
		JPanel connectPanel = new JPanel();
		
		JLabel dbURL = new JLabel("Database URL");
		JLabel username = new JLabel("Username");
		JLabel password = new JLabel("Password");
		
		final JButton connectButton = new JButton("Connect");
		final JButton cancelButton = new JButton("Cancel");
		final JTextField urlField = new JTextField("");
		final JTextField usernameField = new JTextField("");
		final JPasswordField passwordField = new JPasswordField("");
		
		connectPanel.setSize(300, 200);
		connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.Y_AXIS ) );

		connectPanel.add(dbURL);
		connectPanel.add(urlField);
		connectPanel.add(username);
		connectPanel.add(usernameField);
		connectPanel.add(password);				
		connectPanel.add(passwordField);
		connectPanel.add(connectButton);
		connectPanel.add(cancelButton);	
		connectWindow.add(connectPanel);
		connectWindow.setSize(300, 200);
		connectWindow.setResizable(false);
		connectWindow.setVisible(true);
		
		usernameField.addKeyListener(new KeyListener() {			
			@Override
			public void keyReleased(KeyEvent e) {
				if (urlField.getText().length() > 0 && usernameField.getText().length() > 0) {
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
		urlField.addKeyListener(new KeyListener() {			
			@Override
			public void keyReleased(KeyEvent e) {
				if (urlField.getText().length() > 0 && usernameField.getText().length() > 0) {
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
				 if (dbNetwork.connect(urlField.getText(),
						   usernameField.getText(), passwordField.getText())
				 	) {
					 connectWindow.setVisible(false);
				 }
			}
		});
		*/
	}
}
