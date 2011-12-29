/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.sg.cuttlefish.TikzExport;

import javax.swing.JPanel;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.exporter.spi.ExporterUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Petar Tsankov (ptsankov@student.ethz.ch)
 */
@ServiceProvider(service = ExporterUI.class)
public class TikzExportUI implements ExporterUI {
    
    private TikzExport exporter;
    private TikzExportSettings settings = new TikzExportSettings();
    private TikzExportJPanel panel;
 
    @Override
    public void setup(Exporter exporter) {
        this.exporter= (TikzExport)exporter;
        settings.load(this.exporter);
        panel.setup(this.exporter);
    }
 
    @Override
    public JPanel getPanel() {
        panel = new TikzExportJPanel();
        return panel;
    }
 
    @Override
    public void unsetup(boolean update) {
        if(update) {
            panel.unsetup(exporter);           
            settings.save(exporter);
        } else {
            //Cancel was hit
        }
        panel = null;
        exporter = null;
    }
 
    @Override
    public String getDisplayName() {
        return "Tikz Exporter";
    }
 
    @Override
    public boolean isUIForExporter(Exporter exporter) {
        return exporter instanceof TikzExport;
    }
    
    private static class TikzExportSettings {

        private boolean hideNodeLabels = false;
        private boolean hideEdgeLabels = false;
        private String nodeStyle = "circle";
        private double nodeScalingFactor = 0.5;
        private double edgeScalingFactor = 0.5;
        private double coordinatesScalingFactor = 0.01;

        private void save(TikzExport exporter) {
            hideNodeLabels = exporter.hideNodeLabels();
            hideEdgeLabels = exporter.hideEdgeLabels();
            nodeStyle = exporter.nodeStyle();            
            nodeScalingFactor = exporter.nodeScalingFactor();
            edgeScalingFactor = exporter.edgeScalingFactor();
            coordinatesScalingFactor = exporter.coordinatesScalingFactor();
        }

        private void load(TikzExport exporter) {
            exporter.setCoordinatesScalingFactor(coordinatesScalingFactor);
            exporter.setNodeScalingFactor(nodeScalingFactor);
            exporter.setEdgeScalingFactor(edgeScalingFactor);
            exporter.setHideEdgeLabels(hideEdgeLabels);
            exporter.setHideNodeLabels(hideNodeLabels);
            exporter.setNodeStyle(nodeStyle);
        }
        
    }
    
}