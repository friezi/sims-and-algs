/**
 * 
 */
package de.zintel.camera;

import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;

import de.zintel.animation.IAnimator;
import de.zintel.gfx.g3d.BezierPointInterpolater3D;
import de.zintel.gfx.g3d.StepUnit3D;
import de.zintel.math.Axis3D;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;
import de.zintel.math.transform.CoordinateTransformation3D;

/**
 * @author friedo
 *
 */
public class BezierCameraAnimator implements IAnimator {

	private final ICamera3D camera;

	private final Vector3D center;

	private final Dimension dimension;

	private BezierPointInterpolater3D bezierPointInterpolater3D;

	private Vector3D previousPoint = null;

	public BezierCameraAnimator(ICamera3D camera, Vector3D center, Dimension dimension) {
		this.camera = camera;
		this.center = center;
		this.dimension = dimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.animation.IAnimator#reinit()
	 */
	@Override
	public void reinit() {
		camera.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.animation.IAnimator#step()
	 */
	@Override
	public void step() {

		if (bezierPointInterpolater3D == null || !bezierPointInterpolater3D.hasNext()) {
			makeBezier();
		} else {

			final Vector3D next = bezierPointInterpolater3D.next().getPoint();

			final CoordinateTransformation3D toCamera = camera.getTransformationToCamera();
			final Vector3D ppoint = toCamera.transformPoint(next);

			final Vector3D pcenter = toCamera.transformPoint(center);

			// translation
			camera.translate(ppoint);
			previousPoint = next;

			// rotation to center
			final Vector3D vpoint = camera.getViewpoint();
			final Vector3D cnorm = Vector3D.substract(vpoint, pcenter);
			final double cnlen = cnorm.length();
			if (cnlen == 0) {
				// camera has reached center, no adjustment possible
				return;
			}

			final Vector3D snorm = new Vector3D(0, 0, -1);
			final Vector3D rotnorm = Vector3D.crossProduct(cnorm, snorm);
			final double angle = Math.asin(rotnorm.length() / (Math.abs(cnlen) * Math.abs(snorm.length())));

			camera.rotate(new Axis3D(vpoint, Vector3D.add(vpoint, rotnorm)), angle);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.animation.IAnimator#finished()
	 */
	@Override
	public boolean finished() {
		return false;
	}

	private void makeBezier() {

		bezierPointInterpolater3D = new BezierPointInterpolater3D(previousPoint == null ? makeRandomPoint() : previousPoint,
				makeRandomPoint());

		int maxControllPoints = MathUtils.RANDOM.nextInt(15) + 1;
		for (int i = 0; i < maxControllPoints; i++) {
			bezierPointInterpolater3D.addControlPoint(makeRandomPoint());
		}
	}

	private Vector3D makeRandomPoint() {
		return new Vector3D(makeRangeValue(dimension.width), makeRangeValue(dimension.height), makeRangeValue(dimension.height));
	}

	private double makeRangeValue(int dim) {

		int fac = 7;
		return MathUtils.RANDOM.nextInt(fac * dim) - (fac / 2) * dim;
	}

}
