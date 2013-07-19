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
import java.awt.event.MouseWheelEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;

public class ZoomToolbar extends AbstractToolbar {

	private static final long serialVersionUID = 5121768424551966519L;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JButton refreshButton;
	private JButton centerButton;

	private static String zoomInIconFile = "icons/zoom-in.png";
	private static String zoomOutIconFile = "icons/zoom-out.png";
	private static String refreshIconFile = "icons/refresh.png";
	private static String centerIconFile = "icons/center_layout.png";

	public ZoomToolbar(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
	}

	private void initialize() {
		zoomInButton = new JButton(new ImageIcon(getClass().getResource(
				zoomInIconFile)));
		zoomOutButton = new JButton(new ImageIcon(getClass().getResource(
				zoomOutIconFile)));
		refreshButton = new JButton(new ImageIcon(getClass().getResource(
				refreshIconFile)));
		centerButton = new JButton(new ImageIcon(getClass().getResource(
				centerIconFile)));

		zoomInButton.setToolTipText("Zoom in");
		zoomOutButton.setToolTipText("Zoom out");
		refreshButton.setToolTipText("Refresh layout");
		centerButton.setToolTipText("Restore main view");

		this.add(zoomInButton);
		this.add(zoomOutButton);
		this.add(refreshButton);
		this.add(centerButton);

		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				networkPanel.resumeLayout();
			}
		});

		centerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				networkPanel.centerLayout();
			}
		});

		zoomInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scrollMouse(1);
			}
		});

		zoomOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scrollMouse(-1);
			}
		});
	}

	private void scrollMouse(int scrollAmount) {
		int x = networkPanel.getCanvas().getWidth() / 2;
		int y = networkPanel.getCanvas().getHeight() / 2;

		MouseWheelEvent evt = new MouseWheelEvent(networkPanel.getCanvas(),
				CENTER, 0, 0, x, y, 1, false, 1, scrollAmount, scrollAmount);

		networkPanel.getMouse().mouseWheelMoved(evt);
	}
}
