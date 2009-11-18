
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
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.ethz.sg.cuttlefish.layout.ARF2Layout;
import ch.ethz.sg.cuttlefish.layout.FixedLayout;
import ch.ethz.sg.cuttlefish.layout.WeightedARF2Layout;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.EdgeFactory;
import ch.ethz.sg.cuttlefish.misc.Utils;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.misc.VertexFactory;
import ch.ethz.sg.cuttlefish.misc.XMLUtil;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.Context;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;

/**
* Class for the main JPanel of cuttlefish where the network and tabs are displayed.
*/
public class CuttlefishPanel extends JPanel implements ItemListener,INetworkBrowser{

private static final long serialVersionUID = 1L;

/*Network and layout data*/
private BrowsableNetwork network = null;
private Layout<Vertex,Edge> layout = null;
private String layoutType = null;
private VisualizationViewer<Vertex,Edge> visualizationViewer = null;

/*User interface*/
private JTabbedPane menuPane = null;
private JToggleButton jToggleButton = null;
private LayoutPanel layoutPanel = null;

private JPanel viewPanel = null;
private EditingModalGraphMouse<Vertex,Edge> graphMouse;  

/*Configuration*/
private Hashtable<String, String> arguments = new Hashtable<String, String>();
private Hashtable<String, BrowserWidget> widgetTable = new Hashtable<String, BrowserWidget>();
private ArrayList<BrowserTab> tabArray = new ArrayList<BrowserTab>(); 
private Document configuration; 

/*Factories*/
private VertexFactory vertexFactory = null;
private EdgeFactory edgeFactory = null;
	
/**
 * This is the default constructor, initializes the view with the configuration read from the file
 * @param configfile open file with the configuration
 */
public CuttlefishPanel(File configFile) {
	super();
	initialize(configFile);
	
	
	RenderContext<Vertex,Edge> renderContext = visualizationViewer.getRenderContext();		

	/*Vertex rendering*/
	Transformer<Vertex, Shape> vertexShapeTransformer = new Transformer<Vertex, Shape>() {			
		public Shape transform(Vertex vertex) {
			return vertex.getShape();} };
	Transformer<Vertex,String> vertexLabelTransformer = new Transformer<Vertex,String>(){
		public String transform(Vertex vertex) {
		
			if (network instanceof CxfNetwork)
				if (((CxfNetwork)network).hideVertexLabels())
					return null;
			return vertex.getLabel(); 
			
		} };
	Transformer<Vertex, Stroke> vertexStrokeTransformer = new Transformer<Vertex, Stroke>(){
		public Stroke transform(Vertex vertex) {
			return new BasicStroke(new Double(vertex.getWidth()).intValue()); } };
	Transformer<Vertex, Paint> vertexPaintTransformer = new Transformer<Vertex, Paint>(){
		public Paint transform(Vertex vertex) {
			return vertex.getFillColor(); } };
	Transformer<Vertex, Paint> vertexBorderTransformer = new Transformer<Vertex, Paint>(){
		public Paint transform(Vertex vertex) {
			return vertex.getColor(); } };		
    // This predicate selects which vertices to ignore		
	Predicate<Context<Graph<Vertex,Edge>,Vertex>> vertexIncludePredicate = new Predicate<Context<Graph<Vertex,Edge>,Vertex>>() {
		public boolean evaluate(Context<Graph<Vertex, Edge>, Vertex> context) {
			Vertex vertex = context.element;
			return !vertex.isExcluded(); } };
	

	//vertex label, shape and border width
	renderContext.setVertexShapeTransformer(vertexShapeTransformer);
	renderContext.setVertexLabelTransformer(vertexLabelTransformer);
	renderContext.setVertexDrawPaintTransformer(vertexBorderTransformer);
	renderContext.setVertexIncludePredicate(vertexIncludePredicate);
	renderContext.setLabelOffset(20); //shift of the vertex label to center the first character under the vertex

	//vertex colors
	renderContext.setVertexStrokeTransformer(vertexStrokeTransformer);
	renderContext.setVertexFillPaintTransformer(vertexPaintTransformer);
			
	/* edge rendering */	
	Transformer<Edge, Paint> edgePaintTransformer = new Transformer<Edge, Paint>(){
		public Paint transform(Edge edge) {
			return edge.getColor(); } };
	Transformer<Edge, String> edgeLabelTransformer = new Transformer<Edge, String>(){
		public String transform(Edge edge) {
			if (network instanceof CxfNetwork)
				if (((CxfNetwork)network).hideEdgeLabels())
					return null;
			return edge.getLabel();} };
	Transformer<Edge,Stroke> edgeStrokeTransformer = new Transformer<Edge, Stroke>() {
		public Stroke transform(Edge edge) {
			return new BasicStroke(new Double(edge.getWidth()).intValue()); } };
	// This predicate selects which edges to ignore
	Predicate<Context<Graph<Vertex,Edge>,Edge>> edgeIncludePredicate = new Predicate<Context<Graph<Vertex,Edge>,Edge>>() {
		public boolean evaluate(Context<Graph<Vertex, Edge>, Edge> context) {
			Edge edge = context.element;
			return !edge.isExcluded(); } };
	
	renderContext.setEdgeDrawPaintTransformer(edgePaintTransformer);	
	renderContext.setArrowFillPaintTransformer(edgePaintTransformer);	
	renderContext.setArrowDrawPaintTransformer(edgePaintTransformer); //arrows are of the same color as the edge
	renderContext.setEdgeLabelTransformer(edgeLabelTransformer);
	renderContext.setEdgeStrokeTransformer(edgeStrokeTransformer);
	renderContext.setEdgeIncludePredicate(edgeIncludePredicate);
	
	/*mouse settings*/
	visualizationViewer.setPickSupport(new ShapePickSupport<Vertex,Edge>(visualizationViewer));
    visualizationViewer.setGraphMouse(graphMouse);
    visualizationViewer.getPickedVertexState().addItemListener(this);
    visualizationViewer.setDoubleBuffered(true);
}

/**
 * This method initializes the CuttlefishPanel, creating the GraphMouse, the DocumentFactory 
 * and loadlensColoring the information contained in the configuration file
 * @param configFile open file with the configuration
 * @return void
 */
private void initialize(File configFile) {
	BrowsableNetwork temp = new BrowsableNetwork();
	this.setNetwork(temp);	
	vertexFactory = new VertexFactory();
    edgeFactory = new EdgeFactory();
		
	graphMouse = new EditingModalGraphMouse<Vertex,Edge>(visualizationViewer.getRenderContext(),
				vertexFactory, edgeFactory);		
	graphMouse.getAnnotatingPlugin().setAnnotationColor(getForeground());
	
	//starting on transforming mode, the most used
	graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
	
    DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
   
    factory.setValidating(false);  
    factory.setNamespaceAware(true);
   
    Schema schema;
	try {
		 /*We open configuration.xsd as a stream associated to the .jar and we create a local copy conf_aux.xsd*/
		 /*TODO: make file paths consistent across operating systems: this does not work in windows*/
		 File schemaFile = Utils.createLocalFile("/ch/ethz/sg/cuttlefish/resources/configuration.xsd", (Object) this);
		 schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaFile);
		 factory.setSchema(schema);
		 
	} catch (SAXException saxEx) {
		JOptionPane.showMessageDialog(null,saxEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		System.err.println("Error in XML validation!");
		saxEx.printStackTrace();
	} 
 
       DocumentBuilder builder;
	try {
	   builder = factory.newDocumentBuilder();
       configuration = builder.parse(configFile);
       Node arguments = configuration.getElementsByTagName("Arguments").item(0);
       this.arguments = XMLUtil.getArguments(arguments);
       
       /*Iterate through the tabs, creating them*/
       NodeList tabs = configuration.getElementsByTagName("Tab");

       for( int i=0; i<tabs.getLength(); i++ ){
    	   	  Node tab = tabs.item(i);
    	      System.out.println( ""+i+": " + tab.getAttributes().getNamedItem("name").getNodeValue() );
    	      BrowserTab panel = new BrowserTab(tab.getAttributes().getNamedItem("condition").getNodeValue());
    	      panel.setBackground(Color.lightGray);
    	      panel.setLayout(new GridBagLayout());
    	      panel.setName(tab.getAttributes().getNamedItem("name").getNodeValue());
    	      
    	      GridBagConstraints boxConstraints = new GridBagConstraints();
  			  boxConstraints.gridx = 0;
  			  boxConstraints.gridy = 0;
  			  boxConstraints.fill = GridBagConstraints.BOTH;
    	      panel.add(new Box.Filler(new Dimension(0,0), new Dimension(0,125), new Dimension(0,Integer.MAX_VALUE)), boxConstraints);
    	      tabArray.add(panel);
    	      int count = 1;
    	      
    	      /*Iterate through the widgets of the current created tab*/
      	      NodeList widgets = tab.getChildNodes();
      	      for(int j=0; j<widgets.getLength(); j++){
    	    	 	  Node widget = widgets.item(j);
        	    	  if(widget.getNodeName().equals("Widget")){
        	    		  
        	    		  GroupPanel groupPanel = new GroupPanel();
        	    		  groupPanel.addItemListener(this);
        	    		  
        	    		  String className = widget.getAttributes().getNamedItem("class").getNodeValue();
        	    		  Class<?> clazz;
        	    		  
        	    		  try {
        	    			  clazz = Class.forName(className);
        	    		 
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
						} catch (ClassNotFoundException classEx) {
							JOptionPane.showMessageDialog(null,classEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
							System.err.println("Widget class in XML does not exist");
							classEx.printStackTrace();
						} catch (Exception instEx /*actually an InstantiationException and IllegalAcessException*/) {
							JOptionPane.showMessageDialog(null,instEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
							System.err.println("Error instantiating widget");
							instEx.printStackTrace();
						} 
        	    	  }
    	      }
       }
	} catch (ParserConfigurationException parsEx) {
		JOptionPane.showMessageDialog(null,parsEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		System.err.println("Parser syntax error in configuration XML");
		parsEx.printStackTrace();
	} catch (SAXException saxEx) {
		JOptionPane.showMessageDialog(null,saxEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		System.err.println("SAX syntax error in configuration XML");
		saxEx.printStackTrace();
	} catch (IOException ioEx) {
		JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		System.err.println("Input syntax error in configuration XML");
		ioEx.printStackTrace();
	}

	this.setNetwork(temp);

    
	this.setLayout(new BorderLayout());
	this.setSize(1096, 200);
	this.setBackground(Color.gray);
	
	this.add(getVisualizationViewer(), BorderLayout.CENTER);
	
	this.add(getMenuPane(), BorderLayout.NORTH);
	this.add(getViewPanel(), BorderLayout.SOUTH);
}

/**
 * Getter for the graph mouse associated to the panel
 * @return EditingModalGraphMouse automatically created by JUNG2.0
 */
public EditingModalGraphMouse<Vertex, Edge> getMouse(){
	return graphMouse;
}
/**
 * Getter for JUNG's VisualizationViewer, creating it if it does not exist
 * @return VisualizationViewer	
 */
private VisualizationViewer<Vertex,Edge> getVisualizationViewer() {
	//Create it if it didn't exist before
	if (visualizationViewer == null) {
		visualizationViewer = new VisualizationViewer<Vertex,Edge>(layout);
		visualizationViewer.setBackground(Color.white);
		visualizationViewer.setGraphMouse(graphMouse);
	}
	return visualizationViewer;
}

/**
 * Setter for the network subject of cuttlefish
 * @param network Network object to use in cuttlefish
 * @return void
 */
@SuppressWarnings("unchecked")
public void setNetwork(BrowsableNetwork network) {
	this.network = network;
	System.out.println("Set network " + network.getName() + " (" + network.getVertices().size() + " nodes)");
	
	/*default layout: ARF*/
	if (layout == null)
		setLayout("ARFLayout");
	else if (layout.getGraph() != network) //consistency check between layout and network
		layout.setGraph(network);

	//maxUpdates in ARF2Layout depends on the network size, this way it's updated if the network changes
	if ((layout instanceof ARF2Layout) && (((ARF2Layout<Vertex,Edge>)layout).getMaxUpdates() < getNetwork().getVertexCount()))
			((ARF2Layout<Vertex,Edge>)layout).setMaxUpdates(getNetwork().getVertexCount());		
	
	network.init();
	refreshAnnotations();
	updateWidgets();
}



/**
 * Updates the network information of all the widgets in the list and the affected tabs
 * @return void
 */
private void updateWidgets() {
	for(BrowserWidget widget: widgetTable.values())
		widget.setNetwork(network);
	
	getMenuPane().removeAll();
	for(BrowserTab tab: tabArray){
		if(tab.checkCondition(network)){
			getMenuPane().add(tab.getName(), tab);
		}
	}
}

/**
 * updates the annotations of the widgets in case of event
 * @return void
 */
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
 * This method initializes and gets the menu panel	
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
 * This method initializes and gets jToggleButton	
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
 * This method initializes and gets the viewPanel	
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
		viewPanel.add(graphMouse.getModeComboBox());
	}
	return viewPanel;
}

/**
 * Getter for the network layout to be used in widgets through the interface
 * @return Layout<Vertex, Edge> general layout being used for the visualization
 **/
public Layout<Vertex,Edge> getNetworkLayout() {
	return layout;
}

/**
 * Repaints the network viewer
 * @return void
 */
public void repaintViewer() {
	visualizationViewer.repaint();	
}

/**
 * Updates the annotations of the widgets, the network and repaints the visualization
 * @return void
 */
public void refreshAnnotations() {
	for(BrowserWidget widget: widgetTable.values()){
		if((!widget.isClickable() || widget.isActive()) && (widget.getNetwork() !=null) ){
			widget.updateAnnotations();
		}
	}
	// TODO: check precise functionality of network annotations and shadows
	network.updateAnnotations();
	network.applyShadows();
	System.gc();
	visualizationViewer.repaint();
}

/**
 * Interface function to trigger the changes due to a change on the network nature
 * @return void
 */
@SuppressWarnings("unchecked")
public void onNetworkChange() {
	System.out.println("Network changed " + network.getName());
	
	//concurrent modification of the ARF layouts for simulation position updates
	if (layout instanceof ARF2Layout)
	{
		((ARF2Layout<Vertex,Edge>)layout).step();
		((ARF2Layout<Vertex,Edge>)layout).resetUpdates();
	}
	if (layout instanceof WeightedARF2Layout)
	{
		((WeightedARF2Layout<Vertex,Edge>)layout).step();
		((WeightedARF2Layout<Vertex,Edge>)layout).resetUpdates();
	}
	getVisualizationViewer().repaint();

}

/**
 * Getter for an argument from the configuration defined by its name
 * @param String name of the argument
 * @return String with the value of the argument taken from the configuration XML
 */
public String getArgument(String name) {
	return arguments.get(name);
}


/**
 * This method initializes layoutPanel	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getLayoutPanel() {
	if (layoutPanel == null) {
		layoutPanel = new LayoutPanel(this);	
	}
	return layoutPanel;
}


/**
 * Refreshes the layout type and unlocks all the vertices
 * @return void
 */
public void resumeLayout() {
	setLayout(layoutType);
	for (Vertex v : getNetwork().getVertices())
		if (!v.isFixed())
			layout.lock(v, false);	
		else
			layout.setLocation(v, v.getPosition());
}

/**
 * Locks all the vertices in the layout, nothing should move after this
 * @return void
 */
public void stopLayout() {
	for (Vertex v : getNetwork().getVertices())
	{
		layout.lock(v, true);
		if (v.isFixed())
			layout.setLocation(v, v.getPosition());
	}
}

/**
 * Creates a layout from the type chosen among "ARFLayout", "FixedLayout", "WeightedARFLayout", "SpringLayout", "Kamada-Kawai",
 * "Fruchterman-Reingold", "ISOMLayout" or "CircleLayout"
 * @param String with the layout type from the possible ones
 * @return void
 */
@SuppressWarnings("unchecked")
public void setLayout(String selectedLayout){
	
	layoutType = selectedLayout;
	Layout<Vertex,Edge> newLayout = null;
	
	if (selectedLayout.equalsIgnoreCase("ARFLayout"))
	{	
		newLayout = new ARF2Layout<Vertex,Edge>(getNetwork(), ((BrowsableNetwork)getNetwork()).isIncremental(),layout);
		if (((ARF2Layout<Vertex,Edge>)newLayout).getMaxUpdates() < getNetwork().getVertexCount())
			((ARF2Layout<Vertex,Edge>)newLayout).setMaxUpdates(getNetwork().getVertexCount());		
	}
	if (selectedLayout.equalsIgnoreCase("WeightedARFLayout"))
	{
		newLayout = new WeightedARF2Layout<Vertex,Edge>(getNetwork(), ((BrowsableNetwork)getNetwork()).isIncremental(),layout);
		if (((WeightedARF2Layout<Vertex,Edge>)newLayout).getMaxUpdates() < getNetwork().getVertexCount())
			((WeightedARF2Layout<Vertex,Edge>)newLayout).setMaxUpdates(getNetwork().getVertexCount());		
	}
	if (selectedLayout.equalsIgnoreCase("SpringLayout"))
	{	
		newLayout = new SpringLayout2<Vertex, Edge>(getNetwork());
		//System.out.println("rep: "+((SpringLayout2<Vertex,Edge>) newLayout).getRepulsionRange());
		//System.out.println("force: "+((SpringLayout2<Vertex,Edge>) newLayout).getForceMultiplier());
		((SpringLayout2<Vertex,Edge>) newLayout).setForceMultiplier(10);

	}
	if (selectedLayout.equalsIgnoreCase("Kamada-Kawai"))
	{
		newLayout = new KKLayout<Vertex, Edge>(getNetwork());
		((KKLayout<Vertex,Edge>)newLayout).setExchangeVertices(false);
		((KKLayout<Vertex,Edge>)newLayout).setDisconnectedDistanceMultiplier(3);
		((KKLayout<Vertex,Edge>)newLayout).setLengthFactor(0.15);
	}
	if (selectedLayout.equalsIgnoreCase("Fruchterman-Reingold"))
		newLayout = new FRLayout2<Vertex, Edge>(getNetwork());
	if (selectedLayout.equalsIgnoreCase("ISOMLayout"))
		newLayout = new ISOMLayout<Vertex, Edge>(getNetwork());
	if (selectedLayout.equalsIgnoreCase("CircleLayout"))
	{
		newLayout = new CircleLayout<Vertex, Edge>(getNetwork());
		((CircleLayout)newLayout).setRadius(getNetwork().getVertexCount() * 10);
	}
	if (selectedLayout.equalsIgnoreCase("FixedLayout"))
		newLayout = new FixedLayout<Vertex, Edge>(getNetwork(),layout);
	
	layout = newLayout;
	System.out.println("Set layout to " + layout.getClass());

	for (Vertex v : getNetwork().getVertices())
		if (v.isFixed())
			layout.lock(v,true);
	
	getVisualizationViewer().setGraphLayout(layout);
	
	getVisualizationViewer().repaint();
}

@Override
/**
 * Creates an image of the current visualization
 * @return BufferedImage image created by the visualization viewer 
 **/
public BufferedImage getSnapshot() {
	 Dimension size = visualizationViewer.getSize();
     BufferedImage img = new BufferedImage(size.width, size.height,
       BufferedImage.TYPE_INT_RGB);
	 
     Graphics2D g2 = img.createGraphics();
	 visualizationViewer.paint(g2);
	 return img;

}

@Override
/**
 * Gives the set of selected vertices by the mouse
 * @return set of selected vertices
 */
public Set<Vertex> getPickedVertices()
{
	return visualizationViewer.getPickedVertexState().getPicked();
}

}  //  @jve:decl-index=0:visual-constraint="10,10"
