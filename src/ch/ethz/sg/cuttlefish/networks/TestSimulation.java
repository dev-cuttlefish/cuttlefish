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

package ch.ethz.sg.cuttlefish.networks;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;

public class TestSimulation extends BrowsableNetwork implements ISimulation {

	private static final long serialVersionUID = 1L;
	private Vertex lastInsert = null;
	
	public void reset() {
		lastInsert = null;
		for (Edge edge : super.getEdges())
			super.removeEdge(edge);
		for (Vertex vertex : super.getVertices())
			super.removeVertex(vertex);

	}

	public boolean update(long passedTime) {
		Vertex v = new Vertex();
		addVertex(v);
		if(lastInsert!=null){
			Edge e = new Edge();
			super.addEdge(e, v, lastInsert);
		}
		lastInsert = v;
		return true;
	}
}