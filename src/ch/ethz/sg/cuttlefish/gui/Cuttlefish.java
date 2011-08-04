/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

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

package ch.ethz.sg.cuttlefish.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import ch.ethz.sg.cuttlefish.misc.Utils;

/**
 * Class of the General User Interface for cuttlefish
 */
public class Cuttlefish extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	
	private JMenuBar menu = null;

	private CuttlefishPanel networkBrowserPanel = null;

	private File configFile;

	/**
	 * main method for the execution of cuttlefish
	 * @param args program call argument: filename of the configuration file in xml format
	 * 		  if no xml is in the first argument, "configuration.xml" will be used
	 * @return void
	 */
	public static void main(String[] args) {
		Frame f;
		String filename;
		
		if (args.length == 0)
			filename = "configuration.xml";
		else
			filename = args[0];
		
		f = new Cuttlefish(filename);		
		f.setVisible(true);
	}
	
	/**
	 * Constructor for cuttlefish
	 * @param string filename of configuration file
	 */
	public Cuttlefish(String string) {
		super();
		
		configFile = new File(string);
		menu = new JMenuBar();
		
		if (!configFile.exists())
	    {
			configFile = Utils.createLocalFile("/ch/ethz/sg/cuttlefish/resources/default_configuration.xml", (Object) this);
	    	System.out.println("WARNING: configuration file not found - using default");
	    }
		
		
		initialize();
	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setSize(1000, 700); //The initial size of the user interface is slightly smaller than 1024x768
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(getJContentPane());
		//this.add(menu, BorderLayout.NORTH);
		this.setTitle("Cuttlefish");
	}


	
	/**
	 * This method initializes jContentPane, adding the network browser
	 * @return javax.swing.JPanel the panel with the network
	 */
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			CuttlefishPanel networkBrowserPanel = getNetworkBrowserPanel();
			jContentPane.add(networkBrowserPanel, BorderLayout.CENTER);
			
		}
		return jContentPane;
	}

	/**
	 * This method initializes networkBrowserPanel	
	 * @return ch.ethz.sg.jung.visualisation.NetworkBrowserPanel
	 */
	private CuttlefishPanel getNetworkBrowserPanel() {
		if (networkBrowserPanel == null) {
			networkBrowserPanel = new CuttlefishPanel(configFile, menu);
		}
		return networkBrowserPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
