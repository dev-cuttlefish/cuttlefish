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

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

/**
 * @author Ilias Rinis
 * 
 *         The GraphMLImporter class implements the importing of a network from
 *         the GraphML format. It has been updated to the latest schemas and
 *         specifications of GraphML. GraphMLImporter is compatible with the
 *         GraphMLExporter class of Cuttlefish, and also with the respective
 *         Gephi operation. It has been tested for interoperability until Gephi
 *         0.8.1 beta.
 */

public class GraphMLImporter {

	// GraphML Elements and Attributes
	private static final String GRAPHML = "graphml";
	private static final String KEY = "key";
	private static final String GRAPH = "graph";
	private static final String NODE = "node";
	private static final String EDGE = "edge";
	private static final String DATA = "data";
	private static final String ATTR_NAME = "attr.name";
	private static final String ATTR_TYPE = "attr.type";
	private static final String ATTR_FOR = "for";
	private static final String ATTR_ID = "id";
	private static final String NODE_ID = "id";
	private static final String NODE_LABEL = "label";
	private static final String NODE_X = "x";
	private static final String NODE_Y = "y";
	private static final String NODE_SIZE = "size";
	private static final String NODE_R = "r";
	private static final String NODE_G = "g";
	private static final String NODE_B = "b";
	private static final String EDGE_DIRECTED = "directed";
	private static final String EDGE_SRC = "source";
	private static final String EDGE_DEST = "target";
	private static final String EDGE_LABEL = "label";
	private static final String EDGE_WIDTH = "width";
	private static final String EDGE_WEIGHT = "weight";
	private static final String EDGE_R = "r";
	private static final String EDGE_G = "g";
	private static final String EDGE_B = "b";
	private static final String GRAPH_EDGEDEFAULT = "edgedefault";

	private File graphmlFile;
	private BrowsableNetwork network;

	public GraphMLImporter(File graphmlFile) {
		this.graphmlFile = graphmlFile;
	}

