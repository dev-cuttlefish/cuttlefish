package ch.ethz.sg.cuttlefish.gui2.tasks;

import javax.swing.SwingWorker;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;

public class SetLayoutWorker extends SwingWorker<Object, Object> {
		NetworkPanel networkPanel;
		String layoutName;
		public SetLayoutWorker(String layoutName, NetworkPanel networkPanel) {
			this.networkPanel = networkPanel;
			this.layoutName = layoutName;
		}
		@Override
		protected Object doInBackground() throws Exception {
			networkPanel.getStatusBar().setBusyMessage("Setting layout to " + layoutName, this);
			networkPanel.setLayoutByName(layoutName);
			synchronized (networkPanel) {
				networkPanel.notifyAll();	
			}	
			return null;
		}
		
		@Override
		protected void done() {
			super.done();
			networkPanel.getStatusBar().setMessage("Done setting layout");
		}
		
	}