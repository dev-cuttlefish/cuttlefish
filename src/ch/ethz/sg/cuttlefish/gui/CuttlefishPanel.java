
/*

Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

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

package ch.ethz.sg.cuttlefish.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.InstantiateFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.ethz.sg.cuttlefish.gui.mouse.PopupMousePlugin;
import ch.ethz.sg.cuttlefish.gui.widgets.MousePanel;
import ch.ethz.sg.cuttlefish.layout.ARF2Layout;
import ch.ethz.sg.cuttlefish.layout.WeightedARF2Layout;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.EdgeFactory;
import ch.ethz.sg.cuttlefish.misc.Utils2;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.misc.VertexFactory;
import ch.ethz.sg.cuttlefish.misc.XMLUtil;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.*;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;

/**
* Class for the main JPanel of cuttlefish
*/
public class CuttlefishPanel extends JPanel implements ItemListener,INetworkBrowser{

private static final long serialVersionUID = 1L;
//DefaultModalGraphMouse gm = new DefaultModalGraphMouse();  //  @jve:decl-index=0:
private BrowsableNetwork network = null;  //  @jve:decl-index=0:
private Layout<Vertex,Edge> layout = null;
private VisualizationViewer<Vertex,Edge> visualizationViewer = null;
private final ScalingControl scaler = new CrossoverScalingControl();  //  @jve:decl-index=0:
private JTabbedPane menuPane = null;
//private double scale=1.0;  //  @jve:decl-index=0:
private JToggleButton jToggleButton = null;
private JPanel viewPanel = null;
private Hashtable<String, String> arguments = new Hashtable<String, String>();


private Hashtable<String, BrowserWidget> widgetTable = new Hashtable<String, BrowserWidget>();
private ArrayList<BrowserTab> tabArray = new ArrayList<BrowserTab>();  //  @jve:decl-index=0:
private Document configuration;  //  @jve:decl-index=0:
private EditingModalGraphMouse<Vertex,Edge> graphMouse;  //  @jve:decl-index=0:
//private JPanel pickPanel = null;
private JPanel layoutPanel = null;
private JCheckBox layoutCheckBox = null;
private JComboBox layoutComboBox = null;
private JButton writeLayoutButton = null;

private VertexFactory vertexFactory = null;

private EdgeFactory edgeFactory = null;
	
/**
 * This is the default constructor, initializes the rendering and the viewer
 * @param configfile open file with the configuration
 */
public CuttlefishPanel(File configFile) {
	super();
	
	initialize(configFile);
	
	Transformer<Vertex, Shape> vertexShapeTransformer = new Transformer<Vertex, Shape>() {			
		public Shape transform(Vertex vertex) {
			return vertex.getShape();} };
	
	Transformer<Edge, Paint> edgePaintTransformer = new Transformer<Edge, Paint>(){
		public Paint transform(Edge edge) {
			return edge.getColor(); } };

	Transformer<Edge, String> edgeLabelTransformer = new Transformer<Edge, String>(){
		public String transform(Edge edge) {
			return edge.getLabel();} };

	Transformer<Vertex,String> vertexLabelTransformer = new Transformer<Vertex,String>(){
		public String transform(Vertex vertex) {
			return vertex.getLabel(); } };
	
	Transformer<Edge,Stroke> edgeStrokeTransformer = new Transformer<Edge, Stroke>() {
		public Stroke transform(Edge edge) {
			return new BasicStroke(new Double(edge.getWidth()).intValue()); } };
		
	Transformer<Vertex, Stroke> vertexStrokeTransformer = new Transformer<Vertex, Stroke>(){
		public Stroke transform(Vertex vertex) {
			return new BasicStroke(new Double(vertex.getWidth()).intValue()); } };
		
	Transformer<Vertex, Paint> vertexPaintTransformer = new Transformer<Vertex, Paint>(){
		public Paint transform(Vertex vertex) {
			return vertex.getFillColor(); } };			

	Transformer<Vertex, Paint> vertexBorderTransformer = new Transformer<Vertex, Paint>(){
		public Paint transform(Vertex vertex) {
			return vertex.getColor(); } };			

	visualizationViewer.getRenderContext().setVertexShapeTransformer(vertexShapeTransformer);
	visualizationViewer.getRenderContext().setEdgeDrawPaintTransformer(edgePaintTransformer);		
	visualizationViewer.getRenderContext().setEdgeLabelTransformer(edgeLabelTransformer);
	visualizationViewer.getRenderContext().setVertexLabelTransformer(vertexLabelTransformer);
	visualizationViewer.getRenderContext().setLabelOffset(20);
	visualizationViewer.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
	visualizationViewer.getRenderContext().setVertexStrokeTransformer(vertexStrokeTransformer);
	visualizationViewer.getRenderContext().setVertexDrawPaintTransformer(vertexBorderTransformer);
	visualizationViewer.getRenderContext().setVertexFillPaintTransformer(vertexPaintTransformer);
	visualizationViewer.setPickSupport(new ShapePickSupport<Vertex,Edge>(visualizationViewer));
    visualizationViewer.setGraphMouse(graphMouse);
    visualizationViewer.getPickedVertexState().addItemListener(this);
    visualizationViewer.setDoubleBuffered(true);
}



/**
 * This method initializes this, creating the GraphMouse, the DocumentFactory 
 * and loading the information contained in the configuration file
 * @param configFile open file with the configuration
 * @return void
 */
private void initialize(File configFile) {
	 BrowsableNetwork temp = new BrowsableNetwork();
		//temp.setName(name);
	this.setNetwork(temp);	
	vertexFactory = new VertexFactory();
    edgeFactory = new EdgeFactory();
		
	graphMouse = new EditingModalGraphMouse<Vertex,Edge>(visualizationViewer.getRenderContext(),
				vertexFactory, edgeFactory);		
	graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		
    //visualizationViewer.setPickSupport(new ShapePickSupport());
	
    DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
   
    factory.setValidating(false);  
    factory.setNamespaceAware(true);
   // factory.setSchema(arg0)
   // System.out.println(this.getClass().getResource("/ch/ethz/sg/cuttlefish/resources/datasources.xsd"));
   
    Schema schema;
	try {
		 InputStream schemaStream = this.getClass().getResourceAsStream("/ch/ethz/sg/cuttlefish/resources/configuration.xsd");
		 File schemaFile = new File("conf_aux.xsd");
		 OutputStream auxStream = new FileOutputStream(schemaFile);
		 byte buf[]=new byte[1024];
         int len;
         while((len=schemaStream.read(buf))>0)
        	 auxStream.write(buf,0,len);
		 auxStream.close();
		 schemaStream.close();
		 schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaFile);
		 //File schemaFile = new File(this.getClass().getResource("/ch/ethz/sg/cuttlefish/resources/configuration.xsd").getFile());
		 factory.setSchema(schema);
		 schemaFile.deleteOnExit();
	} catch (SAXException e1) {
		System.err.println("Error in XML validation!");
		e1.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  
    
    try {
       DocumentBuilder builder = factory.newDocumentBuilder();
       configuration = builder.parse(configFile);
       Node arguments = configuration.getElementsByTagName("Arguments").item(0);
	      this.arguments = XMLUtil.getArguments(arguments);
          
       NodeList tabs = configuration.getElementsByTagName("Tab");

       for( int i=0; i<tabs.getLength(); i++ ){
    	   	  Node tab = tabs.item(i);
    	      System.out.println( ""+i+": " + tab.getAttributes().getNamedItem("name").getNodeValue() );
    	      BrowserTab panel = new BrowserTab(tab.getAttributes().getNamedItem("condition").getNodeValue());
    	      panel.setBackground(Color.lightGray);
    	      panel.setLayout(new GridBagLayout());
    	      panel.setName(tab.getAttributes().getNamedItem("name").getNodeValue());
    	      NodeList widgets = tab.getChildNodes();
    	      
    	      GridBagConstraints boxConstraints = new GridBagConstraints();
  			  boxConstraints.gridx = 0;
  			  boxConstraints.gridy = 0;
  			  boxConstraints.fill = GridBagConstraints.BOTH;
    	      panel.add(new Box.Filler(new Dimension(0,0), new Dimension(0,125), new Dimension(0,Integer.MAX_VALUE)), boxConstraints);
    	      tabArray.add(panel);
    	      int count = 1;
    	      for(int j=0; j<widgets.getLength(); j++){
    	    	  try{
        	    	  Node widget = widgets.item(j);
        	    	  if(widget.getNodeName().equals("Widget")){
        	    		  
        	    		  GroupPanel groupPanel = new GroupPanel();
        	    		  groupPanel.addItemListener(this);
        	    		  
        	    		  String className = widget.getAttributes().getNamedItem("class").getNodeValue();
        	    		  Class<?> clazz = Class.forName(className);
        	    		  BrowserWidget browserWidget = (BrowserWidget) clazz.newInstance();
        	  			 
        	    		  groupPanel.setLabel(widget.getAttributes().getNamedItem("name").getNodeValue());
        	  			  groupPanel.setBrowserWidget(browserWidget, widget.getAttributes().getNamedItem("id").getNodeValue());
        	  			  
        	  			  browserWidget.setBrowser(this);
        	  			  browserWidget.setArguments(XMLUtil.getArguments(widget));
        	  			  
        	  			  GridBagConstraints gridBagConstraints = new GridBagConstraints();
        	  			  gridBagConstraints.gridx = count++;
        	  			  gridBagConstraints.fill = GridBagConstraints.BOTH;
        	  			  gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        	  			  gridBagConstraints.gridy = 0;
        	  			  
        	  			  panel.add(groupPanel, gridBagConstraints);
        	  			  widgetTable.put(widget.getAttributes().getNamedItem("id").getNodeValue(), browserWidget);
        	  			  browserWidget.init();
        	    	  }
    	    	  }catch(Exception e){
    	    		  
    	    		  e.printStackTrace();
    	    		  
    	    	  }
   	    	  
    	      }

       }
       
	      
    } catch (Exception e) {
      System.err.println("Error in the configuration file!");
		  System.err.println(e);
		  System.exit(-1);
    }
    this.setNetwork(temp);

    
	this.setLayout(new BorderLayout());
	this.setSize(1096, 200);
	this.setBackground(Color.gray);
	
	this.add(getVisualizationViewer(), BorderLayout.CENTER);
	
	this.add(getMenuPane(), BorderLayout.NORTH);
	this.add(getViewPanel(), BorderLayout.SOUTH);
}

public EditingModalGraphMouse<Vertex, Edge> getMouse(){
	return graphMouse;
}
/**
 * Getter for JUNG's VisualizationViewer, creating it if it does not exist
 * @return VisualizationViewer	
 */
private VisualizationViewer<Vertex,Edge> getVisualizationViewer() {
	if (visualizationViewer == null) {
		visualizationViewer = new VisualizationViewer<Vertex,Edge>(layout);
		//visualizationViewer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		visualizationViewer.setBackground(Color.white);
		visualizationViewer.setGraphMouse(graphMouse);
	}
	return visualizationViewer;
}

/**
 * Gives the position file of the argument list
 * @return File object with the position file
 */
public File getPositionFile(){
	String base = getArgument("layoutFolder");
	File positionData = new File((base == null ? "" : base) + network.getName()+".pos");
	return positionData;
}

/**
 * Starts a dynamic or a static layout for the network
 * @param isDynamic boolean to determine the dynamics of the network, 
 * with ARF or a fixed layout
 * @return void
 */
public void setLayout(boolean isDynamic){
	
	File positionData = getPositionFile();
	layout = null;
	
	if(!isDynamic){
		try{
			Layout<Vertex,Edge> fixed = new CircleLayout<Vertex,Edge>(network);
			layout = fixed;
			getLayoutCheckBox().setSelected(false);
		}catch(Exception e){
			System.out.println("Position file missing for network " +  network.getName() + " ("+positionData+")");
		}
	}
	
	if(layout == null){
		Layout<Vertex, Edge> arf = new SpringLayout<Vertex, Edge>(network);
		//arf.setForceCutoff(100);
		//arf.setAttraction(0.15);
		layout = arf;
		getLayoutCheckBox().setSelected(true);
	}
	System.out.println("Set layout to " + layout.getClass());
	
	getVisualizationViewer().repaint();
	getVisualizationViewer().setGraphLayout(layout);
	//System.out.println("VV restarted");
}

/**
 * Setter for the network subject of cuttlefish
 * @param network Network object to use in cuttlefish
 * @return void
 */
public void setNetwork(BrowsableNetwork network) {
	this.network = network;
	System.out.println("Set network " + network.getName() + " (" + network.getVertices().size() + " nodes)");
	
	if (layout == null)
		setLayout("ARFLayout");
	else if (layout.getGraph() != network)
		layout.setGraph(network);
	
	if (layout instanceof ARF2Layout)
		if (((ARF2Layout<Vertex,Edge>)layout).getMaxUpdates() < getNetwork().getVertexCount())
			((ARF2Layout<Vertex,Edge>)layout).setMaxUpdates(getNetwork().getVertexCount());		
	network.init();
	
	refreshAnnotations();
	updateWidgets();
}



/**
 * Updates the network information of all the widgets in the list
 * @return void
 */
private void updateWidgets() {
	for(BrowserWidget widget: widgetTable.values()){
		System.out.println("updating " + widget.getId());
		widget.setNetwork(network);
		
	}
	
	getMenuPane().removeAll();
	for(BrowserTab tab: tabArray){
		if(tab.checkCondition(network)){
			getMenuPane().add(tab.getName(), tab);
		}
	}
}



public void itemStateChanged(ItemEvent arg0) {
	refreshAnnotations();
}

/**
 * Getter for the network
 * @return DirectedSparseGraph network in use in CuttleFish
 */
public SparseGraph<Vertex,Edge> getNetwork() {
	return network;
}

/**
 * This method initializes menuPane	
 * 	
 * @return javax.swing.JTabbedPane	
 */
private JTabbedPane getMenuPane() {
	if (menuPane == null) {
		menuPane = new JTabbedPane();
		menuPane.setBackground(Color.lightGray);
		menuPane.setForeground(Color.black);
	}
	return menuPane;
}

/**
 * This method initializes jToggleButton	
 * 	
 * @return javax.swing.JToggleButton	
 */
private JToggleButton getJToggleButton() {
	if (jToggleButton == null) {
		jToggleButton = new JToggleButton();
		jToggleButton.setText("tools");
		jToggleButton.setSelected(true);
		jToggleButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				menuPane.setVisible(jToggleButton.isSelected());
			}
		});
	}
	return jToggleButton;
}

