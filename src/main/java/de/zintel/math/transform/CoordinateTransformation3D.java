/**
 * 
 */
package de.zintel.math.transform;

import de.zintel.math.Vector3D;

/**
 * caution: both coordinate-systems are expected to be orthogonal systems.
 * 
 * @author friedemann.zintel
 *
 */
public class CoordinateTransformation3D {

	private Rotator3D rotator = null;

	private Scaler3D scaler = null;

	private Translator3D translator = null;

	/**
	 * 
	 */
	public CoordinateTransformation3D() {
	}

	public Vector3D transformPoint(final Vector3D vector) {
		return scaleVector(translateVector(rotateVector(vector)));
	}

	public Vector3D transformVector(final Vector3D vector) {
		return scaleVector(rotateVector(vector));
	}

	public CoordinateTransformation3D translate(Vector3D vector) {
		this.translator = new Translator3D(new Vector3D(-vector.x(), -vector.y(), -vector.z()));
		return this;
	}

	public CoordinateTransformation3D scale(Vector3D diagvector) {
		this.scaler = new Scaler3D(new Vector3D(1 / diagvector.x(), 1 / diagvector.y(), 1 / diagvector.z()));
		return this;
	}

	public CoordinateTransformation3D rotate(final double angleX, final double angleY, final double angleZ) {
		this.rotator = new Rotator3D(-angleX, -angleY, -angleZ);
		return this;
	}

	/**
	 * @param vector
	 * @return
	 */
	private Vector3D translateVector(Vector3D vector) {
		return (translator == null ? vector : translator.apply(vector));
	}

	/**
	 * @param vector
	 * @return
	 */
	private Vector3D scaleVector(Vector3D vector) {
		return (scaler == null ? vector : scaler.apply(vector));
	}

	private Vector3D rotateVector(final Vector3D vector) {
		return (rotator == null ? vector : rotator.apply(vector));
	}

}
