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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;
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
	private final double WIDTHFACTOR = 0.5;
	private double maxY= 0;
	private boolean hideVertexLabels = false;
	private boolean hideEdgeLabels = false;
	
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
		
		//We find the maximum value of Y to revert the y coordinate when writing the nodes in tex
		for (Vertex vertex : network.getVertices())
		{
			double y = layout.transform(vertex).getY();
			if (y > maxY)
				maxY = y;
		}
		
		try {
			p = new PrintStream(outFile);
		
			p.println("\\documentclass{minimal}");		
			p.println("\\usepackage{tikz, tkz-graph}");
			p.println("\\usepackage[active,tightpage]{preview}");
			p.println("\\PreviewEnvironment{tikzpicture}");
			p.println("\\setlength\\PreviewBorder{5pt}");	
			p.println("\\begin{document}");
			
			//In pgf we need to define the colors outside the figure before using them
			writeVertexColorTable();
			writeEdgeColorTable();;
			
			p.println("\\pgfdeclarelayer{background}");
			p.println("\\pgfdeclarelayer{foreground}");
			p.println("\\pgfsetlayers{background,main,foreground}");
			p.println("\\begin{tikzpicture}");
			
			//Vertices will appear in the main layer while edges will be in the background
			for (Vertex v : network.getVertices())
				exportVertex(v);
			
			p.println("\\begin{pgfonlayer}{background}");
			//Arrow style for directed networks
			//TODO: Check arrows in undirected tikz 
			p.println("\\tikzset{EdgeStyle/.style = {->,shorten >=1pt,>=stealth, bend right=10}}");
			
			//The edges are sorted by wheight so the heavier ones will be plotted later and thus on top
			//of the lightest ones
			ArrayList<Edge> edgeList = new ArrayList<Edge>(network.getEdges());
			Collections.sort(edgeList);

			for (Edge e : edgeList)
				exportEdge(e);
			
			p.println("\\end{pgfonlayer}");
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

	/**
	 * Private method that defines the colors of each edge from its object identifier 
	 */
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

	/**
	 * Prints the necessary information to display a vertex in the tikz output
	 * @param vertex
	 */
	private void exportVertex(Vertex vertex)
	{
		Point2D coordinates = layout.transform(vertex);
		p.print("\\node at (" + Utils.ensureDecimal(coordinates.getX()*FACTOR)
				+ "," + Utils.ensureDecimal((maxY - coordinates.getY())*FACTOR) + ") [");
		if (vertex.getShape() != null)
		{
			if (vertex.getShape() instanceof Rectangle2D)
				p.print("rectangle,");
			else
				p.print("circle,");
		}
		p.print(" line width=" + Utils.ensureDecimal(vertex.getWidth()) + ",");
		
		if ((vertex.getColor() != null) && (vertex.getWidth() > 0))
			p.print(" draw="+vertex.getId()+"COLOR,");
		if (vertex.getFillColor() != null)
			p.print(" fill="+vertex.getId()+"FILL,");
		p.print(" minimum size = " + Utils.ensureDecimal((vertex.getSize())) + "pt,");
		
		if ((vertex.getLabel() != null) && (true))
		{
			String latexLabel = vertex.getLabel().replace("&", "\\&");
			latexLabel = latexLabel.replace("_", "\\_");
			p.print(" label={[label distance=-7]"+ calculateAngle(vertex)+":" + latexLabel + "},");
		}
		//TODO: now all vertices are shaded, add a variable in cxf to determine that
		p.print(" shading=ball,");
		if (vertex.getFillColor() != null) //The color reappears in the shading
			p.print(" ball color="+vertex.getId()+"FILL");
		else
			p.print(" ball color=black");			
		p.print("] (" + vertex.getId() + ") {};\n");
	}
	
	Point2D center = null;
	/**
	 * Private method that calculates the angle for the vertex label according to the four quadrant rule that
	 * paints the labels in the tex file in a way that they are on the other side of the center of the network.
	 * @param v vertex that is going to be painted
	 * @return
	 */
	private double calculateAngle(Vertex v)
	{
		if (center == null)
			center = Utils.caculateCenter(layout, network);
		Point2D vPos = layout.transform(v);
		if (vPos.getX() > center.getX())
		{
			if (vPos.getY() < center.getY())
				return 45;
			else
				return 315;
		}
		else
		{
			if (vPos.getY() < center.getY())
				return 135;
			else
				return 225;
		}
	}
	
	/**
	 * Prints the necessary information for tikz to represent an edge between given nodes
	 * @param edge to paint
	 */
	private void exportEdge(Edge edge)
	{
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
		
		// the edges are displayed as directed by default, if it is undirected, the line style
		// is overwritten to be arrowless
		if (network.getEdgeType(edge) == EdgeType.UNDIRECTED)
			p.println("\\tikzset{EdgeStyle/.append style = {-}}");
		
		if (v1.getId() == v2.getId()) //self loop case, it is written with Loop in pgf
		{	
			double angle = calculateAngle(v1);
			
			if	((angle > 124) && (angle < 226)) //two kinds of loops, in the left or right of the node
				p.print("\\Loop[dist=1cm,dir=WE,");
			else
				p.print("\\Loop[dist=1cm,dir=EA,");
			
			p.print("style={->,shorten >=1pt,>=stealth,line width="+ Utils.ensureDecimal(edge.getWidth()*WIDTHFACTOR));
		    p.print("}, color="+edge.toString());
		  
		    if ((edge.getLabel() != null) && (! hideEdgeLabels))
		    	p.print(", label="+ edge.getLabel());
		    
		    p.print("](" + v1.getId() + ")\n");
		}
		else
		{
			p.print("\\Edge [lw=" + Utils.ensureDecimal(edge.getWidth()*WIDTHFACTOR) );
			if (edge.getColor() != null)
				p.print(", color=" + edge.toString());
		
			if ((edge.getLabel() != null) && (! hideEdgeLabels))
				p.print(", label=" + edge.getLabel());
			
			p.print("](" + v1.getId() + ")(" + v2.getId() + ")\n");
			
		}

		//To return to default directed style, the arrow edge is reset
		if (network.getEdgeType(edge) == EdgeType.UNDIRECTED)
			p.println("\\tikzset{EdgeStyle/.append style = {->}}");

	}
	/**
	 * Getter to know if the vertex labels have to be ignored
	 * @return
	 */
	public boolean hiddenVertexLabels() {
		return hideVertexLabels;
	}

	/**
	 * Setter to hide the vertex labels
	 * @param hideVertexLabels
	 */
	public void setHideVertexLabels(boolean hideVertexLabels) {
		this.hideVertexLabels = hideVertexLabels;
	}

	/**
	 * Setter to hide the edge labels
	 * @param hideEdgeLabels
	 */
	public void setHideEdgeLabels(boolean hideEdgeLabels) {
		this.hideEdgeLabels = hideEdgeLabels;
	}

	/**
	 * Getter to know if the edge labels have to be ignored
	 * @return
	 */
	public boolean hiddenEdgeLabels() {
		return hideEdgeLabels;
	}
}
