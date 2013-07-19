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

package ch.ethz.sg.cuttlefish.gui2;

import javax.swing.JMenuBar;

import ch.ethz.sg.cuttlefish.gui2.menus.ExportMenu;
import ch.ethz.sg.cuttlefish.gui2.menus.HelpMenu;
import ch.ethz.sg.cuttlefish.gui2.menus.LayoutMenu;
import ch.ethz.sg.cuttlefish.gui2.menus.NetworkMenu;
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
	private NetworkMenu networkMenu;
	
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
		networkMenu = new NetworkMenu(networkPanel, toolbars, openMenu, exportMenu );
		add(networkMenu);		
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
