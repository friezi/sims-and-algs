/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Point;
import java.io.Serializable;

/**
 * @author Friedemann
 *
 */
public class Vector2D implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5754883813412571661L;

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

	/**
	 * only for deserialization purpose!
	 * 
	 * @param x
	 * @param y
	 * @param length
	 */
	public Vector2D(double x, double y, double length) {
		this(x, y);
		this.length = length;
	}

	public double length() {

		if (length == -1) {
			length = Math.sqrt(mult(this, this));
		}

		return length;
	}

	public Vector2D add(Vector2D vector) {
		return add(vector.x, vector.y);
	}

	public Vector2D substract(Vector2D vector) {
		return substract(vector.x, vector.y);
	}

	public Vector2D mult(double value) {
		this.x *= value;
		this.y *= value;
		length = -1;
		return this;
	}

	public Vector2D add(double x, double y) {
		this.x += x;
		this.y += y;
		length = -1;
		return this;
	}

	public Vector2D substract(double x, double y) {
		this.x -= x;
		this.y -= y;
		length = -1;
		return this;
	}

	public static Vector2D mult(double val, Vector2D vector) {
		final Vector2D nvector = new Vector2D(vector);
		nvector.mult(val);
		return nvector;
	}

	public static Vector2D add(Vector2D a, Vector2D b) {
		final Vector2D nvector = new Vector2D(a);
		nvector.add(b);
		return nvector;
	}

	public static Vector2D substract(Vector2D a, Vector2D b) {
		final Vector2D nvector = new Vector2D(a);
		nvector.substract(b);
		return nvector;
	}

	public static double mult(Vector2D a, Vector2D b) {
		return a.x * b.x + a.y * b.y;
	}

	public boolean isNullVector() {
		return x == 0.0 && y == 0.0;
	}

	public static Vector2D normalize(Vector2D vector) {
		return mult(1 / vector.length(), vector);
	}

	public static Vector2D max(Vector2D v1, Vector2D v2) {
		return (v1.length() >= v2.length() ? v1 : v2);
	}

	public Polar toPolar() {
		return new Polar(length(), x != 0 ? Math.acos(x / length()) : Math.asin(y / length()));
	}

	@Override
	public String toString() {
		return "Vector2D [x=" + x + ", y=" + y + "]";
	}

	public static double distance(Vector2D p1, Vector2D p2) {
		return substract(p2, p1).length();
	}

}
