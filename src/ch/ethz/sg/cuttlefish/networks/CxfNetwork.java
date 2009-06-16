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
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;


import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Class for the loading of network data from the Cuttlefish eXtended Format
 * @author david
 */
public class CxfNetwork extends BrowsableNetwork {

	private static final long serialVersionUID = 1L;
	FileReader fr;
	BufferedReader br;
	boolean directed = true;
	boolean hideVertexLabels = false;
	boolean hideEdgeLabels = false;
	int lineNum = 0;
	String line = null;
	int instructionIndex = 0;
	File graphFile;

	/**
	 * Class as data structure for the parsed tokens
	 */
	class Token{
		int line;
		
		String type = null;
		
		Integer id = null;
		
		Integer id_source = null; Integer id_dest = null;
		
		String label = null; Double weight = null;
		
		Color color = null; Color borderColor = null;
		
		Double size = null; String shape = null;
		
		Integer borderWidth = null;
		
		String var1 = null; String var2 = null;	
		
		Point2D position = null;
		
		boolean hide = false;
		
		boolean freeze = false; boolean commit = false;
	}
	
	ArrayList<Token> instructionTokens = null;
	HashMap<Integer,Vertex> hash = null;

	/**
	 * Void general constructor
	 */
	public CxfNetwork(){
	}
	
	/**
	 * Constructor that loads directly the graph file
	 * @param graphFile
	 */
	public CxfNetwork(File graphFile){
		load(graphFile);
	}
	
