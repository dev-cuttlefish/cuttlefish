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
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;


public class EdgeMetricsPanel extends BrowserWidget  {

	private static final long serialVersionUID = 1L;
	private JButton inDegreeDistributionButton = null;
	private JButton outDegreeNodeButton = null;
	private JButton edgeInfoButton = null;
	private JTextField edgeInfoTextField = null;
	private JLabel jLabel = null;

	private JFileChooser inDegreeDistributionFileC = null;
	private JFileChooser outDegreeNodeFileC = null;

	/**
	 * This is the default constructor
	 */
	public EdgeMetricsPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints3.gridy = 0;
		jLabel = new JLabel();
		jLabel.setText("prefix:");
		jLabel.setVisible(false);
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints21.gridy = 2;
		gridBagConstraints21.weightx = 1.0;
		gridBagConstraints21.gridwidth = 2;
		gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints21.gridx = 0;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.gridy = 1;
		gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints11.weightx = 1.0;
		gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridy = 1;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.gridy = 3;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridy = 3;
		
		this.setSize(287, 230);
		this.setLayout(new GridBagLayout());
		//this.setBorder(BorderFactory.createTitledBorder(null, "export", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		this.add(getInDegreeDistributionButton(), gridBagConstraints);
		this.add(getOutDegreeNodeButton(), gridBagConstraints1);
		this.add(getEdgeInfoButton(), gridBagConstraints11);
		this.add(getEdgeInfoTextField(), gridBagConstraints21);
		
		this.add(jLabel, gridBagConstraints3);
	}

	
	private JFileChooser getInDegreeDistributionFileChooser() {
		if (inDegreeDistributionFileC == null) {
			inDegreeDistributionFileC = new JFileChooser();
		}
		return inDegreeDistributionFileC;
	}
	
	/**
	 * This method initializes tikzButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getInDegreeDistributionButton() {
		if (inDegreeDistributionButton == null) {
			inDegreeDistributionButton = new JButton();
			inDegreeDistributionButton.setText("in-deg dist");
			inDegreeDistributionButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					 JFileChooser fc = getInDegreeDistributionFileChooser();
				     fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
				     	fc.setSelectedFile(new File(getNetwork().getName()+".txt"));
			            int returnVal = fc.showSaveDialog(EdgeMetricsPanel.this);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
			                File file = fc.getSelectedFile();
			                saveInDegreeDistribution(file);
					    } else {
			                System.out.println("Input cancelled by user");
			            }
				}
			});
		}
		return inDegreeDistributionButton;
	}

	

	/**
	 * This method initializes tikzButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOutDegreeNodeButton() {
		if (outDegreeNodeButton == null) {
			outDegreeNodeButton = new JButton();
			outDegreeNodeButton.setText("out-deg node");
			outDegreeNodeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					 JFileChooser fc = getOutDegreeNodeFileChooser();
				     fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
				     	fc.setSelectedFile(new File(getNetwork().getName()+".txt"));
			            int returnVal = fc.showSaveDialog(EdgeMetricsPanel.this);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
			                File file = fc.getSelectedFile();
			                saveOutDegreeNode(file);
						}
			              else {
			                System.out.println("Input cancelled by user");
			            }
				}
			});
		}
		return outDegreeNodeButton;
	}
		

	private JFileChooser getOutDegreeNodeFileChooser() {
		if (outDegreeNodeFileC == null) {
			outDegreeNodeFileC = new JFileChooser();
		}
		return outDegreeNodeFileC;
	}
	

	/**
	 * This method initializes tikzButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getEdgeInfoButton() {
		if (edgeInfoButton == null) {
			edgeInfoButton = new JButton();
			edgeInfoButton.setText("edge info");
			edgeInfoButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					 double weight = 0;
					 Vertex destination = null;
					 Edge edge = null;
					 for (Edge edge_temp : getBrowser().getPickedEdges())
					 {
						 edge = edge_temp;
						 weight = edge.getWeight();
						 destination = getNetwork().getDest(edge);
					 }
					
					 double v1 = calculateRatioWeight(weight, getNetwork().getSource(edge));
					 double v2 = calculateRatioScore(destination);
				     double information = v1 - v2;
					 getEdgeInfoTextField().setText("" + information);
					 
				}
			});
		}
		return edgeInfoButton;
	}
	
	private double calculateRatioWeight(double weight, Vertex vertex)
	{
		double totalWeight = 0;
		for ( Edge edge : getNetwork().getOutEdges(vertex))
			totalWeight += edge.getWeight();
		
		return weight / totalWeight;
	}

	Double totalWeight = null;
	private double calculateRatioScore(Vertex vertex)
	{
			if (totalWeight == null)
			{	
				totalWeight = new Double(0.0);
				for (Edge edge : getNetwork().getEdges())
					totalWeight += edge.getWeight();
			}
			double vertexScore = 0;
			for (Edge edge: getNetwork().getIncidentEdges(vertex))
				vertexScore += edge.getWeight();
			
			return vertexScore / totalWeight;
		
	}
	
		
	
	private JTextField getEdgeInfoTextField(){
		if (edgeInfoTextField == null)
		{
			edgeInfoTextField = new JTextField("edge info");
		}
		return edgeInfoTextField;
	}
	
	private void saveOutDegreeNode(File file)
	{

		ArrayList<Double> inDegrees = new ArrayList<Double>();
		ArrayList<Double> weights = new ArrayList<Double>();

		for (Vertex v : getBrowser().getPickedVertices())
		{
			for (Edge e : getNetwork().getOutEdges(v))
			{
				Vertex v2 = getNetwork().getDest(e);
				double inDegree = 0;
				
				for (Edge e2 : getNetwork().getIncidentEdges(v2))
					inDegree += e2.getWeight();
				inDegrees.add(inDegree);
				weights.add(e.getWeight());
			}
		}

		
		try {
			PrintStream p = new PrintStream(file);

			while (! weights.isEmpty())
			{
				Double minW = Collections.min(weights);
				int index = weights.indexOf(minW);
				p.println(minW + "\t" + inDegrees.get(index));
				weights.remove(index);
				inDegrees.remove(index);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
		
		
		@Override
	public void init() {
	}

	private void saveInDegreeDistribution(File file)
	{
		ArrayList<Double> inDegrees = new ArrayList<Double>();
		
		for (Vertex v : getNetwork().getVertices())
		{
			double inDegree = 0;
			
			for (Edge e : getNetwork().getIncidentEdges(v))
				inDegree += e.getWeight();
	
			inDegrees.add(inDegree);
		}
		
		Collections.sort(inDegrees);
		try {
			PrintStream p = new PrintStream(file);
			int n_vals = 0;
			for (Double degree : inDegrees)
			{
				
				p.println(n_vals + "\t" + degree);
				n_vals++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}  //  @jve:decl-index=0:visual-constraint="0,0"
