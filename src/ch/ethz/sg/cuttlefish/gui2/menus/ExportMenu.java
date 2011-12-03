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

package ch.ethz.sg.cuttlefish.gui2.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.collections15.Transformer;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.AppletExporter;
import ch.ethz.sg.cuttlefish.misc.Conversion;
import ch.ethz.sg.cuttlefish.misc.CxfToCmx;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.FileChooser;
import ch.ethz.sg.cuttlefish.misc.SVGExporter;
import ch.ethz.sg.cuttlefish.misc.TikzExporter;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLWriter;

public class ExportMenu extends AbstractMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3697550568255024207L;
	private JMenuItem toJpeg;
	private JMenuItem toTikz;
	private JMenuItem toAdjMatrix;
	private JMenuItem toEdgeList;
	private JMenuItem toSVG;
	private JMenuItem dumpToDB;
	private JMenuItem toCMX;
	private JMenuItem toApplet;
	private JMenuItem toGraphml;
	private JFileChooser snapshotFileChooser;
	private JFileChooser svgFileChooser;
	private JFileChooser appletFileChooser;
	private JFileChooser  tikzFileChooser;
	private JFileChooser datFileChooser;
	private JFileChooser cmxFileChooser;

	
	public ExportMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super(networkPanel, toolbars);
		initialize();
		this.setText("Export");	}

	private void initialize() {		
		toJpeg = new JMenuItem("JPG");
		toTikz = new JMenuItem("TikZ");
		toAdjMatrix = new JMenuItem("Adjacency matrix");
		toEdgeList = new JMenuItem("Edge list");
		dumpToDB = new JMenuItem("Dump to database");
		toCMX = new JMenuItem("Commetrix csv");
		toApplet = new JMenuItem("To Applet");
		toSVG = new JMenuItem("To interactive SVG");
		toGraphml = new JMenuItem("To GraphML");
		this.add(toGraphml);
		this.add(toAdjMatrix);
		this.add(toEdgeList);
		this.addSeparator();
		this.add(toJpeg);
		this.add(toSVG);
		this.add(toTikz);
		this.add(toApplet);		
		this.addSeparator();		
		this.add(dumpToDB);
		this.add(toCMX);

		dumpToDB.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(! (networkPanel.getNetwork() instanceof CxfNetwork) ) {
					JOptionPane.showMessageDialog(networkPanel, "Dump a network to a database supports only CXF networks", "Could not dump the network", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				new DBDump(networkPanel);			
			}
		});
		
		toGraphml.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {							
				GraphMLWriter<Vertex, Edge> out = new GraphMLWriter<Vertex, Edge>();
				
				Map<String, GraphMLMetadata<Vertex> > vertexAttributes = new HashMap<String, GraphMLMetadata<Vertex>>();
				Transformer<Vertex, String> vertexRedColorTransformer = new Transformer<Vertex, String>() {
					@Override
					public String transform(Vertex v) { return Integer.toString(v.getFillColor().getRed()); }					
				};
				Transformer<Vertex, String> vertexGreenColorTransformer = new Transformer<Vertex, String>() {
					@Override
					public String transform(Vertex v) { return Integer.toString(v.getFillColor().getGreen()); }					
				};
				Transformer<Vertex, String> vertexBlueColorTransformer = new Transformer<Vertex, String>() {
					@Override
					public String transform(Vertex v) { return Integer.toString(v.getFillColor().getBlue()); }					
				};
				Transformer<Vertex, String> vertexLabelTransformer = new Transformer<Vertex, String>() {
					@Override
					public String transform(Vertex v) { return v.getLabel(); }					
				};
				Transformer<Vertex, String> vertexXTransformer = new Transformer<Vertex, String>() {
					@Override
					public String transform(Vertex v) { return Double.toString(networkPanel.getNetworkLayout().transform(v).getX()); }					
				};
				Transformer<Vertex, String> vertexYTransformer = new Transformer<Vertex, String>() {
					@Override
					public String transform(Vertex v) { return Double.toString(networkPanel.getNetworkLayout().transform(v).getY()); }					
				};
				Transformer<Vertex, String> vertexSizeTransformer = new Transformer<Vertex, String>() {
					@Override
					public String transform(Vertex v) { return Double.toString(v.getSize()); }					
				};
				GraphMLMetadata<Vertex> vertexRColor = new GraphMLMetadata<Vertex>("red", "0", vertexRedColorTransformer);
				GraphMLMetadata<Vertex> vertexGColor = new GraphMLMetadata<Vertex>("green", "0", vertexGreenColorTransformer);
				GraphMLMetadata<Vertex> vertexBColor = new GraphMLMetadata<Vertex>("blue", "0", vertexBlueColorTransformer);
				GraphMLMetadata<Vertex> vertexLabel = new GraphMLMetadata<Vertex>("label", "", vertexLabelTransformer);
				GraphMLMetadata<Vertex> vertexX = new GraphMLMetadata<Vertex>("xPos", "0", vertexXTransformer);
				GraphMLMetadata<Vertex> vertexY = new GraphMLMetadata<Vertex>("yPos", "0", vertexYTransformer);
				GraphMLMetadata<Vertex> vertexSize = new GraphMLMetadata<Vertex>("size", "10", vertexSizeTransformer);
				vertexAttributes.put("r", vertexRColor);
				vertexAttributes.put("g", vertexGColor);
				vertexAttributes.put("b", vertexBColor);
				vertexAttributes.put("label", vertexLabel);
				vertexAttributes.put("x", vertexX);
				vertexAttributes.put("y", vertexY);
				vertexAttributes.put("size", vertexSize);				
				out.setVertexData(vertexAttributes);
				
				Map<String, GraphMLMetadata<Edge> > edgeAttributes = new HashMap<String, GraphMLMetadata<Edge>>();
				Transformer<Edge, String> edgeRedColorTransformer = new Transformer<Edge, String>() {
					@Override
					public String transform(Edge e) { return Integer.toString(e.getColor().getRed()); }					
				};
				Transformer<Edge, String> edgeGreenColorTransformer = new Transformer<Edge, String>() {
					@Override
					public String transform(Edge e) { return Integer.toString(e.getColor().getGreen()); }					
				};
				Transformer<Edge, String> edgeBlueColorTransformer = new Transformer<Edge, String>() {
					@Override
					public String transform(Edge e) { return Integer.toString(e.getColor().getBlue()); }					
				};
				Transformer<Edge, String> edgeLabelTransformer = new Transformer<Edge, String>() {
					@Override
					public String transform(Edge e) { return e.getLabel(); }					
				};
				Transformer<Edge, String> edgeWeightTransformer = new Transformer<Edge, String>() {
					@Override
					public String transform(Edge e) { return Double.toString(e.getWeight()); }					
				};
				Transformer<Edge, String> edgeWidthTransformer = new Transformer<Edge, String>() {
					@Override
					public String transform(Edge e) { return Double.toString(e.getWidth()); }					
				};
				GraphMLMetadata<Edge> edgeRColor = new GraphMLMetadata<Edge>("red", "0", edgeRedColorTransformer);
				GraphMLMetadata<Edge> edgeGColor = new GraphMLMetadata<Edge>("green", "0", edgeGreenColorTransformer);
				GraphMLMetadata<Edge> edgeBColor = new GraphMLMetadata<Edge>("blue", "0", edgeBlueColorTransformer);
				GraphMLMetadata<Edge> edgeLabel = new GraphMLMetadata<Edge>("label", "", edgeLabelTransformer);
				GraphMLMetadata<Edge> edgeWeight = new GraphMLMetadata<Edge>("weight", "0", edgeWeightTransformer);
				GraphMLMetadata<Edge> edgeWidth = new GraphMLMetadata<Edge>("width", "0", edgeWidthTransformer);
				edgeAttributes.put("r", edgeRColor);
				edgeAttributes.put("g", edgeGColor);
				edgeAttributes.put("b", edgeBColor);
				edgeAttributes.put("label", edgeLabel);
				edgeAttributes.put("weight", edgeWeight);
				edgeAttributes.put("width", edgeWidth);
				out.setEdgeData(edgeAttributes);

				JFileChooser graphmlFileChooser = new FileChooser();
				graphmlFileChooser.setDialogTitle("Exporting cuttlefish network to GraphML");
				graphmlFileChooser.setSelectedFile(new File("network.graphml"));
				graphmlFileChooser.setFileFilter(new FileNameExtensionFilter(".graphml files", "graphml"));
				if(graphmlFileChooser.showSaveDialog(networkPanel) == JFileChooser.APPROVE_OPTION) {
					try {
						out.save(networkPanel.getNetwork(), new FileWriter(graphmlFileChooser.getSelectedFile()));
						
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(networkPanel, "Error while exporting to graphml", "Error exporting", JOptionPane.ERROR_MESSAGE, null);
					}
				}
			}
		});
		
		toSVG.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String svgFilename = "network.xml";
				JFileChooser fc = getSVGFileChooser();
				fc.setSelectedFile(new File(svgFilename));
				int imageHeight = networkPanel.getVisualizationViewer().getHeight();
				int imageWidth = networkPanel.getVisualizationViewer().getWidth();
				int returnVal = fc.showSaveDialog(networkPanel);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					SVGExporter exporter = new SVGExporter(networkPanel.getNetwork(), networkPanel.getNetworkLayout());
					exporter.toSVG(fc.getSelectedFile(), imageHeight, imageWidth);
				}				
			}
		});
		
		toApplet.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String imageFilename = "applet.jpeg";
				JFileChooser fc = getSnapshotFileChooser();
				fc.setSelectedFile(new File("network.jpeg"));
				int returnVal = fc.showSaveDialog(networkPanel);
				int imageHeight = networkPanel.getVisualizationViewer().getHeight();
				int imageWidth = networkPanel.getVisualizationViewer().getWidth();
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					exportToJpg(fc.getSelectedFile());
					imageFilename = fc.getSelectedFile().getName();
				}				
				
				fc = getAppletChooser();
				fc.setSelectedFile(new File("network.html"));
				returnVal = fc.showSaveDialog(networkPanel);
				String networkFilename = "network.html";
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					(new AppletExporter(networkPanel.getNetwork(), networkPanel.getNetworkLayout())).exportToDynamicApplet(fc.getSelectedFile());
					networkFilename = fc.getSelectedFile().getName();
				}
				fc = getAppletChooser();
				fc.setSelectedFile(new File("index.html"));
				returnVal = fc.showSaveDialog(networkPanel);				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					(new AppletExporter(networkPanel.getNetwork(), networkPanel.getNetworkLayout())).exportInitialPage(fc.getSelectedFile(), imageFilename, networkFilename, imageWidth, imageHeight);
				}	
				
			}
		});
		
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
			     fc.setSelectedFile(new File(((BrowsableNetwork)networkPanel.getNetwork()).getName()+".tex"));
			     int returnVal = fc.showSaveDialog(networkPanel);
			     if (returnVal == JFileChooser.APPROVE_OPTION) {
			    	 File file = fc.getSelectedFile();
			    	 exportToTikz(file);
			     }
			}
		});
		
		toCMX.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(! (networkPanel.getNetwork() instanceof CxfNetwork) ) {
					JOptionPane.showMessageDialog(networkPanel, "Exporting to Commetrix csv files supports only CXF networks", "Could not export network", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				new Thread(new Runnable() {	public void run() {						
					JFileChooser fc = getCMXFileChooser();
					fc.setSelectedFile(new File(((BrowsableNetwork)networkPanel.getNetwork()).getName()));
					int returnVal = fc.showSaveDialog(networkPanel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String baseFilename = fc.getSelectedFile().getAbsolutePath();							
						CxfToCmx.cxfToCmx((CxfNetwork)networkPanel.getNetwork(), new File(baseFilename + ".linkevent.csv"), new File(baseFilename + ".linkparent.csv"), new File(baseFilename + ".linkrecipient.csv"), new File(baseFilename + ".linksender.csv"), new File(baseFilename + ".node.csv"));
					}					
				} }).start();
			};
		});
		
		toAdjMatrix.addActionListener( new ActionListener() {				
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser fc = getDatFileChooser();
					fc.setSelectedFile(new File(((BrowsableNetwork)networkPanel.getNetwork()).getName()+".dat"));
					int returnVal = fc.showSaveDialog(networkPanel);
					
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						PrintStream p = new PrintStream(file);						
						int[][] myAdjMatrix = Conversion.graphToAdjacencyMatrix( networkPanel.getNetwork() );
						Conversion.printMatrix( myAdjMatrix , p );
					}											
				} catch (FileNotFoundException fnfEx) {
					JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					System.err.println("Error trying to write andjacency list file");
					fnfEx.printStackTrace();
				}
			}				
		});
		
		toEdgeList.addActionListener( new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser fc = getDatFileChooser();
					fc.setSelectedFile(new File(((BrowsableNetwork)networkPanel.getNetwork()).getName()+".dat"));
					int returnVal = fc.showSaveDialog(networkPanel);
					
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						PrintStream p = new PrintStream(file);						
						Conversion.writeEdgeList(networkPanel.getNetwork(), p);
					}											
				} catch (FileNotFoundException fnfEx) {
					JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					System.err.println("Error trying to write andjacency list file");
					fnfEx.printStackTrace();
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
		snapshotFileChooser = new FileChooser();
		snapshotFileChooser.setDialogTitle("Saving cuttlefish network to jpeg...");
		snapshotFileChooser.setFileFilter(new FileNameExtensionFilter(".jpeg files", "jpeg", "jpg"));		
		return snapshotFileChooser;
	}
	
	/**
	 * This method initializes the file chooser for the SVG export button.
	 * 
	 * @return javax.swing.JFileChooser
	 */
	private JFileChooser getSVGFileChooser() {
		svgFileChooser = new FileChooser();
		svgFileChooser.setDialogTitle("Saving cuttlefish network to interactive SVG...");
		svgFileChooser.setFileFilter(new FileNameExtensionFilter(".xml files", "xml"));		
		return svgFileChooser;
	}
	
	/**
	 * This method initializes the file chooser for the Applet export button.
	 * 
	 * @return javax.swing.JFileChooser
	 */
	private JFileChooser getAppletChooser() {
		appletFileChooser = new FileChooser();
		appletFileChooser.setDialogTitle("Exporting network to an applet...");
		appletFileChooser.setFileFilter(new FileNameExtensionFilter(".html files", "html", "html"));		
		return appletFileChooser;
	}
	
	/**
	 * This method initializes the file chooser for the TikZ export button.
	 *  
	 * @return javax.swing.JFileChooser
	 */
	private JFileChooser getTikzFileChooser() {
		tikzFileChooser = new FileChooser();
		tikzFileChooser.setDialogTitle("Exporting network to TikZ...");
		tikzFileChooser.setFileFilter(new FileNameExtensionFilter(".tex files", "tex"));
		return tikzFileChooser;
	}
	
	/**
	 * This method initializes the file chooser for the edge list and
	 * adjacency list export buttons.
	 * 
	 * @return javax.swing.JFileChooser
	 */
	private JFileChooser getDatFileChooser() {
		datFileChooser = new FileChooser();
		datFileChooser.setDialogTitle("Exporting cuttlefish network");
		datFileChooser.setFileFilter(new FileNameExtensionFilter(".dat files", "dat"));
		return datFileChooser;	
	}
	
	/**
	 * This method initializes the file chooser for the CMX export button.
	 * 
	 * @return javax.swing.JFileChooser
	 */
	private JFileChooser getCMXFileChooser() {
		cmxFileChooser = new FileChooser();
		cmxFileChooser.setDialogTitle("Exporting to Commetrix csv files");
		cmxFileChooser.setFileFilter(new FileNameExtensionFilter(".csv files", "csv"));
		return cmxFileChooser;	
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
