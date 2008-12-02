/*
 * Created on Apr 23, 2004
 */
package samples.preview_new_graphdraw.staticlayouts;

import java.awt.Dimension;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.StaticLayout;
import samples.preview_new_graphdraw.VisVertex;
import samples.preview_new_graphdraw.iter.IterableLayout;
import edu.uci.ics.jung.exceptions.FatalException;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;


/**
 * The IterableToStaticLayout allows a graph to start fully
 * visualized and advanced, at the cost of some time.
 * 
 * created Apr 23, 2004
 * @author danyelf
 */
public class IterableToStaticLayout extends StaticLayout {

    protected IterableLayout iterableLayout;
    protected StaticLayout startingLayout;

    public IterableToStaticLayout( StaticLayout starting, IterableLayout iter ) {
        this.startingLayout = starting;
        this.iterableLayout = iter;
        if (! iter.isFinite() ) 
            throw new IllegalArgumentException("Can only use IterableToStaticLayout with iterable layouts");
    }
    
    public StaticLayout initializeLocations(Dimension d, Graph g) {
        // run the graph forward
        startingLayout.initializeLocations(d, g);
        EmittedLayout emitted = startingLayout.emit();
        iterableLayout.initializeLocationsFromLayout(emitted);
        while( ! iterableLayout.iterationsAreDone() ) {
            iterableLayout.advance();
        }

        EmittedLayout el = iterableLayout.emit();
        this.visEdgeMap = el.visEdgeMap;
        this.visVertexMap = el.visVertexMap;
        this.screenSize = el.screenSize;
        
        return this;
    }

    
    /* (non-Javadoc)
     * @see samples.preview_new_graphdraw.StaticLayout#createVisVertex(edu.uci.ics.jung.graph.Vertex)
     */
    protected VisVertex createVisVertex(Vertex v) {
        throw new FatalException("createvisvertex should not be called by iterabletostaticlayout");
    }

}
