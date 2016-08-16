/**
 * 
 */
package de.zintel.ai;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Friedemann
 *
 */
public class Perceptron {

	private final int dimension;

	private Double[] weights;

	private final boolean initRnd;

	private final Random rnd = new Random();

	/**
	 * 
	 */
	public Perceptron(final int dimension, boolean initRnd) {
		this.dimension = dimension + 1; // for theta must be 1 more
		this.initRnd = initRnd;
		this.weights = new Double[this.dimension];
	}

	public void init(final List<Double[]> trainingpositiv, final List<Double[]> trainingnegativ) {

		if (initRnd) {
			for (int i = 0; i < dimension; i++) {
				weights[i] = rnd.nextDouble();
			}
		} else {

			final Double[] vp = sum(trainingpositiv);
			final Double[] vn = sum(trainingnegativ);
			final Double[] rv = vectorsub(vp, vn);

			for (int i = 0; i < dimension - 1; i++) {
				weights[i] = rv[i];
			}

			weights[dimension - 1] = 0.0;
		}
	}

	public boolean train(final List<Double[]> trainingpositiv, final List<Double[]> trainingnegativ) throws Exception {

		boolean classifiedCorrectly;

		int iteration = 0;

		do {

			final Double[] cweights = clone(weights);

			classifiedCorrectly = true;
			iteration++;

			for (final Double[] values : trainingpositiv) {

				check(values);

				if (mul(cweights, values) <= 0) {
					classifiedCorrectly = false;
					add(cweights, values);
				}

			}

			for (final Double[] values : trainingnegativ) {

				check(values);

				if (mul(cweights, values) > 0) {
					classifiedCorrectly = false;
					sub(cweights, values);
				}

			}

			if (!classifiedCorrectly && equals(weights, cweights)) {
				System.out.println("cycling! no training possible!");
				return false;
			}

			weights = cweights;

		} while (classifiedCorrectly == false);

		System.out.println("iterations: " + iteration + " weigths: " + Arrays.asList(weights));
		return true;

	}

	private Double[] clone(final Double[] vector) {

		Double[] nvector = new Double[vector.length];
		for (int i = 0; i < vector.length; i++) {
			nvector[i] = vector[i];
		}

		return nvector;

	}

	private boolean equals(final Double[] v1, final Double[] v2) {

		for (int i = 0; i < v1.length; i++) {
			if (!v1[i].equals(v2[i])) {
				return false;
			}
		}

		return true;

	}

	private void check(final Double[] values) throws Exception {
		if (values.length != weights.length - 1) {
			throw new Exception(
					"vector values does not match weights. values.length=" + values.length + " weigths.length=" + weights.length);
		}
	}

	public int classify(final Double[] values) throws Exception {

		check(values);

		if (mul(weights, values) > 0) {
			return 1;
		} else {
			return 0;
		}

	}

	private double mul(final Double[] weights, final Double[] values) {

		double result = 0;
		for (int i = 0; i < values.length; i++) {
			result += weights[i] * values[i];
		}

		result += weights[dimension - 1];

		return result;

	}

	private void add(final Double[] weights, final Double[] values) {

		double length = length(values);

		for (int i = 0; i < values.length; i++) {
			weights[i] += values[i] / length;
		}

		weights[dimension - 1] += 1 / length;

	}

	private void sub(final Double[] weights, final Double[] values) {

		Double length = length(values);

		for (int i = 0; i < values.length; i++) {
			weights[i] -= values[i] / length;
		}

		weights[dimension - 1] -= 1 / length;

	}

	private Double length(final Double[] values) {

		double result = 0;

		for (double value : values) {
			result += value * value;
		}

		result += 1;

		return Math.sqrt(result);
	}

	private Double[] sum(final List<Double[]> vectors) {

		Double[] resultvector = null;

		for (Double vector[] : vectors) {

			if (resultvector == null) {
				resultvector = new Double[vector.length];

				for (int i = 0; i < resultvector.length; i++) {
					resultvector[i] = 0.0;
				}

			}

			for (int i = 0; i < vector.length; i++) {
				resultvector[i] += vector[i];
			}

		}

		return resultvector;

	}

	private Double[] vectorsub(final Double[] v1, final Double[] v2) {

		Double[] resultvector = new Double[v1.length];

		for (int i = 0; i < resultvector.length; i++) {
			resultvector[i] = v1[i] - v2[i];
		}

		return resultvector;

	}

}
