/*Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra, Petar
Tsankov

The ARF layout plugin is free software: you can redistribute it and/or
modify it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package ch.ethz.sg.cuttlefish.ARFLayout;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.openide.util.NbBundle;

/**
 *
 * @author Petar Tsankov (ptsankov@student.ethz.ch)
 */
public class ARFLayout implements Layout {

    private final LayoutBuilder layoutBuilder;
    protected GraphModel graphModel;
    private Graph graph;
    private boolean converged;

    /**
     * the parameter a controls the attraction between connected nodes. 
     */
    private float a = 3;
    public void setNeighborAttraction(Float a) { this.a = a; }
    public Float getNeighborAttraction() { return a; }

    /**
     * ??? is a scaling factor for the attractive term. Connected as well as unconnected nodes are affected.
     */
    private float attraction = 0.2f;
    public void setAttraction(Float attraction) { this.attraction = attraction; }
    public Float getAttraction() { return attraction; }
    
    /**
     * b scales the repulsive force
     */
    private float b = 8;
    public void setRepulsiveForceScale(Float b) { this.b = b; }
    public Float getRepulsiveForceScale() { return b; }

    /**
     * deltaT controls the calculation precision: smaller deltaT results in higher precession
     */
    private float deltaT = 2;
    public void setPrecision(Float deltaT) { this.deltaT = deltaT; }
    public Float getPrecision() { return deltaT; }

    private boolean done = false;
    
    /**
     * if the movement in the system is less than epsilon*|V|, the algorithm terminates
     */
    private float epsilon = 0.2f;
    
    private Random random = new Random();

    /**
     * a maximum force for a node
     */
    private float forceCutoff=7;
    public void setMaxForce(Float forceCutoff) { this.forceCutoff = forceCutoff; }
    public Float getMaxForce() { return forceCutoff; }    

    public ARFLayout(LayoutBuilder layoutBuilder) {
        this.layoutBuilder = layoutBuilder;
    }

    @Override
    public void initAlgo() {
        converged = false;
        graph = graphModel.getGraphVisible();
        for (Node n : graph.getNodes()) {
            float radius = 2000 * random.nextFloat();
            float alpha = 360 * random.nextFloat();
            n.getNodeData().setX( radius * (float)Math.cos(alpha) );
            n.getNodeData().setY( radius * (float)Math.sin(alpha) );
        }
    }

    @Override
    public void goAlgo() {
        graph = graphModel.getGraphVisible();
        layout();
    }

    private void layout() {        
        int count = 0;
        advancePositions();
    }
    
    private void advancePositions() {
        for(Node n : graph.getNodes() ) {
            Point2D.Float f = getForceforNode(n);
            double deltaIndividual = graph.getDegree(n) > 1 ? deltaT / Math.pow(graph.getDegree(n), 0.4) : deltaT;
            f.setLocation(f.getX() * deltaIndividual, f.getY() * deltaIndividual);
            n.getNodeData().setX(n.getNodeData().x() + (float)f.getX() );
            n.getNodeData().setY(n.getNodeData().y() + (float)f.getY() );
        }      
        align(100,100);
    }
    
    private void align(float x0, float y0) {
    	float x = Float.MAX_VALUE;
	float y = Float.MAX_VALUE;
	
	for (Node n : graph.getNodes() ) {
            x = Math.min(x, n.getNodeData().x() );
            y = Math.min(y, n.getNodeData().y() );
        }
	
	for (Node n : graph.getNodes() ) {
            n.getNodeData().setX(n.getNodeData().x() - x + x0);
            n.getNodeData().setY(n.getNodeData().y() - y + y0);
        }
    }
    
    private Point2D.Float getForceforNode(Node node) {
        double numNodes = graph.getNodeCount();
   	
        Point2D.Float mDot = new Point2D.Float();

        if (node.getNodeData().x() == 0 && node.getNodeData().y() == 0) {
            return mDot;
        }

        for(Node otherNode : graph.getNodes() ) {
            if(node == otherNode) continue;
            
            if (otherNode.getNodeData().x() == 0 && otherNode.getNodeData().y() == 0 ) {
                continue;
            }
                        
            float tempX = otherNode.getNodeData().x()  - node.getNodeData().x();
            float tempY = otherNode.getNodeData().y()  - node.getNodeData().y();
            
            float multiplier = graph.isAdjacent(node, otherNode) ? a : 1;
            multiplier *= attraction / Math.sqrt(numNodes);

            mDot.setLocation(mDot.getX() + tempX * multiplier, mDot.getY() + tempY * multiplier);
            
            multiplier = 1 / (float)Math.sqrt(tempX*tempX +  tempY*tempY);
            mDot.setLocation(mDot.getX() - tempX * multiplier * b, mDot.getY() - tempY * multiplier * b);
        }
        
        if (mDot.distance(0, 0) > forceCutoff) {
            float mult = forceCutoff / (float)mDot.distance(0, 0);
            mDot.setLocation(mDot.getX() * mult, mDot.getY() * mult);
        }
    
        return mDot;
    }

        
    @Override
    public boolean canAlgo() {
        return !converged;
    }

    @Override
    public void endAlgo() {
    }

    @Override
    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Float.class, 
                    NbBundle.getMessage(getClass(), "ARF.a.name"),
                    null,
                    NbBundle.getMessage(getClass(), "ARF.a.name"),
                    "getNeighborAttraction", "setNeighborAttraction"));
            properties.add(LayoutProperty.createProperty(
                    this, Float.class, 
                    NbBundle.getMessage(getClass(), "ARF.attraction.name"),
                    null,
                    NbBundle.getMessage(getClass(), "ARF.attraction.desc"),
                    "getAttraction", "setAttraction"));
            properties.add(LayoutProperty.createProperty(
                    this, Float.class, 
                    NbBundle.getMessage(getClass(), "ARF.b.name"),
                    null,
                    NbBundle.getMessage(getClass(), "ARF.b.desc"),
                    "getRepulsiveForceScale", "setRepulsiveForceScale"));
            properties.add(LayoutProperty.createProperty(
                    this, Float.class, 
                    NbBundle.getMessage(getClass(), "ARF.deltaT.name"),
                    null,
                    NbBundle.getMessage(getClass(), "ARF.deltaT.desc"),
                    "getPrecision", "setPrecision"));
            properties.add(LayoutProperty.createProperty(
                    this, Float.class, 
                    NbBundle.getMessage(getClass(), "ARF.forceCutoff.name"),
                    null,
                    NbBundle.getMessage(getClass(), "ARF.forceCutoff.desc"),
                    "getMaxForce", "setMaxForce"));            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    @Override
    public void resetPropertiesValues() {
        a = 3;
        attraction = 0.2f;
        b = 8;
        deltaT = 2;
        forceCutoff=7;
    }

    @Override
    public void setGraphModel(GraphModel graphModel) {
        this.graphModel = graphModel;
    }

    @Override
    public LayoutBuilder getBuilder() {
        return layoutBuilder;
    }
    
}
