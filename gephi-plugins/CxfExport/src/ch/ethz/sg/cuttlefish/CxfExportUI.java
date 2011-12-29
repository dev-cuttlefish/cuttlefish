/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.sg.cuttlefish;

import javax.swing.JPanel;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.exporter.spi.ExporterUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Petar Tsankov (ptsankov@student.ethz.ch)
 */
@ServiceProvider(service = ExporterUI.class)
public class CxfExportUI implements ExporterUI {

    private CxfExportJPanel panel;
    private CxfExport exporter;    
    private CxfExportSettings settings = new CxfExportSettings();
    
    @Override
    public JPanel getPanel() {
        panel = new CxfExportJPanel();
        return panel;
    }    
    
    @Override
    public void setup(Exporter exporter) {
        this.exporter= (CxfExport)exporter;
        settings.load(this.exporter);
        panel.setup(this.exporter);
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
    public boolean isUIForExporter(Exporter exporter) {
        return exporter instanceof CxfExport;
    }

    @Override
    public String getDisplayName() {
        return "Cxf Exporter Options";
    }

    private static class CxfExportSettings {
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
        
        private void save(CxfExport exporter) {
            //store the settings to the settings object
            exportNodeLabel = exporter.exportNodeLabel();
            exportNodeColor = exporter.exportNodeColor();
            exportNodeSize = exporter.exportNodeSize();
            exportNodePosition = exporter.exportNodePosition();
            exportEdgeLabel = exporter.exportEdgeLabel();
            exportEdgeColor = exporter.exportEdgeColor();
            exportEdgeWidth = exporter.exportEdgeWidth();
            exportEdgeWeight = exporter.exportEdgeWeight();
            nodeScaleFactor = exporter.getNodeScaleFactor();
            edgeScaleFactor = exporter.getEdgeScaleFactor();
            coordinatesScaleFactor = exporter.getCoordinatesScaleFactor();
        }
        private void load(CxfExport exporter) {
            //set the exporter object with the new settings
            exporter.setExportNodeLabel(exportNodeLabel);
            exporter.setExportNodeColor(exportNodeColor);
            exporter.setExportNodeSize(exportNodeSize);
            exporter.setExportNodePosition(exportNodePosition);
            exporter.setExportEdgeColor(exportEdgeColor);
            exporter.setExportEdgeLabel(exportEdgeLabel);
            exporter.setExportEdgeWeight(exportEdgeWeight);
            exporter.setExportEdgeWidth(exportEdgeWidth);
            exporter.setNodeScaleFactor(nodeScaleFactor);
            exporter.setEdgeScaleFactor(edgeScaleFactor);
            exporter.setCoordinatesScaleFactor(coordinatesScaleFactor);
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
        
        
    }
    
}
