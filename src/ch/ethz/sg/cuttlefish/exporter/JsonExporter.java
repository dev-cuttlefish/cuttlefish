package ch.ethz.sg.cuttlefish.exporter;

import java.io.IOException;
import java.io.Writer;

import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.project.api.Workspace;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class JsonExporter implements GraphExporter, CharacterExporter,
		NetworkExporter {

	private BrowsableNetwork network;
	private Workspace workspace;
	private Writer writer;

	@Override
	public boolean execute() {
		try {
			exportData();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public Workspace getWorkspace() {
		return workspace;
	}

	@Override
	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void setExportVisible(boolean exportVisible) {
		// TODO ilias: always exports visible graph
	}

	@Override
	public boolean isExportVisible() {
		return true;
	}

	@Override
	public void setNetwork(BrowsableNetwork network) {
		this.network = network;
	}

	private void exportData() throws IOException {
		writer.append("{ nodes: [").append("\n");
		boolean first = true;
		for (Vertex v : network.getVertices()) {
			if (!first) {
				writer.append(",").append("\n");
			} else {
				first = false;
			}
			writer.append("     {id: " + v.getId());
			writer.append(", size: " + v.getSize());
			writer.append(", width: " + v.getWidth());

			if (v.getLabel() != null)
				writer.append(", label: \"" + v.getLabel() + "\"");
			if (v.getFillColor() != null)
				writer.append(", color: \"{"
						+ ((double) v.getFillColor().getRed() / 256.d) + ","
						+ ((double) v.getFillColor().getGreen() / 256.d) + ","
						+ ((double) v.getFillColor().getBlue() / 256.d)
						+ "}\"}");
			else
				writer.append("}");

		}
		writer.append("     ], edges: [").append("\n");
		first = true;
		for (Edge e : network.getEdges()) {
			if (!first) {
				writer.append(",").append("\n");
			} else {
				first = false;
			}
			writer.append("     {id_origin: "
					+ (e.getSource() != null ? e.getSource().getId() : network
							.getEndpoints(e).getFirst().getId()));
			writer.append(", id_dest: "
					+ (e.getTarget() != null ? e.getTarget().getId() : network
							.getEndpoints(e).getSecond().getId()));
			writer.append(", label: " + e.getLabel());
			writer.append(", weight: " + e.getWeight());
			writer.append(", width: " + e.getWidth());
			if (e.getColor() != null)
				writer.append(", color: \"{"
						+ ((double) e.getColor().getRed() / 256.d) + ","
						+ ((double) e.getColor().getGreen() / 256.d) + ","
						+ ((double) e.getColor().getBlue() / 256.d) + "}\"}");
			else
				writer.append("} />");
		}
		writer.append("     ]}").append("\n");

	}

}
