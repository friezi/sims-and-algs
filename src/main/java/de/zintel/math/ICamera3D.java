package de.zintel.math;

import de.zintel.math.transform.CoordinateTransformation3D;
import de.zintel.math.transform.CoordinateTransformation3DNew;

/**
 * An abstract camera. Attention: transformations are always relative to the
 * camera's coordinate system, e. g. axis is always axis of the camera
 * 
 * @author friedemann.zintel
 *
 */
public interface ICamera3D {

	Vector3D getViewpoint();

	CoordinateTransformation3DNew getTransformationToCamera();

	void rotate(Axis3D axis, double angle);

	void translate(Vector3D vector);

	Vector3D project(Vector3D point);

	boolean inRange(final Vector3D point);
}