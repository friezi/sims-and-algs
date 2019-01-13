/**
 * 
 */
package de.zintel.math;

import java.util.function.Function;

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

		Vector3D diff = AVectorND.substract(b, a);
		final double div = AVectorND.mult(diff, plane.getNormal());
		if (div == 0) {
			return null;
		}

		final double lambda = (plane.getPn() - AVectorND.mult(a, plane.getNormal())) / div;

		return Vector3D.add(a, Vector3D.mult(lambda, diff));

	}

	// obsolete:
	private double projectX(final double x, final double z, final Vector3D vp) {
		return projectXY(x, z, vp, Vector3D::x);
	}

	private double projectY(final double y, final double z, final Vector3D vp) {
		return projectXY(y, z, vp, Vector3D::y);
	}

	private double projectXY(final double xy, final double z, final Vector3D vp, Function<Vector3D, Double> vpxy) {

		double diffZ = vp.z() - z;
		if (diffZ == 0) {
			return 0;
		}
		return (xy * vp.z() - z * vpxy.apply(vp)) / diffZ;

	}

}
