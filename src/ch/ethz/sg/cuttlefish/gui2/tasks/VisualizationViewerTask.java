package ch.ethz.sg.cuttlefish.gui2.tasks;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public abstract class VisualizationViewerTask extends Task {

	protected VisualizationViewer<Vertex, Edge> visualizationViewer;
	
	public VisualizationViewerTask(VisualizationViewer<Vertex, Edge> viewer) {
		this.visualizationViewer = viewer;
	}

}
