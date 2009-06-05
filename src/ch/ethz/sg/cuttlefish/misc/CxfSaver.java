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
package ch.ethz.sg.cuttlefish.misc;

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * This class implements a saver for any network in the cxf file format, taking the positions 
 * in the current layout.
 * @author gadavid
 */
public class CxfSaver {

	BrowsableNetwork network;
	Layout<Vertex,Edge> layout;
	PrintStream ps;
	
	/**
	 * Constructor for the saver
	 * @param network to save
	 * @param layout to get the position of the nodes of the network
	 */
	public CxfSaver(BrowsableNetwork network, Layout<Vertex,Edge> layout)
	{
		this.network = network;
		this.layout = layout;
	}
	
	
	
	/**
	 * saves the data of the network used to create it and the layout
	 * @param file open and created file to save
	 */
	public void save(File file)
	{
		try {
			ps = new PrintStream(file);
			boolean hideVertexLabels = false;
			boolean hideEdgeLabels = false;
			if (network instanceof CxfNetwork)
			{
				//These two variables can only be defined if the network was loaded from cxf
				hideVertexLabels = ((CxfNetwork)network).hideVertexLabels();
				hideEdgeLabels = ((CxfNetwork)network).hideEdgeLabels();
			}
			boolean isDirected = false;
			
			for (Edge edge : network.getEdges())
				if (network.getEdgeType(edge) == EdgeType.DIRECTED)
					isDirected = true;
			
			// Setting up the configuration line
			if ((!isDirected) || hideVertexLabels || hideEdgeLabels)
				ps.print("configuration:");
			
			if (!isDirected)
				ps.print(" undirected");
			if (hideVertexLabels)
				ps.print(" hide_node_labels");
			if (hideEdgeLabels)
				ps.print(" hide_edge_labels");

			if ((!isDirected) || hideVertexLabels || hideEdgeLabels)
				ps.print("\n");
			
			for (Vertex vertex : network.getVertices())
				printVertex(vertex);
			
			for (Edge edge : network.getEdges())
				printEdge(edge);
			
			ps.close();
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("File not found while saving network");
			fnfEx.printStackTrace();
		}
		
	}

	/**
	 * private method to print a vertex in the file
	 * @param vertex
	 */
	private void printVertex(Vertex vertex)
	{	
		ps.print("node: (" + vertex.getId() + ")");
		if (vertex.getLabel() != null)
			ps.print(" label{"+vertex.getLabel()+"}");
		if (vertex.getFillColor() != null)
			ps.print(" color{"+((double)vertex.getFillColor().getRed()/256.d)
					+","+((double)vertex.getFillColor().getGreen()/256.d)+","+((double)vertex.getFillColor().getBlue()/256.d)+"}");
		if (vertex.getColor() != null)
			ps.print(" borderColor{"+((double)vertex.getColor().getRed()/256.d)
					+","+((double)vertex.getColor().getGreen()/256.d)+","+((double)vertex.getColor().getBlue()/256.d)+"}");
		ps.print(" size{"+vertex.getSize()+"}");
		if (vertex.getShape() != null)
		{
			if (vertex.getShape() instanceof RectangularShape)
				ps.print(" shape{rectangle}");
			else
				ps.print(" shape{circle}");
		}
		
		double x = ((Point2D)layout.transform(vertex)).getX();
		double y = ((Point2D)layout.transform(vertex)).getY();
		ps.print(" position{"+x+","+y+"}");
		
		if (vertex.getWidth() != 1)
			ps.print(" width{"+vertex.getWidth()+"}");
		
		if (vertex.getVar1() != null)
			ps.print(" var1{"+vertex.getVar1()+"}");
		if (vertex.getVar2() != null)
			ps.print(" var2{"+vertex.getVar2()+"}");
		if (vertex.isExcluded())
			ps.print(" hide");
			
		ps.print("\n");
	}
	
	/**
	 * private method to print an edge in the file
	 * @param edge
	 */
	private void printEdge(Edge edge)
	{

		if (network.getEdgeType(edge) == EdgeType.DIRECTED)
			ps.print("edge: ("+ network.getSource(edge).getId() +","+ network.getDest(edge).getId()+")");
		else
		{
			Pair<Vertex> endpoints = network.getEndpoints(edge);
			ps.print("edge: ("+ endpoints.getFirst().getId() +","+ endpoints.getSecond().getId()+")");	
		}
		if (edge.getLabel() != null)
			ps.print(" label{"+edge.getLabel()+"}");
		ps.print(" weight{"+edge.getWeight()+"}");
		ps.print(" width{"+edge.getWidth()+"}");
		if (edge.getColor() != null)
			ps.print(" color{"+((double)edge.getColor().getRed()/256.d)
					+","+((double)edge.getColor().getGreen()/256.d)+","+((double)edge.getColor().getBlue()/256.d)+"}");
		if (edge.getVar1() != null)
			ps.print(" var1{"+edge.getVar1()+"}");
		if (edge.getVar2() != null)
			ps.print(" var2{"+edge.getVar2()+"}");
		if (edge.isExcluded())
			ps.print(" hide");
		ps.print("\n");
	}	
}
