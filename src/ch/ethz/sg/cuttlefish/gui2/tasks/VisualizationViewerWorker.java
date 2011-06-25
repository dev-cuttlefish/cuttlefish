package ch.ethz.sg.cuttlefish.gui2.tasks;

import java.util.concurrent.BlockingQueue;
import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class VisualizationViewerWorker implements Runnable {

	private VisualizationViewer<Vertex, Edge> visualizationViewer;
	private BlockingQueue<VisualizationViewerTask> tasks;
	private boolean running;
	private Layout<Vertex, Edge> layout;
	
	public VisualizationViewerWorker(Layout<Vertex, Edge> layout, BlockingQueue<VisualizationViewerTask> tasks) {
		System.out.println("Initializing worker");
		this.tasks = tasks;
		running = true;		
		this.layout = layout;
	}
	
	@Override
	public void run() {
		visualizationViewer = new VisualizationViewer<Vertex, Edge>(layout);
		synchronized (this) {
			this.notifyAll();	
		}
		
		while(running) {
			try {
				System.out.println("Running a task");
				tasks.take().run();
				System.out.println("Finished a task");
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(null, "Could not execute VisualizationViewer Task", "Visualization error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
	
	public VisualizationViewer<Vertex, Edge> getVisualizationViewer() {
		return visualizationViewer;					
	}

}
