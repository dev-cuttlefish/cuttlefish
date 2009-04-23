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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.util.Pair;

public class BASimulation extends BrowsableNetwork implements ISimulation {

	private static final long serialVersionUID = 1L;
	
	public BASimulation(){
		super();
		//The initial network for this simulation case is a triangle
		Vertex v1 = new Vertex(), v2 = new Vertex(), v3 = new Vertex();
		addVertex(v1); v1.setLabel("2");
		addVertex(v2); v2.setLabel("2");
		addVertex(v3); v3.setLabel("2");
		addEdge(new Edge(),v1,v2); 
		addEdge(new Edge(),v2,v3);
		addEdge(new Edge(),v3,v1);
	}
	
	public void reset() {
		setIncremental(true);
		clearGraph();
	}

	public boolean update(long passedTime) {
		setIncremental(true);
		
		//endpoint selection as linear preferential attachment without initial attractiveness
		Vertex dest;
		if (Math.random() > 0.5)
			dest = getEndpoints(getRandomEdge()).getFirst();
		else
			dest = getEndpoints(getRandomEdge()).getSecond();
		
		//rejection sampling of second vertex - inital network on 3 vertices assures bounded running time
		Vertex dest2 = dest;
		while (dest2 == dest)
			if (Math.random() > 0.5)
				dest2 = getEndpoints(getRandomEdge()).getFirst();
			else
				dest2 = getEndpoints(getRandomEdge()).getSecond();	
		
		//growth
		Vertex v = new Vertex();
		v.setLabel("2");
		int vCount = getVertexCount();
		v.setFillColor(new Color((float) (vCount) / (200.0f),(float) (vCount) / (200.0f),(float) (vCount) / (200.0f)));
		v.setColor(new Color(0.0f,0.0f,0.0f));
		
		addVertex(v);
		
		//preferential attachment of parameter 2
		addEdge(new Edge(), v, dest);
		addEdge(new Edge(), v, dest2);
		dest.setLabel(Integer.toString(getNeighborCount(dest)));
		dest2.setLabel(Integer.toString(getNeighborCount(dest2)));
			
		return (getVertexCount() < 200);
	}
}