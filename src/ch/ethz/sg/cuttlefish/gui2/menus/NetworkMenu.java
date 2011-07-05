/*
  
    Copyright (C) 2011  Markus Michael Geipel, David Garcia Becerra,
    Petar Tsankov

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

package ch.ethz.sg.cuttlefish.gui2.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import ch.ethz.sg.cuttlefish.gui2.Cuttlefish;
import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.CxfSaver;
import ch.ethz.sg.cuttlefish.misc.FileChooser;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;

public class NetworkMenu extends AbstractMenu implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OpenMenu openMenu;
	private ExportMenu exportMenu;
	private JMenuItem newNetwork;
	private JMenuItem saveNetwork;
	private JMenuItem saveAsNetwork;
	private JMenuItem exitNetwork;

	public NetworkMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars, OpenMenu openMenu, ExportMenu exportMenu) {
		super(networkPanel, toolbars);
		this.openMenu = openMenu;
		this.exportMenu = exportMenu;
		this.setText("Network");
		this.setMnemonic('N');
		initialize();
		openMenu.addObserver(this);
	}
	
	private void initialize() {
		newNetwork = new JMenuItem("New");
		saveNetwork = new JMenuItem("Save");
		saveAsNetwork = new JMenuItem("Save as...");
		exitNetwork = new JMenuItem("Exit");
		newNetwork.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newNetwork.setMnemonic('N');
		saveNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveNetwork.setMnemonic('S');
		saveAsNetwork.setMnemonic('a');
		exitNetwork.setMnemonic('x');
		exportMenu.setMnemonic('E');
		add(newNetwork);
		add(openMenu);
		addSeparator();		
		add(saveNetwork);
		saveNetwork.setEnabled(false);
		add(saveAsNetwork);
		add(exportMenu);
		addSeparator();
		add(exitNetwork);
		
		newNetwork.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean directed = false;
				String answer = (String)JOptionPane.showInputDialog(networkPanel, "Choose network type:", "Network type", JOptionPane.QUESTION_MESSAGE, null, new String[]{"undirected", "directed"}, "undirected");
				if(answer != null) {
					if(answer.compareToIgnoreCase("directed") == 0)
						directed = true;
				}
				networkPanel.setNetwork(new CxfNetwork());
				((CxfNetwork)networkPanel.getNetwork()).setDirected(directed);
				networkPanel.setLayout("ARFLayout");
				toolbars.getDBToolbar().setVisible(false);
				toolbars.getSimulationToolbar().setVisible(false);
				saveNetwork.setEnabled(false);
			}
		});
		
		exitNetwork.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		saveNetwork.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				File saveTo = new File(Cuttlefish.currentDirectory.toString() + '/' + ((CxfNetwork)networkPanel.getNetwork()).getCxfName());
				if(saveTo.exists())
					saveTo.delete();
				try {
					saveTo.createNewFile();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,e1.getMessage(),"Could not write to file",JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}				
				CxfSaver saver = new CxfSaver((BrowsableNetwork)networkPanel.getNetwork(), networkPanel.getNetworkLayout());
				saver.save(saveTo);
			}
		});
		
		saveAsNetwork.addActionListener(new java.awt.event.ActionListener() {			
			public void actionPerformed(java.awt.event.ActionEvent e) {
				//The action is opening a dialog and saving in Cxf on the selected file
				JFileChooser fc = new FileChooser();
				fc.setSelectedFile(new File(((BrowsableNetwork)networkPanel.getNetwork()).getName()+".cxf"));
				int returnVal = fc.showSaveDialog(null);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                try {
						file.createNewFile();
						CxfSaver saver = new CxfSaver((BrowsableNetwork)networkPanel.getNetwork(), networkPanel.getNetworkLayout());
						saver.save(file);
					} catch (IOException ioEx) {
						JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
						System.err.println("Impossible to write");
						ioEx.printStackTrace();
					}
	            }
			}
		});
	}

	@Override
	public void update(Subject o) {
		if(networkPanel.getNetwork() instanceof CxfNetwork) {
			saveNetwork.setEnabled(true);
		} else {
			saveNetwork.setEnabled(false);
		}
	}

}