/**
 * This method initializes viewPanel	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getViewPanel() {
	if (viewPanel == null) {
		viewPanel = new JPanel();
		viewPanel.setLayout(new BoxLayout(getViewPanel(), BoxLayout.X_AXIS));
		viewPanel.setBackground(Color.DARK_GRAY);
		viewPanel.add(getJToggleButton(), null);
		viewPanel.add(getLayoutPanel(), null);
		//viewPanel.add(getPickPanel(), null);
		//graphMouse.getModeComboBox().setPreferredSize(new Dimension());
		viewPanel.add(graphMouse.getModeComboBox());
		
		
	}
	return viewPanel;
}

public Layout<Vertex,Edge> getNetworkLayout() {
	return layout;
}


public void repaintViewer() {
	visualizationViewer.repaint();
	
}

public void refreshAnnotations() {
	
	//network.colorAll(Color.DARK_GRAY);
	for(BrowserWidget widget: widgetTable.values()){
		if((!widget.isClickable() || widget.isActive())&&widget.getNetwork()!=null){
			widget.updateAnnotations();
		}
	}
	network.updateAnnotations();
	network.applyShadows();
	System.gc();
	visualizationViewer.repaint();
}


public Document getConfiguration() {
	return configuration;
}

public void onNetworkChange() {
	System.out.println("Network changed " + network.getName());
	
	if (layout instanceof ARF2Layout)
	{
		((ARF2Layout)layout).step();
		((ARF2Layout)layout).resetUpdates();
		getVisualizationViewer().repaint();
	}
	if (layout instanceof WeightedARF2Layout)
	{
		((WeightedARF2Layout)layout).step();
//		((WeightedARF2Layout)layout).resetUpdates();
		getVisualizationViewer().repaint();
	}
/*	else
	{
		layout.setGraph(getNetwork());
		layout.initialize();
		getVisualizationViewer().setGraphLayout(layout);
		getVisualizationViewer().repaint();
	}
	**/
}

