/**
 * 
 */
package de.zintel.math;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Friedemann
 *
 */
public final class MathUtils {

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

	private MathUtils() {

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

	public static Optional<Double> max(final Collection<Double> collection) {
		return reduceDoubles(collection, Collectors.maxBy(DOUBLE_COMPARATOR));
	}

	public static Optional<Double> min(final Collection<Double> collection) {
		return reduceDoubles(collection, Collectors.minBy(DOUBLE_COMPARATOR));
	}

	public static Double sum(final Collection<Double> collection) {
		return reduceDoubles(collection, Collectors.summingDouble(x -> x));
	}

	private static <T> T reduceDoubles(final Collection<Double> collection, final Collector<Double, ?, T> collector) {
		return collection.stream().collect(collector);
	}

	public static <T> Double getMeanMinDistance(final Collection<T> items, BiFunction<T, T, Double> distanceOp) {

		final Collection<Double> minDistances = new ArrayList<>(items.size());
		for (T item : items) {
			final Collection<Double> distances = new ArrayList<>(items.size());

			for (T neighbour : items) {
				if (item == neighbour) {
					continue;
				}

				distances.add(distanceOp.apply(item, neighbour));
			}

			minDistances.add(MathUtils.min(distances).get());
		}

		final Double meanMinDistance = MathUtils.sum(minDistances) / items.size();
		return meanMinDistance;

	}

	public static <T> Set<Collection<T>> getClusters(final Collection<T> items, final Double coefficientMeanMinDistance,
			BiFunction<T, T, Double> distanceOp) {

		final Double meanMinDistance = getMeanMinDistance(items, distanceOp);

		final Set<Collection<T>> clusters = new HashSet<>();

		final Queue<T> remainingItems = new LinkedList<>(items);
		T clusterItem = null;
		while ((clusterItem = remainingItems.poll()) != null) {

			final Set<T> cluster = new HashSet<>();
			clusters.add(cluster);
			cluster.add(clusterItem);

			final Queue<T> parents = new LinkedList<>();
			parents.add(clusterItem);

			T parent = null;
			T remainingItem = null;
			while ((parent = parents.poll()) != null) {

				final Queue<T> nonClassified = new LinkedList<>();
				while ((remainingItem = remainingItems.poll()) != null) {

					if (distanceOp.apply(parent, remainingItem) - coefficientMeanMinDistance * meanMinDistance <= 0) {
						cluster.add(remainingItem);
						parents.add(remainingItem);
					} else {
						nonClassified.add(remainingItem);
					}
				}

				remainingItems.addAll(nonClassified);
			}
		}

		return clusters;
	}

}