	/**
	 * Loads the data stored in cxf format to the Cxf network
	 * @param graphFile
	 */
	public void load(File graphFile){
		
		this.graphFile = graphFile;
		hash = new HashMap<Integer,Vertex>();
		directed = true;
		hideVertexLabels = false;
		hideEdgeLabels = false;
		lineNum = 1;
		line = null;
	
		//First, empty the network
		for (Edge e : getEdges())
			removeEdge(e);
		Collection<Vertex> vertices = getVertices();
		//this funny way of deleting the vertices is because some ConcurrentModificationExceptions could appear
		//after this we are sure that all the vertices have been deleted in a correct way
		while (!vertices.isEmpty())
		{
			try{
			for (Vertex v : vertices)
				removeVertex(v);
			}
			catch (ConcurrentModificationException e){}
		}
		
		try {
			br = new BufferedReader(new FileReader(graphFile));
			Token token;
			ArrayList<Token> edgeTokens = new ArrayList<Token>();
			while ((token = getNextToken()) != null)
			{
				if (token.type.equalsIgnoreCase("node"))
				{
					Vertex v = createVertex(token);
			
					if (hash.get(v.getId()) != null)
					{
						JOptionPane.showMessageDialog(null,"Double node identifier: " + v.getId() +" in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
						System.out.println("Double node identifier" + v.getId() +" in line " + token.line);	
					}
					else{
						addVertex(v);
						//we store the ids and vertices in a hash table
						hash.put(v.getId(), v);
					}
				}
				else if (token.type.equalsIgnoreCase("edge"))
					edgeTokens.add(token);
				else if (!token.type.equalsIgnoreCase("configuration"))
				{
					JOptionPane.showMessageDialog(null,"Unkown command in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
					System.out.println("Unkown command in line " + token.line);
				}
			}
			
			for (Token t : edgeTokens)
			{
				//We retrieve the vertex object from the identifiers in the edge
				Vertex source = hash.get(t.id_source);
				Vertex dest = hash.get(t.id_dest);
				if ((source == null) || (dest == null))
				{
					JOptionPane.showMessageDialog(null,"Malformed edge (nonexistent endpoint): (" + t.id_source + "," + t.id_dest + ") in line "+t.line,
							"cxf error", JOptionPane.WARNING_MESSAGE);
					System.out.println("Malformed edge: (" + t.id_source + "," + t.id_dest + ") in line "+t.line);
				}
				else
				{
					System.out.println("adding edge ("+ source.getId() + "," + dest.getId()+")");
					Edge e = createEdge(t);
					EdgeType et = EdgeType.DIRECTED;
					if (!directed)
						et = EdgeType.UNDIRECTED;
					addEdge(e, source, dest, et);
				}
			}
			line = null;
		
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Network file not found");
			fnfEx.printStackTrace();
		}
	}
	
	/**
	 * Method that reloads the data from the cxf graph file
	 */
	public void reload(){
		load(graphFile);
	}
	
	private static final String BRACES = "}";
	private static final String WHITESPACES = " \t\r\n";
	
	/**
	 * Retrieves the next parsed token from the input
	 * @return the created structure with the available data from the file
	 */
	Token getNextToken(){
		Token token = new Token();
		try {
			if (line == null)
			{
				if ((line = br.readLine()) == null)
					return null;
				lineNum++;
			}
			token.line=lineNum-1;
		    		  
			ArrayList<String> lineFields = getFields(line);
		  
			line = br.readLine();
			String lineLc;
			if (line != null)
				lineLc = new String(line.toLowerCase());
			else
				lineLc = null;
			lineNum++;
		    		
		    while (notFinal(lineLc))
			{

		    	lineFields.addAll(getFields(line));
				line = br.readLine();
				if (line != null)
					lineLc = new String(line.toLowerCase());
				else
					lineLc = null;
				lineNum++;
			}
		    
		    Iterator<String> it = lineFields.iterator();
	    	String field = it.next();
	    	if (field.equals("[")) {
	    		field = it.next();	
	    		token.freeze = true;
	    	}
		    if ( field.toLowerCase().contains("node"))
    			token = parseNode(token, it);
    		
		    else if (field.toLowerCase().contains("edge"))
		    	token = parseEdge(token, it);

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
		    		{
		    			JOptionPane.showMessageDialog(null,"Unkown configuration line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
						System.out.println("Unkown field configuration line" + token.line);
		    		}
		    	}
		    	token.type = "configuration";
    		}
		    else if (! lineFields.isEmpty())
		    {
		    	JOptionPane.showMessageDialog(null,"Unkown object in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
		    	System.out.println("Unkown object in line" + token.line);
		    }
			
		} catch (IOException ioEx) {
			JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Input error");
			ioEx.printStackTrace();
		}
		
		return token;
	}
	
	private ArrayList<String> getFields(String line){
		ArrayList<String> lineFields = new ArrayList<String>();
	    String currentDelims = WHITESPACES;
		StringTokenizer parser = new StringTokenizer(line, currentDelims, true);

	    String field = null;
	    String fieldaux = null;
	    while ( parser.hasMoreTokens() ) {
	    	field = parser.nextToken(currentDelims);
			if (!field.equals(" ") && !field.equals("\t") && !field.equals("\n") && !field.equals("\r")){
				if ((!((field.contains("{") && ! field.contains("}")) 
	
		    		|| (!field.contains("{") && field.contains("}")))) 
					&& (fieldaux == null) )
				{
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
	    return lineFields;
	}
	
	private boolean notFinal(String lineLc){
		return (lineLc != null) &&  !(lineLc.startsWith("node") || lineLc.startsWith("edge")
				|| lineLc.startsWith("addnode") || lineLc.startsWith("addedge")
				|| lineLc.startsWith("removenode") || lineLc.startsWith("removeedge")
				|| lineLc.startsWith("editnode") || lineLc.startsWith("editedge")
				|| lineLc.startsWith("["));
	}
	
	private Token parseNode(Token t, Iterator<String> it){
		String field = null;;
		Token token = t; 
    	token.type = "node";
    	float R, G, B;
    	double x,y;
    	while (it.hasNext())
    	{
    		field = it.next();
    		if (field.startsWith("("))
    		{
    			if (token.id == null)
    				token.id = Integer.parseInt(field.substring(1, field.indexOf(')')));
    			else 
    			{	
    				JOptionPane.showMessageDialog(null,"Two identifiers for the same node in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
					System.out.println("WARNING: two identifiers for the same node: " + token.line);
    			}
    		}
    		else if (field.startsWith("label"))  
    		{
    			while (field.indexOf('}') < 0)
    				field = field.concat(" " + it.next());
    			token.label = field.substring(field.indexOf('{')+1,field.indexOf('}'));
    		}
    		else if (field.startsWith("color"))
    		{
    			int pos = field.indexOf('{')+1;
    			try{
    				R = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
    				pos = field.indexOf(',',pos)+1;
	    			G = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
	    			pos = field.indexOf(',',pos)+1;
	    			B = Float.parseFloat(field.substring(pos,field.indexOf('}',pos)));
	    			token.color = new Color(R,G,B);
    			}
    			catch (Exception nfEx)
    			{
    				JOptionPane.showMessageDialog(null,"Malformed color in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
					System.out.println("Malformed color in line " + token.line);
    			}
    		}
    		else if (field.startsWith("borderColor"))
    		{
    			int pos = field.indexOf('{')+1;
        		
    			try {    		
	    			R = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
	    			pos = field.indexOf(',',pos)+1;
	    			G = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
	    			pos = field.indexOf(',',pos)+1;
	    			B = Float.parseFloat(field.substring(pos,field.indexOf('}',pos)));
	    			token.borderColor = new Color(R,G,B);		    			
    			}
    			catch (Exception nfEx)
    			{
    				JOptionPane.showMessageDialog(null,"Malformed color in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
					System.out.println("Malformed color in line " + token.line);
    			}
    		}
    		else if (field.startsWith("size"))
    			token.size = new Double(Double.parseDouble(field.substring(field.indexOf('{')+1,field.indexOf('}'))));
		    else if (field.startsWith("shape"))
    			token.shape = field.substring(field.indexOf('{')+1,field.indexOf('}'));
		    else if (field.startsWith("width"))
    			token.borderWidth = new Integer(Integer.parseInt(field.substring(field.indexOf('{')+1,field.indexOf('}'))));
		    else if (field.startsWith("position"))
		    {
				int pos = field.indexOf('{')+1;
				x = Double.parseDouble(field.substring(pos,field.indexOf(',',pos)));
    			pos = field.indexOf(',',pos)+1;
    			y = Double.parseDouble(field.substring(pos,field.indexOf('}',pos)));
    			token.position = new Point2D.Double(x,y);		    			
    	    }
		    else if (field.startsWith("var1"))
    			token.var1 = field.substring(field.indexOf('{')+1,field.indexOf('}'));
    		else if (field.startsWith("var2"))
    			token.var2 = field.substring(field.indexOf('{')+1,field.indexOf('}'));
    		else if (field.startsWith("hide"))
    			token.hide = true;
    		else
    		{
    			JOptionPane.showMessageDialog(null,"Unknown node property in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
				System.out.println("Unknown node property in line " + (lineNum-1));
			}
    	}
    	if (field.contains("]"))
    		token.commit = true;
		return token;
	}

	private Token parseEdge(Token t, Iterator<String> it){
		String field = null;;
		Token token = t; 
    	token.type = "edge";
    	float R, G, B;
		while (it.hasNext())
    	{
    		field = it.next();
    		if (field.startsWith("("))
    		{
				token.id_source = Integer.parseInt(field.substring(1, field.indexOf(',')));
				token.id_dest = Integer.parseInt(field.substring(field.indexOf(',')+1, field.indexOf(')')));
    		}
    		else if (field.startsWith("label"))  
    		{		
    			while (field.indexOf('}') < 0)
    				field = field.concat(" " + it.next());
    			token.label = field.substring(field.indexOf('{')+1,field.indexOf('}'));
    		}
    		else if (field.startsWith("weight"))
    			token.weight = new Double(Double.parseDouble(field.substring(field.indexOf('{')+1,field.indexOf('}'))));
		    else if (field.startsWith("width"))
		    	token.size = new Double(Double.parseDouble(field.substring(field.indexOf('{')+1,field.indexOf('}'))));
		    else if (field.startsWith("color"))
    		{
    			int pos = field.indexOf('{')+1;
		    	try {
	    			R = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
	    			pos = field.indexOf(',',pos)+1;
	    			G = Float.parseFloat(field.substring(pos,field.indexOf(',',pos)));
	    			pos = field.indexOf(',',pos)+1;
	    			B = Float.parseFloat(field.substring(pos,field.indexOf('}',pos)));
	    			token.color = new Color(R,G,B);
		    	}
    			catch (Exception nfEx)
    			{
    				JOptionPane.showMessageDialog(null,"Malformed color in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
					System.out.println("Malformed color in line " + token.line);
    			}
    		}
    		else if (field.startsWith("var1"))
    			token.var1 = field.substring(field.indexOf('{')+1,field.indexOf('}'));
    		else if (field.startsWith("var2"))
    			token.var2 = field.substring(field.indexOf('{')+1,field.indexOf('}'));
    		else if (field.startsWith("hide"))
    			token.hide = true;
    		else   		
    		{
    			JOptionPane.showMessageDialog(null,"Unknown edge property in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
				System.out.println("Unknown edge property in line " + (lineNum-1));
			}  
    		if (field.contains("]"))
	    		token.commit = true;
    	}
		return token;
	}
	
	private String flipDelimiters( String aCurrentDelims ) {
	    String result = null;
	    if ( aCurrentDelims.equals(WHITESPACES) ) {
	      result = BRACES;
	    }
	    else {
	      result = WHITESPACES;
	    }
	    return result;
	}

	Vertex createVertex(Token token){
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
		if (token.position != null)
		{
//			v.setFixed(true);
			v.setPosition(token.position);
		}
		if (token.borderWidth != null)
			v.setWidth(token.borderWidth);
		if (token.var1 != null)
			v.setVar1(token.var1);
		if (token.var2 != null)
			v.setVar2(token.var2);
		if (token.hide)
			v.setExcluded(true);
		return v;
	}

	Edge createEdge(Token token){
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
		if (token.hide)
			e.setExcluded(true);
		return e;
	}
	
	public boolean hideVertexLabels()
	{
		return hideVertexLabels;
	}
	public boolean hideEdgeLabels(){
		return hideEdgeLabels;
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



}