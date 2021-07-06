/**
 * 
 */
package de.zintel.camera;

import java.awt.Dimension;

import de.zintel.math.Axis3D;
import de.zintel.math.MathUtils;
import de.zintel.math.Plane3D;
import de.zintel.math.Utils3D;
import de.zintel.math.Vector3D;
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

	private final CoordinateTransformation3D transformationToCamera;

	private double curvature;

	private final Dimension screenDimension;

	private final Plane3D plane = new Plane3D(new Vector3D(0, 0, 1), new Vector3D(0, 0, 0));
	private final Vector3D middle;

	private final double maxDistance;

	private boolean showBehindCamera = false;

	private String id = "";

	public PlaneCamera3D(Vector3D viewpoint, CoordinateTransformation3D transformationToCamera, double curvature,
			Dimension screenDimension) {
		this.viewpoint = viewpoint;
		this.transformationToCamera = transformationToCamera;
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
	public CoordinateTransformation3D getTransformationToCamera() {
		return transformationToCamera;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.ICamera3D#project(de.zintel.math.Vector3D)
	 */
	@Override
	public Vector3D projectWorld(final Vector3D worldpoint) {
		return projectCamera(toCamera(worldpoint));
	}

	@Override
	public Vector3D projectCamera(Vector3D camerapoint) {
		return projectCamera(camerapoint, showBehindCamera);
	}

	@Override
	public Vector3D projectCamera(Vector3D camerapoint, boolean showbehind) {

		// liegt bei z<0 hinter der Linse
		Vector3D screenpoint = (!showbehind && behindScreen(camerapoint)) ? null : intersect(camerapoint, viewpoint);
		if (screenpoint != null && curvature > 0) {
			screenpoint = curve(screenpoint, curvature);
		}
		return screenpoint;
	}

	@Override
	public boolean inScreenRange(final Vector3D point) {
		return point != null && point.x() >= 0 && point.x() < screenDimension.getWidth() && point.y() >= 0
				&& point.y() < screenDimension.getHeight();
	}

	private Vector3D curve(final Vector3D point, final double curvature) {

		final Vector3D direction = Vector3D.substract(point, middle);
		final double distance = direction.length();
		final double weight = Math.pow(Math.sin(MathUtils.scalel(0, maxDistance, 0, Math.PI / 2, distance)), 2);

		return Vector3D.add(point, Vector3D.mult((weight * curvature * distance) / maxDistance, direction));

	}

	@Override
	public double getCurvature() {
		return curvature;
	}

	@Override
	public void setCurvature(double curvature) {
		this.curvature = curvature;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.ICamera3D#rotate(double, double, double)
	 */
	@Override
	public void rotate(Axis3D axis, double angle) {
		transformationToCamera.rotate(axis, angle);
	}

	@Override
	public void translate(final Vector3D vector) {
		transformationToCamera.translate(vector);
	}

	@Override
	public void reset() {
		transformationToCamera.reset();
	}

	public boolean isShowBehindCamera() {
		return showBehindCamera;
	}

	public void setShowBehindCamera(boolean showBehindCamera) {
		this.showBehindCamera = showBehindCamera;
	}

	public Dimension getScreenDimension() {
		return screenDimension;
	}

	@Override
	public boolean behindScreen(Vector3D camerapoint) {
		return camerapoint.z() < 0;
	}

	@Override
	public String getId() {
		return id;
	}

	public PlaneCamera3D setId(String id) {
		this.id = id;
		return this;
	}

	@Override
	public Vector3D toCamera(Vector3D worldpoint) {
		return transformationToCamera.transformPoint(worldpoint);
	}

	@Override
	public Vector3D toWorld(Vector3D camerapoint) {
		return transformationToCamera.inverseTransformPoint(camerapoint);
	}

	@Override
	public Vector3D intersect(Vector3D camerap1, Vector3D camerap2) {
		return Utils3D.intersect(camerap1, camerap2, plane);
	}

}
