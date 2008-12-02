/*
 * Created on Mar 23, 2004
 */
package samples.preview_new_graphdraw.transform;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.Map;

import samples.preview_new_graphdraw.*;
import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.VisEdge;
import samples.preview_new_graphdraw.VisVertex;
import samples.preview_new_graphdraw.test.CircleEdge;

/**
 * 
 * @author danyelf
 */
public class CrookedLineTransformer implements LayoutTransformer {

	/**
	 * @see samples.preview_new_graphdraw.transform.LayoutTransformer#adjustSize(java.awt.Dimension)
	 */
	public void adjustSize(Dimension d) {
		return;
	}
	/**
	 * @see samples.preview_new_graphdraw.transform.LayoutTransformer#transform(samples.preview_new_graphdraw.EmittedLayout)
	 */
	public EmittedLayout transform(EmittedLayout el) {
		// ok, this one's big
		// for each edge, replace it with a crooked edge that does the same thing
		for (Iterator iter = el.visEdgeMap.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry) iter.next();
			VisEdge ve = (VisEdge) me.getValue();
			me.setValue(new CircleEdge(ve.getEdge(), (VisVertex) ve.getBack(), (VisVertex) ve.getFront()));
		}
		return el;
	}
    /**
     * This method allows inversion, by doing nothing.
     * 
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#supportsInvert()
     */
    public boolean supportsInvert() {
        return true;
    }

    /**
     * Since CrookedLine doesn't modify the coordinates, neither does this.
     * 
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#invert(samples.preview_new_graphdraw.Coordinates)
     */
    public Coordinates invert(Coordinates in) {
        return in;
    }
}
