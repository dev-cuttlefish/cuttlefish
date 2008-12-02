/*
 * Created on Dec 11, 2001
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package samples.preview_new_graphdraw.impl;

import java.awt.Color;
import java.awt.Graphics;

import samples.preview_new_graphdraw.VertexRenderer;
import samples.preview_new_graphdraw.VisVertex;

/**
 * @author Scott
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SimpleVertexRenderer implements VertexRenderer {
	
	public void renderVertex(Graphics g, VisVertex vc) {
		g.setColor( Color.black );
		g.fillOval((int) vc.getX()-3, (int) vc.getY()-3, 6, 6);
	}

}
