/*

Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

This file is part of Cuttlefish.

	Cuttlefish is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

package ch.ethz.sg.cuttlefish.layout;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author markus michael geipel
 * 
 * 
 *         An implementation of the ARF Layouter. See
 *         http://www.sg.ethz.ch/research/ for details
 */
public class ARF2Layout<V, E> extends AbstractLayout<Vertex, Edge> implements IterativeContext {

	/**
	 * number of position updates before the graph is rendered
	 */
	private int updatesPerFrame = 1;
	
	/**
	 * how much did the layout change from the previous iteration
	 */
	private double change = Double.MAX_VALUE;
	private double new_change = 0;

	/**
	 * the parameter a controls the attraction between connected nodes.
	 */
	private double a = 3;

	/**
	 * ??? is a scaling factor for the attractive term. Connected as well as unconnected nodes are affected.
	 */
	private double attraction = 0.2;

	/**
	 * b scales the repulsive force
	 */
	private double b = 8;

	/**
	 * deltaT controls the calculation precision: smaller deltaT results in
	 * higher precision
	 */
	private double deltaT = 2;

	private boolean done = false;
	private boolean locked = false;

	private int maxUpdates = 50;
	int countUpdates = 0;
	/**
	 * A marker used to tag nodes that shall not be moved
	 */
	public static final String FIXED = "_ch.ethz.sg.jung.visualisation.FIXED";

	/**
	 * if the movement in the system is less than epsilon*|V|, the algorithm
	 * terminates
	 */
	private double epsilon = 0.2;

	/**
	 * If the layout is used in a non interactive way, this variable gives a
	 * maximum bound to the layout steps
	 */
	private int maxRelayouts = 50;

	/**
	 * the random number generator used
	 */
	private Random rnd = new Random();

	/**
	 * 
	 */
	private boolean incremental = true;

	/**
	 * a maximum force for a node
	 */
	private double forceCutoff = 7;

	private Collection<Vertex> visualizedVertices = new HashSet<Vertex>();

	private Layout<Vertex, Edge> initial_positions = null;

	/**
	 * Genrates a new Layout for graph g
	 * 
	 * @param g
	 */
	public ARF2Layout(Graph<Vertex, Edge> g) {
		super(g);
		initialize();
	}

	/**
	 * Generates a new Layout for graph g. if incremental is false the layout
	 * will not be interactive.
	 * 
	 * @param g
	 * @param incremental
	 */
	public ARF2Layout(Graph<Vertex, Edge> g, boolean incremental) {
		super(g);
		this.incremental = incremental;
		initialize();
		if (!incremental) {
			update();
		}
	}

	public ARF2Layout(Graph<Vertex, Edge> g, boolean incremental, Layout<Vertex, Edge> init) {
		super(g);
		this.incremental = incremental;
		initial_positions = init;
		initialize();
		if (!incremental) {
			update();
		}
	}
	
