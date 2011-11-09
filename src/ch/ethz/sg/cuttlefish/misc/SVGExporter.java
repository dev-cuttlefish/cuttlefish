package ch.ethz.sg.cuttlefish.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;

public class SVGExporter {

	private BrowsableNetwork network;
	private Layout< Vertex, Edge> layout;
	
	
	public SVGExporter(BrowsableNetwork network, Layout<Vertex,Edge> layout) {
		this.network = network;
		this.layout = layout;
	}
	
	public void toSVG(File file, int height, int width) {
		try {
			PrintStream p = new PrintStream(file);
			p.println("<?xml version='1.0'?>");
			p.println("<html xmlns='http://www.w3.org/1999/xhtml' xmlns:svg='http://www.w3.org/2000/svg' xmlns:xul='http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul' xmlns:xlink='http://www.w3.org/1999/xlink'>");
			p.println("<head>");
			p.println("<script>");
			p.println("<![CDATA[var dx,dy;\n  var nodes;\n  var labels;\n  var edges;\n  var selectedNode;");
			p.println("  function init() {");
			p.println("    labels = new Array();");
			Map<Vertex, Integer> index = new HashMap<Vertex, Integer>();
			int i = 0;
			for(Vertex n : network.getVertices()) {
				index.put(n, i++);
			}
			for(Vertex n : network.getVertices()) {
				p.println("    labels[" + index.get(n) + "] = document.getElementById('label" + index.get(n) + "');");
			}
			p.println("    edges = new Array();");
			for(Vertex n : network.getVertices() ) {
				p.println("    edges["+ index.get(n) +"] = new Array();");
			}
			for(Vertex n : network.getVertices() ) {
				int count = 0;
				for(Edge e : network.getIncidentEdges(n)) {
					int sourceId, destId;
					// is the network directed?
					if(network.getSource(e) != null) {
						sourceId = index.get(network.getSource(e));
						destId = index.get(network.getDest(e));
					} else {
						sourceId = index.get(network.getEndpoints(e).getFirst());
						destId = index.get(network.getEndpoints(e).getSecond());
					}
					String edgeName = "edge" + (sourceId < destId? sourceId: destId) + (sourceId > destId? sourceId: destId);
					p.println("    edges[" + index.get(n) + "][" + count + "] = document.getElementById('" + edgeName + "');");
					count++;
					
				}
			}
			p.println("    nodes = new Array();");
			for(Vertex n : network.getVertices() ) {
				p.println("    nodes["+ index.get(n) +"] = document.getElementById('" + index.get(n) + "');");
			}
			for(Vertex n : network.getVertices() ) {
				p.println("    nodes[" + index.get(n) + "].addEventListener('mousedown', mousedown_listener, false);");
			}
			p.println("  }");
			p.println("  function mousedown_listener(evt)");
			p.println("  {");
			p.println("    selectedNode = evt.target;");
			p.println("    dx = selectedNode.cx.baseVal.value - evt.clientX;");
			p.println("    dy = selectedNode.cy.baseVal.value - evt.clientY;");
			p.println("    document.addEventListener('mousemove', mousemove_listener, true);");
			p.println("    document.addEventListener('mouseup', mouseup_listener, true);");
			p.println("  }");

			p.println("  function mouseup_listener(evt)");
			p.println("  {");
			p.println("    document.removeEventListener('mousemove', mousemove_listener, true);");
			p.println("    document.removeEventListener('mouseup', mouseup_listener, true);");
			p.println("  }");

			p.println("  function mousemove_listener(evt)");
			p.println("  {");
			p.println("    var id = selectedNode.ownerSVGElement.suspendRedraw(1000);");

			p.println("    for(i in edges[selectedNode.id]) {");
			p.println("      var edge = edges[selectedNode.id][i];");
			p.println("      if(edge.x1.baseVal.value == selectedNode.cx.baseVal.value");
			p.println("          && edge.y1.baseVal.value == selectedNode.cy.baseVal.value ) {");
			p.println("        edge.x1.baseVal.value = evt.clientX + dx;");
			p.println("        edge.y1.baseVal.value = evt.clientY + dy;");
			p.println("      } else {");
			p.println("        edge.x2.baseVal.value = evt.clientX + dx;");
			p.println("        edge.y2.baseVal.value = evt.clientY + dy;");
			p.println("      }");
			p.println("    }");
			p.println("    selectedNode.cx.baseVal.value = evt.clientX + dx;");
			p.println("    selectedNode.cy.baseVal.value = evt.clientY + dy;");   
			p.println("    labels[selectedNode.id].setAttribute('x', selectedNode.cx.baseVal.value + selectedNode.r.baseVal.value + 2);");
			p.println("    labels[selectedNode.id].setAttribute('y', selectedNode.cy.baseVal.value + selectedNode.r.baseVal.value + 2);");

			p.println("    selectedNode.ownerSVGElement.unsuspendRedraw(id);");
			p.println("  }");
			p.println("  ]]>");
			p.println("</script>");
			p.println("</head>");
			p.println("<body onload='init();'>");
			p.println("<svg:svg width='"+ width +"px' height='" + height + "px'>");
			p.println("  <svg:polyline points='0,0 " + width + ",0 " + width + "," + height + " 0," + height + "' style='stroke:black; fill:none;'/>");
			for(Edge e : network.getEdges() ) {
				Vertex sourceNode, destNode;
				// is the network directed?
				if(network.getSource(e) != null) {
					sourceNode = network.getSource(e);
					destNode = network.getDest(e);
				} else {
					sourceNode = network.getEndpoints(e).getFirst();
					destNode = network.getEndpoints(e).getSecond();
				}
				int sourceId = index.get(sourceNode);
				int destId = index.get(destNode);
				String edgeName = "edge" + (sourceId < destId? sourceId: destId) + (sourceId > destId? sourceId: destId);
				double x1, y1, x2, y2;
				x1 = (int)layout.transform(sourceNode).getX();
				y1 = (int)layout.transform(sourceNode).getY();
				x2 = (int)layout.transform(destNode).getX();
				y2 = (int)layout.transform(destNode).getY();
				p.println("  <svg:line id='" + edgeName + "' x1='" + x1 + "' y1='" + y1 + "' x2='" + x2 + "' y2='" + y2 + "' style='stroke:rgb(" + e.getColor().getRed() + "," + e.getColor().getGreen() + "," + e.getColor().getBlue() + ");stroke-width:" + e.getWidth() + "'/>");
			}
			for(Vertex n : network.getVertices() ) {
				p.println("  <svg:circle id='" + index.get(n) + "' r='" + n.getSize() + "' cx='" + (int)layout.transform(n).getX() + "' cy='" +(int) layout.transform(n).getY() + "' style='fill:rgb(" + n.getFillColor().getRed() + "," + n.getFillColor().getGreen() + "," + n.getFillColor().getBlue() + "); stroke:rgb(" + n.getColor().getRed() + "," + n.getColor().getGreen() + "," + n.getColor().getBlue() + "); stroke-width:" + n.getWidth() + ";'/>");
			}
			for(Vertex n : network.getVertices() ) {
				p.println("  <svg:a xlink:href='#' target='_black'>");
				int x = (int)layout.transform(n).getX() + (int)n.getSize() + 2;
				int y = (int)layout.transform(n).getY() + (int)n.getSize() + 2;
				String label = "";
				if(n.getLabel() != null)
					label = n.getLabel();					
				p.println("     <svg:text id='label" + index.get(n) + "' x='" + x + "' y='" + y + "' font-size='12' fill='black'>" + label + "</svg:text>");
				p.println("</svg:a>");
			}
			p.println("</svg:svg>");
			p.println("</body>");
			p.println("</html>");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
