/**
 * 
 */
package de.zintel.camera;

import java.awt.Dimension;

import de.zintel.gfx.g3d.APointInterpolater3D;
import de.zintel.gfx.g3d.BezierPointInterpolater3D;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;

/**
 * @author friedo
 *
 */
public class BezierCameraAnimator extends PathCameraAnimator {

	public BezierCameraAnimator(ICamera3D camera, Vector3D center, Dimension dimension) {
		super(camera, center, dimension);
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

	@Override
	protected APointInterpolater3D newPointInterpolater(Vector3D start, Vector3D end) {

		BezierPointInterpolater3D bezierPointInterpolater3D = new BezierPointInterpolater3D(start, end);

		int maxControllPoints = MathUtils.RANDOM.nextInt(8) + 1;
		for (int i = 0; i < maxControllPoints; i++) {
			bezierPointInterpolater3D.addControlPoint(makeRandomPoint());
		}

		return bezierPointInterpolater3D;
	}
}
