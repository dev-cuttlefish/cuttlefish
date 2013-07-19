package ch.ethz.sg.cuttlefish.exporter;

import java.io.IOException;
import java.io.Writer;

import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.project.api.Workspace;

import ch.ethz.sg.cuttlefish.misc.Utils;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class CXFExporter implements GraphExporter, CharacterExporter,
		NetworkExporter {

	private BrowsableNetwork network;
	private Workspace workspace;
	private Writer writer;

	@Override
	public boolean execute() {
		try {
			exportData();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	@Override
	public void setWriter(Writer writer) {
		this.writer = writer;
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

		if (Utils.checkForDuplicatedVertexIds(network))
			Utils.reassignVertexIds(network);

		boolean hideVertexLabels = false;
		boolean hideEdgeLabels = false;

		if (network instanceof CxfNetwork) {
			// These two variables can only be defined if the network was
			// loaded from cxf
			hideVertexLabels = ((CxfNetwork) network).hideVertexLabels();
			hideEdgeLabels = ((CxfNetwork) network).hideEdgeLabels();
		}

		boolean isDirected = false;

		for (Edge edge : network.getEdges())
			isDirected = network.isDirected(edge);

		// Setting up the configuration line
		if ((!isDirected) || hideVertexLabels || hideEdgeLabels)
			writer.append("configuration:");

		if (!isDirected)
			writer.append(" undirected");
		if (hideVertexLabels)
			writer.append(" hide_node_labels");
		if (hideEdgeLabels)
			writer.append(" hide_edge_labels");

		if ((!isDirected) || hideVertexLabels || hideEdgeLabels)
			writer.append("\n");

		for (Vertex vertex : network.getVertices())
			printVertex(vertex);

		for (Edge edge : network.getEdges())
			printEdge(edge);

		// } catch (FileNotFoundException fnfEx) {
		// JOptionPane.showMessageDialog(null, fnfEx.getMessage(), "Error",
		// JOptionPane.ERROR_MESSAGE);
		// System.err.println("File not found while saving network");
		// fnfEx.printStackTrace();
		// }
	}

	/**
	 * private method to print a vertex in the file
	 * 
	 * @param vertex
	 */
	private void printVertex(Vertex vertex) throws IOException {
		writer.append("node: (" + vertex.getId() + ")");

		if (vertex.getLabel() != null)
			writer.append(" label{" + vertex.getLabel() + "}");

		if (vertex.getFillColor() != null)
			writer.append(" color{"
					+ ((double) (vertex.getFillColor()).getRed() / 256.d) + ","
					+ ((double) (vertex.getFillColor()).getGreen() / 256.d)
					+ ","
					+ ((double) (vertex.getFillColor()).getBlue() / 256.d)
					+ "}");

		if (vertex.getBorderColor() != null)
			writer.append(" borderColor{"
					+ ((double) (vertex.getBorderColor()).getRed() / 256.d) + ","
					+ ((double) (vertex.getBorderColor()).getGreen() / 256.d) + ","
					+ ((double) (vertex.getBorderColor()).getBlue() / 256.d) + "}");

		writer.append(" size{" + vertex.getSize() + "}");

		if (vertex != null) {
			writer.append(" shape{" + (vertex.getShape() + "}"));
		}

		double x = vertex.getPosition().getX(); // ((Point2D)layout.transform(vertex)).getX();
		double y = vertex.getPosition().getY(); // ((Point2D)layout.transform(vertex)).getY();
		writer.append(" position{" + x + "," + y + "}");

		if (vertex.getWidth() != 1)
			writer.append(" width{" + vertex.getWidth() + "}");

		if (vertex.getVar1() != null)
			writer.append(" var1{" + vertex.getVar1() + "}");

		if (vertex.getVar2() != null)
			writer.append(" var2{" + vertex.getVar2() + "}");

		if (vertex.isExcluded())
			writer.append(" hide");

		writer.append("\n");
	}

	/**
	 * private method to print an edge in the file
	 * 
	 * @param edge
	 */
	private void printEdge(Edge edge) throws IOException {

		writer.append("edge: (" + edge.getSource().getId() + ","
				+ edge.getTarget().getId() + ")");

		if (edge.getLabel() != null)
			writer.append(" label{" + edge.getLabel() + "}");

		writer.append(" weight{" + edge.getWeight() + "}");
		writer.append(" width{" + edge.getWidth() + "}");

		if (edge.getColor() != null)
			writer.append(" color{"
					+ ((double) edge.getColor().getRed() / 256.d) + ","
					+ ((double) edge.getColor().getGreen() / 256.d) + ","
					+ ((double) edge.getColor().getBlue() / 256.d) + "}");

		if (edge.getVar1() != null)
			writer.append(" var1{" + edge.getVar1() + "}");

		if (edge.getVar2() != null)
			writer.append(" var2{" + edge.getVar2() + "}");

		if (edge.isExcluded())
			writer.append(" hide");

		writer.append("\n");
	}

}
