/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.sg.cuttlefish;

import org.gephi.io.exporter.api.FileType;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.io.exporter.spi.GraphFileExporterBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Petar Tsankov (ptsankov@student.ethz.ch)
 */
@ServiceProvider(service = GraphFileExporterBuilder.class)
public class CxfExportBuilder implements GraphFileExporterBuilder {

    @Override
    public GraphExporter buildExporter() {
        return new CxfExport();
    }

    @Override
    public FileType[] getFileTypes() {
        return new FileType[]{new FileType(".cxf", "Cuttlefish files")};
    }

    @Override
    public String getName() {
        return "Cxf Exporter";
    }
    
}
