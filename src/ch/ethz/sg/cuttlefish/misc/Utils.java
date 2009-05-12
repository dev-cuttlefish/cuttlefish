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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JOptionPane;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class Utils {
	static double x  = Double.MIN_VALUE;
	static double y  = Double.MIN_VALUE;
	static double x0 = Double.MAX_VALUE; 
	static double y0 = Double.MAX_VALUE;

	
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
			fillColor=vertex.getFillColor().toString();
			color=vertex.getColor().toString();
		}
		
		Integer intSize=10;
		Double r = vertex.getSize();
		if (r != null) {
			intSize = new Integer((int) Math.floor(r/2));
		}
		
		
		Integer w = vertex.getWidth();
	

		double rim=80.0;
		
		p.println("\\Cnode[" +
				"fillstyle=solid, " +
				"fillcolor="+vertex.toString()+"FILL, " +
				"linecolor=" +vertex.toString()+"COLOR, " +
				"linewidth=" +(new DecimalFormat("###.#######################").format((w*f)))+", " +
				"radius="+(new DecimalFormat("###.#######################").format((intSize*f)))+"]("+
				(new DecimalFormat("#####.#######################").format(coordinates.getX()*f))+","+
				(new DecimalFormat("#####.#######################").format((y+rim-coordinates.getY())*f)) +")" +
						"{"+vertex.toString()+"}");
		String label = vertex.getLabel();
		
		
		if(detail && label != null){
			label = label.replace("_", "\\_").replace("&", "\\&");
			p.println("\\rput[l]("+
					(new DecimalFormat("###.#######################").format((coordinates.getX()-5)*f))+","+
					(new DecimalFormat("###.#######################").format(((y+rim-coordinates.getY())-10)*f)) 
					+"){\\textbf{\\tiny "+label+"}}");
		}

	}
	
	@SuppressWarnings("unchecked")
	static public void exportGraphToPSTricks(Graph<Vertex,Edge> graph, PrintStream p, Layout<Vertex,Edge> layout, boolean frame){
    	
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
		
		p.println("\\psset{unit=0.5mm}");
		double rim=40.0;
		p.println("\\begin{pspicture}("+(int)(x0*f-rim)+","+(int)(y0*f-rim)+")("+(int)(x*f+6*rim)+","+(int)(y*f+rim)+")");
		
	
		
		
		
		for (Vertex vertex : vertices) {
			exportVertex(vertex, layout, f, p, false);
		}
			
		Collection<Edge> edges = graph.getEdges();
		for (Edge edge : edges) {
			exportEdge(edge, graph,layout, f, p);
		
		}
		
		for (Vertex vertex : vertices) {
			exportVertex(vertex, layout, f, p, true);
		}

		p.println("\\end{pspicture}");
		
		if(frame){
			p.println("\\end{document}");
		}
	}

	private static void exportEdge(Edge edge, Graph<Vertex,Edge> graph, Layout<Vertex,Edge> layout,
			float f, PrintStream p) {
		String scolor= "black";

		Color cColor = edge.getColor();
		p.println("\\definecolor{temp}{rgb}{"
				+(cColor.getRed()/255.0)+","
				+(cColor.getGreen()/255.0)+","
				+(cColor.getBlue()/255.0)+"}");
			scolor = "temp";
		
	
		Double lineWidth = 1.0;
		Double lw = edge.getWidth();
		if (lw != null)
			lineWidth = (Double) lw/1.5;

		if (graph.getEdgeType(edge) == EdgeType.DIRECTED)
		{
			String source = graph.getSource(edge).toString();
			String dest = graph.getDest(edge).toString();
		
			if(dest.equals(source)){
				Integer intSize=10;
				Double size = graph.getSource(edge).getSize();
				if (size != null) {
					intSize = (Integer) (new Integer((int)Math.floor(size)))/2;
					
				}
				p.println("\\nccircle[linecolor="+scolor+", linewidth="+
						(new DecimalFormat("###.#######################").format((lineWidth*f)))
						+"]{->}{"+source+"}{"+(intSize+lineWidth)/2.0+"}");
			}else{			
				p.println("\\ncarc[linecolor="+scolor+", linewidth="+
						(new DecimalFormat("###.#######################").format((lineWidth*f)))
						+ ", arrowsize=4pt 4]{->}{"+source+"}{"+dest+"}");
			}
		}
		else
		{
			String first = graph.getEndpoints(edge).getFirst().toString();
			String second = graph.getEndpoints(edge).getSecond().toString();
		
			if(first.equals(second)){
				Integer intSize=10;
				Double size = graph.getSource(edge).getSize();
				if (size != null) {
					intSize = (Integer) (new Integer((int)Math.floor(size)))/2;
					
				}
				p.println("\\nccircle[linecolor="+scolor+", linewidth="+
						(new DecimalFormat("###.#######################").format((lineWidth*f)))
						+"]{-}{"+first+"}{"+(intSize+lineWidth)/2.0+"}");
			}else{			
				p.println("\\ncarc[linecolor="+scolor+", linewidth="+
						(new DecimalFormat("###.#######################").format((lineWidth*f)))
						+"]{-}{"+first+"}{"+second+"}");
			}
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
	
	public static File createLocalFile(String fileName, Object object)
	{
		InputStream inStream = object.getClass().getResourceAsStream(fileName);
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

}
