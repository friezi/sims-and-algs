/**
 * 
 */
package de.zintel.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Field of dim n
 * 
 * @author friedemann.zintel
 *
 */
public interface IVectorField {

	List<Integer> getDimensions();

	int getDimensionsCodomain();

	/**
	 * @param pos
	 *            vector dim n
	 * @return vector dim m
	 */
	VectorND getValue(VectorND pos);

	default VectorND interpolateLinear(VectorND pos) {

		final List<Integer> fieldDimensions = getDimensions();
		final List<Double> origin = new ArrayList<>(pos.getDim());
		// distancevector
		final List<Double> deltavector = new ArrayList<>(pos.getDim());
		long edgeComponetMask = 0L;
		int fieldDimension = 0;
		for (final double component : pos.getCoords()) {

			final double floor = Math.floor(component);
			origin.add(floor);
			deltavector.add(component - floor);

			// check for edge-position
			if (floor == fieldDimensions.get(fieldDimension) - 1) {
				edgeComponetMask |= 1L << fieldDimension;
			}
			fieldDimension++;
		}

		final int dimensions = fieldDimensions.size();
		final long cardinality = (int) Math.pow(2, dimensions);

		VectorND sum = null;
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

				final Double dvd = deltavector.get(dim);
				final boolean delta = MathUtils.isSet(targetSetMask, dim);
				prod *= (delta ? dvd : (1 - dvd));

				if (prod == 0.0) {
					break;
				}
			}

			if (prod == 0.0) {
				continue;
			}

			final VectorND targetVector = getTargetVector(origin, targetSetMask);
			final VectorND value = getValue(targetVector);
			sum = new VectorND(value.getDim()).add(value).mult(prod).add(sum == null ? new VectorND(value.getDim()) : sum);
		}

		return (sum == null ? new VectorND(getDimensionsCodomain()) : sum);
	}

	default VectorND getTargetVector(final List<Double> origin, final long dirmask) {

		final List<Double> dirVector = new ArrayList<>(origin.size());
		for (int i = 0; i < origin.size(); i++) {
			if (MathUtils.isSet(dirmask, i)) {
				dirVector.add(origin.get(i) + 1);
			} else {
				dirVector.add(origin.get(i));
			}
		}

		return new VectorND(dirVector.size(), dirVector, true);

	}

}
