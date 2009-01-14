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

package ch.ethz.sg.cuttlefish.gui.widgets;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JSlider;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.networks.TemporalNetwork;

public class DatePanel extends BrowserWidget {

	private static final long serialVersionUID = 1L;
	private JSlider dateSlider = null;
	private JLabel jLabel = null;
	private ArrayList<Date> dates = new ArrayList<Date>();  //  @jve:decl-index=0:
	private DateFormat format = new SimpleDateFormat("yyyy"); //former: "yyyy-MM-dd HH:mm:ss" 
	public Date getDate(){
		
		Date date = null;
		if(isActive()){
			date = dates.get(dateSlider.getValue());
		}

		return date;
	}
	


	/**
	 * This is the default constructor
	 */
	public DatePanel() {
		super();
		initialize();
		dates.add(null);
		dateSlider.setValue(0);
		setClickable(true);
		setNetworkClass(TemporalNetwork.class);
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.fill = GridBagConstraints.NONE;
		gridBagConstraints2.gridwidth = 4;
		gridBagConstraints2.anchor = GridBagConstraints.CENTER;
		gridBagConstraints2.insets = new Insets(0, 0, 6, 0);
		gridBagConstraints2.gridy = 0;
		jLabel = new JLabel();
		jLabel.setText("JLabel");
		jLabel.setEnabled(false);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.gridx = 1;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getDateSlider(), gridBagConstraints);
		this.add(jLabel, gridBagConstraints2);
	}

	/**
	 * This method initializes dateSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getDateSlider() {
		if (dateSlider == null) {
			dateSlider = new JSlider();
			dateSlider.setPaintTicks(true);
			dateSlider.setValue(0);
			dateSlider.setMinorTickSpacing(1);
			dateSlider.setMaximum(0);
			dateSlider.setEnabled(false);
			dateSlider.setPreferredSize(new Dimension(400, 27));
			dateSlider.setSnapToTicks(true);
			dateSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				//TODO: change here the method so that status is updated only when the mouse button is released
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					refresh();
				}
			});

			
		}
		return dateSlider;
	}

	public ArrayList<Date> getDates() {
		return dates;
	}

	public void setDates(ArrayList<Date> dates2) {
		if(dates2.size()>0){
			this.dates = dates2;
			dateSlider.setMaximum(dates2.size()-1);
			dateSlider.setValue(0);
		}
	}



	private void refresh(){
		if(getNetwork() != null){
		jLabel.setText(getDate()==null ? "" : format.format(getDate()));
		((TemporalNetwork)getNetwork()).setDate(getDate());
		getBrowser().onNetworkChange();
		}
	}
	
	@Override
	public void onActiveChanged() {
		dateSlider.setEnabled(isActive());
		jLabel.setEnabled(isActive());
		refresh();
		
	}





	@Override
	protected void onNetworkSet() {
		setDates(((TemporalNetwork)getNetwork()).getDates());
	}



	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
