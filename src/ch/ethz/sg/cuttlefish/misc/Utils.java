/*
    
    Copyright (C) 2008  David Garcia Becerra Markus Michael Geipel

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
package ch.ethz.sg.cuttlefish.misc;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * Class with static methods of general usage
 * @author david
 */
public class Utils {
	
	/**
	 * Calculates the center of masses of a network given its layout and as if the nodes had constant weight.
	 * @param layout positions of the nodes of the networks
	 * @param graph network to analyze
	 * @return Point2D with the coordinates of the center
	 */
	static public Point2D caculateCenter(Layout<Vertex, Edge> layout, Graph<Vertex,Edge> graph)
	{
		Point2D center = new Point2D.Double(0.0d,0.0d);
		double n = 0.0d;
		
		for (Vertex vertex : graph.getVertices())
		{
			Point2D vertexPosition = layout.transform(vertex);
			center.setLocation(center.getX() + vertexPosition.getX(), center.getY() + vertexPosition.getY());
			n++;
		}
		center.setLocation(center.getX() / n, center.getY() / n);
		return center;
	}

	/**
	 * Writes on the output a list of the edges of the network
	 * @param graph to print
	 * @param ps output stream
	 */
	public static void writeEdgeList(DirectedSparseGraph<Vertex,Edge> graph, PrintStream ps) {
		Hashtable<Vertex, Integer> table = new Hashtable<Vertex, Integer>();
		int count=0;
		for(Vertex v: graph.getVertices()){
			table.put(v, count);
			count++;
		}
		for(Edge e: graph.getEdges()){
			ps.println(table.get(graph.getSource(e)) + "\t"+table.get(graph.getDest(e)));
		}
		
	}
	
	/**
	 * Creates a local version of a file contained in the jar
	 * @param fileName name of the file in the jar
	 * @param object usually the object that calls createLocalFile and has the
	 * reference to the file
	 * @return exact local copy of the file
	 */
	public static File createLocalFile(String fileName, Object object)
	{
		InputStream inStream = object.getClass().getResourceAsStream(fileName);
		//TODO: careful! This does not work in Windows
		File copyFile = new File(fileName.substring(fileName.lastIndexOf('/')+1).concat("_aux"));
		
		OutputStream outStream;
		try {
			outStream = new FileOutputStream(copyFile);
			byte buf[]=new byte[1024];
			int len;
			while((len=inStream.read(buf))>0)
				outStream.write(buf,0,len);
			inStream.close();
			outStream.close();
			copyFile.deleteOnExit();
		}
		catch (FileNotFoundException fileEx) {
			JOptionPane.showMessageDialog(null,fileEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Error: "+ fileName +" not found");
			fileEx.printStackTrace();
		} catch (IOException ioEx) {
			JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Error: problem in " + fileName );
			ioEx.printStackTrace();
		}
		return copyFile;
	} 

	/**
	 * Ensures that the string representaton of the number is not exponential. 5 digits before "."
	 * @param num
	 * @return String with a decimal notation of the number
	 */
	public static String ensureDecimal(double num)
	{
			return (new DecimalFormat("#####.#######################").format((num)));
	}

	/**
	 * Erases the multiple edges of a graph
	 * @param graph
	 */
	public static void deleteDoubleEdges(DirectedSparseGraph<Vertex,Edge> graph){
		HashSet<Edge> toDelete = new HashSet<Edge>();
		for (Edge edge1 : graph.getEdges()) {
			for (Edge edge2 : graph.getEdges()) {
				if(graph.getSource(edge1) == graph.getDest(edge2) 
						&& graph.getSource(edge2) == graph.getDest(edge1)
						&& !toDelete.contains(edge1)
						&& graph.getSource(edge1).toString().compareTo(graph.getDest(edge1).toString()) > 0){
					toDelete.add(edge1);
				}
			}
		}
		System.out.println(toDelete.size());
		for (Edge edge : toDelete) {
			System.out.println(graph.getEdges().contains(edge));
			graph.removeEdge(edge);	
		}	
	}
	
	/**
	 * This method checks if there are duplicated vertices with the
	 * same ID. If there are duplicated IDs then we cannot save the
	 * network before reassigning unique IDs
	 * @return true if there are duplicated IDs, false otherwise.
	 */
	public static boolean checkForDuplicatedVertexIds(BrowsableNetwork network) {
		Set<Integer> ids = new HashSet<Integer>(); 
		for(Vertex vertex : network.getVertices() ) {
			if(ids.contains(vertex.getId())) {
				return true;
			}
			ids.add(vertex.getId());
		}
		return false;
	}
	
	/**
	 * This method assigns unique IDs to vertices.
	 */
	public static void reassignVertexIds(BrowsableNetwork network) {
		int curId = 0;
		for(Vertex vertex : network.getVertices() )
			vertex.setId(curId++);
	}
}