public String getArgument(String name) {
	return arguments.get(name);
}

/**
 * This method initializes pickPanel	
 * 	
 * @return javax.swing.JPanel	
 */
/*	private JPanel getPickPanel() {
	if (pickPanel == null) {
		pickPanel = new JPanel();
		pickPanel.setLayout(new GridBagLayout());
		pickPanel.add(graphMouse.getModeComboBox());
		pickPanel.setBackground(Color.GRAY);
	}
	return pickPanel;
}*/



/**
 * This method initializes layoutPanel	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getLayoutPanel() {
	if (layoutPanel == null) {
		layoutPanel = new JPanel();
		layoutPanel.setLayout(new GridBagLayout());
	//	layoutPanel.add(getLayoutCheckBox(), new GridBagConstraints());
		layoutPanel.add(getLayoutComboBox(), new GridBagConstraints());
		layoutPanel.setBackground(Color.gray);
		layoutPanel.add(getWriteLayoutButton(), new GridBagConstraints());
	}
	return layoutPanel;
}



/**
 * This method initializes layoutComboBox	
 * 	
 * @return javax.swing.JComboBox	
 */
private JComboBox getLayoutComboBox() {
	
	String[] layoutNames = {"ARFLayout", "ResetARF", "WeightedARFLayout", "SpringLayout", "Kamada-Kawai", 
			"Fruchterman-Reingold", "ISOMLayout", "CircleLayout"};
	
	
	if (layoutComboBox == null) {
		layoutComboBox = new JComboBox(layoutNames);
		layoutComboBox.setName("Layout");
		layoutComboBox.setBackground(Color.gray);
		layoutComboBox.setForeground(Color.orange);
		layoutComboBox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if ((String)layoutComboBox.getSelectedItem() == "ResetARF")
					setLayout("ARFLayout");	
				else
					setLayout((String)layoutComboBox.getSelectedItem());
			}
			
		});
	}
	return layoutComboBox;
}



