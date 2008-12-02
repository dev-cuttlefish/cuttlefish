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
public class SimpleLayoutAlgorithm extends StaticLayout {

	int i = 0;
	
	protected VisVertex createVisVertex(Vertex v) {
		return new VisVertex(v,i*5, 5*i++);
	}

}
