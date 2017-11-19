/**
 * 
 */
package de.zintel.math;

/**
 * @author Friedemann
 *
 */
public class Polar2D {

	public double radius;

	public double angle;

	public Polar2D(double radius, double angle) {
		this.radius = radius;
		this.angle = angle;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public Vector2D toCartesian() {
		double x = radius * Math.cos(angle);
		double y = radius * Math.sin(angle);

		if (Double.isNaN(x) || Double.isNaN(y)) {
			System.out.println("NaN: x=" + x + ", y=" + y + " " + this);
			System.exit(1);
		}

		return new Vector2D(x, y);
	}

	@Override
	public String toString() {
		return "Polar [radius=" + radius + ", angle=" + angle + "]";
	}

}
