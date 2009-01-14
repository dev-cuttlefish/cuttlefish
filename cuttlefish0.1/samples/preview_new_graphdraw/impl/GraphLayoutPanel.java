/*
 * Created on Dec 8, 2003
 */
package samples.preview_new_graphdraw.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import samples.preview_new_graphdraw.EdgeRenderer;
import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.VertexRenderer;
import samples.preview_new_graphdraw.VisEdge;
import samples.preview_new_graphdraw.VisVertex;
import samples.preview_new_graphdraw.event.ClickListener;
import samples.preview_new_graphdraw.iter.GraphLayoutPanelUtils;
import samples.preview_new_graphdraw.iter.TransformerPipeline;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author danyelf
 */
public class GraphLayoutPanel extends JPanel {

    /** No graph events are fired from this panel */
    public final static GraphLayoutPanelMouseListener.ClickPolicy NO_EVENT_POLICY = new GraphLayoutPanelMouseListener.NoEventPolicy();

    /**
     * Only vertex events are fired from this panel. If a click isn't near a
     * vertex, no events are fired.
     */
    public final static GraphLayoutPanelMouseListener.ClickPolicy VERTEX_ONLY_POLICY = new GraphLayoutPanelMouseListener.VertexEventPolicy();

    /**
     * Only edge events are fired from this panel. If a click isn't near a
     * edge, no events are fired.
     */
    public final static GraphLayoutPanelMouseListener.ClickPolicy EDGE_ONLY_POLICY = new GraphLayoutPanelMouseListener.VertexEventPolicy();

    /**
     * Either an edge event or a vertex event is fired from this panel. If the
     * click is close to a vertex, a vertex event is fired; otherwise, if the
     * click is close to an edge, the edge event.
     */
    public final static GraphLayoutPanelMouseListener.ClickPolicy EDGE_AND_VERTEX_POLICY = new GraphLayoutPanelMouseListener.EdgeAndVertexPolicy();

    /**
     * Both an edge event or a vertex event is fired from this panel. If the
     * click is close to a vertex, a vertex event is fired; if the click is
     * close to an edge, the edge event. (If both, then both may be fired: it
     * is up to the developer to choose a mechansim. For example, some
     * applications will select only vertices if the user is holding down the
     * control button.)
     */
    public final static GraphLayoutPanelMouseListener.ClickPolicy EDGE_AND_VERTEX_BOTH = new GraphLayoutPanelMouseListener.BothEdgeAndVertexPolicy();

    protected GraphLayoutPanelMouseListener.ClickPolicy clickPolicy;

    protected VertexRenderer mVertexRenderer;

    protected EdgeRenderer mEdgeRenderer;

    protected volatile EmittedLayout mLayout;

    protected TransformerPipeline pipeline;

    /**
     * Creates a GraphLayout panel. It will set its preferred size to d, its
     * local graph to g
     * 
     * @param g
     * @param d
     * @param vr
     * @param er
     */
    public GraphLayoutPanel(Graph g, Dimension d, VertexRenderer vr,
            EdgeRenderer er) {
        this.clickPolicy = VERTEX_ONLY_POLICY;
        addMouseListener(getDefaultMouseListener());
        this.setPreferredSize(d);
        this.setSize(d);
        //		addComponentListener(getDefaultResizeHandler());
        mVertexRenderer = vr;
        mEdgeRenderer = er;
        this.pipeline = new TransformerPipeline(this);
        setBackground(Color.white);
    }

    /**
     * By default, returns a JunGraphPanelMouseListener. Override this to
     * install your own MouseListener instead of the one provided. (You can
     * always add another listener with affecting this one.)
     * 
     */
    protected MouseListener getDefaultMouseListener() {
        return new GraphLayoutPanelMouseListener(this);
    }

    /**
     * This method paints the component. It does so by calling renderEdge and
     * renderVertex on every VERTEX and every EDGE in the CURRENT GRAPH. Note
     * that this will crash if the current graph has mutated.
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (mLayout == null) return;
        EmittedLayout localLayout = mLayout;
        for (Iterator iter = localLayout.visEdgeMap.values().iterator(); iter
                .hasNext();) {
            VisEdge eee = (VisEdge) iter.next();
            mEdgeRenderer.renderEdge(graphics, eee);
        }
        for (Iterator iter = localLayout.visVertexMap.values().iterator(); iter
                .hasNext();) {
            VisVertex vvv = (VisVertex) iter.next();
            mVertexRenderer.renderVertex(graphics, vvv);
        }
    }

    // ============================ EVENT MECHANISMS ====================
    public void addClickListener(ClickListener cl) {
        if (this.listeners == null) {
            listeners = new LinkedList();
        }
        listeners.add(cl);
    }

    public void setClickPolicy(GraphLayoutPanelMouseListener.ClickPolicy policy) {
        this.clickPolicy = policy;
    }

    List listeners = null;

    //    protected StaticVertexDragger mouseDragger;

    protected EmittedLayout cleanOriginal;

    /**
     * (Called only by vertexdrag.)
     * 
     */
    public EmittedLayout getGraphLayout() {
        return mLayout;
    }

    /**
     * We maintain, in memory, two layouts. One of them is the one currently
     * being drawn. It's been distorted, warped, and bumped through the
     * pipeline. The other is a clean original.
     * 
     * @param lr2
     */
    public void setLayoutDisplay(EmittedLayout lr2) {
        this.cleanOriginal = lr2;
        resizeLayouts();
    }

    public TransformerPipeline getPipeline() {
        return pipeline;
    }

    static int i = 0;

    /**
     *  
     */

    public void resizeLayouts() {
        EmittedLayout tempLayout = GraphLayoutPanelUtils.copy(cleanOriginal);
        pipeline.transformSequentially(tempLayout);
        mLayout = tempLayout;
        repaint();
    }

    /**
     * @param g
     */
    public void updateGraphToMatch(final Graph g) {
    }

    //    ////////////////////////
    //    /////// POORLY_WORKING_CODE
    //    /////////////
    //    public void setMouseDrag(boolean b) {
    //        if (b) {
    //            if (mouseDragger != null) return;
    //            mouseDragger = new StaticVertexDragger(this);
    //            addMouseListener(mouseDragger);
    //            addMouseMotionListener(mouseDragger);
    //        } else {
    //            removeMouseListener(mouseDragger);
    //            removeMouseMotionListener(mouseDragger);
    //            mouseDragger = null;
    //        }
    //    }

}
