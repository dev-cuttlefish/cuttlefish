/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

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

package ch.ethz.sg.cuttlefish.gui.widgets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.networks.ISimulation;

public class SimulationPanel extends BrowserWidget {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton stopButton = null;
	private JButton runButton = null;
	private JButton jButton2 = null;
	private JButton jButton3 = null;
	private Thread thread = null;
	private long sleepTime=200;
	private boolean isRunning=true;

	/**
	 * This method initializes 
	 * 
	 */
	public SimulationPanel() {
		super();
		initialize();
		setNetworkClass(ISimulation.class);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.anchor = GridBagConstraints.CENTER;
		gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
		
		this.setLayout(new GridBagLayout());
        this.add(getJButton3(), gridBagConstraints1);
        this.add(getStopButton(), gridBagConstraints1);
        this.add(getRunButton(), gridBagConstraints1);
        this.add(getJButton2(), gridBagConstraints1);
	}

	/**
	 * This method initializes stopButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton();
			stopButton.setText("stop");
			stopButton.setEnabled(false);
			stopButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					thread = null;
					isRunning = false;
					stopButton.setEnabled(false);
					runButton.setEnabled(true);
				}
			});
		}
		return stopButton;
	}

	/**
	 * This method initializes runButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRunButton() {
		if (runButton == null) {
			runButton = new JButton();
			runButton.setText("run");
			runButton.addActionListener(new java.awt.event.ActionListener() {
				

				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (thread == null) {
							stopButton.setEnabled(true);
							runButton.setEnabled(false);
							
	                       thread = new Thread() {
	                    	                         
							
	                           /*
	                            * (non-Javadoc)
	                            *
	                            * @see java.lang.Thread#run()
	                            */
	                           @Override
	                           public void run() {
	                        	   isRunning = true;
	                        	   while(isRunning){
	                        		   isRunning = ((ISimulation)getNetwork()).update(sleepTime);
	                        		   getBrowser().onNetworkChange();
	                        		   
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
					getBrowser().resumeLayout();				
				}
			});
		}
		return runButton;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("step");
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					((ISimulation)getNetwork()).update(sleepTime);
					System.out.println("StepChange");
					getBrowser().onNetworkChange();
				}
			});
		}
		return jButton2;
	}

	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton3() {
		if (jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setText("reset");
			jButton3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					isRunning = false;
					thread = null;
					((ISimulation)getNetwork()).reset();
					System.out.println("ResetChange");
					getBrowser().onNetworkChange();
					getRunButton().setEnabled(true);
					getStopButton().setEnabled(false);
				}
			});
		}
		return jButton3;
	}

	@Override
	public void init() {
	}

}
