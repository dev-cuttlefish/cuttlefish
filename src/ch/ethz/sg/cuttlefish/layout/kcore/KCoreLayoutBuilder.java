package ch.ethz.sg.cuttlefish.layout.kcore;

import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;

public class KCoreLayoutBuilder implements LayoutBuilder {

	@Override
	public KCoreLayout buildLayout() {
		return new KCoreLayout(this);
	}

	@Override
	public String getName() {
		return "K-Core";
	}

	@Override
	public LayoutUI getUI() {
		// No LayoutUI needed in Cuttlefish
		return null;
	}

}
