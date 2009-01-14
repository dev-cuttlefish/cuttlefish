package samples.preview_new_graphdraw.iterablelayouts;

/*
 * This source is under the same license with JUNG.
 * http://jung.sourceforge.net/license.txt for a description.
 */
//package edu.uci.ics.jung.visualization;
//package org.ingrid.nexas.graph;
import java.util.*;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.VisVertex;
import samples.preview_new_graphdraw.iter.IterableLayout;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;

/**
 * Implements the Kamada-Kawai algorithm for node layout. Does not respect
 * filter calls, and sometimes crashes when the view changes to it.
 * 
 * @see "Tomihisa Kamada and Satoru Kawai: An algorithm for drawing general
 *      indirect graphs. Information Processing Letters 31(1):7-15, 1989"
 * @see "Tomihisa Kamada: On visualization of abstract objects and relations.
 *      Ph.D. dissertation, Dept. of Information Science, Univ. of Tokyo, Dec.
 *      1988."
 * 
 * @author Masanori Harada
 */
public class KKLayout extends IterableLayout {

    private double EPSILON = 0.1d;

    private int currentIteration;

    private double L; // the ideal length of an edge

    private double K = 1; // arbitrary const number

    private DoubleMatrix2D dm; // distance matrix

    private Indexer id;

    private boolean adjustForGravity = true;

    private boolean exchangeVertices = true;

    /**
     * Stores graph distances between vertices of the visible graph
     */
    protected UnweightedShortestPath unweightedShortestPaths;

    /**
     * The diameter of the visible graph. In other words, length of the longest
     * shortest path between any two vertices of the visible graph.
     */
    protected double diameter;

    protected static final String LAYOUT_INDEX_KEY = "Index for KKLayout";

    protected Set vertices;

	protected double energy = Double.MAX_VALUE;
	protected double energyDelta = Double.MAX_VALUE;

	// by default, the threshold is 100. This isn't necessarily
	// the best, or even a particualrly good, choice. 
	public final double THRESHOLD;

	public KKLayout( ) {
	    THRESHOLD = 0.01;
	}
	
	public KKLayout( double thresh ) {
	    this.THRESHOLD = thresh;
	}
	
