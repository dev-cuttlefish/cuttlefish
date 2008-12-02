/*
 * Created on Jan 7, 2004
 */
package samples.graph.southern;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import samples.preview_new_graphdraw.EdgeRenderer;
import samples.preview_new_graphdraw.VertexRenderer;
import samples.preview_new_graphdraw.event.ClickEvent;
import samples.preview_new_graphdraw.event.ClickListener;
import samples.preview_new_graphdraw.impl.SimpleEdgeRenderer;
import samples.preview_new_graphdraw.iter.LocalGraphDraw;
import samples.preview_new_graphdraw.iterablelayouts.KKLayout;
import samples.preview_new_graphdraw.iterablelayouts.SpringLayout;
import samples.preview_new_graphdraw.staticlayouts.CircleLayout;
import samples.preview_new_graphdraw.staticlayouts.RandomLayout;
import edu.uci.ics.jung.algorithms.transformation.FoldingTransformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.KPartiteGraph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.io.BipartiteGraphReader;
import edu.uci.ics.jung.utils.PredicateUtils;

/**
 * @author danyelf
 */
public class TestSouthernWomenBipartite extends JPanel
{

	private StringLabeller dates;
	private StringLabeller women;
//	private BipartiteGraph bpg;
    private KPartiteGraph bpg;
    private FoldingTransformer kpf;

	private JPanel bipartitePanel;
	private LocalGraphDraw bipartiteGraphDraw;

	/**
	 * @param fr
	 */
	public TestSouthernWomenBipartite(Reader fr) throws IOException
	{
        BipartiteGraphReader bgr = new BipartiteGraphReader(true, false, false);
		bpg = bgr.load(fr);
		System.out.println("loaded");

		women = StringLabeller.getLabeller(bpg, BipartiteGraphReader.PART_A);
		dates = StringLabeller.getLabeller(bpg, BipartiteGraphReader.PART_B);

		LocalGraphDraw lgd = createVisualization();
		final JList jl = assembleList();

        kpf = new FoldingTransformer(false); // will not create parallel edges
        
		createFoldedSpace(jl);
		lgd.getPanel().addClickListener(new ClickListener()
		{

			public void edgeClicked(ClickEvent ece)
			{
			}

			public void vertexClicked(ClickEvent vce)
			{
				Vertex v = (Vertex) vce.getGraphObject();
				String label = dates.getLabel(v);

				if (label != null)
				{
					changeSelectLabel(label, jl);
				}
			}

		});

		JPanel right = new JPanel();
		right.add(jl);

		setLayout(new BorderLayout());
		// we need two panels here
		JPanel core = new JPanel();
		core.add(lgd.getPanel());
		core.add(bipartitePanel);
		add(core, BorderLayout.CENTER);
		add(right, BorderLayout.EAST);

	}

	private static void changeSelectLabel(String label, JList jl)
	{
		// figure out index
		List integers = new LinkedList();

		int i = -1;
		for (i = 0; i < jl.getModel().getSize(); i++)
		{
			boolean shouldAdd = false;
			if (jl.isSelectedIndex(i))
			{
				shouldAdd = true;
			}
			if (jl.getModel().getElementAt(i).equals(label))
			{
				shouldAdd = !shouldAdd;
			}
			if (shouldAdd)
			{
				integers.add(new Integer(i));
			}
		}

		int[] selections = new int[integers.size()];
		int j = 0;
		for (Iterator iter = integers.iterator(); iter.hasNext();)
		{
			Integer ii = (Integer) iter.next();
			selections[j++] = ii.intValue();
		}
		jl.setValueIsAdjusting(true);
		jl.setSelectedIndices(selections);
		jl.setValueIsAdjusting(false);
	}

	/**
	 * @param jl
	 * @return
	 */
	private void createFoldedSpace(JList jl)
	{
		jl.addListSelectionListener(new RemoveAndRefold(bpg));

		bipartitePanel = new JPanel();
		foldAndDisplay(bpg);
	}

	/**
	 * @param bpg2
	 */
//	void foldAndDisplay(BipartiteGraph bpgx)
    void foldAndDisplay(KPartiteGraph bpgx)
	{
//		final BipartiteGraph bpg2 = bpgx;
        final KPartiteGraph bpg2 = bpgx;
        Graph f = kpf.fold(bpg2, BipartiteGraphReader.PART_A);
//		Graph f = BipartiteGraph.fold(bpg2, BipartiteGraph.CLASSA);
		EdgeRenderer er = new ThickEdgeRenderer(Color.lightGray);
		VertexRenderer vr = new BipartiteVertexRenderer(bpg2, women, dates);
		Dimension d = new Dimension(450, 450);
		if (bipartiteGraphDraw != null)
		{
			bipartiteGraphDraw.stop();
			bipartitePanel.removeAll();
		}

		bipartiteGraphDraw =
			new LocalGraphDraw(
				f,
				new CircleLayout(),
				new SpringLayout(),
				vr,
				er,d, true);

		bipartiteGraphDraw.start();
		bipartitePanel.add(bipartiteGraphDraw.getPanel());
	}

