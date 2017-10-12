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
public interface Field {

	List<Integer> getDimensions();

	/**
	 * @param pos
	 *            vector dim n
	 * @return vector dim m
	 */
	VectorND getValue(VectorND pos);

	default VectorND interpolateBilinear(VectorND pos) {

		final List<Double> origin = new ArrayList<>(pos.getDim());
		// distancevector
		final List<Double> dv = new ArrayList<>(pos.getDim());
		for (final double comp : pos.getCoords()) {

			final double floor = Math.floor(comp);
			origin.add(floor);
			dv.add(comp - floor);
		}

		final int dim = getDimensions().size();
		final long card = (int) Math.pow(2, dim);

		final int outputDim = getValue(pos).getDim();
		VectorND sum = new VectorND(outputDim);
		for (long dirset = 0; dirset < card; dirset++) {

			double prod = 1;
			for (int d = 0; d < dim; d++) {

				final Double dvd = dv.get(d);
				final boolean delta = MathUtils.isSet(card, d);
				prod *= (delta ? dvd : (1 - dvd));
			}

			final VectorND directionVector = getDirectionVector(origin, dirset);
			sum.add(new VectorND(outputDim).add(getValue(directionVector)).mult(prod));
		}

		return sum;
	}

	default VectorND getDirectionVector(final List<Double> origin, final long dirmask) {

		final List<Double> dirVector = new ArrayList<>(origin.size());
		for (int i = 0; i < origin.size(); i++) {
			if (MathUtils.isSet(dirmask, i)) {
				dirVector.add(origin.get(i) + 1);
			} else {
				dirVector.get(i);
			}
		}

		return new VectorND(dirVector);

	}

}
