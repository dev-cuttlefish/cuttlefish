package ch.ethz.sg.cuttlefish.gui.visualization.geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

// TODO ilias: Unused??
public class Transformer2D {

	private static class Translation {
		double x = 0;
		double y = 0;
		double z = 0;
	}

	private static class Vector {
		double x = 0;
		double y = 0;
		double z = 0;
	}

	private static Translation translation = new Translation();
	private static double rotation = 0;
	private static Vector rotationVector = new Vector();

	public static void setTranslation(double x, double y) {
		Transformer2D.translation.x = x;
		Transformer2D.translation.y = y;
	}

	public static void setRotation(double radians, double x, double y, double z) {
		Transformer2D.rotation = radians;

		Transformer2D.rotationVector.x = x;
		Transformer2D.rotationVector.y = y;
		Transformer2D.rotationVector.z = z;

		normalizeVector(Transformer2D.rotationVector);
	}

	public static Point2D transformPoint(double x, double y) {
		Point2D p = new Point2D.Double(x, y);
		return transformPoint(p);
	}

	public static Point2D transformPoint(Point2D pt) {

		// Compute transformation matrix
		double matrix[][] = new double[4][4];
		matrix[0][3] = translation.x;
		matrix[1][3] = translation.y;
		matrix[2][3] = translation.z;
		matrix[3][0] = 0;
		matrix[3][1] = 0;
		matrix[3][2] = 0;
		matrix[3][3] = 1;

		double c = Math.cos(rotation);
		double s = Math.sin(rotation);
		double x = rotationVector.x;
		double y = rotationVector.y;
		double z = rotationVector.z;

		matrix[0][0] = x * x * (1 - c) + c;
		matrix[0][1] = x * y * (1 - c) - z * s;
		matrix[0][2] = x * z * (1 - c) + y * s;
		matrix[1][0] = y * x * (1 - c) + z * s;
		matrix[1][1] = y * y * (1 - c) + c;
		matrix[1][2] = y * z * (1 - c) - x * s;
		matrix[2][0] = x * z * (1 - c) - y * s;
		matrix[2][1] = y * z * (1 - c) + x * s;
		matrix[2][2] = z * z * (1 - c) + c;

		double point[] = { pt.getX(), pt.getY(), 0, 1 };
		double result[] = multiply(matrix, point);

		pt.setLocation(result[0], result[1]);
		return pt;
	}

	public static Collection<Point2D> transformVector(Collection<Point2D> v) {
		ArrayList<Point2D> transformed = new ArrayList<Point2D>();

		for (Point2D p : v) {
			transformPoint(p);
			transformed.add(p);
		}

		return transformed;
	}

	private static void normalizeVector(Vector v) {
		double x = v.x;
		double y = v.y;
		double z = v.z;

		double length = Math.abs(Math.sqrt(x * x + y * y + z * z));

		v.x = x / length;
		v.y = y / length;
		v.z = z / length;
	}

	private static double[] multiply(double[][] m, double[] v) {
		int mrows = m.length;
		int mcols = m[0].length;
		int vrows = v.length;
		double[] result = new double[mrows];

		if (mcols != vrows)
			throw new IllegalArgumentException("matrices don't match: " + mcols
					+ " != " + vrows);

		for (int i = 0; i < mrows; i++)
			for (int k = 0; k < mcols; k++)
				result[i] += m[i][k] * v[k];

		return result;
	}
}
