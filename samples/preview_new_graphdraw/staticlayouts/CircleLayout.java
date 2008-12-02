/*
 * Created on Dec 8, 2003
 */
package samples.preview_new_graphdraw.staticlayouts;

import java.awt.Dimension;

import samples.preview_new_graphdraw.StaticLayout;
import samples.preview_new_graphdraw.VisVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

/**
 * @author danyelf
 */
public class CircleLayout extends StaticLayout {


	double radius = 0;

	int total_vertices = 0;
	int vertex_number;

	private double getHeight()
	{
		return getScreenSize().getHeight();
	}

	private double getWidth()
	{
		return getScreenSize().getWidth();
	}

	public StaticLayout initializeLocations(Dimension d, Graph g)
	{
		this.total_vertices = g.getVertices().size();
		vertex_number = 0;

		double height = d.getHeight();
		double width = d.getWidth();

		radius = 0.45 * Math.min(height, width);

		return super.initializeLocations(d, g);
	}

	protected VisVertex createVisVertex(Vertex v)
	{
		double angle = (2 * Math.PI * vertex_number++) / total_vertices;
		double x = Math.cos(angle) * radius + getWidth() / 2;
		double y = Math.sin(angle) * radius + getHeight() / 2;

		return new VisVertex(v, x, y);
	}

}
