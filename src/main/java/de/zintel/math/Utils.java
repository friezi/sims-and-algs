/**
 * 
 */
package de.zintel.math;

import java.awt.Point;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Friedemann
 *
 */
public final class Utils {

	@FunctionalInterface
	public static interface StepProjection {

		double project(int x, int max);

	}

	public static final Comparator<Double> DOUBLE_COMPARATOR = new Comparator<Double>() {

		@Override
		public int compare(Double o1, Double o2) {
			return Double.compare(o1, o2);
		}
	};

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

	public static Double max(final Collection<Double> collection) {
		return minmax(collection, Collectors.maxBy(DOUBLE_COMPARATOR));
	}

	public static Double min(final Collection<Double> collection) {
		return minmax(collection, Collectors.minBy(DOUBLE_COMPARATOR));
	}

	public static Double sum(final Collection<Double> collection) {
		return collection.stream().collect(Collectors.summingDouble(x -> x));
	}

	private static Double minmax(final Collection<Double> collection, final Collector<Double, ?, Optional<Double>> collector) {

		if (collection.isEmpty()) {
			throw new IllegalArgumentException("collection is null");
		}

		return collection.stream().collect(collector).get();
	}

}
