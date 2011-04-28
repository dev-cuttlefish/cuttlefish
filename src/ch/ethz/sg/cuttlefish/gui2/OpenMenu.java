package ch.ethz.sg.cuttlefish.gui2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.gui.NetworkInitializer;
import ch.ethz.sg.cuttlefish.misc.XMLUtil;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

public class OpenMenu extends AbstractMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuItem cxfNetwork;
	private JMenuItem dbNetwork;
	private JMenuItem interactiveNetwork;
	private JMenuItem pajekNetwork;
	private JMenuItem graphmlNetwork;
	private JMenuItem cffNetwork;
	private JMenuItem testSimulation;		
	private JMenuItem baSimulation;
	private ActionListener networkSelectAction;
	private HashMap<JMenuItem, Class<?> > networkClassMap;

	public OpenMenu(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
		this.setText("Open");
	}
	
	private void initialize() {
		cxfNetwork = new JMenuItem("Cxf network");
		dbNetwork = new JMenuItem("Interactive network");
		interactiveNetwork = new JMenuItem("Database network");
		pajekNetwork = new JMenuItem("Pajek network");
		graphmlNetwork = new JMenuItem("GraphML network");
		cffNetwork = new JMenuItem("CFF network");
		testSimulation = new JMenuItem("Test simulation");		
		baSimulation = new JMenuItem("BA simulation");
		
		networkClassMap = new HashMap<JMenuItem, Class<?> >();
		try {
			networkClassMap.put(cxfNetwork, Class.forName("ch.ethz.sg.cuttlefish.networks.CxfNetwork") );
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
		this.add(dbNetwork);
		this.add(interactiveNetwork);
		this.add(pajekNetwork);
		this.add(graphmlNetwork);
		this.add(cffNetwork);
		this.add(baSimulation);
		this.add(testSimulation);
		this.setVisible(true);
		
		cxfNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { networkSelected(cxfNetwork); }
		});
		dbNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { networkSelected(dbNetwork); }
		});
		interactiveNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { networkSelected(interactiveNetwork); }
		});
		pajekNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { networkSelected(pajekNetwork); }
		});
		graphmlNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { networkSelected(graphmlNetwork); }
		});
		cffNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { networkSelected(cffNetwork); }
		});
		baSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { networkSelected(baSimulation); }
		});
		testSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { networkSelected(testSimulation); }
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
		network.graphicalInit(new NetworkInitializer() );
		networkPanel.setNetwork(network);
        networkPanel.onNetworkChange();
        networkPanel.getNetworkLayout().reset();
        networkPanel.repaintViewer();
        networkPanel.stopLayout();	
	}

}