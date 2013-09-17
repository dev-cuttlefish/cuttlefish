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
import org.gephi.layout.plugin.fruchterman.FruchtermanReingoldBuilder;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.openide.util.Lookup;
import org.openide.util.NotImplementedException;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.layout.arf.ARFLayoutBuilder;
import ch.ethz.sg.cuttlefish.layout.arf.WeightedARFLayoutBuilder;
import ch.ethz.sg.cuttlefish.layout.circle.CircleLayoutBuilder;
import ch.ethz.sg.cuttlefish.layout.fixed.FixedLayout;
import ch.ethz.sg.cuttlefish.layout.fixed.FixedLayoutBuilder;
import ch.ethz.sg.cuttlefish.layout.kcore.KCoreLayoutBuilder;
import ch.ethz.sg.cuttlefish.layout.kcore.WeightedKCoreLayoutBuilder;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class LayoutLoader {

	// Static fields and methods

	private static final String DEFAULT = "fixed";
	private static final Map<String, Class<? extends LayoutBuilder>> builders;
	private static final Map<String, Class<? extends LayoutBuilder>> unsupported;
	private static final Map<String, String> abbreviations;

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

	public static String getKeyList() {
		StringBuilder sb = new StringBuilder();

		for (String layout : builders.keySet()) {
			if (layout.equalsIgnoreCase(DEFAULT)) {
				layout = layout + "(default)";
			}
			sb.append(layout).append(", ");
		}

		return sb.substring(0, sb.length() - 2).toString();
	}

	// Instance fields and methods

	private LayoutModel layoutModel = null;
	private NetworkPanel networkPanel = null;

	public LayoutLoader() {
		this(null);
	}

	private long layoutTime = 0;

	public LayoutLoader(NetworkPanel panel) {
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
					if (shouldNormalize())
						rect = normalizeLayout();

					if (networkPanel != null) {

						if (shouldNormalize())
							networkPanel.getNetworkRenderer().centerNetwork(
									rect);

						if (layoutStopped) {
							networkPanel.getNetworkRenderer().animate(false,
									true);

							networkPanel.getStatusBar().setMessage(
									"Layout set: "
											+ networkPanel.getNetworkLayout()
													.getBuilder().getName());

							layoutTime = System.currentTimeMillis()
									- layoutTime;

							// Cuttlefish.debug(this, "# Layout computed in "
							// + (layoutTime / 1000.0) + "s.");
						}
					}
				}
			}
		});

	}

	private boolean shouldNormalize() {
		boolean isFixed = layoutModel.getSelectedLayout() instanceof FixedLayout;

		return !isFixed;
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

}
