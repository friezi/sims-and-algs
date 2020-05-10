package de.zintel.camera;

import de.zintel.math.Axis3D;
import de.zintel.math.Vector3D;
import de.zintel.math.transform.CoordinateTransformation3D;

/**
 * An abstract camera. Attention: transformations are always relative to the
 * camera's coordinate system, e. g. axis is always axis of the camera
 * 
 * @author friedemann.zintel
 *
 */
public interface ICamera3D {

	Vector3D getViewpoint();

	CoordinateTransformation3D getTransformationToCamera();

	void rotate(Axis3D axis, double angle);

	void translate(Vector3D vector);

	Vector3D project(Vector3D point);

	boolean inRange(final Vector3D point);
	
	void reset();

	void setCurvature(double value);

	double getCurvature();
}