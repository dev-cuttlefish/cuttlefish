/*
 * Created on Apr 23, 2004
 */
package samples.preview_new_graphdraw.test;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.VisEdge;
import samples.preview_new_graphdraw.VisVertex;
import samples.preview_new_graphdraw.iterablelayouts.InterpolatingIterableLayout;

/**
 * created Apr 23, 2004
 * 
 * @author danyelf
 */
public class LinearInterpolatingLayout extends InterpolatingIterableLayout {

    /**
     * @param endE
     * @param i
     */
    public LinearInterpolatingLayout(EmittedLayout endE, int i) {
        super(endE, i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see samples.preview_new_graphdraw.iterablelayouts.InterpolatingIterableLayout#interpolateV(samples.preview_new_graphdraw.VisVertex,
     *      samples.preview_new_graphdraw.VisVertex, int, int)
     */
    protected VisVertex interpolateV(VisVertex startV, VisVertex endV,
            int thisFrame2, int numFrames2) {
        VisVertex vv = startV.copy();
        double frac = (1.0 * thisFrame2) / numFrames2;
        vv.x = ((startV.getX() * (1 - frac)) + (endV.getX() * frac));
        vv.y = ((startV.getY() * (1 - frac)) + (endV.getY() * frac));
        return vv;

    }

    /*
     * (non-Javadoc)
     * 
     * @see samples.preview_new_graphdraw.iterablelayouts.InterpolatingIterableLayout#interpolateE(samples.preview_new_graphdraw.VisEdge,
     *      samples.preview_new_graphdraw.VisEdge,
     *      samples.preview_new_graphdraw.VisVertex,
     *      samples.preview_new_graphdraw.VisVertex, int, int)
     */
    protected VisEdge interpolateE(VisEdge startE, VisEdge endE, VisVertex f,
            VisVertex s, int thisFrame2, int numFrames2) {

        if (startE != null) {
            return startE.copy(f, s);
        } else
            return null;
    }

}