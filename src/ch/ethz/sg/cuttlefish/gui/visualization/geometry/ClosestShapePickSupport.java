package ch.ethz.sg.cuttlefish.gui.visualization.geometry;

import java.awt.geom.Point2D;

import ch.ethz.sg.cuttlefish.gui.visualization.NetworkRenderer;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class ClosestShapePickSupport {

	private NetworkRenderer nr;

	public ClosestShapePickSupport(NetworkRenderer nr) {
		this.nr = nr;
	}

	public Vertex selectVertexByPoint(Point2D p) {
		Vertex grabbed = null;
		double minSize = Double.MAX_VALUE;
		double scaleFactor = nr.getScaleFactor();

		for (Vertex v : nr.getVertices()) {
			double size = v.getSize() / scaleFactor;

			if (v.containsPoint(p.getX(), p.getY(), scaleFactor)) {
				if (size < minSize) {
					grabbed = v;
					minSize = size;
				}
			}
		}

		return grabbed;
	}

	/**
	 * Not supported yet.
	 * 
	 * @param p
	 * @return
	 */
	public Edge selectEdgeByPoint(Point2D p) {
		Edge grabbed = null;



		return grabbed;
	}
}
