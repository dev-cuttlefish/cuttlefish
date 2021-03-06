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

package ch.ethz.sg.cuttlefish.gui;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import ch.ethz.sg.cuttlefish.gui.toolbars.DBToolbar;
import ch.ethz.sg.cuttlefish.gui.toolbars.MouseToolbar;
import ch.ethz.sg.cuttlefish.gui.toolbars.SimulationToolbar;
import ch.ethz.sg.cuttlefish.gui.toolbars.UndoableToolbar;
import ch.ethz.sg.cuttlefish.gui.toolbars.ZoomToolbar;

public class CuttlefishToolbars extends JPanel {

	private MouseToolbar mouseToolbar;
	private ZoomToolbar zoomToolbar;
	private SimulationToolbar simulationToolbar;
	private DBToolbar dbToolbar;
	private static final long serialVersionUID = 1L;
	
	private UndoableToolbar undoToolbar;

	public CuttlefishToolbars(NetworkPanel networkPanel) {
		super();
		this.setBackground(Color.WHITE);
		this.setLayout(new FlowLayout());
		mouseToolbar = new MouseToolbar(networkPanel);
		zoomToolbar = new ZoomToolbar(networkPanel);
		simulationToolbar = new SimulationToolbar(networkPanel);
		dbToolbar = new DBToolbar(networkPanel);
		this.add(mouseToolbar);
		this.add(zoomToolbar);
		this.add(simulationToolbar);
		this.add(dbToolbar);
		simulationToolbar.setVisible(true);
		dbToolbar.setVisible(true);
		
		undoToolbar = new UndoableToolbar(networkPanel);
		this.add(undoToolbar);
	}
	
	public MouseToolbar getMouseToolbar() {
		return mouseToolbar;
	}
	
	public ZoomToolbar getZoomToolbar() {
		return zoomToolbar;
	}
	
	public SimulationToolbar getSimulationToolbar() {
		return simulationToolbar;
	}
	
	public DBToolbar getDBToolbar() {
		return dbToolbar;
	}
	
	public UndoableToolbar getUndoToolbar() {
		return undoToolbar;
	}
	
}
