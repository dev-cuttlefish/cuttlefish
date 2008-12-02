/*
 * Created on Jan 7, 2004
 */
package samples.preview_new_graphdraw.test;

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
public class CircleRenderer implements EdgeRenderer, VertexRenderer {

	int i = 0;

	public void renderEdge(Graphics g, VisEdge ec) {
		try {
			CircleEdge cve = (CircleEdge) ec;

//			g.setColor( Color.green );
//			plainDraw( g, ec );
			
			double r = cve.getRadius();
			
			
			Coordinates tl = cve.getCenter();

//			g.setColor( Color.lightGray );
//			g.drawArc(
//					(int) (tl.getX() - r),
//					(int) (tl.getY() - r),
//					(int) (2 * r),
//					(int) (2 * r),
//					0,
//					360);
			
			if (selected(ec.getEdge())) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.black);
			}
			
			double theta = cve.getStartAngle();
			int angle = (int) ( theta * 180 / Math.PI ) ;
			
			Color c = new Color( 247,210,130, 50 );
			g.setColor( c );
			g.fillArc(
					(int) (tl.getX() - r),
					(int) (tl.getY() - r),
					(int) (2 * r),
					(int) (2 * r),
					- angle,
					180);

			g.setColor(new Color(247,210,130));
			g.drawArc(
				(int) (tl.getX() - r),
				(int) (tl.getY() - r),
				(int) (2 * r),
				(int) (2 * r),
				- angle,
				180);
		} catch (ClassCastException cce) {
			g.setColor(Color.lightGray);
			plainDraw( g, ec );
		}
	}

	/**
	 * @param g
	 * @param ec
	 */
	private void plainDraw(Graphics g, VisEdge ec) {
		
		if (selected(ec.getEdge())) {
			g.setColor(Color.red);
		}
		
		Coordinates v1 = ec.getFront();
		Coordinates v2 = ec.getBack();
		g.drawLine((int) v1.getX(), (int) v1.getY(), (int) v2.getX(), (int) v2.getY());
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
		boolean b = (grapobject.getUserDatum("SELECTED") != null);
		return b;
	}

}
