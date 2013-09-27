package ch.ethz.sg.cuttlefish.exporter;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.project.api.Workspace;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class AppletExporter implements GraphExporter, CharacterExporter,
		NetworkExporter {

	private BrowsableNetwork network;
	private Workspace workspace;
	private Writer writer;

	@Override
	public boolean execute() {
		try {
			exportToApplet();
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

	public void exportToApplet() throws IOException {
		writer.append(
				"<applet code=\"ch.ethz.sg.cuttlefish.gui.applet.Cuttlefish.class\"")
				.append("\n");
		// writer.append("\tcodebase=\"dist\"");
		writer.append("\tarchive=\"cuttlefish.jar\"").append("\n");
		writer.append("\twidth=\"1000\" height=\"700\">").append("\n");
		String bla = "{nodes: [{id: 0, size: 23, width:  0, label: \"MS\", "
				+ "color: \"{0.900000,0.780300,0.270000}\"}], edges: []}";
		writer.append("\t<param name='data' value='" + bla + "'/>")
				.append("\n");
		writer.append("\t<param name='source_node' value='0'/>").append("\n");
		writer.append("\t<param name='distance' value='1'/>").append("\n");
		writer.append("Your browser is completely ignoring the <applet> tag!")
				.append("\n");
		writer.append("</applet>").append("\n");
	}

	public void exportInitialPage(File file, String imageName,
			String networkFilename, int imageWidth, int imageHeight)
			throws IOException {

		writer = new BufferedWriter(new FileWriter(file));
		writer.append(
				"<img src='" + imageName + "' width:" + imageWidth + " height:"
						+ imageHeight + " usemap=#nodes />").append("\n");
		writer.append("<map name='nodes'>").append("\n");
		for (Vertex v : network.getVertices()) {
			int nodeId = v.getId();
			int distance = 1;
			Point2D loc = v.getPosition();
			writer.append(
					"  <area shape='circle' coords='" + loc.getX() + ","
							+ loc.getY() + "," + v.getSize() + "' href='"
							+ networkFilename + "?source_node=" + nodeId
							+ "&distance=" + distance + "' alt='"
							+ v.getLabel() + "' title='" + v.getLabel()
							+ "' />").append("\n");
		}
		writer.append("</map>").append("\n");
		writer.close();
	}

	public void exportToDynamicApplet(File file) throws IOException {
		JsonExporter json = (JsonExporter) NetworkExportController
				.getExporter("json");
		json.setNetwork(network);
		writer = new BufferedWriter(new FileWriter(file));
		json.setWriter(writer);

		writer.append("<html>").append("\n");
		writer.append("<head>").append("\n");
		writer.append("<script language=\"javascript\">").append("\n");
		writer.append("    function getParam(variable){").append("\n");
		writer.append(
				"         var query = window.location.search.substring(1);")
				.append("\n");
		writer.append("         var vars = query.split(\"&\");").append("\n");
		writer.append("          for (var i=0;i<vars.length;i++) {").append(
				"\n");
		writer.append("                var pair = vars[i].split(\"=\");")
				.append("\n");
		writer.append(
				"                if(pair[0] == variable){return pair[1];}")
				.append("\n");
		writer.append("           }       return(false);").append("\n");
		writer.append("    }");
		writer.append("</script>").append("\n");
		writer.append("</head>").append("\n");
		writer.append("<body>").append("\n");
		writer.append(
				"<applet code=\"ch.ethz.sg.cuttlefish.gui.applet.Cuttlefish.class\"")
				.append("\n");
		writer.append("   archive=\"cuttlefish.jar\"").append("\n");
		writer.append("   width=\"1000\" height=\"700\">").append("\n");

		// "{nodes: [{id: 0, size: 23, width:  0, label: \"MS\", color: \"{0.900000,0.780300,0.270000}\"}], edges: []}";
		writer.append("  <param name='data' value='").append("\n");

		json.execute();
		writer.append("' />");
		writer.append("   <script language=\"javascript\">").append("\n");
		writer.append("      var source_node= getParam(\"source_node\");")
				.append("\n");
		writer.append("      var distance = getParam(\"distance\");").append(
				"\n");
		writer.append(
				"      document.write(\"<param name=source_node value=\" + source_node + \" />\");")
				.append("\n");
		writer.append(
				"      document.write(\"<param name=distance value=\" + distance+ \" />\");")
				.append("\n");
		writer.append("    </script>").append("\n");
		writer.append("Your browser is completely ignoring the <applet> tag!")
				.append("\n");
		writer.append("</applet>").append("\n");
		writer.append("</body>").append("\n");
		writer.append("</html>").append("\n");
		writer.close();
	}
}
