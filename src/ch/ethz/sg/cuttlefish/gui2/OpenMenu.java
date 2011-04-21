package ch.ethz.sg.cuttlefish.gui2;

import javax.swing.JMenuItem;

public class OpenMenu extends AbstractMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpenMenu(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
		this.setText("Open");
	}
	
	private void initialize() {
		JMenuItem cxfNetwork = new JMenuItem("Cxf network");
		JMenuItem dbNetwork = new JMenuItem("Database network");
		JMenuItem interactiveNetwork = new JMenuItem("Interactive network");
		JMenuItem pajekNetwork = new JMenuItem("Pajek network");
		JMenuItem baSimulation = new JMenuItem("BA simulation");
		JMenuItem testSimulation = new JMenuItem("Test simulation");
		this.add(cxfNetwork);
		this.add(dbNetwork);
		this.add(interactiveNetwork);
		this.add(pajekNetwork);
		this.add(baSimulation);
		this.add(testSimulation);
		this.setVisible(true);
	}

}
