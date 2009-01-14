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

package ch.ethz.sg.cuttlefish.networks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;

import edu.uci.ics.jung.graph.DirectedSparseGraph;



public class BrowsableNetwork extends DirectedSparseGraph<Vertex, Edge> {
	
	//default value for serialization in serialVersionUID
	private static final long serialVersionUID = 1L;
	
	private String name=this.getClass().getName();
	private Hashtable<String, String> arguments = new Hashtable<String, String>();
	private ArrayList<String> annotations = loadAnnotations();
	
	public void init(){
	}
	
	public void updateAnnotations(){
	}
	
	private final ArrayList<String> loadAnnotations() {
		ArrayList<String> annotations = new ArrayList<String>();
		//TODO: add reflection and extension on Vertices
		annotations.add("label");
		return annotations;
	}

	public final void setArguments(Hashtable<String, String> args){
		arguments=args;
	}
	
	public final String getArgument(String name){
		return arguments.get(name);
	}
	
	public final String getName(){
		return name;
		
	}
	
	public final void setName(String name){
		this.name = name;
	}
	
	public final int getMaxDegree() {
		int degree=0;
		for(Vertex v: getVertices()){
			degree = Math.max(degree, super.degree(v));
		}
		return degree;
	}
	
	public final void colorAll(Color color) {
		for(Vertex vertex: getVertices()){
			vertex.setFillColor(color);
		}
	}
	protected final void setColor(Vertex v, Color c2){
		if(v!=null){
			v.setFillColor(c2);
		}
	}
	
	
	protected final void addColor(Vertex v, Color c2){
		if(v!=null){
			Color c = v.getFillColor();
			Color nc;
			if (c != null)
				nc = maxColor(c2, c);
			else
				nc = c2;
			v.setFillColor(nc);
		}
	}
	
	protected final Color maxColor(Color c1, Color c2){
		int red = Math.max(c1.getRed(), c2.getRed());
		int green = Math.max(c1.getGreen(), c2.getGreen());
		int blue = Math.max(c1.getBlue(), c2.getBlue());
		
		return new Color(red, green, blue);
	}
	
	protected final void setShadowed(Vertex v, boolean b){
			v.setShadowed(b);
	}
	
	public final void copyIDsToLabels(){
		for(Vertex vertex: (Set<Vertex>)getVertices()){
			vertex.setLabel(new Integer(vertex.getId()).toString());
		}	
	}

	public void applyShadows() {
		for(Vertex vertex: getVertices()){
			if(vertex.isShadowed()){
				//System.out.println("shadowing "+vertex);
				Color shadowColor = Color.white;
				Color c = vertex.getFillColor();
				if (c!= null)
					shadowColor = c;
				
				shadowColor = new Color(shadowColor.getRed(),shadowColor.getGreen(), shadowColor.getBlue(), 200);
				
				vertex.setFillColor(Color.LIGHT_GRAY);
				vertex.setRadius(5);
				vertex.setFillColor(Color.LIGHT_GRAY);
				
				Collection<Edge> edges = super.getIncidentEdges(vertex);
				for(Edge edge: edges){
					edge.setColor(Color.LIGHT_GRAY.toString());
					edge.setWidth(0.5);
				}
			}
		}		
		
	}


}
