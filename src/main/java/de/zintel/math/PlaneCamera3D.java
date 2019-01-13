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

	public PlaneCamera3D(Vector3D viewpoint, CoordinateTransformation3D transformationToScreen, double curvature, Dimension screenDimension) {
		this.viewpoint = viewpoint;
		this.transformationToScreen = transformationToScreen;
		this.curvature = curvature;
		this.screenDimension = screenDimension;
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
	 * @see de.zintel.math.ICamera3D#rotate(double, double, double)
	 */
	@Override
	public void rotate(double angleX, double angleY, double angleZ) {
		transformationToScreen.rotate(angleX, angleY, angleZ);
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
			i_point = new Vector3D(curve(screenDimension.getWidth() - 1, i_point.x(), curvature),
					curve(screenDimension.getHeight() - 1, i_point.y(), curvature), i_point.z());
		}
		return i_point;

	}

	@Override
	public boolean inRange(final Vector3D point) {
		return point.x() >= 0 && point.x() < screenDimension.getWidth() && point.y() >= 0 && point.y() < screenDimension.getHeight();
	}

	private double curve(final double max, final double value, final double curvature) {

		final double m = max / 2;
		final double dx = value - m;
		final double adx = Math.abs(dx);
		return m + MathUtils.morphRange(0, m, 1, 1 + Math.sin(MathUtils.morphRange(0, m, 0, Math.PI / 2, adx)) * curvature, adx) * dx;

	}

	public double getCurvature() {
		return curvature;
	}

	public void setCurvature(double curvature) {
		this.curvature = curvature;
	}

}
