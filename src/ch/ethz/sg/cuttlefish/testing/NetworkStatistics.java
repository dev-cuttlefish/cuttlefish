package ch.ethz.sg.cuttlefish.testing;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.project.api.ProjectController;
import org.gephi.statistics.api.StatisticsController;
import org.gephi.statistics.plugin.ConnectedComponents;
import org.gephi.statistics.plugin.Degree;
import org.gephi.statistics.plugin.GraphDensity;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.spi.Statistics;
import org.openide.util.Lookup;

import ch.ethz.sg.cuttlefish.networks.CxfNetwork;

// TODO ilias: Improve & Add to menu!
public class NetworkStatistics {

	private StatisticsController controller;
	private Graph graph;

	private List<Statistics> allStats;
	private long time;

	private ConnectedComponents cc;
	private Degree degree;
	private GraphDensity density;
	private GraphDistance distance;

	public NetworkStatistics() {
		controller = Lookup.getDefault().lookup(StatisticsController.class);
		graph = Lookup.getDefault().lookup(GraphController.class).getModel()
				.getGraph();
		allStats = new ArrayList<Statistics>();

		cc = (ConnectedComponents) controller.getBuilder(
				ConnectedComponents.class).getStatistics();
		allStats.add(cc);

		degree = (Degree) controller.getBuilder(Degree.class).getStatistics();
		allStats.add(degree);

		density = (GraphDensity) controller.getBuilder(GraphDensity.class)
				.getStatistics();
		allStats.add(density);

		distance = (GraphDistance) controller.getBuilder(GraphDistance.class)
				.getStatistics();
		allStats.add(distance);
	}

	public boolean execute() {

		if (graph.getNodeCount() == 0) {
			return false;
		}

		time = System.currentTimeMillis();

		for (Statistics s : allStats)
			controller.execute(s);

		time = System.currentTimeMillis() - time;
		return true;
	}

	public String getReport() {
		StringBuilder report = new StringBuilder();

		report.append("\n");
		report.append("~ Graph Elements\n");
		report.append("    * Vertices: ").append(graph.getNodeCount())
				.append("\n");
		report.append("    * Edges: ").append(graph.getEdgeCount())
				.append("\n\n");

		report.append("~ Connected Components\n");
		report.append("    * Number: ")
				.append(cc.getConnectedComponentsCount()).append("\n");
		if (cc.getConnectedComponentsCount() > 1)
			report.append("    * Sizes: ")
					.append(Arrays.toString(cc.getComponentsSize()))
					.append("\n");
		report.append("\n");

		report.append("~ Graph Degree\n");
		report.append("    * Average degree: ")
				.append(degree.getAverageDegree()).append("\n\n");

		report.append("~ Graph Density\n");
		report.append("    * Density: ").append(density.getDensity())
				.append("\n\n");

		report.append("~ Graph Distance\n");
		report.append("    * Diameter: ").append(distance.getDiameter())
				.append("\n");
		report.append("    * Path Length: ").append(distance.getPathLength())
				.append("\n\n");

		report.append("(statistics computed in " + (time / 1000.0) + " s)");

		return report.toString();
	}

	public JFrame getReportUI() {
		JFrame frame = new JFrame("Network Statistics Report Beta");

		frame.setSize(300, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JTextArea text = new JTextArea(getReport());
		Font font = new Font("Monospaced", Font.BOLD, 12);
		text.setEditable(false);
		text.setFont(font);
		text.setForeground(Color.darkGray);

		frame.add(text);

		return frame;
	}

	public static void main(String[] args) throws FileNotFoundException {

		Lookup.getDefault().lookup(ProjectController.class).newProject();
		CxfNetwork network = new CxfNetwork();

		network.load(new File(
				"/home/ilias/workspace/sg/resources/networks/linear.cxf"));

		NetworkStatistics netStats = new NetworkStatistics();
		netStats.execute();

		System.out.println("~~~ Network Statistics Report ~~~");
		System.out.println(netStats.getReport());
		System.out.println("~~~~~~~~~ End of Report  ~~~~~~~~");

		netStats.getReportUI().setVisible(true);
	}

}
