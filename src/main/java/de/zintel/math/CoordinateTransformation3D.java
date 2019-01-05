/**
 * 
 */
package de.zintel.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * caution: both coordinate-systems must e orthogonal systems.
 * 
 * @author friedemann.zintel
 *
 */
public class CoordinateTransformation3D {

	private static List<VectorND> unitymatrix = new ArrayList<VectorND>(
			Arrays.asList(new Vector3D(1, 0, 0), new Vector3D(0, 1, 0), new Vector3D(0, 0, 1)));

	private Vector3D translationvector = new Vector3D(0, 0, 0);

	private Vector3D scalevector = new Vector3D(1, 1, 1);

	private Vector3D scalevectorInverted = scalevector;

	private List<VectorND> rotationmatrixX = unitymatrix;

	private List<VectorND> rotationmatrixY = unitymatrix;

	private List<VectorND> rotationmatrixZ = unitymatrix;

	/**
	 * 
	 */
	public CoordinateTransformation3D() {
	}

	public Vector3D transformPoint(final Vector3D vector) {

		VectorND rotated = rotateVector(vector);
		VectorND translated = tranlateVector(rotated);
		VectorND scaled = scaleVector(translated);

		return new Vector3D(scaled);
	}

	public Vector3D transformVector(final Vector3D vector) {

		VectorND rotated = rotateVector(vector);
		VectorND scaled = scaleVector(rotated);

		return new Vector3D(scaled);
	}

	public CoordinateTransformation3D translate(Vector3D vector) {
		this.translationvector = vector;
		return this;
	}

	public CoordinateTransformation3D scale(Vector3D diagvector) {
		this.scalevector = diagvector;
		this.scalevectorInverted = new Vector3D(1 / scalevector.x(), 1 / scalevector.y(), 1 / scalevector.z());
		return this;
	}

	/**
	 * @param vector
	 * @return
	 */
	private VectorND tranlateVector(VectorND vector) {
		return VectorND.substract(vector, translationvector);
	}

	/**
	 * @param vector
	 * @return
	 */
	private VectorND scaleVector(VectorND vector) {
		return VectorND.diagmult(scalevectorInverted, vector);
	}

	private VectorND rotateVector(final VectorND vector) {

		final VectorND rotatedX = rotateVector(vector, rotationmatrixX);
		final VectorND rotatedXY = rotateVector(rotatedX, rotationmatrixY);
		final VectorND rotatedXYZ = rotateVector(rotatedXY, rotationmatrixZ);

		return rotatedXYZ;

	}

	private VectorND rotateVector(final VectorND vector, final List<VectorND> rotationmatrix) {
		return VectorND.mmult(rotationmatrix, vector);
	}

}
