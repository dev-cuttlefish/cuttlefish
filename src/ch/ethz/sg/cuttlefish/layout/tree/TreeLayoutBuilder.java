package ch.ethz.sg.cuttlefish.layout.tree;

import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;

public class TreeLayoutBuilder implements LayoutBuilder {

	public TreeLayoutBuilder() {
	}

	@Override
	public TreeLayout buildLayout() {
		return new TreeLayout(this);
	}

	@Override
	public String getName() {
		return "Tree";
	}

	@Override
	public LayoutUI getUI() {
		// No LayoutUI needed in Cuttlefish
		return null;
	}

	public void setParameters(Object p) {
	}

}
