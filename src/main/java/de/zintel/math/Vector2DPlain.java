/**
 * 
 */
package de.zintel.math;

import java.awt.Point;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Friedemann
 *
 */
public class Vector2DPlain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5754883813412571661L;

	public static final Vector2DPlain NULL_VECTOR = new Vector2DPlain();

	public double x;

	public double y;

	private double length = -1;

	public Vector2DPlain() {
		this(0.0, 0.0);
	}

	public Vector2DPlain(Vector2DPlain vector) {
		this(vector.x, vector.y);
	}

	public Vector2DPlain(Point point) {
		this(point.x, point.y);
	}

	public Vector2DPlain(double x, double y) {
		super();
		this.x = x;
		this.y = y;
		checkForNaN();
	}

	/**
	 * only for deserialization purpose!
	 * 
	 * @param x
	 * @param y
	 * @param length
	 */
	public Vector2DPlain(double x, double y, double length) {
		this(x, y);
		this.length = length;
	}

	public double length() {

		if (length == -1) {
			length = Math.sqrt(mult(this, this));
		}

		return length;
	}

	public Vector2DPlain add(Vector2DPlain vector) {
		return add(vector.x, vector.y);
	}

	public Vector2DPlain substract(Vector2DPlain vector) {
		return substract(vector.x, vector.y);
	}

	public Vector2DPlain mult(double value) {
		this.x *= value;
		this.y *= value;
		checkForNaN();
		length = -1;
		return this;
	}

	public Vector2DPlain add(double x, double y) {
		this.x += x;
		this.y += y;
		checkForNaN();
		length = -1;
		return this;
	}

	public Vector2DPlain substract(double x, double y) {
		this.x -= x;
		this.y -= y;
		checkForNaN();
		length = -1;
		return this;
	}

	public static Vector2DPlain mult(double val, Vector2DPlain vector) {
		final Vector2DPlain nvector = new Vector2DPlain(vector);
		nvector.mult(val);
		return nvector;
	}

	public static Vector2DPlain add(Vector2DPlain a, Vector2DPlain b) {
		final Vector2DPlain nvector = new Vector2DPlain(a);
		nvector.add(b);
		return nvector;
	}

	public static Vector2DPlain substract(Vector2DPlain a, Vector2DPlain b) {
		final Vector2DPlain nvector = new Vector2DPlain(a);
		nvector.substract(b);
		return nvector;
	}

	public static double mult(Vector2DPlain a, Vector2DPlain b) {
		return a.x * b.x + a.y * b.y;
	}

	public boolean isNullVector() {
		return x == 0.0 && y == 0.0;
	}

	public static Vector2DPlain normalize(Vector2DPlain vector) {
		return mult(1 / vector.length(), vector);
	}

	public static Vector2DPlain max(Vector2DPlain v1, Vector2DPlain v2) {
		return (v1.length() >= v2.length() ? v1 : v2);
	}

	public Polar2D toPolar() {
		return new Polar2D(length(), Math.atan2(y, x));
	}

	public Vector2D toND() {
		return new Vector2D(Arrays.asList(x, y));
	}

	public Point toPoint() {
		return new Point((int) x, (int) y);
	}

	private void checkForNaN() throws RuntimeException {
		if (!Double.isFinite(x)) {
			x = Double.MAX_VALUE;
			// System.out.println("WARNING: adjusted x to 0.0 due to Nan or
			// infinity!");
		}
		if (!Double.isFinite(y)) {
			y = Double.MAX_VALUE;
			// System.out.println("WARNING: adjusted y to 0.0 due to Nan or
			// infinity!");
		}
	}

	@Override
	public String toString() {
		return "Vector2D [x=" + x + ", y=" + y + "]";
	}

	public static double distance(Vector2DPlain p1, Vector2DPlain p2) {
		return substract(p2, p1).length();
	}

}
