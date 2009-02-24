package ch.ethz.sg.cuttlefish.gui;
import java.io.File;

import javax.swing.*;
public class CuttlefishApplet extends JApplet {
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private CuttlefishPanel networkBrowserPanel = null;

	private File configFile = null;

	
	/**
	 * Constructor for cuttlefish
	 * @param string filename of configuration file
	 */
	public CuttlefishApplet() {
		init();
	}

	/**
	 * This method initializes this
	 * @return void
	 */
	public void init() {
		this.setSize(807, 375);
		//this.setSize(1400, 980);
		this.setName("CuttleFish");
//		this.setContentPane(getJContentPane());
		if (configFile == null)
			configFile = new File("configuration.xml");
	}

	/**
	 * This method initializes jContentPane
	 * @return javax.swing.JPanel
	 */
/*	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getNetworkBrowserPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}*/

	/**
	 * This method initializes networkBrowserPanel	
	 * @return ch.ethz.sg.jung.visualisation.NetworkBrowserPanel	
	 */
	private CuttlefishPanel getNetworkBrowserPanel() {
		if (networkBrowserPanel == null) {
			networkBrowserPanel = new CuttlefishPanel(configFile);
		}
		return networkBrowserPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"

