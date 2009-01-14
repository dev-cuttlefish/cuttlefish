/*
 * Created on Feb 5, 2004
 */
package samples.preview_new_graphdraw.transform;

import java.awt.Dimension;
import java.util.Iterator;

import samples.preview_new_graphdraw.Coordinates;
import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.VisVertex;

/**
 * This resizer ensures that there's a little space at the margins. This is
 * done by percentage rather than by absolute numbers.
 * 
 * created March 20, 2004
 * 
 * @author danyelf
 */
public class SlightMarginTransformer implements LayoutTransformer {

    private Dimension d;

    /**
     * Creates an instance of a slight margin transformer. By default, starts
     * at 90% of size (= 5% margins on all sides)
     */
    public SlightMarginTransformer() {
    }

    /**
     * Creates an instance of a slight margin transformer with the given
     * percentage. 
     * 
     * @param percentage the value (should be between 0 and 1; 1 means no margin;
     * 0.5 means the graph is half the size of its window).
     */
    public SlightMarginTransformer(double percentage) {
        this.percentage = percentage;
    }

    double percentage = 0.9;

    /**
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#transform(samples.preview_new_graphdraw.EmittedLayout)
     */
    public EmittedLayout transform(EmittedLayout el) {
        for (Iterator iter = el.visVertexMap.values().iterator(); iter
                .hasNext();) {
            VisVertex vv = (VisVertex) iter.next();
            //            vv.x *= 0.5;
            //            vv.y *= 0.5;
            vv.x += ((1 - percentage) / 2) * d.getWidth();
            vv.y += ((1 - percentage) / 2) * d.getHeight();
        }
        return el;
    }

    /**
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#adjustSize(java.awt.Dimension)
     */
    public void adjustSize(Dimension d) {
//        System.out.println("Incoming size = " + d);
        d.setSize(d.getWidth() * percentage, d.getHeight() * percentage);
        this.d = d;
//        System.out.println("    Out size = " + d);
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
        in.x -= ((1 - percentage) / 2) * d.getWidth();
        in.y -= ((1 - percentage) / 2) * d.getHeight();
        return in;
    }
}
