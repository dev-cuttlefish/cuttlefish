package ch.ethz.sg.cuttlefish.gui2.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.TikzExporter;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork;
import edu.uci.ics.jung.algorithms.util.IterativeContext;

public class ExportMenu extends AbstractMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3697550568255024207L;
	private JMenuItem toJpeg;
	private JMenuItem toTikz;
	private JFileChooser snapshotFileChooser;
	private JFileChooser  tikzFileChooser;
	
	public ExportMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super(networkPanel, toolbars);
		initialize();
		this.setText("Export");	}

	private void initialize() {
		toJpeg = new JMenuItem("JPG");
		toTikz = new JMenuItem("TikZ");
		
		this.add(toJpeg);
		this.add(toTikz);
		
		toJpeg.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(networkPanel.getNetwork() instanceof InteractiveCxfNetwork) {
					int answer = JOptionPane.showConfirmDialog(getParent(), "Save a snapshot for each frame?");
					if(answer == 0) {
						Thread thread;
						((InteractiveCxfNetwork)networkPanel.getNetwork()).reload();
	                   	thread = new Thread() {
	                   		boolean isRunning;
	                   		@Override
	                   		public void run() {
	                   			JFileChooser fc = getSnapshotFileChooser();		
	    						fc.setSelectedFile(new File(((BrowsableNetwork)networkPanel.getNetwork()).getName()+".jpeg"));
	    						int returnVal = fc.showSaveDialog(null);		
	    			            if (returnVal != JFileChooser.APPROVE_OPTION) return;
	    			            String fileName = fc.getSelectedFile().getName();
                   				int maxIterations = -1;
                   				if(networkPanel.getNetworkLayout() instanceof IterativeContext) {
                   					while (maxIterations <= 0) {
                   						String input = JOptionPane.showInputDialog (getParent(), "The selected layout is interactive, enter maximum layout seconds per frame", "");
                   						try {
                   							maxIterations = Integer.parseInt(input);
                   						} catch(NumberFormatException e) {
                   							maxIterations = -1;
                   							JOptionPane.showMessageDialog(getParent(), "Type an integer greater than 0");
                   						} 
                   					}
                   				}

	                   			isRunning = true;
	                   			int frame = 0;
	                   			while(isRunning){
	                   				frame++;
	                   				exportToJpg(new File(fileName + "_" + frame + ".jpeg"));
	                   				isRunning = ((InteractiveCxfNetwork)networkPanel.getNetwork()).update(200);
	                   				networkPanel.onNetworkChange();
	                   				networkPanel.getNetworkLayout().reset();
	                       			if(networkPanel.getNetworkLayout() instanceof IterativeContext) {	                       				
	                       				int curIteration = 0;
	                       				while(!((IterativeContext)networkPanel.getNetworkLayout()).done() && curIteration < maxIterations) {
	                       					try {
	                       						//sleep for a second and check if the layout has finished
	                       						Thread.sleep(1000);;
	                       					} catch (InterruptedException e) {
	                       						System.out.println("Saving to JPG thread interrupted");
	                       					}
	                       					curIteration++;
	                       				}
	                       			}
	                       			// save the last snapshot
	                       			if(!isRunning) {
	                       				frame++;
		                   				exportToJpg(new File(fileName + "_" + frame + ".jpeg"));
	                       			}
	                   			}
	               				frame++;
	               				exportToJpg(new File(((BrowsableNetwork)networkPanel.getNetwork()).getName()+ "_" + frame + ".jpeg"));
		                   	}
	                   	};
		                thread.start();
						networkPanel.resumeLayout();
						((InteractiveCxfNetwork)networkPanel.getNetwork()).reload();
					} else if(answer == 1) {
						JFileChooser fc = getSnapshotFileChooser();		
						fc.setSelectedFile(new File(((BrowsableNetwork)networkPanel.getNetwork()).getName()+".jpeg"));
						int returnVal = fc.showSaveDialog(null);		
			            if (returnVal == JFileChooser.APPROVE_OPTION) {
			                File file = fc.getSelectedFile();
							exportToJpg(file);
			            }
					} 
				} else {
					JFileChooser fc = getSnapshotFileChooser();		
					fc.setSelectedFile(new File(((BrowsableNetwork)networkPanel.getNetwork()).getName()+".jpeg"));
					int returnVal = fc.showSaveDialog(null);
	
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fc.getSelectedFile();
						exportToJpg(file);
		            }
				}
			}
		});
		
		toTikz.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = getTikzFileChooser();
			     fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
			     fc.setSelectedFile(new File(((BrowsableNetwork)networkPanel.getNetwork()).getName()+".tex"));
			     int returnVal = fc.showSaveDialog(networkPanel);
			     if (returnVal == JFileChooser.APPROVE_OPTION) {
			    	 File file = fc.getSelectedFile();
			    	 exportToTikz(file);
			     }
			}
		});
	}
	
	/**
	 * Export network to TikZ format
	 * @param file the file where the tikz output is stored
	 */
	public void exportToTikz(File file) {
		TikzExporter tikzexp = new TikzExporter(((BrowsableNetwork)networkPanel.getNetwork()));
		tikzexp.exportToTikz(file, networkPanel.getNetworkLayout());
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
	 * Export a file to JPEG format
	 * @param file the file where the JPEG image is stored
	 */
	public void exportToJpg(File file) {
		BufferedImage img = networkPanel.getSnapshot();			    
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

}
