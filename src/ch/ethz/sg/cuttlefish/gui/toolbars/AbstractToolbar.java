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

package ch.ethz.sg.cuttlefish.gui.toolbars;


import java.util.LinkedList;

import javax.swing.JToolBar;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;

public abstract class AbstractToolbar extends JToolBar implements Subject {
	/**
	 * 
	 */
	protected LinkedList<Observer> observers;
	
	private static final long serialVersionUID = 1L;
	protected NetworkPanel networkPanel;
	
	public AbstractToolbar(NetworkPanel networkPanel) {
		this.networkPanel = networkPanel;
		this.setFloatable(true);
		observers = new LinkedList<Observer>();
	}
	
	public void addObserver(Observer o) {
		observers.add(o);
	}
	
	public void removeObserver(Observer o) {
		observers.remove(o);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		for(Observer o : observers) {
			o.update(this);
		}
	}
	
}
