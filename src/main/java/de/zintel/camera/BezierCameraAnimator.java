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

	private final Vector3D mid;

	private BezierPointInterpolater3D bezierPointInterpolater3D;

	private Vector3D previousPoint;

	private int counter = 0;

	private Collection<Vector3D> pathpoints = Collections.emptyList();

	private Iterator<Vector3D> pathiterator = null;

	public BezierCameraAnimator(ICamera3D camera, Vector3D center, Dimension dimension) {
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

		if (bezierPointInterpolater3D == null || !pathiterator.hasNext()) {
			makeBezier();
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

			camera.rotate(new Axis3D(vpoint, Vector3D.add(vpoint, rotnorm)), -angle);

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

		bezierPointInterpolater3D = new BezierPointInterpolater3D(previousPoint == null ? makeRandomPoint() : previousPoint, makeRandomPoint());

		int maxControllPoints = MathUtils.RANDOM.nextInt(8) + 1;
		for (int i = 0; i < maxControllPoints; i++) {
			bezierPointInterpolater3D.addControlPoint(makeRandomPoint());
		}

		final LinkedList<Vector3D> list = new LinkedList<>();
		for (final StepUnit3D unit : bezierPointInterpolater3D) {
			list.add(unit.getPoint());
		}
		pathpoints = list;
		pathiterator = pathpoints.iterator();

		counter = 0;
	}

	private Vector3D makeRandomPoint() {
		return new Vector3D(makeRangeValue(dimension.width), makeRangeValue(dimension.height), makeRangeValue(dimension.height));
	}

	private double makeRangeValue(int dim) {

		int fac = 7;
		return MathUtils.RANDOM.nextInt(fac * dim) - (fac / 2) * dim;
	}

	public Collection<Vector3D> getPathpoints() {
		return pathpoints;
	}

}
