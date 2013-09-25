package ch.ethz.sg.cuttlefish.exporter;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.project.api.Workspace;

import ch.ethz.sg.cuttlefish.misc.Pair;
import ch.ethz.sg.cuttlefish.misc.Utils;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class TikzExporter implements GraphExporter, CharacterExporter,
		NetworkExporter {

	private BrowsableNetwork network;
	private Workspace workspace;
	private Writer writer;

	private final double defaultCoordinateFactor = 1;
	private final double defaultNodeSizeFactor = 1;
	private final double defaultEdgeSizeFactor = 1;
	private double coordinateFactor;
	private double nodeSizeFactor;
	private double edgeSizeFactor;

	private double maxY = 0;
	private boolean hideVertexLabels = false;
	private boolean hideEdgeLabels = false;
	private Map<Color, String> colors;
	private DecimalFormat formatter;
	private boolean fixedSize = false;
	private double width = 0, height = 0;

	// alpha and beta scale the x and y coordinates of a node, s scales the node
	// size, edge width, etc.
	private double s = 1, alpha = 1, beta = 1,
			xmin = java.lang.Double.MAX_VALUE,
			xmax = java.lang.Double.MIN_VALUE,
			ymin = java.lang.Double.MAX_VALUE,
			ymax = java.lang.Double.MIN_VALUE;
	private String nodeStyle = "circle";
	private Point2D center = null;

	public TikzExporter() {
		colors = new HashMap<Color, String>();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(
				Locale.getDefault());
		symbols.setDecimalSeparator('.');
		formatter = new DecimalFormat("###.#######", symbols);
		formatter.setGroupingUsed(false);
		setScalingFactors(defaultNodeSizeFactor, defaultEdgeSizeFactor,
				defaultCoordinateFactor);
	}

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
		if (fixedSize) {
			computeScaleCoordinates();
		}

		if (Utils.checkForDuplicatedVertexIds(network))
			Utils.reassignVertexIds(network);

		// We find the maximum value of Y to revert the y coordinate when
		// writing the nodes in tex
		for (Vertex vertex : network.getVertices()) {
			double y = vertex.getPosition().getY();
			if (y > maxY)
				maxY = y;
		}

		double k = java.lang.Double.MAX_VALUE;
		// calculate min (2*dist / (size1+size2) as max node scale that avoids
		// node overlapping
		for (Vertex v1 : network.getVertices()) {
			for (Vertex v2 : network.getVertices()) {
				if (v2.getId() > v1.getId()) {
					double dist = v1.getPosition().distance(v2.getPosition());
					double knew = 2 * dist / (v1.getSize() + v2.getSize());
					if (knew < k)
						k = knew;
				}
			}
		}

		// TODO ilias: why this??
		// nodeSizeFactor = 0.75 * k;

		writer.append("\\documentclass{minimal}").append("\n");
		writer.append("\\usepackage{tikz, tkz-graph}").append("\n");
		writer.append("\\usepackage[active,tightpage]{preview}").append("\n");
		writer.append("\\PreviewEnvironment{tikzpicture}").append("\n");
		writer.append("\\setlength\\PreviewBorder{5pt}").append("\n");
		writer.append("\\begin{document}").append("\n");

		// In pgf we need to define the colors outside the figure before using
		// them
		defineColors();

		writer.append("\\pgfdeclarelayer{background}").append("\n");
		writer.append("\\pgfdeclarelayer{foreground}").append("\n");
		writer.append("\\pgfsetlayers{background,main,foreground}")
				.append("\n");
		writer.append("\\begin{tikzpicture}").append("\n");

		// Vertices will appear in the main layer while edges will be in the
		// background
		for (Vertex v : network.getVertices())
			exportVertex(v);

		writer.append("\\begin{pgfonlayer}{background}").append("\n");
		// Arrow style for directed networks
		writer.append(
				"\\tikzset{EdgeStyle/.style = {->,shorten >=1pt,>=stealth, bend right=10}}")
				.append("\n");

		exportEdges();

		writer.append("\\end{pgfonlayer}").append("\n");
		writer.append("\\end{tikzpicture}").append("\n");
		writer.append("\\end{document}").append("\n");

		// } catch (FileNotFoundException fnfEx) {
		// JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		// Cuttlefish.err.println("Error trying to save in Tikz");
		// fnfEx.printStackTrace();
		// }
	}

	private void computeScaleCoordinates() {
		// first find the min and max values used as coordinates
		for (Vertex v : network.getVertices()) {
			if (v.getPosition().getX() < xmin)
				xmin = v.getPosition().getX();
			if (v.getPosition().getX() > xmax)
				xmax = v.getPosition().getX();
			if (v.getPosition().getY() < ymin)
				ymin = v.getPosition().getY();
			if (v.getPosition().getY() > ymax)
				ymax = v.getPosition().getY();
		}
		// compute the scaling factors alpha and beta
		alpha = width / (xmax - xmin);
		beta = height / (ymax - ymin);
		// System.out.println("Computed scaling factors: " + alpha + ' ' +
		// beta);
	}

	/**
	 * Private method that reads all used colors in the network and defines them
	 * in the Tikz document.
	 * 
	 * @throws IOException
	 */
	private void defineColors() throws IOException {

		for (Vertex vertex : network.getVertices()) {
			Color fColor = (Color) vertex.getFillColor();
			if (fColor != null && !colors.containsKey(fColor)) {
				colors.put(fColor, "COLOR" + colors.size());
				writeColor(fColor);
			}
			Color cColor = (Color) vertex.getBorderColor();
			if (cColor != null && !colors.containsKey(cColor)) {
				colors.put(cColor, "COLOR" + colors.size());
				writeColor(cColor);
			}
		}
		for (Edge edge : network.getEdges()) {
			Color color = edge.getColor();
			if (color != null && !colors.containsKey(color)) {
				colors.put(color, "COLOR" + colors.size());
				writeColor(color);
			}
		}
	}

	/**
	 * Private method that defines a color in the Tikz document.
	 * 
	 * @param color
	 *            The color to be written to the Tikz document
	 * @throws IOException
	 */
	private void writeColor(Color color) throws IOException {
		writer.append(
				"\\definecolor{" + colors.get(color) + "}{rgb}{"
						+ (color.getRed() / 255.0) + ","
						+ (color.getGreen() / 255.0) + ","
						+ (color.getBlue() / 255.0) + "}").append("\n");
	}

	/**
	 * Prints the necessary information to display a vertex in the tikz output
	 * 
	 * @param vertex
	 * @throws IOException
	 */
	private void exportVertex(Vertex vertex) throws IOException {
		Point2D coordinates = null;
		// if the size is fixed we have to scale the coordinates
		if (fixedSize) {
			Point2D origCoordinates = vertex.getPosition();
			coordinates = new Point2D.Double(alpha
					* (origCoordinates.getX() - xmin), beta
					* (origCoordinates.getY() - ymin));
			s = Math.max(alpha, beta);
		} else {
			coordinates = vertex.getPosition();
			s = 1;
		}
		// TODO ilias: sometimes the coordinates are much bigger than tikz can
		// handle
		writer.append("\\node at ("
				+ formatter.format(coordinates.getX()
						* (coordinateFactor / 10.0))
				+ ","
				+ formatter.format((maxY - coordinates.getY())
						* (coordinateFactor / 10.0)) + ") [");
		if (vertex != null) {
			writer.append(vertex.getShape()).append(",");
		}
		writer.append(" line width=" + formatter.format(vertex.getWidth() * s)
				+ ",");

		if ((vertex.getBorderColor() != null) && (vertex.getWidth() > 0))
			writer.append(" draw=" + colors.get(vertex.getBorderColor()) + ",");
		if (vertex.getFillColor() != null)
			writer.append(" fill=" + colors.get(vertex.getFillColor()) + ",");
		writer.append(" inner sep=0pt,");
		writer.append(" minimum size = "
				+ formatter.format((vertex.getSize()) * nodeSizeFactor * s)
				+ "pt,");

		if ((vertex.getLabel() != null) && (!hideVertexLabels)) {
			writer.append(" label={[label distance=0]" +
			// calculateAngle(vertex)
					"315" + ":" + escapeChars(vertex.getLabel()) + "}");
		}
		if (nodeStyle.compareToIgnoreCase("ball") == 0) {
			writer.append(", shading=ball,");
			if (vertex.getFillColor() != null) // The color reappears in the
												// shading
				writer.append(" ball color="
						+ colors.get(vertex.getFillColor()));
			else
				writer.append(" ball color=black");
		}

		writer.append("] (" + vertex.getId() + ") {};\n");
	}

	private String escapeChars(String s) {
		String[] chars = { "&", "_", "%" };
		for (String ch : chars) {
			s = s.replace(ch, "\\" + ch);
		}
		return s;
	}

	/**
	 * Private method that exports all edges and writes them to the Tikz file.
	 * 
	 * @throws IOException
	 */
	private void exportEdges() throws IOException {
		if (fixedSize) {
			s = Math.max(alpha, beta);
		} else {
			s = 1;
		}
		ArrayList<Edge> edgeList = new ArrayList<Edge>(network.getEdges());
		if (edgeList.size() == 0)
			return;
		Collections.sort(edgeList, new Comparator<Edge>() {
			@Override
			public int compare(Edge edge1, Edge edge2) {
				if (network.isDirected(edge1) != network.isDirected(edge2)) {
					if (network.isDirected(edge1))
						return -1;
					else
						return 1;
				}
				if (edge1.getColor() != edge2.getColor()) {
					return edge1.getColor().hashCode()
							- edge2.getColor().hashCode();
				}
				if (edge1.getWidth() != edge2.getWidth()) {
					if (edge1.getWidth() < edge2.getWidth())
						return -1;
					else
						return 1;
				}
				return 0;
			}
		});

		Edge curEdge = null;
		Color curColor = null;
		double curWidth = java.lang.Double.MAX_VALUE;
		for (Edge edge : edgeList) {
			Color color = edge.getColor();
			double width = edge.getWidth();
			// If any of the edge properties is different from the current edge
			// settings,
			// we need to redefine the edge settings
			if (curEdge == null
					|| (curEdge != null && network.isDirected(curEdge) != network
							.isDirected(edge)) || !color.equals(curColor)
					|| width != curWidth) {
				curEdge = edge;
				curColor = color;
				curWidth = width;
				writer.append("\\tikzset{EdgeStyle/.style = {");
				if (network.isDirected(curEdge))
					writer.append("->, ");
				else
					writer.append("-, ");
				writer.append("shorten >=1pt, >=stealth, bend right=10, ");
				writer.append("line width="
						+ formatter.format(curWidth * edgeSizeFactor * s));
				if (curColor != null)
					writer.append(", color=" + colors.get(curColor) + "}}")
							.append("\n");
				else
					writer.append("}}").append("\n");
			}
			Vertex v1, v2;
			if (network.isDirected(edge)) {
				v1 = network.getSource(edge);
				v2 = network.getDest(edge);
			} else {
				Pair<Vertex> endpoints = network.getEndpoints(edge);
				v1 = endpoints.getFirst();
				v2 = endpoints.getSecond();
			}
			if (v1.getId() == v2.getId()) {
				exportLoopEdge(edge, v1);
				continue;
			}
			writer.append("\\Edge ");
			if ((edge.getLabel() != null) && (!hideEdgeLabels))
				writer.append("[label=" + escapeChars(edge.getLabel()) + "]");
			writer.append("(" + v1.getId() + ")(" + v2.getId() + ")\n");
		}
	}

	/**
	 * Private method that exports a loop edge
	 * 
	 * @param edge
	 *            the loop edge
	 * @param v1
	 *            the vertex that has the loop edge
	 * @throws IOException
	 */
	private void exportLoopEdge(Edge edge, Vertex v) throws IOException {
		double angle = calculateAngle(v);

		if ((angle > 124) && (angle < 226)) // two kinds of loops, in the left
											// or right of the node
			writer.append("\\Loop[dist=1cm,dir=WE,");
		else
			writer.append("\\Loop[dist=1cm,dir=EA,");
		writer.append("style={->,shorten >=1pt,>=stealth,line width="
				+ formatter.format(edge.getWidth() * edgeSizeFactor));
		writer.append("}, color=" + colors.get(edge.getColor()));
		if ((edge.getLabel() != null) && (!hideEdgeLabels))
			writer.append(", label=" + edge.getLabel());

		writer.append("](" + v.getId() + ")\n");
	}

	/**
	 * Private method that calculates the angle for the vertex label according
	 * to the four quadrant rule that paints the labels in the tex file in a way
	 * that they are on the other side of the center of the network.
	 * 
	 * @param v
	 *            vertex that is going to be painted
	 * @return
	 */
	private double calculateAngle(Vertex v) {
		if (center == null)
			center = Utils.caculateCenter(network);
		Point2D vPos = v.getPosition();
		if (vPos.getX() > center.getX()) {
			if (vPos.getY() < center.getY())
				return 45;
			else
				return 315;
		} else {
			if (vPos.getY() < center.getY())
				return 135;
			else
				return 225;
		}
	}

	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public double getCoordinatesFactor() {
		return coordinateFactor;
	}

	public double getNodeSizeFactor() {
		return nodeSizeFactor;
	}

	public double getEdgeSizeFactor() {
		return edgeSizeFactor;
	}

	public void setNodeStyle(String style) {
		this.nodeStyle = style;
	}

	public void setFixedSize(boolean fixedSize) {
		this.fixedSize = fixedSize;
	}

	public void setScalingFactors(double node, double edge, double coord) {
		this.nodeSizeFactor = node;
		this.edgeSizeFactor = edge;
		this.coordinateFactor = coord;
	}

	public void setDefaultFactors() {
		setScalingFactors(defaultNodeSizeFactor, defaultEdgeSizeFactor,
				defaultCoordinateFactor);
		this.nodeSizeFactor = defaultNodeSizeFactor;
		this.edgeSizeFactor = defaultEdgeSizeFactor;
		this.coordinateFactor = defaultCoordinateFactor;
	}

}
