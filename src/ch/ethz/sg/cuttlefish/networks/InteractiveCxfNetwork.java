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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import sun.security.action.GetLongAction;

import ch.ethz.sg.cuttlefish.gui.NetworkInitializer;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.util.EdgeType;

public class InteractiveCxfNetwork extends CxfNetwork implements ISimulation, Subject {
	
	boolean done;
	private static final long serialVersionUID = 1L;
	private ArrayList<Observer> observers;
	private String currentLabel = "";

	public InteractiveCxfNetwork(){
		super();
		observers = new ArrayList<Observer>();
		setIncremental(true);
	}
	
	public InteractiveCxfNetwork(File graphFile){
		load(graphFile);
		setIncremental(true);
		done = false;
	}
	
	@Override
	public void graphicalInit(NetworkInitializer initializer) {
		initializer.initInteractiveCxfNetwork(this);
	}
	
	public void loadInstructions(File instructionsFile)
	{
		setIncremental(true);
		try {
			fr = new FileReader(instructionsFile);
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("cxf network file not found");
			fnfEx.printStackTrace();
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
		if (token.type == null)
			return;
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
			{
				JOptionPane.showMessageDialog(null,"Editing an inexistent node --- use addNode","Warning",JOptionPane.WARNING_MESSAGE);
				System.out.println("WARNING: editing an inexistent node --- use addNode");
			}
			
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
			else if (!token.commit && !token.freeze)
			{
				JOptionPane.showMessageDialog(null,"One of the endpoints of the added edge ("+token.id_source +
						","+token.id_dest +") does not exist","Warning",JOptionPane.WARNING_MESSAGE);
				System.out.println("WARNING: one of the endpoints of the added edge ("+token.id_source +
						","+token.id_dest +") does not exist");
			}
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
			{
				JOptionPane.showMessageDialog(null,"One of the endpoints of the added edge ("+token.id_source +
						","+token.id_dest +") does not exist","Warning",JOptionPane.WARNING_MESSAGE);
				System.out.println("WARNING: one of the endpoints of the added edge ("+token.id_source +
						","+token.id_dest +") does not exist");
			}
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
			{
				JOptionPane.showMessageDialog(null,"One of the endpoints of the added edge ("+token.id_source +
						","+token.id_dest +") does not exist","Warning",JOptionPane.WARNING_MESSAGE);
				System.out.println("WARNING: one of the endpoints of the added edge ("+token.id_source +
						","+token.id_dest +") does not exist");
			}

		}
		else if (token.type.equalsIgnoreCase("label")) {
			System.out.println("Changing frame label to " + token.label);
			currentLabel = token.label;
			for(Observer o : observers)
				o.update(this);
		}
		return;
	}
	
	public String getCurrentLabel() {
		return currentLabel;
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
		if (token.position != null)
			v.setPosition(token.position);
		if (token.borderWidth != null)
			v.setWidth(token.borderWidth);
		if (token.var1 != null)
			v.setVar1(token.var1);
		if (token.var2 != null)
			v.setVar2(token.var2);
		v.setExcluded(token.hide);
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
		e.setExcluded(token.hide);
		return;
	}
	
	@Override
	public void reset() {
		instructionIndex = 0;
		reload();
		currentLabel = "";
		for(Observer o : observers)
			o.update(this);
		done = false;
	}

	@Override
	public boolean update(long passedTime) {

		if (!done)
		{
			Token token = instructionTokens.get(instructionIndex);
			
			if (token.freeze)
			{
				execute(token);
				instructionIndex++;
				boolean commited = token.commit;
				while ((instructionIndex < instructionTokens.size()) && (!commited))
				{
					token = instructionTokens.get(instructionIndex);
					execute(token);
					instructionIndex++;
					commited = token.commit;
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

	@Override
	public void addObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}
}