	public static void main(String[] s)
	{
		try
		{
			FileReader fr =
				new FileReader("samples/datasets/southern_women_data.txt");

			System.out.println("loading : ");

			JPanel jp = new TestSouthernWomenBipartite(fr);
			JFrame jf = new JFrame();
			jf.getContentPane().add(jp);
			jf.pack();
			jf.setVisible(true);
			jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param bpg
	 * @param dates
	 * @return
	 */
	private JList assembleList()
	{
		try
		{
			List listOfDates = getListOfDates();
			List displayList = new ArrayList();
			for (Iterator iter = listOfDates.iterator(); iter.hasNext();)
			{
				Date d = (Date) iter.next();
				displayList.add(df.format(d));
			}
			return new JList(displayList.toArray());
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
	}

	/**
	 * @param bpg
	 * @param dates
	 * @return
	 */
	private List getListOfDates() throws ParseException
	{
		SortedSet s = new TreeSet();
        Set dates_part = PredicateUtils.getVertices(bpg, BipartiteGraphReader.PART_B);
		for (Iterator iter =
            dates_part.iterator();
//			bpg.getAllVertices(BipartiteGraph.CLASSB).iterator();
			iter.hasNext();
			)
		{
			Vertex v = (Vertex) iter.next();
			String label = dates.getLabel(v);
			Date d = df.parse(label);
			s.add(d);
		}
		List l = new LinkedList(s);
		return l;
	}

	static DateFormat df = new SimpleDateFormat("d-MMM", Locale.US);
	private BipartiteVertexRenderer wellKnownRenderer;

	/**
	 * @param bpg
	 */
	protected static void dumpNames(
		KPartiteGraph bpg,
		StringLabeller women,
		StringLabeller dates)
	{
		for (Iterator iter =
			PredicateUtils.getVertices(bpg, BipartiteGraphReader.PART_A).iterator();
			iter.hasNext();
			)
		{
		    Vertex bpv = (Vertex) iter.next();
			System.out.println(
				women.getLabel(bpv) + " (" + bpv.getNeighbors().size());
			for (Iterator iterator = bpv.getNeighbors().iterator();
				iterator.hasNext();
				)
			{
				Vertex bpv2 = (Vertex) iterator.next();
				System.out.println(" " + dates.getLabel(bpv2));
			}
		}
	}

	/**
	 * @param bpg
	 * @param women
	 * @param dates
	 */
	private LocalGraphDraw createVisualization()
	{
		EdgeRenderer er = new SimpleEdgeRenderer(Color.lightGray);
		wellKnownRenderer = new BipartiteVertexRenderer(bpg, women, dates);
		Dimension d = new Dimension(450, 450);
		LocalGraphDraw lgd =
			new LocalGraphDraw(
				bpg,
				new RandomLayout(),
				new KKLayout(),
				wellKnownRenderer,
				er,
				d, true);

		lgd.start();
		return lgd;
	}

	/**
	 * @author danyelf
	 */
	public class RemoveAndRefold implements ListSelectionListener
	{
	    private KPartiteGraph kpg;
//		private BipartiteGraph bpg;

//		public RemoveAndRefold(BipartiteGraph bpg)
        public RemoveAndRefold(KPartiteGraph kpg)
		{
			this.kpg = kpg;
		}

		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e)
		{
			JList jl = (JList) e.getSource();
			if (jl.getSelectionModel().getValueIsAdjusting())
				return;
            KPartiteGraph bpg2 = (KPartiteGraph)kpg.copy();
//			BipartiteGraph bpg2 = (BipartiteGraph) kpg.copy();
			Object[] selected = jl.getSelectedValues();
//			System.out.println("Value Changed " + Arrays.asList(selected));
			for (int i = 0; i < selected.length; i++)
			{
				Vertex v2 = dates.getVertex((String) selected[i]);
				if (v2 == null)
				{
					System.out.println(
						"No original vertex with label " + selected[i]);
				}
				bpg2.removeVertex((Vertex) v2.getEqualVertex(bpg2));
			}
			wellKnownRenderer.setHiddenList(Arrays.asList(selected));
			foldAndDisplay(bpg2);
		}
	}

}
