/**
 * 
 */
package de.zintel.math.transform;

import de.zintel.math.Axis3D;
import de.zintel.math.Vector3D;
import de.zintel.math.matrix.DMatrix;

/**
 * rotation happens always in positive direction from top to bottom, i. e.
 * x->y->z. It starts at positive top element and rotates in direction of
 * positive bottom-element. So, f. i. to rotate around the y-axis, you start at
 * x-axis in positive direction and rotate in positive z-direction.
 * 
 * @author friedemann.zintel
 *
 */
public class Transformer3D {

	private final DMatrix<Vector3D> initialRotationMatrix;

	private final Vector3D initialTranslationVector;

	private DMatrix<Vector3D> rotationMatrix;

	private Vector3D translationVector;

	/**
	 * 
	 */
	public Transformer3D() {

		initialRotationMatrix = Utils3D.IDENTITY_MATRIX;
		initialTranslationVector = new Vector3D();

		reset();
	}

	private Transformer3D(DMatrix<Vector3D> initialRotationMatrix, Vector3D initialTranslationVector) {

		this.initialRotationMatrix = initialRotationMatrix;
		this.initialTranslationVector = initialTranslationVector;

		reset();

	}

	public void reset() {

		rotationMatrix = initialRotationMatrix.copy();
		translationVector = new Vector3D(initialTranslationVector);

	}

	public Vector3D transformPoint(final Vector3D point) {
		return Vector3D.add(Vector3D.mmult(rotationMatrix, point), translationVector);
	}

	public Vector3D transformVector(final Vector3D vector) {
		return Vector3D.mmult(rotationMatrix, vector);
	}

	public Transformer3D addTranslation(final Vector3D tv) {
		translationVector.add(tv);
		return this;
	}

	public Transformer3D addRotation(final Axis3D axis, final double angle) {

		final Vector3D axisVector = Vector3D.substract(axis.getP2(), axis.getP1());
		final double avLengthXZ = new Vector3D(axisVector.x(), 0, axisVector.z()).length();

		final DMatrix<Vector3D> rotY = avLengthXZ == 0 ? Utils3D.IDENTITY_MATRIX
				: RotationMatrix3DProvider.getRmY(new TrigonomFnProviderDirect(axisVector.x() / avLengthXZ, axisVector.z() / avLengthXZ));
		final DMatrix<Vector3D> rotX = RotationMatrix3DProvider
				.getRmX(new TrigonomFnProviderDirect(axisVector.y() / axisVector.length(), avLengthXZ / axisVector.length()));
		final DMatrix<Vector3D> rotZ = RotationMatrix3DProvider.getRmZ(new TrigonomFnProviderAngle(angle));

		final DMatrix<Vector3D> rg = DMatrix.mmult(rotY.transpose(), DMatrix.mmult(rotX.transpose(), DMatrix.mmult(rotZ, DMatrix.mmult(rotX, rotY))));

		// V'=R_A*R*V+R_A(T-T_A)+T_A --> R_A: combined rotation by axis T_A:
		// translation of axis
		rotationMatrix = DMatrix.mmult(rg, rotationMatrix);
		translationVector = Vector3D.add(Vector3D.mmult(rg, Vector3D.substract(translationVector, axis.getP1())), axis.getP1());

		return this;
	}

	public Transformer3D inverse() {
		final DMatrix<Vector3D> rmT = rotationMatrix.transpose();
		// V=RT*V'-RT*T
		return new Transformer3D(rmT, Vector3D.mult(-1, Vector3D.mmult(rmT, translationVector)));
	}

}