	public ARF2Layout(Graph<Vertex, Edge> g, boolean incremental, int maxUpdates) {		
		super(g);
		this.maxUpdates = maxUpdates;
		this.incremental = incremental;
		initialize();
		if (!incremental) {
			update();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.jung.visualization.AbstractLayout#initialize_local_vertex
	 * (edu.uci.ics.jung.graph.Vertex)
	 */
	protected void initialize_local_vertex(Vertex v) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.visualization.AbstractLayout#advancePositions()
	 */
	public void advancePositions() {
		for (int i = 0; i < updatesPerFrame; i++) {
			try {
				for (Vertex v : graph.getVertices()) {
					if (!isFixed(v)) {
						Point2D c = transform( v);
						if (c != null) {
							Point2D f = getForceforNode(v);
							double deltaIndividual = 0;
							try {
								deltaIndividual = getGraph().degree(v) > 1 ? (deltaT/Math.log10(getGraph().getVertexCount())) / Math.pow(getGraph().degree(v), 0.4) : (deltaT/Math.log10(getGraph().getVertexCount()));
							} catch (java.lang.IllegalArgumentException ex) {
								//System.out.println("Error: vertex not found in the graph");
								this.reset();
							}
							f.setLocation(f.getX() * deltaIndividual, f.getY() * deltaIndividual);
							c.setLocation(c.getX() + f.getX(), c.getY() + f.getY());
						}
					}
				}
			} catch (ConcurrentModificationException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		align(100, 100);
	}
	

	/**
	 * aligns the graph to make (x0,y0) the left upper corner
	 * 
	 * @param x0
	 * @param y0
	 */
	public void align(double x0, double y0) {
		double x = Double.MAX_VALUE;
		double y = Double.MAX_VALUE;

		for (Vertex v : graph.getVertices()) {
			Point2D c = transform(v);
			x = Math.min(x, c.getX());
			y = Math.min(y, c.getY());
		}

		for (Vertex v : graph.getVertices()) {
			Point2D c = transform(v);
			new_change += (-x+x0-y+y0);
			c.setLocation(c.getX() - x + x0, c.getY() - y + y0);
		}
		change = new_change;
		new_change = 0;
	}
	
	

	/**
	 * Determines whether the position of v should not be changed by the
	 * layouter
	 * 
	 * @param v
	 * @return if the FIXED marker is set
	 */
	private boolean isFixed(Vertex v) {
		return v.isFixed();
	}

	/**
	 * 
	 * @return a random position in within the unit square
	 */
	private Point2D getRandomPoint(int scale) {
		return new Point2D.Double(rnd.nextDouble() * scale, rnd.nextDouble() * scale);
	}

	/**
	 * Computes the force experienced by node
	 * 
	 * @param node
	 * @return force vector
	 */
	private Point2D getForceforNode(Vertex node) {

		double numNodes = graph.getVertices().size();
		// double aIndivisual = node.degree() > 1 ? 1+((a-1)/node.degree()) : a;

		Point2D mDot = new Point2D.Double();
		Point2D x = transform(node);
		Point2D origin = new Point2D.Double(0.0, 0.0);

		if (x.distance(origin) == 0.0) {
			return mDot;
		}

		Collection<Vertex> vertices = graph.getVertices();
		Iterator<Vertex> it = vertices.iterator();
		while (it.hasNext()) {
			Vertex otherNode;
			try {
				otherNode = (Vertex) it.next();
				if (node != otherNode) {
					Point2D otherNodeX = transform(otherNode);
					if (otherNodeX == null || otherNodeX.distance(origin) == 0.0) {
						continue;
					}
					Point2D temp = (Point2D) otherNodeX.clone();
					temp.setLocation(temp.getX() - x.getX(), temp.getY() - x.getY());
					double multiplier = isEdgeInGraph(node, otherNode) ? a : 1;
					multiplier *= attraction / Math.sqrt(numNodes);
					Point2D addition = (Point2D) temp.clone();
					addition.setLocation(addition.getX() * multiplier, addition.getY() * multiplier);
					mDot.setLocation(mDot.getX() + addition.getX(), mDot.getY() + addition.getY());
					multiplier = 1 / temp.distance(origin);
					addition = (Point2D) temp.clone();
					addition.setLocation(addition.getX() * multiplier * b, addition.getY() * multiplier * b);
					mDot.setLocation(mDot.getX() - addition.getX(), mDot.getY() - addition.getY());					
				}
			} catch (ConcurrentModificationException e) {		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (incremental && mDot.distance(origin) > forceCutoff) {
			double mult = forceCutoff / mDot.distance(origin);
			mDot.setLocation(mDot.getX() * mult, mDot.getY() * mult);
		}

		return mDot;
	}

	/**
	 * Assign position p to the vertex
	 * 
	 * @param vertex
	 * @param p
	 * @return the newly assigned position
	 */
	public Point2D assignPositionToVertex(Vertex vertex, Point2D p) {
		Point2D c = transform(vertex);
		c.setLocation(p);
		return p;
	}

	/**
	 * Assigns a position to the vertex
	 * 
	 * @param vertex
	 * @return the newly assigned position
	 */
	public Point2D assignPositionToVertex(Vertex vertex) {
		Point2D c;

		if (!visualizedVertices.contains(vertex)) {
			c = getRandomPoint(((int) Math.sqrt(graph.getVertices().size()) * 50) + 1);
			locations.put(vertex, c);
			visualizedVertices.add(vertex);
			//done = false;
			countUpdates = 0;
		} else
			c = transform(vertex);
		return c;
	}

	public void updateVertices() {
		Set<Vertex> nvertices = new HashSet<Vertex>();
		for (Vertex vertex2 : (Collection<Vertex>) getGraph().getVertices()) {
			if (!visualizedVertices.contains(vertex2)) {
				nvertices.add(vertex2);
			}
		}
		Point2D c;
		if (nvertices.size() > 0) {
			for (Vertex vertex2 : nvertices) {
				c = getRandomPoint(((int) Math.sqrt(graph.getVertices().size()) * 50) + 1);
				locations.put(vertex2, c);
			}
			done = false;
			countUpdates = 0;
		}
	}

	/**
	 * Checks for the existence of edges
	 * 
	 * @param node
	 * @param node2
	 * @return true if node and node2 are connected
	 */
	private boolean isEdgeInGraph(Vertex node, Vertex node2) {

		Edge e = getGraph().findEdge(node, node2);
		if (e == null) {
			e = getGraph().findEdge(node2, node);
		}
		if (e != null) {
			return !e.isExcluded();
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.visualization.Layout#isIncremental()
	 */
	public boolean isIncremental() {
		return incremental;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.visualization.Layout#incrementsAreDone()
	 */
	public boolean incrementsAreDone() {
		return !incremental;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.visualization.LayoutMutable#update()
	 */
	public void update() {
		if (! locked)
		{
			for (Vertex v : (Collection<Vertex>) getGraph().getVertices())
				assignPositionToVertex(v);
			
				updateVertices();
			
			if (!incremental) {
					layout();
			}	
		}
	}

	/**
	 * produce a non interactive layout by calling advancePositions() several
	 * times.
	 */
	private void layout() {
		double error = Double.MAX_VALUE;
		/*
		 * for(int i = 0; i< getVisibleVertices().size(); i++){ if(verbose ||
		 * !verbose){ System.out.println("run 1: " +
		 * (getVisibleVertices().size() - i)); } advancePositions(); }
		 */
		double threshold = (double) graph.getVertices().size() * epsilon;
		int count = 0;
		while (error > threshold && count < maxRelayouts) {
			advancePositions();
			count++;
		}

	}

	public double getDeltaT() {
		return deltaT;
	}

	public void setDeltaT(double alpha) {
		this.deltaT = alpha;
	}

	public double getAttraction() {
		return attraction;
	}

	public void setAttraction(double attraction) {
		this.attraction = attraction;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getForceCutoff() {
		return forceCutoff;
	}

	public void setForceCutoff(double forceCutoff) {
		this.forceCutoff = forceCutoff;
	}

	public int getMaxRelayouts() {
		return maxRelayouts;
	}

	public void setMaxRelayouts(int maxRelayouts) {
		this.maxRelayouts = maxRelayouts;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public int getUpdatesPerFrame() {
		return updatesPerFrame;
	}

	public void setUpdatesPerFrame(int updatesPerFrame) {
		this.updatesPerFrame = updatesPerFrame;
	}

	public void setMaxUpdates(int maxUpdates) {
		this.maxUpdates = maxUpdates;
	}

	public int getMaxUpdates() {
		return maxUpdates;
	}

	public void resetUpdates() {
		countUpdates = 0;
	}

	@Override
	public void initialize() {
		if (initial_positions != null) {
			for (Vertex v : (Collection<Vertex>) getGraph().getVertices()) {
				// randomized initialization
				locations.put(v, initial_positions.transform(v));
				visualizedVertices.add(v);
			}
		} else {
			Point2D randomPoint;

			for (Vertex v : (Collection<Vertex>) getGraph().getVertices()) {
				// randomized initialization
				randomPoint = getRandomPoint(((int) Math.sqrt(graph
						.getVertices().size()) * 50) + 1);
				locations.put(v, randomPoint);
				visualizedVertices.add(v);
			}
		}

		update();
	}

	@Override
	public void reset() {
		done = false;
		visualizedVertices.clear();
		update();
	}

	public void lock(boolean lockvalue)
	{
		locked = lockvalue;
		return;
	}
	@Override
	public boolean done() {
		return locked;
	}
	
	/**
	 * Return the amount of change in the last iteration of the layout
	 * @return
	 */
	public double getChange() {
		return change;
	}

	@Override
	public void step() {
		countUpdates++;
		done = (countUpdates > maxUpdates);

		if (!done) {
			update();
			advancePositions();
		}
	}

}
