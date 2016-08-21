/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Point;

/**
 * @author Friedemann
 *
 */
public class Vector2D {

	public double x;

	public double y;

	private double length = -1;

	public Vector2D() {
		this(0.0, 0.0);
	}

	public Vector2D(Vector2D vector) {
		this(vector.x, vector.y);
	}

	public Vector2D(Point point) {
		this(point.x, point.y);
	}

	public Vector2D(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double length() {

		if (length == -1) {
			length = Math.sqrt(x * x + y * y);
		}

		return length;
	}

	public Vector2D add(Vector2D vector) {
		add(vector.x, vector.y);
		return this;
	}

	public Vector2D substract(Vector2D vector) {
		add(-vector.x, -vector.y);
		return this;
	}

	public void mult(double value) {
		this.x *= value;
		this.y *= value;
	}

	public void add(double x, double y) {
		this.x += x;
		this.y += y;
	}

	public static Vector2D mult(Vector2D vector, double val) {
		return new Vector2D(val * vector.x, val * vector.y);
	}

	public static Vector2D add(Vector2D a, Vector2D b) {
		return new Vector2D(a.x + b.x, a.y + b.y);
	}

	public static Vector2D substract(Vector2D a, Vector2D b) {
		return new Vector2D(a.x - b.x, a.y - b.y);
	}

	public static Vector2D normalize(Vector2D vector) {

		final double vLength = vector.length();
		return new Vector2D(vector.x / vLength, vector.y / vLength);

	}

	public Polar toPolar() {
		return new Polar(length(), x != 0 ? Math.acos(x / length()) : Math.asin(y / length()));
	}

	@Override
	public String toString() {
		return "Vector2D [x=" + x + ", y=" + y + "]";
	}

	public static double distance(Vector2D p1, Vector2D p2) {
		double dX = p2.x - p1.x;
		double dY = p2.y - p1.y;
		return Math.sqrt(dX * dX + dY * dY);
	}

}
