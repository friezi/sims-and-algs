/**
 * 
 */
package de.zintel.math;

import java.util.Arrays;
import java.util.List;

/**
 * @author friedemann.zintel
 *
 */
public class Vector2D extends AVectorND<Vector2D> {

	private static final int DIM = 2;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6604676797217529689L;

	private static final int X = 0;
	private static final int Y = 1;

	/**
	 * @param dim
	 */
	public Vector2D() {
		super(DIM);
	}

	public Vector2D(final double x, final double y) {
		super(DIM, Arrays.asList(x, y));
	}

	public Vector2D(AVectorND<Vector2D> vector) {
		this(vector.getValues());
	}

	public Vector2D(List<Double> values) {
		super(DIM, values);
	}

	public double x() {
		return get(X);
	}

	public double y() {
		return get(Y);
	}

	public Vector2D setX(final double value) {
		set(X, value);
		return this;
	}

	public Vector2D setY(final double value) {
		set(Y, value);
		return this;
	}

	@Override
	public Vector2D newVector(Vector2D vector) {
		return new Vector2D(vector);
	}

	@Override
	public Vector2D newVector() {
		return new Vector2D();
	}

	@Override
	public Vector2D newVector(List<Double> values) {
		return new Vector2D(values);
	}

}
