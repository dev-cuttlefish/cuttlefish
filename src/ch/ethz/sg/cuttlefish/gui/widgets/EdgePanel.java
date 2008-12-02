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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.misc.SGUserData;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.utils.UserData;

public class EdgePanel extends BrowserWidget implements ItemListener{

	@Override
	public void updateAnnotations() {
		colorEdges();
	}


	private static final long serialVersionUID = 1L;
	private JRadioButton indegreeButton = null;
	private JRadioButton outdegreeButton = null;
	private JRadioButton linearButton = null;
	private JRadioButton logButton = null;
	
	public static final int OFF = 0;
	public static final int OUTDEGREE = 1;
	public static final int INDEGREE = 2;
	
	public static final int LINEAR = 1;
	public static final int LOG = 0;
	
	ButtonGroup degreeGroup = new ButtonGroup();  //  @jve:decl-index=0:
	ButtonGroup functionGroup = new ButtonGroup();
  //  @jve:decl-index=0:
	private JPanel jPanel = null;

	/**
	 * This is the default constructor
	 */
	public EdgePanel() {
		super();
		initialize();
		setClickable(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
		gridBagConstraints41.gridx = 0;
		gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints41.gridy = 4;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.anchor = GridBagConstraints.WEST;
		gridBagConstraints4.gridy = 6;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.gridy = 5;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.gridy = 3;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 1;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getIndegreeButton(), gridBagConstraints);
		this.add(getOutdegreeButton(), gridBagConstraints1);
		this.add(getLinearButton(), gridBagConstraints3);
		this.add(getLogButton(), gridBagConstraints4);
		this.add(getJPanel(), gridBagConstraints41);
	}

	/**
	 * This method initializes indegreeButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getIndegreeButton() {
		if (indegreeButton == null) {
			indegreeButton = new JRadioButton();
			indegreeButton.setText("indeg");
			indegreeButton.setSelected(true);
			indegreeButton.addItemListener(this);
			degreeGroup.add(indegreeButton);
		}
		return indegreeButton;
	}

	/**
	 * This method initializes outdegreeButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getOutdegreeButton() {
		if (outdegreeButton == null) {
			outdegreeButton = new JRadioButton();
			outdegreeButton.setText("outdeg");
			outdegreeButton.addItemListener(this);
			degreeGroup.add(outdegreeButton);
		}
		return outdegreeButton;
	}

	/**
	 * This method initializes linearButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getLinearButton() {
		if (linearButton == null) {
			linearButton = new JRadioButton();
			linearButton.setText("linear");
			linearButton.addItemListener(this);
			functionGroup.add(linearButton);
		}
		return linearButton;
	}

	/**
	 * This method initializes logButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getLogButton() {
		if (logButton == null) {
			logButton = new JRadioButton();
			logButton.setText("log");
			logButton.setSelected(true);
			logButton.addItemListener(this);
			functionGroup.add(logButton);
		}
		return logButton;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setPreferredSize(new Dimension(10, 1));
			jPanel.setBackground(Color.gray);
		}
		return jPanel;
	}

	public int getDegreeMode() {
		
		return indegreeButton.isSelected() ? INDEGREE : OUTDEGREE;
	}

	public int getFunction() {
		return linearButton.isSelected() ? LINEAR : LOG;
	}

	public void itemStateChanged(ItemEvent arg0) {
		getBrowser().refreshAnnotations();
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void colorEdges(){
		int maxDegree = getNetwork().getMaxDegree();
		double factor = 255.0 / maxDegree;
		System.out.println(factor);

		
		
		for(DirectedSparseEdge e: (Set<DirectedSparseEdge>)getNetwork().getEdges()){
			
			int degree;
			if(getDegreeMode() == EdgePanel.INDEGREE){
				degree = e.getDest().degree();
			}else{
				degree = e.getSource().degree();
			}
			int red;	
			if(getFunction() == EdgePanel.LOG){
				red = (int) ((Math.log(degree+1) * 255)/ Math.log(maxDegree+1));
			}else{
				red = (int) ((degree * 255.0)/ maxDegree);
			}
			 
			//int red = (int) ((degree * 255.0)/ maxDegree);
			Color color;
			
				color = new Color(red,0,255-red);
				e.setUserDatum(SGUserData.COLOR, color, UserData.REMOVE);
			
			
			
		}
		//getBrowser().repaintViewer();
	}


	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
