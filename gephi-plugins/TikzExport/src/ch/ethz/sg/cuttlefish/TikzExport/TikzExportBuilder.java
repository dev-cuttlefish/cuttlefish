/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.sg.cuttlefish.TikzExport;

import org.gephi.io.exporter.api.FileType;
import org.gephi.io.exporter.spi.VectorExporter;
import org.gephi.io.exporter.spi.VectorFileExporterBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Petar Tsankov
 */
@ServiceProvider(service = VectorFileExporterBuilder.class)
public class TikzExportBuilder implements VectorFileExporterBuilder {

    @Override
    public FileType[] getFileTypes() {
        return new FileType[]{new FileType(".tex", "Tikz files")};
    }

    @Override
    public String getName() {
        return "TikzExporter";
    }

    @Override
    public VectorExporter buildExporter() {
        return new TikzExport();
    }
    
}