	public boolean importGraph(BrowsableNetwork network)
			throws FileNotFoundException {

		if (network != null) {
			this.network = network;
		} else {
			return false;
		}

		try {
			FileInputStream fis;
			fis = new FileInputStream(graphmlFile);
			XMLStreamReader xmlReader = XMLInputFactory.newInstance()
					.createXMLStreamReader(fis);

			while (xmlReader.hasNext()) {

				int event = xmlReader.next();
				if (event == XMLEvent.START_ELEMENT) {
					String name = xmlReader.getLocalName();

					if (name.equalsIgnoreCase(KEY)) {
						readKey(xmlReader);
					} else if (name.equalsIgnoreCase(GRAPH)) {
						readGraph(xmlReader);
					} else if (name.equalsIgnoreCase(NODE)) {
						readNode(xmlReader);
					} else if (name.equalsIgnoreCase(EDGE)) {
						readEdge(xmlReader);
					} else if (name.equalsIgnoreCase(GRAPHML)) {
						// Ignore <graphml> element
					}

				} else if (event == XMLEvent.END_ELEMENT) {
					// Ignore ending element </>
					// String name = xmlReader.getLocalName();
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void readKey(XMLStreamReader xmlReader) {
		/*
		 * Currently, the attributes defined in GraphML for data types are not
		 * useful.
		 */
		String attrName, attrType, attrFor, attrId;

		for (int i = 0; i < xmlReader.getAttributeCount(); ++i) {
			String attr = xmlReader.getAttributeLocalName(i);

			if (attr.equalsIgnoreCase(ATTR_NAME)) {
				attrName = xmlReader.getAttributeValue(i);
			} else if (attr.equalsIgnoreCase(ATTR_TYPE)) {
				attrType = xmlReader.getAttributeValue(i);
			} else if (attr.equalsIgnoreCase(ATTR_FOR)) {
				attrFor = xmlReader.getAttributeValue(i);
			} else if (attr.equalsIgnoreCase(ATTR_ID)) {
				attrId = xmlReader.getAttributeValue(i);
			}
		}
	}

	private void readGraph(XMLStreamReader xmlReader) {
		String edgeDefault = "";

		for (int i = 0; i < xmlReader.getAttributeCount(); ++i) {
			String attr = xmlReader.getAttributeLocalName(i);

			if (attr.equalsIgnoreCase(GRAPH_EDGEDEFAULT)) {
				edgeDefault = xmlReader.getAttributeValue(i);
			}
		}

		network.setDirected(edgeDefault.equalsIgnoreCase(EDGE_DIRECTED));
	}

	private void readNode(XMLStreamReader xmlReader) throws XMLStreamException {
		int attrId = 0;
		Vertex v = null;

		for (int i = 0; i < xmlReader.getAttributeCount(); ++i) {
			String attr = xmlReader.getAttributeLocalName(i);

			if (attr.equalsIgnoreCase(NODE_ID)) {
				attrId = Integer.parseInt(xmlReader.getAttributeValue(i));
			}
		}

		v = new Vertex(attrId);

		boolean elemEnd = false;

		while (xmlReader.hasNext() && !elemEnd) {
			int event = xmlReader.next();

			if (event == XMLEvent.START_ELEMENT) {
				if (xmlReader.getLocalName().equalsIgnoreCase(DATA)) {
					readNodeData(xmlReader, v);
				}

			} else if (event == XMLEvent.END_ELEMENT) {
				if (xmlReader.getLocalName().equalsIgnoreCase(NODE)) {
					elemEnd = true;
				}
			}
		}

		network.addVertex(v);
	}

	private void readNodeData(XMLStreamReader xmlReader, Vertex v)
			throws XMLStreamException {
		String key = "", value = "";

		for (int i = 0; i < xmlReader.getAttributeCount(); ++i) {
			String attr = xmlReader.getAttributeLocalName(i);

			if (attr.equalsIgnoreCase(KEY)) {
				key = xmlReader.getAttributeValue(i);
			}
		}

		value = xmlReader.getElementText();
		double x = v.getPosition() != null ? v.getPosition().getX() : 0;
		double y = v.getPosition() != null ? v.getPosition().getY() : 0;
		int r = v.getFillColor().getRed(), g = v.getFillColor().getGreen(), b = v
				.getFillColor().getBlue();

		if (!value.isEmpty()) {
			if (key.equalsIgnoreCase(NODE_LABEL)) {
				v.setLabel(value);
			} else if (key.equalsIgnoreCase(NODE_X)) {
				v.setPosition(Double.parseDouble(value), y);
			} else if (key.equalsIgnoreCase(NODE_Y)) {
				v.setPosition(x, Double.parseDouble(value));
			} else if (key.equalsIgnoreCase(NODE_SIZE)) {
				v.setSize(Double.parseDouble(value));
			} else if (key.equalsIgnoreCase(NODE_R)) {
				r = Integer.parseInt(value);
				v.setFillColor(new Color(r, g, b));
			} else if (key.equalsIgnoreCase(NODE_G)) {
				g = Integer.parseInt(value);
				v.setFillColor(new Color(r, g, b));
			} else if (key.equalsIgnoreCase(NODE_B)) {
				b = Integer.parseInt(value);
				v.setFillColor(new Color(r, g, b));
			}
		}
	}

	private void readEdge(XMLStreamReader xmlReader) throws XMLStreamException {
		int src = 0, dest = 0;
		boolean directed = false;
		boolean elemEnd = false;
		Edge e = null;

		for (int i = 0; i < xmlReader.getAttributeCount(); ++i) {
			String attr = xmlReader.getAttributeLocalName(i);

			if (attr.equalsIgnoreCase(EDGE_SRC)) {
				src = Integer.parseInt(xmlReader.getAttributeValue(i));
			} else if (attr.equalsIgnoreCase(EDGE_DEST)) {
				dest = Integer.parseInt(xmlReader.getAttributeValue(i));
			} else if (attr.equalsIgnoreCase(EDGE_DIRECTED)) {
				directed = Boolean.parseBoolean(xmlReader.getAttributeValue(i));
			}
		}

		Vertex vs = null, vt = null;

		for (Vertex v : network.getVertices()) {
			if (v.getId() == src)
				vs = v;
			if (v.getId() == dest)
				vt = v;

			if (vs != null && vt != null)
				break;
		}

		if (vs != null && vt != null) {
			e = new Edge(vs, vt, 1, directed);
		}

		while (xmlReader.hasNext() && !elemEnd) {
			int event = xmlReader.next();

			if (event == XMLEvent.START_ELEMENT) {
				if (xmlReader.getLocalName().equalsIgnoreCase(DATA)) {
					readEdgeData(xmlReader, e);
				}

			} else if (event == XMLEvent.END_ELEMENT) {
				if (xmlReader.getLocalName().equalsIgnoreCase(EDGE)) {
					elemEnd = true;
				}
			}
		}

		network.addEdge(e);
	}

	private void readEdgeData(XMLStreamReader xmlReader, Edge e)
			throws XMLStreamException {
		String key = "", value = "";

		for (int i = 0; i < xmlReader.getAttributeCount(); ++i) {
			String attr = xmlReader.getAttributeLocalName(i);

			if (attr.equalsIgnoreCase(KEY)) {
				key = xmlReader.getAttributeValue(i);
			}
		}

		value = xmlReader.getElementText();
		int r = e.getColor().getRed(), g = e.getColor().getGreen(), b = e
				.getColor().getBlue();

		if (!value.isEmpty()) {
			if (key.equalsIgnoreCase(EDGE_LABEL)) {
				e.setLabel(value);
			} else if (key.equalsIgnoreCase(EDGE_WIDTH)) {
				e.setWidth(Double.parseDouble(value));
			} else if (key.equalsIgnoreCase(EDGE_WEIGHT)) {
				e.setWeight(Double.parseDouble(value));
			} else if (key.equalsIgnoreCase(EDGE_R)) {
				r = Integer.parseInt(value);
				e.setColor(new Color(r, g, b));
			} else if (key.equalsIgnoreCase(EDGE_G)) {
				g = Integer.parseInt(value);
				e.setColor(new Color(r, g, b));
			} else if (key.equalsIgnoreCase(EDGE_B)) {
				b = Integer.parseInt(value);
				e.setColor(new Color(r, g, b));
			}
		}

	}
}
