/*
 * Created on Dec 11, 2001
 */
package samples.preview_new_graphdraw;

import java.awt.Graphics;

/**
 * @author Danyel Fisher, Scott White
 */
public interface VertexRenderer {

	/**
	 * @param layoutResult
	 * @param vv
	 */
	void renderVertex(Graphics g, VisVertex vc);

}
