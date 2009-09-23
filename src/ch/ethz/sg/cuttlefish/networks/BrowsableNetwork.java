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
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;

import edu.uci.ics.jung.graph.SparseGraph;

public class BrowsableNetwork extends SparseGraph<Vertex, Edge> {
	
	private static final long serialVersionUID = 1L;
	
	private String name=this.getClass().getName();
	private Hashtable<String, String> arguments = new Hashtable<String, String>();
	private boolean incremental = false;
	
	public void init(){
	}
	
	public void updateAnnotations(){
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
	
	public Edge getRandomEdge(){
		int edgeIndex = (int) (Math.random() * getEdgeCount());
		Edge ret = null;
		Iterator<Edge> itEdge = getEdges().iterator();
		if (edgeIndex == 0)
			return itEdge.next();
		while ((edgeIndex >= 0) && itEdge.hasNext())
		{
			edgeIndex--;
			ret = itEdge.next();
		}
		return ret;
	}
	
	public void clearGraph(){
		for (Edge edge : super.getEdges())
			super.removeEdge(edge);

		Collection<Vertex> vertices = super.getVertices();
		while (!vertices.isEmpty())
		{ 
			try{
				vertices = getVertices();
				for (Vertex vertex : vertices)
					super.removeVertex(vertex);
			}
			catch (ConcurrentModificationException e)
			{}
		}
		System.out.println("erasing complete");
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
				Color shadowColor = Color.white;
				Color c = vertex.getFillColor();
				if (c!= null)
					shadowColor = c;
				
				shadowColor = new Color(shadowColor.getRed(),shadowColor.getGreen(), shadowColor.getBlue(), 200);
				
				vertex.setFillColor(Color.LIGHT_GRAY);
				vertex.setSize(5);
				vertex.setFillColor(Color.LIGHT_GRAY);
				
				Collection<Edge> edges = super.getIncidentEdges(vertex);
				for(Edge edge: edges){
					edge.setColor(Color.LIGHT_GRAY);
					edge.setWidth(0.5);
				}
			}
		}		
		
	}

	public boolean isIncremental()
	{
		return incremental;
	}
	
	public void setIncremental(boolean inc)
	{
		incremental = inc;
	}
		
}
