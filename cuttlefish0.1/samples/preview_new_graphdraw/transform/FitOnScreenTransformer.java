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
 * This implementation forces the graph to render within the screen size by
 * resizing the 
 * 
 * @author danyelf
 */
public class FitOnScreenTransformer implements LayoutTransformer {

    /**
     * created Feb 5, 2004
     * 
     * @author danyelf
     */
    public class ScaleAndTranslate {

        private double scaleX;

        private double scaleY;

        private double minX;

        private double minY;

        /**
         * @param minX
         * @param minY
         * @param maxX
         * @param maxY
         * @param i
         * @param j
         */
        public ScaleAndTranslate(double minX, double minY, double maxX,
                double maxY, int xSpace, int ySpace) {
            this.minY = minY;
            this.minX = minX;
            double width = maxX - minX;
            double height = maxY - minY;
            scaleX = xSpace / width;
            scaleY = ySpace / height;
        }

        /**
         * @param vv2
         */
        public void relocate(VisVertex vv2) {
            vv2.x = forwardX(vv2.x);
            vv2.y = forwardY(vv2.y);
        }

        public void invert( Coordinates vv) {
            vv.x = (vv.x / scaleX) + minX;
            vv.y = (vv.y / scaleY) + minY;
        }
        
        private double forwardY(double d) {
            return (d - minY) * scaleY;
        }

        private double forwardX(double d) {
            return (d - minX) * scaleX;
        }
    }

    private Dimension d;

    protected ScaleAndTranslate checkbounds(EmittedLayout el,
            Dimension screenSize) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        for (Iterator iter = el.visVertexMap.values().iterator(); iter
                .hasNext();) {
            VisVertex vv = (VisVertex) iter.next();
            if (minX > vv.getX()) minX = vv.getX();
            if (minY > vv.getY()) minY = vv.getY();
            if (maxX < vv.getX()) maxX = vv.getX();
            if (maxY < vv.getY()) maxY = vv.getY();
        }
        return new ScaleAndTranslate(minX, minY, maxX, maxY, screenSize.width,
                screenSize.height);
    }

    /**
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#transform(samples.preview_new_graphdraw.EmittedLayout)
     */
    public EmittedLayout transform(EmittedLayout el) {
        ScaleAndTranslate st = checkbounds(el, d);
        for (Iterator iter = el.visVertexMap.values().iterator(); iter
                .hasNext();) {
            VisVertex vv = (VisVertex) iter.next();
            st.relocate(vv);
        }
        return el;
    }

    /**
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#adjustSize(java.awt.Dimension)
     */
    public void adjustSize(Dimension d) {
        //        System.out.println("Setting to size " + d );
        this.d = d;
    }

    /**
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#supportsInvert()
     */
    public boolean supportsInvert() {
        return true;
    }

    /**
     * Modifies the input Coordinates object to reverse the input transformation.
     * 
     * @see samples.preview_new_graphdraw.transform.LayoutTransformer#invert(samples.preview_new_graphdraw.Coordinates)
     */
    public Coordinates invert(Coordinates in) {
        return invert(in);
    }
}
