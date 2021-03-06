package ch.ethz.sg.cuttlefish.layout.tree;

import java.util.ArrayList;
import java.util.List;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;

import ch.ethz.sg.cuttlefish.Cuttlefish;
import ch.ethz.sg.cuttlefish.layout.LayoutLoader;

public class TreeLayout implements Layout {

	private LayoutBuilder layoutBuilder = null;
	private GraphModel graphModel = null;

	@SuppressWarnings("unused")
	private Graph graph = null;

	public TreeLayout(LayoutBuilder layoutBuilder) {
		this.layoutBuilder = layoutBuilder;
	}

	@Override
	public void initAlgo() {
		if (graphModel == null) {
			throw new RuntimeException(
					"The GraphModel for this layout cannot be null!");
		}
		graph = graphModel.getGraphVisible();

		if (LayoutLoader.VERBOSE_LAYOUT)
			Cuttlefish.debug(this, "Layout initialized.");
	}

	@Override
	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
	}

	@Override
	public void goAlgo() {
		if (LayoutLoader.VERBOSE_LAYOUT)
			Cuttlefish.debug(this, "");
	}

	@Override
	public boolean canAlgo() {
		return false;
	}

	@Override
	public void endAlgo() {
		if (LayoutLoader.VERBOSE_LAYOUT) {
			Cuttlefish.debug(this, "Layout ended");

			for (Node n : graphModel.getGraph().getNodes()) {
				Cuttlefish.debug(this, n + ": " + n.getNodeData().x() + ", "
						+ n.getNodeData().y());
			}
		}
	}

	@Override
	public LayoutProperty[] getProperties() {
		List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
		return properties.toArray(new LayoutProperty[0]);
	}

	@Override
	public void resetPropertiesValues() {

	}

	@Override
	public LayoutBuilder getBuilder() {
		return layoutBuilder;
	}
}
