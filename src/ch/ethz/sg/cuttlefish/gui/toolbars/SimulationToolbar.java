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

package ch.ethz.sg.cuttlefish.gui.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.Cuttlefish;
import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.layout.arf.WeightedARFLayout;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;
import ch.ethz.sg.cuttlefish.networks.ISimulation;
import ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork;

public class SimulationToolbar extends AbstractToolbar implements Observer {

	private static final long serialVersionUID = -103163832608026663L;
	private JButton stepButton;
	private JButton runButton;
	private JButton resetButton;
	private JButton settingsButton;
	private JLabel frameLabel;
	private final String stepIconFile = "icons/step.png";
	private final String runIconFile = "icons/run.png";
	private final String resetIconFile = "icons/stop.png";
	private final String pauseIconFile = "icons/pause.png";
	private Icon runIcon = null;
	private Icon pauseIcon = null;
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
		if (networkPanel.getNetwork() instanceof ISimulation) {
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

	private Icon getRunIcon() {
		if (runIcon == null)
			runIcon = new ImageIcon(getClass().getResource(runIconFile));
		return runIcon;
	}

	private Icon getPauseIcon() {
		if (pauseIcon == null)
			pauseIcon = new ImageIcon(getClass().getResource(pauseIconFile));
		return pauseIcon;
	}

	private void initialize() {
		stepButton = new JButton(new ImageIcon(getClass().getResource(
				stepIconFile)));
		runButton = new JButton(getRunIcon());
		resetButton = new JButton(new ImageIcon(getClass().getResource(
				resetIconFile)));
		settingsButton = new JButton("Settings");
		frameLabel = new JLabel();
		frameLabel.setVisible(false);
		resetButton.setEnabled(false);
		stepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetButton.setEnabled(true);
				boolean isRunning = ((ISimulation) networkPanel.getNetwork())
						.update(sleepTime);
				networkPanel.onNetworkChange();
				stepButton.setEnabled(isRunning);
				runButton.setEnabled(isRunning);
			}
		});
		settingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String sleepTimeStr = (String) JOptionPane.showInputDialog(
						networkPanel,
						"Enter time between frames in milliseconds",
						"Time between frames", JOptionPane.QUESTION_MESSAGE,
						null, null, sleepTime);
				if (sleepTimeStr != null) {
					try {
						sleepTime = Long.parseLong(sleepTimeStr);
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(networkPanel,
								"The value that you enter is not an integer",
								"Incorrect input", JOptionPane.WARNING_MESSAGE,
								null);
					}
				}
			}
		});

		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetButton.setEnabled(true);
				if (thread == null || !thread.isAlive()) {
					// make sure the network is at initial state
					if (!stepButton.isEnabled()) {
						((ISimulation) networkPanel.getNetwork()).reset();
						networkPanel.onNetworkChange();
					}

					runButton.setIcon(getPauseIcon());
					stepButton.setEnabled(false);
					thread = new Thread() {
						@Override
						public void run() {
							isRunning = true;
							while (isRunning) {
								isRunning = ((ISimulation) networkPanel
										.getNetwork()).update(sleepTime);
								networkPanel.onNetworkChange();

								try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException iEx) {
									JOptionPane.showMessageDialog(null,
											iEx.getMessage(), "Error",
											JOptionPane.ERROR_MESSAGE);
									Cuttlefish
											.err("Interrupted simulation process");
									iEx.printStackTrace();
								}
							}
							runButton.setIcon(getRunIcon());
						}
					};
					thread.start();
				} else {
					isRunning = false;
					thread = null;
					runButton.setIcon(getRunIcon());
					stepButton.setEnabled(true);
				}
				// networkPanel.resumeLayout();
			}
		});

		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isRunning = false;
				thread = null;
				((ISimulation) networkPanel.getNetwork()).reset();
				networkPanel.onNetworkChange();
				runButton.setIcon(getRunIcon());
				runButton.setEnabled(true);
				stepButton.setEnabled(true);
				resetButton.setEnabled(false);
			}
		});

		this.add(resetButton);
		this.add(runButton);
		this.add(stepButton);
		this.add(settingsButton);
		this.add(frameLabel);
	}

	@Override
	public void update(Subject o) {
		if (o instanceof InteractiveCxfNetwork) {
			if (!frameLabel.isVisible())
				frameLabel.setVisible(true);
			frameLabel.setText(" "
					+ ((InteractiveCxfNetwork) o).getCurrentLabel() + " ");
			if (networkPanel.getNetworkLayout() instanceof WeightedARFLayout) {
				WeightedARFLayout layout = (WeightedARFLayout) networkPanel
						.getNetworkLayout();
				layout.setSleepTime(((InteractiveCxfNetwork) o)
						.getCurrentSleepTime());
				layout.setMaxUpdates(((InteractiveCxfNetwork) o)
						.getCurrentMaxStepUpdates());
			}
		}
	}

}