    public void initializeLocationsFromLayout(EmittedLayout sla) {
        super.initializeLocationsFromLayout(sla);
        vertices = sla.visVertexMap.keySet();

        int n = vertices.size();
        dm = new DenseDoubleMatrix2D(n, n);

        Graph theGraph = (Graph) ((Vertex) vertices.iterator().next())
                .getGraph();
        unweightedShortestPaths = new UnweightedShortestPath(theGraph);

        id = Indexer.getAndUpdateIndexer(theGraph, LAYOUT_INDEX_KEY);

        // This is practically fast, but it would be the best if we have an
        // implementation of All Pairs Shortest Paths(APSP) algorithm.
        diameter = getDiameter(n, id);

        int width = sla.getScreenSize().width;
        int height = sla.getScreenSize().height;

        double L0 = height > width ? width : height;
        L = L0 / diameter * 0.9;

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                double dist = getDistance((Vertex) id.getVertex(i), (Vertex) id
                        .getVertex(j));
                dm.setQuick(i, j, dist);
                dm.setQuick(j, i, dist);
            }
        }
    }

    private double getDiameter(int n, Indexer id) {
        double diameter = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                Number dist = unweightedShortestPaths.getDistance(
                        (Vertex) id.getVertex(i), (Vertex) id.getVertex(j));
                if (dist != null && dist.doubleValue() > diameter)
                    diameter = dist.doubleValue();
            }
        }
        return diameter;
    }

    /**
     * Gets a distance (a length of the shortest path) between the specified
     * vertices. Returned value is used for computing the strength of an
     * embedded spring. You may override this method to visualize a graph with
     * weighted edges.
     * <p>
     * The original Kamada-Kawai algorithm requires a connected graph. That is,
     * pathes must be exist between every pair of vertices in the graph. To
     * visualize a non-connected graph, this method returns (diameter + 1) for
     * vertices that are not connected.
     * <p>
     * The default implementation is as follows:
     * 
     * <pre>
     *  int dist = unweightedShortestPaths.getShortestPath(v1, v2);
     *  if (dist &lt; 0)
     *      return diameter + 1;
     *  else
     *      return dist;
     * </pre>
     */
    protected double getDistance(Vertex v1, Vertex v2) {
        Number n = unweightedShortestPaths.getDistance(v1, v2);
        if (n == null)
            return diameter + 1;
        else
            return n.doubleValue();
    }
    
    static int round = 0;

    public void calculate() {
        //		System.out.println( "runLayout " );
        currentIteration++;

        List orderedVisVertices = new ArrayList(vertices.size());
        for (Iterator iter = vertices.iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            orderedVisVertices.add(getVisVertex(v));
        }

        double oldEnergy = energy;
        energy = calcEnergy(orderedVisVertices);
        energyDelta  = Math.abs( oldEnergy - energy );

/*        round++;
        if( round % 10  == 0 ) {
            System.out.println( energy );
        }
*/
        int n = vertices.size();
        if (n == 0) return;

        double maxDeltaM = 0;
        Vertex pm = null; // the node having max deltaM

        for (Iterator iter = vertices.iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();

            //		}
            //		for (int i = 0; i < n; i++)
            //		{
            //			if (dontMove(vertices[i]))
            //				continue;
            double deltam = calcDeltaM(v, vertices);
            //			System.out.println("* i=" + i + " deltaM=" + deltam);
            if (maxDeltaM < deltam) {
                maxDeltaM = deltam;
                pm = v;
            }
        }
        if (pm == null) return;

        for (int i = 0; i < 100; i++) {
            double[] dxy = calcDeltaXY(pm, vertices);
            VisVertex vv = getVisVertex(pm);

            //			System.out.println("Offsetting " + vv + " by " + dxy[0] + " " +
            // dxy[1] );

            vv.offset(dxy[0], dxy[1]);
            double deltam = calcDeltaM(pm, vertices);
            if (deltam < EPSILON) break;
            //if (dxy[0] > 1 || dxy[1] > 1 || dxy[0] < -1 || dxy[1] < -1)
            //    break;
        }

        if (adjustForGravity) adjustForGravity(vertices);

        if (exchangeVertices && maxDeltaM < EPSILON) {
            energy = calcEnergy(orderedVisVertices);

            for (int ix = 0; ix < orderedVisVertices.size() - 1; ix++) {
                VisVertex vi = (VisVertex) orderedVisVertices.get(ix);

                for (Iterator itj = orderedVisVertices.listIterator(ix + 1); itj
                        .hasNext();) {
                    VisVertex vj = (VisVertex) itj.next();

                    double xenergy = calcEnergyIfExchanged(vi, vj,
                            orderedVisVertices);
                    if (energy > xenergy) {
                        double sx = vi.getX();
                        double sy = vi.getY();
                        vi.setX(vj.getX());
                        vi.setY(vj.getY());
                        vj.setX(sx);
                        vj.setY(sy);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Shift all vertices so that the center of gravity is located at the
     * center of the screen.
     */
    public void adjustForGravity(Set vertices) {
        int width = getScreenSize().width;
        int height = getScreenSize().height;
        double gx = 0;
        double gy = 0;

        for (Iterator iter = vertices.iterator(); iter.hasNext();) {
            Vertex i = (Vertex) iter.next();
            VisVertex vv = getVisVertex(i);
            gx += vv.getX();
            gy += vv.getY();
        }
        gx /= vertices.size();
        gy /= vertices.size();
        double diffx = width / 2 - gx;
        double diffy = height / 2 - gy;

        for (Iterator iter = vertices.iterator(); iter.hasNext();) {
            Vertex i = (Vertex) iter.next();
            VisVertex vv = getVisVertex(i);
            vv.offset(diffx, diffy);
        }
    }

    /**
     * Enable or disable gravity point adjusting.
     */
    public void setAdjustForGravity(boolean on) {
        adjustForGravity = on;
    }

    /**
     * Returns true if gravity point adjusting is enabled.
     */
    public boolean getAdjustForGravity() {
        return adjustForGravity;
    }

    /**
     * Enable or disable the local minimum escape technique by exchanging
     * vertices.
     */
    public void setExchangeVertices(boolean on) {
        exchangeVertices = on;
    }

    /**
     * Returns true if the local minimum escape technique by exchanging
     * vertices is enabled.
     */
    public boolean getExchangeVertices() {
        return exchangeVertices;
    }

    /**
     * Determines a step to new position of the vertex m.
     */
    private double[] calcDeltaXY(Vertex m, Set vertices) {
        double dE_dxm = 0;
        double dE_dym = 0;
        double d2E_d2xm = 0;
        double d2E_dxmdym = 0;
        double d2E_dymdxm = 0;
        double d2E_d2ym = 0;

        for (Iterator iter = vertices.iterator(); iter.hasNext();) {
            Vertex i = (Vertex) iter.next();
            //		for (int i = 0; i < id.getSize(); i++)
            //			{
            if (i != m) {
                double dist = getBFSDistance(m, i);
                double l_mi = L * dist;
                double k_mi = K / (dist * dist);

                VisVertex vm = getVisVertex(m);
                VisVertex vi = getVisVertex(i);

                double dx = vm.getX() - vi.getX();
                double dy = vm.getY() - vi.getY();
                double d = Math.sqrt(dx * dx + dy * dy);
                double ddd = d * d * d;

                dE_dxm += k_mi * (1 - l_mi / d) * dx;
                dE_dym += k_mi * (1 - l_mi / d) * dy;
                d2E_d2xm += k_mi * (1 - l_mi * dy * dy / ddd);
                d2E_dxmdym += k_mi * l_mi * dx * dy / ddd;
                //d2E_dymdxm += k_mi * l_mi * dy * dx / ddd;
                d2E_d2ym += k_mi * (1 - l_mi * dx * dx / ddd);
            }
        }
        // d2E_dymdxm equals to d2E_dxmdym.
        d2E_dymdxm = d2E_dxmdym;

        double denomi = d2E_d2xm * d2E_d2ym - d2E_dxmdym * d2E_dymdxm;
        double deltaX = (d2E_dxmdym * dE_dym - d2E_d2ym * dE_dxm) / denomi;
        double deltaY = (d2E_dymdxm * dE_dxm - d2E_d2xm * dE_dym) / denomi;
        return new double[] { deltaX, deltaY};
    }

    /**
     * Calculates the gradient of energy function at the vertex m.
     */
    private double calcDeltaM(Vertex m, Set vertices) {
        double dEdxm = 0;
        double dEdym = 0;
        for (Iterator iter = vertices.iterator(); iter.hasNext();) {
            Vertex i = (Vertex) iter.next();
            //			
            //		}
            //		for (int i = 0; i < id.getSize(); i++)
            //		{
            if (i != m) {
                double dist = getBFSDistance(i, m);
                double l_mi = L * dist;
                double k_mi = K / (dist * dist);

                VisVertex vm = getVisVertex(m);
                VisVertex vi = getVisVertex(i);

                double dx = vm.getX() - vi.getX();
                double dy = vm.getY() - vi.getY();
                double d = Math.sqrt(dx * dx + dy * dy);

                double common = k_mi * (1 - l_mi / d);
                dEdxm += common * dx;
                dEdym += common * dy;
            }
        }
        return Math.sqrt(dEdxm * dEdxm + dEdym * dEdym);
    }

    /**
     * Calculates the energy function E.
     */
    private double calcEnergy(List visVertices) {
        double energy = 0;
        for (int i = 0; i < visVertices.size() - 1; i++) {
            for (int j = i + 1; j < visVertices.size(); j++) {

                VisVertex vi = (VisVertex) visVertices.get(i);
                VisVertex vj = (VisVertex) visVertices.get(j);

                double dist = getBFSDistance(vi.getVertex(), vj.getVertex());
                double l_ij = L * dist;
                double k_ij = K / (dist * dist);
                double dx = vi.getX() - vj.getX();
                double dy = vi.getY() - vj.getY();
                double d = Math.sqrt(dx * dx + dy * dy);

                energy += k_ij / 2
                        * (dx * dx + dy * dy + l_ij * l_ij - 2 * l_ij * d);
            }
        }
        return energy;
    }

    private double getBFSDistance(Vertex u, Vertex v) {
        int i = id.getIndex(u);
        int j = id.getIndex(v);
        return dm.getQuick(i, j);
    }

    /**
     * Calculates the energy function E as if positions of the specified
     * vertices are exchanged.
     */
    private double calcEnergyIfExchanged(VisVertex p, VisVertex q,
            List orderedVertices) {
        VisVertex pButQ = new VisVertex(p.getVertex(), q.getX(), q.getY());
        VisVertex qButP = new VisVertex(q.getVertex(), p.getX(), p.getY());

        List l = new ArrayList(orderedVertices);
        int x = l.indexOf(p);
        l.remove(p);
        l.add(x, pButQ);

        x = l.indexOf(q);
        l.remove(p);
        l.add(x, qButP);

        return calcEnergy(l);
    }

	/**
	 * @see samples.preview_new_graphdraw.iter.IterableLayout#iterationsAreDone()
	 */
	public boolean iterationsAreDone() {
//	    System.out.println( "ENERGY:" + energy  + " \t DELTA: " + energyDelta );
		return energyDelta < THRESHOLD;
	}

	/**
	 * @see samples.preview_new_graphdraw.iter.IterableLayout#isFinite()
	 */
	public boolean isFinite() {
		return true;
	}
}
