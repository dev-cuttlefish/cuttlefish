/*
    
    Copyright (C) 2008  Markus Michael Geipel

    This program is free software: you can redistribute it and/or modify
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

package ch.ethz.sg.cuttlefish.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class ZoomSliderPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JSlider jSlider = null;
	private ZoomListener zoomListener = null;  //  @jve:decl-index=0:
	private JLabel jLabel = null;
	public ZoomListener getZoomListener() {
		return zoomListener;
	}

	public void setZoomListener(ZoomListener zoomListener) {
		this.zoomListener = zoomListener;
	}

	/**
	 * This is the default constructor
	 */
	public ZoomSliderPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		jLabel = new JLabel();
		jLabel.setText("100%");
		jLabel.setForeground(Color.white);
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		this.setLayout(flowLayout);
		this.setSize(248, 37);
		this.setBackground(Color.gray);
		this.add(jLabel, null);
		this.add(getJSlider(), null);
	}

	/**
	 * This method initializes jSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getJSlider() {
		if (jSlider == null) {
			jSlider = new JSlider();
			jSlider.setMajorTickSpacing(100);
			jSlider.setMinimum(1);
			jSlider.setMinorTickSpacing(2);
			jSlider.setInverted(false);
			jSlider.setPaintTicks(true);
			jSlider.setValue(100);
			jSlider.setSnapToTicks(true);
			jSlider.setBackground(Color.gray);
			jSlider.setPreferredSize(new Dimension(400, 27));
			jSlider.setMaximum(200);
			jSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if(zoomListener!=null){
						double zoom = ((double)jSlider.getValue())/100.0;
						zoomListener.setZoom(zoom);
						jLabel.setText(jSlider.getValue() + "%");
					}
				}
			});
		}
		return jSlider;
	}

	public void setZoom(double d) {
		jSlider.setValue((int)(d*100));
		
	}

}  //  @jve:decl-index=0:visual-constraint="0,0"
