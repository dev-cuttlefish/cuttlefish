package ch.ethz.sg.cuttlefish.misc;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;

public class AppletExporter {

	private BrowsableNetwork network;
	private Layout< Vertex, Edge> layout;
	
	
	public AppletExporter(BrowsableNetwork network, Layout<Vertex,Edge> layout) {
		this.network = network;
		this.layout = layout;
	}
	
	public void exportToApplet(File file) {
		try {
			PrintStream p = new PrintStream(file);
			p.println("<applet code=\"ch.ethz.sg.cuttlefish.gui2.applet.Cuttlefish.class\"");
			//p.println("\tcodebase=\"dist\"");
			p.println("\tarchive=\"cuttlefish.jar\"");
			p.println("\twidth=\"1000\" height=\"700\">");
			String bla = "{nodes: [{id: 0, size: 23, width:  0, label: \"MS\", color: \"{0.900000,0.780300,0.270000}\"}], edges: []}";
			p.println("\t<param name='data' value='" + bla + "'/>");
			p.println("\t<param name='source_node' value='0'/>");
			p.println("\t<param name='distance' value='1'/>");
			p.println("Your browser is completely ignoring the <applet> tag!");
			p.println("</applet>");
			p.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public void exportInitialPage(File file, String imageName, String networkFilename, int imageWidth, int imageHeight) {
		try {
			PrintStream p = new PrintStream(file);
			p.println("<img src='" + imageName + "' width:" + imageWidth + 
					" height:" + imageHeight + " usemap=#nodes />");
			p.println("<map name='nodes'>");
			for(Vertex v : network.getVertices()) {
				int nodeId = v.getId();
				int distance = 1;
				Point2D loc = layout.transform(v);
				p.println("  <area shape='circle' coords='" + loc.getX() + "," + loc.getY() + "," 
						+ v.getSize() + "' href='" + networkFilename + "?source_node=" + nodeId + 
						"&distance=" + distance + "' alt='" + v.getLabel() + "' title='" + v.getLabel() + "' />");
			}
			p.println("</map>");
			p.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
			
	}
	
	public void exportToDynamicApplet(File file) {
		try {
			PrintStream p = new PrintStream(file);
			p.println("<html>");
			p.println("<head>");
			p.println("<script language=\"javascript\">");
			p.println("    function getParam(variable){");
			p.println("         var query = window.location.search.substring(1);");
			p.println("         var vars = query.split(\"&\");");
			p.println("          for (var i=0;i<vars.length;i++) {");
			p.println("                var pair = vars[i].split(\"=\");");
			p.println("                if(pair[0] == variable){return pair[1];}");
			p.println("           }       return(false);");
			p.println("    }");
			p.println("</script>");
			p.println("</head>");
			p.println("<body>");			
			p.println("<applet code=\"ch.ethz.sg.cuttlefish.gui2.applet.Cuttlefish.class\"");
			p.println("   archive=\"cuttlefish.jar\"");
			p.println("   width=\"1000\" height=\"700\">");
			
			//"{nodes: [{id: 0, size: 23, width:  0, label: \"MS\", color: \"{0.900000,0.780300,0.270000}\"}], edges: []}";
			p.println("  <param name='data' value='");
			
			exportJsonData(p);
			p.print("' />");
			p.println("   <script language=\"javascript\">");
			p.println("      var source_node= getParam(\"source_node\");");
			p.println("      var distance = getParam(\"distance\");");
			p.println("      document.write(\"<param name=source_node value=\" + source_node + \" />\");");
			p.println("      document.write(\"<param name=distance value=\" + distance+ \" />\");");
			p.println("    </script>");
			p.println("Your browser is completely ignoring the <applet> tag!");
			p.println("</applet>");
			p.println("</body>");
			p.println("</html>");
			p.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void exportJsonData(PrintStream p) {
		p.println("{ nodes: [");
		boolean first = true;
		for(Vertex v : network.getVertices() ) {
			if(!first) {
				p.println(",");
			} else {
				first = false;
			}	
			p.print("     {id: " + v.getId());
			p.print(", size: " + v.getSize());
			p.print(", width: " + v.getWidth());
							
			if(v.getLabel() != null)
				p.print(", label: \"" + v.getLabel() + "\"");
			if(v.getFillColor() != null)
				p.print(", color: \"{" + ((double)v.getFillColor().getRed()/256.d) +","+((double)v.getFillColor().getGreen()/256.d)+","+((double)v.getFillColor().getBlue()/256.d)+"}\"}");
			else
				p.print("}");
			
		}			
		p.println("     ], edges: [");
		first = true;
		for(Edge e : network.getEdges() ) {
			if(!first) {
				p.println(",");
			} else {
				first = false;
			}	
			p.print("     {id_origin: " + (network.getSource(e) != null ? network.getSource(e).getId() : network.getEndpoints(e).getFirst().getId() ) );
			p.print(", id_dest: " + (network.getDest(e) != null ? network.getDest(e).getId() : network.getEndpoints(e).getSecond().getId() ));
			p.print(", label: " + e.getLabel());
			p.print(", weight: " + e.getWeight());
			p.print(", width: " + e.getWidth());
			if(e.getColor() != null)
				p.print(", color: \"{" + ((double)e.getColor().getRed()/256.d) +","+((double)e.getColor().getGreen()/256.d)+","+((double)e.getColor().getBlue()/256.d)+"}\"}");
			else
				p.print("} />");				
		}
		p.println("     ]}");
	}

}
