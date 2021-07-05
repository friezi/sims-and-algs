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
public class CoordinateTransformation3D {

	private Transformer3D transformer;

	private Transformer3D inverseTransformer;

	private Scaler3D scaler = null;

	private Scaler3D inverseScaler = null;

	/**
	 * 
	 */
	public CoordinateTransformation3D() {
		this(new Transformer3D(), null);
	}

	private CoordinateTransformation3D(Transformer3D transformer, Scaler3D scaler) {

		this.transformer = transformer;
		this.inverseTransformer = transformer.inverse();
		this.scaler = scaler;
		this.inverseScaler = (scaler != null ? scaler.inverse() : null);
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

	public CoordinateTransformation3D setScaling(Vector3D diagvector) {
		inverseScaler = new Scaler3D(diagvector);
		scaler = inverseScaler.inverse();
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
	public CoordinateTransformation3D rotate(final Axis3D axis, final double angle) {

		transformer.addRotation(axis, -angle);
		inverseTransformer = transformer.inverse();
		return this;
	}

	public CoordinateTransformation3D translate(final Vector3D vector) {

		transformer.addTranslation(Vector3D.mult(-1, vector));
		inverseTransformer = transformer.inverse();
		return this;
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

	public CoordinateTransformation3D reset() {

		transformer.reset();
		inverseTransformer = transformer.inverse();

		return this;
	}

	public CoordinateTransformation3D snapshot() {
		return new CoordinateTransformation3D(transformer.snapshot(), scaler == null ? null : scaler.snapshot());
	}

	public CoordinateTransformation3D inverse() {
		return new CoordinateTransformation3D(transformer.inverse(), scaler == null ? null : scaler.inverse());
	}

	public CoordinateTransformation3D cat(CoordinateTransformation3D ct) {
		final Transformer3D tf = transformer.cat(ct.transformer);
		final Scaler3D sc = (scaler == null && ct.scaler == null ? null
				: scaler == null ? ct.scaler.snapshot() : ct.scaler == null ? scaler.snapshot() : scaler.cat(ct.scaler));
		return new CoordinateTransformation3D(tf, sc);
	}

}
