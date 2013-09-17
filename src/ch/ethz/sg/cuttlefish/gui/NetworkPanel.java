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

package ch.ethz.sg.cuttlefish.gui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.gephi.layout.api.LayoutController;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.layout.spi.Layout;
import org.openide.util.Lookup;
import org.openide.util.NotImplementedException;

import ch.ethz.sg.cuttlefish.Cuttlefish;
import ch.ethz.sg.cuttlefish.gui.tasks.SetLayoutWorker;
import ch.ethz.sg.cuttlefish.gui.undoable.UndoableControl;
import ch.ethz.sg.cuttlefish.gui.visualization.NetworkRenderer;
import ch.ethz.sg.cuttlefish.gui.visualization.mouse.GraphMouse;
import ch.ethz.sg.cuttlefish.gui.visualization.mouse.GraphMouse.Mode;
import ch.ethz.sg.cuttlefish.gui.visualization.mouse.GraphMouseImpl;
import ch.ethz.sg.cuttlefish.layout.LayoutLoader;
import ch.ethz.sg.cuttlefish.layout.arf.ARFLayout;
import ch.ethz.sg.cuttlefish.layout.arf.WeightedARFLayout;
import ch.ethz.sg.cuttlefish.layout.circle.CircleLayout;
import ch.ethz.sg.cuttlefish.layout.kcore.KCoreLayout;
import ch.ethz.sg.cuttlefish.layout.kcore.WeightedKCoreLayout;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;
import ch.ethz.sg.cuttlefish.networks.BrowsableForestNetwork;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class NetworkPanel extends JPanel implements Subject, ItemListener,
		INetworkBrowser, Runnable, java.util.Observer {

	private static final long serialVersionUID = 1L;

	private final boolean LIMIT_LAYOUT_ITERATIONS = false;
	private int layoutIterationLimit = 0;

	private BrowsableNetwork network = null;
	private Layout layout = null;
	private String currentLayout = null;
	private String layoutType = null;
	private List<Observer> observers = null;
	private StatusBar statusBar = null;
	private SetLayoutWorker setLayoutWorker = null;
	private int width;
	private int height;

	/*
	 * Used so that the user will be able to pass layout parameters when
	 * necessary. For example, in Weighted KCore, the user must specify two
	 * parameters, alpha & beta from input.
	 */
	private Object[] layoutParameters = new Object[2];

	// Updated visualization
	private GLCanvas canvas = null;
	LayoutLoader layoutLoader = null;
	private NetworkRenderer networkRenderer = null;
	private GraphMouse gMouse;

	public NetworkPanel() {
		super();
		width = 1096;
		height = 200;
		initialize();
	}

	public NetworkPanel(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		initialize();
	}

	public NetworkRenderer getNetworkRenderer() {
		//
		// // Create renderer if it doesn't exist
		// if (networkRenderer == null) {
		// if (network == null) {
		// synchronized (this) {
		// try {
		// this.wait();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		//
		// networkRenderer = new NetworkRenderer(network.getVertices(),
		// network.getEdges(), width, height);
		// networkRenderer.setNetwork(network);
		// }
		//

		if (networkRenderer == null) {
			networkRenderer = new NetworkRenderer(this, width, height);
			canvas = networkRenderer.getCanvas();
		}

		return networkRenderer;
	}

	public StatusBar getStatusBar() {
		if (statusBar == null)
			statusBar = new StatusBar();
		return statusBar;
	}

	public String getCurrentLayout() {
		return currentLayout;
	}

	private void initialize() {
		observers = new LinkedList<Observer>();

		setLayout(new BorderLayout());
		add(getStatusBar(), BorderLayout.SOUTH);
		setSize(width, height);

		BrowsableNetwork network = new BrowsableNetwork();
		networkRenderer = new NetworkRenderer(this, width, height);
		layoutLoader = new LayoutLoader(this);
		setNetwork(network);

		canvas = networkRenderer.getCanvas();
		add(canvas, BorderLayout.CENTER);

		gMouse = new GraphMouseImpl(this);
		gMouse.setMode(Mode.TRANSFORMING);

		// Add OpenGL and Mouse event listeners
		canvas.addGLEventListener(getNetworkRenderer());
		canvas.addMouseMotionListener(getNetworkRenderer().getGraphMouse());
		canvas.addMouseListener(getNetworkRenderer().getGraphMouse());
		canvas.addMouseWheelListener(getNetworkRenderer().getGraphMouse());

		// TODO ilias: Popups disabled
		// PopupMousePlugin<Vertex, Edge> popupMouse = new
		// PopupMousePlugin<Vertex, Edge>(this);
		// popupMouse.setVertexPopup(new MouseMenus.VertexMenu());
		// popupMouse.setEdgePopup(new MouseMenus.EdgeMenu(null));
		//
		// graphMouse = new
		// EditingModalGraphMouse<Vertex,Edge>(getVisualizationViewer().getRenderContext(),
		// vertexFactory, edgeFactory);
		// graphMouse.getAnnotatingPlugin().setAnnotationColor(getForeground());
		//
		// // starting on transforming mode, the most used
		// graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		// graphMouse.remove(graphMouse.getPopupEditingPlugin());
		// PopupMousePlugin<Vertex, Edge> popupMouse = new
		// PopupMousePlugin<Vertex, Edge>(this);
		// popupMouse.setVertexPopup(new MouseMenus.VertexMenu());
		// popupMouse.setEdgePopup(new MouseMenus.EdgeMenu(null));
		// graphMouse.add(popupMouse);

		// Vertex rendering
		// RenderContext<Vertex,Edge> renderContext =
		// getVisualizationViewer().getRenderContext();
		// Transformer<Vertex, Shape> vertexShapeTransformer = new
		// Transformer<Vertex, Shape>() {
		// public Shape transform(Vertex vertex) {
		// return vertex.getShape();} };
		// Transformer<Vertex,String> vertexLabelTransformer = new
		// Transformer<Vertex,String>(){
		// public String transform(Vertex vertex) {
		//
		// if (network instanceof CxfNetwork)
		// if (((CxfNetwork)network).hideVertexLabels())
		// return null;
		// return vertex.getLabel();
		//
		// } };
		// Transformer<Vertex, Stroke> vertexStrokeTransformer = new
		// Transformer<Vertex, Stroke>(){
		// public Stroke transform(Vertex vertex) {
		// return new BasicStroke(new Double(vertex.getWidth()).intValue()); }
		// };
		// Transformer<Vertex, Paint> vertexPaintTransformer = new
		// Transformer<Vertex, Paint>(){
		// public Paint transform(Vertex vertex) {
		// return vertex.getFillColor(); } };
		// Transformer<Vertex, Paint> vertexBorderTransformer = new
		// Transformer<Vertex, Paint>(){
		// public Paint transform(Vertex vertex) {
		// return vertex.getColor(); } };

		// This predicate selects which vertices to ignore
		// Predicate<Context<Graph<Vertex, Edge>, Vertex>>
		// vertexIncludePredicate = new Predicate<Context<Graph<Vertex, Edge>,
		// Vertex>>() {
		// public boolean evaluate(Context<Graph<Vertex, Edge>, Vertex> context)
		// {
		// Vertex vertex = context.element;
		// return !vertex.isExcluded();
		// }
		// };

		// // vertex label, shape and border width
		// renderContext.setVertexShapeTransformer(vertexShapeTransformer);
		// renderContext.setVertexLabelTransformer(vertexLabelTransformer);
		// renderContext.setVertexDrawPaintTransformer(vertexBorderTransformer);
		// renderContext.setVertexIncludePredicate(vertexIncludePredicate);
		// renderContext.setLabelOffset(20); //shift of the vertex label to
		// center the first character under the vertex
		// //vertex colors
		// renderContext.setVertexStrokeTransformer(vertexStrokeTransformer);
		// renderContext.setVertexFillPaintTransformer(vertexPaintTransformer);

		// Edge rendering
		// Transformer<Edge, Paint> edgePaintTransformer = new Transformer<Edge,
		// Paint>(){
		// public Paint transform(Edge edge) {
		// return edge.getColor(); } };
		// Transformer<Edge, String> edgeLabelTransformer = new
		// Transformer<Edge, String>(){
		// public String transform(Edge edge) {
		// if (network instanceof CxfNetwork)
		// if (((CxfNetwork)network).hideEdgeLabels())
		// return null;
		// return edge.getLabel();} };
		//
		//
		// Transformer<Context<Graph<Vertex,Edge>, Edge>, Shape>
		// edgeShapeTransformer = new Transformer<Context<Graph<Vertex,Edge>,
		// Edge>, Shape>() {
		// public Shape transform(Context<Graph<Vertex, Edge>, Edge> context) {
		// if(network instanceof CxfNetwork) {
		// if( ((CxfNetwork)network).getEdgeShape() == "line" ) {
		// return new EdgeShape.Line<Vertex, Edge>().transform(context);
		// }
		// } else if (network instanceof BrowsableForestNetwork) {
		// if( ((BrowsableForestNetwork)network).getEdgeShape() == "line" ) {
		// return new EdgeShape.Line<Vertex, Edge>().transform(context);
		// }
		// }
		// // The default edge shape is QuadCurve
		// return new EdgeShape.QuadCurve<Vertex, Edge>().transform(context);
		// }
		// };
		// renderContext.setEdgeShapeTransformer(edgeShapeTransformer);
		//
		// Transformer<Edge,Stroke> edgeStrokeTransformer = new
		// Transformer<Edge, Stroke>() {
		// public Stroke transform(Edge edge) {
		// return new BasicStroke(new Double(edge.getWidth()).intValue()); } };
		// // This predicate selects which edges to ignore
		// Predicate<Context<Graph<Vertex,Edge>,Edge>> edgeIncludePredicate =
		// new Predicate<Context<Graph<Vertex,Edge>,Edge>>() {
		// public boolean evaluate(Context<Graph<Vertex, Edge>, Edge> context) {
		// Edge edge = context.element;
		// return !edge.isExcluded(); } };
		//
		// renderContext.setEdgeDrawPaintTransformer(edgePaintTransformer);
		// renderContext.setArrowFillPaintTransformer(edgePaintTransformer);
		// renderContext.setArrowDrawPaintTransformer(edgePaintTransformer);
		// //arrows are of the same color as the edge
		// renderContext.setEdgeLabelTransformer(edgeLabelTransformer);
		// renderContext.setEdgeStrokeTransformer(edgeStrokeTransformer);
		// renderContext.setEdgeIncludePredicate(edgeIncludePredicate);

		// Mouse settings
		// getVisualizationViewer().setPickSupport(new
		// ShapePickSupport<Vertex,Edge>(getVisualizationViewer()));
		// getVisualizationViewer().setGraphMouse(graphMouse);
		// getVisualizationViewer().getPickedVertexState().addItemListener(this);
		// getVisualizationViewer().setDoubleBuffered(true);
	}

	@Override
	public Layout getNetworkLayout() {
		return layout;
	}

	@Override
	public void repaintViewer() {
		getNetworkRenderer().repaint();
	}

	/**
	 * Getter for the network
	 * 
	 * @return BrowsableNetwork in use in CuttleFish (wraps an
	 *         org.gephi.graph.api.MixedGraph)
	 */
	public BrowsableNetwork getNetwork() {
		return network;
	}

	@Override
	public void setNetwork(BrowsableNetwork network) {
		this.network = network;

		/*
		 * Default layout: ARF If the network is not a forest and the layout is
		 * a tree layout, change the layout to the default ARF layout.
		 */
		if (layout == null) {
			setLayoutByName("arf");
			// for the first setting of the layout we busy wait until
			// the thread layout setter thread sets the layout
			while (layout == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// else if (layout instanceof TreeLayout) {
		// network = new BrowsableForestNetwork(network);
		// //layout.setGraph(network);
		// }

		if (layout != null /* && layout.getGraph() != network */) {// consistency
																	// check
			// between layout
			// and network
			// layout.setGraph(network);

			// maxUpdates in ARF2Layout depends on the network size, this way
			// it's
			// updated if the network changes
			// if ((layout != null && layout instanceof ARF2Layout)
			// && (((ARF2Layout<Vertex, Edge>) layout).getMaxUpdates() <
			// getNetwork()
			// .getVertexCount()))
			// ((ARF2Layout<Vertex, Edge>) layout).setMaxUpdates(getNetwork()
			// .getVertexCount());

			// network.init(); TODO ilias: this will clear the network
			try {
				layout = layoutLoader.getLayout(layout.getBuilder().getName());
			} catch (Exception e) {
				errorPopup(layout.getBuilder().getName() + " Layout error",
						e.getLocalizedMessage());
			}
			setLayout(layout);
		}

		refreshAnnotations();

		/* Undoable Controller */
		UndoableControl.getController().addObserver(this);
		UndoableControl.resetController();
	}

	@Override
	public void refreshAnnotations() {
		network.updateAnnotations();
		network.applyShadows();
		this.repaintViewer();
	}

	@Override
	public void onNetworkChange() {

		// //concurrent modification of the ARF layouts for simulation position
		// updates
		// if (layout instanceof ARF2Layout) {
		// ((ARF2Layout<Vertex, Edge>) layout).step();
		// ((ARF2Layout<Vertex, Edge>) layout).resetUpdates();
		// } else if (layout instanceof WeightedARF2Layout) {
		// ((WeightedARF2Layout<Vertex, Edge>) layout).step();
		// ((WeightedARF2Layout<Vertex, Edge>) layout).resetUpdates();
		// }
		//
		// if (layout instanceof FixedLayout) {
		// ((FixedLayout<Vertex, Edge>) layout).update();
		// }

		// non-iterative layouts need to be explicitly reset
		/*
		 * layout instanceof TreeLayout || layout instanceof RadialTreeLayout ||
		 */

		stopLayout();
		if (layout instanceof CircleLayout || layout instanceof KCoreLayout
				|| layout instanceof WeightedKCoreLayout) {
			setNetwork(((BrowsableForestNetwork) getNetwork())
					.getOriginalNetwork());
		}
		resumeLayout();

		Cuttlefish.debug(this, "onNetworkChange");
	}

	@Override
	public void stopLayout() {
		// TODO ilias: lock all nodes if fixed here!

		LayoutController layoutController = Lookup.getDefault().lookup(
				LayoutController.class);

		if (layoutController.canStop())
			layoutController.stopLayout();
	}

	public void centerLayout() {
		Rectangle2D rect = layoutLoader.normalizeLayout();

		if (rect != null) {
			networkRenderer.centerNetwork(rect);
			networkRenderer.repaint();
		}
	}

	@Override
	public void resumeLayout() {

		if (layoutType != null && !layoutType.isEmpty())
			setLayoutByName(layoutType);

		// TODO ilias: lock all nodes if fixed here!
		// boolean fixed = getNetworkLayout() instanceof FixedLayout;
		// getNetwork().fixVertices(!fixed);
	}

	public void setLayoutParameters(Object[] parameters) {
		for (int i = 0; i < parameters.length; ++i) {
			layoutParameters[i] = parameters[i];
		}
	}

	@Override
	public void setLayoutByName(String selectedLayout) {
		Layout newLayout = null;

		try {
			newLayout = layoutLoader.getLayout(selectedLayout);
		} catch (Exception e) {
			errorPopup(selectedLayout + " Layout error",
					e.getLocalizedMessage());
			return;
		}

		if (newLayout == null) {
			throw new RuntimeException("Unknown layout: " + selectedLayout);
		}

		layoutIterationLimit = 0;
		if (LIMIT_LAYOUT_ITERATIONS)
			layoutIterationLimit = getNetwork().getVertexCount();

		if (newLayout instanceof ARFLayout) {
			ARFLayout arf = (ARFLayout) newLayout;
			arf.setIncremental(((BrowsableNetwork) getNetwork())
					.isIncremental());
			arf.keepInitialPostitions(true);

		} else if (newLayout instanceof WeightedARFLayout) {
			WeightedARFLayout weightedArf = (WeightedARFLayout) newLayout;
			weightedArf.setIncremental(((BrowsableNetwork) getNetwork())
					.isIncremental());
			weightedArf.keepInitialPostitions(true);

		} else if (newLayout instanceof WeightedKCoreLayout) {
			double alpha = (Double) layoutParameters[0];
			double beta = (Double) layoutParameters[1];

			((WeightedKCoreLayout) newLayout).setAlpha(alpha);
			((WeightedKCoreLayout) newLayout).setBeta(beta);

		} else if (newLayout instanceof FruchtermanReingold) {
			layoutIterationLimit = 700;

		}

		// TODO ilias: check that fixed vertices remain fixed during layout

		setLayout(newLayout);
		layoutType = selectedLayout;

		for (Observer o : observers)
			o.update(this);
	}

	private void setLayout(Layout layout) {
		this.layout = layout;
		LayoutController layoutController = Lookup.getDefault().lookup(
				LayoutController.class);

		// configure layout parameters & execution
		layoutController.stopLayout();
		layoutController.setLayout(layout);

		if (!network.isEmpty() && layoutController.canExecute()) {
			if (layoutIterationLimit > 0)
				layoutController.executeLayout(layoutIterationLimit);
			else
				layoutController.executeLayout();

			getStatusBar().setBusyMessage(
					"Setting layout to " + layout.getBuilder().getName(),
					layoutController);
		}
	}

	@Deprecated
	public void setLayout2(String selectedLayout) {
		if (setLayoutWorker != null) {
			// make sure we stopped the previous layout worker
			while (!setLayoutWorker.isDone()) {
				setLayoutWorker.cancel(true);
			}
			setLayoutWorker = null;
		}
		setLayoutWorker = new SetLayoutWorker(selectedLayout, this);
		setLayoutWorker.execute();
	}

	@Deprecated
	public void setLayoutByName2(String selectedLayout) {
		throw new NotImplementedException(
				"This is no longer supported! Gephi Layouts are independent workers");
	}

	// public void setLayoutByName(String selectedLayout) {
	//
	// layoutType = selectedLayout;
	// Layout<Vertex, Edge> newLayout = null;
	// currentLayout = selectedLayout;
	//
	// if (selectedLayout.equalsIgnoreCase("arf")) {
	// newLayout = new ARF2Layout<Vertex, Edge>(getNetwork(),
	// ((BrowsableNetwork) getNetwork()).isIncremental(), layout);
	// ((ARF2Layout<Vertex, Edge>) newLayout).setMaxUpdates(Integer.MAX_VALUE);
	// if (((ARF2Layout<Vertex, Edge>) newLayout).getMaxUpdates() <
	// getNetwork().getVertexCount())
	// ((ARF2Layout<Vertex, Edge>)
	// newLayout).setMaxUpdates(getNetwork().getVertexCount());
	// }
	// if (selectedLayout.equalsIgnoreCase("weighted-arf")) {
	// newLayout = new WeightedARF2Layout<Vertex, Edge>(getNetwork(),
	// ((BrowsableNetwork) getNetwork()).isIncremental(), layout);
	// ((WeightedARF2Layout<Vertex, Edge>)
	// newLayout).setMaxUpdates(Integer.MAX_VALUE);
	// if (((WeightedARF2Layout<Vertex, Edge>) newLayout).getMaxUpdates() <
	// getNetwork().getVertexCount())
	// ((WeightedARF2Layout<Vertex, Edge>)
	// newLayout).setMaxUpdates(getNetwork().getVertexCount());
	// }
	// if (selectedLayout.equalsIgnoreCase("SpringLayout")) {
	// newLayout = new SpringLayout2<Vertex, Edge>(getNetwork());
	// ((SpringLayout2<Vertex, Edge>) newLayout).setForceMultiplier(10);
	// }
	// if (selectedLayout.equalsIgnoreCase("kamada-kawai")) {
	// newLayout = new KKLayout<Vertex, Edge>(getNetwork());
	// ((KKLayout<Vertex, Edge>) newLayout).setExchangeVertices(false);
	// ((KKLayout<Vertex, Edge>)
	// newLayout).setDisconnectedDistanceMultiplier(3);
	// ((KKLayout<Vertex, Edge>) newLayout).setLengthFactor(0.15);
	// }
	// if (selectedLayout.equalsIgnoreCase("fruchterman-reingold")) {
	// newLayout = new FRLayout2<Vertex, Edge>(getNetwork());
	// }
	// if (selectedLayout.equalsIgnoreCase("iso-m")) {
	// newLayout = new ISOMLayout<Vertex, Edge>(getNetwork());
	// }
	// if (selectedLayout.equalsIgnoreCase("circle"))
	// newLayout = new CircleLayout<Vertex, Edge>(getNetwork());
	// if (selectedLayout.equalsIgnoreCase("fixed"))
	// newLayout = new FixedLayout<Vertex, Edge>(getNetwork(), layout);
	// if (selectedLayout.equalsIgnoreCase("kcore"))
	// newLayout = new KCoreLayout<Vertex, Edge>(getNetwork(), layout);
	// if (selectedLayout.equalsIgnoreCase("weighted-kcore")) {
	// double alpha = (Double) layoutParameters[0];
	// double beta = (Double) layoutParameters[1];
	// newLayout = new WeightedKCoreLayout<Vertex, Edge>(getNetwork(), layout,
	// alpha, beta);
	// }
	// if (selectedLayout.equalsIgnoreCase("tree")) {
	// BrowsableForestNetwork network = new
	// BrowsableForestNetwork((BrowsableNetwork) getNetwork());
	// setNetwork(network);
	// newLayout = new TreeLayout<Vertex, Edge>(network);
	// }
	// if (selectedLayout.equalsIgnoreCase("radial-tree")) {
	// BrowsableForestNetwork network = new
	// BrowsableForestNetwork((BrowsableNetwork) getNetwork());
	// setNetwork(network);
	// newLayout = new RadialTreeLayout<Vertex, Edge>(network);
	//
	// }
	//
	// layout = newLayout;
	// for (Vertex v : getNetwork().getVertices())
	// if (v.isFixed())
	// layout.lock(v, true);
	//
	// // centerGraph() requires double invocation
	// // centerGraph();
	// centerGraph();
	// repaintViewer();
	//
	// for (Observer o : observers)
	// o.update(this);
	//
	// }

	/**
	 * Getter for the graph mouse associated to the panel
	 * 
	 * @return EditingModalGraphMouse automatically created by JUNG2.0
	 */
	// public EditingModalGraphMouse<Vertex, Edge> getMouse() {
	// return graphMouse;
	// }

	public GraphMouse getMouse() {
		return gMouse;
	}

	@Override
	public BufferedImage getSnapshot() {
		// Dimension size = getVisualizationViewer().getSize();
		// BufferedImage img = new BufferedImage(size.width, size.height,
		// BufferedImage.TYPE_INT_RGB);
		// Graphics2D g2 = img.createGraphics();
		// getVisualizationViewer().paint(g2);
		// return img;
		throw new NotImplementedException(
				"Not implemented yet for the Gephi Toolkit!");
	}

	@Override
	public Set<Vertex> getPickedVertices() {
		// return getVisualizationViewer().getPickedVertexState().getPicked();
		throw new NotImplementedException(
				"Not implemented yet for the Gephi Toolkit!");
	}

	@Override
	public Set<Edge> getPickedEdges() {
		// return getVisualizationViewer().getPickedEdgeState().getPicked();
		throw new NotImplementedException(
				"Not implemented yet for the Gephi Toolkit!");
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO ilias: Check if necessary
		Cuttlefish.debug(this,
				"itemStateChanged: When does this happen? Is it necessary?");
		refreshAnnotations();
	}

	@Override
	public void run() {
		while (true)
			;
	}

	@Override
	public void addObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	@Override
	public void update(Observable o, Object arg) {
		refreshAnnotations();
	}

	public GLCanvas getCanvas() {
		return canvas;
	}

	private void errorPopup(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}
}
