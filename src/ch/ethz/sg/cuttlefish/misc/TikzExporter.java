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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
	private Map<Color, String> colors;
	
	/**
	 * General constructor for the class, pure object oriented approach.
	 * It is necessary to create the object with the network before printing.
	 * @param network
	 */
	public TikzExporter(BrowsableNetwork network){
		this.network = network;
		colors = new HashMap<Color, String>();
	}
	
	/**
	 * Private method that reads all used colors in the network
	 * and defines them in the Tikz document.
	 */
	private void defineColors() {
	
		for (Vertex vertex : network.getVertices()) 
		{
			Color fColor = vertex.getFillColor();
			if(fColor != null && !colors.containsKey(fColor) ) {
				colors.put(fColor, "COLOR"+colors.size());
				writeColor(fColor);
			}
			Color cColor = vertex.getColor();
			if(cColor != null && !colors.containsKey(cColor) ) {
				colors.put(cColor, "COLOR"+colors.size());
				writeColor(cColor);
			}
		}
		for( Edge edge : network.getEdges() ) {
			Color color = edge.getColor();
			if(color != null && !colors.containsKey(color) ) {
				colors.put(color, "COLOR"+colors.size());
				writeColor(color);
			}
		}		
	}	
	
	/**
	 * Private method that defines a color in the Tikz document.
	 * @param color The color to be written to the Tikz document
	 */
	private void writeColor(Color color) { 
		p.println("\\definecolor{"+colors.get(color)+"}{rgb}{"
				+(color.getRed()/255.0)+","
				+(color.getGreen()/255.0)+","
				+(color.getBlue()/255.0)+"}");
	}
	
	/**
	 * Method that prints all the available information in the form of a latex file
	 * @param file open File where to print
	 * @param netLayout layout to define the position of the vertices
	 */
	public void exportToTikz(File file, Layout<Vertex, Edge> netLayout){
		if(Utils.checkForDuplicatedVertexIds(network))
			Utils.reassignVertexIds(network);
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
			defineColors();
			
			p.println("\\pgfdeclarelayer{background}");
			p.println("\\pgfdeclarelayer{foreground}");
			p.println("\\pgfsetlayers{background,main,foreground}");
			p.println("\\begin{tikzpicture}");
			
			//Vertices will appear in the main layer while edges will be in the background
			for (Vertex v : network.getVertices())
				exportVertex(v);
			
			p.println("\\begin{pgfonlayer}{background}");
			//Arrow style for directed networks
			p.println("\\tikzset{EdgeStyle/.style = {->,shorten >=1pt,>=stealth, bend right=10}}");
			
			exportEdges();
			
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
	
	private String escapeChars(String s) {
		String[] chars = {"&", "_", "%"};
		for(String ch : chars) {
			s = s.replace(ch, "\\"+ch);
		}
		return s;
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
			p.print(" draw=" + colors.get(vertex.getColor()) + ",");
		if (vertex.getFillColor() != null)
			p.print(" fill=" + colors.get(vertex.getFillColor()) + ",");
		p.print(" minimum size = " + Utils.ensureDecimal((vertex.getSize())) + "pt,");
		
		if ((vertex.getLabel() != null) && (true))
		{			
			p.print(" label={[label distance=-5]"+ calculateAngle(vertex)+":" + escapeChars(vertex.getLabel()) + "},");
		}
		//TODO: now all vertices are shaded, add a variable in cxf to determine that
		p.print(" shading=ball,");
		if (vertex.getFillColor() != null) //The color reappears in the shading
			p.print(" ball color="+ colors.get(vertex.getFillColor() ) );
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
	 * Private method that exports all edges and writes them to the
	 * Tikz file.
	 */
	private void exportEdges() {
		ArrayList<Edge> edgeList = new ArrayList<Edge>(network.getEdges());
		if(edgeList.size() == 0) return;
		Collections.sort(edgeList, new Comparator<Edge>() {
			@Override
			public int compare(Edge edge1, Edge edge2) {
				if( network.getEdgeType(edge1) != network.getEdgeType(edge2) ) {
					if(network.getEdgeType(edge1) == EdgeType.DIRECTED) return -1;
					else return 1;
				}
				if(edge1.getColor() != edge2.getColor() ) {
					return edge1.getColor().hashCode() - edge2.getColor().hashCode();
				}
				if(edge1.getWidth() != edge2.getWidth() ) {
					if(edge1.getWidth() < edge2.getWidth() ) return -1;
					else return 1;
				}
				return 0;
			}			
		});
		EdgeType curEdgeType = null;
		Color curColor = null;
		double curWidth = java.lang.Double.MAX_VALUE;
		for(Edge edge : edgeList) {
			System.out.println("Edge: " + network.getEdgeType(edge) + ' ' + edge.getColor().toString() + ' ' + edge.getWidth() );
			EdgeType edgeType = network.getEdgeType(edge);
			Color color = edge.getColor();
			double width = edge.getWidth();
			// If any of the edge properties is different from the current edge settings,
			// we need to redefine the edge settings
			if(edgeType != curEdgeType || !color.equals(curColor) || width != curWidth) {
				curEdgeType = edgeType;
				curColor = color;
				curWidth = width;
				p.print("\\tikzset{EdgeStyle/.style = {");
				if(curEdgeType == EdgeType.DIRECTED) p.print("->, ");
				else p.print("-, ");
				p.print("shorten >=1pt, >=stealth, bend right=10, ");
				p.print("line width=" + Utils.ensureDecimal(curWidth*WIDTHFACTOR) );
				if(curColor != null)
					p.println(", color=" + colors.get(curColor) + "}}");
				else
					p.println("}}");
			}
			Vertex v1, v2;
			if (edgeType == EdgeType.DIRECTED)	{
				v1 = network.getSource(edge);
				v2 = network.getDest(edge);
			} else {
				Pair<Vertex> endpoints = network.getEndpoints(edge);
				v1 = endpoints.getFirst();
				v2 = endpoints.getSecond();
			}
			if (v1.getId() == v2.getId()) {
				exportLoopEdge(edge, v1);
				continue;
			}
			p.print("\\Edge ");
			if ((edge.getLabel() != null) && (! hideEdgeLabels))
				p.print("[label=" + escapeChars(edge.getLabel()) + "]");			
			p.print("(" + v1.getId() + ")(" + v2.getId() + ")\n");
		}
	}
	
	/**
	 * Private method that exports a loop edge
	 * @param edge the loop edge
	 * @param v1 the vertex that has the loop edge
	 */
	private void exportLoopEdge(Edge edge, Vertex v) {
		double angle = calculateAngle(v);
			
		if	((angle > 124) && (angle < 226)) //two kinds of loops, in the left or right of the node
			p.print("\\Loop[dist=1cm,dir=WE,");
		else
			p.print("\\Loop[dist=1cm,dir=EA,");
			p.print("style={->,shorten >=1pt,>=stealth,line width="+ Utils.ensureDecimal(edge.getWidth()*WIDTHFACTOR));
		    p.print("}, color="+colors.get(edge.getColor()));
		    if ((edge.getLabel() != null) && (! hideEdgeLabels))
		    	p.print(", label="+ edge.getLabel());

		    p.print("](" + v.getId() + ")\n");
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
