package ch.ethz.sg.cuttlefish.gui2;

import javax.swing.JMenu;

public abstract class AbstractMenu extends JMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected NetworkPanel networkPanel;
	
	public AbstractMenu(NetworkPanel networkPanel) {
		this.networkPanel = networkPanel;
	}
	
}
