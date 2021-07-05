/**
 * 
 */
package de.zintel.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

import de.zintel.math.matrix.DMatrix;

/**
 * 
 * Attention! To not produce ClasscCastExceptions, the type T must always be
 * equal to the extended class type. Define only classes in the way 'T extends
 * AVectorND<T>'. I. e. omit something like this: 'S extends AVectorND<S> ... T
 * extends AVectorND<S>'. Due to a lack of the typesystem, the required equality
 * is not specifyable.
 * 
 * @author Friedemann
 *
 */
public abstract class AVectorND<T extends AVectorND<T>> implements IVectorFactory<T>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5071024763655354475L;

	private int dim;

	private final List<Double> values;

	private double length = -1;

	public AVectorND(AVectorND<T> vector) {
		this(vector.getValues().size(), vector.getValues());
	}

	/**
	 * creates the null vector.
	 * 
	 * @param dim
	 */
	@SuppressWarnings("serial")
	public AVectorND(final int dim) {

		this(new ArrayList<Double>(dim) {
			{
				for (int i = 0; i < dim; i++) {
					add(0.0);
				}
			}
		});
	}

	public AVectorND(final int dim, final Collection<Double> values) {
		this(new ArrayList<Double>(values));
		assertProp(dim == this.values.size());
	}

	/**
	 * carefull !!! for performance-reason no copy of coords will be made
	 * 
	 * @param values
	 */
	public AVectorND(final List<Double> values) {
		this.dim = values.size();
		this.values = values;

	}

	//
	// /**
	// * only for deserialization purpose!
	// *
	// * @param x
	// * @param y
	// * @param length
	// */
	// public Vector(double x, double y, double length) {
	// this(x, y);
	// this.length = length;
	// }

	public double length() {

		if (length == -1) {
			length = Math.sqrt(dotProduct((T) this, (T) this));
		}

		return length;
	}

	public T add(T vector) {
		return add(vector.getValues());
	}

	public T substract(T vector) {
		return substract(vector.getValues());
	}

	public T mult(double value) {
		return combine(value, (a, b) -> a * b);
	}

	public T negate() {
		return mult(-1);
	}

	public T div(double value) {
		return combine(value, (a, b) -> a / b);
	}

	private T combine(double value, BiFunction<Double, Double, Double> combinator) {

		for (int i = 0; i < dim; i++) {
			this.values.set(i, combinator.apply(this.values.get(i), value));
		}

		length = -1;

		return (T) this;
	}

	public T add(final List<Double> values) {
		return combine(values, (a, b) -> a + b);
	}

	public T substract(final List<Double> values) {
		return combine(values, (a, b) -> a - b);
	}

	protected T combine(final List<Double> values, BiFunction<Double, Double, Double> combinator) {

		assertProp(dim == values.size());

		for (int i = 0; i < dim; i++) {
			this.values.set(i, combinator.apply(this.values.get(i), values.get(i)));
		}

		length = -1;

		return (T) this;
	}

	public static <T extends AVectorND<T>> T mult(double val, T vector) {
		return vector.newVector(vector).mult(val);
	}

	public static <T extends AVectorND<T>> T negate(T vector) {
		return mult(-1, vector);
	}

	/**
	 * multiplication with a diagonal matrix.
	 * 
	 * @param dmvector
	 *            the diagonal-vector
	 * @param vector
	 * @return product
	 */
	public static <T extends AVectorND<T>> T diagmult(T dmvector, T vector) {
		return vector.newVector(vector).combine(dmvector.getValues(), (a, b) -> a * b);
	}

	public static <T extends AVectorND<T>> T add(T a, T b) {
		final T nvector = b.newVector(a);
		nvector.add(b);
		return nvector;
	}

	public static <T extends AVectorND<T>> T substract(T a, T b) {
		final T nvector = b.newVector(a);
		nvector.substract(b);
		return nvector;
	}

	/**
	 * multiplication of matrix and vector
	 * 
	 * @param matrix
	 *            the row-vectors of the matrix
	 * @param vector
	 * @return resultvector
	 */
	public static <T extends AVectorND<T>> T mmult(DMatrix<T> matrix, T vector) {

		assertProp(matrix.getRows() == vector.getDim());
		assertProp(matrix.getColumns() == vector.getDim());

		final List<Double> nvalues = new ArrayList<>(vector.getDim());
		for (int i = 0; i < matrix.getRows(); i++) {
			double sum = 0;
			for (int j = 0; j < matrix.getColumns(); j++) {
				sum += matrix.get(i, j) * vector.get(j);
			}
			nvalues.add(sum);
		}

		return vector.newVector(nvalues);
	}

	/**
	 * dotproduct
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T extends AVectorND<T>> double dotProduct(T a, T b) {

		assertProp(a.getDim() == b.getDim());

		double sum = 0.0;
		final Iterator<Double> it1 = a.getValues().iterator(), it2 = b.getValues().iterator();
		while (it1.hasNext()) {
			sum += it1.next() * it2.next();
		}
		return sum;
	}

	public boolean isNullVector() {
		return values.stream().allMatch(value -> value == 0.0);
	}

	public static <T extends AVectorND<T>> T normalize(T vector) {
		return (vector.isNullVector() ? vector.newVector() : mult(1 / vector.length(), vector));
	}

	/**
	 * returns the vector which corresponds to the maximum of the absolute
	 * values.
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static <T extends AVectorND<T>> T absmax(T v1, T v2) {
		return (v1.length() >= v2.length() ? v1 : v2);
	}

	public PolarND<T> toPolar() {

		final Double[] angles = new Double[getDim() - 1];
		final int dimAngles = getDim() - 1;
		final double radius = length();

		double sumSquares = 0;
		for (int i = 0; i < dimAngles; i++) {

			sumSquares += Math.pow(values.get(i), 2);
			angles[i] = Math.atan2(values.get(i + 1), i == 0 ? values.get(i) : Math.sqrt(sumSquares));

		}

		return new PolarND<>(radius, Arrays.asList(angles));

	}

	public static <T extends AVectorND<T>> double distance(T p1, T p2) {
		return substract(p2, p1).length();
	}

	public int getDim() {
		return dim;
	}

	public List<Double> getValues() {
		return values;
	}

	public Double get(int index) {
		return values.get(index);
	}

	public AVectorND<T> set(int index, double value) {
		values.set(index, value);
		return this;
	}

	public void extend(final double coord) {
		values.add(coord);
		dim++;
		length = -1;
	}

	private static void assertProp(final boolean value) {
		if (!value) {
			throw new IllegalArgumentException("dim not matching");
		}
	}

	@Override
	public String toString() {
		return "VectorND [dim=" + dim + ", values=" + values + "]";
	}

}
