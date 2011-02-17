package ch.ethz.sg.cuttlefish.misc;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;

import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Class for the exporter into a latex file written in PSTricks
 * @author david
 */
public class PSTricksExporter {

	private BrowsableNetwork network;
	static double x  = Double.MIN_VALUE;
	static double y  = Double.MIN_VALUE;
	static double x0 = Double.MAX_VALUE; 
	static double y0 = Double.MAX_VALUE;
	
	/**
	 * Default constructor for the exporter. Just sets the network
	 * @param network
	 */
	public PSTricksExporter(BrowsableNetwork network)
	{
		this.network = network;
	}

	/**
	 * Method that does the export.
	 * @param file output file where to write the latex code
	 * @param netLayout layout to define the position of the vertices
	 */
	public void exportToPSTricks(File file, Layout<Vertex, Edge> netLayout){
		PrintStream p;
		try {
			p = new PrintStream(file);
			exportGraphToPSTricks(network, p, netLayout, true);
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Error trying to save in PSTricks");
			fnfEx.printStackTrace();
		}
	}
	
	/**
	 * Private method to write the necessary color table for the vertices
	 * @param vertices vertex set to write
	 * @param p stream where to print
	 */
	private void writeVertexColorTable(Collection<Vertex> vertices, PrintStream p){
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
	
	/**
	 * Private method to print in the output stream the information about a vertex in PSTricks format
	 * @param vertex vertex to print
	 * @param layout used to find the position of the vertex
	 * @param f factor for the representation
	 * @param p stream for the output
	 * @param detail boolean whether to print the label or not
	 */
	private void exportVertex(Vertex vertex, Layout<Vertex,Edge> layout, double f, PrintStream p, boolean detail){
		Point2D coordinates = layout.transform(vertex);
	
		Integer intSize=10;
		Double r = vertex.getSize();
		if (r != null) {
			intSize = new Integer((int) Math.floor(r/2));
		}
		
		Integer w = vertex.getWidth();
		double rim=80.0;
		
		p.println("\\Cnode[" +
				"fillstyle=solid, " +
				"fihttp://www.texample.net/tikz/llcolor="+vertex.toString()+"FILL, " +
				"linecolor=" +vertex.toString()+"COLOR, " +
				"linewidth=" + Utils.ensureDecimal(w*f)+", " +
				"radius="+Utils.ensureDecimal(intSize*f)+"]("+
				Utils.ensureDecimal(coordinates.getX()*f)+","+
				Utils.ensureDecimal((y+rim-coordinates.getY())*f) +")" +
				"{"+vertex.toString()+"}");
		
		String label = vertex.getLabel();
		if(detail && label != null){
			label = label.replace("_", "\\_").replace("&", "\\&");
			p.println("\\rput[l]("+
					Utils.ensureDecimal((coordinates.getX()-5)*f)+","+
					Utils.ensureDecimal(((y+rim-coordinates.getY())-(10 +vertex.getSize()/2  ))*f) 
					+"){\\textbf{\\tiny "+label+"}}");
		}
	}
	
	/**
	 * Inner version of export method with settable frame
	 * @param graph network to print
	 * @param p stream for the output
	 * @param layout location of the nodes
	 * @param frame wheter to write preamble or not
	 */
	private void exportGraphToPSTricks(Graph<Vertex,Edge> graph, PrintStream p, Layout<Vertex,Edge> layout, boolean frame){
    	
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
		for (Vertex vertex : vertices)
			exportVertex(vertex, layout, f, p, false);
			
		Collection<Edge> edges = graph.getEdges();
		for (Edge edge : edges)
			exportEdge(edge, graph,layout, f, p);
		
		for (Vertex vertex : vertices)
			exportVertex(vertex, layout, f, p, true);
		
		p.println("\\end{pspicture}");
		
		if(frame){
			p.println("\\end{document}");
		}
	}

	/**
	 * Method that prints in the output the information of an edge in PSTricks format
	 * @param edge edge to print
	 * @param graph network where the edge belongs
	 * @param layout -not used yet-
	 * @param f factor for the representation
	 * @param p stream for the output
	 */
	private void exportEdge(Edge edge, Graph<Vertex,Edge> graph, Layout<Vertex,Edge> layout,
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
						Utils.ensureDecimal(lineWidth*f)
						+"]{->}{"+source+"}{"+(intSize+lineWidth)/2.0+"}");
			}else{			
				p.println("\\ncarc[linecolor="+scolor+", linewidth="+
						Utils.ensureDecimal(lineWidth*f)
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
						Utils.ensureDecimal(lineWidth*f)
						+"]{-}{"+first+"}{"+(intSize+lineWidth)/2.0+"}");
			}else{			
				p.println("\\ncarc[linecolor="+scolor+", linewidth="+
						Utils.ensureDecimal(lineWidth*f)
						+"]{-}{"+first+"}{"+second+"}");
			}
		}
	
	}

}
