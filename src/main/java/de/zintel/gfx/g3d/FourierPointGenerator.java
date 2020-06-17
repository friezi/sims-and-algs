package de.zintel.gfx.g3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.zintel.math.MathUtils;
import de.zintel.math.PolarND;
import de.zintel.math.Vector3D;

public class FourierPointGenerator extends APointInterpolater3D {

	public static class FourierCircle {

		private final PolarND<Vector3D> start;

		private final double radius;

		private final double angularVelocity;

		public double angle = 0;

		public Vector3D vector;

		/**
		 * @param radius
		 * @param angularVelocity
		 *            in degree!
		 */
		public FourierCircle(double radius, double angularVelocity) {
			this.radius = radius;
			this.angularVelocity = angularVelocity;
			this.start = new PolarND<>(radius, Arrays.asList(0D));
		}

		/**
		 * @param start
		 * @param angularVelocity
		 *            in degree!
		 */
		public FourierCircle(Vector3D start, double angularVelocity) {
			this.start = start.toPolar();
			this.radius = this.start.getRadius();
			this.angularVelocity = angularVelocity;
		}

		@Override
		public String toString() {
			return "FourierCircle [radius=" + radius + ", angularVelocity=" + angularVelocity + "]";
		}

	}

	private final int maxIterations;

	private final List<FourierCircle> circles = new LinkedList<>();

	private final List<Vector3D> points = new LinkedList<>();

	private int step = 0;

	public FourierPointGenerator(Vector3D start, Vector3D end, int maxIterations) {
		super(start, end);
		this.maxIterations = maxIterations;
	}

	@Override
	public boolean hasNext() {
		return step <= maxIterations;
	}

	@Override
	public StepUnit3D next() {

		final Vector3D point = new Vector3D();

		for (final FourierCircle circle : circles) {

			circle.angle = step * (circle.angularVelocity < 0 ? 360 + circle.angularVelocity : circle.angularVelocity);

			final Double initAngle = circle.start.getAngles().iterator().next();
			circle.vector = new Vector3D(circle.radius * Math.cos(MathUtils.radian(circle.angle) + initAngle),
					-circle.radius * Math.sin(MathUtils.radian(circle.angle) + initAngle), 0);
			point.add(circle.vector);
		}

		step++;

		return new StepUnit3D(Vector3D.add(getStart(), point), step, maxIterations);
	}

	public FourierPointGenerator addCircle(final FourierCircle circle) {
		circles.add(circle);
		return this;
	}

	public Collection<FourierCircle> getCircles() {
		return new ArrayList<>(circles);
	}

}
