package ch.ethz.sg.cuttlefish.gui2.tasks;

import java.util.Random;

import javax.swing.SwingWorker;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;

public class SetLayoutWorker extends SwingWorker<Object, Object> {
		NetworkPanel networkPanel;
		String layoutName;
		int id;
	
		public SetLayoutWorker(String layoutName, NetworkPanel networkPanel) {
			id = (new Random()).nextInt();
			System.out.println("Creating layout worker " + id);
			this.networkPanel = networkPanel;
			this.layoutName = layoutName;
		}
		@Override
		protected Object doInBackground() throws Exception {
			networkPanel.getStatusBar().setBusyMessage("Setting layout to " + layoutName, this);
			System.out.println("Creating layout worker " + id + " : start setting layout");
			networkPanel.setLayoutByName(layoutName);
			synchronized (networkPanel) {
				networkPanel.notifyAll();	
			}
			System.out.println("Creating layout worker " + id + " : done");
			return null;
		}
		
		@Override
		protected void done() {
			super.done();
			System.out.println("Creating layout worker " + id + " : done");
			networkPanel.getStatusBar().setMessage("Done setting layout");
		}
		
	}