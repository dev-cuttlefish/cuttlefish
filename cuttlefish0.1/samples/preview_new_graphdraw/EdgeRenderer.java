/*
 * Created on Dec 11, 2001
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package samples.preview_new_graphdraw;

import java.awt.Graphics;

/**
 * An EdgeRenderer is responsible for rendering every VisEdge that comes its
 * way
 * @author Danyel Fisher, Scott White
 */
public interface EdgeRenderer {

	/**
	 * @param layoutResult
	 * @param ve
	 */
	void renderEdge(Graphics g, VisEdge ec);

}
