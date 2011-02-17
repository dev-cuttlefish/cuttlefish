package ch.ethz.sg.cuttlefish.gui;
import java.io.File;

import javax.swing.*;
public class CuttlefishApplet extends JApplet {
	
	private static final long serialVersionUID = 1L;

	private CuttlefishPanel networkBrowserPanel = null;

	private File configFile = null;

	
	/**
	 * Constructor for Cuttlefish
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
		this.setName("CuttleFish");
		if (configFile == null)
			configFile = new File("configuration.xml");
	}

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

