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
import java.util.HashMap;
import java.util.Iterator;

import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.gui2.NetworkInitializer;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Class for the loading of network data from the Cuttlefish eXtended Format
 * @author david
 */
public class CxfNetwork extends BrowsableNetwork {

	private static final long serialVersionUID = 1L;
	private String networkFileName = null;
	FileReader fr;
	BufferedReader br;
	boolean directed = true;
	private String edgeShape = "bendLine";
	private boolean hideVertexLabels = false;
	private boolean hideEdgeLabels = false;
	// By default CxfNetwork does not allow
	// parallel edges
	private boolean multiGraph = false;
	int lineNum = 0;
	String line = null;
	int instructionIndex = 0;
	File graphFile;

	@Override
	public void graphicalInit(NetworkInitializer initializer) {
		initializer.initCxfNetwork(this);
	}
	
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
		
		int sleepTime = 0;
		
		int maxUpdateSteps = 50;
		
		boolean hide = false;
		
		boolean isRoot = false;
		
		boolean freeze = false; boolean commit = false;
	}
	
	ArrayList<Token> instructionTokens = null;
	HashMap<Integer,Vertex> hash = null;

	/**
	 * Void general constructor
	 */
	public CxfNetwork(){}
	
	/**
	 * Constructor that loads directly the graph file
	 * @param graphFile
	 */
	public CxfNetwork(File graphFile){
		load(graphFile);
	}
	
	/**
	 * Empty the currently loaded network.
	 * @param
	 */
	private void emptyNetwork() {
		clearGraph();		
	}
	
	public String getEdgeShape() {
		return edgeShape;
	}
	
	/**
	 * Check if parallel edges are allowed
	 * @return
	 */
	public boolean isMultiGraph() {
		return multiGraph;
	}
	
	/**
	 * set if the graph should be a multigraph
	 * @param b
	 */
	public void setMultiGraph(boolean b) {
		multiGraph = b;
	}
	
	/**
	 * Getter for directed
	 * @return
	 */
	public boolean isDirected() {
		return directed;
	}
	
	public void  setEdgeShape(String edgeType) {
		this.edgeShape = edgeType;
	}
	
	protected boolean confirmFileFormatWarning(String warning, String dialogTitle) {
		System.out.println(warning);
		int answer = JOptionPane.showConfirmDialog(null, warning, dialogTitle, 2, JOptionPane.WARNING_MESSAGE);
		if(answer == JOptionPane.CANCEL_OPTION) {
			emptyNetwork();
			return false;
		}
		return true;
	} 
	
	
	/**
	 * Loads the data stored in cxf format to the Cxf network
	 * @param graphFile
	 */
	public void load(File graphFile){	
		this.graphFile = graphFile;
		hash = new HashMap<Integer,Vertex>();
		directed = true;
		edgeShape = "bendLine";
		hideVertexLabels = false;
		hideEdgeLabels = false;
		lineNum = 1;
		line = null;
	
		//First, empty the network
		emptyNetwork();		
		
		try {
			br = new BufferedReader(new FileReader(graphFile));
			Token token;
			ArrayList<Token> edgeTokens = new ArrayList<Token>();
			while ((token = getNextToken()) != null)
			{
				if (token.type.toLowerCase().contains("node"))
				{
					Vertex v = createVertex(token);
			
					if (hash.get(v.getId()) != null)
					{
						if(!confirmFileFormatWarning("Double node identifier: " + v.getId() +" in line " + token.line, "cxf error") )
							return;	
					}
					else{
						addVertex(v);
						//we store the ids and vertices in a hash table
						hash.put(v.getId(), v);
					}
				}
				else if (token.type.toLowerCase().contains("edge"))
					edgeTokens.add(token);
				else if (!token.type.toLowerCase().equalsIgnoreCase("configuration"))
				{
					if(!confirmFileFormatWarning("Unknown command in line " + token.line, "cxf error") )
						return;					
				}
			}
			
			for (Token t : edgeTokens)
			{
				//We retrieve the vertex object from the identifiers in the edge
				Vertex source = hash.get(t.id_source);
				Vertex dest = hash.get(t.id_dest);
				if ((source == null) || (dest == null))
				{
					if(!confirmFileFormatWarning("Malformed edge (nonexistent endpoint): (" + t.id_source + "," + t.id_dest + ") in line "+t.line, "cxf error") )
						return;
				}
				else
				{
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
		
		this.networkFileName = graphFile.getName();
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
//	    	if (field.equals("[")) {
//	    		field = it.next();	
	    	if (field.startsWith("[")) {
	    		if ((field.length() < 3) && (it.hasNext()))
	    			field = it.next();
	    		else
	    			field = field.substring(1);
	    		token.freeze = true;
	    	}

	    	if (field.endsWith("]")) {
//	    		field = it.next();	
	    		field = field.substring(0,field.length()-1);
	    		token.commit = true;
	    	}

	    	if ( field.toLowerCase().contains("node"))
    			token = parseNode(token,field.toLowerCase(), it);
    		
		    else if (field.toLowerCase().contains("edge"))
		    	token = parseEdge(token,field.toLowerCase(), it);
	    	
		    else if (field.toLowerCase().contains("options"))
		    	token = parseOptions(token, field.toLowerCase(), it);

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
		    		else if (field.equalsIgnoreCase("draw_straight_edges"))
		    			edgeShape = "line";
		    		else if (field.equalsIgnoreCase("multi_graph"))
		    			multiGraph = true;
		    		else	
		    		{
		    			JOptionPane.showMessageDialog(null,"Unkown configuration line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
						System.out.println("Unkown field configuration line" + token.line);
		    		}
		    	}
		    	token.type = "configuration";
    		}
		    else if ((! lineFields.isEmpty()) && (!token.freeze) && (!token.commit))
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
	
	public void setDirected(boolean d) {
		directed = d;
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
	
	private Token parseOptions(Token t, String preField, Iterator<String> it) {
		String field = null;
		Token token = t;
		token.type = preField.substring(0,preField.indexOf(":"));
		while(it.hasNext()) {
			field = it.next();
			if (field.startsWith("label") ) {
				token.label = field.substring(field.indexOf('{')+1,field.indexOf('}'));
			} else if( field.startsWith("sleepTime") ) {
				token.sleepTime = Integer.parseInt(field.substring(field.indexOf('{')+1,field.indexOf('}')));
			} else if( field.startsWith("maxUpdateSteps") ) {
				token.maxUpdateSteps = Integer.parseInt(field.substring(field.indexOf('{')+1,field.indexOf('}')));
			} else {
				System.out.println("WARNING: Improper use of label: " + token.line);
			}
		}
		return token;
	}
	
	private Token parseNode(Token t, String preField, Iterator<String> it){
		String field = null;;
		Token token = t; 
    	token.type = preField.substring(0,preField.indexOf(":"));
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
    		else if (field.startsWith("root")) {
    			token.isRoot = true;
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
    		else if (! (field.contains("[") || field.contains("]")))
    		{
    			JOptionPane.showMessageDialog(null,"Unknown node property in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
				System.out.println("Unknown node property in line " + (lineNum-1));
			}
    	}
    	if (field.contains("]"))
    		token.commit = true;
		return token;
	}

	private Token parseEdge(Token t, String preField, Iterator<String> it){
		String field = null;;
		Token token = t; 
    	token.type = preField.substring(0,preField.indexOf(":"));
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
    		else if (field.contains("]"))
	    		token.commit = true;
    		else   		
    		{
    			JOptionPane.showMessageDialog(null,"Unknown edge property in line " + token.line,"cxf error", JOptionPane.WARNING_MESSAGE);
				System.out.println("Unknown edge property in line " + (lineNum-1));
			}  
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
		if (token.isRoot)
			v.setIsRoot(token.isRoot);
		if (token.var1 != null)
			v.setVar1(token.var1);
		if (token.var2 != null)
			v.setVar2(token.var2);
		v.setExcluded(token.hide);
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
		e.setExcluded(token.hide);
		return e;
	}
	
	public boolean hideVertexLabels()
	{
		return hideVertexLabels;
	}
	public boolean hideEdgeLabels(){
		return hideEdgeLabels;
	}
	
	public String getCxfName(){
		return networkFileName;
	}
	
	
	/**
	 * Overriding addEdge method from SparseGraph to conform
	 * to the
	 */
/*	@Override
	public boolean addEdge(Edge arg0, java.util.Collection<? extends Vertex> arg1, EdgeType arg2) {
		if(directed) {
			return super.addEdge(arg0, arg1, EdgeType.DIRECTED);
		} else {
			return super.addEdge(arg0, arg1, EdgeType.UNDIRECTED);
		}
	};*/
	
	@Override
	public boolean addEdge(Edge e, Vertex v1, Vertex v2, EdgeType edge_type) {
		if(findEdgeSet(v1, v2).size() > 0 && !multiGraph ) {
			System.err.println("The graph does not allow parallel edges");
			return false;
		}
		if(directed)
			return super.addEdge(e, v1, v2, EdgeType.DIRECTED);
		else
			return super.addEdge(e, v1, v2, EdgeType.UNDIRECTED);
	};
	
	@Override
	public boolean addEdge(Edge e, Vertex v1, Vertex v2) {
		if(findEdgeSet(v1, v2).size() > 0 && !isMultiGraph() ) {
			System.err.println("The graph does not allow parallel edges");
			return false;
		}
		if(directed)
			return super.addEdge(e, v1, v2, EdgeType.DIRECTED);
		else
			return super.addEdge(e, v1, v2, EdgeType.UNDIRECTED);
	};
	
/*	@Override
	public boolean addEdge(Edge edge, java.util.Collection<? extends Vertex> vertices) {		
	};*/
	
	@Override
	public boolean addEdge(Edge edge, edu.uci.ics.jung.graph.util.Pair<? extends Vertex> endpoints) {
		if(findEdgeSet(endpoints.getFirst(), endpoints.getSecond()).size() > 0 && !multiGraph ) {
			System.err.println("The graph does not allow parallel edges");
			return false;
		}
		if(directed)
			return super.addEdge(edge, endpoints, EdgeType.DIRECTED);
		else
			return super.addEdge(edge, endpoints, EdgeType.UNDIRECTED);
	};
	
	@Override
	public boolean addEdge(Edge edge, edu.uci.ics.jung.graph.util.Pair<? extends Vertex> endpoints, EdgeType edgeType) {
		if(findEdgeSet(endpoints.getFirst(), endpoints.getSecond()).size() > 0 && !multiGraph ) {
			System.err.println("The graph does not allow parallel edges");
			return false;
		}
		if(directed)
			return super.addEdge(edge, endpoints, EdgeType.DIRECTED);
		else
			return super.addEdge(edge, endpoints, EdgeType.UNDIRECTED);
	};
	
		
}