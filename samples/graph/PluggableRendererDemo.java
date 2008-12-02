/*
 * Copyright (c) 2004, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 *
 * Created on Nov 7, 2004
 */
package samples.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;

import org.apache.commons.collections.Predicate;

import edu.uci.ics.jung.algorithms.importance.VoltageRanker;
import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedEdge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.AbstractVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.ConstantEdgeStringer;
import edu.uci.ics.jung.graph.decorators.ConstantVertexStringer;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeFontFunction;
import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.GradientEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.NumberEdgeValue;
import edu.uci.ics.jung.graph.decorators.NumberEdgeValueStringer;
import edu.uci.ics.jung.graph.decorators.NumberVertexValue;
import edu.uci.ics.jung.graph.decorators.NumberVertexValueStringer;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberEdgeValue;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberVertexValue;
import edu.uci.ics.jung.graph.decorators.VertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.VertexFontFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexSizeFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.decorators.VertexStrokeFunction;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.predicates.ContainsUserDataKeyVertexPredicate;
import edu.uci.ics.jung.graph.predicates.SelfLoopEdgePredicate;
import edu.uci.ics.jung.random.generators.BarabasiAlbertGenerator;
import edu.uci.ics.jung.utils.MutableDouble;
import edu.uci.ics.jung.utils.PredicateUtils;
import edu.uci.ics.jung.utils.TestGraphs;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.HasGraphLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PickSupport;
import edu.uci.ics.jung.visualization.PickedInfo;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.transform.LayoutTransformer;
import edu.uci.ics.jung.visualization.transform.Transformer;


/**
 * Shows off some of the capabilities of <code>PluggableRenderer</code>.
 * This code provides examples of different ways to provide and
 * change the various functions that provide property information
 * to the renderer.
 * 
 * <p>This demo creates a random mixed-mode graph with random edge
 * weights using <code>TestGraph.generateMixedRandomGraph</code>.
 * It then runs <code>VoltageRanker</code> on this graph, using half
 * of the "seed" vertices from the random graph generation as 
 * voltage sources, and half of them as voltage sinks.</p>
 * 
 * <p>What the controls do:
 * <ul>
 * <li/>Mouse controls:
 * <ul>
 * <li/>If your mouse has a scroll wheel, scrolling forward zooms out and 
 * scrolling backward zooms in.
 * <li/>Left-clicking on a vertex or edge selects it, and unselects all others.
 * <li/>Middle-clicking on a vertex or edge toggles its selection state.
 * <li/>Right-clicking on a vertex brings up a pop-up menu that allows you to
 * increase or decrease that vertex's transparency.
 * <li/>Left-clicking on the background allows you to drag the image around.
 * <li/>Hovering over a vertex tells you what its voltage is; hovering over an
 * edge shows its identity; hovering over the background shows an informational 
 * message.
</ul>
 * <li/>Vertex stuff:
 * <ul>
 * <li/>"vertex seed coloring": if checked, the seed vertices are colored blue, 
 * and all other vertices are colored red.  Otherwise, all vertices are colored
 * a slightly transparent red (except the currently "picked" vertex, which is
 * colored transparent purple).
 * <li/>"vertex selection stroke highlighting": if checked, the picked vertex
 * and its neighbors are all drawn with heavy borders.  Otherwise, all vertices
 * are drawn with light borders.
 * <li/>"show vertex ranks (voltages)": if checked, each vertex is labeled with its
 * calculated 'voltage'.  Otherwise, vertices are unlabeled.
 * <li/>"vertex degree shapes": if checked, vertices are drawn with a polygon with
 * number of sides proportional to its degree.  Otherwise, vertices are drawn
 * as ellipses.
 * <li/>"vertex voltage size": if checked, vertices are drawn with a size 
 * proportional to their voltage ranking.  Otherwise, all vertices are drawn 
 * at the same size.
 * <li/>"vertex degree ratio stretch": if checked, vertices are drawn with an
 * aspect ratio (height/width ratio) proportional to the ratio of their indegree to
 * their outdegree.  Otherwise, vertices are drawn with an aspect ratio of 1.
 * <li/>"filter vertices of degree &lt; 4": if checked, does not display any vertices
 * (or their incident edges) whose degree in the original graph is less than 4; 
 * otherwise, all vertices are drawn.
 * </ul>
 * <li/>Edge stuff:
 * <ul>
 * <li/>"edge shape": selects between lines, wedges, quadratic curves, and cubic curves
 * for drawing edges.  
 * <li/>"fill edge shapes": if checked, fills the edge shapes.  This will have no effect
 * if "line" is selected.
 * <li/>"edge paint": selects between solid colored edges, and gradient-painted edges.
 * Gradient painted edges are darkest in the middle for undirected edges, and darkest
 * at the destination for directed edges.
 * <li/>"show edges": only edges of the checked types are drawn.
 * <li/>"show arrows": only arrows whose edges are of the checked types are drawn.
 * <li/>"edge weight highlighting": if checked, edges with weight greater than
 * a threshold value are drawn using thick solid lines, and other edges are drawn
 * using thin gray dotted lines.  (This combines edge stroke and paint.) Otherwise,
 * all edges are drawn with thin solid lines.
 * <li/>"show edge weights": if checked, edges are labeled with their weights.
 * Otherwise, edges are not labeled.
 * </ul>
 * <li/>Miscellaneous (center panel)
 * <ul>
 * <li/>"bold text": if checked, all vertex and edge labels are drawn using a
 * boldface font.  Otherwise, a normal-weight font is used.  (Has no effect if
 * no labels are currently visible.)
 * <li/>zoom controls: 
 * <ul>
 * <li/>"+" zooms in, "-" zooms out
 * <li/>"zoom at mouse (wheel only)": if checked, zooming (using the mouse 
 * scroll wheel) is centered on the location of the mouse pointer; otherwise,
 * it is centered on the center of the visualization pane.
 * </ul>
 * </ul>
 * </p>
 * 
 * @author Danyel Fisher, Joshua O'Madadhain, Tom Nelson
 */
