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

public class CxfNetwork extends BrowsableNetwork implements ISimulation{

	private static final long serialVersionUID = 1L;
	
	FileReader fr;
	BufferedReader br;
	boolean directed = true;
	boolean hideVertexLabels = false;
	boolean hideEdgeLabels = false;
	int lineNum = 0;
	String line = null;
	int instructionIndex = 0;
	
	private class Token{
		
		String type = null;
		
		Integer id = null;
		
		Integer id_source = null; Integer id_dest = null;
		
		String label = null; Double weight = null;
		
		Color color = null; Color borderColor = null;
		
		Double size = null; String shape = null;
		
		String var1 = null; String var2 = null;		
		
		boolean freeze = false; boolean commit = false;
	}
	
	ArrayList<Token> instructionTokens = null;
	HashMap<Integer,Vertex> hash = null;

	
	public CxfNetwork(){
	}
	
	public CxfNetwork(File graphFile){
		load(graphFile);
	}
	
	public void load(File graphFile){
		
		for (Edge e : getEdges())
			removeEdge(e);
		for (Vertex v : getVertices())
			removeVertex(v);
	    directed = true;
		hideVertexLabels = false;
		hideEdgeLabels = false;
		lineNum = 1;
		line = null;
		
		hash = new HashMap<Integer,Vertex>();
		
		try {
			fr = new FileReader(graphFile);
		} catch (FileNotFoundException e) {
			System.out.println("NETWORK FILE NOT FOUND");
			e.printStackTrace();
		}
		
		br = new BufferedReader(fr);
	
		Token token;
		ArrayList<Token> edgeTokens = new ArrayList<Token>();
		
		while ((token = getNextToken()) != null)
		{
		
			if (token.type.equalsIgnoreCase("node"))
			{
				Vertex v = createVertex(token);
				addVertex(v);
				hash.put(v.getId(), v);
			}
			else if (token.type.equalsIgnoreCase("edge"))
				edgeTokens.add(token);
			else if (!token.type.equalsIgnoreCase("configuration"))
			{
				System.out.println("Unkown command in line " + line);
			}
		}
		
		for (Token t : edgeTokens)
		{
			Edge e = createEdge(t);
			EdgeType et = EdgeType.DIRECTED;
			if (!directed)
				et = EdgeType.UNDIRECTED;
		
			Vertex source = hash.get(t.id_source);
			Vertex dest = hash.get(t.id_dest);
			System.out.println("adding edge ("+ source.getId() + "," + dest.getId()+")");
			addEdge(e, source, dest);
		}
		
		line = null;
		
	}
	
	
	
