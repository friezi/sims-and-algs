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

/**
 * @author Friedemann
 *
 */
public class VectorND implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5071024763655354475L;

	private int dim;

	private final List<Double> coords;

	private double length = -1;

	public VectorND(VectorND vector) {
		this(vector.getCoords());
	}

	public VectorND(Collection<Double> coords) {
		this(coords.size(), coords);
	}

	/**
	 * creates the null vector.
	 * 
	 * @param dim
	 */
	@SuppressWarnings("serial")
	public VectorND(final int dim) {

		this(new ArrayList<Double>(dim) {
			{
				for (int i = 0; i < dim; i++) {
					add(0.0);
				}
			}
		});
	}

	public VectorND(final int dim, final Collection<Double> coords) {
		this(new ArrayList<Double>(coords));
		assertProp(dim == this.coords.size());
	}

	/**
	 * carefull !!! for performance-reason no copy of coords will be made
	 * 
	 * @param coords
	 */
	public VectorND(final List<Double> coords) {
		this.dim = coords.size();
		this.coords = coords;

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
			length = Math.sqrt(mult(this, this));
		}

		return length;
	}

	public VectorND add(VectorND vector) {
		return add(vector.getCoords());
	}

	public VectorND substract(VectorND vector) {
		return substract(vector.getCoords());
	}

	public VectorND mult(double value) {
		return combine(value, (a, b) -> a * b);
	}

	public VectorND div(double value) {
		return combine(value, (a, b) -> a / b);
	}

	private VectorND combine(double value, BiFunction<Double, Double, Double> combinator) {

		for (int i = 0; i < dim; i++) {
			this.coords.set(i, combinator.apply(this.coords.get(i), value));
		}

		length = -1;

		return this;
	}

	public VectorND add(final List<Double> coords) {
		return combine(coords, (a, b) -> a + b);
	}

	public VectorND substract(final List<Double> coords) {
		return combine(coords, (a, b) -> a - b);
	}

	private VectorND combine(final List<Double> coords, BiFunction<Double, Double, Double> combinator) {

		assertProp(dim == coords.size());

		for (int i = 0; i < dim; i++) {
			this.coords.set(i, combinator.apply(this.coords.get(i), coords.get(i)));
		}

		length = -1;

		return this;
	}

	public static VectorND mult(double val, VectorND vector) {
		return new VectorND(vector).mult(val);
	}

	/**
	 * multiplication with a diagonal matrix.
	 * 
	 * @param dmvector
	 *            the diagonal-vector
	 * @param vector
	 * @return product
	 */
	public static VectorND diagmult(VectorND dmvector, VectorND vector) {
		return new VectorND(vector).combine(dmvector.getCoords(), (a, b) -> a * b);
	}

	public static VectorND add(VectorND a, VectorND b) {
		final VectorND nvector = new VectorND(a);
		nvector.add(b);
		return nvector;
	}

	public static VectorND substract(VectorND a, VectorND b) {
		final VectorND nvector = new VectorND(a);
		nvector.substract(b);
		return nvector;
	}

	/**
	 * multiplication of matrix and vector
	 * 
	 * @param rowmatrix
	 *            the row-vectors of the matrix
	 * @param vector
	 * @return resultvector
	 */
	public static VectorND mmult(List<VectorND> rowmatrix, VectorND vector) {

		assertProp(rowmatrix.size() == vector.getDim());
		final List<Double> nvalues = new ArrayList<>(vector.getDim());
		for (int i = 0; i < rowmatrix.size(); i++) {
			nvalues.add(mult(rowmatrix.get(i), vector));
		}

		return new VectorND(nvalues);
	}

	/**
	 * dotproduct
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double mult(VectorND a, VectorND b) {

		assertProp(a.getDim() == b.getDim());

		double sum = 0.0;
		final Iterator<Double> it1 = a.getCoords().iterator(), it2 = b.getCoords().iterator();
		while (it1.hasNext()) {
			sum += it1.next() * it2.next();
		}
		return sum;
	}

	public boolean isNullVector() {
		return coords.stream().allMatch(value -> value == 0.0);
	}

	public static VectorND normalize(VectorND vector) {
		return (vector.isNullVector() ? new VectorND(vector.getDim()) : mult(1 / vector.length(), vector));
	}

	/**
	 * returns the vector which corresponds to the maximum of the absolute
	 * values.
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static VectorND absmax(VectorND v1, VectorND v2) {
		return (v1.length() >= v2.length() ? v1 : v2);
	}

	public PolarND toPolar() {

		final Double[] angles = new Double[getDim() - 1];
		final int dimAngles = getDim() - 1;
		final double radius = length();

		double sumSquares = 0;
		for (int i = 0; i < dimAngles; i++) {

			sumSquares += Math.pow(coords.get(i), 2);
			angles[i] = Math.atan2(coords.get(i + 1), i == 0 ? coords.get(i) : Math.sqrt(sumSquares));

		}

		return new PolarND(radius, Arrays.asList(angles));

	}

	public static double distance(VectorND p1, VectorND p2) {
		return substract(p2, p1).length();
	}

	public int getDim() {
		return dim;
	}

	public List<Double> getCoords() {
		return coords;
	}

	public Double get(int index) {
		return coords.get(index);
	}

	public VectorND set(int index, double value) {
		coords.set(index, value);
		return this;
	}

	public void extend(final double coord) {
		coords.add(coord);
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
		return "VectorND [dim=" + dim + ", coords=" + coords + "]";
	}

}