public class PluggableRendererDemo extends JApplet implements ActionListener 
{
    protected JCheckBox v_color;
    protected JCheckBox e_color;
    protected JCheckBox v_stroke;
    protected JCheckBox e_uarrow_pred;
    protected JCheckBox e_darrow_pred;
    protected JCheckBox v_shape;
    protected JCheckBox v_size;
    protected JCheckBox v_aspect;
    protected JCheckBox v_labels;
    protected JRadioButton e_line;
    protected JRadioButton e_bent;
    protected JRadioButton e_wedge;
    protected JRadioButton e_quad;
    protected JRadioButton e_cubic;
    protected JCheckBox e_labels;
    protected JCheckBox font;
    protected JCheckBox e_show_d;
    protected JCheckBox e_show_u;
    protected JCheckBox v_small;
    protected JCheckBox zoom_at_mouse;
    protected JCheckBox fill_edges;
    
	protected JRadioButton no_gradient;
//	protected JRadioButton gradient_absolute;
	protected JRadioButton gradient_relative;

	protected static final int GRADIENT_NONE = 0;
	protected static final int GRADIENT_RELATIVE = 1;
//	protected static final int GRADIENT_ABSOLUTE = 2;
	protected static int gradient_level = GRADIENT_NONE;

    protected PluggableRenderer pr;
    protected SeedColor vcf;
    protected EdgeWeightStrokeFunction ewcs;
    protected VertexStrokeHighlight vsh;
    protected VertexStringer vs;
    protected VertexStringer vs_none;
    protected EdgeStringer es;
    protected EdgeStringer es_none;
    protected FontHandler ff;
    protected VertexShapeSizeAspect vssa;
    protected DirectionDisplayPredicate show_edge;
    protected DirectionDisplayPredicate show_arrow;
    protected VertexDisplayPredicate show_vertex;
    protected GradientPickedEdgePaintFunction edgePaint;
    protected final static Object VOLTAGE_KEY = "voltages";
    protected final static Object TRANSPARENCY = "transparency";
    
    protected NumberEdgeValue edge_weight = new UserDatumNumberEdgeValue("edge_weight");
    protected NumberVertexValue voltages = new UserDatumNumberVertexValue(VOLTAGE_KEY);
    protected NumberVertexValue transparency = new UserDatumNumberVertexValue(TRANSPARENCY);
    
