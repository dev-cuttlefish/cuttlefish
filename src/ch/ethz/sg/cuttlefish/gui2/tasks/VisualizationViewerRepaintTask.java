package ch.ethz.sg.cuttlefish.gui2.tasks;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class VisualizationViewerRepaintTask extends VisualizationViewerTask{

	public VisualizationViewerRepaintTask(VisualizationViewer<Vertex, Edge> viewer) {
		super(viewer);
	}

	@Override
	public void run() {
		System.out.println("Executing repaint task");
		visualizationViewer.repaint();		
	}
	
}
