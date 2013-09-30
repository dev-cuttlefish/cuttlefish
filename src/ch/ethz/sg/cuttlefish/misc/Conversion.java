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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import ch.ethz.sg.cuttlefish.networks.BrowsableForestNetwork;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class Conversion {

	public static void graphToDot(BrowsableNetwork graph, PrintStream ps) {
		ps.println("digraph g {");
		ps.println("node [style=filled, color=black, fillcolor=gray, shape=circle, label=\"\", height=.15, width=.15]");

		for (Vertex vertex : graph.getVertices()) {
			ps.println(vertex.toString() + "[label=\"" + vertex.getLabel()
					+ "\"]");
		}
		for (Edge edge : graph.getEdges()) {
			ps.println(graph.getSource(edge) + "->"
					+ graph.getDest(edge).toString());
		}
		ps.println("}");
	}

	public static int[][] graphToAdjacencyMatrix(BrowsableNetwork graph) {
		int size = graph.getVertexCount();

		Vertex[] vertices = new Vertex[size];
		int c = 0;
		for (Vertex vertex : graph.getVertices()) {
			vertices[c++] = vertex;
		}

		int[][] matrix = new int[size][];
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new int[size];
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = ((graph.findEdge(vertices[j], vertices[i]) != null) || (graph
						.findEdge(vertices[i], vertices[j]) != null)) ? 1 : 0;
			}
		}
		return matrix;
	}

	public static void printMatrix(int[][] matrix, PrintStream out) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				// out.print(matrix[i][j] == 1 ? "X" : "+");
				out.print(matrix[i][j] == 1 ? "1" : "0");
			}
			out.println();
		}
	}

	public static double[][] readMatrix(File file) throws IOException {

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
		for (String string : lines) {
			String[] entries = string.split("\t");
			for (int i = 0; i < entries.length; i++) {
				matrix[x][i] = Double.parseDouble(entries[i]);
			}
			x++;
		}

		input.close();
		return matrix;

	}

	@Deprecated
	public static BrowsableNetwork matrixToGraph(double[][] matrix) {
		BrowsableNetwork graph = new BrowsableNetwork();
		if (matrix.length != matrix[0].length) {
			return null;
		}

		Vertex[] vertices = new Vertex[matrix.length];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = new Vertex(i);
			graph.addVertex(vertices[i]);
		}

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] != 0.0) {
					Edge edge = new Edge(vertices[i], vertices[j], true);
					graph.addEdge(edge);
				}
			}
		}
		return graph;
	}

	static public void writePositions(BrowsableNetwork graph, PrintStream p) {

		double x0 = Double.MAX_VALUE;
		double y0 = Double.MAX_VALUE;
		double x = Double.MIN_VALUE;
		double y = Double.MIN_VALUE;
		Collection<Vertex> vertices = graph.getVertices();

		for (Vertex vertex : vertices) {
			Point2D c = vertex.getPosition();
			x0 = Math.min(x0, c.getX());
			y0 = Math.min(y0, c.getY());
			x = Math.max(x, c.getX());
			y = Math.max(y, c.getY());
		}

		for (Vertex vertex : vertices) {
			Point2D coordinates = vertex.getPosition();
			Integer id = vertex.getId();
			if (id != null) {
				p.println(id + " " + (coordinates.getX() - x0) + " "
						+ (coordinates.getY() - y0));
			}
		}
	}

	public static void writeEdgeList(BrowsableNetwork graph, PrintStream ps) {
		Hashtable<Vertex, Integer> table = new Hashtable<Vertex, Integer>();
		int count = 0;
		for (Vertex v : graph.getVertices()) {
			table.put(v, count);
			count++;
		}
		for (Edge e : graph.getEdges()) {
			ps.println(table.get(e.getSource()) + "\t"
					+ table.get(e.getTarget()));
		}

	}

	public static void writeAdjacencyList(BrowsableForestNetwork graph,
			PrintStream ps) {
		Hashtable<Integer, Vertex> table = new Hashtable<Integer, Vertex>();
		Hashtable<Vertex, Integer> rtable = new Hashtable<Vertex, Integer>();
		int count = 0;
		for (Vertex v : (Set<Vertex>) graph.getVertices()) {
			table.put(count, v);
			rtable.put(v, count);
			count++;
		}

		for (int i = 0; i < table.size(); i++) {
			Vertex v = table.get(i);
			ps.print(graph.getSuccessors(v).size() + "\t");
			for (Vertex vn : graph.getSuccessors(v)) {
				ps.print(rtable.get(vn) + "\t");
			}
			ps.println();

		}

	}
}
