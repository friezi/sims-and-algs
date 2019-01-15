/**
 * 
 */
package de.zintel.math;

import java.awt.Dimension;

import de.zintel.math.transform.CoordinateTransformation3D;

/**
 * Viewpoint and plane are always relative according to the
 * coordinate-transformation
 * 
 * @author friedemann.zintel
 *
 */
public class PlaneCamera3D implements ICamera3D {

	private final Vector3D viewpoint;

	private final CoordinateTransformation3D transformationToScreen;

	private double curvature;

	private final Dimension screenDimension;

	private final Plane3D plane = new Plane3D(new Vector3D(0, 0, 1), new Vector3D(0, 0, 0));
	private final Vector3D middle;

	private final double maxDistance;

	public PlaneCamera3D(Vector3D viewpoint, CoordinateTransformation3D transformationToScreen, double curvature,
			Dimension screenDimension) {
		this.viewpoint = viewpoint;
		this.transformationToScreen = transformationToScreen.translateRotation(new Vector3D(viewpoint.x(), viewpoint.y(), viewpoint.z()));
		this.curvature = curvature;
		this.screenDimension = screenDimension;
		this.middle = new Vector3D((screenDimension.getWidth() - 1) / 2, (screenDimension.getHeight() - 1) / 2, 0);
		this.maxDistance = Vector3D.add(new Vector3D(screenDimension.getWidth() - 1, screenDimension.getHeight() - 1, 0), middle).length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.ICamera3D#getViewpoint()
	 */
	@Override
	public Vector3D getViewpoint() {
		return viewpoint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.ICamera3D#getTransformationToLens()
	 */
	@Override
	public CoordinateTransformation3D getTransformationToScreen() {
		return transformationToScreen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.ICamera3D#project(de.zintel.math.Vector3D)
	 */
	@Override
	public Vector3D project(final Vector3D point) {

		final Vector3D t_point = transformationToScreen.transformPoint(point);
		// liegt bei z<0 hinter der Linse
		Vector3D i_point = t_point.z() < 0 ? null : Utils3D.intersect(t_point, getViewpoint(), plane);
		if (i_point != null && inRange(i_point) && curvature > 0) {
			i_point = curve(i_point, curvature);
		}
		return i_point;

	}

	@Override
	public boolean inRange(final Vector3D point) {
		return point.x() >= 0 && point.x() < screenDimension.getWidth() && point.y() >= 0 && point.y() < screenDimension.getHeight();
	}

	private Vector3D curve(final Vector3D point, final double curvature) {

		final Vector3D direction = Vector3D.substract(point, middle);
		final double distance = direction.length();
		final double sin = Math.sin(MathUtils.morphRange(0, maxDistance, 0, Math.PI / 2, distance));

		return Vector3D.add(point, Vector3D.mult((curvature * Math.pow(sin, 2) * distance) / maxDistance, direction));

	}

	public double getCurvature() {
		return curvature;
	}

	public void setCurvature(double curvature) {
		this.curvature = curvature;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.ICamera3D#rotate(double, double, double)
	 */
	@Override
	public void rotate(double angleX, double angleY, double angleZ) {
		transformationToScreen.rotate(angleX, angleY, angleZ);
	}

	@Override
	public void translate(final Vector3D vector) {
		transformationToScreen.translate(vector).translateRotation(vector);
	}

}
