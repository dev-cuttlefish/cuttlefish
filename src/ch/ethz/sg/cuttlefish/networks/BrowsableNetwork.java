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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import ch.ethz.sg.cuttlefish.misc.SGUserData;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.utils.UserDataContainer;

public class BrowsableNetwork extends DirectedSparseGraph {
	
	private String name=this.getClass().getName();
	private Hashtable<String, String> arguments = new Hashtable<String, String>();
	private ArrayList<String> annotations = loadAnnotations();
	
	
	public void init(){
	}
	
	public void updateAnnotations(){
	}
	
	private final ArrayList<String> loadAnnotations() {
		ArrayList<String> annotations = new ArrayList<String>();
		
		Field[] fields = SGUserData.class.getDeclaredFields();
		for(Field field : fields){
			try {
				Object o = field.get(SGUserData.class);
				if (o instanceof String) {
					String value = (String) o;
					if(value.startsWith("__G_")){
						annotations.add(value);
					//	System.out.println(value);
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	
	@SuppressWarnings("unchecked")
	public final int getMaxDegree() {
		int degree=0;
		for(Vertex v: (Set<Vertex>) getVertices()){
			degree = Math.max(degree, v.degree());
		}
		return degree;
	}
	
	@SuppressWarnings("unchecked")
	public final void colorAll(Color color) {
		for(Vertex vertex: (Set<Vertex>)getVertices()){
			vertex.setUserDatum(SGUserData.FILLCOLOR, color, UserData.REMOVE);
		}
	}
	protected final void setColor(Vertex v, Color c2){
		if(v!=null){
			v.setUserDatum(SGUserData.FILLCOLOR, c2, UserData.REMOVE);
		}
	}
	
	
	protected final void addColor(Vertex v, Color c2){
		if(v!=null){
			Color c = new Color(0,0,0);
			Object o =  v.getUserDatum(SGUserData.FILLCOLOR);
			if (o!=null && o instanceof Color) {
				c = (Color) o;
				
			}
	
			Color nc = maxColor(c2, c);
			v.setUserDatum(SGUserData.FILLCOLOR, nc, UserData.REMOVE);
		}
	}
	
	protected final Color maxColor(Color c1, Color c2){
		int red = Math.max(c1.getRed(), c2.getRed());
		int green = Math.max(c1.getGreen(), c2.getGreen());
		int blue = Math.max(c1.getBlue(), c2.getBlue());
		
		return new Color(red, green, blue);
	}

	@SuppressWarnings("unchecked")
	
	
	
	private final void removeAnnotation(UserDataContainer c, Object key){
	
		if(c.getUserDatum(key)!=null && c.getUserDatumCopyAction(key) == UserData.REMOVE){
			c.removeUserDatum(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public final void removeAnnotations() {

		
		
		for(Vertex vertex: (Set<Vertex>)getVertices()){
			for(String key:annotations){
				removeAnnotation(vertex, key);
			}
		}
		for(Edge edge: (Set<Edge>)getEdges()){
			for(String key:annotations){
				removeAnnotation(edge, key);
			}
		}
		
	}
	
	protected final void setShadowed(Vertex v, boolean b){
		if(b){
			v.setUserDatum(SGUserData.SHADOWED, true, UserData.REMOVE);
		}else{
			v.removeUserDatum(SGUserData.SHADOWED);
		}
	}
	
	@SuppressWarnings("unchecked")
	public final void copyIDsToLabels(){
		for(Vertex vertex: (Set<Vertex>)getVertices()){
			vertex.setUserDatum(SGUserData.LABEL, vertex.getUserDatum(SGUserData.ID), UserData.REMOVE);
		}
		
	}

	@SuppressWarnings("unchecked")
	public void applyShadows() {
		for(Vertex vertex: (Set<Vertex>)getVertices()){
			if(vertex.getUserDatum(SGUserData.SHADOWED)!=null){
				//System.out.println("shadowing "+vertex);
				Color shadowColor = Color.white;
				Object o = vertex.getUserDatum(SGUserData.FILLCOLOR);
				if (o instanceof Color) {
					shadowColor = (Color) o;
				}
				shadowColor = new Color(shadowColor.getRed(),shadowColor.getGreen(), shadowColor.getBlue(), 200);
					
				vertex.setUserDatum(SGUserData.FILLCOLOR, Color.LIGHT_GRAY, UserData.REMOVE);
				vertex.setUserDatum(SGUserData.RADIUS, 5, UserData.REMOVE);
				vertex.setUserDatum(SGUserData.COLOR, Color.LIGHT_GRAY, UserData.REMOVE);
				vertex.removeUserDatum(SGUserData.LABEL);
				Set<Edge> edges = vertex.getIncidentEdges();
				for(Edge edge: edges){
					edge.setUserDatum(SGUserData.COLOR, Color.LIGHT_GRAY, UserData.REMOVE);
					edge.setUserDatum(SGUserData.WIDTH, 0.5, UserData.REMOVE);
				}
			}
		}		
		
	}


}
