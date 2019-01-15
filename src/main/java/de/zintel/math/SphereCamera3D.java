/**
 * 
 */
package de.zintel.math;

import java.awt.Dimension;

import de.zintel.math.transform.CoordinateTransformation3D;

/**
 * unfortunately this class is not really practical, because it would need a
 * very huge sphere for a senseful curvature.
 * 
 * The center of the sphere is the origin of the coordinate system. The z-axis
 * points into screen direction.
 * 
 * @author friedemann.zintel
 *
 */
public class SphereCamera3D implements ICamera3D {

	private final Vector3D viewpoint;

	private final CoordinateTransformation3D transformationToScreen;

	private final Dimension screenDimension;

	private final Vector3D sphereCenter = new Vector3D(0, 0, 0);

	private double radius;

	private double d_anglex;

	private double d_angley;

	private CoordinateTransformation3D screenToLens;

	public SphereCamera3D(Vector3D viewpoint, CoordinateTransformation3D transformationToScreen, double radius, Dimension screenDimension) {

		this.transformationToScreen = transformationToScreen;
		this.screenDimension = screenDimension;

		reinit(radius);

		this.viewpoint = screenToLens.transformPoint(viewpoint);

	}

	private void reinit(double sradius) {

		this.radius = Math.abs(sradius);

		this.d_anglex = (screenDimension.getWidth() - 1) / (2 * this.radius);
		this.d_angley = (screenDimension.getHeight() - 1) / (2 * this.radius);

		this.screenToLens = new CoordinateTransformation3D()
				.setTranslation(new Vector3D((screenDimension.getWidth() - 1) / 2, (screenDimension.getHeight() - 1) / 2, this.radius))
				.setScaling(new Vector3D(1, -1, -1));

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

	@Override
	public void translate(Vector3D vector) {
		transformationToScreen.translate(vector);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.ICamera3D#project(de.zintel.math.Vector3D)
	 */
	@Override
	public Vector3D project(Vector3D point) {

		if (radius == 0) {
			return null;
		}

		final Vector3D t_point = screenToLens.transformPoint(transformationToScreen.transformPoint(point));

		if (t_point.z() >= 0 && t_point.length() > radius) {
			return null;
		}

		final Vector3D s_point = intersectWithSphere(t_point);
		if (s_point == null) {
			return null;
		}

		final double alphax = angle(s_point.x());
		final double alphay = angle(s_point.y());

		final double x = MathUtils.morphRange(-d_anglex, d_anglex, 0, screenDimension.getWidth() - 1, alphax);
		final double y = MathUtils.morphRange(-d_angley, d_angley, screenDimension.getHeight() - 1, 0, alphay);

		return new Vector3D(x, y, 0);
	}

	private final Vector3D intersectWithSphere(final Vector3D point) {

		final Vector3D vp = AVectorND.substract(viewpoint, point);
		final double vpLength = vp.length();

		if (vpLength == 0) {
			return null;
		}

		final Vector3D l = AVectorND.mult(1.0 / vpLength, vp);
		final double lpc = AVectorND.mult(l, AVectorND.substract(point, sphereCenter));
		final double root = Math.sqrt(Math.pow(lpc, 2) + 2 * Vector3D.mult(point, sphereCenter) + Math.pow(radius, 2)
				- Math.pow(point.length(), 2) - Math.pow(sphereCenter.length(), 2));

		final double lambda1 = -lpc + root;
		final double lambda2 = -lpc - root;

		final Vector3D p1 = Vector3D.add(point, Vector3D.mult(lambda1 / vpLength, vp));
		final Vector3D p2 = Vector3D.add(point, Vector3D.mult(lambda2 / vpLength, vp));

		return nearest(p1, p2);

	}

	private Vector3D nearest(final Vector3D a, final Vector3D b) {
		return (a.z() >= b.z() ? a : b);
	}

	private double angle(final double value) {
		return Math.signum(value) * Math.acos(Math.abs(value) / radius);
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		reinit(radius);
	}

	@Override
	public boolean inRange(final Vector3D point) {
		return point.x() >= 0 && point.x() < screenDimension.getWidth() && point.y() >= 0 && point.y() < screenDimension.getHeight();
	}

}
