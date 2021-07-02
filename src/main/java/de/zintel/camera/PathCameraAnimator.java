/**
 * 
 */
package de.zintel.camera;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import de.zintel.animation.IAnimator;
import de.zintel.gfx.g3d.APointInterpolater3D;
import de.zintel.gfx.g3d.StepUnit3D;
import de.zintel.math.Axis3D;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;
import de.zintel.math.transform.CoordinateTransformation3D;

/**
 * @author friedo
 *
 */
public abstract class PathCameraAnimator implements IAnimator {

	private final ICamera3D camera;

	private final Vector3D center;

	private final Dimension dimension;

	private final Vector3D mid;

	private APointInterpolater3D pointInterpolater3D;

	private Vector3D previousPoint;

	private int counter = 0;

	private Collection<Vector3D> pathpoints = Collections.emptyList();

	private Iterator<Vector3D> pathiterator = null;

	private boolean logging = false;

	// for smooth centering
	private int mergeWithCenterStep = 0;

	private int mergeWithCenterSteps = 120;

	public PathCameraAnimator(ICamera3D camera, Vector3D center, Dimension dimension) {
		this.camera = camera;
		this.center = center;
		this.dimension = dimension;
		this.mid = new Vector3D(dimension.getWidth() / 2, dimension.getHeight() / 2, 0);
		this.previousPoint = camera.getTransformationToCamera().inverseTransformPoint(camera.getViewpoint());
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

		if (pointInterpolater3D == null || !pathiterator.hasNext()) {
			makePath();
		} else {

			final Vector3D next = pathiterator.next();

			counter++;
			if (counter % 5 != 0) {
				step();
				return;
			}

			final CoordinateTransformation3D toCamera = camera.getTransformationToCamera();
			final Vector3D ppoint = toCamera.transformPoint(next);

			final Vector3D pcenter = toCamera.transformPoint(center);

			// translation
			camera.translate(ppoint);
			previousPoint = next;

			// rotation to center
			final Vector3D vpoint = camera.getViewpoint();
			final Vector3D cnorm = Vector3D.substract(pcenter, vpoint);
			final double cnlen = cnorm.length();
			if (cnlen == 0) {
				// camera has reached center, no adjustment possible
				return;
			}

			final Vector3D snorm = new Vector3D(0, 0, 1);
			final Vector3D rotnorm = Vector3D.crossProduct(cnorm, snorm);
			final double angle = Math.asin(rotnorm.length() / (Math.abs(cnlen) * Math.abs(snorm.length())));
			final double effectiveAngle = MathUtils.interpolateReal(0, angle, mergeWithCenterStep, mergeWithCenterSteps,
					(x, max) -> x * Math.sin((Math.PI * x) / (2 * max)));

			if (MathUtils.inEpsilonRange(effectiveAngle)) {
				camera.rotate(new Axis3D(vpoint, Vector3D.add(vpoint, rotnorm)), -effectiveAngle);
			}

			if (mergeWithCenterStep < mergeWithCenterSteps) {
				mergeWithCenterStep++;
			}

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

	private void makePath() {

		pointInterpolater3D = newPointInterpolater(previousPoint == null ? makeRandomPoint() : previousPoint, makeRandomPoint());

		final LinkedList<Vector3D> list = new LinkedList<>();
		for (final StepUnit3D unit : pointInterpolater3D) {
			list.add(unit.getPoint());
		}
		pathpoints = list;
		pathiterator = pathpoints.iterator();

		counter = 0;
	}

	protected abstract APointInterpolater3D newPointInterpolater(final Vector3D start, final Vector3D end);

	protected Vector3D makeRandomPoint() {
		return new Vector3D(makeRangeValue(dimension.width), makeRangeValue(dimension.height), makeRangeValue(dimension.height));
	}

	private double makeRangeValue(int dim) {

		int fac = 7;
		return MathUtils.RANDOM.nextInt(fac * dim) - (fac / 2) * dim;
	}

	public Collection<Vector3D> getPathpoints() {
		return pathpoints;
	}

	public boolean isLogging() {
		return logging;
	}

	public void setLogging(boolean logging) {
		this.logging = logging;
	}

}
