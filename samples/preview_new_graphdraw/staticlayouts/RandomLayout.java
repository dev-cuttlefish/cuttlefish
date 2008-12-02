/*
 * Created on Dec 8, 2003
 */
package samples.preview_new_graphdraw.staticlayouts;

import samples.preview_new_graphdraw.StaticLayout;
import samples.preview_new_graphdraw.VisVertex;
import edu.uci.ics.jung.graph.Vertex;

/**
 * @author danyelf
 */
public class RandomLayout extends StaticLayout {

	private double getHeight() {
		return getScreenSize().getHeight();
	}

	private double getWidth() {
		return getScreenSize().getWidth();
	}

	protected VisVertex createVisVertex(Vertex v) {
		double x = Math.random() * getWidth();
		double y = Math.random() * getHeight();

		return new VisVertex(v, x, y);
	}

}
