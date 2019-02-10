/**
 * 
 */
package de.zintel.math.transform;

import de.zintel.math.Axis3D;
import de.zintel.math.Vector3D;

/**
 * caution: both coordinate-systems are expected to be orthogonal systems.
 * 
 * @author friedemann.zintel
 *
 */
public class CoordinateTransformation3DNew {

	private Transformer3D transformer;

	private Transformer3D inverseTransformer;

	private Scaler3D scaler = null;

	private Scaler3D inverseScaler = null;
	//
	// private Translator3D axisTranslator = null;
	//
	// private Translator3D inverseAxisTranslator = null;

	/**
	 * 
	 */
	public CoordinateTransformation3DNew() {

		transformer = new Transformer3D();
		inverseTransformer = transformer.inverse();

	}

	public Vector3D transformPoint(final Vector3D vector) {
		return scaleVector(transformer.transformPoint(vector));
	}

	public Vector3D inverseTransformPoint(final Vector3D vector) {
		return inverseTransformer.transformPoint(inverseScaleVector(vector));
	}

	public Vector3D transformVector(final Vector3D vector) {
		return scaleVector(transformer.transformVector(vector));
	}

	public Vector3D inverseTransformVector(final Vector3D vector) {
		return inverseTransformer.transformVector(inverseScaleVector(vector));
	}

	public CoordinateTransformation3DNew setTransformation(final Axis3D axis, final double angle, final Vector3D translationVector) {

		transformer = new Transformer3D();
		transformer.addRotation(axis, -angle);
		transformer.addTranslation(Vector3D.mult(-1, translationVector));
		inverseTransformer = transformer.inverse();
		return this;

	}

	public CoordinateTransformation3DNew setScaling(Vector3D diagvector) {
		this.scaler = new Scaler3D(new Vector3D(1 / diagvector.x(), 1 / diagvector.y(), 1 / diagvector.z()));
		this.inverseScaler = new Scaler3D(diagvector);
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
	public CoordinateTransformation3DNew rotate(final Axis3D axis, final double angle) {

		transformer.addRotation(axis, -angle);
		inverseTransformer = transformer.inverse();
		return this;
	}
//
//	public CoordinateTransformation3DNew translateRotation(final Vector3D vector) {
//
//		if (axisTranslator == null) {
//			axisTranslator = new Translator3D();
//		}
//
//		this.axisTranslator.add(Vector3D.mult(-1, vector));
//		this.inverseAxisTranslator = this.axisTranslator.getInvertedTranslator();
//		return this;
//	}

	public CoordinateTransformation3DNew translate(final Vector3D vector) {

		transformer.addTranslation(Vector3D.mult(-1, vector));
		inverseTransformer = transformer.inverse();
		return this;
	}
//
//	/**
//	 * @param vector
//	 *            in original coordinate system
//	 * @return
//	 */
//	public Vector3D translateAxis(Vector3D vector) {
//		return (axisTranslator == null ? vector : axisTranslator.apply(vector));
//	}
//
//	/**
//	 * @param vector
//	 *            in original coordinate system
//	 * @return
//	 */
//	public Vector3D inverseTranslateAxis(Vector3D vector) {
//		return (inverseAxisTranslator == null ? vector : inverseAxisTranslator.apply(vector));
//	}

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

}
