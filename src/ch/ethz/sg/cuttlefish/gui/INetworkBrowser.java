/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

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

import java.awt.image.BufferedImage;
import java.io.File;

import org.w3c.dom.Document;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;

public interface INetworkBrowser {
	
	public Layout<Vertex,Edge> getNetworkLayout();

	public void repaintViewer();

	public void setNetwork(BrowsableNetwork network);
	
	public void refreshAnnotations();
	
	public void onNetworkChange();
	
	public String getArgument(String name);
	
	public void stopLayout();
	public void resumeLayout();
	
	public File getPositionFile();
	public void setLayout(String selectedLayout);
	
	public EditingModalGraphMouse<Vertex, Edge> getMouse();
	
	public BufferedImage getSnapshot();
}
