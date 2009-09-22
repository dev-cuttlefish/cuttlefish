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

import java.awt.HeadlessException;
import java.sql.*;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
* Network class that loads data from a database and sets the graph
* @author David Garcia Becerra
*/
public class DBNetwork extends BrowsableNetwork {

	private static final long serialVersionUID = 1L;
	
	Connection conn;
	HashMap<Integer,Vertex> hash = new HashMap<Integer,Vertex>();
	
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
		hash.clear();
	}
	
	/**
	 * Executes a proper node query and adds all the resulting nodes to the network
	 * @param queryString
	 */
	public void nodeQuery(String queryString) {
		    try
		    {
		      Statement st = conn.createStatement();
		      ResultSet rs = st.executeQuery(queryString);
		      while (rs.next())
		      {
		        int id = rs.getInt("id");
		        String label = rs.getString("label");
		        Vertex v = new Vertex(id, label); 
		        addVertex(v);
		        hash.put(id, v);
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
	
	public void extendNeighborhood(int id, int distance) {
		Vertex v = hash.get(id);
		if (v == null)
		{
			try
		    {
			  String queryString = "select id, label from CFNodes where id = " + id + ";";
		      Statement st = conn.createStatement();
		      ResultSet rs = st.executeQuery(queryString);
		      while (rs.next())
		      {
		        String label = rs.getString("label");
		        v = new Vertex(id, label); 
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
		if (distance > 0)
		{
			try
			  {
				  String queryString = "select * from CFEdges where id_origin =" + v.getId() + ";";
			      Statement st = conn.createStatement();
			      ResultSet rs = st.executeQuery(queryString);
			      while (rs.next())
			      {
			    	  int id_dest = rs.getInt("id_dest");
			    	  extendNeighborhood(id_dest, distance-1);  
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
	
	public void extendEdges() {
		for (Vertex v : getVertices())
		{
			  try
			  {
				  String queryString = "select * from CFEdges where id_origin =" + v.getId() + ";";
			      Statement st = conn.createStatement();
			      ResultSet rs = st.executeQuery(queryString);
			      while (rs.next())
			      {
			    	  int id_dest = rs.getInt("id_dest");
			    	  Vertex v_dest = hash.get(id_dest);
			    	  if (v_dest != null)
			    		  addEdge(new Edge(),v , v_dest, EdgeType.DIRECTED);
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
	
}


