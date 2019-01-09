/**
 * 
 */
package de.zintel.math;

import de.zintel.math.transform.Rotator3D;

/**
 * @author friedemann.zintel
 *
 */
public class Plane3D {

	private Vector3D normal;

	private Vector3D positionvector;

	private double pn;

	public Plane3D(Vector3D normal, Vector3D positionvector) {
		this.normal = normal;
		this.positionvector = positionvector;
		this.pn = calculatePn(normal, positionvector);
	}

	/**
	 * @param normal
	 * @param positionvector
	 * @return
	 */
	public double calculatePn(Vector3D normal, Vector3D positionvector) {
		return VectorND.mult(positionvector, normal);
	}

	public Vector3D getNormal() {
		return normal;
	}

	public Vector3D getPositionvector() {
		return positionvector;
	}

	public double getPn() {
		return pn;
	}

	public void rotate(double angleX, double angleY, double angleZ) {

		normal = new Rotator3D(angleX, angleY, angleZ).apply(normal);
		pn = calculatePn(normal, positionvector);

	}

}
