package ch.ethz.sg.cuttlefish.gui.visualization.mouse;

import java.awt.geom.Point2D;

import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public interface PickSupport {
	
	public Vertex pickVertex(Point2D pickPoint);
	
	public Edge pickEdge(Point2D pickPoint);

}
