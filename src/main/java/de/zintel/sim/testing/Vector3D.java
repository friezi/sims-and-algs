/**
 * 
 */
package de.zintel.sim.testing;

import java.util.Arrays;

import de.zintel.math.VectorND;

/**
 * @author friedemann.zintel
 *
 */
public class Vector3D extends VectorND {

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
		super(3);
	}

	public Vector3D(final double x, final double y, final double z) {
		super(3, Arrays.asList(x, y, z));
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

}
