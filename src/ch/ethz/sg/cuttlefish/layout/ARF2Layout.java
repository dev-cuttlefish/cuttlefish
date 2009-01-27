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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.misc.Edge;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author markus michael geipel
 * 
 *
 * An implementation of the ARF Layouter. 
 * See http://www.sg.ethz.ch/research/ for details
 */

public class ARF2Layout<V,E> extends AbstractLayout<Vertex,Edge> implements IterativeContext {
	
    /**
     * number of position updates before the graph is rendered 
     */
    private int updatesPerFrame = 1;

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
     * deltaT controls the calculation precision: smaller deltaT results in higher precission
     */
    private double deltaT = 2;

    /**
     * A marker used to tag nodes that shall not be moved
     */
    public	static final String FIXED = "_ch.ethz.sg.jung.visualisation.FIXED";
	
    /**
     * if the movement in the system is less than epsilon*|V|, the algorithm terminates
     */
    private double epsilon = .2;
    
	/**
	 * If the layout is used in a non interactive way, this variable gives a maximum bound to the layout steps
	 */
	private int maxRelayouts = 30;
    
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
	private double forceCutoff=7;

	private boolean verbose =false;
	
	boolean done = false;
	
    /**
     * Generates a new Layout for graph g
     * @param g
     */
    public ARF2Layout(Graph<Vertex,Edge> g) {
        super(g);
       // update();
    }

    /**
     * Generates a new Layout for graph g. if incremental is false the layout will not be interactive.
     * @param g
     * @param incremental
     */
    public ARF2Layout(Graph<Vertex,Edge> g, boolean incremental) {

        super(g);
        this.incremental  = incremental;
        if(!incremental){
        	update();
        }
    }
        
    @SuppressWarnings("unchecked")
	public void advancePositions() {
    	
    	try{
	        for (int i = 0; i < updatesPerFrame; i++) {
	        	for (Vertex v : getVertices()) {
	               if(!isFixed(v)){
	                	Point2D oldPoint = transform(v);
	                	if(! oldPoint.equals(new Point2D.Double(0.0,0.0))){
	                		Point2D force = getForceforNode(v);
	                		double forceX = force.getX();
	                		double forceY = force.getY();
	                        double deltaIndividual = getGraph().degree(v) > 1 ? deltaT / Math.pow(getGraph().degree(v), 0.4) : deltaT;
			                
	                        forceX *= deltaIndividual;
	                        forceY *= deltaIndividual;
			                
	                        Point2D point = new Point2D.Double(oldPoint.getX() + forceX,
	                        		oldPoint.getY() + forceY);
							locations.put(v, point);
		                }
	                	else{
	                		assignPositionToVertex(v);
	                	}
	                }
	            }
	        }
    	}catch(Exception e){
    		System.err.println(e);
    		e.printStackTrace();
    	}
    }

    /**
     * aligns the graph to make (x0,y0) the left upper corner
     * @param x0 
     * @param y0 
     */
    @SuppressWarnings("unchecked")
	public void align(double x0, double y0){
    	double x = Double.MAX_VALUE;
    	double y = Double.MAX_VALUE;
    	
    	for (Vertex v : getVertices()) {
            x = Math.min(x, locations.get(v).getX());
            y = Math.min(y, locations.get(v).getY());
        }
    	
    	for (Object o : getVertices()) {
            Vertex v = (Vertex) o;
            Point2D c = locations.get(v);
            c.setLocation(c.getX()-x + x0, c.getY() - y + y0);
            locations.put(v, c);
        }
    }
  
