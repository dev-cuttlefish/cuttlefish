/*
 * Created on Mar 22, 2004
 */
package samples.preview_new_graphdraw.transform;

import java.awt.Dimension;
import java.util.Iterator;

import samples.preview_new_graphdraw.Coordinates;
import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.VisVertex;

/**
 * Changes an EmittedLayout from a fixed size to a new fixed size. Vertices
 * are scaled directly. 
 * 
 * @author danyelf
 */
public class PipelineResizingTransformer implements LayoutTransformer {

    private Dimension elPreviousSize;
    private Dimension viewingDimension;

    public PipelineResizingTransformer() {
    }

    /**
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#transform(samples.preview_new_graphdraw.EmittedLayout)
     */
    public EmittedLayout transform(EmittedLayout el) {
        // no size difference: no reason to change anything.
        if( el.screenSize.equals(viewingDimension.getSize())) return el;
        
        for (Iterator iter = el.visVertexMap.values().iterator(); iter
                .hasNext();) {
            VisVertex vv = (VisVertex) iter.next();
            vv.scale(el.screenSize, viewingDimension);
        }
        elPreviousSize = el.screenSize;
        el.screenSize = viewingDimension.getSize();
        return el;
    }

    /**
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#adjustSize(java.awt.Dimension)
     */
    public void adjustSize(Dimension d) {
        this.viewingDimension = d;
    }

    /**
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#supportsInvert()
     */
    public boolean supportsInvert() {
        return true;
    }

    /**
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#invert(samples.preview_new_graphdraw.Coordinates)
     */
    public Coordinates invert(Coordinates in) {
        in.scale(viewingDimension, elPreviousSize);
        return in;
    }

}
