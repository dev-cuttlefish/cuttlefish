package ch.ethz.sg.cuttlefish.exporter;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.spi.Exporter;
import org.openide.util.Lookup;

import ch.ethz.sg.cuttlefish.layout.LayoutLoader;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

public class NetworkExportController {

	private static final Set<String> gephiExporters;
	private static final Map<String, Class<? extends Exporter>> cuttlefishExporters;

	static {
		gephiExporters = new HashSet<String>();
		cuttlefishExporters = new HashMap<String, Class<? extends Exporter>>();

		gephiExporters.add("csv");
		gephiExporters.add("gexf");
		gephiExporters.add("graphml");
		gephiExporters.add("pajek");
		gephiExporters.add("pdf");
		gephiExporters.add("png");
		gephiExporters.add("svg");

		cuttlefishExporters.put("cxf", CXFExporter.class);
		cuttlefishExporters.put("tikz", TikzExporter.class);
		cuttlefishExporters.put("applet", AppletExporter.class);
		cuttlefishExporters.put("json", JsonExporter.class);
		cuttlefishExporters.put("jpg", JPEGExporter.class);
	}

	public static void export(BrowsableNetwork network, String fileName,
			Exporter exporter) throws Exception {

		File file = new File(fileName);
		if (!file.exists())
			file.createNewFile();

		export(network, file, exporter);
	}

	public static void export(BrowsableNetwork network, File file,
			Exporter exporter) throws Exception {

		ExportController ec = Lookup.getDefault()
				.lookup(ExportController.class);

		// Certain Gephi exporters need a normalized layout i.e., all vertices
		// are translated near the origin
		LayoutLoader.getInstance().normalizeLayout();

		if (!file.exists())
			file.createNewFile();

		if (exporter instanceof NetworkExporter)
			((NetworkExporter) exporter).setNetwork(network);

		ec.exportFile(file, exporter);
	}

	public static Collection<String> getExporterList() {
		Set<String> allExporters = new TreeSet<String>();
		allExporters.addAll(gephiExporters);
		allExporters.addAll(cuttlefishExporters.keySet());

		return allExporters;
	}

	public static Exporter getExporter(String exporterName) {
		Exporter exporter = null;

		if (gephiExporters.contains(exporterName)) {
			ExportController ec = Lookup.getDefault().lookup(
					ExportController.class);
			exporter = ec.getExporter(exporterName);

		} else {
			try {
				Class<? extends Exporter> exporterClass = cuttlefishExporters
						.get(exporterName);

				if (exporterClass != null)
					exporter = exporterClass.newInstance();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (exporter == null)
			throw new UnsupportedOperationException("Unknown export format: "
					+ exporterName);

		configureExporter(exporter);

		return exporter;
	}

	private static void configureExporter(Exporter exporter) {
		// Perform exporter specific configurations
	}
}
