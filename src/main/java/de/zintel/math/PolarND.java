/**
 * 
 */
package de.zintel.math;

import java.util.Arrays;

/**
 * @author friedemann.zintel
 *
 */
public class PolarND {

	private double radius;

	private VectorND angles;

	public PolarND(double radius, VectorND angles) {
		this.radius = radius;
		this.angles = angles;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public VectorND getAngles() {
		return angles;
	}

	public void setAngles(VectorND angles) {
		this.angles = angles;
	}

	public VectorND toCartesian() {

		final Double[] coords = new Double[angles.getDim() + 1];

		double cosprod = 1;
		for (int i = angles.getDim() - 1; i >= 0; i--) {

			if (i < angles.getDim() - 1) {
				cosprod *= Math.cos(angles.get(i));
			}
			final double coord = radius * (i == 0 ? 1 : Math.sin(angles.get(i - 1))) * cosprod;
			coords[i] = coord;

		}

		return new VectorND(Arrays.asList(coords));

	}

}
