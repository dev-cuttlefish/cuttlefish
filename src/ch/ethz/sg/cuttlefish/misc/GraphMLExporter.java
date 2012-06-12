/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra, 
    Ilias Rinis

	This file is part of Cuttlefish.
	
 	Cuttlefish is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
 */

package ch.ethz.sg.cuttlefish.misc;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * @author Ilias Rinis
 * 
 *         The GraphMLExporter class implements exporting a network to the
 *         GraphML format. The class has been update to comply with the latest
 *         GraphML schemas and specifications; however, the GraphML structure
 *         used is still simple.
 */

public class GraphMLExporter {

	private BrowsableNetwork network;
	private Layout<Vertex, Edge> layout;

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private Document doc;

	public GraphMLExporter(BrowsableNetwork network, Layout<Vertex, Edge> layout) {
		this.network = network;
		this.layout = layout;
	}

	public void export(File file) {
		try {
			// Create the GraphML Document
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();

			// Export the data
			exportData();

			// Save data as GraphML file
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(source, result);

		} catch (TransformerException te) {
			te.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
	}

	private void exportData() {
		Element root = doc.createElementNS(
				"http://graphml.graphdrawing.org/xmlns", "graphml");
		doc.appendChild(root);

		createKeys(root);

		createGraphElement(root);
	}

	private void createKeys(Element root) {

		// Node Attributes
		// label(string), x(double), y(double), size(double), r(int), g(int),
		// b(int)
		Element nodeKeyLabel = createKeyElement("label", "string", "node");
		root.appendChild(nodeKeyLabel);

		Element nodeKeyX = createKeyElement("x", "double", "node");
		root.appendChild(nodeKeyX);

		Element nodeKeyY = createKeyElement("y", "double", "node");
		root.appendChild(nodeKeyY);

		Element nodeKeySize = createKeyElement("size", "double", "node");
		root.appendChild(nodeKeySize);

		Element nodeKeyColorR = createKeyElement("r", "int", "node");
		root.appendChild(nodeKeyColorR);

		Element nodeKeyColorG = createKeyElement("g", "int", "node");
		root.appendChild(nodeKeyColorG);

		Element nodeKeyColorB = createKeyElement("b", "int", "node");
		root.appendChild(nodeKeyColorB);

		// Edge Attributes
		// label(string), weight(double), id(string), width(double), r(int),
		// g(int), b(int)
		Element edgeKeyLabel = createKeyElement("label", "string", "edge");
		root.appendChild(edgeKeyLabel);

		Element edgeKeyWeight = createKeyElement("weight", "double", "edge");
		root.appendChild(edgeKeyWeight);

		Element edgeKeyID = createKeyElement("id", "string", "edge");
		root.appendChild(edgeKeyID);

		Element edgeKeyWidth = createKeyElement("width", "double", "edge");
		root.appendChild(edgeKeyWidth);

		Element edgeKeyColorR = createKeyElement("r", "int", "edge");
		root.appendChild(edgeKeyColorR);

		Element edgeKeyColorG = createKeyElement("g", "int", "edge");
		root.appendChild(edgeKeyColorG);

		Element edgeKeyColorB = createKeyElement("b", "int", "edge");
		root.appendChild(edgeKeyColorB);
	}

	private Element createKeyElement(String id, String type, String attrFor) {
		Element el = doc.createElement("key");
		el.setAttribute("id", id);
		el.setAttribute("attr.name", id);
		el.setAttribute("attr.type", type);
		el.setAttribute("for", attrFor);

		return el;
	}

	private Element createGraphElement(Element root) {
		Element graphEl = doc.createElement("graph");
		root.appendChild(graphEl);

		if (network.getEdgeCount(EdgeType.DIRECTED) > network
				.getEdgeCount(EdgeType.UNDIRECTED)) {
			graphEl.setAttribute("edgedefault", "directed");
		} else {
			graphEl.setAttribute("edgedefault", "undirected");
		}

		// Create nodes
		createNodes(graphEl);

		// Create edges
		createEdges(graphEl);

		return graphEl;
	}

	private void createNodes(Element graphEl) {
		Element dataEl;

		for (Vertex v : layout.getGraph().getVertices()) {
			Element nodeEl = doc.createElement("node");
			graphEl.appendChild(nodeEl);
			nodeEl.setAttribute("id", v.getId() + "");

			dataEl = doc.createElement("data");
			nodeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "label");
			dataEl.setTextContent(v.getLabel());

			dataEl = doc.createElement("data");
			nodeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "x");
			dataEl.setTextContent(Double.toString(v.getPosition().getX()));

			dataEl = doc.createElement("data");
			nodeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "y");
			dataEl.setTextContent(Double.toString(v.getPosition().getY()));

			dataEl = doc.createElement("data");
			nodeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "size");
			dataEl.setTextContent(Double.toString(v.getSize()));

			dataEl = doc.createElement("data");
			nodeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "r");
			dataEl.setTextContent(Integer.toString(v.getFillColor().getRed()));

			dataEl = doc.createElement("data");
			nodeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "g");
			dataEl.setTextContent(Integer.toString(v.getFillColor().getGreen()));

			dataEl = doc.createElement("data");
			nodeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "b");
			dataEl.setTextContent(Integer.toString(v.getFillColor().getBlue()));
		}
	}

	private void createEdges(Element graphEl) {
		Element dataEl;
		boolean includeWeight = false;

		/*
		 * If there are non-zero edge weights, include the weight attribute,
		 * otherwise skip.
		 */
		for (Edge e : layout.getGraph().getEdges()) {
			includeWeight = (e.getWeight() != 0);
			if (includeWeight)
				break;
		}

		for (Edge e : layout.getGraph().getEdges()) {
			Element edgeEl = doc.createElement("edge");
			graphEl.appendChild(edgeEl);
			Vertex src, dest;
			boolean directed = false;

			if (network.getEdgeType(e) == EdgeType.DIRECTED) {
				src = network.getSource(e);
				dest = network.getDest(e);
				directed = true;
			} else {
				src = network.getEndpoints(e).getFirst();
				dest = network.getEndpoints(e).getSecond();
			}

			edgeEl.setAttribute("directed", Boolean.toString(directed));
			edgeEl.setAttribute("source", Integer.toString(src.getId()));
			edgeEl.setAttribute("target", Integer.toString(dest.getId()));

			dataEl = doc.createElement("data");
			edgeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "label");
			dataEl.setTextContent(e.getLabel());

			if (includeWeight) {
				dataEl = doc.createElement("data");
				edgeEl.appendChild(dataEl);
				dataEl.setAttribute("key", "weight");
				dataEl.setTextContent(Double.toString(e.getWeight()));
			}

			// Not supported in cuttlefish
			// dataEl = doc.createElement("data");
			// edgeEl.appendChild(dataEl);
			// dataEl.setAttribute("key", "id");
			// dataEl.setTextContent(Integer.toString(e.getId()));

			dataEl = doc.createElement("data");
			edgeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "width");
			dataEl.setTextContent(Double.toString(e.getWidth()));

			dataEl = doc.createElement("data");
			edgeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "r");
			dataEl.setTextContent(Integer.toString(e.getColor().getRed()));

			dataEl = doc.createElement("data");
			edgeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "g");
			dataEl.setTextContent(Integer.toString(e.getColor().getGreen()));

			dataEl = doc.createElement("data");
			edgeEl.appendChild(dataEl);
			dataEl.setAttribute("key", "b");
			dataEl.setTextContent(Integer.toString(e.getColor().getBlue()));
		}
	}

}
