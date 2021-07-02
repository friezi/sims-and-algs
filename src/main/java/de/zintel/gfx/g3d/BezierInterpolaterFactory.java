/**
 * 
 */
package de.zintel.gfx.g3d;

import java.awt.Dimension;
import java.util.function.BiFunction;

import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;

/**
 * @author friedo
 *
 */
public class BezierInterpolaterFactory implements BiFunction<Vector3D, Vector3D, APointInterpolater3D> {

	private final Dimension dimension;

	public BezierInterpolaterFactory(Dimension dimension) {
		this.dimension = dimension;
	}

	@Override
	public APointInterpolater3D apply(Vector3D start, Vector3D end) {

		BezierPointInterpolater3D bezierPointInterpolater3D = new BezierPointInterpolater3D(start, end);

		int maxControllPoints = MathUtils.RANDOM.nextInt(8) + 1;
		for (int i = 0; i < maxControllPoints; i++) {
			bezierPointInterpolater3D.addControlPoint(makeRandomPoint());
		}

		return bezierPointInterpolater3D;
	}

	protected Vector3D makeRandomPoint() {
		return new Vector3D(makeRangeValue(dimension.width), makeRangeValue(dimension.height), makeRangeValue(dimension.height));
	}

	private double makeRangeValue(int dim) {

		int fac = 7;
		return MathUtils.RANDOM.nextInt(fac * dim) - (fac / 2) * dim;
	}
}