/**
 * This method initializes layoutCheckBox	
 * 	
 * @return javax.swing.JCheckBox	
 */
private JCheckBox getLayoutCheckBox() {
	if (layoutCheckBox == null) {
		layoutCheckBox = new JCheckBox();
		layoutCheckBox.setText("dynamic layout");
		layoutCheckBox.setBackground(Color.gray);
		layoutCheckBox.setForeground(Color.orange);
		layoutCheckBox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if (layoutCheckBox.isSelected())
					setLayout("SpringLayout");
				else
					setLayout("CircleLayout");					
			}
			
		});
	}
	return layoutCheckBox;
}



public void resumeLayout() {
//	visualizationViewer.stop();	
}



public void stopLayout() {
//	visualizationViewer.restart();	
}



/**
 * This method initializes writeLayoutButton	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getWriteLayoutButton() {
	if (writeLayoutButton == null) {
		writeLayoutButton = new JButton();
		writeLayoutButton.setText("Save Layout");
		writeLayoutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try {
					PrintStream p = new PrintStream(getPositionFile());
					Utils2.writePositions(getNetwork(), p, getNetworkLayout());
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	return writeLayoutButton;
}



/**
 * Starts a dynamic or a static layout for the network
 * @param isDynamic boolean to determine the dynamics of the network, 
 * with ARF or a fixed layout
 * @return void
 */
