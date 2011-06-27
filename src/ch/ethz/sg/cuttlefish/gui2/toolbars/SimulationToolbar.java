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

package ch.ethz.sg.cuttlefish.gui2.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;
import ch.ethz.sg.cuttlefish.networks.ISimulation;
import ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork;

public class SimulationToolbar extends AbstractToolbar implements Observer  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -103163832608026663L;
	private JButton stepButton;
	private JButton runButton;
	private JButton resetButton;
	private JLabel frameLabel;
	private final String stepIconFile = "icons/step.png";
	private final String runIconFile = "icons/run.png";
	private final String resetIconFile = "icons/stop.png";
	private long sleepTime = 200;
	private Thread thread = null;
	private boolean isRunning = false;
	private boolean enabled = false;
	
	public SimulationToolbar(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
	}
	
	@Override
	public void setVisible(boolean b) {
		if( networkPanel.getNetwork() instanceof ISimulation) {
			super.setVisible(b);
			enabled = true;
		} else {
			super.setVisible(false);
			enabled = false;
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	private void initialize() {
		stepButton = new JButton(new ImageIcon(getClass().getResource(stepIconFile)));
		runButton = new JButton(new ImageIcon(getClass().getResource(runIconFile)));
		resetButton = new JButton(new ImageIcon(getClass().getResource(resetIconFile)));
		frameLabel = new JLabel();
		frameLabel.setVisible(false);
		stepButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				resetButton.setEnabled(true);
				((ISimulation)networkPanel.getNetwork()).update(sleepTime);
				System.out.println("StepChange");
				networkPanel.onNetworkChange();
			}
		});
		
		runButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (thread == null) {
					resetButton.setEnabled(true);
					runButton.setEnabled(false);					
                   	thread = new Thread() {
                   		@Override
                   		public void run() {
                   			isRunning = true;
                   			while(isRunning){
                   				isRunning = ((ISimulation)networkPanel.getNetwork()).update(sleepTime);
                   				networkPanel.onNetworkChange();
                    		   
                   				try {
                   					Thread.sleep(sleepTime);
                   				} catch (InterruptedException iEx) {
                    				JOptionPane.showMessageDialog(null,iEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    				System.err.println("Interrupted simulation process");
                    				iEx.printStackTrace();
                   				}
                   			}
                   		}

                   	};
                   	thread.start();
				}
				networkPanel.resumeLayout();								
			}
		});
		
		resetButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				isRunning = false;
				thread = null;
				((ISimulation)networkPanel.getNetwork()).reset();
				System.out.println("ResetChange");
				networkPanel.onNetworkChange();
				runButton.setEnabled(true);
				resetButton.setEnabled(false);
			}
		});
		
		this.add(resetButton);
		this.add(runButton);
		this.add(stepButton);
		this.add(frameLabel);
	}

	@Override
	public void update(Subject o) {
		if(o instanceof InteractiveCxfNetwork) {
			if(!frameLabel.isVisible())
				frameLabel.setVisible(true);
			frameLabel.setText(" " + ((InteractiveCxfNetwork)o).getCurrentLabel() + " ");
		}
	}


}
