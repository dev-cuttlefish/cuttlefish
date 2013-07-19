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

import java.awt.image.BufferedImage;
import java.util.Set;

import ch.ethz.sg.cuttlefish.gui.visualization.GraphMouse;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

/**
 * Interface for the network browser when it is accessed from the networks and
 * widgets
 * 
 * @author david
 * 
 */
public interface INetworkBrowser {

	/**
	 * Returns the layout used to display the network.
	 * 
	 * @return
	 */
	public org.gephi.layout.spi.Layout getNetworkLayout();

	/**
	 * Requests a revisualization of the network.
	 */
	public void repaintViewer();

	/**
	 * Changes the network to a new one.
	 * 
	 * @param network
	 *            to use in the browser.
	 */
	public void setNetwork(BrowsableNetwork network);

	/**
	 * Refreshes the widgets of the Browser.
	 */
	public void refreshAnnotations();

	/**
	 * Method to call when there is a change in the network meaningful for the
	 * browser.
	 */
	public void onNetworkChange();

	/**
	 * Locks all the vertices in the layout, nothing should move after this
	 */
	public void stopLayout();

	/**
	 * Refreshes the layout type and unlocks all the vertices
	 */
	public void resumeLayout();

	/**
	 * Creates a layout from the type chosen among "ARFLayout", "FixedLayout",
	 * "WeightedARFLayout", "SpringLayout", "Kamada-Kawai",
	 * "Fruchterman-Reingold", "ISOMLayout" or "CircleLayout"
	 * 
	 * @param String
	 *            with the layout type from the possible ones
	 */
	public void setLayoutByName(String selectedLayout);

	/**
	 * Getter for the graph mouse associated to the panel
	 * 
	 * @return EditingModalGraphMouse automatically created by JUNG2.0
	 */
	public GraphMouse getMouse();

	/**
	 * Creates an image of the current visualization
	 * 
	 * @return BufferedImage image created by the visualization viewer
	 **/
	public BufferedImage getSnapshot();

	/**
	 * Gives the set of selected vertices by the mouse
	 * 
	 * @return set of selected vertices
	 */
	public Set<Vertex> getPickedVertices();

	/**
	 * Gives the set of selected edges by the mouse
	 * 
	 * @return set of selected edges
	 */
	public Set<Edge> getPickedEdges();
}
