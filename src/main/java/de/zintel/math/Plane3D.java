/**
 * 
 */
package de.zintel.math;

/**
 * @author friedemann.zintel
 *
 */
public class Plane3D {

	private final Vector3D normal;

	private final Vector3D positionvector;

	private final double pn;

	public Plane3D(Vector3D normal, Vector3D positionvector) {
		this.normal = normal;
		this.positionvector = positionvector;
		this.pn = VectorND.mult(positionvector, normal);
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

}
