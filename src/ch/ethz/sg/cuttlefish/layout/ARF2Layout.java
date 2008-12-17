
/*
    
    Copyright (C) 2008  Markus Michael Geipel

    This program is free software: you can redistribute it and/or modify
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ch.ethz.sg.cuttlefish.misc.Vertex;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author markus michael geipel
 * 
 *
 * An implementation of the ARF Layouter. 
 * See http://www.sg.ethz.ch/research/ for details
 */

public class ARF2Layout<V,E> extends AbstractLayout<V,E> implements Layout<V,E>, IterativeContext {
	
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
	
    /**
     * Generates a new Layout for graph g
     * @param g
     */
    public ARF2Layout(Graph<V,E> g) {
        super(g);
       // update();
    }

    /**
     * Generates a new Layout for graph g. if incremental is false the layout will not be interactive.
     * @param g
     * @param incremental
     */
    public ARF2Layout(Graph<V,E> g, boolean incremental) {

        super(g);
        this.incremental  = incremental;
        if(!incremental){
        	update();
        }
    }
        
    public void advancePositions() {

    	try{
	        for (int i = 0; i < updatesPerFrame; i++) {
	        	for (V o : getVertices()) {
	                Vertex v = (Vertex) o;
	                if(!isFixed(v)){
	                	Point2D oldPoint = locations.get((V) v);
	                	if(oldPoint != null){
	                		ArrayList<Double> force = getForceforNode(v);
	                		double forceX = force.get(0);
	                		double forceY = force.get(1);
	                        double deltaIndividual = getGraph().degree((V) v) > 1 ? deltaT / Math.pow(getGraph().degree((V) v), 0.4) : deltaT;
			                
	                        forceX *= deltaIndividual;
	                        forceY *= deltaIndividual;
			                
	                        Point2D point = new Point2D.Double(oldPoint.getX() + forceX,
	                        		oldPoint.getY() + forceY);
							locations.put((V) v, point);
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
    public void align(double x0, double y0){
    	double x = Double.MAX_VALUE;
    	double y = Double.MIN_VALUE;
    	
    	for (Object o : getVertices()) {
            Vertex v = (Vertex) o;
            x = Math.min(x, c.x);
            y = Math.min(y, c.y);
        }
    	
    	for (Object o : getVertices()) {
            Vertex v = (Vertex) o;
            Coordinates c = getCoordinates(v);
            c.add(-x + x0, -y + y0);
            v.setPosX(c.getX());
            v.setPosY(c.getY());
            
            Point2D point = new Point2D.Double(v.getPosX(), v.getPosY());
            locations.put((V) v, point);
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
	private Coordinates getRandomPoint(int scale) {
        return new Coordinates(rnd.nextDouble()*scale, rnd.nextDouble()*scale);
    }

    /**
     * Computes the force experienced by node
     * @param node
     * @return force vector
     */
    private Coordinates getForceforNode(Vertex node) {

        double numNodes = getVertices().size();
       // double aIndivisual = node.degree() > 1 ? 1+((a-1)/node.degree()) : a;
        	
        Coordinates mDot = new Coordinates();
        Coordinates x = getCoordinates(node);
       
        if (x.distance(new Coordinates(0.0, 0.0)) == 0.0) {
        	return mDot;
        }
 
        for (Object o : getVertices()) {
            Vertex otherNode = (Vertex) o;
            if (node != otherNode) {
                Coordinates otherNodeX = getCoordinates(otherNode);
                if (otherNodeX == null || otherNodeX.distance(new Coordinates(0.0, 0.0)) == 0.0) {
                	continue;
                }
                
                Coordinates temp = new Coordinates(otherNodeX);
                temp.add(-x.getX(), -x.getY());

                
                double mult = isEdgeInGraph(node, otherNode) ? a : 1;

                mult *= attraction / Math.sqrt(numNodes);

                Coordinates add = new Coordinates(temp);
                add.mult(mult, mult);
                mDot.add(add.getX(), add.getY());

                mult = 1 / temp.distance(new Coordinates(0.0, 0.0));
                add = new Coordinates(temp);
                add.mult(mult, mult);
                add.mult(b, b);
                mDot.add(-add.getX(), -add.getY());

            }
        }
        
        if (incremental && mDot.distance(new Coordinates(0.0, 0.0)) > forceCutoff) {
            double mult = forceCutoff / mDot.distance(new Coordinates(0.0, 0.0));
            mDot.mult(mult, mult);
        }
        
        return mDot;
    }


    /**
     * Assign position p to the vertex
     * @param vertex
     * @param p
     * @return the newly assigned position
     */
    public Coordinates assignPositionToVertex(Vertex vertex, Coordinates p) {
    
    	vertex.setPosY(p.getY());
    	vertex.setPosX(p.getX());
    	Point2D point = new Point2D.Double(p.getY(), p.getX());
    	locations.put((V) vertex, point);   	
    	return p;
    }
    
    
	/**
	 * Assigns a position to the vertex
	 * @param vertex
	 * @return the newly assigned position
	 */
	@SuppressWarnings("unchecked")
	public Coordinates assignPositionToVertex(Vertex vertex) {
		Set<Vertex> nvertices = new HashSet<Vertex>();
		for (Vertex vertex2 : (Set<Vertex> ) getGraph().getNeighbors((V) vertex)) {
			if(getCoordinates(vertex2) != null){
				nvertices.add(vertex2);
			}
		}
		Coordinates c = getCoordinates(vertex);
		if(c == null){
			c = getRandomPoint(((int)Math.sqrt(getVertices().size())*50)+1);
		}

		if(nvertices.size() > 0){
			c = getRandomPoint(1);
			for (Vertex vertex2 : nvertices) {
				Coordinates c2 = getCoordinates(vertex2);
				c.add(c2.getX(), c2.getY());
			}
			double mult = 1.0 / (double) nvertices.size();
			c.mult(mult, mult);
		}
		return c;
	}

	/**
	 * Checks for the existence of edges
	 * @param node
	 * @param node2
	 * @return true if node and node2 are connected
	 */
	private boolean isEdgeInGraph(Vertex node, Vertex node2) {

		Edge e = node.findEdge(node2);
		if(e==null){
			e =	node2.findEdge(node);
		}
		if(e!=null){
			return e.getUserDatum(SGUserData.EXCLUDE)==null;
		}else{
			return false;
		}
		
        //return (node.findEdge(node2) != null || node2.findEdge(node) != null);
  
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
    @SuppressWarnings("unchecked")
	public void update() {
    	initializeLocations(); 	
    	
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
	public Object getBaseKey() {
		return SGUserData.POSITION;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initializeLocations() {
		System.out.println("Init positions");
      	Set<Vertex> vetices = getGraph().getVertices();
    	for (Vertex vertex : vetices) {
      		
    		Coordinates coord = (Coordinates) vertex.getUserDatum(getBaseKey());
            if (coord == null) {
            	assignPositionToVertex(vertex);
            	//System.out.println(vertex + " added");
            }
		}
  
		
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean done() {
		return false;
	}

	@Override
	public void step() {
		advancePositions();
	}
	


}