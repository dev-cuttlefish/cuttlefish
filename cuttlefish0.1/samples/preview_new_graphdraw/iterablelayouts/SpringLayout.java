/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package samples.preview_new_graphdraw.iterablelayouts;

import java.util.Iterator;
import java.util.Set;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.VisEdge;
import samples.preview_new_graphdraw.VisVertex;
import samples.preview_new_graphdraw.iter.*;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;

/**
 * The SpringLayout package represents a visualization of a set of nodes. The
 * SpringLayout, which is initialized with a Graph, assigns X/Y locations to
 * each node. When called <code>relax()</code>, the SpringLayout moves the
 * visualization forward one step.
 */
public class SpringLayout extends UpdatableIterableLayout {

	//	protected LengthFunction lengthFunction;
	//
	public static final int RANGE = 100;
	protected static final double FORCE_CONSTANT = 1.0 / 3.0;
	public static final int STRETCH = 70;

	protected static class SpringVertex extends VisVertex {
		public double edgedx;
		public double edgedy;
		public double repulsiondx;
		public double repulsiondy;
		/** movement speed, x */
		public double dx;
		/** movement speed, y */
		public double dy;

		public SpringVertex(Vertex v, double x, double y) {
			super(v, x, y);
		}
	}

	protected static class SpringEdge extends VisEdge {
		public double f;
		double length;
		public SpringEdge(Edge e, VisVertex v1, VisVertex v2) {
			super(e, v1, v2);
		}
	}

	protected VisVertex createVisVertex(VisVertex ve) {
		return new SpringVertex( ve.getVertex(), ve.getX(), ve.getY() );
	}

	protected VisEdge createVisEdge(Edge ve, VisVertex front, VisVertex back) {
		return new SpringEdge(ve, front, back);
	}
	
	public void initializeLocationsFromLayout( EmittedLayout el ) {
	    // create current layout
	    for (Iterator iter = el.visVertexMap.values().iterator(); iter.hasNext();) {
            VisVertex vv = (VisVertex) iter.next();
            el.visVertexMap.put( vv.getVertex(), createVisVertex(vv));
        }

	    for (Iterator iter = el.visEdgeMap.values().iterator(); iter.hasNext();) {
            VisEdge ve = (VisEdge) iter.next();

            Edge e = ve.getEdge();            
            Pair p = e.getEndpoints();
            VisVertex front = el.getVisVertex((Vertex) p.getFirst());
            VisVertex back = el.getVisVertex((Vertex) p.getSecond());

            el.visEdgeMap.put( e, createVisEdge(e, front, back));
        }
	    currentLayout =el;
        returnableLayout = GraphLayoutPanelUtils.copy(currentLayout);
	}

	/* ------------------------- */

	//	protected void calcEdgeLength(SpringEdgeData sed, LengthFunction f) {
	//		sed.length = f.getLength(sed.e);
	//	}

	/* ------------------------- */

	//	long relaxTime = 0;

	/**
	 * Relaxation step. Moves all nodes a smidge.
	 */
	public void calculate() {
	    Set vertices = currentLayout.visVertexMap.keySet();
	    Set edges = currentLayout.visEdgeMap.keySet();
		//				System.out.println("Relax");
		for (Iterator iter = vertices.iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			SpringVertex svd = (SpringVertex) getVisVertex(v);
			if (svd == null) {
				System.out.println("How confusing!");
				continue;
			}
			svd.dx /= 4;
			svd.dy /= 4;
			svd.edgedx = svd.edgedy = 0;
			svd.repulsiondx = svd.repulsiondy = 0;
		}

		relaxEdges(edges);
		calculateRepulsion(vertices);
		moveNodes(vertices);
	}

	protected Vertex getAVertex(Edge e) {
		Vertex v = (Vertex) e.getEndpoints().getFirst();
		return v;
	}

	protected void relaxEdges(Set edges) {
		for (Iterator i = edges.iterator(); i.hasNext();) {
			Edge e = (Edge) i.next();

			Vertex v1 = getAVertex(e);
			Vertex v2 = e.getOpposite(v1);

			double vx = getVisVertex(v1).getX() - getVisVertex(v2).getX();
			double vy = getVisVertex(v1).getY() - getVisVertex(v2).getY();
			double len = Math.sqrt(vx * vx + vy * vy);

			double desiredLen = getLength(e);
			//			desiredLen *= Math.pow( 1.1, (v1.degree() + v2.degree()) );

			// round from zero, if needed [zero would be Bad.].
			len = (len == 0) ? .0001 : len;

			// force factor: optimal length minus actual length,
			// is made smaller as the current actual length gets larger.
			// why?

			//			System.out.println("Desired : " + getLength( e ));
			double f = FORCE_CONSTANT * (desiredLen - len) / len;

			f = f * Math.pow(STRETCH / 100.0, (v1.degree() + v2.degree() - 2));

			//			f= Math.min( 0, f );

			// the actual movement distance 'dx' is the force multiplied by the
			// distance to go.
			double dx = f * vx;
			double dy = f * vy;
			SpringVertex v1D, v2D;
			v1D = (SpringVertex) getVisVertex(v1);
			v2D = (SpringVertex) getVisVertex(v2);

			SpringEdge sed = (SpringEdge) getVisEdge(e);
			sed.f = f;

			v1D.edgedx += dx;
			v1D.edgedy += dy;
			v2D.edgedx += -dx;
			v2D.edgedy += -dy;
		}
	}

