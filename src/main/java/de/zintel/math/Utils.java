/**
 * 
 */
package de.zintel.math;

import java.awt.Point;

/**
 * @author Friedemann
 *
 */
public final class Utils {

	@FunctionalInterface
	public static interface StepProjection {

		double project(int x, int max);

	}

	private Utils() {

	}

	public static int interpolateLinear(int v1, int v2, int iteration, int maxIterations) {
		return interpolate(v1, v2, iteration, maxIterations, (x, max) -> {
			return x;
		});
	}

	public static double interpolateLinearReal(double v1, double v2, int iteration, int maxIterations) {
		return interpolateReal(v1, v2, iteration, maxIterations, (x, max) -> {
			return x;
		});
	}

	public static int interpolateLinearMoreScattering(int v1, int v2, int iteration, int maxIterations) {
		return interpolateMoreScattering(v1, v2, iteration, maxIterations, (x, max) -> {
			return x;
		});
	}

	public static int interpolateTexture(int v1, int v2, int iteration, int maxIterations) {
		return interpolateMisc(v1, v2, iteration, maxIterations);
	}

	public static int interpolateMisc(int v1, int v2, int iteration, int maxIterations) {
		return interpolate(v1, v2, iteration, maxIterations, (x, max) -> {
			return x + (x < max / 2 ? 1 : -1) * 100 * Math.sin((x * Math.PI) / max) * Math.cos((x * Math.PI) / max);
		});
	}

	public static int interpolate(int v1, int v2, int iteration, int maxIterations, StepProjection p) {
		return (int) interpolateReal(v1, v2, iteration, maxIterations, p);
	}

	public static double interpolateReal(double v1, double v2, int iteration, int maxIterations, StepProjection p) {

		final int maxSteps = maxIterations - 1;
		final int step = iteration - 1;
		double projectedStep = Math.abs(p.project(step, maxSteps)) % (maxSteps + 1);
		return (maxSteps <= 0 ? v1 : (v1 + (((projectedStep * (v2 - v1))) / maxSteps)));
	}

	public static int interpolateMoreScattering(int v1, int v2, int iteration, int maxIterations, StepProjection p) {

		final int maxSteps = maxIterations - 1;
		final int step = iteration - 1;
		double projectedStep = Math.abs(p.project(step, maxSteps)) % (maxSteps + 1);
		return (maxSteps <= 0 ? v1 : (v1 - ((int) (v1 * projectedStep)) / maxSteps + ((int) (v2 * projectedStep)) / maxSteps));
	}

	public static int distance(Point p1, Point p2) {
		int dX = p2.x - p1.x;
		int dY = p2.y - p1.y;
		return (int) Math.sqrt(dX * dX + dY * dY);
	}

	public static int length(Point vector) {
		return distance(new Point(), vector);
	}

}
