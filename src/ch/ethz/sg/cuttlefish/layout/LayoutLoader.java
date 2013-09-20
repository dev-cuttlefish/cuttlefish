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
import ch.ethz.sg.cuttlefish.layout.fixed.FixedLayout;
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
		// TODO ilias: debug ForceAtlas2
		builders.put("fruchterman-reingold", FruchtermanReingoldBuilder.class);
		builders.put("yifanhu", YifanHu.class);
		builders.put("force-atlas", ForceAtlas2Builder.class);

		// TODO ilias: Port from jung2 if necessary
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
	}

	public static LayoutLoader getInstance() {
		return getInstance(null);
	}

	public static LayoutLoader getInstance(NetworkPanel panel) {
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

	private final boolean LIMIT_LAYOUT_ITERATIONS = false;
	private int layoutIterationLimit = 0;
	private long layoutTime = 0;
	private Object[] layoutParameters = new Object[2];
	private String layoutName = null;

	private Map<String, String> parameters = null;

	private LayoutLoader(NetworkPanel panel) {
		networkPanel = panel;
		layoutModel = Lookup.getDefault().lookup(LayoutController.class)
				.getModel();

		layoutModel.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				if (evt.getPropertyName().equals(LayoutModel.SELECTED_LAYOUT)) {
					// Selected layout changed
					if (networkPanel != null
							&& !networkPanel.getNetwork().isEmpty()) {

						// Network might be too large to animate layout
						// computation
						int renderLimit = 2000;
						if (networkPanel.getNetwork().getVertexCount() < renderLimit
								&& networkPanel.getNetwork().getEdgeCount() < renderLimit)
							networkPanel.getNetworkRenderer().animate(true,
									true);

						layoutTime = System.currentTimeMillis();
					}

				} else if (evt.getPropertyName().equals(LayoutModel.RUNNING)) {
					// Layout state changed

					boolean layoutStopped = (Boolean) evt.getOldValue()
							&& !(Boolean) evt.getNewValue();

					Rectangle2D rect = null;
					boolean normBeforeCenter = false;

					if (normBeforeCenter && shouldNormalize())
						rect = normalizeLayout();

					if (networkPanel != null) {

						if (normBeforeCenter && shouldNormalize())
							networkPanel.getNetworkRenderer().centerNetwork(
									rect);
						else
							centerLayout();

						if (layoutStopped) {
							networkPanel.getNetworkRenderer().animate(false,
									true);

							networkPanel.getStatusBar().setMessage(
									"Layout set: "
											+ networkPanel.getNetworkLayout()
													.getBuilder().getName());

							layoutTime = System.currentTimeMillis()
									- layoutTime;
						}
					}
				}
			}
		});

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
			layoutIterationLimit = networkPanel.getNetwork().getVertexCount();

		if (newLayout instanceof ARFLayout) {
			ARFLayout arf = (ARFLayout) newLayout;
			arf.setIncremental(((BrowsableNetwork) networkPanel.getNetwork())
					.isIncremental());
			arf.keepInitialPostitions(true);

		} else if (newLayout instanceof WeightedARFLayout) {
			WeightedARFLayout weightedArf = (WeightedARFLayout) newLayout;
			weightedArf.setIncremental(((BrowsableNetwork) networkPanel
					.getNetwork()).isIncremental());
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
		layoutName = selectedLayout;
		setLayout(newLayout);
	}

	public void resetLayout() {
		parameters = null;
		setLayoutByName(layoutName);
	}

	public void stopLayout() {
		LayoutController layoutController = Lookup.getDefault().lookup(
				LayoutController.class);

		if (layoutController.canStop())
			layoutController.stopLayout();

		parameters = null;
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

		if (!networkPanel.getNetwork().isEmpty()
				&& layoutController.canExecute()) {
			if (layoutIterationLimit > 0)
				layoutController.executeLayout(layoutIterationLimit);
			else
				layoutController.executeLayout();

			networkPanel.getStatusBar().setBusyMessage(
					"Setting layout to " + layout.getBuilder().getName(),
					layoutController);
		}
	}

	public boolean isLayoutRunning() {
		LayoutController layoutController = Lookup.getDefault().lookup(
				LayoutController.class);

		return layoutController.canStop();
	}

	public Map<String, String> getLayoutParameters() {
		return parameters;
	}

	public void setLayoutParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public void setLayoutParameters(Object[] parameters) {
		for (int i = 0; i < parameters.length; ++i) {
			layoutParameters[i] = parameters[i];
		}
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
	public Rectangle2D normalizeLayout() {

		// TODO ilias: Performance benefit if the bounding rectangle is
		// created every time a node is added?
		BrowsableNetwork network = BrowsableNetwork.loadExistingNetwork();
		if (network.isEmpty())
			return null;

		Point2D anyPoint = network.randomVertex().getPosition();
		Rectangle2D oldBounds = new Rectangle2D.Double(anyPoint.getX(),
				anyPoint.getY(), 0, 0);

		for (Vertex v : network.getVertices())
			oldBounds.add(v.getPosition());

		double x, y;
		Rectangle2D normBounds = null;
		for (Vertex v : network.getVertices()) {
			x = v.getPosition().getX();
			y = v.getPosition().getY();

			v.setPosition(x - oldBounds.getMinX(), y - oldBounds.getMinY());

			if (normBounds == null)
				normBounds = new Rectangle2D.Double(v.getPosition().getX(), v
						.getPosition().getY(), 0, 0);
			else
				normBounds.add(v.getPosition());
		}

		return normBounds;
	}

	public void centerLayout() {
		networkPanel.getNetworkRenderer().centerNetwork();
	}

	private boolean shouldNormalize() {
		boolean isFixed = layoutModel.getSelectedLayout() instanceof FixedLayout;

		return !isFixed;
	}

}
