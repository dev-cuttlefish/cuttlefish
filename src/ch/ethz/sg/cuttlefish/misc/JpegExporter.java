package ch.ethz.sg.cuttlefish.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

public final class JpegExporter {

	private Layout<Vertex, Edge> layout;
	private BrowsableNetwork network;
	private VisualizationViewer<Vertex, Edge> vv;
	private Dimension dimensions;

	public JpegExporter(Layout<Vertex, Edge> layout, BrowsableNetwork network,
			VisualizationViewer<Vertex, Edge> vv, int height, int width) {
		this.layout = layout;
		this.network = network;
		this.dimensions = new Dimension(width, height);
		this.vv = vv;
		this.vv.setBackground(Color.white);
		this.vv.setSize(dimensions);
	}

	public final boolean exportToJpeg(String outputFilename) {

		BufferedImage img = prepareImage();

		try {
			OutputStream out;
			JPEGImageEncoder encoder;
			JPEGEncodeParam param;
			out = new FileOutputStream(outputFilename);
			encoder = JPEGCodec.createJPEGEncoder(out);
			param = JPEGCodec.getDefaultJPEGEncodeParam(img);

			param.setQuality(1.0f, true);
			encoder.setJPEGEncodeParam(param);

			encoder.encode(img, param);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private BufferedImage prepareImage() {

		// Center layout
		NetworkPanel.centerGraph(vv, network, layout, null);
		// NetworkPanel.centerGraph(vv, network, layout, null);

		// Scale to image size
		ScalingControl scaler = new CrossoverScalingControl();
		Point2D center = vv.getCenter();
		float amount = getScaleFactor(dimensions.width, dimensions.height);
		scaler.scale(vv, amount, center);
		vv.repaint();

		System.out.println("Painting");
		BufferedImage img = new BufferedImage(dimensions.width,
				dimensions.height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = img.createGraphics();
		vv.paint(g);
		g.dispose();

		return img;
	}

	private float getScaleFactor(int imgWidth, int imgHeight) {

		MutableTransformer layout2 = vv.getRenderContext()
				.getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
		double top = Double.MAX_VALUE;
		double bottom = Double.MAX_VALUE;
		double left = Double.MAX_VALUE;
		double right = Double.MAX_VALUE;
		double maxSize = 0;

		for (Vertex v : network.getVertices()) {
			Point2D p;
			if (layout instanceof AbstractLayout)
				p = ((AbstractLayout<Vertex, Edge>) layout).transform(v);
			else
				p = ((TreeLayout<Vertex, Edge>) layout).transform(v);
			Point2D invP = layout2.transform(p);
			if (top < invP.getY() || top == Double.MAX_VALUE)
				top = invP.getY();
			if (bottom > invP.getY() || bottom == Double.MAX_VALUE)
				bottom = invP.getY();
			if (left > invP.getX() || left == Double.MAX_VALUE)
				left = invP.getX();
			if (right < invP.getX() || right == Double.MAX_VALUE)
				right = invP.getX();

			if (v.getSize() > maxSize)
				maxSize = v.getSize();
		}

		double width = Math.abs(left - right);
		double height = Math.abs(top - bottom);

		if (width > height)
			return (float) ((imgWidth - maxSize * 0.5) / width);
		else
			return (float) ((imgHeight - maxSize * 0.5) / height);
	}
}