	public void loadInstructions(File instructionsFile)
	{
		
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
				addEdge(createEdge(token), vSource, vDest);
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
	
	
	
	
	  private static final String QUOTES = "}";
	  private static final String WHITESPACES = " \t\r\n";
	
	private Token getNextToken(){
		Token token = new Token();
		try {
			if (line == null)
			{
				if ((line = br.readLine()) == null)
					return null;
				lineNum++;
			}
		    ArrayList<String> lineFields = new ArrayList<String>();

		    String currentDelims = WHITESPACES;
		    StringTokenizer parser = new StringTokenizer(line, currentDelims, true);

		    String field = null;
		    String fieldaux = null;
		    while ( parser.hasMoreTokens() ) {
		    	field = parser.nextToken(currentDelims);
				if (!field.equals(" ") && !field.equals("\t") && !field.equals("\n") && !field.equals("\r")){
					if (field.indexOf('\"') == -1){
						lineFields.add(field.trim());
					}
					else{
						if (fieldaux == null){
							fieldaux = field;					
							currentDelims = flipDelimiters(currentDelims);
						}
						else{
							fieldaux = fieldaux.concat(field);
							currentDelims = flipDelimiters(currentDelims);
							fieldaux = fieldaux.concat(parser.nextToken());
							lineFields.add(fieldaux);
							fieldaux = null;
						}
					}
				}
		      }
		    
			line = br.readLine();
			String lineLc;
			if (line != null)
				lineLc = new String(line.toLowerCase());
			else
				lineLc = null;
			lineNum++;
		    		
		    while ((line != null) &&  !(lineLc.startsWith("node") || lineLc.startsWith("edge")
		    					|| lineLc.startsWith("addnode") || lineLc.startsWith("addedge")
		    					|| lineLc.startsWith("removenode") || lineLc.startsWith("removeedge")
		    					|| lineLc.startsWith("editnode") || lineLc.startsWith("editedge")
		    					|| lineLc.startsWith("[")))
			{
		    	parser = new StringTokenizer(line, currentDelims, true);
		    	field = null;
		    	while ( parser.hasMoreTokens() ) {
			    	field = parser.nextToken(currentDelims);
					if (!field.equals(" ") && !field.equals("\t") && !field.equals("\n") && !field.equals("\r")){
						if (field.indexOf('\"') == -1){
							lineFields.add(field.trim());
						}
						else{
							if (fieldaux == null){
								fieldaux = field;					
								currentDelims = flipDelimiters(currentDelims);
							}
							else{
								fieldaux = fieldaux.concat(field);
								currentDelims = flipDelimiters(currentDelims);
								fieldaux = fieldaux.concat(parser.nextToken());
								lineFields.add(fieldaux);
								fieldaux = null;
							}
						}
					}
			      }
				line = br.readLine();
				if (line != null)
					lineLc = new String(line.toLowerCase());
				else
					lineLc = null;
				lineNum++;
			}
		    
		    Iterator<String> it = lineFields.iterator();
	    	field = it.next();
	    	float R, G, B;
	    	if (field.equals("[")) {
	    		field = it.next();	
	    		token.freeze = true;
	    	}
		    if ( field.toLowerCase().contains("node"))
    		{
		    	token.type = field.substring(0, field.lastIndexOf(':'));
		    	while (it.hasNext())
		    	{
		    		field = it.next();
		    		if (field.startsWith("("))
		    		{
		    			if (token.id == null)
		    				token.id = Integer.parseInt(field.substring(1, field.indexOf(')')));
		    			else 
		    				System.out.println("WARNING: two identifiers for the same node: " + (lineNum-1));
		    		}
		    		else if (field.startsWith("label"))  
		    			token.label = field.substring(field.indexOf('{')+1,field.indexOf('}'));
		    		else if (field.startsWith("color"))
		    		{
		    			int pos = field.indexOf('{')+1;
		    			R = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
		    			pos = field.indexOf(',',pos)+1;
		    			G = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
		    			pos = field.indexOf(',',pos)+1;
		    			B = Float.parseFloat(field.substring(pos,field.indexOf('}',pos)));
		    			token.color = new Color(R,G,B);
		    		}
		    		else if (field.startsWith("borderColor"))
		    		{
		    			int pos = field.indexOf('{')+1;
		    			R = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
		    			pos = field.indexOf(',',pos)+1;
		    			G = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
		    			pos = field.indexOf(',',pos)+1;
		    			B = Float.parseFloat(field.substring(pos,field.indexOf('}',pos)));
		    			token.borderColor = new Color(R,G,B);		    			
		    		}
		    		else if (field.startsWith("size"))
		    			token.size = new Double(Double.parseDouble(field.substring(field.indexOf('{')+1,field.indexOf('}'))));
				    else if (field.startsWith("shape"))
		    			token.shape = field.substring(field.indexOf('{')+1,field.indexOf('}'));
		    		else if (field.startsWith("var1"))
		    			token.var1 = field.substring(field.indexOf('{')+1,field.indexOf('}'));
		    		else if (field.startsWith("var2"))
		    			token.var2 = field.substring(field.indexOf('{')+1,field.indexOf('}'));
		    	}
		    	if (field.contains("]"))
		    		token.commit = true;
    		}
		    else if (field.toLowerCase().contains("edge"))
    		{
		     	token.type = field.substring(0, field.lastIndexOf(':'));
				while (it.hasNext())
		    	{
		    		field = it.next();
		    		if (field.startsWith("("))
		    		{
	    				token.id_source = Integer.parseInt(field.substring(1, field.indexOf(',')));
	    				token.id_dest = Integer.parseInt(field.substring(field.indexOf(',')+1, field.indexOf(')')));
		    		}
		    		else if (field.startsWith("label"))  
		    			token.label = field.substring(field.indexOf('{')+1,field.indexOf('}'));
		    		else if (field.startsWith("weight"))
		    			token.weight = new Double(Double.parseDouble(field.substring(field.indexOf('{')+1,field.indexOf('}'))));
				    else if (field.startsWith("width"))
				    	token.size = new Double(Double.parseDouble(field.substring(field.indexOf('{')+1,field.indexOf('}'))));
				    else if (field.startsWith("color"))
		    		{
		    			int pos = field.indexOf('{')+1;
		    			R = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
		    			pos = field.indexOf(',',pos)+1;
		    			G = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
		    			pos = field.indexOf(',',pos)+1;
		    			B = Float.parseFloat(field.substring(pos,field.indexOf('}',pos)));
		    			token.color = new Color(R,G,B);
		    		}
		    		else if (field.startsWith("var1"))
		    			token.var1 = field.substring(field.indexOf('{')+1,field.indexOf('}'));
		    		else if (field.startsWith("var2"))
		    			token.var2 = field.substring(field.indexOf('{')+1,field.indexOf('}'));
	
		    	}
		    	if (field.contains("]"))
		    		token.commit = true;
    		}
		    else if (lineFields.get(0).equalsIgnoreCase("configuration:"))
    		{
		    	while (it.hasNext())
		    	{
		    		field = it.next();
		    		if (field.equalsIgnoreCase("undirected"))
		    			directed = false;
		    		else if (field.equalsIgnoreCase("hide_node_labels"))
		    			hideVertexLabels = true;
		    		else if (field.equalsIgnoreCase("hide_edge_labels"))
		    			hideEdgeLabels = true;
		    		else	
		    			System.out.println("Unkown field in object of line" + (lineNum-1));
		    	}
		    	token.type = "configuration";
    		}
		    else
		    {
		    	System.out.println("Unkown object in line" + (lineNum-1));
		    }
			
		} catch (IOException e) {
			System.out.println("INPUT ERROR");
			e.printStackTrace();
		}
		
		return token;
	}
	

	private String flipDelimiters( String aCurrentDelims ) {
	    String result = null;
	    if ( aCurrentDelims.equals(WHITESPACES) ) {
	      result = QUOTES;
	    }
	    else {
	      result = WHITESPACES;
	    }
	    return result;
	}

	
	private Vertex createVertex(Token token){
		Vertex v = new Vertex(token.id);
		
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
		return v;
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
	
	private Edge createEdge(Token token){
		Edge e = new Edge();
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
		
		return e;
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
		CxfNetwork network = new CxfNetwork();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean update(long passedTime) {

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
		else
		{
			execute(token);
			instructionIndex++;
		}
		
		return (instructionIndex >= instructionTokens.size());
	}
}