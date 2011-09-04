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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import ch.ethz.sg.cuttlefish.gui2.NetworkInitializer;
import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.gui2.toolbars.DBToolbar;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;
import ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork;

public class OpenMenu extends AbstractMenu implements Subject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuItem cxfNetwork;
	private JMenuItem dbNetwork;
	private JMenuItem interactiveNetwork;
	private JMenuItem exploreNetwork;
	private JMenuItem pajekNetwork;
	private JMenuItem graphmlNetwork;
	private JMenuItem cffNetwork;
	private JMenuItem testSimulation;		
	private JMenuItem baSimulation;
	private List<Observer> observers;
	
	private HashMap<JMenuItem, Class<?> > networkClassMap;

	public OpenMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super(networkPanel, toolbars);
		observers = new ArrayList<Observer>();
		initialize();
		this.setText("Open");
	}
	
	private void initialize() {
		cxfNetwork = new JMenuItem("Cxf network");
		interactiveNetwork = new JMenuItem("Interactive network");
		exploreNetwork = new JMenuItem("Explore cxf network");
		dbNetwork = new JMenuItem("Database network");
		pajekNetwork = new JMenuItem("Pajek network");
		graphmlNetwork = new JMenuItem("GraphML network");
		cffNetwork = new JMenuItem("CFF network");
		testSimulation = new JMenuItem("Test simulation");		
		baSimulation = new JMenuItem("BA simulation");
		
		cxfNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		cxfNetwork.setMnemonic('C');
		exploreNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		cxfNetwork.setMnemonic('E');
		dbNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
		dbNetwork.setMnemonic('D');
		
		networkClassMap = new HashMap<JMenuItem, Class<?> >();
		try {
			networkClassMap.put(cxfNetwork, Class.forName("ch.ethz.sg.cuttlefish.networks.CxfNetwork") );
			networkClassMap.put(exploreNetwork, Class.forName("ch.ethz.sg.cuttlefish.networks.ExploreNetwork") );			
			networkClassMap.put(dbNetwork, Class.forName("ch.ethz.sg.cuttlefish.networks.DBNetwork") );
			networkClassMap.put(interactiveNetwork, Class.forName("ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork") );
			networkClassMap.put(pajekNetwork, Class.forName("ch.ethz.sg.cuttlefish.networks.PajekNetwork") );
			networkClassMap.put(graphmlNetwork, Class.forName("ch.ethz.sg.cuttlefish.networks.GraphMLNetwork") );
			networkClassMap.put(cffNetwork, Class.forName("ch.ethz.sg.cuttlefish.networks.StaticCxfNetwork") );
			networkClassMap.put(testSimulation, Class.forName("ch.ethz.sg.cuttlefish.networks.TestSimulation") );
			networkClassMap.put(baSimulation, Class.forName("ch.ethz.sg.cuttlefish.networks.BASimulation") );
		} catch (ClassNotFoundException e1) {
			JOptionPane.showMessageDialog (null, "Could not find a class for network", "Network class error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
		
		this.setMnemonic('O');
		cxfNetwork.setMnemonic('C');
		dbNetwork.setMnemonic('D');
		interactiveNetwork.setMnemonic('I');
		pajekNetwork.setMnemonic('P');
		graphmlNetwork.setMnemonic('G');
		cffNetwork.setMnemonic('f');
		testSimulation.setMnemonic('T');
		baSimulation.setMnemonic('B');
		
		
		this.add(cxfNetwork);
		this.add(exploreNetwork);
		this.add(dbNetwork);
		this.add(interactiveNetwork);
		this.add(pajekNetwork);
		this.add(graphmlNetwork);
		this.add(cffNetwork);
		this.add(baSimulation);
		this.add(testSimulation);
		this.setVisible(true);
		
		cxfNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				networkSelected(cxfNetwork);				
				toolbars.getSimulationToolbar().setVisible(false);
				toolbars.getDBToolbar().setVisible(false);
				notifyObservers();
			}
		});
		exploreNetwork.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				networkSelected(exploreNetwork);
				toolbars.getSimulationToolbar().setVisible(false);
				toolbars.getDBToolbar().setVisible(true);
				notifyObservers();
			}
		});
		dbNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				networkSelected(dbNetwork); 
				toolbars.getSimulationToolbar().setVisible(false);
				toolbars.getDBToolbar().setVisible(false);
				notifyObservers();
			}
		});
		interactiveNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				networkSelected(interactiveNetwork); 
				toolbars.getSimulationToolbar().setVisible(true); 
				toolbars.getDBToolbar().setVisible(false);
				notifyObservers();
			}
		});
		pajekNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				networkSelected(pajekNetwork); 
				toolbars.getSimulationToolbar().setVisible(false);
				toolbars.getDBToolbar().setVisible(false);
				notifyObservers();
			}
		});
		graphmlNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				networkSelected(graphmlNetwork); 
				toolbars.getSimulationToolbar().setVisible(false);
				toolbars.getDBToolbar().setVisible(false);
				notifyObservers();
			}
		});
		cffNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				networkSelected(cffNetwork); 
				toolbars.getSimulationToolbar().setVisible(false); 
				toolbars.getDBToolbar().setVisible(false);
				notifyObservers();
			}
		});
		baSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				networkSelected(baSimulation); 
				toolbars.getSimulationToolbar().setVisible(true); 
				toolbars.getDBToolbar().setVisible(false);
				notifyObservers();
			}
		});
		testSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				networkSelected(testSimulation); 
				toolbars.getSimulationToolbar().setVisible(true); 
				toolbars.getDBToolbar().setVisible(false);
				notifyObservers();
			}
		});
	}
	
	private void networkSelected(JMenuItem selected) {
		BrowsableNetwork network = null;      
		try {
			network = (BrowsableNetwork) networkClassMap.get(selected).newInstance();
		} catch (InstantiationException e) {
			JOptionPane.showMessageDialog (null, "Could not create an instance of the selected network", "Network instance error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			JOptionPane.showMessageDialog (null, "Could not create an instance of the selected network", "Network instance error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		if(network instanceof InteractiveCxfNetwork) {
			((InteractiveCxfNetwork)network).addObserver(toolbars.getSimulationToolbar());
		}
		if(network instanceof DBNetwork) {
			new Thread(new DBToolbarInitializer(toolbars.getDBToolbar(), (DBNetwork)network) ).start();
		}
		network.graphicalInit(new NetworkInitializer() );
		networkPanel.setNetwork(network);
        networkPanel.onNetworkChange();
        networkPanel.getNetworkLayout().reset();
        networkPanel.repaintViewer();   
		networkPanel.stopLayout();
		//new OpenNetworkTask(networkPanel, network).execute();
		
	}

	@Override
	public void addObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}
	
	private void notifyObservers() {
		for(Observer o : observers) {
			o.update(this);
		} 
	}
	
	class DBToolbarInitializer implements Runnable {
		private DBToolbar dbToolbar;
		private DBNetwork dbNetwork;
		public DBToolbarInitializer(DBToolbar dbToolbar, DBNetwork dbNetwork) {
			this.dbToolbar = dbToolbar;
			this.dbNetwork = dbNetwork;
		}
		@Override
		public void run() {
			while(!dbNetwork.isConnected() ) {
				synchronized (dbNetwork) {
					try {
						dbNetwork.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
				}				
			}
			dbToolbar.setVisible(true);
			dbToolbar.findDBTables();
		}
		
	}

}