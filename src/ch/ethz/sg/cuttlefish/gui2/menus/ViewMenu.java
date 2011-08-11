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

package ch.ethz.sg.cuttlefish.gui2.menus;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;

import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.gui2.toolbars.DBToolbar;
import ch.ethz.sg.cuttlefish.gui2.toolbars.MouseToolbar;
import ch.ethz.sg.cuttlefish.gui2.toolbars.SimulationToolbar;
import ch.ethz.sg.cuttlefish.gui2.toolbars.ZoomToolbar;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;

public class ViewMenu extends AbstractMenu implements ItemListener, Observer{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4770350822967430897L;
	JCheckBoxMenuItem mouseToolbarCheckbox;
	JCheckBoxMenuItem zoomToolbarCheckbox;
	JCheckBoxMenuItem simulationToolbarCheckbox;
	JCheckBoxMenuItem dbToolbarCheckbox;
	
	public ViewMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super(networkPanel, toolbars);
		initialize();
		this.setText("View");
	}
	
	private void initialize() {
		mouseToolbarCheckbox = new JCheckBoxMenuItem("Mouse toolbar");
		zoomToolbarCheckbox = new JCheckBoxMenuItem("Zoom toolbar");
		simulationToolbarCheckbox = new JCheckBoxMenuItem("Simulation toolbar");
		dbToolbarCheckbox = new JCheckBoxMenuItem("Explore toolbar");
		
		mouseToolbarCheckbox.addItemListener(this);
		zoomToolbarCheckbox.addItemListener(this);
		simulationToolbarCheckbox.addItemListener(this);
		dbToolbarCheckbox.addItemListener(this);
		
		this.add(mouseToolbarCheckbox);
		this.add(zoomToolbarCheckbox);
		this.add(simulationToolbarCheckbox);
		this.add(dbToolbarCheckbox);

		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() == mouseToolbarCheckbox) {
			toolbars.getMouseToolbar().setVisible(mouseToolbarCheckbox.getState() );			
		} else if (e.getItem() == zoomToolbarCheckbox) {
			toolbars.getZoomToolbar().setVisible(zoomToolbarCheckbox.getState() );
		} else if (e.getItem() == simulationToolbarCheckbox) {
			toolbars.getSimulationToolbar().setVisible(simulationToolbarCheckbox.getState() );
		} else if (e.getItem() == dbToolbarCheckbox) {
			toolbars.getDBToolbar().setVisible(dbToolbarCheckbox.getState() );
		} else {
			System.err.println("Unknown event");
		}
	}

	@Override
	public void update(Subject o) {
		if(o instanceof MouseToolbar) {
			mouseToolbarCheckbox.setSelected( ((MouseToolbar)o).isVisible() );
		} else if(o instanceof ZoomToolbar) {
			zoomToolbarCheckbox.setSelected( ((ZoomToolbar)o).isVisible() );
		} else if(o instanceof SimulationToolbar) {
			simulationToolbarCheckbox.setSelected( ((SimulationToolbar)o).isVisible() );
			if(((SimulationToolbar)o).isEnabled()) {
				simulationToolbarCheckbox.setEnabled(true);
			} else {
				simulationToolbarCheckbox.setEnabled(false);
			}
		} else if(o instanceof DBToolbar) {
			dbToolbarCheckbox.setSelected( ((DBToolbar)o).isVisible() );
			if(((DBToolbar)o).isEnabled()) {
				dbToolbarCheckbox.setEnabled(true);
			} else {
				dbToolbarCheckbox.setEnabled(false);
			}
		}
	}


}