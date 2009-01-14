/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package samples.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.MultiPickedState;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.subLayout.CircularSubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;

/**
 * This simple app demonstrates how one can use our algorithms and visualization libraries in unison.
 * In this case, we generate use the Zachary karate club data set, widely known in the social networks literature, then
 * we cluster the vertices using an edge-betweenness clusterer, and finally we visualize the graph using
 * Fruchtermain-Rheingold layout and provide a slider so that the user can adjust the clustering granularity.
 * @author Scott White
 */
public class ClusteringDemo extends JApplet {

	private static final Object DEMOKEY = "DEMOKEY";

	public final Color[] similarColors =
	{
		new Color(216, 134, 134),
		new Color(135, 137, 211),
		new Color(134, 206, 189),
		new Color(206, 176, 134),
		new Color(194, 204, 134),
		new Color(145, 214, 134),
		new Color(133, 178, 209),
		new Color(103, 148, 255),
		new Color(60, 220, 220),
		new Color(30, 250, 100)
	};
	
	public static void main(String[] args) throws IOException {
		
		ClusteringDemo cd = new ClusteringDemo();
		cd.start();
		// Add a restart button so the graph can be redrawn to fit the size of the frame
		JFrame jf = new JFrame();
		jf.getContentPane().add(cd);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
	}

	//ClusteringLayout layout;
	
	public void start() {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("samples/datasets/zachary.net");
		BufferedReader br = new BufferedReader( new InputStreamReader( is ));
        
        try
        {
            setUpView(br);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            System.out.println("Error in loading graph");
            e.printStackTrace();
        }
	}

	private void setUpView(BufferedReader br) throws IOException {
        PajekNetReader pnr = new PajekNetReader();
        final Graph graph = pnr.load(br);

		//Create a simple layout frame
        //specify the Fruchterman-Rheingold layout algorithm
        final SubLayoutDecorator layout = new SubLayoutDecorator(new FRLayout(graph));
        final PickedState ps = new MultiPickedState();
        PluggableRenderer pr = new PluggableRenderer();
		pr.setVertexPaintFunction(new VertexPaintFunction() {
			public Paint getFillPaint(Vertex v) {
				Color k = (Color) v.getUserDatum(DEMOKEY);
				if (k != null)
					return k;
				return Color.white;
			}

			public Paint getDrawPaint(Vertex v) {
				if(ps.isPicked(v)) {
					return Color.cyan;
				} else {
					return Color.BLACK;
				}
			}
		});

		pr.setEdgePaintFunction(new EdgePaintFunction() {
			public Paint getDrawPaint(Edge e) {
				Color k = (Color) e.getUserDatum(DEMOKEY);
				if (k != null)
					return k;
				return Color.blue;
			}
            public Paint getFillPaint(Edge e)
            {
                return null;
            }
		});

        pr.setEdgeStrokeFunction(new EdgeStrokeFunction()
            {
                protected final Stroke THIN = new BasicStroke(1);
                protected final Stroke THICK= new BasicStroke(2);
                public Stroke getStroke(Edge e)
                {
                    Color c = (Color)e.getUserDatum(DEMOKEY);
                    if (c == Color.LIGHT_GRAY)
                        return THIN;
                    else 
                        return THICK;
                }
            });


		final VisualizationViewer vv = new VisualizationViewer(layout, pr);
		vv.setBackground( Color.white );
		//Tell the renderer to use our own customized color rendering
        
		//add restart button
		JButton scramble = new JButton("Restart");
		scramble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				vv.restart();
			}

		});
		
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		vv.setGraphMouse(gm);
		vv.setPickSupport(new ShapePickSupport());
		vv.setPickedState(ps);
		
		final JToggleButton groupVertices = new JToggleButton("Group Clusters");

		//Create slider to adjust the number of edges to remove when clustering
		final JSlider edgeBetweennessSlider = new JSlider(JSlider.HORIZONTAL);
        edgeBetweennessSlider.setBackground(Color.WHITE);
		edgeBetweennessSlider.setPreferredSize(new Dimension(210, 50));
		edgeBetweennessSlider.setPaintTicks(true);
		edgeBetweennessSlider.setMaximum(graph.numEdges());
		edgeBetweennessSlider.setMinimum(0);
		edgeBetweennessSlider.setValue(0);
		edgeBetweennessSlider.setMajorTickSpacing(10);
		edgeBetweennessSlider.setPaintLabels(true);
		edgeBetweennessSlider.setPaintTicks(true);

