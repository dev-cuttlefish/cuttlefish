package ch.ethz.sg.cuttlefish.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLWriter;

public class GraphmlExport {
	
	private BrowsableNetwork network;
	private Layout<Vertex, Edge> layout;
	
	public GraphmlExport(BrowsableNetwork n, Layout<Vertex, Edge> l) {
		this.network = n;
		this.layout = l;
	}
	
	public void export(File file) {
		GraphMLWriter<Vertex, Edge> out = new GraphMLWriter<Vertex, Edge>();	 
	 	 
        Map<String, GraphMLMetadata<Vertex> > vertexAttributes = new HashMap<String, GraphMLMetadata<Vertex>>();	 
        Transformer<Vertex, String> vertexRedColorTransformer = new Transformer<Vertex, String>() {	 
                @Override	 
                public String transform(Vertex v) { return Integer.toString(v.getFillColor().getRed()); }	 
        };	 
        Transformer<Vertex, String> vertexGreenColorTransformer = new Transformer<Vertex, String>() {	 
                @Override	 
                public String transform(Vertex v) { return Integer.toString(v.getFillColor().getGreen()); }	 
        };	 
        Transformer<Vertex, String> vertexBlueColorTransformer = new Transformer<Vertex, String>() {	 
                @Override	 
                public String transform(Vertex v) { return Integer.toString(v.getFillColor().getBlue()); }	 
        };	 
        Transformer<Vertex, String> vertexLabelTransformer = new Transformer<Vertex, String>() {	 
                @Override	 
                public String transform(Vertex v) { return v.getLabel(); }	 
        };	 
        Transformer<Vertex, String> vertexXTransformer = new Transformer<Vertex, String>() {	 
                @Override	 
                public String transform(Vertex v) { return Double.toString(layout.transform(v).getX()); }	 
        };	 
        Transformer<Vertex, String> vertexYTransformer = new Transformer<Vertex, String>() {	 
                @Override	 
                public String transform(Vertex v) { return Double.toString(layout.transform(v).getY()); }	 
        };	 
        Transformer<Vertex, String> vertexSizeTransformer = new Transformer<Vertex, String>() {	 
                @Override	 
                public String transform(Vertex v) { return Double.toString(v.getSize()); }	 
        };	 
        GraphMLMetadata<Vertex> vertexRColor = new GraphMLMetadata<Vertex>("red", "0", vertexRedColorTransformer);	 
        GraphMLMetadata<Vertex> vertexGColor = new GraphMLMetadata<Vertex>("green", "0", vertexGreenColorTransformer);	 
        GraphMLMetadata<Vertex> vertexBColor = new GraphMLMetadata<Vertex>("blue", "0", vertexBlueColorTransformer);	 
        GraphMLMetadata<Vertex> vertexLabel = new GraphMLMetadata<Vertex>("label", "", vertexLabelTransformer);	 
        GraphMLMetadata<Vertex> vertexX = new GraphMLMetadata<Vertex>("xPos", "0", vertexXTransformer);	 
        GraphMLMetadata<Vertex> vertexY = new GraphMLMetadata<Vertex>("yPos", "0", vertexYTransformer);	 
        GraphMLMetadata<Vertex> vertexSize = new GraphMLMetadata<Vertex>("size", "10", vertexSizeTransformer);	 
        vertexAttributes.put("r", vertexRColor);	 
        vertexAttributes.put("g", vertexGColor);	 
        vertexAttributes.put("b", vertexBColor);	 
        vertexAttributes.put("label", vertexLabel);	 
        vertexAttributes.put("x", vertexX);	 
        vertexAttributes.put("y", vertexY);	 
        vertexAttributes.put("size", vertexSize);	 
        out.setVertexData(vertexAttributes);	 

        Map<String, GraphMLMetadata<Edge> > edgeAttributes = new HashMap<String, GraphMLMetadata<Edge>>();	 
        Transformer<Edge, String> edgeRedColorTransformer = new Transformer<Edge, String>() {	 
                @Override	 
                public String transform(Edge e) { return Integer.toString(e.getColor().getRed()); }	 
        };	 
        Transformer<Edge, String> edgeGreenColorTransformer = new Transformer<Edge, String>() {	 
                @Override	 
                public String transform(Edge e) { return Integer.toString(e.getColor().getGreen()); }	 
        };	 
        Transformer<Edge, String> edgeBlueColorTransformer = new Transformer<Edge, String>() {	 
                @Override	 
                public String transform(Edge e) { return Integer.toString(e.getColor().getBlue()); }	 
        };	 
        Transformer<Edge, String> edgeLabelTransformer = new Transformer<Edge, String>() {	 
                @Override	 
                public String transform(Edge e) { return e.getLabel(); }	 
        };	 
        Transformer<Edge, String> edgeWeightTransformer = new Transformer<Edge, String>() {	 
                @Override	 
                public String transform(Edge e) { return Double.toString(e.getWeight()); }	 
        };	 
        Transformer<Edge, String> edgeWidthTransformer = new Transformer<Edge, String>() {	 
                @Override	 
                public String transform(Edge e) { return Double.toString(e.getWidth()); }	 
        };	 
        GraphMLMetadata<Edge> edgeRColor = new GraphMLMetadata<Edge>("red", "0", edgeRedColorTransformer);	 
        GraphMLMetadata<Edge> edgeGColor = new GraphMLMetadata<Edge>("green", "0", edgeGreenColorTransformer);	 
        GraphMLMetadata<Edge> edgeBColor = new GraphMLMetadata<Edge>("blue", "0", edgeBlueColorTransformer);	 
        GraphMLMetadata<Edge> edgeLabel = new GraphMLMetadata<Edge>("label", "", edgeLabelTransformer);	 
        GraphMLMetadata<Edge> edgeWeight = new GraphMLMetadata<Edge>("weight", "0", edgeWeightTransformer);	 
        GraphMLMetadata<Edge> edgeWidth = new GraphMLMetadata<Edge>("width", "0", edgeWidthTransformer);	 
        edgeAttributes.put("r", edgeRColor);	 
        edgeAttributes.put("g", edgeGColor);	 
        edgeAttributes.put("b", edgeBColor);	 
        edgeAttributes.put("label", edgeLabel);	 
        edgeAttributes.put("weight", edgeWeight);	 
        edgeAttributes.put("width", edgeWidth);	 
        out.setEdgeData(edgeAttributes);
        
        try {
			out.save(network, new FileWriter(file));
		} catch (IOException e1) {
			System.out.println("Could not export to graphml file");
			e1.printStackTrace();
		}
	}
}
