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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.misc.Utils;
import ch.ethz.sg.cuttlefish.networks.TemporalNetwork;

public class AnimationPanel extends BrowserWidget  {

	private static final long serialVersionUID = 1L;
	private JButton jButton = null;
	private JTextField jTextField = null;
	private JLabel jLabel = null;
	/**
	 * This is the default constructor
	 */
	public AnimationPanel() {
		super();
		setNetworkClass(TemporalNetwork.class);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		jLabel = new JLabel();
		jLabel.setText("path prefix: ");
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints2.gridwidth = 1;
		gridBagConstraints2.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.gridy = 2;
		this.setSize(372, 200);
		this.setLayout(new GridBagLayout());
		this.add(getJButton(), gridBagConstraints);
		this.add(getJTextField(), gridBagConstraints2);
		this.add(jLabel, gridBagConstraints1);
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("export animation");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					makeAnimation();
				}
			});
		}
		return jButton;
	}

	private void makeAnimation(){
		String sFolder = jTextField.getText();//getArgument("folder");
		if(sFolder==null){
			sFolder = "";
		}
		
		//File folder = new File(sFolder);
		List<Date> dates = ((TemporalNetwork) getNetwork()).getDates();
		
		for(Date date:dates){
			((TemporalNetwork) getNetwork()).setDate(date);
			getBrowser().onNetworkChange();
			
			
			try {
				
				String fileName = null;
				//DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmm_ss");
				DateFormat format = new SimpleDateFormat("yyyy");
				fileName = sFolder+getNetwork().getName()+ format.format(((TemporalNetwork)getNetwork()).getDate()) + ".tex";
								
				System.out.println("exporting to " + fileName);
				PrintStream p = new PrintStream(new File(fileName));
				Utils.exportGraphToPSTricks(getNetwork(), p, getBrowser().getNetworkLayout());
				p.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			//Utils.exportGraphToPSTricks(getNetwork(), , getBrowser().getNetworkLayout());
		}
		((TemporalNetwork) getNetwork()).setDate(null);
		getBrowser().onNetworkChange();
	}
	
	@Override
	public void onActiveChanged() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setColumns(25);
		}
		return jTextField;
	}



	@Override
	public void init() {
		//String prefix = getArgument("folder");
		String prefix = "animations/";
		if(prefix == null){
			prefix="";
		}
		jTextField.setText(prefix);
		
		
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
