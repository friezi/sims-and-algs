/**
 * 
 */
package de.zintel.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Field of dim n with vectors of dim m.
 * 
 * @author friedemann.zintel
 *
 */
public interface IVectorField<N extends AVectorND<N>, M extends AVectorND<M>> {

	List<Integer> getDimensions();

	int getDimensionsCodomain();

	/**
	 * @param pos
	 *            vector dim n
	 * @return vector dim m
	 */
	M getValue(N pos);

	void setValue(N pos, M value);

	List<M> asList();

	/**
	 * 
	 * ATTENTION!! works only upto dim=number-of-bits-of-long
	 * 
	 * @param coordinatevector
	 * @param factory
	 * @return
	 */
	default M interpolateLinear(N coordinatevector, IVectorFactory<M> factory) {

		final List<Integer> fieldDimensions = getDimensions();
		final List<Double> floorCoordinates = new ArrayList<>(coordinatevector.getDim());
		// distancevector
		final List<Double> deltacoordinates = new ArrayList<>(coordinatevector.getDim());
		long edgeComponetMask = 0L;
		int fieldDimension = 0;
		for (final double coordinate : coordinatevector.getValues()) {

			final double floor = Math.floor(coordinate);
			floorCoordinates.add(floor);
			deltacoordinates.add(coordinate - floor);

			// check for edge-position
			if (floor == fieldDimensions.get(fieldDimension) - 1) {
				edgeComponetMask |= 1L << fieldDimension;
			}
			fieldDimension++;
		}

		final int dimensions = fieldDimensions.size();
		final long cardinality = (int) Math.pow(2, dimensions);

		M sum = null;
		for (long targetSetMask = 0; targetSetMask < cardinality; targetSetMask++) {

			if ((targetSetMask & edgeComponetMask) > 0) {
				// edge-position contained in dimension-set for target
				continue;
			}

			double prod = 1;
			for (int dim = 0; dim < dimensions; dim++) {

				// check for edge-position of dimension
				if (MathUtils.isSet(edgeComponetMask, dim)) {
					continue;
				}

				final Double dvd = deltacoordinates.get(dim);
				final boolean delta = MathUtils.isSet(targetSetMask, dim);
				prod *= (delta ? dvd : (1 - dvd));

				if (prod == 0.0) {
					break;
				}
			}

			if (prod == 0.0) {
				continue;
			}

			final N targetCoordinates = getTargetCoordinates(floorCoordinates, targetSetMask, coordinatevector);
			final M value = getValue(targetCoordinates);
			sum = factory.newVector().add(value).mult(prod).add(sum == null ? factory.newVector() : sum);
		}

		return (sum == null ? factory.newVector() : sum);
	}

	default N getTargetCoordinates(final List<Double> floorcoordinates, final long directionmask, IVectorFactory<N> factory) {

		final List<Double> targetcoordinates = new ArrayList<>(floorcoordinates.size());
		for (int i = 0; i < floorcoordinates.size(); i++) {
			if (MathUtils.isSet(directionmask, i)) {
				targetcoordinates.add(floorcoordinates.get(i) + 1);
			} else {
				targetcoordinates.add(floorcoordinates.get(i));
			}
		}

		return factory.newVector(targetcoordinates);

	}

}
