/*
  
    Copyright (C) 2011  Markus Michael Geipel, David Garcia Becerra,
    Petar Tsankov

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
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.util.EdgeType;

public class JsonNetwork extends BrowsableNetwork {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Default Json network is undirected
	private boolean directed = true;
	

	/**
	 * Temporary constructor that simply reads the
	 * Json file
	 * @param jsonFile
	 */
	public JsonNetwork(String json) {
		load(json);
	}
	
	/**
	 * Read the Json data and load the network
	 * @param jsonFile
	 */
	private void load(String json) {
		Gson gson = new Gson();

		Type type = new TypeToken<JsonData>(){}.getType();
		JsonData jsonData = gson.fromJson(json, type);

		Collection<JsonNode> jsonNodes = jsonData.nodes;
		Collection<JsonEdge> jsonEdges = jsonData.edges;
		
		// we need this map when adding the edges
		Map<Integer, Vertex> idToNode = new HashMap<Integer, Vertex>();
		// Add all nodes to the network
		for(JsonNode jsonNode : jsonNodes) {
			
			Vertex v = new Vertex(jsonNode.id);			
			if(jsonNode.label != null)
				v.setLabel(jsonNode.label);
			if(jsonNode.color != null) {
		    	float R, G, B;
				try {    		
	    			int pos = jsonNode.color.indexOf('{')+1;
	    			R = Float.parseFloat(jsonNode.color.substring(pos,jsonNode.color.indexOf(',',pos)));
	    			pos = jsonNode.color.indexOf(',',pos)+1;
	    			G = Float.parseFloat(jsonNode.color.substring(pos,jsonNode.color.indexOf(',',pos)));
	    			pos = jsonNode.color.indexOf(',',pos)+1;
	    			B = Float.parseFloat(jsonNode.color.substring(pos,jsonNode.color.indexOf('}',pos)));
	    			v.setFillColor( new Color(R,G,B) );		    			
    			}
    			catch (Exception nfEx)
    			{
    				JOptionPane.showMessageDialog(null,"Malformed color " + jsonNode.color,"cxf error", JOptionPane.WARNING_MESSAGE);
					System.out.println("Malformed color " + jsonNode.color);
    			}				
			}
			if(jsonNode.borderColor != null) {
		    	float R, G, B;
				try {    		
	    			int pos = jsonNode.borderColor.indexOf('{')+1;
	    			R = Float.parseFloat(jsonNode.borderColor.substring(pos,jsonNode.borderColor.indexOf(',',pos)));
	    			pos = jsonNode.borderColor.indexOf(',',pos)+1;
	    			G = Float.parseFloat(jsonNode.borderColor.substring(pos,jsonNode.borderColor.indexOf(',',pos)));
	    			pos = jsonNode.borderColor.indexOf(',',pos)+1;
	    			B = Float.parseFloat(jsonNode.borderColor.substring(pos,jsonNode.borderColor.indexOf('}',pos)));
	    			v.setColor( new Color(R,G,B) );		    			
    			}
    			catch (Exception nfEx)
    			{
    				JOptionPane.showMessageDialog(null,"Malformed color " + jsonNode.color,"cxf error", JOptionPane.WARNING_MESSAGE);
					System.out.println("Malformed color " + jsonNode.color);
    			}				
			}
			
			if(jsonNode.width > 0)
				v.setWidth(jsonNode.width);
			if(jsonNode.size > 0)
				v.setSize(jsonNode.size);
			if(jsonNode.shape != null)
				v.setShape(jsonNode.shape);
			if(jsonNode.x > 0 && jsonNode.y > 0)
				v.setPosition(jsonNode.x, jsonNode.y);
			if(jsonNode.var1 != null)
				v.setVar1(jsonNode.var1);
			if(jsonNode.var2 != null)
				v.setVar2(jsonNode.var2);
			if(jsonNode.hide)
				v.setExcluded(jsonNode.hide);
			if(jsonNode.fixed)
				v.setFixed(jsonNode.fixed);
			//store in the map for later
			idToNode.put(v.getId(), v);
			//add to the network
			addVertex(v);
		}
				
		for(JsonEdge jsonEdge : jsonEdges) {
			Edge e = new Edge();
			if(jsonEdge.label != null)
				e.setLabel(jsonEdge.label);
			if(jsonEdge.weight > 0)
				e.setWeight(jsonEdge.weight);
			if(jsonEdge.width > 0)
				e.setWidth(jsonEdge.width);
			if(jsonEdge.color != null)	{		
		    	float R, G, B;
				try {    		
	    			int pos = jsonEdge.color.indexOf('{')+1;
	    			R = Float.parseFloat(jsonEdge.color.substring(pos,jsonEdge.color.indexOf(',',pos)));
	    			pos = jsonEdge.color.indexOf(',',pos)+1;
	    			G = Float.parseFloat(jsonEdge.color.substring(pos,jsonEdge.color.indexOf(',',pos)));
	    			pos = jsonEdge.color.indexOf(',',pos)+1;
	    			B = Float.parseFloat(jsonEdge.color.substring(pos,jsonEdge.color.indexOf('}',pos)));
	    			e.setColor( new Color(R,G,B) );		    			
    			}
    			catch (Exception nfEx)
    			{
    				JOptionPane.showMessageDialog(null,"Malformed color " + jsonEdge.color,"cxf error", JOptionPane.WARNING_MESSAGE);
					System.out.println("Malformed color " + jsonEdge.color);
    			}				
			}
			if(jsonEdge.var1 != null)
				e.setVar1(jsonEdge.var1);
			if(jsonEdge.var2 != null)
				e.setVar2(jsonEdge.var2);
			if(jsonEdge.hide)
				e.setExcluded(jsonEdge.hide);	
			addEdge(e, idToNode.get(jsonEdge.id_origin), idToNode.get(jsonEdge.id_dest) );
		}

	}
	
	/**
	 * Setter for the directed field of
	 * the network
	 * @param d
	 */
	public void setDirected(boolean d) {
		directed = d;
	}
	
	/**
	 * Below several method override the methods
	 * from BrowsableNetwork so that the directivity of the
	 * network matches with the direction parameter
	 * in this network
	 */
	@Override
	public boolean addEdge(Edge arg0, java.util.Collection<? extends Vertex> arg1, EdgeType arg2) {
		if(directed)
			return super.addEdge(arg0, arg1, EdgeType.DIRECTED);
		else
			return super.addEdge(arg0, arg1, EdgeType.UNDIRECTED);
	};
	
	@Override
	public boolean addEdge(Edge e, Vertex v1, Vertex v2, EdgeType edge_type) {		
		if(directed)
			return super.addEdge(e, v1, v2, EdgeType.DIRECTED);
		else
			return super.addEdge(e, v1, v2, EdgeType.UNDIRECTED);
	};
	
	@Override
	public boolean addEdge(Edge e, Vertex v1, Vertex v2) {
		if(directed)
			return super.addEdge(e, v1, v2, EdgeType.DIRECTED);
		else
			return super.addEdge(e, v1, v2, EdgeType.UNDIRECTED);
	};
	
	@Override
	public boolean addEdge(Edge edge, java.util.Collection<? extends Vertex> vertices) {
		if(directed)
			return super.addEdge(edge, vertices, EdgeType.DIRECTED);
		else
			return super.addEdge(edge, vertices, EdgeType.UNDIRECTED);
	};
	
	@Override
	public boolean addEdge(Edge edge, edu.uci.ics.jung.graph.util.Pair<? extends Vertex> endpoints) {
		if(directed)
			return super.addEdge(edge, endpoints, EdgeType.DIRECTED);
		else
			return super.addEdge(edge, endpoints, EdgeType.UNDIRECTED);
	};
	
	@Override
	public boolean addEdge(Edge edge, edu.uci.ics.jung.graph.util.Pair<? extends Vertex> endpoints, EdgeType edgeType) {
		if(directed)
			return super.addEdge(edge, endpoints, EdgeType.DIRECTED);
		else
			return super.addEdge(edge, endpoints, EdgeType.UNDIRECTED);
	};
	
	class JsonNode {
		int id;
		String label;
		String color;
		String borderColor;
		double size;
		String shape;
		int width;
		float x;
		float y;
		String var1;
		String var2;
		boolean hide;
		boolean fixed;
						
		public String toString() {
			return "Node "
				+ "id: " + id + ", "
				+ "label: " + label + ", "
				+ "color: " + color + ", "
				+ "borderColor: " + borderColor + ", "
				+ "size: " + size + ", "
				+ "shape: " + shape + ", "
				+ "width: " + width + ", "
				+ "x: " + x + ", "
				+ "y: " + y + ", "
				+ "var1: " + var1 + ", "
				+ "var2: " + var2 + ", "
				+ "hide: " + hide + ", "
				+ "fixed: " + fixed;
		}
		
	}
	
	class JsonEdge {		
		int id_origin;
		int id_dest;
		String label;
		double weight;
		double width;
		String color;
		String var1;
		String var2;
		boolean hide;
		
		public String toString() {
			return "Edge "
				+ "id_origin: " + id_origin + ", "
				+ "id_dest: " + id_dest + ", "
				+ "label: " + label + ", "
				+ "weight: " + weight + ", "
				+ "width: " + width + ", "
				+ "color: " + color + ", "
				+ "var1: " + var1 + ", "
				+ "var2: " + var2 + ", "
				+ "hide: " + hide;
		}
	}
	
	class JsonData {
		Collection<JsonNode> nodes;
		Collection<JsonEdge> edges;
	}

}
