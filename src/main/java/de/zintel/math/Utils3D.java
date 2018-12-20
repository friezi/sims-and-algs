/**
 * 
 */
package de.zintel.math;

/**
 * @author friedemann.zintel
 *
 */
public class Utils3D {

	/**
	 * 
	 */
	private Utils3D() {
	}

	public static Vector3D intersect(final Vector3D a, final Vector3D b, final Plane3D plane) {

		VectorND diff = VectorND.substract(b, a);
		final double div = VectorND.mult(diff, plane.getNormal());
		if (div == 0) {
			return null;
		}

		final double lambda = (plane.getPn() - VectorND.mult(a, plane.getNormal())) / div;

		return new Vector3D(VectorND.add(a, VectorND.mult(lambda, diff)));

	}

}
