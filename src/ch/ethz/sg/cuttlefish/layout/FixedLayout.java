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
package ch.ethz.sg.cuttlefish.layout;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;

public class FixedLayout<V,E> extends AbstractLayout<Vertex,E> {

	/**
	 * Constructor for the fixed layout of a given graph
	 * @param g graph to generate the fixed layout
	 
	public FixedLayout(Graph<Vertex,E> g){
		super(g);
		initialize();
	}
*/
	/**
	 * Constructor for the fixed layout of a given graph
	 * @param g graph to generate the fixed layout
	 */
	public FixedLayout(Graph<Vertex,E> g, Layout<Vertex,Edge> layout){
		super(g);
		for (Vertex vertex : g.getVertices())
			vertex.setPosition(layout.transform(vertex));
		initialize();
	}
	
	public void update() {
		initialize();
	}

	@Override
	public void initialize() {		
		for (Vertex vertex : getGraph().getVertices())
		{			
			if (vertex.getPosition() == null)
				locations.put(vertex, new Point2D.Double(0.d,0.d));
			else {
				locations.put(vertex, vertex.getPosition());
			}
			
		}
	}

	@Override
	public void reset(){
		initialize();
	}

}
