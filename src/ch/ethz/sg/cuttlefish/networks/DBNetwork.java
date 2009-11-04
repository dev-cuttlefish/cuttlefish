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
import java.awt.HeadlessException;
import java.sql.*;
import java.util.HashMap;
import javax.swing.JOptionPane;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
* Network class that loads data from a database and sets the graph
* @author David Garcia Becerra
*/
public class DBNetwork extends BrowsableNetwork {

	private static final long serialVersionUID = 1L;
	
	private Connection conn;
	private HashMap<Integer,Vertex> hash = new HashMap<Integer,Vertex>();
	private String nodeFilter = "";
	private String edgeFilter = "";
	private boolean directed = true;
	private boolean initialized = false;
	
	/**
	 * Void general constructor
	 */
	public DBNetwork() {
	}
	
	/**
	 * Method that connects the network to a specified database with user and password
	 * @param dbName name of the database without protocol or connection details
	 * @param userName
	 * @param password
	 */
	public void connect(String dbName, String userName, String password) {
		if (conn != null)
			disConnect();
		try
		{
		  Class.forName("com.mysql.jdbc.Driver").newInstance();
		  String url = "jdbc:mysql://" + dbName;
		  conn = DriverManager.getConnection(url, userName, password);
		  if ((conn == null) || (!conn.isValid(100)))
				JOptionPane.showMessageDialog(null,null,"Error connecting to database "+ dbName,JOptionPane.ERROR_MESSAGE);
		}
		catch (ClassNotFoundException cnfEx) {
			JOptionPane.showMessageDialog(null,cnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Class com.mysql.jdc.Driver not found");
			cnfEx.printStackTrace();
			}
		catch (IllegalAccessException iaEx) {
			JOptionPane.showMessageDialog(null,iaEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Illegal access in database connection");
			iaEx.printStackTrace();
		}
		catch (InstantiationException iEx) {
			JOptionPane.showMessageDialog(null,iEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Instantation exception");
			iEx.printStackTrace();
		}
		catch (SQLException sqlEx) {
			JOptionPane.showMessageDialog(null,sqlEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("SQL error");
			sqlEx.printStackTrace();
		}	
		catch (HeadlessException hEx) {
			JOptionPane.showMessageDialog(null,hEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("SQL error");
			hEx.printStackTrace();
		}
		getDirection();
		
	}
	
	/**
	 * Method that queries the database to determine whether the network should be directed or not
	 */
	private void getDirection(){
		if (! initialized)
		{
			try {
				String queryString = "select * from Directed;";
		      	Statement st;
					st = conn.createStatement();
					ResultSet rs = st.executeQuery(queryString);
					directed = rs.getBoolean(1);
			} catch (SQLException e) {
				initialized = true;
				directed = true;    // if no view is defined, is directed by default
			}
			initialized = true;
		}
		
	}
	
	/**
	 * Method that closes the connection to the database, if existed
	 */
	public void disConnect() {
		  try {
			conn.close();
		} catch (SQLException sqlEx) {
			JOptionPane.showMessageDialog(null,sqlEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("SQL error");
			sqlEx.printStackTrace();
		}
	}
	
	/**
	 * Method that disposes all nodes and edges of the current network
	 */
	public void emptyNetwork() {
		hash.clear();
		clearGraph();
		
	}
	
	/**
	 * Setter for the filter on the queries for the nodes
	 * @param nodeFilter
	 */
	public void setNodeFilter(String nodeFilter)
	{
		this.nodeFilter = nodeFilter;
	}
	
	/**
	 * Setter for the filter on the queries for the edges of the database
	 */
	public void setEdgeFilter(String edgeFilter)
	{
		this.edgeFilter = edgeFilter;
	}
	
	/**
	 * Function that applies certain filter to any query, transforming it to a filtered query
	 * @param query to modify
	 * @param filter to add to the query
	 * @return extended query
	 */
	private String applyFilter(String query, String filter)
	{
		if (filter.equals(""))
			return query;
		int whereIndex = query.lastIndexOf("where");
		if (whereIndex == -1)
			whereIndex = query.lastIndexOf("WHERE");
		
		if (whereIndex == -1)
			return query + "where " + filter;
		
		String result = query.substring(0, whereIndex+5);
		String whereClause = query.substring(whereIndex+5);
		result = result + " " + filter + " and " + whereClause;		
		return result;
	}
	
	/**
	 * Executes a proper node query and adds all the resulting nodes to the network
	 * @param queryString
	 */
	public void nodeQuery(String queryStringOriginal) {
		    try
		    {
		      String queryString = applyFilter(queryStringOriginal, nodeFilter);
		      System.out.println(queryString);
		      Statement st = conn.createStatement();
		      ResultSet rs = st.executeQuery(queryString);
		      while (rs.next())
		      {
			    	Vertex v;
			        int id = rs.getInt("id");
     		    	if (hash.get(id) == null)
			        {
     		    		String label = rs.getString("label");
				        if (label != null)
				        	v = new Vertex(id, label);
				        else
				        	v = new Vertex(id);
				        
				        String colorString = rs.getString("color");
				        if (colorString != null)
				        {
				        	int pos = 0;
				        	float R, G, B;
				        	R = Float.parseFloat(colorString.substring(pos,colorString.indexOf(',',pos)));
		    				pos = colorString.indexOf(',',pos)+1;
			    			G = Float.parseFloat(colorString.substring(pos,colorString.indexOf(',',pos)));
			    			pos = colorString.indexOf(',',pos)+1;
			    			B = Float.parseFloat(colorString.substring(pos));
			    			v.setFillColor(new Color(R,G,B));
			    		}
				        
				        String borderColorString = rs.getString("borderColor");
				        if (borderColorString != null)
				        {
				        	int pos = 0;
				        	float R, G, B;
				        	R = Float.parseFloat(borderColorString.substring(pos,borderColorString.indexOf(',',pos)));
		    				pos = borderColorString.indexOf(',',pos)+1;
			    			G = Float.parseFloat(borderColorString.substring(pos,borderColorString.indexOf(',',pos)));
			    			pos = borderColorString.indexOf(',',pos)+1;
			    			B = Float.parseFloat(borderColorString.substring(pos));
			    			v.setColor(new Color(R,G,B));
			    		}
				        
				        float size = rs.getFloat("size");
				        if (size != 0)
				        	v.setSize(size);
				        
				        String shape = rs.getString("shape");
				        if (shape != null)
				        	v.setShape(shape);
				        
				        int width = rs.getInt("width");
				        if (width != 0)
				        	v.setWidth(width);
	
				        String var1 = rs.getString("var1");
				        if (var1 != null)
				        	v.setVar1(var1);
	
				        String var2 = rs.getString("var2");
				        if (var2 != null)
				        	v.setVar2(var2);
				        
				        String hide = rs.getString("hide");
				        if ((hide != null) && (hide.equals("true")))
				        	v.setExcluded(true);
		
				        addVertex(v);
				        
				        hash.put(id, v);
			        }
			        
		      }
		    }
		    catch (SQLException sqlEx)
		    {
		    	JOptionPane.showMessageDialog(null,sqlEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				System.err.println("SQL error");
				sqlEx.printStackTrace();
		    }
		    extendEdges();
	}
	
	/**
	 * Method that adds to the network the neighboring nodes up to certain distance from a center.
	 * @param id from the node to start the search from
	 * @param distance maximum depth
	 * @param forward whether the traversal is forward or backward
	 */
	public void extendNeighborhood(int id, int distance, boolean forward) {
		Vertex v = hash.get(id);
		if (v == null)
		{
			try
		    {
			  String queryString = "select * from CFNodes where id = " + id + ";";
			  queryString = applyFilter(queryString, nodeFilter);
		      System.out.println(queryString);
		      Statement st = conn.createStatement();
		      ResultSet rs = st.executeQuery(queryString);
		      while (rs.next())
		      {
		    	    String label = rs.getString("label");
			        if (label != null)
			        	v = new Vertex(id, label);
			        else
			        	v = new Vertex(id);
			        
			        String colorString = rs.getString("color");
			        if (colorString != null)
			        {
			        	int pos = 0;
			        	float R, G, B;
			        	R = Float.parseFloat(colorString.substring(pos,colorString.indexOf(',',pos)));
	    				pos = colorString.indexOf(',',pos)+1;
		    			G = Float.parseFloat(colorString.substring(pos,colorString.indexOf(',',pos)));
		    			pos = colorString.indexOf(',',pos)+1;
		    			B = Float.parseFloat(colorString.substring(pos));
		    			v.setFillColor(new Color(R,G,B));
		    		}
			        
			        String borderColorString = rs.getString("borderColor");
			        if (borderColorString != null)
			        {
			        	int pos = 0;
			        	float R, G, B;
			        	R = Float.parseFloat(borderColorString.substring(pos,borderColorString.indexOf(',',pos)));
	    				pos = borderColorString.indexOf(',',pos)+1;
		    			G = Float.parseFloat(borderColorString.substring(pos,borderColorString.indexOf(',',pos)));
		    			pos = borderColorString.indexOf(',',pos)+1;
		    			B = Float.parseFloat(borderColorString.substring(pos));
		    			v.setColor(new Color(R,G,B));
		    		}
			        
			        float size = rs.getFloat("size");
			        if (size != 0)
			        	v.setSize(size);
			        
			        String shape = rs.getString("shape");
			        if (shape != null)
			        	v.setShape(shape);
			        
			        int width = rs.getInt("width");
			        if (width != 0)
			        	v.setWidth(width);

			        String var1 = rs.getString("var1");
			        if (var1 != null)
			        	v.setVar1(var1);

			        String var2 = rs.getString("var2");
			        if (var2 != null)
			        	v.setVar2(var2);
			        
			        String hide = rs.getString("hide");
			        if ((hide != null) && (hide.equals("true")))
			        	v.setExcluded(true);
	
			        addVertex(v);
			        hash.put(id, v);
		      }
		    }
		    catch (SQLException sqlEx)
		    {
		    	JOptionPane.showMessageDialog(null,sqlEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				System.err.println("Node not found");
				sqlEx.printStackTrace();
		    }
		}
		if ((forward == true) && (distance > 0) && (v != null))
		{
			try
			  {
				  String queryString = "select * from CFEdges where id_origin =" + v.getId() + ";";
				  queryString = applyFilter(queryString, edgeFilter);
			      System.out.println(queryString);
			      Statement st = conn.createStatement();
			      ResultSet rs = st.executeQuery(queryString);
			      while (rs.next())
			      {
			    	  int id_dest = rs.getInt("id_dest");
			    	  extendNeighborhood(id_dest, distance-1, true); 
			   
			    	  Edge e = new Edge();
			  		 	
			  			String label = rs.getString("label");
			  			e.setLabel(label);
			  			
			  			String colorString = rs.getString("color");
				        if (colorString != null)
				        {
				        	int pos = 0;
				        	float R, G, B;
				        	R = Float.parseFloat(colorString.substring(pos,colorString.indexOf(',',pos)));
		    				pos = colorString.indexOf(',',pos)+1;
			    			G = Float.parseFloat(colorString.substring(pos,colorString.indexOf(',',pos)));
			    			pos = colorString.indexOf(',',pos)+1;
			    			B = Float.parseFloat(colorString.substring(pos));
			    			e.setColor(new Color(R,G,B));
			    		}
				        
				        int width = rs.getInt("width");
				        if (width != 0)
				        	e.setWidth(width);

				        float weight = rs.getFloat("weight");
				        if (weight != 0)
				        	e.setWeight(weight);
				        
				        String var1 = rs.getString("var1");
				        if (var1 != null)
				        	e.setVar1(var1);

				        String var2 = rs.getString("var2");
				        if (var2 != null)
				        	e.setVar2(var2);
				        
				        String hide = rs.getString("hide");
				        if ((hide != null) && (hide.equals("true")))
				        	e.setExcluded(true);
			
				        if (directed)
				        	addEdge(e, v , hash.get(id_dest), EdgeType.DIRECTED);
				        else
				         	addEdge(e, v , hash.get(id_dest), EdgeType.UNDIRECTED);
					       
			      }
			  }
		      catch (SQLException sqlEx)
			  {
			    	JOptionPane.showMessageDialog(null,sqlEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					System.err.println("SQL error");
					sqlEx.printStackTrace();
			  }
		}
		if ((forward == false) && (distance > 0) && (v != null))
		{
			try
			  {
				  String queryString = "select * from CFEdges where id_dest =" + v.getId() + ";";
				  queryString = applyFilter(queryString, edgeFilter);
			      System.out.println(queryString);
			      Statement st = conn.createStatement();
			      ResultSet rs = st.executeQuery(queryString);
			      while (rs.next())
			      {
			    	  int id_origin = rs.getInt("id_origin");
			    	  extendNeighborhood(id_origin, distance-1, false);  
			    	  
			    	  Edge e = new Edge();
			  			
			  			String label = rs.getString("label");
			  			e.setLabel(label);
			  			
			  			String colorString = rs.getString("color");
				        if (colorString != null)
				        {
				        	int pos = 0;
				        	float R, G, B;
				        	R = Float.parseFloat(colorString.substring(pos,colorString.indexOf(',',pos)));
		    				pos = colorString.indexOf(',',pos)+1;
			    			G = Float.parseFloat(colorString.substring(pos,colorString.indexOf(',',pos)));
			    			pos = colorString.indexOf(',',pos)+1;
			    			B = Float.parseFloat(colorString.substring(pos));
			    			e.setColor(new Color(R,G,B));
			    		}
				        
				        int width = rs.getInt("width");
				        if (width != 0)
				        	e.setWidth(width);

				        float weight = rs.getFloat("weight");
				        if (weight != 0)
				        	e.setWeight(weight);
				        
				        String var1 = rs.getString("var1");
				        if (var1 != null)
				        	e.setVar1(var1);

				        String var2 = rs.getString("var2");
				        if (var2 != null)
				        	e.setVar2(var2);
				        
				        String hide = rs.getString("hide");
				        if ((hide != null) && (hide.equals("true")))
				        	e.setExcluded(true);
			
				        if (directed)
				        	addEdge(e, hash.get(id_origin) , v, EdgeType.DIRECTED);
				        else
				         	addEdge(e, hash.get(id_origin) , v, EdgeType.UNDIRECTED);
						   
			      }
			  }
		      catch (SQLException sqlEx)
			  {
			    	JOptionPane.showMessageDialog(null,sqlEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					System.err.println("SQL error");
					sqlEx.printStackTrace();
			  }
		}
	}
	
	/**
	 * Method that adds all the edges of the subgraph induced in the database by the nodes
	 * already added to the displayed network
	 */
	public void extendEdges() {
		for (Vertex v : getVertices())
		{
			  try
			  {
				  String queryString = "select * from CFEdges where id_origin =" + v.getId() + ";";
				  queryString = applyFilter(queryString, edgeFilter);
			      System.out.println(queryString);
			      Statement st = conn.createStatement();
			      ResultSet rs = st.executeQuery(queryString);
			      while (rs.next())
			      {
			    	  int id_dest = rs.getInt("id_dest");
			    	  Vertex v_dest = hash.get(id_dest);
			    	  if (v_dest != null)
			    	  {
				    		Edge e = new Edge();
				  			
				  			String label = rs.getString("label");
				  			e.setLabel(label);
				  			
				  			String colorString = rs.getString("color");
					        if (colorString != null)
					        {
					        	int pos = 0;
					        	float R, G, B;
					        	R = Float.parseFloat(colorString.substring(pos,colorString.indexOf(',',pos)));
			    				pos = colorString.indexOf(',',pos)+1;
				    			G = Float.parseFloat(colorString.substring(pos,colorString.indexOf(',',pos)));
				    			pos = colorString.indexOf(',',pos)+1;
				    			B = Float.parseFloat(colorString.substring(pos));
				    			e.setColor(new Color(R,G,B));
				    		}
					        
					        int width = rs.getInt("width");
					        if (width != 0)
					        	e.setWidth(width);
	
					        float weight = rs.getFloat("weight");
					        if (weight != 0)
					        	e.setWeight(weight);
					        
					        String var1 = rs.getString("var1");
					        if (var1 != null)
					        	e.setVar1(var1);
	
					        String var2 = rs.getString("var2");
					        if (var2 != null)
					        	e.setVar2(var2);
					        
					        String hide = rs.getString("hide");
					        if ((hide != null) && (hide.equals("true")))
					        	e.setExcluded(true);
				
					        if (directed)
					        	addEdge(e,v , v_dest, EdgeType.DIRECTED);
					        else
					         	addEdge(e,v , v_dest, EdgeType.UNDIRECTED);
						       
			    	  }
			      }
			  }
		      catch (SQLException sqlEx)
			  {
			    	JOptionPane.showMessageDialog(null,sqlEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					System.err.println("SQL error");
					sqlEx.printStackTrace();
			  }

		}
	}
	
	/**
	 * Method that substracts the neighborhood from a selected vertex
	 * @param vertex
	 */
	public void shrinkVertex(Vertex vertex)
	{
		for (Edge adjacentEdge : getOutEdges(vertex))
		{
			Vertex neighbor = getOpposite(vertex, adjacentEdge);
			removeEdge(adjacentEdge);
			if (getNeighborCount(neighbor) < 1)
			{
				hash.put(neighbor.getId(), null);
				removeVertex(neighbor);
			}
		}
	}
	
	/**
	 * Method that substracts the predecessors of a selected vertex
	 * @param vertex
	 */
	public void backShrinkVertex(Vertex vertex)
	{
		for (Edge adjacentEdge : getInEdges(vertex))
		{
			Vertex neighbor = getOpposite(vertex, adjacentEdge);
			removeEdge(adjacentEdge);
			if (getNeighborCount(neighbor) < 1)
			{
				hash.put(neighbor.getId(), null);
				removeVertex(neighbor);
			}
		}
	}
	
}


