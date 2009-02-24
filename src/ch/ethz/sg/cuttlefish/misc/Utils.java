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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class Utils {
	
	/*
	@SuppressWarnings("unchecked")
	public static void graphToGScript(Graph graph, PrintStream ps){
		ps.println("creategraph "+ graph.getVertices().size());
		Set<Vertex> vertices = graph.getVertices();
		
		int c = 0;
		for (Vertex vertex : vertices) {
			vertex.addUserDatum(SGUserData.IDTEMP, new Integer(c++), UserData.REMOVE);
		}
		
		for (Vertex vertex : vertices) {
			Set<Edge> outEdges = vertex.getOutEdges();
			for (Edge edge : outEdges) {
				
				String label = (String) vertex.getUserDatum(SGUserData.LABEL);
				Integer id = (Integer) vertex.getUserDatum(SGUserData.IDTEMP);
				Integer id2 = (Integer) edge.getOpposite(vertex).getUserDatum(SGUserData.IDTEMP);
				ps.println("addedge " + id + " " + id2);;
				if(label != null){
					ps.println("setstringprop " + id + " label " + label);
				}
				
			}
		}
	}
	*/
	public static void graphToDot(Graph<Vertex,Edge> graph, PrintStream ps){
		
		  ps.println("digraph g {");
          ps.println("node [style=filled, color=black, fillcolor=gray, shape=circle, label=\"\", height=.15, width=.15]");
          
          for (Vertex vertex : graph.getVertices()) {
        	  ps.println(vertex.toString() + "[label=\"" + vertex.getLabel() + "\"]");
          }
          for (Edge edge :graph.getEdges()) {
        	  ps.println( graph.getSource(edge)+ "->" + graph.getDest(edge).toString());
          }
          ps.println("}");
	}
	
	@SuppressWarnings("unchecked")
	public static int[][] graphToAdjacencyMatrix(Graph<Vertex,Edge> graph){
		int size = graph.getVertices().size();
		
		Vertex[] vertices = new Vertex[size];
		int c = 0;
		for (Vertex vertex : graph.getVertices()) {
			vertices[c++] = vertex;
		}
		
		int[][] matrix = new int[size][];
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new int[size];
			for (int j = 0; j < matrix[i].length; j++) {
				//matrix[i][j]= vertices[j].findEdge(vertices[i]) != null ? 1 : 0;
				matrix[i][j]= ( ( graph.findEdge(vertices[j], vertices[i]) != null ) 
						|| (graph.findEdge(vertices[i], vertices[j]) != null ) ) ? 1 : 0;
				
			}
		}
		return matrix;
	}
	
	public static void printMatrix(int[][] matrix, PrintStream out){
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				//out.print(matrix[i][j] == 1 ? "X" : "+");
				out.print(matrix[i][j] == 1 ? "1" : "0");
			}
			out.println();
		}
	}
	
	public static double[][] readMatrix(File file) throws IOException{
		 
		 FileReader fileReader = new FileReader(file);
	     BufferedReader input = new BufferedReader(fileReader);
	    
	     ArrayList<String> lines = new ArrayList<String>();
	     	{
			     String line;
			     while ((line = input.readLine()) != null) {
			    	 	lines.add(line);
			     }
	     	}
	     	

	     	
		     double[][] matrix = new double[lines.size()][];
		     for (int i = 0; i < matrix.length; i++) {
				matrix[i] = new double[matrix.length];
			}
		     
		    int x = 0;
		    for (String string: lines) {
				String[] entries = string.split("\t");
				for (int i = 0; i < entries.length; i++) {
					matrix[x][i] = Double.parseDouble(entries[i]);
				}
				x++;
			} 
		     
		return matrix;
	 
	     
	}
	
	public static DirectedSparseGraph<Vertex, Edge> matrixToGraph(double[][] matrix){
		DirectedSparseGraph<Vertex, Edge> graph = new DirectedSparseGraph<Vertex,Edge>();
		if(matrix.length != matrix[0].length){
			return null;
		}
		
		Vertex[] vertices = new Vertex[matrix.length];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = new Vertex(i);
			graph.addVertex(vertices[i]);
		}
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if(matrix[i][j] != 0.0){
					Edge edge = new Edge();
					graph.addEdge(edge,vertices[i], vertices[j]);
				}
			}
		}
		return graph;
	}
	
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	static public void writePositions(Graph<Vertex,Edge> graph, PrintStream p, Layout<Vertex,Edge> layout){
   
		double x0 = Double.MAX_VALUE; 
		double y0 = Double.MAX_VALUE;
		double x  = Double.MIN_VALUE;
		double y  = Double.MIN_VALUE;
		Collection<Vertex> vertices =  graph.getVertices();
		
		for (Vertex vertex : vertices) {
            Point2D c = layout.transform(vertex);
            x0 = Math.min(x0, c.getX());
            y0 = Math.min(y0, c.getY());
            x = Math.max(x, c.getX());
            y = Math.max(y, c.getY());
        }
		
		for (Vertex vertex : vertices) {
			Point2D coordinates = layout.transform(vertex);
			Integer id = vertex.getId();
			if(id!=null){
				p.println(
					id + " "+(coordinates.getX()-x0)+" "+
					(coordinates.getY()-y0));
			}
		}
	}
	
	static public void exportGraphToPSTricks(Graph<Vertex,Edge> graph, PrintStream p, Layout<Vertex,Edge> layout){
		exportGraphToPSTricks(graph, p, layout, true);
	}
	
	static private void writeVertexColorTable(Collection<Vertex> vertices, PrintStream p){
		for (Vertex vertex : vertices) {
			
			Color fColor = vertex.getFillColor();
				p.println("\\definecolor{"+ vertex.toString()+"FILL}{rgb}{"
					+(fColor.getRed()/255.0)+","
					+(fColor.getGreen()/255.0)+","
					+(fColor.getBlue()/255.0)+"}");
		
			Color cColor = vertex.getColor();
			p.println("\\definecolor{"+ vertex.toString()+"COLOR}{rgb}{"
				+(cColor.getRed()/255.0)+","
				+(cColor.getGreen()/255.0)+","
				+(cColor.getBlue()/255.0)+"}");
			
		}
	}
	
	static private void exportVertex(Vertex vertex, Layout<Vertex,Edge> layout, double f, PrintStream p, boolean detail){
		Point2D coordinates = layout.transform(vertex);

		String fillColor="std";
		String color="black";
		String width="1";
		
		if(detail){
			Color cColor = vertex.getFillColor();
			if (cColor != null)
			{
				p.println("\\definecolor{ftemp}{rgb}{"
			
					+(cColor.getRed()/255.0)+","
					+(cColor.getGreen()/255.0)+","
					+(cColor.getBlue()/255.0)+"}");
				fillColor = "ftemp";
			}	
			cColor = vertex.getColor();
			if (cColor != null)
			{
				p.println("\\definecolor{ctemp}{rgb}{"	
					+(cColor.getRed()/255.0)+","
					+(cColor.getGreen()/255.0)+","
					+(cColor.getBlue()/255.0)+"}");
				color = "ctemp";
			}		
		}
		
		Integer intSize=10;
		Double r = vertex.getSize();
		if (r != null) {
			intSize = new Integer((int) Math.floor(r/2));
		}
		
		
		Integer w = vertex.getWidth();
		if (w != null) {
			width = w.toString();
		}
	
		
		p.println("\\Cnode[" +
				"fillstyle=solid, " +
				"fillcolor="+fillColor+", " +
				"linecolor=" +color+", " +
				"linewidth=" +width+", " +
				"radius="+(intSize*f)+"]("+
				(coordinates.getX()*f)+","+
				(coordinates.getY()*f) +"){"+vertex.toString()+"}");
		String label = vertex.getLabel();
		
		
		if(detail && label != null){
			label = label.replace("_", "\\_").replace("&", "\\&");
			
			p.println("\\rput[l]("+
					((coordinates.getX()+ 15)*f)+","+
					(coordinates.getY()*f) +"){\\textsf{\\tiny "+label+"}}");
		}

	}
	
	@SuppressWarnings("unchecked")
	static public void exportGraphToPSTricks(Graph<Vertex,Edge> graph, PrintStream p, Layout<Vertex,Edge> layout, boolean frame){
    	
		double x0 = Double.MAX_VALUE; 
		double y0 = Double.MAX_VALUE;
		double x  = Double.MIN_VALUE;
		double y  = Double.MIN_VALUE;
		Collection<Vertex> vertices = graph.getVertices();
		float f = 0.5f;
		
		for (Vertex vertex : vertices) {
            Point2D c = layout.transform(vertex);
            x0 = Math.min(x0, c.getX());
            y0 = Math.min(y0, c.getY());
            x = Math.max(x, c.getX());
            y = Math.max(y, c.getY());
        }
		
		
		if(frame){
			p.println("\\documentclass[a4paper]{article}");
		    p.println("\\usepackage{a4wide}");
			p.println("\\usepackage{pst-pdf}");
			p.println("\\usepackage{pst-node}");
			p.println("\\usepackage{xcolor}");

			p.println("\\begin{document}");
			
		}
		writeVertexColorTable(vertices, p);
		Color std = Color.DARK_GRAY;
		p.println("\\definecolor{std}{rgb}{"
				+(std.getRed()/255.0)+","
				+(std.getGreen()/255.0)+","
				+(std.getBlue()/255.0)+"}");
		
		p.println("\\psset{unit=0.2mm}");
		double rim=40.0;
		p.println("\\begin{pspicture}("+(int)(x0*f-rim)+","+(int)(y0*f-rim)+")("+(int)(x*f+rim)+","+(int)(y*f+rim)+")");
		
		for (Vertex vertex : vertices) {
			exportVertex(vertex, layout, f, p, true);
		}
			
		Collection<Edge> edges = graph.getEdges();
		for (Edge edge : edges) {
			exportEdge(edge, graph,layout, f, p);
		
		}
		
//		for (Vertex vertex : vertices) {
//			exportVertex(vertex, layout, f, p, true);
//		}

		p.println("\\end{pspicture}");
		
		if(frame){
			p.println("\\end{document}");
		}
	}

	private static void exportEdge(Edge edge, Graph<Vertex,Edge> graph, Layout<Vertex,Edge> layout,
			float f, PrintStream p) {
		String scolor= "black";

		Color cColor = edge.getColor();
		if (cColor != null)
		{
			p.println("\\definecolor{temp}{rgb}{"
		
				+(cColor.getRed()/255.0)+","
				+(cColor.getGreen()/255.0)+","
				+(cColor.getBlue()/255.0)+"}");
			scolor = "temp";
		}
	
		Double lineWidth = 1.0;
		Double lw = edge.getWidth();
		if (lw != null)
			lineWidth = (Double) lw/1.5;

		String source = graph.getSource(edge).toString();
		String dest = graph.getDest(edge).toString();
		
		
		
		if(dest.equals(source)){
			Integer intSize=10;
			Double size = graph.getSource(edge).getSize();
			if (size != null) {
				intSize = (Integer) (new Integer((int)Math.floor(size)))/2;
				
			}
			p.println("\\nccircle[linecolor="+scolor+", linewidth="+0.2*lineWidth*f+"]{->}{"+source+"}{"+(intSize+lineWidth)/2.0+"}");
		}else{			
			p.println("\\ncarc[linecolor="+scolor+", linewidth="+0.2*lineWidth*f+"]{->}{"+source+"}{"+dest+"}");
		}
	
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	public static void writeAdjacencyList(DirectedSparseGraph<Vertex,Edge> graph, PrintStream ps) {
		Hashtable<Integer, Vertex> table = new Hashtable< Integer, Vertex>();
		Hashtable<Vertex, Integer> rtable = new Hashtable<Vertex, Integer>();
		int count=0;
		for(Vertex v: (Set<Vertex>)graph.getVertices()){
			table.put(count, v);
			rtable.put(v, count);
			count++;
		}
		
		for(int i = 0; i < table.size(); i++){
			Vertex v = table.get(i);
			ps.print(graph.getSuccessors(v).size() + "\t");
			for(Vertex vn: graph.getSuccessors(v)){
				ps.print(rtable.get(vn)+"\t");
			}
			ps.println();
			
		}

		
	}
}
