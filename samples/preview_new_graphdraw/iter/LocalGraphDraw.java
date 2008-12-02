/*
 * Created on Jan 7, 2004
 */
package samples.preview_new_graphdraw.iter;

import java.awt.Dimension;

import samples.preview_new_graphdraw.EdgeRenderer;
import samples.preview_new_graphdraw.StaticLayout;
import samples.preview_new_graphdraw.VertexRenderer;
import samples.preview_new_graphdraw.impl.GraphLayoutPanel;
import samples.preview_new_graphdraw.iterablelayouts.UnmovingIterableLayout;
import samples.preview_new_graphdraw.transform.FitOnScreenTransformer;
import samples.preview_new_graphdraw.transform.LayoutTransformer;
import samples.preview_new_graphdraw.transform.SlightMarginTransformer;
import edu.uci.ics.jung.graph.Graph;

/**
 * This is the major class for starting an animated layout runner. Inputs:
 * <ul>
 * <li/>A graph to be laid out <li/>An algorithm for the starting configuration
 * of the graph <li/>An algorithm for iterating on that
 * </ul>
 * 
 * @author danyelf
 */
public class LocalGraphDraw {

    protected GraphLayoutPanel jgp;

    protected IterableLayout layout;

    protected LayoutIterator li;

//    protected AnimatedVertexDragger mouseDragger;

    protected TransformerPipeline pipeline;

    protected boolean staticAnimation = false;

    /**
     * A verbose constructor creates a LocalGraphDraw object, and a panel to
     * show.
     * 
     * @param g
     *            A graph to visualization
     * @param startup
     *            The startup (static) layout that places the notes
     * @param layout
     *            The iterable layout that moves the nodes
     * @param vr
     *            The vertex renderer that draws the nodes
     * @param er
     *            The edge renderer that draws edges
     * @param d
     *            The size to which the panel should be drawn
     * @param prerelax
     *            Should the graph be iterated for a few milliseconds before
     *            the program starts? By default, this will freeze for half a
     *            second. Turn off to start with a messier view but a faster
     *            startup.
     */
    public LocalGraphDraw(Graph g, StaticLayout startup, IterableLayout layout,
            VertexRenderer vr, EdgeRenderer er, Dimension d, boolean prerelax) {
        startup.initializeLocations(d, g);
        this.layout = layout;
        layout.initializeLocationsFromLayout(startup.emit());
        // let's move it forward a couple dozen steps.
        if (prerelax) {
            GraphLayoutPanelUtils.prerelax(500, layout);
        }
        jgp = new GraphLayoutPanel(g, d, vr, er);
        pipeline = jgp.getPipeline();
        addToPipeline(new FitOnScreenTransformer());
        addToPipeline(new SlightMarginTransformer());
    }

    /**
     * A LocalGraphDraw that won't be animated. As such, it doesn't 
     * need an IterableLayout as a startup parameter. (It implements a
     * "static" layout by actually animating very, very slowly--and
     * running the animation through the UnmovingIterableLayout, which
     * actually does nothing.
     * 
     * @param g
     *            A graph to visualization
     * @param startup
     *            The startup (static) layout that places the notes
     * @param vr
     *            The vertex renderer that draws the nodes
     * @param er
     *            The edge renderer that draws edges
     * @param d
     *            The size to which the panel should be drawn
     * @param prerelax
     *            Should the graph be iterated for a few milliseconds before
     *            the program starts? By default, this will freeze for half a
     *            second. Turn off to start with a messier view but a faster
     *            startup.
     */
    public LocalGraphDraw(Graph g, StaticLayout startup, VertexRenderer vr,
            EdgeRenderer er, Dimension d) {
        this(g, startup, UnmovingIterableLayout.getInstance(), vr, er, d, false);
        staticAnimation = true;
    }

    public void addToPipeline(LayoutTransformer lt) {
        pipeline.add(lt);
    }

    public GraphLayoutPanel getPanel() {
        return jgp;
    }

    /**
     *  
     */
    public void start() {
        // last, this is what starts the animation loop
        li = new LayoutIterator(jgp, layout);
        if (staticAnimation) {
            // really, they don't need to update much
            li.setIterationDelay(999999);
        } else {
            li.setIterationDelay(50);
        }
        li.startIterations();
    }

    public void stop() {
        li.stopIterations();
    }

    /**
     * @return  layout iterator
     */
    public LayoutIterator getIterator() {
        return li;
    }

//    /**
//     * @param b
//     */
//    public void setMouseDrag(boolean b) {
//        System.out
//                .println("Warning, mouse dragging does not currently work well.");
//        if (b) {
//            if (mouseDragger != null) return;
//            mouseDragger = new AnimatedVertexDragger(this);
//            this.getPanel().addMouseListener(mouseDragger);
//            this.getPanel().addMouseMotionListener(mouseDragger);
//        } else {
//            this.getPanel().removeMouseListener(mouseDragger);
//            this.getPanel().removeMouseMotionListener(mouseDragger);
//            mouseDragger = null;
//        }
//    }

    /**
     * Returns the iterable layout at the core.
     * USED ONLY BY MOUSE MOVE STUFF
     * @return  iterable layout
     */
    public IterableLayout getLayoutSource() {
        return li.iterableLayout;
    }

    /**
     * @param g
     */
    public void updateGraphTo(Graph g) {
        UpdatableIterableLayout uil = (UpdatableIterableLayout) getLayoutSource();
        getIterator().stopIterations();
        uil.updateGraphToMatch(g);
        jgp.updateGraphToMatch(g);
        getIterator().startIterations();
    }

}