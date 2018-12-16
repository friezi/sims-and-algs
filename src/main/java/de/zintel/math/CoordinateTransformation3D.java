/**
 * 
 */
package de.zintel.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author friedemann.zintel
 *
 */
public class CoordinateTransformation3D {

	private static List<VectorND> unitymatrix = new ArrayList<VectorND>(
			Arrays.asList(new Vector3D(1, 0, 0), new Vector3D(0, 1, 0), new Vector3D(0, 0, 1)));

	private VectorND translationvector = new Vector3D(0, 0, 0);

	private VectorND scalevector = new Vector3D(1, 1, 1);

	private List<VectorND> rotationmatrixX = unitymatrix;

	private VectorND rotationaxisvectorX = new Vector3D();

	private List<VectorND> rotationmatrixY = unitymatrix;

	private VectorND rotationaxisvectorY = new Vector3D();

	private List<VectorND> rotationmatrixZ = unitymatrix;

	private VectorND rotationaxisvectorZ = new Vector3D();

	/**
	 * 
	 */
	public CoordinateTransformation3D() {
	}

	public Vector3D transform(final Vector3D vector) {

		VectorND rotX = rotate(vector, rotationmatrixX, rotationaxisvectorX);
		VectorND rotY = rotate(rotX, rotationmatrixY, rotationaxisvectorY);
		VectorND rotZ = rotate(rotY, rotationmatrixZ, rotationaxisvectorZ);
		VectorND trans = VectorND.substract(rotZ, translationvector);
		VectorND scale = VectorND.diagmult(scalevector, trans);

		return new Vector3D(scale);
	}

	public CoordinateTransformation3D translate(Vector3D vector) {
		this.translationvector = vector;
		return this;
	}

	public CoordinateTransformation3D scale(Vector3D diagvector) {
		this.scalevector = diagvector;
		return this;
	}

	private VectorND rotate(final VectorND vector, final List<VectorND> rotationmatrix, final VectorND translationvector) {
		return VectorND.add(VectorND.mmult(rotationmatrix, VectorND.substract(vector, translationvector)), translationvector);
	}

}