@SuppressWarnings("unchecked")
public void setLayout(String selectedLayout){
	
	//File positionData = getPositionFile(); 
	// TODO: create static layout with position file data

	Layout<Vertex,Edge> newLayout = null;
	
	if (selectedLayout.equals("ARFLayout"))
	{	
		newLayout = new ARF2Layout<Vertex,Edge>(getNetwork(), ((BrowsableNetwork)getNetwork()).isIncremental(),layout);
		if (((ARF2Layout<Vertex,Edge>)newLayout).getMaxUpdates() < getNetwork().getVertexCount())
			((ARF2Layout<Vertex,Edge>)newLayout).setMaxUpdates(getNetwork().getVertexCount());		
	}
	if (selectedLayout.equals("WeightedARFLayout"))
	{
		newLayout = new WeightedARF2Layout<Vertex,Edge>(getNetwork(), ((BrowsableNetwork)getNetwork()).isIncremental(),layout);
		if (((WeightedARF2Layout<Vertex,Edge>)newLayout).getMaxUpdates() < getNetwork().getVertexCount())
			((WeightedARF2Layout<Vertex,Edge>)newLayout).setMaxUpdates(getNetwork().getVertexCount());		
	}
	if (selectedLayout.equals("SpringLayout"))
		newLayout = new SpringLayout2<Vertex, Edge>(getNetwork());
	if (selectedLayout.equals("Kamada-Kawai"))
		newLayout = new KKLayout<Vertex, Edge>(getNetwork());
	if (selectedLayout.equals("Fruchterman-Reingold"))
		newLayout = new FRLayout2<Vertex, Edge>(getNetwork());
	if (selectedLayout.equals("ISOMLayout"))
		newLayout = new ISOMLayout<Vertex, Edge>(getNetwork());
	if (selectedLayout.equals("CircleLayout")){
		newLayout = new CircleLayout<Vertex, Edge>(getNetwork());
		((CircleLayout)newLayout).setRadius(getNetwork().getVertexCount() * 10);
	}
	layout = newLayout;
	System.out.println("Set layout to " + layout.getClass());

	getVisualizationViewer().setGraphLayout(layout);
	
	getVisualizationViewer().repaint();
	
	
	//System.out.println("VV restarted");
}



@Override
public BufferedImage getSnapshot() {
	
	 Dimension size = visualizationViewer.getSize();
     BufferedImage img = new BufferedImage(size.width, size.height,
       BufferedImage.TYPE_INT_RGB);
	 
     Graphics2D g2 = img.createGraphics();
	 visualizationViewer.paint(g2);
	 return img;

}


}  //  @jve:decl-index=0:visual-constraint="10,10"
