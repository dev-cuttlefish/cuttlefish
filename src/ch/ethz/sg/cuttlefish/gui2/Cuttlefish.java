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

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;

public class Cuttlefish extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2232589699110179555L;
	public static File currentDirectory = null;
	private CuttlefishMenu mainMenu = null;
	private CuttlefishToolbars toolbars = null;
	private NetworkPanel networkPanel = null;
	
	public Cuttlefish() {
		super();
		initialize();
		this.setVisible(true);
	}
	

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setSize(1000, 700);  //The initial size of the user interface is slightly smaller than 1024x768
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getMainMenu());
		this.setLayout(new BorderLayout());
		CuttlefishToolbars toolbars = getToolbars();
		this.add(toolbars, BorderLayout.PAGE_START);
		toolbars.getSimulationToolbar().addObserver(mainMenu.getViewMenu());
		toolbars.getMouseToolbar().addObserver(mainMenu.getViewMenu());
		toolbars.getZoomToolbar().addObserver(mainMenu.getViewMenu());
		toolbars.getDBToolbar().addObserver(mainMenu.getViewMenu());
		
		toolbars.getSimulationToolbar().setVisible(true);
		toolbars.getMouseToolbar().setVisible(true);
		toolbars.getZoomToolbar().setVisible(true);
		toolbars.getDBToolbar().setVisible(true);
		networkPanel.addObserver(mainMenu.getLayoutMenu());
		this.add(getNetworkPanel(), BorderLayout.CENTER);
		this.setTitle("Cuttlefish 2.0");
	}
	
	private CuttlefishToolbars getToolbars() {
		if(toolbars == null) {
			toolbars = new CuttlefishToolbars(getNetworkPanel());
		}
		return toolbars;
	}
	
	private CuttlefishMenu getMainMenu() {
		if(mainMenu == null) {
			mainMenu = new CuttlefishMenu(getNetworkPanel(), getToolbars());
		}
		return mainMenu;
	}
	

	private NetworkPanel getNetworkPanel() {
		if (networkPanel == null) {
			networkPanel = new NetworkPanel();
		}
		return networkPanel;
	}	 
}
