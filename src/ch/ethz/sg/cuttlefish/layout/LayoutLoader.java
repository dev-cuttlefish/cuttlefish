package ch.ethz.sg.cuttlefish.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeMap;

import org.gephi.layout.api.LayoutController;
import org.gephi.layout.api.LayoutModel;
import org.gephi.layout.plugin.force.yifanHu.YifanHu;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingoldBuilder;
import org.gephi.layout.plugin.random.Random;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.openide.util.Lookup;
import org.openide.util.NotImplementedException;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.layout.arf.ARFLayout;
import ch.ethz.sg.cuttlefish.layout.arf.ARFLayoutBuilder;
import ch.ethz.sg.cuttlefish.layout.arf.WeightedARFLayout;
import ch.ethz.sg.cuttlefish.layout.arf.WeightedARFLayoutBuilder;
import ch.ethz.sg.cuttlefish.layout.circle.CircleLayoutBuilder;
import ch.ethz.sg.cuttlefish.layout.fixed.FixedLayoutBuilder;
import ch.ethz.sg.cuttlefish.layout.kcore.KCoreLayoutBuilder;
import ch.ethz.sg.cuttlefish.layout.kcore.WeightedKCoreLayout;
import ch.ethz.sg.cuttlefish.layout.kcore.WeightedKCoreLayoutBuilder;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class LayoutLoader {

	// Static fields and methods

	public static final String DEFAULT_LAYOUT = "arf";
	public static final boolean VERBOSE_LAYOUT = false;

	private static final Map<String, Class<? extends LayoutBuilder>> builders;
	private static final Map<String, Class<? extends LayoutBuilder>> unsupported;
	private static final Map<String, String> abbreviations;

	private static LayoutLoader instance = null;

	static {
		builders = new TreeMap<String, Class<? extends LayoutBuilder>>();
		unsupported = new TreeMap<String, Class<? extends LayoutBuilder>>();
		abbreviations = new TreeMap<String, String>();

		// Cuttlefish Layouts
		builders.put("arf", ARFLayoutBuilder.class);
		builders.put("weighted-arf", WeightedARFLayoutBuilder.class);
		builders.put("fixed", FixedLayoutBuilder.class);
		builders.put("circle", CircleLayoutBuilder.class);
		builders.put("kcore", KCoreLayoutBuilder.class);
		builders.put("weighted-kcore", WeightedKCoreLayoutBuilder.class);

		// Gephi Toolkit Layouts
		builders.put("fruchterman-reingold", FruchtermanReingoldBuilder.class);
		builders.put("yifanhu", YifanHu.class);
		builders.put("force-atlas", ForceAtlas2Builder.class);
		builders.put("random", Random.class);

		// TODO: Port from jung2 if necessary
		// Tree, Radial Tree, Kamada Kawai, ISO-M
		unsupported.put("tree", null);
		unsupported.put("radial-tree", null);
		unsupported.put("kamada-kawai", null);
		unsupported.put("iso-m", null);

		// Match a layout name with an abbreviation
		abbreviations.put("ARF", "arf");
		abbreviations.put("Weighted ARF", "weighted-arf");
		abbreviations.put("Circle", "circle");
		abbreviations.put("Fixed", "fixed");
		abbreviations.put("Fruchterman Reingold", "fruchterman-reingold");
		abbreviations.put("K-Core", "kcore");
		abbreviations.put("Weighted K-Core", "weighted-kcore");
		abbreviations.put("Yifan Hu", "yifanhu");
		abbreviations.put("ForceAtlas 2", "force-atlas");
		abbreviations.put("Random", "random");
	}

	public static LayoutLoader getInstance() {
		return initGUI(null);
	}

	public static LayoutLoader initNoGUI(BrowsableNetwork network) {
		if (instance == null) {
			instance = new LayoutLoader(network);
		}

		return instance;
	}

	public static LayoutLoader initGUI(NetworkPanel panel) {
		if (instance == null) {
			instance = new LayoutLoader(panel);
		}

		return instance;
	}

	public static String getKeyList() {
		StringBuilder sb = new StringBuilder();

		for (String layout : builders.keySet()) {
			if (layout.equalsIgnoreCase(DEFAULT_LAYOUT)) {
				layout = layout + "(default)";
			}
			sb.append(layout).append(", ");
		}

		return sb.substring(0, sb.length() - 2).toString();
	}

	// Instance fields and methods

	private LayoutModel layoutModel = null;
	private NetworkPanel networkPanel = null;
	private BrowsableNetwork network = null;

	private final boolean LIMIT_LAYOUT_ITERATIONS = false;
	private int layoutIterationLimit = 0;
	private String layoutName = null;

	private Map<String, String> layoutParameters = null;

	private LayoutLoader(NetworkPanel panel) {
		networkPanel = panel;

		layoutModel = Lookup.getDefault().lookup(LayoutController.class)
				.getModel();
		layoutModel.addPropertyChangeListener(new LayoutChangeListener());
	}

	private LayoutLoader(BrowsableNetwork net) {
		network = net;
		layoutModel = Lookup.getDefault().lookup(LayoutController.class)
				.getModel();
		layoutModel.addPropertyChangeListener(new LayoutChangeListener());
	}
	
	public void setNetwork(BrowsableNetwork network) {
		this.network = network;
	}

	public void setLayoutByName(String selectedLayout) {
		Layout newLayout = null;

		try {
			newLayout = getLayout(selectedLayout);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		if (newLayout == null) {
			throw new RuntimeException("Unknown layout: " + selectedLayout);
		}

		layoutIterationLimit = 0;
		if (LIMIT_LAYOUT_ITERATIONS)
			layoutIterationLimit = network.getVertexCount();

		if (newLayout instanceof ARFLayout) {
			ARFLayout arf = (ARFLayout) newLayout;
			arf.setIncremental(network.isIncremental());
			arf.keepInitialPostitions(true);

		} else if (newLayout instanceof WeightedARFLayout) {
			WeightedARFLayout weightedArf = (WeightedARFLayout) newLayout;
			weightedArf.setIncremental(network.isIncremental());
			weightedArf.keepInitialPostitions(true);

		} else if (newLayout instanceof WeightedKCoreLayout) {
			if (layoutParameters != null) {
				double alpha = Double.parseDouble(layoutParameters
						.get(WeightedKCoreLayout.PARAMETER_ALPHA));

				double beta = Double.parseDouble(layoutParameters
						.get(WeightedKCoreLayout.PARAMETER_BETA));

				((WeightedKCoreLayout) newLayout).setAlpha(alpha);
				((WeightedKCoreLayout) newLayout).setBeta(beta);
			}

		} else if (newLayout instanceof FruchtermanReingold) {
			layoutIterationLimit = 700;

		}

		// TODO ilias: check that fixed vertices remain fixed during layout
		layoutName = selectedLayout;
		setLayout(newLayout);
	}

	public void resetLayout() {
		layoutParameters = null;
		setLayoutByName(layoutName);
	}

	public void stopLayout() {
		LayoutController layoutController = Lookup.getDefault().lookup(
				LayoutController.class);

		if (layoutController.canStop())
			layoutController.stopLayout();

		layoutParameters = null;
	}

	public Layout getSelectedLayout() {
		LayoutController layoutController = Lookup.getDefault().lookup(
				LayoutController.class);

		return layoutController.getModel().getSelectedLayout();
	}

	private void setLayout(Layout layout) {
		LayoutController layoutController = Lookup.getDefault().lookup(
				LayoutController.class);

		// configure layout parameters & execution
		if (layoutController.canStop())
			layoutController.stopLayout();

		layoutController.setLayout(layout);

		if (!network.isEmpty() && layoutController.canExecute()) {
			if (layoutIterationLimit > 0)
				layoutController.executeLayout(layoutIterationLimit);
			else
				layoutController.executeLayout();

			if (isGUI()) {
				networkPanel.getStatusBar().setBusyMessage(
						"Setting layout to " + layout.getBuilder().getName(),
						layoutController);
			}
		}
	}

	public boolean isLayoutRunning() {
		LayoutController layoutController = Lookup.getDefault().lookup(
				LayoutController.class);

		return layoutController.canStop();
	}

	public Map<String, String> getLayoutParameters() {
		return layoutParameters;
	}

	public void setLayoutParameters(Map<String, String> parameters) {
		this.layoutParameters = parameters;
	}

	public Layout getLayout(String name) {
		LayoutBuilder builder = null;
		Layout layout = null;
		String layoutName = name;

		if (abbreviations.containsKey(name))
			layoutName = abbreviations.get(name);

		if (unsupported.containsKey(layoutName))
			throw new NotImplementedException(
					"This layout is not implemented yet!");

		if (!builders.containsKey(layoutName))
			return null;

		try {
			builder = builders.get(layoutName).newInstance();
			layout = layoutModel.getLayout(builder);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return layout;
	}

	/**
	 * Normalizes the coordinates of the nodes of the graph, by bringing them
	 * close to the origin
	 */
	public void normalizeLayout() {

		BrowsableNetwork network = BrowsableNetwork.loadExistingNetwork();
		if (network.isEmpty())
			return;

		Point2D anyPoint = network.randomVertex().getPosition();
		Rectangle2D oldBounds = new Rectangle2D.Double(anyPoint.getX(),
				anyPoint.getY(), 0, 0);

		for (Vertex v : network.getVertices())
			oldBounds.add(v.getPosition());

		double x, y;
		for (Vertex v : network.getVertices()) {
			x = v.getPosition().getX();
			y = v.getPosition().getY();

			v.setPosition(x - oldBounds.getMinX(), y - oldBounds.getMinY());
		}

		centerLayout(true);
	}

	private boolean isGUI() {
		return networkPanel != null;
	}

	public void centerLayout(boolean forceRepaint) {

		// Center layout only when in GUI mode
		if (isGUI()) {
			networkPanel.getNetworkRenderer().centerNetwork();

			if (forceRepaint)
				networkPanel.getNetworkRenderer().repaint();
		}
	}

	// LayoutChangeListener inner class

	class LayoutChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {

			// Cuttlefish.debug(instance, evt.getPropertyName() + ": " +
			// evt.getNewValue());

			if (evt.getPropertyName().equals(LayoutModel.SELECTED_LAYOUT))
				layoutSelectedChanged();

			else if (evt.getPropertyName().equals(LayoutModel.RUNNING))
				layoutStateChanged(evt);
		}

		private void layoutSelectedChanged() {
			// Selected layout changed
			if (isGUI() && !networkPanel.getNetwork().isEmpty()) {

				// Network might be too large to animate
				int renderLimit = 4000;
				boolean animate = network.getVertexCount() < renderLimit
						&& network.getEdgeCount() < renderLimit;

				networkPanel.getNetworkRenderer().animate(animate, true);
			}
		}

		private void layoutStateChanged(PropertyChangeEvent evt) {
			// Layout state changed

			boolean layoutStopped = (Boolean) evt.getOldValue()
					&& !(Boolean) evt.getNewValue();

			if (isGUI()) {
				if (layoutStopped) {
					networkPanel.getNetworkRenderer().animate(false, true);
					networkPanel.getStatusBar().setMessage(
							"Layout set: "
									+ getSelectedLayout().getBuilder()
											.getName());
				}

				centerLayout(true);

			}
		}
	}
}
