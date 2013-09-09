package ch.ethz.sg.cuttlefish.gui.visualization;

import java.awt.geom.Point2D;

import ch.ethz.sg.cuttlefish.networks.Vertex;

public final class Utilities {

	public static Point2D getBorderPoint(Vertex v, Point2D fromPoint,
			double scale) {
		double angle = calculateAngle(fromPoint, v.getPosition());

		return getBorderPoint(v, angle, scale);
	}

	public static Point2D getBorderPoint(Vertex v, double angrad, double scale) {
		double x, y;
		Point2D b = null;

		if (v.getShape().equalsIgnoreCase(Constants.SHAPE_DISK)) {
			x = v.getPosition().getX() - v.getSize() * Math.cos(angrad) / scale;
			y = v.getPosition().getY() - v.getSize() * Math.sin(angrad) / scale;

			b = new Point2D.Float();
			b.setLocation(x, y);

		} else if (v.getShape().equalsIgnoreCase(Constants.SHAPE_SQUARE)) {
			double size = v.getSize();
			double r = size;
			double pi = Math.PI;

			if (rangeContains(angrad, 0, 0.25 * pi)
					|| rangeContains(angrad, 1.75 * pi, 2 * pi)) {
				r = size / Math.cos(angrad);
			} else if (rangeContains(angrad, 0.25 * pi, 0.75 * pi)) {
				r = size / Math.sin(angrad);
			} else if (rangeContains(angrad, 0.75 * pi, 1.25 * pi)) {
				r = -size / Math.cos(angrad);
			} else {
				r = -size / Math.sin(angrad);
			}

			r /= scale;

			x = v.getPosition().getX() - r * Math.cos(angrad);
			y = v.getPosition().getY() - r * Math.sin(angrad);

			b = new Point2D.Float();
			b.setLocation(x, y);
		}

		return b;
	}

	public static double getBorderDistance(Vertex v, Point2D point, double scale) {
		double angle = calculateAngle(point, v.getPosition());

		return getBorderDistance(v, angle, scale);
	}

	public static double getBorderDistance(Vertex v, double angle, double scale) {
		double dist = -1;

		if (v.getShape().equalsIgnoreCase(Constants.SHAPE_DISK)) {
			dist = v.getSize() / scale;

		} else if (v.getShape().equalsIgnoreCase(Constants.SHAPE_SQUARE)) {
			Point2D border = getBorderPoint(v, angle, scale);

			return v.getPosition().distance(border);
		}

		return dist;
	}

	public static double calculateAngle(final Point2D from, final Point2D to) {
		double fx = from.getX();
		double fy = from.getY();
		double tx = to.getX();
		double ty = to.getY();
		double d = from.distance(to);

		double angle = Math.asin((ty - fy) / d);

		// Transform angle to a [0 ... 2π) value
		if (tx < fx) {
			angle = Math.PI - angle;
		} else if (fx < tx && ty < fy) {
			angle = 2 * Math.PI - Math.abs(angle);
		}

		return angle;
	}

	public static final boolean rangeContains(double val, double leftLimit,
			double rightLimit) {
		return leftLimit <= val && val < rightLimit;
	}
}
