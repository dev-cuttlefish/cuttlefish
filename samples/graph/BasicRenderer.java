/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package samples.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.visualization.AbstractRenderer;

/**
 * @author Scott White
 */
public class BasicRenderer extends AbstractRenderer {
	private String mSizeKey;

	private double mNodeSizeScale;
	private int mDefaultNodeSize;

	public BasicRenderer() {
		mNodeSizeScale = 1;
		mDefaultNodeSize = 8;
	}

	public void paintEdge(Graphics g, Edge e, int x1, int y1, int x2, int y2) {
		Color c = g.getColor();
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(x1, y1, x2, y2);
		g.setColor(c);
	}

	public void paintVertex(Graphics g, Vertex v, int x, int y) {

		String label = null;
		if (getLabel() != null) {
//			label = (String) v.getUserDatum(getLabel());
			label = StringLabeller.getLabeller((Graph) v.getGraph()).getLabel(v);
		}

		if (label == null) {
			label = v.toString();
		}

		if (label.length() > 15) {
			label = label.substring(0, 14);
		}

		int nodeSize = mDefaultNodeSize;
		if (mSizeKey != null) {
			Number decoratedNodeSize = (Number) v.getUserDatum(mSizeKey);
			nodeSize =
				(int) Math.ceil(
					decoratedNodeSize.doubleValue() * mNodeSizeScale);
		}

		if (isPicked(v))
			g.setColor(Color.ORANGE);
		else
			g.setColor(Color.RED);

		int labelSize = g.getFontMetrics().stringWidth(label);
		nodeSize = Math.max(nodeSize, 10);
		nodeSize = Math.min(nodeSize, 150);

		g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
		g.setColor(Color.GRAY);
		g.drawOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
		g.setColor(Color.BLACK);
		Font font = new Font("Arial", Font.PLAIN, 12);
		Font f = g.getFont();
		g.setFont(font);
		if (nodeSize > labelSize) {
			g.drawString(label, x - labelSize / 2, y + 4);
		} else {
			g.drawString(label, x - labelSize / 2 + 20, y + 15);

		}
		g.setFont(f);
	}

	public String getSizeKey() {
		return mSizeKey;
	}

	public void setSizeKey(String decorationKey) {
		this.mSizeKey = decorationKey;
	}
	
	String mLabel;

	public String getLabel() {
		return mLabel;
	}

	public void setLabel(String label) {
		this.mLabel = label;
	}

	public double getNodeSizeScale() {
		return mNodeSizeScale;
	}

	public void setNodeSizeScale(double nodeSizeScale) {
		this.mNodeSizeScale = nodeSizeScale;
	}
}
