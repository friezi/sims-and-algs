/**
 * 
 */
package de.zintel.math;

import de.zintel.math.transform.CoordinateTransformation3D;
import de.zintel.math.transform.Rotator3D;

/**
 * Viewpoint and plane are always relativ according to the
 * coordinate-transformation
 * 
 * @author friedemann.zintel
 *
 */
public class Camera3D {

	private Vector3D viewpoint;

	private Plane3D plane;

	private CoordinateTransformation3D coordinateTransformation;

	public Camera3D(Vector3D viewpoint, Plane3D plane, CoordinateTransformation3D coordinateTransformation) {
		this.viewpoint = viewpoint;
		this.plane = plane;
		this.coordinateTransformation = coordinateTransformation;
	}

	public Vector3D getViewpoint() {
		return viewpoint;
	}

	public Plane3D getPlane() {
		return plane;
	}

	public CoordinateTransformation3D getCoordinateTransformation() {
		return coordinateTransformation;
	}

	public void rotate(double angleX, double angleY, double angleZ) {
		coordinateTransformation.rotate(angleX, angleY, angleZ);
	}

}
