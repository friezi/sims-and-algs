package de.zintel.math;

import de.zintel.math.transform.CoordinateTransformation3D;

public interface ICamera3D {

	Vector3D getViewpoint();

	CoordinateTransformation3D getTransformationToScreen();

	void rotate(double angleX, double angleY, double angleZ);

	void translate(Vector3D vector);

	Vector3D project(Vector3D point);

	boolean inRange(final Vector3D point);
}