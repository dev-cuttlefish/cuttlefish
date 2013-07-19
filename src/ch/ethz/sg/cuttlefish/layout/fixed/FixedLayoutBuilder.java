package ch.ethz.sg.cuttlefish.layout.fixed;

import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;

public class FixedLayoutBuilder implements LayoutBuilder {

	@Override
	public FixedLayout buildLayout() {
		return new FixedLayout(this);
	}

	@Override
	public String getName() {
		return "Fixed";
	}

	@Override
	public LayoutUI getUI() {
		// No LayoutUI needed in Cuttlefish
		return null;
	}

}
