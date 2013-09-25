package ch.ethz.sg.cuttlefish.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.sg.cuttlefish.misc.FileChooser;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;
import ch.ethz.sg.cuttlefish.networks.ExploreNetwork;
import ch.ethz.sg.cuttlefish.networks.GraphMLNetwork;
import ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork;
import ch.ethz.sg.cuttlefish.networks.JsonNetwork;
import ch.ethz.sg.cuttlefish.networks.PajekNetwork;
import ch.ethz.sg.cuttlefish.networks.UserNetwork;

public class NetworkInitializer {

	// Store db connect settings
	private static String dbUsername = "";
	private static String dbPassword = "";
	private static String dbSchemaName = "";
	private static String dbUrl = "";
	private int driverIndex = 0;

	private File networkFile;

	public NetworkInitializer() {

	}

	public BrowsableNetwork initNetwork(String className) {
		BrowsableNetwork network = null;

		try {
			if (className.equalsIgnoreCase(CxfNetwork.class.getSimpleName())) {
				network = initCxfNetwork();

			} else if (className.equalsIgnoreCase(PajekNetwork.class
					.getSimpleName())) {
				network = initPajekNetwork();

			} else if (className.equalsIgnoreCase(InteractiveCxfNetwork.class
					.getSimpleName())) {
				network = initInteractiveCxfNetwork();

			} else if (className.equalsIgnoreCase(GraphMLNetwork.class
					.getSimpleName())) {
				network = initGraphMLNetwork();

			} else if (className.equalsIgnoreCase(UserNetwork.class
					.getSimpleName())) {
				network = initUserNetwork();

			} else if (className.equalsIgnoreCase(ExploreNetwork.class
					.getSimpleName())) {
				network = initCxfDBNetwork();

			} else if (className.equalsIgnoreCase(DBNetwork.class
					.getSimpleName())) {
				network = initDBNetwork();

			} else if (className.equalsIgnoreCase(JsonNetwork.class
					.getSimpleName())) {
				network = initJsonNetwork();

			} else {
				network = initBrowsableNetwork();
			}

		} catch (FileNotFoundException e) {
			network = null;
			ch.ethz.sg.cuttlefish.Cuttlefish.err("File not found! "
					+ networkFile.getAbsolutePath());

		} catch (IOException e) {
			network = null;
			e.printStackTrace();
		}

		return network;
	}

	private BrowsableNetwork initBrowsableNetwork() {
		BrowsableNetwork network = new BrowsableNetwork();
		network.setNetworkLoaded(true);

		return network;
	}

	private CxfNetwork initCxfNetwork() throws FileNotFoundException {
		CxfNetwork cxfNetwork = null;

		JFileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a CXF file");
		fc.setFileFilter(new FileNameExtensionFilter(".cxf files", "cxf"));
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			ch.ethz.sg.cuttlefish.gui.Cuttlefish.currentDirectory = fc
					.getCurrentDirectory();
			System.out
					.println("Current directory: " + fc.getCurrentDirectory());
			networkFile = fc.getSelectedFile();
			cxfNetwork = new CxfNetwork();
			cxfNetwork.load(networkFile);
			cxfNetwork.setNetworkLoaded(true);
		} else {
			System.out.println("Input cancelled by user");
		}

