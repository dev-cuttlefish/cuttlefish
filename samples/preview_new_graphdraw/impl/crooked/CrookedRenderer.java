/*
 * Created on Jan 7, 2004
 */
package samples.preview_new_graphdraw.impl.crooked;

import java.awt.Color;
import java.awt.Graphics;

import samples.preview_new_graphdraw.Coordinates;
import samples.preview_new_graphdraw.EdgeRenderer;
import samples.preview_new_graphdraw.VertexRenderer;
import samples.preview_new_graphdraw.VisEdge;
import samples.preview_new_graphdraw.VisVertex;
import edu.uci.ics.jung.graph.Element;

/**
 * @author danyelf
 */
public class CrookedRenderer implements EdgeRenderer, VertexRenderer {

	int i = 0;

	public void renderEdge(Graphics g, VisEdge ec) {
		try {
			CrookedSpringLayout.CrookedSpringEdge cve =
				(CrookedSpringLayout.CrookedSpringEdge) ec;

			if (selected(ec.getEdge())) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.black);
			}
			Coordinates v1 = cve.getFront();
			Coordinates c = cve.calcMidpoint();
			Coordinates v2 = cve.getBack();
			g.drawLine(
				(int) v1.getX(),
				(int) v1.getY(),
				(int) c.getX(),
				(int) c.getY());
			g.drawLine(
				(int) c.getX(),
				(int) c.getY(),
				(int) v2.getX(),
				(int) v2.getY());
		} catch (ClassCastException cce) {
			Coordinates v1 = ec.getFront();
			Coordinates v2 = ec.getBack();
			g.setColor( Color.blue );
			g.drawLine(
					(int) v1.getX(),
					(int) v1.getY(),
					(int) v2.getX(),
					(int) v2.getY());
		}
	}

	/**
	 * @see samples.preview_new_graphdraw.VertexRenderer#renderVertex(java.awt.Graphics,
	 *      samples.preview_new_graphdraw.VisVertex)
	 */
	public void renderVertex(Graphics g, VisVertex vc) {
		if (selected(vc.getVertex())) {
			g.setColor(Color.red);
		} else {
			g.setColor(Color.black);
		}
		g.fillOval((int) vc.getX() - 3, (int) vc.getY() - 3, 6, 6);
	}

	/**
	 * @param edge
	 * @return
	 */
	private boolean selected(Element grapobject) {
		return (grapobject.getUserDatum("SELECTED") != null);
	}

}
