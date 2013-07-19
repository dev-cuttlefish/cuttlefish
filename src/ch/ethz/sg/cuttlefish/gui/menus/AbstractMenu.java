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

package ch.ethz.sg.cuttlefish.gui.menus;

import javax.swing.JMenu;

import ch.ethz.sg.cuttlefish.gui.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui.NetworkPanel;

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
