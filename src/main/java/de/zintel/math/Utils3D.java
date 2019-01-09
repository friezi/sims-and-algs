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

		VectorND diff = VectorND.substract(b, a);
		final double div = VectorND.mult(diff, plane.getNormal());
		if (div == 0) {
			return null;
		}

		final double lambda = (plane.getPn() - VectorND.mult(a, plane.getNormal())) / div;

		return new Vector3D(VectorND.add(a, VectorND.mult(lambda, diff)));

	}

	public static Vector3D project(final Vector3D point, final Camera3D camera) {

		final Vector3D tpoint = camera.getCoordinateTransformation().transformPoint(point);
		// liegt bei z<0 hinter der Linse
		return tpoint.z() < 0 ? null : intersect(tpoint, camera.getViewpoint(), camera.getPlane());
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
