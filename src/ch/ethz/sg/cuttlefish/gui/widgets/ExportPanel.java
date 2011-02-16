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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;

import com.sun.image.codec.jpeg.*;

import ch.ethz.sg.cuttlefish.misc.CxfSaver;
import ch.ethz.sg.cuttlefish.misc.PSTricksExporter;
import ch.ethz.sg.cuttlefish.misc.TikzExporter;
import ch.ethz.sg.cuttlefish.misc.Conversion;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.TemporalNetwork;


public class ExportPanel extends BrowserWidget  {

	private static final long serialVersionUID = 1L;
	private JButton dotButton = null;
	private JButton pstricksButton = null;
	private JButton adjlistButton = null;
	private JButton edgelistButton = null;
	private JButton snapshotButton = null;
	private JButton tikzButton = null;
	private JButton writeLayoutButton = null;
	private JFileChooser fileC = null;
	private JTextField jTextField = null;
	private JLabel jLabel = null;

	private JFileChooser tikzFileC = null;

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
		
		GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
		gridBagConstraints41.gridx = 2;
		gridBagConstraints41.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints41.fill = GridBagConstraints.BOTH;
		gridBagConstraints41.weightx = 1.0;
		gridBagConstraints41.gridy = 2;
		
		
		this.setSize(287, 230);
		this.setLayout(new GridBagLayout());
		//this.setBorder(BorderFactory.createTitledBorder(null, "export", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		this.add(getSnapshotButton(), gridBagConstraints);
		this.add(getPstricksButton(), gridBagConstraints2);
		this.add(getAdjlistButton(), gridBagConstraints11	);
		this.add(getEdgelistButton(), gridBagConstraints41);
		this.add(getJTextField(), gridBagConstraints21);
		this.add(getTikzButton(), gridBagConstraints4);
		this.add(getWriteLayoutButton(),gridBagConstraints1 );
		
		
		this.add(jLabel, gridBagConstraints3);
	}

	/**
	 * This method initializes dotButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	@SuppressWarnings("unused")
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
	 * This method initializes writeLayoutButton	
	 * @return javax.swing.JButton	
	 */
	private JButton getWriteLayoutButton() {
		if (writeLayoutButton == null) {
			writeLayoutButton = new JButton();
			writeLayoutButton.setText("Save Network");
			writeLayoutButton.addActionListener(new java.awt.event.ActionListener() {
				
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//The action is opening a dialog and saving in Cxf on the selected file
					JFileChooser fc = getFileChooser();
					fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
					int returnVal = fc.showSaveDialog(null);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fc.getSelectedFile();
		                try {
							file.createNewFile();
							CxfSaver saver = new CxfSaver((BrowsableNetwork)getNetwork(), getBrowser().getNetworkLayout());
							saver.save(file);
						} catch (IOException ioEx) {
							JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
							System.err.println("Impossible to write");
							ioEx.printStackTrace();
						}
		            } else {
		                System.out.println("Input cancelled by user");
		            }
				}
			});
		}
		return writeLayoutButton;
	}
	
	/**
	 * This method initializes the fileChooser for saving
	 * @return
	 */
	private JFileChooser getFileChooser() {
		if (fileC == null) {
			fileC = new JFileChooser();
		}
		return fileC;
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
	

	private JFileChooser getTikzFileChooser() {
		if (tikzFileC == null) {
			tikzFileC = new JFileChooser();
		}
		return tikzFileC;
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
					 JFileChooser fc = getTikzFileChooser();
				     fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
				     	fc.setSelectedFile(new File(getNetwork().getName()+".tex"));
			            int returnVal = fc.showSaveDialog(ExportPanel.this);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
			                File file = fc.getSelectedFile();
			                exportToTikz(file);
					    } else {
			                System.out.println("Input cancelled by user");
			            }
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
						
						int[][] myAdjMatrix = Conversion.graphToAdjacencyMatrix( getNetwork() );
						Conversion.printMatrix( myAdjMatrix , p );
						
					} catch (FileNotFoundException fnfEx) {
						JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
						System.err.println("Error trying to write andjacency list file");
						fnfEx.printStackTrace();
					}
				}
			});
		}
		return adjlistButton;
	}

	
	public void exportToJpg() {
		
		BufferedImage img = getBrowser().getSnapshot();
	    
	       OutputStream out;
		try {
			out = new FileOutputStream(getNetwork().getName()+".jpg");
	       JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
	       JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(img);
	       param.setQuality(1.0f,true);
	       encoder.setJPEGEncodeParam(param);
	       encoder.encode(img,param);
	       out.close();
		} 
		catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Impossible to save in JPG");
			fnfEx.printStackTrace();
		} catch (ImageFormatException ifEx) {
			JOptionPane.showMessageDialog(null,ifEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Error creating JPG Snapshot");
			ifEx.printStackTrace();
		} catch (IOException ioEx) {
			JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Output error when saving in JPG");
			ioEx.printStackTrace();
		}
	}
	
	
	public void exportToDot() {
		try {
			PrintStream p = new PrintStream(new File(getNetwork().getName()+".dot"));
			Conversion.graphToDot(getNetwork(), p);
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Impossible export to Dot");
			fnfEx.printStackTrace();
		}
	}



	public void exportToPsTricks() {
		String fileName = null;
		if(getNetwork() instanceof TemporalNetwork && ((TemporalNetwork)getNetwork()).getDate() != null){
			DateFormat format = new SimpleDateFormat("yyyy");//DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmm_ss");
			fileName = getNetwork().getName()+ format.format(((TemporalNetwork)getNetwork()).getDate()) + ".tex";
			
		}else{
			fileName = getNetwork().getName()+".tex";
		}
		System.out.println("exporting to " + fileName);
		File f = new File(fileName);

		PSTricksExporter psExporter = new PSTricksExporter(getNetwork());
		psExporter.exportToPSTricks(f, getBrowser().getNetworkLayout());
	}
	
	public void exportToTikz(File file) {
		TikzExporter tikzexp = new TikzExporter(getNetwork());
		tikzexp.exportToTikz(file, getBrowser().getNetworkLayout());
	}
	
	@Override
	public void init() {
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
						Conversion.writeEdgeList(getNetwork(), p);
					} catch (FileNotFoundException fnfEx) {
						JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
						System.err.println("Impossible to save in edge list");
						fnfEx.printStackTrace();
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
