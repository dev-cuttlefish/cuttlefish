package ch.ethz.sg.cuttlefish.layout.circle;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;

import ch.ethz.sg.cuttlefish.Cuttlefish;
import ch.ethz.sg.cuttlefish.networks.Vertex;

/**
 * A {@code Layout} implementation that positions vertices equally spaced on a
 * regular circle, for jung2.
 * 
 * irinis: Adapted to Gephi Toolkit, added non-overlapping vertices
 * 
 * @author Masanori Harada
 * @author Ilias Rinis
 */
public class CircleLayout implements Layout {

	private LayoutBuilder layoutBuilder = null;
	private GraphModel graphModel = null;
	private Graph graph = null;

	private double radius = 0;
	private List<Node> nodeOrderedList;
	private Map<Node, CircleNodeData> circleNodeDataMap;

	private Dimension dimension = null;
	private boolean converged = false;

	public CircleLayout(LayoutBuilder builder, Dimension dimension) {
		this.layoutBuilder = builder;
		this.dimension = dimension;
	}

	@Override
	public void initAlgo() {
		if (graphModel == null) {
			throw new RuntimeException(
					"The GraphModel for this layout cannot be null!");
		}

		graph = graphModel.getGraphVisible();

		circleNodeDataMap = LazyMap.decorate(
				new HashMap<Node, CircleNodeData>(),
				new Factory<CircleNodeData>() {
					public CircleNodeData create() {
						return new CircleNodeData();
					}
				});

		nodeOrderedList = new ArrayList<Node>();
		for (Node n : graph.getNodes())
			nodeOrderedList.add(n);

		if (Cuttlefish.VERBOSE_LAYOUT)
			Cuttlefish.debug(this, "Layout initialized.");
	}

	@Override
	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
	}

	@Override
	public void goAlgo() {
		double height = dimension.getHeight();
		double width = dimension.getWidth();

		if (radius <= 0) {
			radius = 0.45 * (height < width ? height : width);
		}

		double angle, angleSum = 0;
		for (Node n : nodeOrderedList) {
			double s = new Vertex(n).getSize();
			double r = s * Math.sqrt(2);
			angle = Math.asin(r / radius) * 2;
			angleSum += angle;
			getCircleData(n).setAngle(angle);
		}

		double delta = (2 * Math.PI - angleSum) / nodeOrderedList.size();

		if (delta < 0.02) {
			// Vertices overlap within the current circle.
			// Radius must be increased.
			radius *= 1.2;
			if (Cuttlefish.VERBOSE_LAYOUT)
				Cuttlefish.debug(this, "Radius = " + radius);

			return;
		}

		int count = 0;
		angle = 0;
		for (Node n : nodeOrderedList) {

			if (count++ > 0) {
				angle += getCircleData(n).getAngle() / 2;
			}

			n.getNodeData()
					.setX((float) (Math.cos(angle) * radius + width / 2));
			n.getNodeData().setY(
					(float) (Math.sin(angle) * radius + height / 2));

			angle += getCircleData(n).getAngle() / 2 + delta;
		}

		converged = true;

		if (Cuttlefish.VERBOSE_LAYOUT)
			Cuttlefish.debug(this, "Radius = " + radius);
	}

	@Override
	public boolean canAlgo() {
		return (dimension != null) && !converged;
	}

	@Override
	public void endAlgo() {
		if (Cuttlefish.VERBOSE_LAYOUT) {
			Cuttlefish.debug(this, "Layout ended");
		}
	}

	@Override
	public LayoutProperty[] getProperties() {
		List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
		return properties.toArray(new LayoutProperty[0]);
	}

	@Override
	public void resetPropertiesValues() {
		dimension = new Dimension(1000, 800);
		converged = false;
		radius = 0;

		if (nodeOrderedList != null)
			nodeOrderedList.clear();

		if (circleNodeDataMap != null)
			circleNodeDataMap.clear();
	}

	@Override
	public LayoutBuilder getBuilder() {
		return layoutBuilder;
	}

	protected CircleNodeData getCircleData(Node n) {
		return circleNodeDataMap.get(n);
	}

	protected static class CircleNodeData {
		private double angle;

		protected double getAngle() {
			return angle;
		}

		protected void setAngle(double angle) {
			this.angle = angle;
		}

		@Override
		public String toString() {
			return "CircleNodeData: angle = " + angle;
		}
	}
}
