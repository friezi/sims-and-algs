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

	private Translator3D axisTranslator = null;

	private Translator3D inverseAxisTranslator = null;

	/**
	 * 
	 */
	public CoordinateTransformation3D() {
	}

	public Vector3D transformPoint(final Vector3D vector) {
		return scaleVector(translateVector(inverseTranslateAxis(rotateVector(translateAxis(vector)))));
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

	/**
	 * rorates the coordinate system.
	 * 
	 * @param angleX
	 * @param angleY
	 * @param angleZ
	 * @return
	 */
	public CoordinateTransformation3D rotate(final double angleX, final double angleY, final double angleZ) {

		if (rotator == null) {
			rotator = new Rotator3D(0, 0, 0);
		}

		this.rotator.addAngles(-angleX, -angleY, -angleZ);
		this.inverseRotator = this.rotator.getInvertedRotator();
		return this;
	}

	public CoordinateTransformation3D translateRotation(final Vector3D vector) {

		if (axisTranslator == null) {
			axisTranslator = new Translator3D();
		}

		this.axisTranslator.add(Vector3D.mult(-1, vector));
		this.inverseAxisTranslator = this.axisTranslator.getInvertedTranslator();
		return this;
	}

	public CoordinateTransformation3D translate(final Vector3D vector) {

		if (translator == null) {
			translator = new Translator3D(new Vector3D());
		}

		this.translator.add(Vector3D.mult(-1, vector));
		this.inverseTranslator = this.translator.getInvertedTranslator();
		return this;
	}

	/**
	 * @param vector
	 *            in original coordinate system
	 * @return
	 */
	private Vector3D translateVector(Vector3D vector) {
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
	public Vector3D translateAxis(Vector3D vector) {
		return (axisTranslator == null ? vector : axisTranslator.apply(vector));
	}

	/**
	 * @param vector
	 *            in original coordinate system
	 * @return
	 */
	public Vector3D inverseTranslateAxis(Vector3D vector) {
		return (inverseAxisTranslator == null ? vector : inverseAxisTranslator.apply(vector));
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
