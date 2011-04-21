package ch.ethz.sg.cuttlefish.gui2;

import javax.swing.JMenuBar;

public class CuttlefishMenu extends JMenuBar {
	private NetworkPanel networkPanel;
	
	public CuttlefishMenu(NetworkPanel networkPanel) {
		super();
		this.networkPanel = networkPanel;
		initialize();
	}
	
	private void initialize() {
		add(new OpenMenu(networkPanel));
	}
}
