/**
 * 
 */
package de.zintel.math;

import java.io.Serializable;
import java.util.ArrayList;
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

		this(dim, new ArrayList<Double>(dim) {
			{
				for (int i = 0; i < dim; i++) {
					add(0.0);
				}
			}
		}, true);
	}

	public VectorND(final int dim, final Collection<Double> coords) {
		this(dim, new ArrayList<Double>(coords), true);
	}

	/**
	 * carefull !!! for performance-reason no copy of coords will be made
	 * 
	 * @param dim
	 * @param coords
	 * @param nix
	 *            just to provide different signature from other constructor
	 */
	public VectorND(final int dim, final List<Double> coords, final boolean nix) {
		this.dim = dim;
		this.coords = coords;
		assertProp(dim == this.coords.size());

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

		assertProp(dim == coords.size());

		for (int i = 0; i < dim; i++) {
			this.coords.set(i, this.coords.get(i) * value);
		}

		length = -1;

		return this;
	}

	public VectorND add(final List<Double> coords) {
		combine(coords, (a, b) -> a + b);
		return this;
	}

	public VectorND substract(final List<Double> coords) {
		combine(coords, (a, b) -> a - b);
		return this;
	}

	private void combine(final List<Double> coords, BiFunction<Double, Double, Double> combinator) {

		assertProp(dim == coords.size());

		for (int i = 0; i < dim; i++) {
			this.coords.set(i, combinator.apply(this.coords.get(i), coords.get(i)));
		}

		length = -1;
	}

	public static VectorND mult(double val, VectorND vector) {
		final VectorND nvector = new VectorND(vector);
		nvector.mult(val);
		return nvector;
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

	public static VectorND max(VectorND v1, VectorND v2) {
		return (v1.length() >= v2.length() ? v1 : v2);
	}
	//
	// public Polar toPolar() {
	// return new Polar(length(), x != 0 ? Math.acos(x / length()) : Math.asin(y
	// / length()));
	// }
	//
	// @Override
	// public String toString() {
	// return "Vector2D [x=" + x + ", y=" + y + "]";
	// }

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
