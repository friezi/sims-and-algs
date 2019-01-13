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
public class Vector3D extends AVectorND<Vector3D> {

	private static final int DIM = 3;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6604676797217529689L;

	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;

	/**
	 * @param dim
	 */
	public Vector3D() {
		super(DIM);
	}

	public Vector3D(final double x, final double y, final double z) {
		super(DIM, Arrays.asList(x, y, z));
	}

	public Vector3D(AVectorND<Vector3D> vector) {
		this(vector.getValues());
	}

	public Vector3D(List<Double> values) {
		super(DIM, values);
	}

	public double x() {
		return get(X);
	}

	public double y() {
		return get(Y);
	}

	public double z() {
		return get(Z);
	}

	public Vector3D setX(final double value) {
		set(X, value);
		return this;
	}

	public Vector3D setY(final double value) {
		set(Y, value);
		return this;
	}

	public Vector3D setZ(final double value) {
		set(Z, value);
		return this;
	}

	@Override
	public Vector3D newVector(Vector3D vector) {
		return new Vector3D(vector);
	}

	@Override
	public Vector3D newVector() {
		return new Vector3D();
	}

	@Override
	public Vector3D newVector(List<Double> values) {
		return new Vector3D(values);
	}

}
