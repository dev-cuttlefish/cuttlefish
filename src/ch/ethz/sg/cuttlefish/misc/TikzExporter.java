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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;

import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Class for exporting the information of a network in a latex file using TikZ
 * @author david
 */
public class TikzExporter {

	private BrowsableNetwork network = null;
	private File outFile = null;
	private Layout <Vertex, Edge> layout = null;
	private PrintStream p = null;
	private final double FACTOR = 0.025;
	
	/**
	 * General constructor for the class, pure object oriented approach.
	 * It is necessary to create the object with the network before printing.
	 * @param network
	 */
	public TikzExporter(BrowsableNetwork network){
		this.network = network;
	}
	
	/**
	 * Method that prints all the available information in the form of a latex file
	 * @param file open File where to print
	 * @param netLayout layout to define the position of the vertices
	 */
	public void exportToTikz(File file, Layout<Vertex, Edge> netLayout){
		outFile = file;
		layout = netLayout;
		
		try {
			p = new PrintStream(outFile);
		
			p.println("\\documentclass[a4paper]{article}");
		    p.println("\\usepackage{a4wide}");
			p.println("\\usepackage{pst-pdf}");
			p.println("\\usepackage{pst-node}");
			p.println("\\usepackage{xcolor}");
			p.println("\\usepackage{tikz}");
	
			p.println("\\begin{document}");
			
			writeVertexColorTable();
			writeEdgeColorTable();;
			
			p.println("\\begin{tikzpicture}");
			
			for (Vertex v : network.getVertices())
				exportVertex(v);
			
			for (Edge e : network.getEdges())
				exportEdge(e);
			
			p.println("\\end{tikzpicture}");
			p.println("\\end{document}");
			
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Error trying to save in Tikz");
			fnfEx.printStackTrace();
		}
		return;
	}
	
	/**
	 * Private method that defines the colors of each vertex from its id 
	 * (it is s
	 */
	private void writeVertexColorTable()
	{
		if ((outFile == null) || (layout == null))
			return;
			
		for (Vertex vertex : network.getVertices()) 
		{
			Color fColor = vertex.getFillColor();
			if (fColor != null)
				p.println("\\definecolor{"+ vertex.getId()+"FILL}{rgb}{"
						+(fColor.getRed()/255.0)+","
						+(fColor.getGreen()/255.0)+","
						+(fColor.getBlue()/255.0)+"}");
		
			Color cColor = vertex.getColor();
			if (cColor != null)
				p.println("\\definecolor{"+ vertex.getId()+"COLOR}{rgb}{"
						+(cColor.getRed()/255.0)+","
						+(cColor.getGreen()/255.0)+","
						+(cColor.getBlue()/255.0)+"}");
			
		}
		return;
	}

	private void writeEdgeColorTable()
	{
		if ((outFile == null) || (layout == null))
			return;
			
		for (Edge edge : network.getEdges()) 
		{
			Color color = edge.getColor();
			if (color != null)
				p.println("\\definecolor{"+ edge.toString()+"}{rgb}{"
						+(color.getRed()/255.0)+","
						+(color.getGreen()/255.0)+","
						+(color.getBlue()/255.0)+"}");
		}
		return;
	}

	
	private void exportVertex(Vertex vertex)
	{
		Point2D coordinates = layout.transform(vertex);
		p.print("\\node at (" + Utils.ensureDecimal(coordinates.getX()*FACTOR)
				+ "," + Utils.ensureDecimal(coordinates.getY()*FACTOR) + ") [");
		if (vertex.getShape() != null)
		{
			if (vertex.getShape() instanceof Rectangle2D)
				p.print("rectangle,");
			else
				p.print("circle,");
		}
		p.print(" line width=" + Utils.ensureDecimal(vertex.getWidth()) + ",");
		
		if (vertex.getColor() != null)
			p.print("draw="+vertex.getId()+"COLOR,");
		if (vertex.getFillColor() != null)
			p.print("fill="+vertex.getId()+"FILL,");
		p.print(" minimum size = " + Utils.ensureDecimal((vertex.getSize())) + "pt,");
		
		//TODO: check hide_vertex_labels here
		if ((vertex.getLabel() != null) && (true))
		{
			String latexLabel = vertex.getLabel().replace("&", "\\&");
			p.print(" label=0:" + latexLabel + ",");
		}
		p.print(" shading=ball] (" + vertex.getId() + ") {};\n");
	}
	
	private void exportEdge(Edge edge)
	{
		p.print("\\draw [");
		if (network.getEdgeType(edge) == EdgeType.DIRECTED)
			p.print("->,");
		else
			p.print("-,");
			
		p.print(" line width=" + Utils.ensureDecimal(edge.getWidth()) +", bend right, sloped, looseness=0.1," );
		if (edge.getColor() != null)
			p.print(" draw="+edge.toString());
		Vertex v1, v2;
		if (network.getEdgeType(edge) == EdgeType.DIRECTED)
		{
			v1 = network.getSource(edge);
			v2 = network.getDest(edge);
		}
		else
		{
			Pair<Vertex> endpoints = network.getEndpoints(edge);
			v1 = endpoints.getFirst();
			v2 = endpoints.getSecond();
		}
		
		p.print("] (" + v1.getId() + ") to" );
		//TODO: check hide_edge_labels here
		if ((edge.getLabel() != null) && (true))
			p.print(" node[above] {" + edge.getLabel() + "}");
		p.print(" (" + v2.getId() + ");\n");	
	}
}
