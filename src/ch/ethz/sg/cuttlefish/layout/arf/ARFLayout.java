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

package ch.ethz.sg.cuttlefish.layout.arf;

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

import ch.ethz.sg.cuttlefish.Cuttlefish;

/**
 * 
 * @author Petar Tsankov (ptsankov@student.ethz.ch)
 * @author Ilias Rinis (iliasr@gmail.com)
 */
public class ARFLayout implements Layout {

	private static final boolean THRESHOLD_TYPE_FIXED = false;

	private final LayoutBuilder layoutBuilder;
	private boolean incremental;
	private boolean keepInitialPositions;

	/**
	 * the parameter a controls the attraction between connected nodes.
	 */
	private float a = 3;

	/**
	 * a scaling factor for the attractive term. Connected as well as
	 * unconnected nodes are affected.
	 */
	private float attraction = 0.2f;

	/**
	 * b scales the repulsive force
	 */
	private float b = 8;

	/**
	 * deltaT controls the calculation precision: smaller deltaT results in
	 * higher precession
	 */
	private float deltaT = 2;

	/**
	 * a maximum force for a node
	 */
	private float forceCutoff = 7;

	/**
	 * if the movement in the system is less than epsilon*|V|, the algorithm
	 * terminates
	 */
	private float epsilon = 0.2f;
	private double threshold = 10;
	private int updatesPerFrame = 1;
	private double change;
	private Random random;
	private boolean fixedThreshold = THRESHOLD_TYPE_FIXED;
	private boolean converged = false;
	private boolean randomize = true;

	private GraphModel graphModel = null;
	private Graph graph = null;

	public ARFLayout(LayoutBuilder layoutBuilder, boolean incremental,
			boolean keepInitialPositions) {
		this.layoutBuilder = layoutBuilder;
		this.incremental = incremental;
		this.keepInitialPositions = keepInitialPositions;
	}

	@Override
	public void initAlgo() {

		if (graphModel == null) {
			throw new RuntimeException(
					"The GraphModel for this layout cannot be null!");
		}

		graph = graphModel.getGraphVisible();
		change = Double.MAX_VALUE;
		random = new Random();
		randomize = true;

		if (!fixedThreshold) {
			threshold = epsilon * graph.getNodeCount();
		}

		if (!keepInitialPositions) {
			randomizePositions();
		}

		if (Cuttlefish.VERBOSE_LAYOUT)
			Cuttlefish.debug(this, "Layout initialized. Change threshold = "
					+ threshold);
	}

	@Override
	public void goAlgo() {
		graph = graphModel.getGraphVisible();
		advancePositions();

		// Do a single step when less than 3 nodes exist
		if (graph.getNodeCount() <= 3)
			converged = true;
		
		if (change > 1000 && randomize) {
			randomizePositions();
			randomize = false;
		}

		if (Cuttlefish.VERBOSE_LAYOUT)
			Cuttlefish.debug(this, "Change = " + change);
	}

	@Override
	public boolean canAlgo() {
		return (graphModel != null) && (change > threshold) && !converged;
	}

	@Override
	public void endAlgo() {

		if (Cuttlefish.VERBOSE_LAYOUT) {
			Cuttlefish.debug(this, "Layout ended");

			for (Node n : graphModel.getGraph().getNodes()) {
				Cuttlefish.debug(this, n + ": " + n.getNodeData().x() + ", "
						+ n.getNodeData().y());
			}
		}
	}

	private void randomizePositions() {
		for (Node n : graph.getNodes()) {
			float radius = 2000 * random.nextFloat();
			float alpha = 360 * random.nextFloat();
			n.getNodeData().setX(radius * (float) Math.cos(alpha));
			n.getNodeData().setY(radius * (float) Math.sin(alpha));
		}
	}

	private void advancePositions() {
		double c = 0;
		int nodeCount = graph.getNodeCount();

		for (int iter = 0; iter < updatesPerFrame; ++iter) {
			for (Node n : graph.getNodes()) {

				Point2D.Float f = getForceforNode(n);
				double log = Math.log10(nodeCount) == 0 ? 1 : Math
						.log10(nodeCount);
				double delta = graph.getDegree(n) > 1 ? (deltaT / log)
						/ Math.pow(graph.getDegree(n), 0.4) : (deltaT / log);

				f.setLocation(f.getX() * delta, f.getY() * delta);

				n.getNodeData().setX(n.getNodeData().x() + (float) f.getX());
				n.getNodeData().setY(n.getNodeData().y() + (float) f.getY());

				c += Math.abs(f.getX()) + Math.abs(f.getY());
			}
		}

		converged = (change == c);
		change = c;
		align(100, 100);
	}

