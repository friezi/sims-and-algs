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

	private Rotator3D inverseRotator = null;

	private Scaler3D scaler = null;

	private Scaler3D inverseScaler = null;

	private Translator3D translator = null;

	private Translator3D inverseTranslator = null;

	/**
	 * 
	 */
	public CoordinateTransformation3D() {
	}

	public Vector3D transformPoint(final Vector3D vector) {
		return scaleVector(translateVector(rotateVector(vector)));
	}

	public Vector3D inverseTransformPoint(final Vector3D vector) {
		return inverseRotateVector(inverseTranslateVector(inverseScaleVector(vector)));
	}

	public Vector3D transformVector(final Vector3D vector) {
		return scaleVector(rotateVector(vector));
	}

	public Vector3D inverseTransformVector(final Vector3D vector) {
		return inverseRotateVector(inverseScaleVector(vector));
	}

	public CoordinateTransformation3D setTranslation(Vector3D vector) {
		this.translator = new Translator3D(new Vector3D(-vector.x(), -vector.y(), -vector.z()));
		this.inverseTranslator = new Translator3D(vector);
		return this;
	}

	public CoordinateTransformation3D setScaling(Vector3D diagvector) {
		this.scaler = new Scaler3D(new Vector3D(1 / diagvector.x(), 1 / diagvector.y(), 1 / diagvector.z()));
		this.inverseScaler = new Scaler3D(diagvector);
		return this;
	}

	public CoordinateTransformation3D setRotation(final double angleX, final double angleY, final double angleZ) {
		this.rotator = new Rotator3D(-angleX, -angleY, -angleZ);
		this.inverseRotator = this.rotator.getInvertedRotator();
		return this;
	}

	public CoordinateTransformation3D rotate(final double angleX, final double angleY, final double angleZ) {

		if (rotator == null) {
			rotator = new Rotator3D(0, 0, 0);
		}

		this.rotator.add(-angleX, -angleY, -angleZ);
		this.inverseRotator = this.rotator.getInvertedRotator();
		return this;
	}

	/**
	 * @param vector
	 *            in original coordinate system
	 * @return
	 */
	public Vector3D translateVector(Vector3D vector) {
		return (translator == null ? vector : translator.apply(vector));
	}

	/**
	 * @param vector
	 *            in this coordinate system
	 * @return
	 */
	public Vector3D inverseTranslateVector(Vector3D vector) {
		return (inverseTranslator == null ? vector : inverseTranslator.apply(vector));
	}

	/**
	 * @param vector
	 *            in original coordinate system
	 * @return
	 */
	public Vector3D scaleVector(Vector3D vector) {
		return (scaler == null ? vector : scaler.apply(vector));
	}

	/**
	 * @param vector
	 *            in this coordinate system
	 * @return
	 */
	public Vector3D inverseScaleVector(Vector3D vector) {
		return (inverseScaler == null ? vector : inverseScaler.apply(vector));
	}

	/**
	 * @param vector
	 *            in original coordinate system
	 * @return
	 */
	public Vector3D rotateVector(final Vector3D vector) {
		return (rotator == null ? vector : rotator.apply(vector));
	}

	/**
	 * @param vector
	 *            in this coordinate system
	 * @return
	 */
	public Vector3D inverseRotateVector(final Vector3D vector) {
		return (inverseRotator == null ? vector : inverseRotator.apply(vector));
	}

}
