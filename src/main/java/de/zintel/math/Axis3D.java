/**
 * 
 */
package de.zintel.math;

/**
 * @author friedemann.zintel
 *
 */
public class Axis3D {

	private final Vector3D p1;

	private final Vector3D p2;

	public Axis3D(Vector3D p1, Vector3D p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public Vector3D getP1() {
		return p1;
	}

	public Vector3D getP2() {
		return p2;
	}

	@Override
	public String toString() {
		return "Axis3D [p1=" + p1 + ", p2=" + p2 + "]";
	}

}
