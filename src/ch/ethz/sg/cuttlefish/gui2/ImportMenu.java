package ch.ethz.sg.cuttlefish.gui2;

import javax.swing.JMenuItem;

public class ImportMenu extends AbstractMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImportMenu(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
	}
	
	private void initialize() {
		JMenuItem cxfNetwork = new JMenuItem("Cxf network");
		this.add(cxfNetwork);
		this.setVisible(true);
	}

}
