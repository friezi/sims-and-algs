/**
 * 
 */
package de.zintel.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author friedemann.zintel
 *
 */
public class PolarND {

	private double radius;

	private List<Double> angles;

	public PolarND(double radius, Collection<Double> angles) {
		this.radius = radius;
		this.angles = new ArrayList<>(angles);
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public List<Double> getAngles() {
		return angles;
	}

	public void setAngles(List<Double> angles) {
		this.angles = angles;
	}

	public VectorND toCartesian() {

		final Double[] coords = new Double[angles.size() + 1];

		double cosprod = 1;
		for (int i = angles.size(); i >= 0; i--) {

			if (i < angles.size()) {
				cosprod *= Math.cos(angles.get(i));
			}
			final double coord = radius * (i == 0 ? 1 : Math.sin(angles.get(i - 1))) * cosprod;
			coords[i] = coord;

		}

		return new VectorND(Arrays.asList(coords));

	}

}