    protected VisualizationViewer vv;
    protected DefaultModalGraphMouse gm;
    protected Transformer affineTransformer;
    
    public void start()
    {
        getContentPane().add( startFunction() );
    }
    
    public static void main(String[] s ) 
    {
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel jp = new PluggableRendererDemo().startFunction();
        jf.getContentPane().add(jp);
        jf.pack();
        jf.setVisible(true);
    }
    
    
    public PluggableRendererDemo()
    {
    }
    
    public JPanel startFunction() {
        Graph g = getGraph();
        
        pr = new PluggableRenderer();
        Layout layout = new FRLayout(g);
        vv = new VisualizationViewer(layout, pr);
        // add Shape based pick support
        vv.setPickSupport(new ShapePickSupport());
        PickedState picked_state = vv.getPickedState();
        
        affineTransformer = vv.getLayoutTransformer();
        
        // create decorators
        vcf = new SeedColor(picked_state);
        ewcs = 
            new EdgeWeightStrokeFunction(edge_weight);
        vsh = new VertexStrokeHighlight(picked_state);
        ff = new FontHandler();
        vs_none = new ConstantVertexStringer(null);
        es_none = new ConstantEdgeStringer(null);
        vssa = new VertexShapeSizeAspect(voltages);
        show_edge = new DirectionDisplayPredicate(true, true);
        show_arrow = new DirectionDisplayPredicate(true, false);
        show_vertex = new VertexDisplayPredicate(false);

        // uses a gradient edge if unpicked, otherwise uses picked selection
        edgePaint = new GradientPickedEdgePaintFunction( new PickableEdgePaintFunction(picked_state,Color.black,Color.cyan), 
                vv, vv, picked_state);
        
        pr.setVertexPaintFunction(vcf);
        pr.setVertexStrokeFunction(vsh);
        pr.setVertexStringer(vs_none);
        pr.setVertexFontFunction(ff);
        pr.setVertexShapeFunction(vssa);
        pr.setVertexIncludePredicate(show_vertex);
        
        pr.setEdgePaintFunction( edgePaint );
        pr.setEdgeStringer(es_none);
        pr.setEdgeFontFunction(ff);
        pr.setEdgeStrokeFunction(ewcs);
        pr.setEdgeIncludePredicate(show_edge);
        pr.setEdgeShapeFunction(new EdgeShape.Line());
        pr.setEdgeArrowPredicate(show_arrow);
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        
        vv.setBackground(Color.white);
        GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(vv);
        jp.add(scrollPane);
        gm = new DefaultModalGraphMouse();
        vv.setGraphMouse(gm);
        gm.add(new PopupGraphMousePlugin());

        addBottomControls( jp );
        vssa.setScaling(true);

        vv.setToolTipFunction(new VoltageTips());
        vv.setToolTipText("<html><center>Use the mouse wheel to zoom<p>Click and Drag the mouse to pan<p>Shift-click and Drag to Rotate</center></html>");
        

        
        return jp;
    }
    
    /**
     * Generates a mixed-mode random graph, runs VoltageRanker on it, and
     * returns the resultant graph.
     */
    public Graph getGraph()
    {
        Graph g = TestGraphs.generateMixedRandomGraph(edge_weight, 20, false);
        vs = new NumberVertexValueStringer(voltages);
        es = new NumberEdgeValueStringer(edge_weight);
        
        // collect the seeds used to define the random graph
        Collection seeds = PredicateUtils.getVertices(g, 
                new ContainsUserDataKeyVertexPredicate(BarabasiAlbertGenerator.SEED));
        if (seeds.size() < 2)
            System.out.println("need at least 2 seeds (one source, one sink)");
        
        // use these seeds as source and sink vertices, run VoltageRanker
        boolean source = true;
        Set sources = new HashSet();
        Set sinks = new HashSet();
        for (Iterator iter = seeds.iterator(); iter.hasNext(); )
        {
            if (source)
                sources.add(iter.next());
            else
                sinks.add(iter.next());
            source = !source;
        }
        VoltageRanker vr = new VoltageRanker(edge_weight, voltages, 100, 0.01);
        vr.calculateVoltages(g, sources, sinks);

        Set verts = g.getVertices();
        
        // assign a transparency value of 0.9 to all vertices
        for (Iterator iter = verts.iterator(); iter.hasNext(); )
        {
            Vertex v = (Vertex)iter.next();
            transparency.setNumber(v, new MutableDouble(0.9));
        }

        // add a couple of self-loops (sanity check on rendering)
        Vertex v = (Vertex)verts.iterator().next(); 
        Edge e = new DirectedSparseEdge(v,v);
        edge_weight.setNumber(e, new Double(Math.random()));
        g.addEdge(e);
        e = new UndirectedSparseEdge(v,v);
        edge_weight.setNumber(e, new Double(Math.random()));
        g.addEdge(e);
        return g;  
    }
    
