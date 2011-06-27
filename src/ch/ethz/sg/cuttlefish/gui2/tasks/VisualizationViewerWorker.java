/*
  
    Copyright (C) 2011  Markus Michael Geipel, David Garcia Becerra,
    Petar Tsankov

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