	/**
	 * Determines whether the position of v should not be changed by the layouter
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
        return new Point2D.Double(rnd.nextDouble()*scale, rnd.nextDouble()*scale);
    }

    /**
     * Computes the force experienced by node
     * @param node
     * @return force vector
     */
    @SuppressWarnings("unchecked")
	private Point2D getForceforNode(Vertex node) {

        double numNodes = getVertices().size();
       // double aIndivisual = node.degree() > 1 ? 1+((a-1)/node.degree()) : a;
        
        Point2D origin = new Point2D.Double(0.0, 0.0);
        Point2D mDot = new Point2D.Double();
        Point2D locNode = locations.get(node);
      
        if (locNode.distance(origin) == 0.0) {
        	return mDot;
        }
        // CAUTION: difference between double variables can be different than 0 even if equal
        
        for (Object o : getVertices()) {
            Vertex otherNode = (Vertex) o;
            if (node != otherNode) {
                Point2D otherNodeX = locations.get(otherNode);
                if (otherNodeX == null || otherNodeX.distance(origin) == 0.0) {
                	continue;
                }
                
                Point2D temp = (Point2D) otherNodeX.clone();
                temp.setLocation(temp.getX() -locNode.getX(), temp.getY() -locNode.getY());

                
                double factor = isEdgeInGraph(node, otherNode) ? a : 1;

                factor *= attraction / Math.sqrt(numNodes);

                Point2D desp = (Point2D) temp.clone();
                
                desp.setLocation(desp.getX() * factor, desp.getY() * factor);
                
                mDot.setLocation(mDot.getX() + desp.getX(), mDot.getY() + desp.getY());

                factor = 1 / temp.distance(origin);
                desp = (Point2D) temp.clone();

                desp.setLocation(desp.getX() * factor, desp.getY() * factor);
                desp.setLocation(desp.getX() * b, desp.getY() * b);
                mDot.setLocation(mDot.getX() - desp.getX(),mDot.getX() - desp.getY());

            }
        }
        
        if (incremental && mDot.distance(origin) > forceCutoff) {
            double factor = forceCutoff / mDot.distance(origin);
            mDot.setLocation(mDot.getX() * factor, mDot.getY() * factor);
        }
        
        return mDot;
    }


    /**
     * Assign position p to the vertex
     * @param vertex
     * @param p
     * @return the newly assigned position
     */
    @SuppressWarnings("unchecked")
	public Point2D assignPositionToVertex(Vertex vertex, Point2D p) {
       	locations.put(vertex, p);   	
    	return p;
    }
    
    public Point2D transform(Vertex vertex) {
    	return locations.get(vertex);
    }
    
	/**
	 * calculates position to the vertex
	 * @param vertex
	 * @return the newly assigned position
	 */
	@SuppressWarnings("unchecked")
	public Point2D assignPositionToVertex(Vertex vertex) {
		Set<Vertex> nvertices = new HashSet<Vertex>();
		for (Vertex vertex2 : (Collection <Vertex> ) getGraph().getNeighbors(vertex)) {
			if(locations.get(vertex2) != null){
				nvertices.add(vertex2);
			}
		}
		Point2D c = locations.get(vertex);
		if(c == null){
			c = getRandomPoint(((int)Math.sqrt(getVertices().size())*50)+1);
		}

		if(nvertices.size() > 0){
			c = getRandomPoint(1);
			for (Vertex vertex2 : nvertices) {
				Point2D c2 = locations.get(vertex2);
				c.setLocation(c.getX() + c2.getX(),c.getY() +  c2.getY());
			}
			double factor = 1.0 / (double) nvertices.size();
			c.setLocation(c.getX() * factor, c.getY() * factor);
		}
		
		return assignPositionToVertex(vertex, c);
	}

	/**
	 * Checks for the existence of edges
	 * @param node
	 * @param node2
	 * @return true if node and node2 are connected
	 */
	@SuppressWarnings("unchecked")
	private boolean isEdgeInGraph(Vertex node, Vertex node2) {

		Edge e = getGraph().findEdge(node, node2);
		
		if(e==null){
			e = getGraph().findEdge(node2, node);
		}
		
		if(e!=null){
			return (!((Edge)e).isExcluded());
		}else{
			return false;
		}

    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.Layout#isIncremental()
     */
    public boolean isIncremental() {
        return incremental;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.Layout#incrementsAreDone()
     */
    public boolean incrementsAreDone() {
        return !incremental;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.LayoutMutable#update()
     */
	public void update() {
    	
    	if(!incremental){
    		if(verbose){
				System.out.println("starting non incremental layout");
			}
    		layout();
    	}
    }

	/**
	 * produce a non interactive layout by calling advancePositions() several times.
	 */
	private void layout() {
		double error = Double.MAX_VALUE;
		/*for(int i = 0; i< getVisibleVertices().size(); i++){
			if(verbose || !verbose){
				System.out.println("run 1: " + (getVisibleVertices().size() - i));
			}
			advancePositions();
		}*/
		if ( ! done)
		{
			double threshold = (double)getVertices().size() * epsilon;
			int count = 0;
			while(error > threshold && count < maxRelayouts){
				if(verbose || !verbose){
					System.out.println("relayout: " + (maxRelayouts-count));
				}
				advancePositions();
				count ++;
				
			}
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

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}


	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	public void finish(){
		done = true;
	}

	@Override
	public boolean done() {
		return done;
	}

	@Override
	public void step() {
		if (!done)
			advancePositions();
	}
	


}