		return cxfNetwork;
	}

	private PajekNetwork initPajekNetwork() throws FileNotFoundException {
		PajekNetwork pajekNetwork = null;

		JFileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a Pajek file");
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			networkFile = fc.getSelectedFile();
			pajekNetwork = new PajekNetwork();
			pajekNetwork.load(networkFile);
			pajekNetwork.setNetworkLoaded(true);
		} else {
			System.out.println("Input cancelled by user");
		}

		return pajekNetwork;
	}

	private InteractiveCxfNetwork initInteractiveCxfNetwork()
			throws FileNotFoundException {
		InteractiveCxfNetwork interactiveCxfNetwork = null;

		JFileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a CXF file");
		fc.setFileFilter(new FileNameExtensionFilter(".cxf files", "cxf"));
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			ch.ethz.sg.cuttlefish.gui.Cuttlefish.currentDirectory = fc
					.getCurrentDirectory();
			System.out
					.println("Current directory: " + fc.getCurrentDirectory());
			networkFile = fc.getSelectedFile();
			interactiveCxfNetwork = new InteractiveCxfNetwork();
			interactiveCxfNetwork.load(networkFile);
			interactiveCxfNetwork.setNetworkLoaded(true);
		} else {
			System.out.println("Input cancelled by user");
			return null;
		}

		fc = new FileChooser();
		fc.setDialogTitle("Select a CEF file");
		fc.setFileFilter(new FileNameExtensionFilter(".cef files", "cef"));
		returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			networkFile = fc.getSelectedFile();
			interactiveCxfNetwork.loadInstructions(networkFile);
		} else {
			System.out.println("Input cancelled by user");
		}

		return interactiveCxfNetwork;
	}

	private GraphMLNetwork initGraphMLNetwork() throws FileNotFoundException {
		GraphMLNetwork graphmlNetwork = null;

		JFileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a GraphML file");
		fc.setFileFilter(new FileNameExtensionFilter(".graphml files",
				"graphml"));
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			networkFile = fc.getSelectedFile();
			graphmlNetwork = new GraphMLNetwork();
			graphmlNetwork.load(networkFile);
			graphmlNetwork.setNetworkLoaded(true);
		} else {
			System.out.println("Input cancelled by user");
		}

		return graphmlNetwork;
	}

	private UserNetwork initUserNetwork() {
		UserNetwork userNetwork = null;

		JFileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a CFF file");
		fc.setFileFilter(new FileNameExtensionFilter(".cff files", "cff"));
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			networkFile = fc.getSelectedFile();
			userNetwork = new UserNetwork();
			userNetwork.load(networkFile);
			userNetwork.setNetworkLoaded(true);
		} else {
			System.out.println("Input cancelled by user");
		}

		return userNetwork;
	}

	private ExploreNetwork initCxfDBNetwork() throws FileNotFoundException {
		ExploreNetwork cxfDBNetwork = null;

		FileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a CXF file");
		fc.setFileFilter(new FileNameExtensionFilter(".cxf files", "cxf"));
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			ch.ethz.sg.cuttlefish.gui.Cuttlefish.currentDirectory = fc
					.getCurrentDirectory();
			System.out
					.println("Current directory: " + fc.getCurrentDirectory());
			networkFile = fc.getSelectedFile();
			cxfDBNetwork = new ExploreNetwork();
			cxfDBNetwork.connect(new CxfNetwork(networkFile));
			cxfDBNetwork.setNetworkLoaded(true);
		} else {
			System.out.println("Input cancelled by user");
		}

		return cxfDBNetwork;
	}

	private DBNetwork initDBNetwork() {
		final DBNetwork dbNetwork = new DBNetwork();
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
		driverComboBox = new JComboBox(new String[] { "MySQL", "PostgreSQL",
				"SQLite" });
		driverComboBox.setSelectedIndex(driverIndex);
		urlLabel = new javax.swing.JLabel();
		schemaNameLabel = new javax.swing.JLabel();
		usernameLabel = new javax.swing.JLabel();
		passwordLabel = new javax.swing.JLabel();
		driverLabel = new JLabel();
		connectButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		urlTextField = new javax.swing.JTextField(dbUrl);
		schemaNameTextField = new javax.swing.JTextField(dbSchemaName);
		usernameTextField = new javax.swing.JTextField(dbUsername);
		passwordTextField = new javax.swing.JPasswordField(dbPassword);
		if (urlTextField.getText().length() > 0
				&& usernameTextField.getText().length() > 0) {
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
		urlTextField.setColumns(21);

		connectButton.setText("Connect");
		connectButton.setPreferredSize(new java.awt.Dimension(85, 25));
		cancelButton.setText("Cancel");
		cancelButton.setPreferredSize(new java.awt.Dimension(85, 25));

		GroupLayout layout = new GroupLayout(connectPanel);
		layout.setHorizontalGroup(layout
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addContainerGap()
																.addGroup(
																		layout.createParallelGroup(
																				Alignment.LEADING)
																				.addComponent(
																						driverLabel)
																				.addComponent(
																						urlLabel)
																				.addComponent(
																						schemaNameLabel)
																				.addComponent(
																						usernameLabel)
																				.addComponent(
																						passwordLabel))
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				Alignment.LEADING,
																				false)
																				.addComponent(
																						urlTextField)
																				.addComponent(
																						passwordTextField)
																				.addComponent(
																						usernameTextField)
																				.addComponent(
																						schemaNameTextField)
																				.addComponent(
																						driverComboBox,
																						0,
																						153,
																						Short.MAX_VALUE)))
												.addGroup(
														Alignment.TRAILING,
														layout.createSequentialGroup()
																.addContainerGap()
																.addComponent(
																		connectButton,
																		GroupLayout.PREFERRED_SIZE,
																		94,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		ComponentPlacement.UNRELATED)
																.addComponent(
																		cancelButton,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(driverLabel)
												.addComponent(
														driverComboBox,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(urlLabel)
												.addComponent(
														urlTextField,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(schemaNameLabel)
												.addComponent(
														schemaNameTextField,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(usernameLabel)
												.addComponent(
														usernameTextField,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(passwordLabel)
												.addComponent(
														passwordTextField,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addGap(18)
								.addGroup(
										layout.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(
														cancelButton,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														connectButton,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addContainerGap(GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));
		connectPanel.setLayout(layout);

		usernameTextField.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (urlTextField.getText().length() > 0
						&& usernameTextField.getText().length() > 0) {
					connectButton.setEnabled(true);
				} else {
					connectButton.setEnabled(false);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		urlTextField.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (urlTextField.getText().length() > 0
						&& usernameTextField.getText().length() > 0) {
					connectButton.setEnabled(true);
				} else {
					connectButton.setEnabled(false);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		cancelButton.setEnabled(true);
		connectButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				String driverName;
				String urlName;
				dbUsername = usernameTextField.getText();
				dbPassword = passwordTextField.getText();
				driverIndex = driverComboBox.getSelectedIndex();
				dbUrl = urlTextField.getText();
				if (driverIndex == 0) {
					driverName = "com.mysql.jdbc.Driver";
					urlName = "jdbc:mysql://";
					dbSchemaName = "";
				} else if (driverIndex == 1) {
					driverName = "org.postgresql.Driver";
					urlName = "jdbc:postgresql://";
					dbSchemaName = schemaNameTextField.getText();
				} else {
					driverName = "org.sqlite.JDBC";
					urlName = "jdbc:sqlite::/";
					dbSchemaName = "";
				}
				if (dbNetwork.connect(driverName, urlName, dbSchemaName, dbUrl,
						dbUsername, dbPassword)) {
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
				dbNetwork.setNetworkLoaded(false);
				connectWindow.setVisible(false);
			}
		});

		connectWindow.getContentPane().add(connectPanel);
		connectWindow.setSize(386, 244);
		connectWindow.setResizable(true);
		connectWindow.setTitle("Connect to database");
		connectWindow.setVisible(true);

		return dbNetwork;
	}

	private JsonNetwork initJsonNetwork() throws IOException {
		JsonNetwork jsonNetwork = null;

		JFileChooser fc = new FileChooser();
		fc.setDialogTitle("Select a Pajek file");
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			networkFile = fc.getSelectedFile();
			jsonNetwork = new JsonNetwork();
			jsonNetwork.load(networkFile);
			jsonNetwork.setNetworkLoaded(true);
		} else {
			System.out.println("Input cancelled by user");
		}

		return jsonNetwork;
	}

}
