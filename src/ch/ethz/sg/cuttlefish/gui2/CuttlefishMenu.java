package ch.ethz.sg.cuttlefish.gui2;

import javax.swing.JMenuBar;

public class CuttlefishMenu extends JMenuBar {
	private NetworkPanel networkPanel;
	private CuttlefishToolbars toolbars;
	private OpenMenu openMenu;
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
		openMenu = new OpenMenu(networkPanel, toolbars);		
		layoutMenu = new LayoutMenu(networkPanel, toolbars);
		viewMenu = new ViewMenu(networkPanel, toolbars);
		helpMenu = new HelpMenu(networkPanel, toolbars);
		add(openMenu);
		add(layoutMenu);		
		add(viewMenu);
		add(helpMenu);		
	}
	
	public OpenMenu getOpenMenu() {
		return openMenu;
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
