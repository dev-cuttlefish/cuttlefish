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
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;


import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class InteractiveCxfNetwork extends CxfNetwork implements ISimulation{
	
	boolean done;
	private static final long serialVersionUID = 1L;

	public InteractiveCxfNetwork(){
		super();
		setIncremental(true);
	}
	
	public InteractiveCxfNetwork(File graphFile){
		load(graphFile);
		setIncremental(true);
		done = false;
	}
	public void loadInstructions(File instructionsFile)
	{
		setIncremental(true);
		try {
			fr = new FileReader(instructionsFile);
		} catch (FileNotFoundException e) {
			System.out.println("INSTRUCTIONS FILE NOT FOUND");
			e.printStackTrace();
		}
		
		br = new BufferedReader(fr);
	
		Token token;
		instructionTokens = new ArrayList<Token>();
		
		while ((token = getNextToken()) != null)
			instructionTokens.add(token);
		
		return;
	}

	
	private void execute(Token token)
	{
	
		if (token.type.equalsIgnoreCase("addNode"))
		{
			if (hash.containsKey(token.id))
				System.out.println("WARNING: trying to add an existing node -- Use editNode");
			else
			{
				Vertex v = createVertex(token);
				hash.put(token.id, v);
				addVertex(v);
			}
		}
		else if (token.type.equalsIgnoreCase("removeNode"))
		{
			if (hash.containsKey(token.id))
			{
				Vertex v = hash.get(token.id);
				
				if (directed)
				{
					for (Edge e : getOutEdges(v))
						removeEdge(e);
					for (Edge e : getInEdges(v))
						removeEdge(e);
				}
				else
					for (Edge e : getIncidentEdges(v))
						removeEdge(e);
				removeVertex(v);
				hash.remove(token.id);
			}
				
		}
		else if (token.type.equalsIgnoreCase("editNode"))
		{
			if (hash.containsKey(token.id))
			{
				Vertex v = hash.get(token.id);
				editVertex(v,token);
			}
			else
				System.out.println("WARNING: editing an inexistent node --- use addNode");
			
		}
		else if (token.type.equalsIgnoreCase("addEdge"))
		{
			if (hash.containsKey(token.id_source) && hash.containsKey(token.id_dest))
			{
				Vertex vSource = hash.get(token.id_source);
				Vertex vDest = hash.get(token.id_dest);
				if (findEdge(vSource, vDest) != null)
					System.out.println("WARNING: the edge ("+token.id_source +
							","+token.id_dest +") already existed -- use editEdge");
				Edge e = createEdge(token);
				
				EdgeType et = EdgeType.DIRECTED;
				if (!directed)
					et = EdgeType.UNDIRECTED;
				
				addEdge(e, vSource, vDest,et);
			}
			else
				System.out.println("WARNING: one of the endpoints of the added edge ("+token.id_source +
						","+token.id_dest +") does not exist");
		}
		else if (token.type.equalsIgnoreCase("removeEdge"))
		{
			if (hash.containsKey(token.id_source) && hash.containsKey(token.id_dest))
			{
				Vertex vSource = hash.get(token.id_source);
				Vertex vDest = hash.get(token.id_dest);
				Edge e;
				if ((e = findEdge(vSource, vDest)) != null)
					removeEdge(e);
		}
			else
				System.out.println("WARNING: one of the endpoints of the removing edge ("+token.id_source +
						","+token.id_dest +") does not exist");
		}
		else if (token.type.equalsIgnoreCase("editEdge"))
		{
			if (hash.containsKey(token.id_source) && hash.containsKey(token.id_dest))
			{
				Vertex vSource = hash.get(token.id_source);
				Vertex vDest = hash.get(token.id_dest);
				Edge e;
				if ((e = findEdge(vSource, vDest)) != null)
					editEdge(e,token);
				else
					System.out.println("WARNING: the edited edge ("+token.id_source +
							","+token.id_dest +") didn't exist -- use addEdge");
		}
			else
				System.out.println("WARNING: one of the endpoints of the editing edge ("+token.id_source +
						","+token.id_dest +") does not exist");
		}
		return;
	}
	
	private void editVertex(Vertex v, Token token){
		if (token.label != null)
			v.setLabel(token.label);
		if (token.size != null)
			v.setSize(token.size);
		if (token.shape != null)
			v.setShape(token.shape);
		if (token.color != null)
			v.setFillColor(token.color);
		if (token.borderColor != null)
			v.setColor(token.borderColor);
		if (token.var1 != null)
			v.setVar1(token.var1);
		if (token.var2 != null)
			v.setVar2(token.var2);
		return;
	}
	
	private void editEdge(Edge e, Token token){
		if (token.weight != null)
			e.setWeight(token.weight);
		if (token.label != null)
			e.setLabel(token.label);
		if (token.size != null)
			e.setWidth(token.size);
		if (token.color != null)
			e.setColor(token.color);
		if (token.var1 != null)
			e.setVar1(token.var1);
		if (token.var2 != null)
			e.setVar2(token.var2);

		return;
	}
	
	
	public static void main(String argv[]){
		InteractiveCxfNetwork network = new InteractiveCxfNetwork();
		network.load(new File("testGraph1.cxf"));
		
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
			for (Vertex v : network.getIncidentVertices(e))
				System.out.print(v.getId() + " ");
			System.out.print(e.getLabel() + " ");
			System.out.print(e.getWeight() + " ");
			System.out.print(e.getWidth() + " ");
			System.out.print(e.getShape() + " ");
			System.out.print(e.getVar1() + " ");
			System.out.print(e.getVar2() + "\n");
		}
		
		
	}

	@Override
	public void reset() {
		instructionIndex = 0;
		reload();
		done = false;
	}

	@Override
	public boolean update(long passedTime) {

		if (!done)
		{
			Token token = instructionTokens.get(instructionIndex);
			
			if (token.freeze)
			{
				while ((instructionIndex < instructionTokens.size()) && (!token.commit))
				{
					token = instructionTokens.get(instructionIndex);
					execute(token);
					instructionIndex++;
				}
				
			}
			else if (instructionIndex < instructionTokens.size())
			{
				execute(token);
				instructionIndex++;
			}
		}
		done = !(instructionIndex < instructionTokens.size());
		return !done;
	}
}