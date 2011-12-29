/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.sg.cuttlefish;

import java.io.IOException;
import java.io.Writer;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.project.api.Workspace;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 * @author ptsankov
 */
public class CxfExport implements GraphExporter, CharacterExporter, LongTask {

    private boolean exportVisible = false;
    private Workspace workspace;
    private Writer writer;
    private Graph graph;
    
    private boolean exportNodeLabel = true;
    private boolean exportNodeColor = true;
    private boolean exportNodeSize = true;
    private boolean exportNodePosition = true;
    private boolean exportEdgeLabel = true;
    private boolean exportEdgeColor = true;
    private boolean exportEdgeWidth = true;
    private boolean exportEdgeWeight = true;
    private double nodeScaleFactor = 1;
    private double edgeScaleFactor = 1;
    private double coordinatesScaleFactor = 1;
    
    
    // required by the GraphExporter interface
    @Override
    public void setExportVisible(boolean exportVisible) {
        this.exportVisible = exportVisible;
    }

    // required by the GraphExporter interface
    @Override
    public boolean isExportVisible() {
        return exportVisible;
    }

    // required by the GraphExporter interface
    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    // required by the GraphExporter interface
    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
       
    // required by the CharacterExporter interface
    @Override
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    // required by the LongTask interface
    @Override
    public boolean cancel() {
        //hm, not sure what goes here
        return true;
    }

    // required by the LongTask interface
    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        //hm, not sure what goes here
    }
    
    private void setGraph() {
        GraphModel graphModel = workspace.getLookup().lookup(GraphModel.class);
        if (exportVisible) {
            graph = graphModel.getGraphVisible();
        } else {
            graph = graphModel.getGraph();
        }
    }
    
    // required by the GraphExporter interface
    @Override
    public boolean execute() {
        // do the job
        setGraph();
        try {
            // if the graph has at least one edge and an edge in the graph is undirected
            // then set the undirected flag in the cxf file.
            Edge someEdge = graph.getEdges().iterator().next();
            if(someEdge != null && graph.isDirected(someEdge)) {
                writer.write("configuration: undirected\n");
            }

            //export the nodes
            // node: (id) label{label} color{R,G,B} borderColor{R,G,B} size{size} shape{shape} width{width}
            //position{x,y} var1{var1} var2{var2} hide
            for(Node n : graph.getNodes()) {
                int id = n.getId();                
                writer.write("node: (" + id + ')');
                if(exportNodeLabel) {
                    String label = n.getNodeData().getLabel();
                    writer.write(" label{" + label + '}');
                }
                if(exportNodeColor) {
                    String color =  ""+ n.getNodeData().r() + ',' + n.getNodeData().g() + ',' + n.getNodeData().b();
                    writer.write(" color{" + color + "}");
                }
                if(exportNodeSize) {
                    double size = n.getNodeData().getSize();
                    writer.write(" size{" + size*nodeScaleFactor + '}');
                }
                if(exportNodePosition) {
                    double x = n.getNodeData().x();
                    double y = n.getNodeData().y();
                    writer.write(" position{" + x*coordinatesScaleFactor + ',' + y*coordinatesScaleFactor + '}');
                }
                writer.write('\n');
            }

            //export the edges
            //edge: (id_source, id_destination) label{label} weight{weight} 
            //width{width} color{R,G,B} var1{var1} var2{var2} hide
            for(Edge e : graph.getEdges()) {
                int id_source = e.getSource().getId();
                int id_dest = e.getTarget().getId();                ;                                           
                writer.write("edge: (" + id_source + "," + id_dest + ")");
                if(exportEdgeLabel) {
                    String label = e.getEdgeData().getLabel();
                    writer.write(" label{" + label + "}");
                }
                if(exportEdgeColor) {
                    String color = "" + ((float)getEdgeColor(e).getRed())/255f + ',' + ((float)getEdgeColor(e).getGreen())/255f
                        + ',' + ((float)getEdgeColor(e).getBlue())/255f;
                    writer.write(" color{" + color + "}");                    
                }
                if(exportEdgeWeight) {
                    double weight = e.getWeight();
                    writer.write(" weight{" + weight + "}");
                }
                if(exportEdgeWidth) {
                    double width = e.getWeight();
                    writer.write(" width{" + width*edgeScaleFactor + "}");
                }
                writer.write('\n');
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        //that's all
        return true;
    }

    public boolean exportEdgeColor() {
        return exportEdgeColor;
    }

    public void setExportEdgeColor(boolean exportEdgeColor) {
        this.exportEdgeColor = exportEdgeColor;
    }

    public boolean exportEdgeLabel() {
        return exportEdgeLabel;
    }

    public void setExportEdgeLabel(boolean exportEdgeLabel) {
        this.exportEdgeLabel = exportEdgeLabel;
    }

    public boolean exportEdgeWeight() {
        return exportEdgeWeight;
    }

    public void setExportEdgeWeight(boolean exportEdgeWeight) {
        this.exportEdgeWeight = exportEdgeWeight;
    }

    public boolean exportEdgeWidth() {
        return exportEdgeWidth;
    }

    public void setExportEdgeWidth(boolean exportEdgeWidth) {
        this.exportEdgeWidth = exportEdgeWidth;
    }

    public boolean exportNodeColor() {
        return exportNodeColor;
    }

    public void setExportNodeColor(boolean exportNodeColor) {
        this.exportNodeColor = exportNodeColor;
    }

    public boolean exportNodeLabel() {
        return exportNodeLabel;
    }

    public void setExportNodeLabel(boolean exportNodeLabel) {
        this.exportNodeLabel = exportNodeLabel;
    }

    public boolean exportNodePosition() {
        return exportNodePosition;
    }

    public void setExportNodePosition(boolean exportNodePosition) {
        this.exportNodePosition = exportNodePosition;
    }

    public boolean exportNodeSize() {
        return exportNodeSize;
    }

    public void setExportNodeSize(boolean exportNodeSize) {
        this.exportNodeSize = exportNodeSize;
    }

    public double getCoordinatesScaleFactor() {
        return coordinatesScaleFactor;
    }

    public void setCoordinatesScaleFactor(double coordinatesScaleFactor) {
        this.coordinatesScaleFactor = coordinatesScaleFactor;
    }

    public double getEdgeScaleFactor() {
        return edgeScaleFactor;
    }

    public void setEdgeScaleFactor(double edgeScaleFactor) {
        this.edgeScaleFactor = edgeScaleFactor;
    }

    public double getNodeScaleFactor() {
        return nodeScaleFactor;
    }

    public void setNodeScaleFactor(double nodeScaleFactor) {
        this.nodeScaleFactor = nodeScaleFactor;
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
