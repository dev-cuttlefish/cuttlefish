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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Utils;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
/**
 * Class of the General User Interface for cuttlefish
 */
public class Cuttlefish extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

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
		
		File configfile = new File(filename);
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
		this.setTitle("Cuttlefish");
	}

	/**
	 * Prototype Menu for the Cuttlefish GUI	
	 * @return
	 */
//	private JMenuBar createMenu() {
//		JMenuBar menubar = new JMenuBar();
//		
//		JMenu importMenu = new JMenu("Open");
//        importMenu.setMnemonic(KeyEvent.VK_O);
//
//        JMenuItem importCxfFile = new JMenuItem("Cxf file", KeyEvent.VK_T);
//        importCxfFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));        
//        importMenu.add(importCxfFile);
//        JMenuItem importPajekFile = new JMenuItem("Pajek file", KeyEvent.VK_T);
//        importPajekFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));        
//        importMenu.add(importPajekFile);
//        JMenuItem importCffFile = new JMenuItem("Cff file", KeyEvent.VK_T);
//        importCffFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));        
//        importMenu.add(importCffFile);
//        
//        menubar.add(importMenu);
//        
//        JMenu exportMenu = new JMenu("Export");
//        JMenuItem exportAdjacencyMatrix = new JMenuItem("Adjacency matrix");
//        exportMenu.add(exportAdjacencyMatrix);
//        JMenuItem exportTikz = new JMenuItem("Tikz");
//        exportMenu.add(exportTikz);
//        JMenuItem exportPdf = new JMenuItem("Pdf");
//        exportMenu.add(exportPdf);
//        
//        menubar.add(exportMenu);
//        
//		return menubar;
//	}
	
	/**
	 * Prototype toolbar for mouse edit type
	 */
//	private JToolBar createMouseToolbar(EditingModalGraphMouse<Vertex, Edge> graphMouse) {
//		final EditingModalGraphMouse<Vertex, Edge> mouse = graphMouse;
//		JToolBar toolbar = new JToolBar();
//		toolbar.setFloatable(true);
//		toolbar.setRollover(true);
//		ImageIcon transformingIcon = new ImageIcon("icons/left_ptr.png");
//		JButton transformingButton = new JButton(transformingIcon);
//		transformingButton.setToolTipText("Set mouse to transforming mode");
//		transformingButton.addActionListener( new java.awt.event.ActionListener() {
//				@Override
//				public void actionPerformed(java.awt.event.ActionEvent e) {
//				mouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
//				}
//			}
//		);	
//		//transformingButton.setText("Transform");
//		ImageIcon pickingIcon = new ImageIcon("icons/closedhand.png");
//		JButton pickingButton = new JButton(pickingIcon);
//		pickingButton.setToolTipText("Set mouse to picking mode");
//		pickingButton.addActionListener( new java.awt.event.ActionListener() {
//			@Override
//				public void actionPerformed(java.awt.event.ActionEvent e) {
//					mouse.setMode(ModalGraphMouse.Mode.PICKING);
//				}
//			}
//		);		
//		ImageIcon editIcon = new ImageIcon("icons/crosshair.png");
//		JButton editButton = new JButton(editIcon);
//		editButton.setToolTipText("Set mouse to editing mode");
//		editButton.addActionListener( new java.awt.event.ActionListener() {
//			@Override
//				public void actionPerformed(java.awt.event.ActionEvent e) {
//					mouse.setMode(ModalGraphMouse.Mode.EDITING);
//				}
//			}
//		);
//		toolbar.add(transformingButton);
//		toolbar.add(pickingButton);
//		toolbar.add(editButton);
//		return toolbar;
//	}	
	
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
			// Adding the prototype menu
			//this.setJMenuBar(createMenu());
			// Adding prototype mouse toolbar
			//jContentPane.add(createMouseToolbar(networkBrowserPanel.getMouse()), BorderLayout.SOUTH);
			
		}
		return jContentPane;
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