	private void align(float x0, float y0) {
		float x = Float.MAX_VALUE;
		float y = Float.MAX_VALUE;

		for (Node n : graph.getNodes()) {
			x = Math.min(x, n.getNodeData().x());
			y = Math.min(y, n.getNodeData().y());
		}

		for (Node n : graph.getNodes()) {
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

		for (Node otherNode : graph.getNodes()) {
			if (node == otherNode)
				continue;

			if (otherNode.getNodeData().x() == 0
					&& otherNode.getNodeData().y() == 0) {
				continue;
			}

			float tempX = otherNode.getNodeData().x() - node.getNodeData().x();
			float tempY = otherNode.getNodeData().y() - node.getNodeData().y();

			float multiplier = graph.isAdjacent(node, otherNode) ? a : 1;
			multiplier *= attraction / Math.sqrt(numNodes);

			mDot.setLocation(mDot.getX() + tempX * multiplier, mDot.getY()
					+ tempY * multiplier);

			multiplier = 1 / (float) Math.sqrt(tempX * tempX + tempY * tempY);
			mDot.setLocation(mDot.getX() - tempX * multiplier * b, mDot.getY()
					- tempY * multiplier * b);
		}

		if (incremental && mDot.distance(0, 0) > forceCutoff) {
			float mult = forceCutoff / (float) mDot.distance(0, 0);
			mDot.setLocation(mDot.getX() * mult, mDot.getY() * mult);
		}

		return mDot;
	}

	@Override
	public LayoutProperty[] getProperties() {
		List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
		try {
			properties.add(LayoutProperty.createProperty(this, Float.class,
					"ARF.a.name", null, "ARF.a.desc", "getNeighborAttraction",
					"setNeighborAttraction"));
			properties.add(LayoutProperty.createProperty(this, Float.class,
					"ARF.attraction.name", null, "ARF.attraction.desc",
					"getAttraction", "setAttraction"));
			properties.add(LayoutProperty.createProperty(this, Float.class,
					"ARF.b.name", null, "ARF.b.desc", "getRepulsiveForceScale",
					"setRepulsiveForceScale"));
			properties.add(LayoutProperty.createProperty(this, Float.class,
					"ARF.deltaT.name", null, "ARF.deltaT.desc", "getPrecision",
					"setPrecision"));
			properties.add(LayoutProperty.createProperty(this, Float.class,
					"ARF.forceCutoff.name", null, "ARF.forceCutoff.desc",
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
		forceCutoff = 7;
		fixedThreshold = THRESHOLD_TYPE_FIXED;
		converged = false;

		if (fixedThreshold || graph == null) {
			threshold = 10;
		} else {
			threshold = epsilon * graph.getNodeCount();
		}
	}

	public void setFixedThreshold(boolean isFixed) {
		this.fixedThreshold = isFixed;
	}

	@Override
	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
	}

	@Override
	public LayoutBuilder getBuilder() {
		return layoutBuilder;
	}

	public void setNeighborAttraction(Float a) {
		this.a = a;
	}

	public Float getNeighborAttraction() {
		return a;
	}

	public void setAttraction(Float attraction) {
		this.attraction = attraction;
	}

	public Float getAttraction() {
		return attraction;
	}

	public void setRepulsiveForceScale(Float b) {
		this.b = b;
	}

	public Float getRepulsiveForceScale() {
		return b;
	}

	public void setPrecision(Float deltaT) {
		this.deltaT = deltaT;
	}

	public Float getPrecision() {
		return deltaT;
	}

	public void setMaxForce(Float forceCutoff) {
		this.forceCutoff = forceCutoff;
	}

	public Float getMaxForce() {
		return forceCutoff;
	}

	public int getUpdatesPerFrame() {
		return updatesPerFrame;
	}

	public void setUpdatesPerFrame(int updatesPerFrame) {
		this.updatesPerFrame = updatesPerFrame;
	}

	public void setIncremental(boolean incremental) {
		this.incremental = incremental;
	}

	public void keepInitialPostitions(boolean keep) {
		this.keepInitialPositions = keep;
	}

}