    /**
     * @param jp    panel to which controls will be added
     */
    protected void addBottomControls(final JPanel jp) 
    {
        final JPanel control_panel = new JPanel();
        jp.add(control_panel, BorderLayout.SOUTH);
        control_panel.setLayout(new BorderLayout());
        final Box vertex_panel = Box.createVerticalBox();
        vertex_panel.setBorder(BorderFactory.createTitledBorder("Vertices"));
        final Box edge_panel = Box.createVerticalBox();
        edge_panel.setBorder(BorderFactory.createTitledBorder("Edges"));
        final Box both_panel = Box.createVerticalBox();

        control_panel.add(vertex_panel, BorderLayout.WEST);
        control_panel.add(edge_panel, BorderLayout.EAST);
        control_panel.add(both_panel, BorderLayout.CENTER);
        
        // set up vertex controls
        v_color = new JCheckBox("vertex seed coloring");
        v_color.addActionListener(this);
        v_stroke = new JCheckBox("<html>vertex selection<p>stroke highlighting</html>");
        v_stroke.addActionListener(this);
        v_labels = new JCheckBox("show vertex ranks (voltages)");
        v_labels.addActionListener(this);
        v_shape = new JCheckBox("vertex degree shapes");
        v_shape.addActionListener(this);
        v_size = new JCheckBox("vertex voltage size");
        v_size.addActionListener(this);
        v_size.setSelected(true);
        v_aspect = new JCheckBox("vertex degree ratio stretch");
        v_aspect.addActionListener(this);
        v_small = new JCheckBox("filter vertices of degree < " + VertexDisplayPredicate.MIN_DEGREE);
        v_small.addActionListener(this);

        vertex_panel.add(v_color);
        vertex_panel.add(v_stroke);
        vertex_panel.add(v_labels);
        vertex_panel.add(v_shape);
        vertex_panel.add(v_size);
        vertex_panel.add(v_aspect);
        vertex_panel.add(v_small);
        
        // set up edge controls
		JPanel gradient_panel = new JPanel(new GridLayout(1, 0));
        gradient_panel.setBorder(BorderFactory.createTitledBorder("Edge paint"));
		no_gradient = new JRadioButton("Solid color");
		no_gradient.addActionListener(this);
		no_gradient.setSelected(true);
//		gradient_absolute = new JRadioButton("Absolute gradient");
//		gradient_absolute.addActionListener(this);
		gradient_relative = new JRadioButton("Gradient");
		gradient_relative.addActionListener(this);
		ButtonGroup bg_grad = new ButtonGroup();
		bg_grad.add(no_gradient);
		bg_grad.add(gradient_relative);
		//bg_grad.add(gradient_absolute);
		gradient_panel.add(no_gradient);
		//gradientGrid.add(gradient_absolute);
		gradient_panel.add(gradient_relative);
        
        JPanel shape_panel = new JPanel(new GridLayout(3,2));
        shape_panel.setBorder(BorderFactory.createTitledBorder("Edge shape"));
        e_line = new JRadioButton("line");
        e_line.addActionListener(this);
        e_line.setSelected(true);
//        e_bent = new JRadioButton("bent line");
//        e_bent.addActionListener(this);
        e_wedge = new JRadioButton("wedge");
        e_wedge.addActionListener(this);
        e_quad = new JRadioButton("quad curve");
        e_quad.addActionListener(this);
        e_cubic = new JRadioButton("cubic curve");
        e_cubic.addActionListener(this);
        ButtonGroup bg_shape = new ButtonGroup();
        bg_shape.add(e_line);
//        bg.add(e_bent);
        bg_shape.add(e_wedge);
        bg_shape.add(e_quad);
        bg_shape.add(e_cubic);
        shape_panel.add(e_line);
//        shape_panel.add(e_bent);
        shape_panel.add(e_wedge);
        shape_panel.add(e_quad);
        shape_panel.add(e_cubic);
        fill_edges = new JCheckBox("fill edge shapes");
        fill_edges.setSelected(false);
        fill_edges.addActionListener(this);
        shape_panel.add(fill_edges);
        shape_panel.setOpaque(true);
        e_color = new JCheckBox("edge weight highlighting");
        e_color.addActionListener(this);
        e_labels = new JCheckBox("show edge weights");
        e_labels.addActionListener(this);
        e_uarrow_pred = new JCheckBox("undirected");
        e_uarrow_pred.addActionListener(this);
        e_darrow_pred = new JCheckBox("directed");
        e_darrow_pred.addActionListener(this);
        e_darrow_pred.setSelected(true);
        JPanel arrow_panel = new JPanel(new GridLayout(1,0));
        arrow_panel.setBorder(BorderFactory.createTitledBorder("Show arrows"));
        arrow_panel.add(e_uarrow_pred);
        arrow_panel.add(e_darrow_pred);
        
        e_show_d = new JCheckBox("directed");
        e_show_d.addActionListener(this);
        e_show_d.setSelected(true);
        e_show_u = new JCheckBox("undirected");
        e_show_u.addActionListener(this);
        e_show_u.setSelected(true);
        JPanel show_edge_panel = new JPanel(new GridLayout(1,0));
        show_edge_panel.setBorder(BorderFactory.createTitledBorder("Show edges"));
        show_edge_panel.add(e_show_u);
        show_edge_panel.add(e_show_d);
        
        shape_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        edge_panel.add(shape_panel);
        gradient_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        edge_panel.add(gradient_panel);
        show_edge_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        edge_panel.add(show_edge_panel);
        arrow_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        edge_panel.add(arrow_panel);
        
        e_color.setAlignmentX(Component.LEFT_ALIGNMENT);
        edge_panel.add(e_color);
        e_labels.setAlignmentX(Component.LEFT_ALIGNMENT);
        edge_panel.add(e_labels);

        // set up zoom controls
        zoom_at_mouse = new JCheckBox("<html><center>zoom at mouse<p>(wheel only)</center></html>");
        zoom_at_mouse.addActionListener(this);
        
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });

        Box zoomPanel = Box.createVerticalBox();
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        plus.setAlignmentX(Component.CENTER_ALIGNMENT);
        zoomPanel.add(plus);
        minus.setAlignmentX(Component.CENTER_ALIGNMENT);
        zoomPanel.add(minus);
        zoom_at_mouse.setAlignmentX(Component.CENTER_ALIGNMENT);
        zoomPanel.add(zoom_at_mouse);
        
        // add font and zoom controls to center panel
        font = new JCheckBox("bold text");
        font.addActionListener(this);
        font.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        both_panel.add(zoomPanel);
        both_panel.add(font);
        
        JComboBox modeBox = gm.getModeComboBox();
        modeBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel modePanel = new JPanel(new BorderLayout()) {
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        modePanel.add(modeBox);
        both_panel.add(modePanel);
    }
    
    public void actionPerformed(ActionEvent e)
    {
        AbstractButton source = (AbstractButton)e.getSource();
        if (source == v_color)
        {
            vcf.setSeedColoring(source.isSelected());
        }
        else if (source == e_color)
        {
            ewcs.setWeighted(source.isSelected());
        }
        else if (source == v_stroke) 
        {
            vsh.setHighlight(source.isSelected());
        }
        else if (source == v_labels)
        {
            if (source.isSelected())
                pr.setVertexStringer(vs);
            else
                pr.setVertexStringer(vs_none);
        }
        else if (source == e_labels)
        {
            if (source.isSelected())
                pr.setEdgeStringer(es);
            else
                pr.setEdgeStringer(es_none);
        }
        else if (source == e_uarrow_pred)
        {
            show_arrow.showUndirected(source.isSelected());
        }
        else if (source == e_darrow_pred)
        {
            show_arrow.showDirected(source.isSelected());
        }
        else if (source == font)
        {
            ff.setBold(source.isSelected());
        }
        else if (source == v_shape)
        {
            vssa.useFunnyShapes(source.isSelected());
        }
        else if (source == v_size)
        {
            vssa.setScaling(source.isSelected());
        }
        else if (source == v_aspect)
        {
            vssa.setStretching(source.isSelected());
        }
        else if (source == e_line) 
        {
            if(source.isSelected())
            {
                pr.setEdgeShapeFunction(new EdgeShape.Line());
            }
        }
        else if (source == e_wedge)
        {
            if (source.isSelected())
                pr.setEdgeShapeFunction(new EdgeShape.Wedge(10));
        }
//        else if (source == e_bent) 
//        {
//            if(source.isSelected())
//            {
//                pr.setEdgeShapeFunction(new EdgeShape.BentLine());
//            }
//        }
        else if (source == e_quad) 
        {
            if(source.isSelected())
            {
                pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
            }
        }
        else if (source == e_cubic) 
        {
            if(source.isSelected())
            {
                pr.setEdgeShapeFunction(new EdgeShape.CubicCurve());
            }
        }
       else if (source == e_show_d)
        {
            show_edge.showDirected(source.isSelected());
        }
        else if (source == e_show_u)
        {
            show_edge.showUndirected(source.isSelected());
        }
        else if (source == v_small)
        {
            show_vertex.filterSmall(source.isSelected());
        }
        else if(source == zoom_at_mouse)
        {
            gm.setZoomAtMouse(source.isSelected());
        } 
        else if (source == no_gradient) {
			if (source.isSelected()) {
				gradient_level = GRADIENT_NONE;
			}
//		} else if (source == gradient_absolute) {
//			if (source.isSelected()) {
//				gradient_level = GRADIENT_ABSOLUTE;
//			}
		} 
        else if (source == gradient_relative) {
			if (source.isSelected()) {
				gradient_level = GRADIENT_RELATIVE;
			}
		}
        else if (source == fill_edges)
        {
            edgePaint.useFill(source.isSelected());
        }
        vv.repaint();
    }
    
    private final class SeedColor implements VertexPaintFunction
    {
        protected PickedInfo pi;
        protected final static float dark_value = 0.8f;
        protected final static float light_value = 0.2f;
        protected boolean seed_coloring;
        
        public SeedColor(PickedInfo pi)
        {
            this.pi = pi;
            seed_coloring = false;
        }

        public void setSeedColoring(boolean b)
        {
            this.seed_coloring = b;
        }
        
        public Paint getDrawPaint(Vertex v)
        {
            return Color.BLACK;
        }
        
        public Paint getFillPaint(Vertex v)
        {
            float alpha = transparency.getNumber(v).floatValue();
            if (pi.isPicked(v))
            {
                return new Color(1f, 1f, 0, alpha); 
            }
            else
            {
                if (seed_coloring && v.containsUserDatumKey(BarabasiAlbertGenerator.SEED))
                {
                    Color dark = new Color(0, 0, dark_value, alpha);
                    Color light = new Color(0, 0, light_value, alpha);
                    return new GradientPaint( 0, 0, dark, 10, 0, light, true);
                }
                else
                    return new Color(1f, 0, 0, alpha);
            }
                
        }
    }
    
    private final static class EdgeWeightStrokeFunction
    implements EdgeStrokeFunction
    {
        protected static final Stroke basic = new BasicStroke(1);
        protected static final Stroke heavy = new BasicStroke(2);
        protected static final Stroke dotted = PluggableRenderer.DOTTED;
        
        protected boolean weighted = false;
        protected NumberEdgeValue edge_weight;
        
        public EdgeWeightStrokeFunction(NumberEdgeValue edge_weight)
        {
            this.edge_weight = edge_weight;
        }
        
        public void setWeighted(boolean weighted)
        {
            this.weighted = weighted;
        }
        
        public Stroke getStroke(Edge e)
        {
            if (weighted)
            {
                if (drawHeavy(e))
                    return heavy;
                else
                    return dotted;
            }
            else
                return basic;
        }
        
        protected boolean drawHeavy(Edge e)
        {
            double value = edge_weight.getNumber(e).doubleValue();
            if (value > 0.7)
                return true;
            else
                return false;
        }
        
    }
    
    private final static class VertexStrokeHighlight implements VertexStrokeFunction
    {
        protected boolean highlight = false;
        protected Stroke heavy = new BasicStroke(5);
        protected Stroke medium = new BasicStroke(3);
        protected Stroke light = new BasicStroke(1);
        protected PickedInfo pi;
        
        public VertexStrokeHighlight(PickedInfo pi)
        {
            this.pi = pi;
        }
        
        public void setHighlight(boolean highlight)
        {
            this.highlight = highlight;
        }
        
        public Stroke getStroke(Vertex v)
        {
            if (highlight)
            {
                if (pi.isPicked(v))
                    return heavy;
                else
                {
                    for (Iterator iter = v.getNeighbors().iterator(); iter.hasNext(); )
                    {
                        Vertex w = (Vertex)iter.next();
                        if (pi.isPicked(w))
                            return medium;
                    }
                    return light;
                }
            }
            else
                return light; 
        }
    }
    
    private final static class FontHandler implements VertexFontFunction, EdgeFontFunction
    {
        protected boolean bold = false;
        Font f = new Font("Helvetica", Font.PLAIN, 12);
        Font b = new Font("Helvetica", Font.BOLD, 12);
        
        public void setBold(boolean bold)
        {
            this.bold = bold;
        }
        
        public Font getFont(Vertex v)
        {
            if (bold)
                return b;
            else
                return f;
        }
        
        public Font getFont(Edge e)
        {
            if (bold)
                return b;
            else 
                return f;
        }
    }
    
    private final static class DirectionDisplayPredicate implements Predicate
    {
        protected boolean show_d;
        protected boolean show_u;
        
        public DirectionDisplayPredicate(boolean show_d, boolean show_u)
        {
            this.show_d = show_d;
            this.show_u = show_u;
        }
        
        public void showDirected(boolean b)
        {
            show_d = b;
        }
        
        public void showUndirected(boolean b)
        {
            show_u = b;
        }
        
        public boolean evaluate(Object arg0)
        {
            if (arg0 instanceof DirectedEdge && show_d)
                return true;
            if (arg0 instanceof UndirectedEdge && show_u)
                return true;
            return false;
        }
    }
    
    private final static class VertexDisplayPredicate implements Predicate
    {
        protected boolean filter_small;
        protected final static int MIN_DEGREE = 4;
        
        public VertexDisplayPredicate(boolean filter)
        {
            this.filter_small = filter;
        }
        
        public void filterSmall(boolean b)
        {
            filter_small = b;
        }
        
        public boolean evaluate(Object arg0)
        {
            Vertex v = (Vertex)arg0;
            if (filter_small)
                return (v.degree() >= MIN_DEGREE);
            else
                return true;
        }
    }
    
    /**
     * Controls the shape, size, and aspect ratio for each vertex.
     * 
     * @author Joshua O'Madadhain
     */
    private final static class VertexShapeSizeAspect 
    extends AbstractVertexShapeFunction 
    implements VertexSizeFunction, VertexAspectRatioFunction
    {
        protected boolean stretch = false;
        protected boolean scale = false;
        protected boolean funny_shapes = false;
        protected NumberVertexValue voltages;
        
        public VertexShapeSizeAspect(NumberVertexValue voltages)
        {
            this.voltages = voltages;
            setSizeFunction(this);
            setAspectRatioFunction(this);
        }
        
        public void setStretching(boolean stretch)
        {
            this.stretch = stretch;
        }
        
        public void setScaling(boolean scale)
        {
            this.scale = scale;
        }
        
        public void useFunnyShapes(boolean use)
        {
            this.funny_shapes = use;
        }
        
        public int getSize(Vertex v)
        {
            if (scale)
                return (int)(voltages.getNumber(v).doubleValue() * 30) + 20;
            else
                return 20;
        }
        
        public float getAspectRatio(Vertex v)
        {
            if (stretch)
                return (float)(v.inDegree() + 1) / (v.outDegree() + 1);
            else
                return 1.0f;
        }
        
        public Shape getShape(Vertex v)
        {
            if (funny_shapes)
            {
                if (v.degree() < 5)
                {
                    int sides = Math.max(v.degree(), 3);
                    return factory.getRegularPolygon(v, sides);
                }
                else
                    return factory.getRegularStar(v, v.degree());
            }
            else
                return factory.getEllipse(v);
        }
        
    }
    
    /**
     * a GraphMousePlugin that offers popup
     * menu support
     */
    protected class PopupGraphMousePlugin extends AbstractPopupGraphMousePlugin implements MouseListener {
        
        public PopupGraphMousePlugin() {
            this(MouseEvent.BUTTON3_MASK);
        }
        public PopupGraphMousePlugin(int modifiers) {
            super(modifiers);
        }
        
        /**
         * If this event is over a Vertex, pop up a menu to
         * allow the user to increase/decrease the voltage
         * attribute of this Vertex
         * @param e
         */
        protected void handlePopup(MouseEvent e) {
            final VisualizationViewer vv = 
                (VisualizationViewer)e.getSource();
            Point2D p = vv.inverseViewTransform(e.getPoint());
            
            PickSupport pickSupport = vv.getPickSupport();
            if(pickSupport != null) {
                final Vertex v = pickSupport.getVertex(p.getX(), p.getY());
                if(v != null) {
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new AbstractAction("Decrease Transparency") {
                        public void actionPerformed(ActionEvent e) {
                            MutableDouble value = (MutableDouble)transparency.getNumber(v);
                            value.setDoubleValue(Math.min(1, value.doubleValue() + 0.1));
                            vv.repaint();
                        }
                    });
                    popup.add(new AbstractAction("Increase Transparency"){
                        public void actionPerformed(ActionEvent e) {
                            MutableDouble value = (MutableDouble)transparency.getNumber(v);
                            value.setDoubleValue(Math.max(0, value.doubleValue() - 0.1));
                            vv.repaint();
                        }
                    });
                    popup.show(vv, e.getX(), e.getY());
                } else {
                    final Edge edge = pickSupport.getEdge(p.getX(), p.getY());
                    if(edge != null) {
                        JPopupMenu popup = new JPopupMenu();
                        popup.add(new AbstractAction(edge.toString()) {
                            public void actionPerformed(ActionEvent e) {
                                System.err.println("got "+edge);
                            }
                        });
                        popup.show(vv, e.getX(), e.getY());
                       
                    }
                }
            }
        }
    }
    
    public class VoltageTips extends DefaultToolTipFunction {
        
        public String getToolTipText(Vertex v) {
           return "Voltage:"+voltages.getNumber(v);
        }
        public String getToolTipText(Edge edge) {
            return edge.toString();
        }
    }
    
    public class GradientPickedEdgePaintFunction extends GradientEdgePaintFunction 
    {
        private PickedInfo pi;
        private EdgePaintFunction defaultFunc;
        private final Predicate self_loop = SelfLoopEdgePredicate.getInstance();
        protected boolean fill_edge = false;
        
        public GradientPickedEdgePaintFunction( EdgePaintFunction defaultEdgePaintFunction, HasGraphLayout vv, 
                LayoutTransformer transformer, PickedInfo pi ) 
        {
            super(Color.WHITE, Color.BLACK, vv, transformer);
            this.defaultFunc = defaultEdgePaintFunction;
            this.pi = pi;
        }
        
        public void useFill(boolean b)
        {
            fill_edge = b;
        }
        
        public Paint getDrawPaint(Edge e) {
            if (gradient_level == GRADIENT_NONE) {
                return defaultFunc.getDrawPaint(e);
            } else {
                return super.getDrawPaint(e);
            }
        }
        
        protected Color getColor2(Edge e)
        {
            return pi.isPicked(e)? Color.CYAN : c2;
        }
        
        public Paint getFillPaint(Edge e)
        {
            if (self_loop.evaluate(e) || !fill_edge)
                return null;
            else
                return getDrawPaint(e);
        }
        
    }
    
}

