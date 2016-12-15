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

	public static int interpolateLinear(int start, int end, int iteration, int maxIterations) {
		return interpolate(start, end, iteration, maxIterations, (x, max) -> {
			return x;
		});
	}

	public static double interpolateLinearReal(double start, double end, int iteration, int maxIterations) {
		return interpolateReal(start, end, iteration, maxIterations, (x, max) -> {
			return x;
		});
	}

	public static int interpolateLinearMoreScattering(int start, int end, int iteration, int maxIterations) {
		return interpolateMoreScattering(start, end, iteration, maxIterations, (x, max) -> {
			return x;
		});
	}

	public static int interpolateTexture(int start, int end, int iteration, int maxIterations) {
		return interpolateMisc(start, end, iteration, maxIterations);
	}

	public static int interpolateMisc(int start, int end, int iteration, int maxIterations) {
		return interpolate(start, end, iteration, maxIterations, (x, max) -> {
			return x + (x < max / 2 ? 1 : -1) * 100 * Math.sin((x * Math.PI) / max) * Math.cos((x * Math.PI) / max);
		});
	}

	public static int interpolate(int start, int end, int iteration, int maxIterations, StepProjection p) {
		return (int) interpolateReal(start, end, iteration, maxIterations, p);
	}

	public static double interpolateReal(double start, double end, int iteration, int maxIterations, StepProjection p) {

		final int maxSteps = maxIterations - 1;
		final int step = iteration - 1;
		double projectedStep = Math.abs(p.project(step, maxSteps)) % (maxSteps + 1);
		return (maxSteps <= 0 ? start : (start + (((projectedStep * (end - start))) / maxSteps)));
	}

	public static int interpolateMoreScattering(int start, int end, int iteration, int maxIterations, StepProjection p) {

		final int maxSteps = maxIterations - 1;
		final int step = iteration - 1;
		double projectedStep = Math.abs(p.project(step, maxSteps)) % (maxSteps + 1);
		return (maxSteps <= 0 ? start : (start - ((int) (start * projectedStep)) / maxSteps + ((int) (end * projectedStep)) / maxSteps));
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
