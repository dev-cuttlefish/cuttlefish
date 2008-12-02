/*
 * Created on Mar 22, 2004
 */
package samples.preview_new_graphdraw.transform;

import java.awt.Dimension;

import samples.preview_new_graphdraw.Coordinates;
import samples.preview_new_graphdraw.EmittedLayout;


/**
 * 
 * @author danyelf
 */
public interface LayoutTransformer {

    /**
     * Adjusts this transformer to be working in this size a space
     */
    public void adjustSize( Dimension d );
    
    /**
     * Transforms the given layout. May (but needn't) return the same
     * layout object, albeit somewhat stomped-upon.
     */
    public EmittedLayout transform( EmittedLayout el );
    
    /**
     * Most functions should support invert. If your layout transformer
     * can't be inverted, this should return false. Note that having even
     * one layout be un-invertible makes the whole pipeline un-invertble.
     * 
     * @return true if the layout can be inverted.
     */
    public boolean supportsInvert();
    
    /**
     * This does the *reverse* of the trasformation, for a given point. 
     * This should be quite fast, as it's done in interactive time (e.g.
     * while a user is dragging around a vertex).
     * 
     * @param in
     */
    public Coordinates invert( Coordinates in );
    
}
