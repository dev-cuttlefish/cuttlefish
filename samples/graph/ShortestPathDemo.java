/*
 * Created on Jan 2, 2004
 */
package samples.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import edu.uci.ics.jung.algorithms.connectivity.BFSDistanceLabeler;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.AbstractEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.random.generators.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

/**
 * @author danyelf
 */
public class ShortestPathDemo extends JPanel {

    boolean isBlessed( Edge e ) {
		Vertex v1= (Vertex) e.getEndpoints().getFirst()	;
		Vertex v2= (Vertex) e.getEndpoints().getSecond() ;
		return mPred.contains(v1) && mPred.contains( v2 );
    }
    
	/**
	 * @author danyelf
	 */
	public class MyEdgePaintFunction extends AbstractEdgePaintFunction {
	    
		/**
		 * @see edu.uci.ics.jung.graph.decorators.EdgePaintFunction#getDrawPaint(edu.uci.ics.jung.graph.Edge)
		 */
		public Paint getDrawPaint(Edge e) {
			if ( mPred == null || mPred.size() == 0) return Color.BLACK;
			if( isBlessed( e )) {
				return new Color(0.0f, 0.0f, 1.0f, 0.5f);//Color.BLUE;
			} else {
				return Color.LIGHT_GRAY;
			}
		}
	}
	
	public class MyEdgeStrokeFunction implements EdgeStrokeFunction {
        protected final Stroke THIN = new BasicStroke(1);
        protected final Stroke THICK = new BasicStroke(1);

        public Stroke getStroke(Edge e) {
			if ( mPred == null || mPred.size() == 0) return THIN;
			if (isBlessed( e ) ) {
			    return THICK;
			} else 
			    return THIN;
        }
	    
	}
	
	/**
	 * @author danyelf
	 */
	public class MyVertexPaintFunction implements VertexPaintFunction {

		public Paint getDrawPaint(Vertex v) {
			return Color.black;
		}
		
		public Paint getFillPaint( Vertex v ) {
			if ( v == mFrom) {
				return Color.BLUE;
			}
			if ( v == mTo ) {
				return Color.BLUE;
			}
			if ( mPred == null ) {
				return Color.LIGHT_GRAY;
			} else {
				if ( mPred.contains(v)) {
					return Color.RED;
				} else {
					return Color.LIGHT_GRAY;
				}
			}
		}

	}

	/**
	 * Starting vertex
	 */
	private Vertex mFrom;

	/**
	 * Ending vertex
	 */	
	private Vertex mTo;
	private Graph mGraph;
//	private GraphDraw mGD;
	private Set mPred;

	/**
	 * @param g
	 */
	public ShortestPathDemo(Graph g) {
		this.mGraph = g;
		setBackground(Color.WHITE);
		// show graph
        final Layout layout = new FRLayout(g);
        final PluggableRenderer pr = new PluggableRenderer();
        final VisualizationViewer vv = new VisualizationViewer(layout, pr);
        vv.setBackground(Color.WHITE);
//		mGD = new GraphDraw(layout);
//        mGD.setBackground(Color.WHITE);
//        mGD.hideStatus();
        
//        final VisualizationViewer vv = mGD.getVisualizationViewer();
        pr.setVertexPaintFunction(new MyVertexPaintFunction());
        pr.setEdgePaintFunction(new MyEdgePaintFunction());
        pr.setEdgeStrokeFunction(new MyEdgeStrokeFunction());
        pr.setVertexStringer(StringLabeller.getLabeller(mGraph));
        vv.setPickSupport(new ShapePickSupport());
        vv.setGraphMouse(new DefaultModalGraphMouse());
        vv.addPostRenderPaintable(new VisualizationViewer.Paintable(){
            
            public boolean useTransform() {
                return true;
            }
            public void paint(Graphics g) {
                if(mPred == null) return;
                Graphics2D g2d = (Graphics2D)g;
                
                // for all edges, paint edges that are in shortest path
                for (Iterator iter = layout.getGraph().getEdges().iterator();
                        iter.hasNext(); ) {
                    
                    Edge e = (Edge) iter.next();
                    if(isBlessed(e)) {
                        Vertex v1 = (Vertex) e.getEndpoints().getFirst();
                        Vertex v2 = (Vertex) e.getEndpoints().getSecond();
                        Point2D p1 = layout.getLocation(v1);
                        Point2D p2 = layout.getLocation(v2);
                        p1 = vv.layoutTransform(p1);
                        p2 = vv.layoutTransform(p2);
                        pr.paintEdge(
                                g2d,
                                e,
                                (int) p1.getX(),
                                (int) p1.getY(),
                                (int) p2.getX(),
                                (int) p2.getY());
                    }
                }
            }
        });
        
        setLayout(new BorderLayout());
//        add(mGD, BorderLayout.CENTER);
        add(vv, BorderLayout.CENTER);
        // set up controls
        add(setUpControls(), BorderLayout.SOUTH);
	}

