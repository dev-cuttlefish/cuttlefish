/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package samples.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.DegreeDistributionRanker;
import edu.uci.ics.jung.algorithms.importance.PageRank;
import edu.uci.ics.jung.algorithms.importance.PageRankWithPriors;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.algorithms.transformation.DirectionTransformer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.filters.impl.NumericDecorationFilter;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * @author Scott White
 */
public class RankingDemo extends JPanel {
	/**
	 * 
	 * @author danyelf
	 */
	public class MyJPanel extends JPanel {
		public MyJPanel( String title, Color color ) {
			super();
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));		
			setBackground(Color.WHITE);
			Border redline = BorderFactory.createLineBorder(color);
			Border compound = BorderFactory.createTitledBorder(redline, title );
			setBorder( compound );
			//			setBorder(Border.)
		}
	}
	
	private Graph mCurrentGraph;
	private Layout mVisualizer;
	private BasicRenderer mRenderer;
	private VisualizationViewer mVizViewer;
	private JSlider mNodeAcceptBetweennessSlider;
	private JSlider mNodeSizeDegreeSlider;
	private JSlider mNodeSizePageRankSlider;
	private NumericDecorationFilter mBetweennessFilter;
	//	private NumericDecorationFilter mPageRankFilter;
	private String mLabel;

	public RankingDemo(Graph g) throws HeadlessException {

		initialize();
		displayGraph(g);

		mLabel = "P Smyth";

		Vertex start =
			StringLabeller.getLabeller(mCurrentGraph, PajekNetReader.LABEL).getVertex(mLabel);
//          StringLabeller.getLabeller(mCurrentGraph).getVertex(mLabel);
		recalculate(g, start, true);
	}

	protected void initialize() {

		createControlsPanel();

		mBetweennessFilter = new NumericDecorationFilter();
		mBetweennessFilter.setThreshold(0.05);
		mBetweennessFilter.setDecorationKey(BetweennessCentrality.CENTRALITY);
	}

	private void createControlsPanel() {

		setLayout(new BorderLayout());

		JPanel controlsPanel = new JPanel();
		BoxLayout layoutMgr = new BoxLayout(controlsPanel, BoxLayout.PAGE_AXIS);
		controlsPanel.setPreferredSize(new Dimension(200, 400));
		controlsPanel.setLayout(layoutMgr);
		controlsPanel.setBackground(Color.WHITE);

		add(controlsPanel, BorderLayout.EAST);
		
		//------------------------------------------------------------
		
		controlsPanel.add( createSizePanel() );
		controlsPanel.add( createFilterPanel() );		
		
		mNodeSizeDegreeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				mRenderer.setSizeKey(DegreeDistributionRanker.KEY);
				mRenderer.setNodeSizeScale(mNodeSizeDegreeSlider.getValue());
				repaint();
			}
		});

		//------------------------------------------------------------

		mNodeSizePageRankSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				mRenderer.setSizeKey(PageRank.KEY);
				mRenderer.setNodeSizeScale(mNodeSizePageRankSlider.getValue());
				repaint();
			}
		});

		//------------------------------------------------------------

		mNodeAcceptBetweennessSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				int val = mNodeAcceptBetweennessSlider.getValue();
				mBetweennessFilter.setThreshold(val);
				Graph newGraph =
					mBetweennessFilter.filter(mCurrentGraph).assemble();
				mVisualizer.applyFilter(newGraph);
				try {
					GraphUtils.copyLabels(
						StringLabeller.getLabeller(mCurrentGraph),
						StringLabeller.getLabeller(newGraph));

					Vertex v =
						StringLabeller.getLabeller(newGraph).getVertex(mLabel);

					recalculate(newGraph, v, false);
				} catch (UniqueLabelException e1) {
				}
				repaint();
			}

		});

		//------------------------------------------------------------

		JButton scramble = new JButton("Redo layout");
		controlsPanel.add(scramble);

		scramble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mVisualizer.restart();
				mVizViewer.prerelax();
				mVizViewer.repaint();
			}

		});
		controlsPanel.revalidate();
		controlsPanel.repaint();
	}

	/**
	 * @return
	 */
	private Component createFilterPanel() {
		JPanel jp = new MyJPanel("Filter nodes", Color.RED);
		
		jp.add(new JLabel("Betweenness:"));
		mNodeAcceptBetweennessSlider = new JSlider(JSlider.HORIZONTAL);
		mNodeAcceptBetweennessSlider.setPreferredSize(new Dimension(180, 40));
		mNodeAcceptBetweennessSlider.setValue(0);
		mNodeAcceptBetweennessSlider.setPaintTicks(true);
		mNodeAcceptBetweennessSlider.setBackground(Color.WHITE);
		jp.add(mNodeAcceptBetweennessSlider);
		return jp;
	}

	/**
	 * @return
	 */
	private Component createSizePanel() {
		
		final ButtonGroup buttons = new ButtonGroup();
		JPanel jp = new MyJPanel("Node Size", Color.RED);
		
		mNodeSizeDegreeSlider = new JSlider(JSlider.HORIZONTAL);
		mNodeSizeDegreeSlider.setPreferredSize(new Dimension(180, 40));
		mNodeSizeDegreeSlider.setPaintTicks(true);
		mNodeSizeDegreeSlider.setBackground(Color.WHITE);
		mNodeSizeDegreeSlider.setValue(0);
		final JRadioButton degree = new JRadioButton("Degree");
		degree.setBackground(Color.WHITE);
		degree.setSelected(true);	
		buttons.add(degree);
		jp.add( degree );
		
		jp.add(mNodeSizeDegreeSlider);
		
		mNodeSizePageRankSlider = new JSlider(JSlider.HORIZONTAL);
		mNodeSizePageRankSlider.setPreferredSize(new Dimension(180, 40));
		mNodeSizePageRankSlider.setPaintTicks(true);
		mNodeSizePageRankSlider.setBackground(Color.WHITE);
		mNodeSizePageRankSlider.setEnabled( false );
		final JRadioButton rank = new JRadioButton("PageRank with Prior");
		rank.setBackground(Color.WHITE);
		buttons.add(rank);
		
		jp.add( rank );
		jp.add( mNodeSizePageRankSlider);
	
		ActionListener al = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if( e.getSource() == rank) {
					mNodeSizePageRankSlider.setEnabled(true);
					mNodeSizeDegreeSlider.setEnabled(false);
				} 
				if ( e.getSource() == degree) {
					mNodeSizePageRankSlider.setEnabled(false);
					mNodeSizeDegreeSlider.setEnabled(true);
				}
			}
			
		};
		degree.addActionListener(al);
		rank.addActionListener(al);
		
		return jp;
	}

	protected void displayGraph(Graph graph) {
		mCurrentGraph = graph;
		mVisualizer = new FRLayout(mCurrentGraph);
		mRenderer = new BasicRenderer();
		mRenderer.setLabel("LABEL");
		mRenderer.setSizeKey(PageRank.KEY);
		if (mVizViewer != null) {
			remove(mVizViewer);
		}
		mVizViewer = new VisualizationViewer(mVisualizer, mRenderer);
		mVizViewer.setBackground(Color.WHITE);
		add(mVizViewer);
		mVizViewer.revalidate();
		mVizViewer.repaint();
	}

	protected void recalculate(
		Graph graph,
		Vertex startingVertex,
		boolean fixAxes) {
		DegreeDistributionRanker degreeRanker =
			new DegreeDistributionRanker(graph, true);
		degreeRanker.setRemoveRankScoresOnFinalize(false);
		degreeRanker.evaluate();
		List rankingList = degreeRanker.getRankings();
		Ranking degreeMax = (Ranking) rankingList.get(0);
		Ranking degreeMin = (Ranking) rankingList.get(rankingList.size() - 1);

		Set priors = new HashSet();
		priors.add(startingVertex);

		PageRankWithPriors pageRank =
			new PageRankWithPriors( (DirectedGraph)graph, .2, priors, null);
		pageRank.setRemoveRankScoresOnFinalize(false);
		pageRank.evaluate();
		rankingList = pageRank.getRankings();
		Ranking pageRankMax = (Ranking) rankingList.get(0);
		Ranking pageRankMin = (Ranking) rankingList.get(rankingList.size() - 1);

		BetweennessCentrality bc = new BetweennessCentrality(graph, true);
		bc.setRemoveRankScoresOnFinalize(false);
		bc.evaluate();
		rankingList = bc.getRankings();
		Ranking betwennessMax = (Ranking) rankingList.get(0);

		int minScaleRatio = (int) Math.floor(1.0 / pageRankMin.rankScore);
		int maxScaleRatio = (int) Math.floor(1.0 / pageRankMax.rankScore);

		if (fixAxes) {
			mNodeSizePageRankSlider.setMaximum(minScaleRatio * 30);
			mNodeSizePageRankSlider.setMinimum(maxScaleRatio * 10);
		}

		//Ranking min = (Ranking) rankingList.get(rankingList.size()-1);
		//		ChangeListener[] l =
		// mNodeAcceptBetweennessSlider.getChangeListeners();
		//		for (int i = 0; i < l.length; i++) {
		//			mNodeAcceptBetweennessSlider.removeChangeListener(l[i]);
		//		}

		if (fixAxes) {
			mNodeAcceptBetweennessSlider.setMaximum(
				(int) Math.ceil(betwennessMax.rankScore / 4.0));
			mNodeAcceptBetweennessSlider.setMinimum(1);
		}
		//		for (int i = 0; i < l.length; i++) {
		//			mNodeAcceptBetweennessSlider.addChangeListener(l[i]);
		//		}

		minScaleRatio = (int) Math.floor(1.0 / degreeMin.rankScore);
		maxScaleRatio = (int) Math.floor(1.0 / degreeMax.rankScore);

		if (fixAxes) {
			mNodeSizeDegreeSlider.setMaximum(minScaleRatio * 15);
			mNodeSizeDegreeSlider.setMinimum(maxScaleRatio * 5);
		}
		mVizViewer.revalidate();
		mVizViewer.repaint();

	}

	public static void main(String[] args) throws IOException, UniqueLabelException {

		JFrame jf = new JFrame("Scott's Toy Network Viewer");

//		PajekNetFile file = new PajekNetFile();
//		file.setCreateDirectedOnly(true);
//		Graph g = file.load("samples/datasets/smyth.net");
		PajekNetReader pnr = new PajekNetReader(true);
        Graph ug = pnr.load("samples/datasets/smyth.net", new SparseGraph());
        Graph g = DirectionTransformer.toDirected(ug);
        GraphUtils.copyLabels(StringLabeller.getLabeller(ug, PajekNetReader.LABEL),
            StringLabeller.getLabeller(g, PajekNetReader.LABEL));

		RankingDemo vizApp = new RankingDemo(g);
		jf.getContentPane().add(vizApp);
		jf.setSize(700, 500);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jf.pack();
		jf.setVisible(true);

	}

}
