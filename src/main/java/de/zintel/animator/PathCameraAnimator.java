/**
 * 
 */
package de.zintel.animator;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BiFunction;

import de.zintel.camera.ICamera3D;
import de.zintel.gfx.g3d.APointInterpolater3D;
import de.zintel.math.Axis3D;
import de.zintel.math.MathUtils;
import de.zintel.math.Utils3D;
import de.zintel.math.Vector3D;
import de.zintel.math.transform.CoordinateTransformation3D;
import de.zintel.utils.Pair;
import de.zintel.utils.StepUnit;

/**
 * @author friedo
 *
 */
public class PathCameraAnimator implements IAnimator {

	private final ICamera3D<?> camera;

	private final Vector3D center;

	private final Dimension dimension;

	private final BiFunction<Vector3D, Vector3D, APointInterpolater3D> pointInterpolaterFactory;

	private final Vector3D mid;

	private APointInterpolater3D pointInterpolater3D;

	private Vector3D previousPoint;

	private StepUnit<Vector3D> currentUnit = null;

	private int counter = 0;

	private Collection<StepUnit<Vector3D>> pathpoints = Collections.emptyList();

	private Iterator<StepUnit<Vector3D>> pathiterator = null;

	private boolean logging = false;

	// for smooth centering
	private int mergeWithCenterStep = 0;

	private int mergeWithCenterSteps = 120;

	private Axis3D lastAxis = null;

	public PathCameraAnimator(ICamera3D<?> camera, Vector3D center, Dimension dimension,
			BiFunction<Vector3D, Vector3D, APointInterpolater3D> pointInterpolaterFactory) {
		this.camera = camera;
		this.center = new Vector3D(center);
		this.dimension = dimension;
		this.pointInterpolaterFactory = pointInterpolaterFactory;
		this.mid = new Vector3D(dimension.getWidth() / 2, dimension.getHeight() / 2, 0);
		this.previousPoint = makeRandomPoint();
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

			currentUnit = pathiterator.next();
			final Vector3D point = currentUnit.getElement();

			counter++;
			if (counter % 10 != 0) {
				step();
				return;
			}

			final CoordinateTransformation3D toCamera = camera.getTransformationToCamera();
			final Vector3D ppoint = Vector3D.substract(toCamera.transformPoint(point), camera.getViewpoint());

			final Vector3D pcenter = toCamera.transformPoint(center);

			// translation
			camera.translate(ppoint);
			previousPoint = point;

			// rotation to center
			final Vector3D viewpoint = camera.getViewpoint();
			final Vector3D distancevector = Vector3D.substract(pcenter, viewpoint);

			final Vector3D cameradirection = new Vector3D(0, 0, 100);
			final Pair<Double, Vector3D> angleAxis = Utils3D.angleAxis(cameradirection, distancevector);
			final Double angle = angleAxis.getFirst();
			if (Double.isNaN(angle)) {
				return;
			}

			final Vector3D axisv = Vector3D.normalize(angleAxis.getSecond());

			final double effectiveAngle = (mergeWithCenterStep == mergeWithCenterSteps) ? angle
					: MathUtils.interpolateReal(0, angle, mergeWithCenterStep, mergeWithCenterSteps,
							(x, max) -> x * Math.sin((Math.PI * x) / (2 * max)));

			if (MathUtils.inEpsilonRange(effectiveAngle)) {
				lastAxis = new Axis3D(viewpoint, Vector3D.add(viewpoint, axisv));
				camera.rotate(lastAxis, -effectiveAngle);
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

		pointInterpolater3D = pointInterpolaterFactory.apply(previousPoint == null ? makeRandomPoint() : previousPoint, makeRandomPoint());

		final LinkedList<StepUnit<Vector3D>> list = new LinkedList<>();
		for (final StepUnit<Vector3D> unit : pointInterpolater3D) {
			list.add(unit);
		}
		pathpoints = list;
		pathiterator = pathpoints.iterator();

		counter = 0;
	}

	protected Vector3D makeRandomPoint() {
		return new Vector3D(makeRangeValue(dimension.width), makeRangeValue(dimension.height), makeRangeValue(dimension.height));
	}

	private double makeRangeValue(int dim) {

		int fac = 7;
		return MathUtils.RANDOM.nextInt(fac * dim) - (fac / 2) * dim;
	}

	public Collection<StepUnit<Vector3D>> getPathpoints() {
		return pathpoints;
	}

	public boolean isLogging() {
		return logging;
	}

	public void setLogging(boolean logging) {
		this.logging = logging;
	}

	public Axis3D getLastAxis() {
		return lastAxis;
	}

	public ICamera3D<?> getCamera() {
		return camera;
	}

	public Vector3D getCenter() {
		return center;
	}

	/**
	 * maybe null
	 * 
	 * @return
	 */
	public StepUnit<Vector3D> getCurrentUnit() {
		return currentUnit;
	}

}
