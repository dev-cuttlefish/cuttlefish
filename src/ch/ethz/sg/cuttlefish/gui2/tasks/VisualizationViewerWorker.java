package ch.ethz.sg.cuttlefish.gui2.tasks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class VisualizationViewerWorker implements Runnable {

	private VisualizationViewer<Vertex, Edge> visualizationViewer;
	private BlockingQueue<VisualizationViewerTask> tasks;
	private boolean running;
	private Layout<Vertex, Edge> layout;
	
	public VisualizationViewerWorker(Layout<Vertex, Edge> layout) {
		System.out.println("Initializing worker");
		running = true;		
		this.layout = layout;
		tasks = new LinkedBlockingDeque<VisualizationViewerTask>();
	}
	
	@Override
	public void run() {
		visualizationViewer = new VisualizationViewer<Vertex, Edge>(layout);
		while(running) {
			try {
				System.out.println("Running a task");
				tasks.take().run();
				System.out.println("Finished a task");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addTask(VisualizationViewerTask task) {
		try {
			tasks.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public VisualizationViewer<Vertex, Edge> getVisualizationViewer() {
		return visualizationViewer;		
	}

}
