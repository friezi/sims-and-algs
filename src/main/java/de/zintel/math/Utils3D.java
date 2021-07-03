/**
 * 
 */
package de.zintel.math;

import java.util.function.Function;

import de.zintel.utils.Pair;

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

	/**
	 * determines the intersection point of a line, defined by point a and point
	 * b, and a plane
	 * 
	 * @param a
	 * @param b
	 * @param plane
	 * @return
	 */
	public static Vector3D intersect(final Vector3D a, final Vector3D b, final Plane3D plane) {

		Vector3D diff = AVectorND.substract(b, a);
		final double div = AVectorND.dotProduct(diff, plane.getNormal());
		if (div == 0) {
			return null;
		}

		final double lambda = (plane.getPn() - AVectorND.dotProduct(a, plane.getNormal())) / div;

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

	/**
	 * calculates angle between a and b and rotation axis to rotate from a to b.
	 * 
	 * @param a
	 * @param b
	 * @return (NaN,0v) if a or ba are 0v, else (angle,axis)
	 */
	public static Pair<Double, Vector3D> angleAxis(final Vector3D a, final Vector3D b) {

		final double alen = a.length();
		final double blen = b.length();
		if (!MathUtils.inEpsilonRange(alen) || !MathUtils.inEpsilonRange(blen)) {
			return new Pair<>(Double.NaN, new Vector3D());
		}

		final Vector3D axis = Vector3D.crossProduct(b, a);
		final double dotproduct = Vector3D.dotProduct(b, a);
		final double divisor = Math.abs(blen) * Math.abs(alen);
		final double sine = axis.length() / divisor;
		final double cosine = dotproduct / divisor;
		final double angle = MathUtils.angle(sine, cosine);
		//
		// System.out.println("angle: " + (angle * 360) / (2 * Math.PI) + " b: "
		// + b + " blen: " + blen);

		return new Pair<>(angle, axis);

	}

}
