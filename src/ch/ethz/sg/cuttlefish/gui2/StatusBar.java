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

package ch.ethz.sg.cuttlefish.gui2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;


public class StatusBar extends JPanel {

	/**
	 * 
	 */
	private SwingWorker currentTask;
	private JLabel label = null;
	private JLabel label1 = null;
	public JButton terminateTask;
	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar = null;
	public StatusBar() {
		super();
		terminateTask = new JButton("Stop");
		label = new JLabel();
		label1 = new JLabel("Status ");
		progressBar = new JProgressBar();
		progressBar.setMinimum(50);
		progressBar.setMaximum(100);
		add(label1);
		add(progressBar);
		add(label);		
		add(terminateTask);
		setMessage("Ready");
		
		terminateTask.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentTask == null)
					return;
				currentTask.cancel(true);
				setMessage("Task canceled");
				terminateTask.setEnabled(false);
			}
		});
	}
	
	public void setMessage(String message) {
		label.setText(" " + message);
		progressBar.setIndeterminate(false);
		terminateTask.setEnabled(false);
	}
	
	public void setBusyMessage(String message, SwingWorker task) {
		currentTask = task;		
		progressBar.setIndeterminate(true);
		progressBar.setString("Changing layout");
		label.setText(" " + message);
		terminateTask.setEnabled(true);
	}	
}