	/**
	 *  
	 */
	private JPanel setUpControls() {
		JPanel jp = new JPanel();
		jp.setBackground(Color.WHITE);
		jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
		jp.setBorder(BorderFactory.createLineBorder(Color.black, 3));		
		jp.add(
			new JLabel("Select a pair of vertices for which a shortest path will be displayed"));
		JPanel jp2 = new JPanel();
		jp2.add(new JLabel("vertex from", SwingConstants.LEFT));
		jp2.add(getSelectionBox(true));
		jp2.setBackground(Color.white);
		JPanel jp3 = new JPanel();
		jp3.add(new JLabel("vertex to", SwingConstants.LEFT));
		jp3.add(getSelectionBox(false));
		jp3.setBackground(Color.white);
		jp.add( jp2 );
		jp.add( jp3 );
		return jp;
	}

	/**
	 * @param g
	 * @param from
	 * @return
	 */
	private Component getSelectionBox(final boolean from) {
		StringLabeller sl = StringLabeller.getLabeller(mGraph);
		Set s = new TreeSet();
		
		for (Iterator iter = mGraph.getVertices().iterator();
			iter.hasNext();
			) {
			s.add(sl.getLabel((Vertex) iter.next()));
		}
		final JComboBox choices = new JComboBox(s.toArray());
		choices.setSelectedIndex(-1);
		choices.setBackground(Color.WHITE);
		choices.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				StringLabeller sl = StringLabeller.getLabeller(mGraph);
				Vertex v = sl.getVertex((String) choices.getSelectedItem());
				if (from) {
					mFrom = v;
//					System.out.println("Assigned mFrom!");
				} else {
					mTo = v;
//					System.out.println("Assigned mTo!");
				}
				drawShortest();
				repaint();				
			}

		});
		return choices;
	}

	/**
	 *  
	 */
	protected void drawShortest() {
		if (mFrom == null || mTo == null) {
			return;
		}
		BFSDistanceLabeler bdl = new BFSDistanceLabeler();
		bdl.labelDistances(mGraph, mFrom);
		mPred = new HashSet();
		
		StringLabeller sl = StringLabeller.getLabeller(mGraph);
		
		// grab a predecessor
		Vertex v = mTo;
		Set prd = bdl.getPredecessors(v);
		mPred.add( mTo );
		while( prd != null && prd.size() > 0) {
//			System.out.print("Preds of " + sl.getLabel(v) + " are: ");
			for (Iterator iter = prd.iterator(); iter.hasNext();) {
				Vertex x = (Vertex) iter.next();
//				System.out.print( sl.getLabel(x) +" " );
			}
//			System.out.println();
			v = (Vertex) prd.iterator().next();
			mPred.add( v );
			if ( v == mFrom ) return;
			prd = bdl.getPredecessors(v);
		}
	}

	public static void main(String[] s) {
		Graph g = getGraph();
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add(new ShortestPathDemo(g));
		jf.pack();
		jf.setVisible(true);
	}

	/**
	 * @return
	 */
	static Graph getGraph() {
		//		return TestGraphs.getDemoGraph();
		Graph g =
			(Graph) new EppsteinPowerLawGenerator(26, 50, 50).generateGraph();
		Set removeMe = new HashSet();
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            if ( v.degree() == 0 ) {
                removeMe.add( v );
            }
        }
		GraphUtils.removeVertices(g, removeMe);
		StringLabeller sl = StringLabeller.getLabeller(g);
		char c = 0;
		for (Iterator iter = g.getVertices().iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			try {
				sl.setLabel(v, "" + (char) (c + 'a'));
			} catch (UniqueLabelException e) {
			}
			c++;
		}
		return g;
	}

}