	protected void calculateRepulsion(Set vertices) {
		for (Iterator iter = vertices.iterator(); iter.hasNext();) {
			Vertex v = (Vertex) iter.next();

			SpringVertex svd = (SpringVertex) getVisVertex(v);
			double dx = 0, dy = 0;

			for (Iterator iter2 = vertices.iterator(); iter2.hasNext();) {
				Vertex v2 = (Vertex) iter2.next();
				if (v == v2)
					continue;

				double vx = getVisVertex(v).getX() - getVisVertex(v2).getX();
				double vy = getVisVertex(v).getY() - getVisVertex(v2).getY();
				double distance = vx * vx + vy * vy;

				if (distance == 0) {
					dx += Math.random();
					dy += Math.random();
				} else if (distance < RANGE * RANGE) {
					//					double degreeConstant = 1 + (stretch / 100.0);
					//					double factor = Math.pow( degreeConstant, v.degree() +
					// v2.degree() - 2);
					double factor = 1;
					dx += factor * vx / Math.pow(distance, 2);
					dy += factor * vy / Math.pow(distance, 2);
				}
			}
			double dlen = dx * dx + dy * dy;
			if (dlen > 0) {
				dlen = Math.sqrt(dlen) / 2;
				svd.repulsiondx += dx / dlen;
				svd.repulsiondy += dy / dlen;
			}
		}
	}

	protected void moveNodes(Set vertices) {

//		synchronized (getScreenSize()) {

			//			int showingNodes = 0;

			for (Iterator i = vertices.iterator(); i.hasNext();) {
				Vertex v = (Vertex) i.next();

				// TODO: Do we care about dontmove?
				//				if (dontMove(v))
				//					continue;

				SpringVertex vd = (SpringVertex) getVisVertex(v);

				vd.dx += vd.repulsiondx + vd.edgedx;
				vd.dy += vd.repulsiondy + vd.edgedy;

				double deltaChangeX = Math.max(-5, Math.min(5, vd.dx));
				double deltaChangeY = Math.max(-5, Math.min(5, vd.dy));
				vd.offset(deltaChangeX, deltaChangeY);

				int width = getScreenSize().width;
				int height = getScreenSize().height;

				if (vd.getX() < 0) {
					vd.setX(0);
				} else if (vd.getX() > width) {
					vd.setX(width);
				}
				if (vd.getY() < 0) {
					vd.setY(0);
				} else if (vd.getY() > height) {
					vd.setY(height);
				}

//			}
		}

	}

	public double getLength(Edge e) {
		return ((SpringEdge) getVisEdge(e)).length;
	}

	/**
	 * @see samples.preview_new_graphdraw.iter.IterableLayout#iterationsAreDone()
	 */
	public boolean iterationsAreDone() {
		return false;
	}

	/**
	 * @see samples.preview_new_graphdraw.iter.IterableLayout#isFinite()
	 */
	public boolean isFinite() {
		return false;
	}

	/* ---------------Length Function------------------ */

	//	/**
	//	 * If the edge is weighted, then override this method to show what the
	//	 * visualized length is.
	//	 *
	//	 * @author Danyel Fisher
	//	 */
	//	public interface LengthFunction {
	//		public double getLength(Edge e);
	//	}
	//
	//	private static final class UnitLengthFunction implements LengthFunction
	// {
	//		int length;
	//		public UnitLengthFunction(int length) {
	//			this.length = length;
	//		}
	//		public double getLength(Edge e) {
	//			return length;
	//		}
	//	}
	//
	//	public static final LengthFunction UNITLENGTHFUNCTION = new
	// UnitLengthFunction(30);

	/* ---------------User Data------------------ */

	/* ---------------Resize handler------------------ */

	//	public class SpringDimensionChecker extends ComponentAdapter {
	//		public void componentResized(ComponentEvent e) {
	//			resize(e.getComponent().getSize());
	//		}
	//	}

    /**
     * @see samples.preview_new_graphdraw.iter.UpdatableIterableLayout#addVisEdge(edu.uci.ics.jung.graph.Edge)
     */
    protected VisEdge addVisEdge(Edge e) {
        Pair p = e.getEndpoints();
        Vertex v1 = (Vertex) p.getFirst();
        Vertex v2 = (Vertex) p.getSecond();
        VisVertex vv1 = currentLayout.getVisVertex(v1);
        VisVertex vv2 = currentLayout.getVisVertex(v2);        
        return createVisEdge(e, vv1, vv2);
    }
    /**
     * @see samples.preview_new_graphdraw.iter.UpdatableIterableLayout#addVisVertex(edu.uci.ics.jung.graph.Vertex)
     */
    protected VisVertex addVisVertex(Vertex v) {
        VisVertex vv = super.addVisVertex( v );
		return createVisVertex(vv);
    }
}