/*
    
    Copyright (C) 2008  Markus Michael Geipel

    This program is free software: you can redistribute it and/or modify
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

import ch.ethz.sg.cuttlefish.misc.SGUserData;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.LayoutMutable;

public class FixedLayout2 extends AbstractLayout implements Layout, LayoutMutable {

	@Override
	public Object getBaseKey() {
		return SGUserData.POSITION;
	}

	private Hashtable<String, Coordinates> positionTable = new  Hashtable<String, Coordinates>();
	
	public FixedLayout2(Graph g){
		super(g);
		//readPositionFile(positionFile);
	}

	public void readPositionFile(File positionFile) throws Exception {
		BufferedReader input = new BufferedReader(new FileReader(positionFile));
		String line;
		while ((line = input.readLine()) != null) {
			String[] parts = line.split(" ");
			Double x = new Double(parts[1]);
			Double y = new Double(parts[2]);
			Coordinates c = new Coordinates(x.doubleValue(), y.doubleValue());
			positionTable.put(parts[0], c);
		}
	}

	@Override
	public void advancePositions() {
		

	}

	@Override
	protected void initialize_local_vertex(Vertex vertex) {
		
		Coordinates c = getCoordinates(vertex);
		if(c == null){
			c = new Coordinates();
			vertex.addUserDatum(getBaseKey(), c, UserData.REMOVE);
		}
		
		String id = (String) vertex.getUserDatum(SGUserData.ID);
		if(id!=null){
			Coordinates c1 = positionTable.get(id);
			if(c1!=null){
				c.setLocation(c1);
			}
		}
	}

	public boolean incrementsAreDone() {
		return true;
	}

	public boolean isIncremental() {
		return false;
	}

	public Hashtable<String, Coordinates> getPositionTable() {
		return positionTable;
	}

	public void update() {
		initializeLocations();
		
	}

}
