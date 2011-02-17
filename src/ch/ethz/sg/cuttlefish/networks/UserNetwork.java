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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;


import ch.ethz.sg.cuttlefish.gui.NetworkInitializer;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;

public class UserNetwork extends BrowsableNetwork {

	private static final long serialVersionUID = 1L;
	
	FileReader fr;
	BufferedReader br;
	int orig, dest;
	  
	public void graphicalInit(NetworkInitializer initializer) {
		initializer.initUserNetwork(this);
	}
	
	public void load(File graphFile){
		
		HashMap<Integer,Vertex> hash = new HashMap<Integer,Vertex>();
		
			try {
				fr = new FileReader(graphFile);
				br = new BufferedReader(fr);
				
				Vertex v;
				while ((v = getNextVertex()) != null)
				{
					addVertex(v);
					hash.put(v.getId(), v);
				}
				
				Edge e;
				while (( e = getNextEdge()) != null)
					addEdge(e,hash.get(orig), hash.get(dest));
			
			} catch (FileNotFoundException fnfEx) {
				JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				System.err.println("cff network file not found");
				fnfEx.printStackTrace();
			} catch (IOException ioEx) {
				JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				System.err.println("Input problem in cff file");
				ioEx.printStackTrace();
			}
	}
	
	public void load(File nodeFile, File edgeFile){

	}
	
	private Vertex getNextVertex() throws IOException{
		Vertex v = new Vertex();
		String line = br.readLine();
		if ((line == null) || (line == "\n") || (line.startsWith("*Edges")))
			return null;
		int position = 0;
		int pre_pos = 0;
		
		position = line.indexOf(" ", position);
		int id = Integer.parseInt(line.substring(pre_pos,position));
		v.setId(id);
		pre_pos = position+1;
				
		if (position >= line.length())
			return v;
		
		if (line.indexOf("\"") == -1)
			position = line.indexOf(" ", pre_pos);
		else
		{
			position = line.indexOf("\"", position+2);
			pre_pos++;
		}
		String label = line.substring(pre_pos, position);
		v.setLabel(label);
		if (line.indexOf("\"") == -1)
			pre_pos = position+1;
		else
			pre_pos = position+2;
		
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		
		if ((position >= line.length()) || (position == -1))
			return v;
	
		float r, g, b;
		r = Float.parseFloat(line.substring(pre_pos,position));
		pre_pos = position+1;
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		g = Float.parseFloat(line.substring(pre_pos,position));
		pre_pos = position+1;
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		b = Float.parseFloat(line.substring(pre_pos,position));
		pre_pos = position+1;
		Color fill = new Color(r,g,b);
		v.setFillColor(fill);
		
		if ((position >= line.length()) || (position == -1))
			return v;
		
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		r = Float.parseFloat(line.substring(pre_pos,position));
		pre_pos = position+1;
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		g = Float.parseFloat(line.substring(pre_pos,position));
		pre_pos = position+1;
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		b = Float.parseFloat(line.substring(pre_pos,position));
		pre_pos = position+1;
		Color border = new Color(r,g,b);
		v.setColor(border);
		
		if ((position >= line.length()) || (position == -1))
			return v;
		
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		double size = Double.parseDouble(line.substring(pre_pos, position));
		v.setSize(size);
		pre_pos = position+1;
		
		if ((position >= line.length()) || (position == -1))
			return v;
		
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		v.setShape(line.substring(pre_pos, position));		
		pre_pos = position+1;
		
		if ((position >= line.length()) || (position == -1))
			return v;
		
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		v.setVar1(line.substring(pre_pos, position));		
		pre_pos = position+1;
		
		if ((position >= line.length()) || (position == -1))
			return v;
		
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		v.setVar2(line.substring(pre_pos, position));		
		pre_pos = position+1;
		
		return v;
	}
	
	private Edge getNextEdge() throws IOException{
		Edge e = new Edge();
		String line = br.readLine();
		if ((line == null) || (line == "\n"))
			return null;
		int position = 0, pre_pos = 0;
		
		position = line.indexOf(" ", position);
		orig = Integer.parseInt(line.substring(pre_pos,position));
		pre_pos = position+1;
		position = line.indexOf(" ", pre_pos);
		dest = Integer.parseInt(line.substring(pre_pos,position));
		pre_pos = position+1;
		
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		if (!line.substring(pre_pos, position).startsWith("*"))
		{
			double weight = Double.parseDouble(line.substring(pre_pos, position));
			e.setWeight(weight);
		}
		pre_pos = position+1;
		
		if (position == line.length())
			return e;

		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		double width = Double.parseDouble(line.substring(pre_pos,position));
		e.setWidth(width);
		pre_pos = position+1;
		
		if (position == line.length())
			return e;

		if ((position >= line.length()) || (position == -1))
			return e;
		
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		float r = Float.parseFloat(line.substring(pre_pos,position));
		pre_pos = position+1;
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		float g = Float.parseFloat(line.substring(pre_pos,position));
		pre_pos = position+1;
		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		float b = Float.parseFloat(line.substring(pre_pos,position));
		pre_pos = position+1;
		Color color = new Color(r,g,b);
		e.setColor(color);
	

		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		e.setVar1(line.substring(pre_pos, position));
		pre_pos = position+1;
		
		if (position == line.length())
			return e;

		if ((position = line.indexOf(" ", pre_pos)) == -1)
			position = line.length();
		e.setVar2(line.substring(pre_pos, position));
		pre_pos = position+1;
		
		return e;
	}
	
	public static void main(String argv[]){
		UserNetwork network = new UserNetwork();
		network.load(new File(argv[0]));
		
		for (Vertex v : network.getVertices())
		{
			System.out.print(v.getId() + " ");
			System.out.print(v.getLabel() + " ");
			System.out.print(v.getFillColor() + " ");
			System.out.print(v.getColor() + " ");
			System.out.print(v.getSize() + " ");
			System.out.print(v.getShape() + " ");
			System.out.print(v.getVar1() + " ");
			System.out.print(v.getVar2() + "\n");
		}
		
		for(Edge e : network.getEdges())
		{
			System.out.print(network.getSource(e).getId() + " ");
			System.out.print(network.getDest(e).getId() + " ");
			System.out.print(e.getWeight() + " ");
			System.out.print(e.getWidth() + " ");
			System.out.print(e.getShape() + " ");
			System.out.print(e.getVar1() + " ");
			System.out.print(e.getVar2() + "\n");
		}
		
		
	}
}