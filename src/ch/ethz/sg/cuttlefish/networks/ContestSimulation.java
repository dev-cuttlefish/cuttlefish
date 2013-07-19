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
import java.util.HashMap;


public class ContestSimulation extends BrowsableNetwork implements ISimulation {

	private static final long serialVersionUID = 1L;
	private int contest_size = 25;
	private int voters_size = 25;
	private int[] votes = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 12 };

	public ContestSimulation() {
		// update(0);
	}

	public void reset() {
		setIncremental(true);
		for (Edge edge : super.getEdges())
			super.removeEdge(edge);

		Collection<Vertex> vertices = super.getVertices();
		while (!vertices.isEmpty()) {
			try {
				for (Vertex vertex : vertices)
					super.removeVertex(vertex);
			} catch (ConcurrentModificationException e) {
			}
		}
		update(0);
	}

	public boolean update(long passedTime) {
		setIncremental(false);
		HashMap<Integer, Vertex> hash = new HashMap<Integer, Vertex>();

		for (int i = 1; i <= voters_size; i++) {
			Vertex v = new Vertex(i);
			v.setLabel("" + i);
			addVertex(v);
			hash.put(i, v);
		}

		for (Vertex v : getVertices()) {
			ArrayList<Integer> possibilities = new ArrayList<Integer>();
			for (int i = 1; i <= contest_size; i++)
				if (i != v.getId())
					possibilities.add(new Integer(i));

			for (int vote_count = 0; vote_count < votes.length; vote_count++) {
				int index = (int) (Math.random() * possibilities.size());
				int to_id = possibilities.get(index);
				Vertex v_dest = hash.get(to_id);

				Edge e = new Edge(v, v_dest, true);
				e.setWeight(votes[vote_count]);
				float darkness = (float) (1.0f - ((float) votes[vote_count]) / 12.0f);
				e.setColor(new Color(darkness, darkness, darkness));
				e.setLabel("" + votes[vote_count]);
				addEdge(e);

				possibilities.remove(index);
			}
		}

		hash.clear();

		return false;
	}
}
