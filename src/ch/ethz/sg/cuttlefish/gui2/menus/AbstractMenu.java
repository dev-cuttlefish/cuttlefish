package ch.ethz.sg.cuttlefish.gui2.menus;

import javax.swing.JMenu;

import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;

public abstract class AbstractMenu extends JMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected NetworkPanel networkPanel;
	protected CuttlefishToolbars toolbars;
	
	public AbstractMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		this.networkPanel = networkPanel;
		this.toolbars = toolbars; 
	}
	
}
