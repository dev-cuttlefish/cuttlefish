package ch.ethz.sg.cuttlefish.layout.circle;

import java.awt.Dimension;

import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;

public class CircleLayoutBuilder implements LayoutBuilder {

	private Dimension dimension = new Dimension(1000, 800);

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	@Override
	public CircleLayout buildLayout() {
		return new CircleLayout(this, dimension);
	}

	@Override
	public String getName() {
		return "Circle";
	}

	@Override
	public LayoutUI getUI() {
		// No LayoutUI needed in Cuttlefish
		return null;
	}

}
