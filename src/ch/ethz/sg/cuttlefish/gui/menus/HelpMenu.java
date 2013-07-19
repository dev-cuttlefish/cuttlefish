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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import ch.ethz.sg.cuttlefish.gui.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.gui.toolbars.AboutWindow;

public class HelpMenu extends AbstractMenu {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4251419612908498161L;
	private JMenuItem about;
	private AboutWindow aboutWindow;

	
	public HelpMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super(networkPanel, toolbars);
		initialize();
		this.setText("Help");
	}
	
	private void initialize() {
		aboutWindow = new AboutWindow(networkPanel);
		about = new JMenuItem("About");
		this.add(about);
		about.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				aboutWindow.setVisible(true);
			}
		});
	}
	
	

}
