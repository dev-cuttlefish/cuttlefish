/*
 * Created on Jan 2, 2004
 */
package samples.graph;

import javax.swing.JApplet;

import edu.uci.ics.jung.graph.Graph;

/**
 * @author danyelf
 */
public  class ShortestPathDemoDriver extends JApplet {

	public void start() {
		System.out.println("Starting in applet mode.");
		Graph g = ShortestPathDemo.getGraph();
		getContentPane().add( new ShortestPathDemo( g ) );

	}

}
