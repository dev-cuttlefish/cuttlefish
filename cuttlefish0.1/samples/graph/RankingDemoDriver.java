/*
 * Created on Jan 2, 2004
 */
package samples.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JApplet;

import edu.uci.ics.jung.algorithms.transformation.DirectionTransformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.utils.GraphUtils;

/**
 * @author danyelf
 */
public  class RankingDemoDriver extends JApplet {

	public void start() {
		System.out.println("Starting in applet mode.");
		InputStream is =
			this.getClass().getClassLoader().getResourceAsStream(
				"samples/datasets/smyth.net");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		PajekNetReader pnr = new PajekNetReader(true);
        try
        {
            Graph ug = pnr.load(br, new SparseGraph());
            Graph g = DirectionTransformer.toDirected(ug);
            GraphUtils.copyLabels(StringLabeller.getLabeller(ug, PajekNetReader.LABEL),
                StringLabeller.getLabeller(g, PajekNetReader.LABEL));
//            Graph g = pnr.load(br, new UndirectedSparseGraph());
            RankingDemo vizApp = new RankingDemo(g);
            getContentPane().add(vizApp);
        }
        catch (IOException e)
        {
            System.out.println("Error in loading graph");
            e.printStackTrace();
        }
        catch (UniqueLabelException ule)
        {
            System.out.println("Unexpected duplicate label");
            ule.printStackTrace();
        }
	}

}
