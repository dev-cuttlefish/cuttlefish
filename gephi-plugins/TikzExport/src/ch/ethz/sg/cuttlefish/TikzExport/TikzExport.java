/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.sg.cuttlefish.TikzExport;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.VectorExporter;
import org.gephi.project.api.Workspace;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 * @author Petar Tsankov (ptsankov@student.ethz.ch)
 */
public class TikzExport implements VectorExporter, CharacterExporter, LongTask {
    
    private Workspace workspace;
    private Writer writer;
    
    private Graph graph = null;
    private Map<Color, String> colors;
    private double coordinatesScalingFactor = 0.01;
    private double nodeScalingFactor = 0.5;
    private double edgeScalingFactor = 0.5;    
    private DecimalFormat formatter = null;
    private double maxY;    
    private String nodeStyle = "circle";
    private Point2D.Float center;
    private boolean hideEdgeLabels = false;
    private boolean hideNodeLabels = false;


    
    // Required by the VectorExporter interface
    // This is where the job is done
    @Override
    public boolean execute() {
        // initialize some things
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator('.');
        formatter = new DecimalFormat("###.#######", symbols);
        formatter.setGroupingUsed(false);
        
        // Do the job
        GraphModel graphModel = workspace.getLookup().lookup(GraphModel.class);
        graph = graphModel.getGraphVisible();
        
        try {
            maxY = Double.MIN_VALUE;
            for(Node n : graph.getNodes()) {
                double y = n.getNodeData().y();
                if (y > maxY) {
                    maxY = y;
                }
            }

            /*
            double k = java.lang.Double.MAX_VALUE;
            for (Node n1 : graph.getNodes()) {
                for (Node n2 : graph.getNodes()) {
                    if (n1.getId() != n2.getId()) {
                        double dist = (new Point2D.Float(n1.getNodeData().x(), n1.getNodeData().y())).distance(
                                new Point2D.Float(n2.getNodeData().x(), n2.getNodeData().y()));
                        double knew = 2 * dist /(n1.getNodeData().getSize() + n2.getNodeData().getSize());
                        if (knew < k) {
                            k = knew;
                        }
                    }
                }
            }
            */
            //nodeSizeFactor = 0.75 * k;

            writer.write("\\documentclass{minimal}\n");
            writer.write("\\usepackage{tikz, tkz-graph}\n");
            writer.write("\\usepackage[active,tightpage]{preview}\n");
            writer.write("\\PreviewEnvironment{tikzpicture}\n");
            writer.write("\\setlength\\PreviewBorder{5pt}\n");
            writer.write("\\begin{document}\n");

            //In pgf we need to define the colors outside the figure  before using them
            defineColors();

            writer.write("\\pgfdeclarelayer{background}\n");
            writer.write("\\pgfdeclarelayer{foreground}\n");
            writer.write("\\pgfsetlayers{background,main,foreground}\n");
            writer.write("\\begin{tikzpicture}\n");

            //Vertices will appear in the main layer while edges will be in the background
            for (Node n : graph.getNodes()) {
                exportNode(n);
            }

            writer.write("\\begin{pgfonlayer}{background}\n");
            //Arrow style for directed networks
            writer.write("\\tikzset{EdgeStyle/.style = {->,shorten >=1pt,>=stealth, bend right=10}}\n");

            exportEdges();

            writer.write("\\end{pgfonlayer}\n");
            writer.write("\\end{tikzpicture}\n");
            writer.write("\\end{document}\n");        
        } catch (IOException ex) {
            Logger.getLogger(TikzExport.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        return true;
    }
    
    /**
    * Private method that exports all edges and writes them to the
    * Tikz file.
    */
    private void exportEdges() throws IOException {
        ArrayList<Edge> edgeList = new ArrayList<Edge>();
        for(Edge e : graph.getEdges()) {
            edgeList.add(e);
        }
        if(edgeList.isEmpty()) return;
        Collections.sort(edgeList, new Comparator<Edge>() {
            @Override
            public int compare(Edge edge1, Edge edge2) {
                if( graph.isDirected(edge1) != graph.isDirected(edge2) ) {
                    if(graph.isDirected(edge1)) return -1;
                    else return 1;
                }
                if(getEdgeColor(edge1) != getEdgeColor(edge2)) {
                    return (int)(getEdgeColor(edge1).hashCode() - getEdgeColor(edge2).hashCode());
                }
                if(edge1.getWeight() != edge2.getWeight() ) {
                    if(edge1.getWeight() < edge2.getWeight() ) return -1;
                    else return 1;
                }
                return 0;
            }
        });
        boolean curEdgeType = false;
        Color curColor = null;
        double curWeight = java.lang.Double.MAX_VALUE;
        for(Edge e : edgeList) {
            boolean edgeType = graph.isDirected(e);
            Color color = getEdgeColor(e);
            double weight = e.getWeight();
            // If any of the edge properties is different from the current edge settings,
            // we need to redefine the edge settings
            if(edgeType != curEdgeType || !color.equals(curColor) || weight != curWeight) {
                curEdgeType = edgeType;
                curColor = color;
                curWeight = weight;
                writer.write("\\tikzset{EdgeStyle/.style = {");
                if(curEdgeType == true) writer.write("->, ");
                else writer.write("-, ");
                writer.write("shorten >=1pt, >=stealth, bend right=10, ");
                writer.write("line width=" + formatter.format(curWeight*edgeScalingFactor) );
                if(curColor != null)
                    writer.write(", color=" + colors.get(curColor) + "}}\n");
                else                    
                    writer.write("}}\n");
            }
            Node n1, n2;
            n1 = e.getSource();
            n2 = e.getTarget();            
            if (n1.getId() == n2.getId()) {
                exportLoopEdge(e, n1);
                continue;
            }
            writer.write("\\Edge ");
            if (e.getEdgeData().getLabel() != null) {
                writer.write("[label=" + escapeChars(e.getEdgeData().getLabel()) + "]");
            }
            writer.write("(" + n1.getId() + ")(" + n2.getId() + ")\n");
        }
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
    private void exportNode(Node n) throws IOException {
        // if the size is fixed we have to scale the coordinates
        Point2D.Float coordinates = new Point2D.Float(n.getNodeData().x(), n.getNodeData().y());

        writer.write("\\node at (" + formatter.format(n.getNodeData().x()*coordinatesScalingFactor)
            + "," + formatter.format((maxY - coordinates.getY())*coordinatesScalingFactor) + ") [");
        writer.write("circle,");
        float width = 1;
        writer.write(" line width=" + formatter.format(width) +  ",");

        //if ((vertex.getColor() != null) && (vertex.getWidth() > 0))
        //    p.print(" draw=" + colors.get(vertex.getColor()) + ",");
        writer.write(" fill=" + colors.get(new Color(n.getNodeData().r(),n.getNodeData().g(), n.getNodeData().b())) + ", ");
        writer.write(" inner sep=0pt,");
        writer.write(" minimum size = " + formatter.format((n.getNodeData().getSize())* nodeScalingFactor) + "pt,");

        if ((n.getNodeData().getLabel() != null) && !hideNodeLabels) {
            writer.write(" label={[label distance=0] 315:" + escapeChars(n.getNodeData().getLabel()) + "}");
        }
        if(nodeStyle.compareToIgnoreCase("ball") == 0) {
            writer.write(", shading=ball,");
            //if (vertex.getFillColor() != null) //The color reappears  in the shading
            writer.write(" ball color="+ colors.get(new Color(n.getNodeData().r(), n.getNodeData().g(), n.getNodeData().b()) ) );
            //else
            //    p.print(" ball color=black");
        }
        writer.write("] (" + n.getId() + ") {};\n");
    }
        
    
    private double calculateAngle(Node n) {
        if (center == null) {
            // center = Utils.caculateCenter(layout, network);
            center = new Point2D.Float(0,0);
        }
        if (n.getNodeData().x() > center.getX()) {
            if (n.getNodeData().y() < center.getY())
                return 45;
            else
                return 315;
        } else {
            if (n.getNodeData().y() < center.getY())
                return 135;
            else
                return 225;
        }
    }    
    
    
    /**
    * Private method that exports a loop edge
    * @param edge the loop edge
    * @param v1 the vertex that has the loop edge
    */
    private void exportLoopEdge(Edge edge, Node n) throws IOException {
        double angle = calculateAngle(n);
        if((angle > 124) && (angle < 226)) //two kinds of loops, in the left or right of the node
            writer.write("\\Loop[dist=1cm,dir=WE,");
        else
            writer.write("\\Loop[dist=1cm,dir=EA,");
        writer.write("style={->,shorten >=1pt,>=stealth,line width="+ formatter.format(edge.getWeight()*edgeScalingFactor));
        writer.write("}, color="+colors.get(new Color(edge.getEdgeData().r(), edge.getEdgeData().g(), edge.getEdgeData().b()) ));
        if ((edge.getEdgeData().getLabel() != null) && !hideEdgeLabels)
            writer.write(", label="+ edge.getEdgeData().getLabel());
        writer.write("](" + n.getId() + ")\n");
    }


    // Required by the VectorExporter interface
    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    // Required by the VectorExporter interface
    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    // Required by the CharacterExporter interface
    @Override
    public void setWriter(Writer writer) {
        this.writer = writer;
    }
    
    // Required by the LongTask interface
    @Override
    public boolean cancel() {
        try {
            System.out.append("Export to Tikz canceled");
            writer.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(TikzExport.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // Required by the LongTask interface
    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        // hm... what goes here?
    }
    
    /**
    * Private method that defines a color in the Tikz document.
    * @param color The color to be written to the Tikz document
    */
    private void writeColor(Color color) throws IOException {
        writer.write("\\definecolor{"+colors.get(color)+"}{rgb}{"
            +(color.getRed()/255.0)+","
            +(color.getGreen()/255.0)+","
            +(color.getBlue()/255.0)+"}\n");
    }
    
    /**
    * Private method that reads all used colors in the network
    * and defines them in the Tikz document.
    */
    private void defineColors() throws IOException {
        colors = new HashMap<Color, String>();
        for(Node n : graph.getNodes()) {
            Color color = new Color(n.getNodeData().r(), n.getNodeData().g(), n.getNodeData().b());
            if(!colors.containsKey(color)) {
                colors.put(color, "COLOR"+colors.size());
                writeColor(color);
            }
        }
        for( Edge e : graph.getEdges() ) {                        
            Color color = new Color(
                    e.getEdgeData().r() < 0 ? 0 : e.getEdgeData().r(), 
                    e.getEdgeData().g() < 0 ? 0 : e.getEdgeData().g(), 
                    e.getEdgeData().b() < 0 ? 0 : e.getEdgeData().b());
            if(!colors.containsKey(color) ) {
                colors.put(color, "COLOR"+colors.size());
                writeColor(color);
            }
        }
    }
    
        
    public boolean hideEdgeLabels() {
        return hideEdgeLabels;
    }
    
    public boolean hideNodeLabels() {
        return hideNodeLabels;
    }
    
    public String nodeStyle() {
        return nodeStyle;
    }
    
    public void setNodeStyle(String s) {
        nodeStyle = s;
    }
    
    public void setHideEdgeLabels(boolean b) {
        hideEdgeLabels = b;
    }
    
    public void setHideNodeLabels(boolean b) {
        hideNodeLabels = b;
    } 
    
    public void setCoordinatesScalingFactor(double d) {
        coordinatesScalingFactor = d;
    }
    
    public void setNodeScalingFactor(double d) {
        nodeScalingFactor = d;
    }
    
    public void setEdgeScalingFactor(double d) {
        edgeScalingFactor = d;
    }
    
    public double coordinatesScalingFactor() {
        return coordinatesScalingFactor;                
    }
    
    public double nodeScalingFactor() {
        return nodeScalingFactor;
    }
    
    public double edgeScalingFactor() {
        return edgeScalingFactor;
    }
    
    
    
    class Color extends java.awt.Color{    
        public Color(float r, float g, float b){            
            super((r >= 0 && r <= 1) ? r : 0,
                    (g >= 0 && g <= 1) ? g : 0,
                    (b >= 0 && b <= 1) ? b : 0);
        }
    }
    
    private Color getEdgeColor(Edge e) {
        return new Color((e.getEdgeData().r() >= 0 && e.getEdgeData().r() <= 1) ? e.getEdgeData().r() : e.getSource().getNodeData().r(),
                    (e.getEdgeData().g() >= 0 && e.getEdgeData().g() <= 1) ? e.getEdgeData().g() : e.getSource().getNodeData().g(),
                    (e.getEdgeData().b() >= 0 && e.getEdgeData().b() <= 1) ? e.getEdgeData().b() : e.getSource().getNodeData().b()
        );
    }

    
}