//		edgeBetweennessSlider.setBorder(BorderFactory.createLineBorder(Color.black));
		//TO DO: edgeBetweennessSlider.add(new JLabel("Node Size (PageRank With Priors):"));
		//I also want the slider value to appear
		final JPanel eastControls = new JPanel();
		eastControls.setOpaque(true);
		eastControls.setLayout(new BoxLayout(eastControls, BoxLayout.Y_AXIS));
		eastControls.add(Box.createVerticalGlue());
		eastControls.add(edgeBetweennessSlider);

		final String COMMANDSTRING = "Edges removed for clusters: ";
		final String eastSize = COMMANDSTRING + edgeBetweennessSlider.getValue();
		
		final TitledBorder sliderBorder = BorderFactory.createTitledBorder(eastSize);
		eastControls.setBorder(sliderBorder);
		//eastControls.add(eastSize);
		eastControls.add(Box.createVerticalGlue());
		
		groupVertices.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
					clusterAndRecolor(layout, edgeBetweennessSlider.getValue(), 
							similarColors, e.getStateChange() == ItemEvent.SELECTED);
			}});


		clusterAndRecolor(layout, 0, similarColors, groupVertices.isSelected());

		edgeBetweennessSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int numEdgesToRemove = source.getValue();
					clusterAndRecolor(layout, numEdgesToRemove, similarColors,
							groupVertices.isSelected());
					sliderBorder.setTitle(
						COMMANDSTRING + edgeBetweennessSlider.getValue());
					eastControls.repaint();
					vv.validate();
					vv.repaint();
				}
			}
		});

		Container content = getContentPane();
		content.add(new GraphZoomScrollPane(vv));
		JPanel south = new JPanel();
		JPanel grid = new JPanel(new GridLayout(2,1));
		grid.add(scramble);
		grid.add(groupVertices);
		south.add(grid);
		south.add(eastControls);
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
		p.add(gm.getModeComboBox());
		south.add(p);
		content.add(south, BorderLayout.SOUTH);
	}

	public void clusterAndRecolor(SubLayoutDecorator layout,
		int numEdgesToRemove,
		Color[] colors, boolean groupClusters) {
		//Now cluster the vertices by removing the top 50 edges with highest betweenness
		//		if (numEdgesToRemove == 0) {
		//			colorCluster( g.getVertices(), colors[0] );
		//		} else {
		
		Graph g = layout.getGraph();
        layout.removeAllSubLayouts();

		EdgeBetweennessClusterer clusterer =
			new EdgeBetweennessClusterer(numEdgesToRemove);
		ClusterSet clusterSet = clusterer.extract(g);
		List edges = clusterer.getEdgesRemoved();

		int i = 0;
		//Set the colors of each node so that each cluster's vertices have the same color
		for (Iterator cIt = clusterSet.iterator(); cIt.hasNext();) {

			Set vertices = (Set) cIt.next();
			Color c = colors[i % colors.length];

			colorCluster(vertices, c);
			if(groupClusters == true) {
				groupCluster(layout, vertices);
			}
			i++;
		}
		for (Iterator it = g.getEdges().iterator(); it.hasNext();) {
			Edge e = (Edge) it.next();
			if (edges.contains(e)) {
				e.setUserDatum(DEMOKEY, Color.LIGHT_GRAY, UserData.REMOVE);
			} else {
				e.setUserDatum(DEMOKEY, Color.BLACK, UserData.REMOVE);
			}
		}

	}

	private void colorCluster(Set vertices, Color c) {
		for (Iterator iter = vertices.iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			v.setUserDatum(DEMOKEY, c, UserData.REMOVE);
		}
	}
	
	private void groupCluster(SubLayoutDecorator layout, Set vertices) {
		if(vertices.size() < layout.getGraph().numVertices()) {
			Point2D center = layout.getLocation((ArchetypeVertex)vertices.iterator().next());
			SubLayout subLayout = new CircularSubLayout(vertices, 20, center);
			layout.addSubLayout(subLayout);
		}
	}
}
