package ch.ethz.sg.cuttlefish.gui2;

import javax.swing.JMenuBar;

import ch.ethz.sg.cuttlefish.gui2.menus.ExportMenu;
import ch.ethz.sg.cuttlefish.gui2.menus.HelpMenu;
import ch.ethz.sg.cuttlefish.gui2.menus.LayoutMenu;
import ch.ethz.sg.cuttlefish.gui2.menus.OpenMenu;
import ch.ethz.sg.cuttlefish.gui2.menus.ViewMenu;

public class CuttlefishMenu extends JMenuBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8478131371832666397L;
	private NetworkPanel networkPanel;
	private CuttlefishToolbars toolbars;
	private OpenMenu openMenu;
	private ExportMenu exportMenu;
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
		exportMenu = new ExportMenu(networkPanel, toolbars);
		layoutMenu = new LayoutMenu(networkPanel, toolbars);
		viewMenu = new ViewMenu(networkPanel, toolbars);
		helpMenu = new HelpMenu(networkPanel, toolbars);
		add(openMenu);
		add(exportMenu);
		add(layoutMenu);		
		add(viewMenu);
		add(helpMenu);		
	}
	
	public OpenMenu getOpenMenu() {
		return openMenu;
	}
	

	public ExportMenu getExportMenu() {
		return exportMenu;
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
