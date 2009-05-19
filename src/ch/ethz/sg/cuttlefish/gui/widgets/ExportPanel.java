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
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import com.sun.image.codec.jpeg.*;
import com.sun.imageio.plugins.png.PNGImageWriter;
import com.sun.imageio.plugins.png.PNGImageWriterSpi;

import ch.ethz.sg.cuttlefish.misc.TikzExporter;
import ch.ethz.sg.cuttlefish.misc.Utils;
import ch.ethz.sg.cuttlefish.misc.Utils2;
import ch.ethz.sg.cuttlefish.networks.TemporalNetwork;


public class ExportPanel extends BrowserWidget  {

	private static final long serialVersionUID = 1L;
	private JButton dotButton = null;
	private JButton pstricksButton = null;
	private JButton adjlistButton = null;
	private JButton edgelistButton = null;
	private JButton snapshotButton = null;
	private JButton tikzButton = null;
	private JTextField jTextField = null;
	private JLabel jLabel = null;


	/**
	 * This is the default constructor
	 */
	public ExportPanel() {
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
		gridBagConstraints21.gridy = 1;
		gridBagConstraints21.weightx = 1.0;
		gridBagConstraints21.gridwidth = 2;
		gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints21.gridx = 0;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.gridy = 2;
		gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints11.weightx = 1.0;
		gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridy = 2;
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
		
		
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 2;
		gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints4.fill = GridBagConstraints.BOTH;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.gridy = 3;
		
		
		this.setSize(287, 230);
		this.setLayout(new GridBagLayout());
		//this.setBorder(BorderFactory.createTitledBorder(null, "export", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		this.add(getSnapshotButton(), gridBagConstraints);
		this.add(getPstricksButton(), gridBagConstraints2);
		this.add(getAdjlistButton(), gridBagConstraints1);
		this.add(getEdgelistButton(), gridBagConstraints11);
		this.add(getJTextField(), gridBagConstraints21);
		this.add(getTikzButton(), gridBagConstraints4);
		
		
		this.add(jLabel, gridBagConstraints3);
	}

	/**
	 * This method initializes dotButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDotButton() {
		if (dotButton == null) {
			dotButton = new JButton();
			dotButton.setText("dot");
			dotButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
						exportToDot();
					
				}
			});
		}
		return dotButton;
	}

	/**
	 * This method initializes snapshotButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSnapshotButton() {
		if (snapshotButton == null) {
			snapshotButton = new JButton();
			snapshotButton.setText("snapshot");
			snapshotButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
						exportToJpg();
				}
			});
		}
		return snapshotButton;
	}

	
	/**
	 * This method initializes pstricksButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPstricksButton() {
		if (pstricksButton == null) {
			pstricksButton = new JButton();
			pstricksButton.setText("pstricks");
			pstricksButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
						exportToPsTricks();
					
				}
			});
		}
		return pstricksButton;
	}
	
	/**
	 * This method initializes tikzButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getTikzButton() {
		if (tikzButton == null) {
			tikzButton = new JButton();
			tikzButton.setText("tikz");
			tikzButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
						exportToTikz();
				}
			});
		}
		return tikzButton;
	}


	/**
	 * This method initializes adjlistButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAdjlistButton() {
		if (adjlistButton == null) {
			adjlistButton = new JButton();
			//adjlistButton.setText("adj. list");
			adjlistButton.setText("adj. matrix");
			adjlistButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						//PrintStream p = new PrintStream(new File("adjlists/" + getNetwork().getName()+".adjlist"));
						PrintStream p = new PrintStream(new File("adjlists/" + getNetwork().getName()+"_adjmatrix.dat"));
						//Utils.writeAdjacencyList(getNetwork(), p);
						
						int[][] myAdjMatrix = Utils2.graphToAdjacencyMatrix( getNetwork() );
						Utils2.printMatrix( myAdjMatrix , p );
						
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
						//exportToPos();
				
				}
			});
		}
		return adjlistButton;
	}

	
	public void exportToJpg() {
		
		BufferedImage img = getBrowser().getSnapshot();
	    
	    try {
	       OutputStream out = new FileOutputStream(getNetwork().getName()+".jpg");
	   
	       JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
	       JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(img);
	       param.setQuality(1.0f,true);
	       encoder.setJPEGEncodeParam(param);
	       encoder.encode(img,param);
	       out.close();
	     } catch (Exception e) {
	       System.out.println(e); 
	     }
		
	}
	
	
	public void exportToDot() {
		try {
			PrintStream p = new PrintStream(new File(getNetwork().getName()+".dot"));
			Utils2.graphToDot(getNetwork(), p);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}



	public void exportToPsTricks() {
		try {
			
			String fileName = null;
			if(getNetwork() instanceof TemporalNetwork && ((TemporalNetwork)getNetwork()).getDate() != null){
				DateFormat format = new SimpleDateFormat("yyyy");//DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmm_ss");
				fileName = getNetwork().getName()+ format.format(((TemporalNetwork)getNetwork()).getDate()) + ".tex";
				
			}else{
				fileName = getNetwork().getName()+".tex";
			}
			System.out.println("exporting to " + fileName);
			File f = new File(fileName);
			//f.createNewFile();
			PrintStream p = new PrintStream(f);
			Utils.exportGraphToPSTricks(getNetwork(), p, getBrowser().getNetworkLayout());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void exportToTikz() {
		try {
			
			String fileName = null;
			if(getNetwork() instanceof TemporalNetwork && ((TemporalNetwork)getNetwork()).getDate() != null){
				DateFormat format = new SimpleDateFormat("yyyy");//DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmm_ss");
				fileName = getNetwork().getName()+ format.format(((TemporalNetwork)getNetwork()).getDate()) + ".tex";
				
			}else{
				fileName = getNetwork().getName()+".tex";
			}
			System.out.println("exporting to " + fileName);
			File f = new File(fileName);
			//f.createNewFile();
			TikzExporter tikzexp = new TikzExporter(getNetwork());
			tikzexp.exportToTikz(f, getBrowser().getNetworkLayout());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void exportToPos() {
		try {
			PrintStream p = new PrintStream(getBrowser().getPositionFile());
			Utils2.writePositions(getNetwork(), p, getBrowser().getNetworkLayout());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method initializes edgelistButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getEdgelistButton() {
		if (edgelistButton == null) {
			edgelistButton = new JButton();
			edgelistButton.setText("edge list");
			edgelistButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						PrintStream p = new PrintStream(new File("edgelists/" + getNetwork().getName()+".edgelist"));
						Utils2.writeEdgeList(getNetwork(), p);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					//
				}
			});
		}
		return edgelistButton;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setVisible(false);
		}
		return jTextField;
	}

}  //  @jve:decl-index=0:visual-constraint="0,0"
