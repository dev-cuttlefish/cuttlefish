/*
 * Created on Mar 22, 2004
 */
package samples.preview_new_graphdraw.iterablelayouts;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.iter.IterableLayout;
import edu.uci.ics.jung.exceptions.FatalException;

/**
 * This class runs one iterable layout until it is finished, then
 * starts on another (for either K iterations or until finished). 
 * This allows people like Joshua to do "KKLayout until done, plus
 * just a few iterations of SpringLayout to clean up."
 * 
 * @author danyelf
 */
public class WrappedIterableLayout extends IterableLayout {

    protected IterableLayout next;
    protected IterableLayout start;
    protected final int frames;
    protected int thisFrame = 0;
    
    boolean currentlyInStart = true;

    public WrappedIterableLayout( IterableLayout start, IterableLayout next ) {
        this.start = start;
        this.next = next;
        this.frames = -1;
        if (! start.isFinite()) {
            throw new IllegalArgumentException( start + " is not finite; you'll never see the next");
        }
    }
    
    public WrappedIterableLayout( IterableLayout start, IterableLayout next , int frames ) {
        this.start = start;
        this.next = next;
        this.frames = frames;        
        if (! start.isFinite()) {
            throw new IllegalArgumentException( start + " is not finite; you'll never see the next");
        }
    }
        
    /* (non-Javadoc)
     * @see samples.preview_new_graphdraw.iter.IterableLayout#iterationsAreDone()
     */
    public boolean iterationsAreDone() {
        if ( frames > 0 ) {
            return thisFrame > frames;
        } else 
            return next.iterationsAreDone();
    }

    /* (non-Javadoc)
     * @see samples.preview_new_graphdraw.iter.IterableLayout#isFinite()
     */
    public boolean isFinite() {
        return (frames > 0) || next.isFinite();
    }

    /* (non-Javadoc)
     * @see samples.preview_new_graphdraw.iter.IterableLayout#calculate()
     */
    public void advance() {
        if ( currentlyInStart ) {
            if (! start.iterationsAreDone() )                
                start.advance();
            else {
                // start is finished; let's check the next
                // layout out.
                EmittedLayout seedOfTheNext = start.emit();
                next.initializeLocationsFromLayout(seedOfTheNext);
                currentlyInStart = false;
            }
        } else {
            next.advance();
            thisFrame ++;
        }        
    }
    
    public EmittedLayout emit() {
        EmittedLayout rv;
        if ( currentlyInStart ) {
            rv = start.emit();
        } else {
            rv = next.emit();
        }
        if ( rv == null ) {
            throw new FatalException("Layouts should not generate null");
        }
        return rv;
    }

    public void initializeLocationsFromLayout(EmittedLayout inputLayout) {
        start.initializeLocationsFromLayout( inputLayout );
    }

    /* (non-Javadoc)
     * @see samples.preview_new_graphdraw.iter.IterableLayout#calculate()
     */
    protected void calculate() {
        throw new FatalException ("Calculate should not be called in a WrappedIterableLayout");
    }

}
