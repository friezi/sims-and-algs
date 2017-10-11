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
		final List<Double> dv = new ArrayList<>(pos.getDim());
		for (final double comp : pos.getCoords()) {

			final double floor = Math.floor(comp);
			origin.add(floor);
			dv.add(comp - floor);
		}

		final int dim = getDimensions().size();
		final long card = (int) Math.pow(2, dim);

		final int outputDim = getValue(pos).getDim();
		final List<Double> outputVec = new ArrayList<>(outputDim);
		for (int i = 0; i < outputDim; i++) {
			double sum = 0;
			double prod = 1;
			for (long s = 0; s < card; s++) {

				final List<Double> tv = new ArrayList<>(origin.size());
				for (int d = 0; d < dim; d++) {

					final Double dvd = dv.get(d);
					final boolean delta = MathUtils.isSet(card, d);
					tv.add(delta ? origin.get(d) + 1 : origin.get(d));
					prod *= (delta ? dvd : (1 - dvd));
				}

				final VectorND directionVector = getDirectionVector(origin, s);
				sum += prod * getValue(directionVector).getCoords().get(i);
			}

			outputVec.add(sum);
		}

		return new VectorND(outputVec);
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
