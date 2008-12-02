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
package ch.ethz.sg.cuttlefish.misc;


import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.Layout;

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
	@SuppressWarnings({ "unchecked", "unchecked" })
	public static void graphToDot(Graph graph, PrintStream ps){
		
		  ps.println("digraph g {");
          ps.println("node [style=filled, color=black, fillcolor=gray, shape=circle, label=\"\", height=.15, width=.15]");
          
          for (DirectedSparseVertex vertex : (Set<DirectedSparseVertex>)graph.getVertices()) {
        	  ps.println(vertex.toString() + "[label=\"" + vertex.getUserDatum(SGUserData.LABEL) + "\"]");
          }
          for (DirectedSparseEdge edge : (Set<DirectedSparseEdge>)graph.getEdges()) {
        	 
        	  ps.println( edge.getSource().toString()+ "->" + edge.getDest().toString());
          }
          ps.println("}");
	}
	
	@SuppressWarnings("unchecked")
	public static int[][] graphToAdjacencyMatrix(Graph graph){
		int size = graph.getVertices().size();
		
		Set<Vertex> verticeSet = graph.getVertices();
		Vertex[] vertices = new Vertex[size];
		int c = 0;
		for (Vertex vertex : verticeSet) {
			vertices[c++] = vertex;
		}
		
		int[][] matrix = new int[size][];
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new int[size];
			for (int j = 0; j < matrix[i].length; j++) {
				
				
				//matrix[i][j]= vertices[j].findEdge(vertices[i]) != null ? 1 : 0;
				
				matrix[i][j]= ( ( vertices[j].findEdge(vertices[i]) != null ) || ( vertices[i].findEdge(vertices[j]) != null ) ) ? 1 : 0;
				
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
	
	public static DirectedSparseGraph matrixToGraph(double[][] matrix){
		DirectedSparseGraph graph = new DirectedSparseGraph();
		if(matrix.length != matrix[0].length){
			return null;
		}
		
		DirectedSparseVertex[] vertices = new DirectedSparseVertex[matrix.length];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = new DirectedSparseVertex();
			graph.addVertex(vertices[i]);
		}
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if(matrix[i][j] != 0.0){
					DirectedSparseEdge edge = new DirectedSparseEdge(vertices[i], vertices[j]);
					graph.addEdge(edge);
					edge.addUserDatum(SGUserData.WEIGHT, new Double(matrix[i][j]), UserData.CLONE );
				}
			}
		}
		return graph;
	}
	
	@SuppressWarnings("unchecked")
	public static void deleteDoubleEdges(DirectedSparseGraph graph){
		HashSet<DirectedSparseEdge> toDelete = new HashSet<DirectedSparseEdge>();
		for (DirectedSparseEdge edge1 : (Set<DirectedSparseEdge>) graph.getEdges()) {
			for (DirectedSparseEdge edge2 : (Set<DirectedSparseEdge>)graph.getEdges()) {
				if(edge1.getSource() == edge2.getDest() 
						&& edge2.getSource() == edge1.getDest()
						&& !toDelete.contains(edge1)
						&& edge1.getSource().toString().compareTo(edge1.getDest().toString()) > 0){
					toDelete.add(edge1);
				}
			}
		}
		System.out.println(toDelete.size());
		for (DirectedSparseEdge edge : toDelete) {
			System.out.println(graph.getEdges().contains(edge));
			graph.removeEdge(edge);
			
		}
	
		
	}
	
	@SuppressWarnings("unchecked")
	static public void writePositions(Graph graph, PrintStream p, Layout layout){
   
		double x0 = Double.MAX_VALUE; 
		double y0 = Double.MAX_VALUE;
		double x  = Double.MIN_VALUE;
		double y  = Double.MIN_VALUE;
		Set<Vertex> vertices = graph.getVertices();
		
		for (Vertex vertex : vertices) {
            Point2D c = layout.getLocation(vertex);
            x0 = Math.min(x0, c.getX());
            y0 = Math.min(y0, c.getY());
            x = Math.max(x, c.getX());
            y = Math.max(y, c.getY());
        }
		
		
		for (Vertex vertex : vertices) {
			Point2D coordinates = layout.getLocation(vertex);
			String id = (String) vertex.getUserDatum(SGUserData.ID);
			if(id!=null){
				p.println(
					id + " "+(coordinates.getX()-x0)+" "+
					(coordinates.getY()-y0));
			}
		}
	}
	
	static public void exportGraphToPSTricks(Graph graph, PrintStream p, Layout layout){
		exportGraphToPSTricks(graph, p, layout, true);
	}
	
	static private void writeVertexColorTable(Set<Vertex> vertices, PrintStream p){
		for (Vertex vertex : vertices) {
			Object o;
			o = vertex.getUserDatum(SGUserData.FILLCOLOR);
			if (o instanceof Color) {
				Color cColor = (Color) o;
				
				p.println("\\definecolor{"+ vertex.toString()+"FILL}{rgb}{"
					+(cColor.getRed()/255.0)+","
					+(cColor.getGreen()/255.0)+","
					+(cColor.getBlue()/255.0)+"}");
		
			}
			o = vertex.getUserDatum(SGUserData.COLOR);
			if (o instanceof Color) {
				Color cColor = (Color) o;
				
				p.println("\\definecolor{"+ vertex.toString()+"LINE}{rgb}{"
					+(cColor.getRed()/255.0)+","
					+(cColor.getGreen()/255.0)+","
					+(cColor.getBlue()/255.0)+"}");
		
			}
			
		}
	}
	
	static private void exportVertex(Vertex vertex, Layout layout, double f, PrintStream p, boolean detail){
		Point2D coordinates = layout.getLocation(vertex);
			
		

		String fillColor="std";
		String color="black";
		String width="1";
		
		Object o = vertex.getUserDatum(SGUserData.FILLCOLOR);
		
		if(detail){
			if (o instanceof Color) {
				fillColor=vertex.toString()+"FILL";
			}
		}
		
		Integer intSize=10;
		o = vertex.getUserDatum(SGUserData.RADIUS);
		if (o instanceof Integer) {
			intSize = (Integer) o/2;
			
		}
		if(detail){
			o = vertex.getUserDatum(SGUserData.COLOR);
			if (o instanceof Color) {
				color=vertex.toString()+"LINE";
			}
		}
		o= vertex.getUserDatum(SGUserData.WIDTH);
		if (o instanceof Integer) {
			Integer iWidth = (Integer) o;
			width = iWidth.toString();
		}
	
		
		p.println("\\Cnode[" +
				"fillstyle=solid, " +
				"fillcolor="+fillColor+", " +
				"linecolor=" +color+", " +
				"linewidth=" +width+", " +
				"radius="+(intSize*f)+"]("+
				(coordinates.getX()*f)+","+
				(coordinates.getY()*f) +"){"+vertex.toString()+"}");
		String label = (String)vertex.getUserDatum(SGUserData.LABEL);
		
		
		if(detail && label != null){
			label = label.replace("_", "\\_").replace("&", "\\&");
			
			p.println("\\rput[l]("+
					((coordinates.getX()+ 15)*f)+","+
					(coordinates.getY()*f) +"){\\textsf{\\tiny "+label+"}}");
		}

	}
	
	@SuppressWarnings("unchecked")
	static public void exportGraphToPSTricks(Graph graph, PrintStream p, Layout layout, boolean frame){
    	
		double x0 = Double.MAX_VALUE; 
		double y0 = Double.MAX_VALUE;
		double x  = Double.MIN_VALUE;
		double y  = Double.MIN_VALUE;
		Set<Vertex> vertices = graph.getVertices();
		float f = 0.5f;
		
		for (Vertex vertex : vertices) {
            Point2D c = layout.getLocation(vertex);
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
			exportVertex(vertex, layout, f, p, false);
		}
			
		Set<DirectedSparseEdge> edges = graph.getEdges();
		for (DirectedSparseEdge edge : edges) {
			exportEdge(edge, layout, f, p);
		
		}
		
		for (Vertex vertex : vertices) {
			exportVertex(vertex, layout, f, p, true);
		}

		p.println("\\end{pspicture}");
		
		if(frame){
			p.println("\\end{document}");
		}
	}

	private static void exportEdge(DirectedSparseEdge edge, Layout layout,
			float f, PrintStream p) {
		String scolor= "black";
		Object color = edge.getUserDatum(SGUserData.COLOR);

		if (color instanceof Color) {
			Color cColor = (Color) color;
			p.println("\\definecolor{temp}{rgb}{"
				+(cColor.getRed()/255.0)+","
				+(cColor.getGreen()/255.0)+","
				+(cColor.getBlue()/255.0)+"}");
			scolor = "temp";
		}	
		
		Object o = edge.getUserDatum(SGUserData.WIDTH);

		Double lineWidth = 1.0;
		if (o instanceof Double) {
			lineWidth = (Double) o/1.5;
			
		}	
		String source = edge.getSource().toString();
		String dest = edge.getDest().toString();
		
		
		
		if(dest.equals(source)){
			Integer intSize=10;
			Object size = edge.getSource().getUserDatum(SGUserData.RADIUS);
			if (size instanceof Integer) {
				intSize = (Integer) size/2;
				
			}
			p.println("\\nccircle[linecolor="+scolor+", linewidth="+0.2*lineWidth*f+"]{->}{"+source+"}{"+(intSize+lineWidth)/2.0+"}");
		}else{			
			p.println("\\ncarc[linecolor="+scolor+", linewidth="+0.2*lineWidth*f+"]{->}{"+source+"}{"+dest+"}");
		}
	
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	public static void writeEdgeList(DirectedSparseGraph graph, PrintStream ps) {
		Hashtable<Vertex, Integer> table = new Hashtable<Vertex, Integer>();
		int count=0;
		for(Vertex v: (Set<Vertex>)graph.getVertices()){
			table.put(v, count);
			count++;
		}
		for(DirectedSparseEdge e: (Set<DirectedSparseEdge>)graph.getEdges()){
			ps.println(table.get(e.getSource()) + "\t"+table.get(e.getDest()));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static void writeAdjacencyList(DirectedSparseGraph graph, PrintStream ps) {
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
			ps.print(v.getSuccessors().size() + "\t");
			for(Vertex vn: (Set<Vertex>)v.getSuccessors()){
				ps.print(rtable.get(vn)+"\t");
			}
			ps.println();
			
		}

		
	}
}
