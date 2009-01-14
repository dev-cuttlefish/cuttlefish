/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
/*
 * Created on Jun 23, 2003
 */
package samples.graph;

import java.io.IOException;

import javax.swing.JFrame;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * Illustrates the simplest possible drawing program.
 * Does the following:
 * <ul>
 * <li/>reads in a graph 
 * <li/>creates a <code>Layout</code> based on that graph
 * <li/>creates a <code>Renderer</code>
 * <li/>creates a <code>VisualizationViewer</code> based on that layout and renderer
 * <li/>adds the <code>VisualizationViewer</code> object to a Swing <code>JFrame</code>'s
 * content pane.
 * </ul>
 * Does no processing, no filtering, and no customization of the rendering.
 * 
 * @author danyelf
 */
public class SimpleGraphDraw {

	public static void main(String[] args) throws IOException {
		JFrame jf = new JFrame();
		Graph g = getGraph();
        VisualizationViewer vv = new VisualizationViewer(new SpringLayout(g), new PluggableRenderer());
        jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
        jf.setVisible(true);
	}

	/**
	 * Generates a graph: in this case, reads it from the file
	 * "samples/datasetsgraph/simple.net"
	 * @return A sample undirected graph
	 */
	public static Graph getGraph() throws IOException {
        PajekNetReader pnr = new PajekNetReader();
        Graph g = pnr.load("samples/datasets/simple.net");
		return g;
	}
}
