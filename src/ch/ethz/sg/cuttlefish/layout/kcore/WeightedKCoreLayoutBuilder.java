package ch.ethz.sg.cuttlefish.layout.kcore;

import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;

public class WeightedKCoreLayoutBuilder implements LayoutBuilder {

	private double alpha = 1.0;
	private double beta = 1.0;

	@Override
	public WeightedKCoreLayout buildLayout() {
		return new WeightedKCoreLayout(this, alpha, beta);
	}

	@Override
	public String getName() {
		return "Weighted K-Core";
	}

	@Override
	public LayoutUI getUI() {
		// No LayoutUI needed in Cuttlefish
		return null;
	}
}
