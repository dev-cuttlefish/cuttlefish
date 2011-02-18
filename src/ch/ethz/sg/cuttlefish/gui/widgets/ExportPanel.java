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
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

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
	private JButton adjlistButton = null;
	private JButton edgelistButton = null;
	private JButton snapshotButton = null;
	private JButton tikzButton = null;
	private JButton cxfButton = null;
	private JFileChooser cxfFileChooser = null;
	private JFileChooser tikzFileChooser = null;
	private JFileChooser snapshotFileChooser = null;
	private JFileChooser datFileChooser = null;

	/**
	 * Default constructor
	 */
	public ExportPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes the Export widget.
	 * 
	 * @return void
	 */
	private void initialize() {
		
		GridBagConstraints gridBagConstraintsTop1 = new GridBagConstraints();
		gridBagConstraintsTop1.gridx = 0;
		gridBagConstraintsTop1.gridy = 0;
		gridBagConstraintsTop1.fill = GridBagConstraints.BOTH;
		gridBagConstraintsTop1.insets = new Insets(2, 2, 2, 2);
				
		GridBagConstraints gridBagConstraintsTop2 = new GridBagConstraints();
		gridBagConstraintsTop2.gridx = 1;
		gridBagConstraintsTop2.gridy = 0;
		gridBagConstraintsTop2.fill = GridBagConstraints.BOTH;
		gridBagConstraintsTop2.insets = new Insets(2, 2, 2, 2);				
		
		GridBagConstraints gridBagConstraintsTop3 = new GridBagConstraints();
		gridBagConstraintsTop3.gridx = 2;
		gridBagConstraintsTop3.gridy = 0;
		gridBagConstraintsTop3.fill = GridBagConstraints.BOTH;
		gridBagConstraintsTop3.insets = new Insets(2, 2, 2, 2);	
		
		GridBagConstraints gridBagConstraintsBottom1 = new GridBagConstraints();
		gridBagConstraintsBottom1.gridx = 0;
		gridBagConstraintsBottom1.gridy = 1;
		gridBagConstraintsBottom1.fill = GridBagConstraints.BOTH;
		gridBagConstraintsBottom1.insets = new Insets(2, 2, 2, 2);		
		
		
		GridBagConstraints gridBagConstraintsBottom2 = new GridBagConstraints();
		gridBagConstraintsBottom2.gridx = 1;
		gridBagConstraintsBottom2.gridy = 1;
		gridBagConstraintsBottom2.fill = GridBagConstraints.BOTH;
		gridBagConstraintsBottom2.insets = new Insets(2, 2, 2, 2);		
						
		
		this.setSize(287, 230);
		this.setLayout(new GridBagLayout());
				
		this.add(getTikzButton(), gridBagConstraintsBottom1);
		this.add(getSnapshotButton(), gridBagConstraintsBottom2);
		this.add(getCxfButton(),gridBagConstraintsTop1 );
		this.add(getAdjlistButton(), gridBagConstraintsTop2);
		this.add(getEdgelistButton(), gridBagConstraintsTop3);
	}

	/**
	 * This method initializes the Snapshot button	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSnapshotButton() {
		if (snapshotButton == null) {
			snapshotButton = new JButton();
			snapshotButton.setText("Snapshot");
			snapshotButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fc = getSnapshotFileChooser();		
					fc.setSelectedFile(new File(getNetwork().getName()+".jpeg"));
					int returnVal = fc.showSaveDialog(null);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fc.getSelectedFile();
						exportToJpg(file);
		            }
				}
			});
		}
		return snapshotButton;
	}

	/**
	 * This method initializes getCxfButton	
	 * 
	 * @return javax.swing.JButton	
	 */
	private JButton getCxfButton() {
		if (cxfButton == null) {
			cxfButton = new JButton();
			cxfButton.setText("Save network");
			cxfButton.addActionListener(new java.awt.event.ActionListener() {
				
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//The action is opening a dialog and saving in Cxf on the selected file
					JFileChooser fc = getFileChooser();
					fc.setSelectedFile(new File(getNetwork().getName()+".cxf"));
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
		            }
				}
			});
		}
		return cxfButton;
	}
	
	/**
	 * This method initializes the cxfFileChooser for saving
	 * 
	 * @return javax.swing.JFileChooser
	 */
	private JFileChooser getFileChooser() {
		if (cxfFileChooser == null) {
			cxfFileChooser = new JFileChooser();
			cxfFileChooser.setDialogTitle("Saving cuttlefish network...");
			cxfFileChooser.setFileFilter(new FileNameExtensionFilter(".cxf files", "cxf"));
			cxfFileChooser.setCurrentDirectory( new File(System.getProperty("user.dir")));
		}		
		return cxfFileChooser;
	}
	
	/**
	 * This method initializes the file chooser for the Snapshot export button.
	 * 
	 * @return javax.swing.JFileChooser
	 */
	private JFileChooser getSnapshotFileChooser() {
		if (snapshotFileChooser == null) {
			snapshotFileChooser = new JFileChooser();
			snapshotFileChooser.setDialogTitle("Saving cuttlefish network to jpeg...");
			snapshotFileChooser.setFileFilter(new FileNameExtensionFilter(".jpeg files", "jpeg", "jpg"));
			snapshotFileChooser.setCurrentDirectory( new File(System.getProperty("user.dir")));
		}		
		return snapshotFileChooser;
	}
	
	/**
	 * This method initializes the file chooser for the TikZ export button.
	 *  
	 * @return javax.swing.JFileChooser
	 */
	private JFileChooser getTikzFileChooser() {
		if (tikzFileChooser == null) {
			tikzFileChooser = new JFileChooser();
			tikzFileChooser.setDialogTitle("Exporting network to TikZ...");
			tikzFileChooser.setFileFilter(new FileNameExtensionFilter(".tex files", "tex"));
			tikzFileChooser.setCurrentDirectory( new File(System.getProperty("user.dir")));
		}
		return tikzFileChooser;
	}
	
	/**
	 * This method initializes the file chooser for the edge list and
	 * adjacency list export buttons.
	 * 
	 * @return javax.swing.JFileChooser
	 */
	private JFileChooser getDatFileChooser() {
		if(datFileChooser == null) {
			datFileChooser = new JFileChooser();
			datFileChooser.setDialogTitle("Exporting cuttlefish network");
			datFileChooser.setFileFilter(new FileNameExtensionFilter(".dat files", "dat"));
			datFileChooser.setCurrentDirectory( new File(System.getProperty("user.dir")));
		}
		return datFileChooser;	
	}	
	
	/**
	 * This method initializes tikzButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getTikzButton() {
		if (tikzButton == null) {
			tikzButton = new JButton();
			tikzButton.setText("TikZ");
			tikzButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					 JFileChooser fc = getTikzFileChooser();
				     fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
				     fc.setSelectedFile(new File(getNetwork().getName()+".tex"));
				     int returnVal = fc.showSaveDialog(ExportPanel.this);
				     if (returnVal == JFileChooser.APPROVE_OPTION) {
				    	 File file = fc.getSelectedFile();
				    	 exportToTikz(file);
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
			adjlistButton.setText("Adj. matrix");
			adjlistButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						JFileChooser fc = getDatFileChooser();
						fc.setSelectedFile(new File(getNetwork().getName()+".dat"));
						int returnVal = fc.showSaveDialog(ExportPanel.this);
						
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File file = fc.getSelectedFile();
							PrintStream p = new PrintStream(file);						
							int[][] myAdjMatrix = Conversion.graphToAdjacencyMatrix( getNetwork() );
							Conversion.printMatrix( myAdjMatrix , p );
						}											
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

	/**
	 * Export a file to JPEG format
	 * @param file the file where the JPEG image is stored
	 */
	public void exportToJpg(File file) {
		BufferedImage img = getBrowser().getSnapshot();			    
		OutputStream out;
		try {
			out = new FileOutputStream(file);			
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(img);
			param.setQuality(1.0f,true);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(img, param);
			out.close();
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Impossible to save in JPEG");
			fnfEx.printStackTrace();
		} catch (ImageFormatException ifEx) {
			JOptionPane.showMessageDialog(null,ifEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Error creating JPG Snapshot");
			ifEx.printStackTrace();
		} catch (IOException ioEx) {
			JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Output error when saving in JPG");
			ioEx.printStackTrace();
		} catch(NullPointerException nullEx) {
			JOptionPane.showMessageDialog(null,"Error while creating JPEG Encoder." +
					"You are probably using Java OpenJDK which does contain" +
					"the JPEGEncoder libraries.","JPEG Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Error while creating JPEG Encoder");
			nullEx.printStackTrace();
		}
	}
	
	/**
	 * Export network to DOT format.
	 */
	@Deprecated
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

	/**
	 * Export network to PSTricks format.
	 */
	@Deprecated
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
	
	/**
	 * Export network to TikZ format
	 * @param file the file where the tikz output is stored
	 */
	public void exportToTikz(File file) {
		TikzExporter tikzexp = new TikzExporter(getNetwork());
		tikzexp.exportToTikz(file, getBrowser().getNetworkLayout());
	}
	
	@Override
	public void init() {
	}

	/**
	 * This method initializes edgelistButton	
	 * @return javax.swing.JButton	
	 */
	private JButton getEdgelistButton() {
		if (edgelistButton == null) {
			edgelistButton = new JButton();
			edgelistButton.setText("Edge list");
			edgelistButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						JFileChooser fc = getDatFileChooser();
						fc.setSelectedFile(new File(getNetwork().getName()+".dat"));
						int returnVal = fc.showSaveDialog(ExportPanel.this);
						
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File file = fc.getSelectedFile();
							PrintStream p = new PrintStream(file);						
							Conversion.writeEdgeList(getNetwork(), p);
						}											
					} catch (FileNotFoundException fnfEx) {
						JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
						System.err.println("Error trying to write andjacency list file");
						fnfEx.printStackTrace();
					}					
				}
			});
		}
		return edgelistButton;
	}
}
