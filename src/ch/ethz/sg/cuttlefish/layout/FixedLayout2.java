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

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import ch.ethz.sg.cuttlefish.misc.Vertex;

public class FixedLayout2<V,E> extends AbstractLayout<Vertex,E> implements Layout<Vertex,E> {

	
	private File positionFile;
	
	/**
	 * Constructor for the fixed layout of a given graph
	 * @param g graph to generate the fixed layout
	 */
	public FixedLayout2(Graph<Vertex,E> g){
		super(g);
	}
	/**
	 * Constructor for the fixed layout of a given graph
	 * @param g graph to generate the fixed layout
	 */
	public FixedLayout2(Graph<Vertex,E> g, File positionFile){
		super(g);
		this.positionFile = positionFile;
	}

	/**
	 * sets the file with the initial positions of the nodes, without reading them
	 * yet
	 * @param positionFile open file where to read the positions
	 * @throws Exception
	 */
	public void setPositionFile(File positionFile){
		this.positionFile = positionFile;
	}
	
	public boolean incrementsAreDone() {
		return true;
	}

	public boolean isIncremental() {
		return false;
	}

	public void update() {
		initialize();
	}

	@Override
	public void initialize() {		
		
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(positionFile));
			String line;
			while ((line = input.readLine()) != null) {
				String[] parts = line.split(" ");
				Double x = new Double(parts[1]);
				Double y = new Double(parts[2]);
				Point2D p = new Point2D.Double(x,y);
				locations.put(new Vertex(Integer.parseInt(parts[0])), p);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error initializing from positions file");
		}

	}

	@Override
	public void reset(){
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(positionFile));
			String line;
			while ((line = input.readLine()) != null) {
				String[] parts = line.split(" ");
				Double x = new Double(parts[1]);
				Double y = new Double(parts[2]);
				Point2D p = new Point2D.Double(x,y);
				locations.put(new Vertex(Integer.parseInt(parts[0])), p);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error resetting from positions file");
		}
	}

}
