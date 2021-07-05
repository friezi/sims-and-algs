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
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Friedemann
 *
 */
public final class MathUtils {

	public static final Random RANDOM = new Random();

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
		return (int) interpolateLinearReal(start, end, iteration, maxIterations);
	}

	public static double interpolateLinearReal(double start, double end, int iteration, int maxIterations) {
		return interpolateReal(start, end, iteration, maxIterations, (x, max) -> x);
	}

	public static int interpolateLinearMoreScattering(int start, int end, int iteration, int maxIterations) {
		return interpolateMoreScattering(start, end, iteration, maxIterations, (x, max) -> x);
	}

	public static int interpolate(int start, int end, int iteration, int maxIterations, StepProjection p) {
		return (int) interpolateReal(start, end, iteration, maxIterations, p);
	}

	public static int interpolateMisc(int start, int end, int iteration, int maxIterations) {
		return interpolate(start, end, iteration, maxIterations,
				(x, max) -> x + (x < max / 2 ? 1 : -1) * 100 * Math.sin((x * Math.PI) / max) * Math.cos((x * Math.PI) / max));
	}

	public static double interpolateReal(double start, double end, int iteration, int maxIterations, StepProjection p) {

		final int maxSteps = maxIterations - 1;
		final int step = iteration - 1;
		// we could directly use this expression as transition-function, but it
		// would require additional casts int-> double and double -> int
		double projectedStep = Math.abs(p.project(step, maxSteps)) % (maxSteps + 1);

		return maxSteps <= 0 ? start : morph(x -> start, x -> end, x -> x, x -> (double) maxSteps, projectedStep);
	}

	/**
	 * @param fstart
	 *            start-function
	 * @param fend
	 *            end-function
	 * @param ftrans
	 *            transition-function: if ftrans:double->[0;1] in such way, that
	 *            ftrans(rangestart)=0 and ftrans(rangeend)=1 then a morph from
	 *            fstart to fend will take place
	 * @param x
	 *            value x
	 * @return morphed value
	 */
	public static double morph(final Function<Double, Double> fstart, final Function<Double, Double> fend,
			final Function<Double, Double> ftrans, final double x) {
		return morph(fstart, fend, ftrans, d -> 1.0, x);
	}

	/**
	 * @param fstart
	 *            start-function
	 * @param fend
	 *            end-function
	 * @param ftransNumerator
	 *            transition-function: if ftrans:double->[0;1] in such way, that
	 *            ftrans(rangestart)=0 and ftrans(rangeend)=1 then a morph from
	 *            fstart to fend well take place
	 * @param ftransDenominator
	 *            for precision-reasons it's possible to split up the
	 *            ftrans-function into two functions, so that the calculation
	 *            will be
	 *            (ftransNumerator()*(...))/ftransDenominator()=(ftransNumerator/ftransDenominator)*(...)
	 * @param x
	 *            value x
	 * @return morphed value
	 */
	public static double morph(final Function<Double, Double> fstart, final Function<Double, Double> fend,
			final Function<Double, Double> ftransNumerator, final Function<Double, Double> ftransDenominator, final double x) {

		final Double start = fstart.apply(x);
		final Double end = fend.apply(x);
		final Double numerator = ftransNumerator.apply(x);
		final Double denominator = ftransDenominator.apply(x);

		return start + (numerator * (end - start)) / denominator;
	}

	/**
	 * @param fstart
	 *            start-function
	 * @param fend
	 *            end-function
	 * @param ftransDividend
	 *            transition-function: if ftrans:double->[0;1] in such way, that
	 *            ftrans(rangestart)=0 and ftrans(rangeend)=1 then a morph from
	 *            fstart to fend well take place
	 * @param ftransDivisor
	 *            for precision-reasons it's possible to split up the
	 *            ftrans-function into two functions, so that the calculation
	 *            will be
	 *            (ftransDividend()*(...))/ftransDivisor()=(ftransDividend/ftransDivisor)*(...)
	 * @param x
	 *            value x
	 * @return morphed value
	 */
	public static <N extends AVectorND<N>, M extends AVectorND<M>> M morph(final Function<N, M> fstart, final Function<N, M> fend,
			final Function<N, Double> ftransDividend, final Function<N, Double> ftransDivisor, final N x) {
		final M start = fstart.apply(x);
		return fend.apply(x).substract(start).mult(ftransDividend.apply(x)).div(ftransDivisor.apply(x)).add(start);
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

	/**
	 * scale linear: a value in range [smin,smax] will be scaled to [tmin,tmax]
	 * 
	 * @param smin
	 *            source-range minimum
	 * @param smax
	 *            source-range maximum
	 * @param tmin
	 *            target-range minimum
	 * @param tmax
	 *            target-range maximum
	 * @param x
	 *            value (MUST BE WITHIN [smin,smax], NO CHECK!!!
	 * @return
	 */
	public static double scalel(final double smin, final double smax, final double tmin, final double tmax, final double x) {
		return scalea(smin, smax, tmin, tmax, t -> (t - smin), t -> (smax - smin), x);
	}

	/**
	 * scale arbitrary: a value in range [smin,smax] will be scaled to
	 * [tmin,tmax] with scaling transformation function
	 * (ftransNumerator(x)/ftransDenominator(x))
	 * 
	 * @param smin
	 *            source-range minimum
	 * @param smax
	 *            source-range maximum
	 * @param tmin
	 *            target-range minimum
	 * @param tmax
	 *            target-range maximum
	 * @param ftransNumerator
	 *            MUST provide values in range [0,1], SHOULD provide
	 *            (ftransNumerator(smin)/ftransDenominator(smin))==0,
	 *            (ftransNumerator(smax)/ftransDenominator(smax))==1
	 * @param ftransDenominator
	 *            MUST provide values in range [0,1], SHOULD provide
	 *            (ftransNumerator(smin)/ftransDenominator(smin))==0,
	 *            (ftransNumerator(smax)/ftransDenominator(smax))==1
	 * @param x
	 *            value (MUST BE WITHIN [smin,smax], NO CHECK!!!
	 * @return
	 */
	public static double scalea(final double smin, final double smax, final double tmin, final double tmax,
			final Function<Double, Double> ftransNumerator, final Function<Double, Double> ftransDenominator, final double x) {
		return smax == smin ? tmin : morph(t -> tmin, t -> tmax, ftransNumerator, ftransDenominator, x);
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

	public static int makeRandom(int min, int max) {
		return (int) scalel(0, max - min, min, max, RANDOM.nextInt(max - min + 1));
	}

	/**
	 * calculates the mean minimal distance of all items depending on a
	 * distance-operation.
	 * 
	 * @param items
	 * @param distanceOp
	 * @return
	 */
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

	/**
	 * clustering. calculates clusters of items.
	 * 
	 * @param items
	 * @param coefficientMeanMinDistance
	 * @param distanceOp
	 * @return
	 */
	public static <T> Set<Collection<T>> getClusters(final Collection<T> items, final Double coefficientMeanMinDistance,
			BiFunction<T, T, Double> distanceOp) {

		final Double meanMinDistance = getMeanMinDistance(items, distanceOp);

		final Set<Collection<T>> clusters = new HashSet<>();

		Queue<T> remainingItems = new LinkedList<>(items);
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

				final Queue<T> nonClassifiedItems = new LinkedList<>();
				while ((remainingItem = remainingItems.poll()) != null) {

					if (distanceOp.apply(parent, remainingItem) - coefficientMeanMinDistance * meanMinDistance <= 0) {
						cluster.add(remainingItem);
						parents.add(remainingItem);
					} else {
						nonClassifiedItems.add(remainingItem);
					}
				}

				remainingItems = nonClassifiedItems;
			}
		}

		return clusters;
	}

	public static boolean isSet(final long bitmask, final int bit) {
		return ((bitmask >> bit) & 1L) == 1L;
	}

	public static double sigmoid(double x) {
		return scalel(-1, 1, 0, 1, Math.tanh(x));
	}

	/**
	 * degree -> radian
	 * 
	 * @param degree
	 * @return
	 */
	public static double radian(final double degree) {
		return scalel(0, 360, 0, 2 * Math.PI, ((int) degree) % 360);
	}

	public static boolean inEpsilonRange(final double value) {
		return Math.abs(value) > 0.0000000001;
	}

	/**
	 * calculates the
	 * 
	 * @param sine
	 * @param cosine
	 * @return
	 */
	public static double angle(final double sine, final double cosine) {

		final double sigsine = Math.signum(sine);
		final double sigcosine = Math.signum(cosine);
		final double asine = Math.asin(sine);

		return sigcosine > 0 ? asine : sigsine * Math.PI - asine;

	}

}
