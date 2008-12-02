/*
 * Created on Dec 11, 2001
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package samples.preview_new_graphdraw.impl;

import java.awt.Color;
import java.awt.Graphics;

import samples.preview_new_graphdraw.Coordinates;
import samples.preview_new_graphdraw.EdgeRenderer;
import samples.preview_new_graphdraw.VisEdge;

/**
 * @author Scott
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SimpleEdgeRenderer implements EdgeRenderer {
	
	private Color color;

	public SimpleEdgeRenderer() {		
		this.color = Color.black;
	}
	
	public SimpleEdgeRenderer( Color c ) {
		this.color = c;
	}
	
	public void renderEdge(Graphics g, VisEdge ec) {
		g.setColor( color );
			//if ( c1 == null  || c2 == null ) {
			//	System.out.println( oneEnd + "-" + otherEnd + "--" + c1  + "-" + c2 );
			//}
		Coordinates v1 = ec.getFront();
		Coordinates v2 = ec.getBack();
		g.drawLine((int) v1.getX(), (int) v1.getY(), (int) v2.getX(), (int) v2.getY());
	}

}
