package ch.ethz.sg.cuttlefish.gui.applet;

import javax.swing.JMenuBar;

import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.HelpMenu;
import ch.ethz.sg.cuttlefish.gui2.LayoutMenu;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.gui2.ViewMenu;

public class CuttlefishMenu extends JMenuBar {
	private NetworkPanel networkPanel;
	private CuttlefishToolbars toolbars;
	private LayoutMenu layoutMenu;
	private ViewMenu viewMenu;
	private HelpMenu helpMenu;
	
	public CuttlefishMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super();
		this.networkPanel = networkPanel;
		this.toolbars = toolbars;
		initialize();
	}
	
	private void initialize() {		
		layoutMenu = new LayoutMenu(networkPanel, toolbars);
		viewMenu = new ViewMenu(networkPanel, toolbars);
		helpMenu = new HelpMenu(networkPanel, toolbars);
		add(layoutMenu);		
		add(viewMenu);
		add(helpMenu);		
	}
	
	public LayoutMenu getLayoutMenu() {
		return layoutMenu;
	}
	
	public ViewMenu getViewMenu() {
		return viewMenu;
	}
	
	public HelpMenu getHelpMenu() {
		return helpMenu;
	}
}
