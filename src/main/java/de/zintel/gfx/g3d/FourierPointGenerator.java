package de.zintel.gfx.g3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;

public class FourierPointGenerator extends APointInterpolater3D {

	public static class FourierCircle {

		private final double radius;

		private final double angularVelocity;

		double angle = 0;

		public FourierCircle(double radius, double angularVelocity) {
			this.radius = radius;
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

			if (step > 0) {
				circle.angle = circle.angle + (circle.angularVelocity < 0 ? 360 + circle.angularVelocity : circle.angularVelocity);
			}

			point.add(new Vector3D(circle.radius * Math.cos(theta(circle.angle)), circle.radius * Math.sin(theta(circle.angle)), 0));
		}

		step++;

		return new StepUnit3D(Vector3D.add(getStart(), point), step, maxIterations);
	}

	public FourierPointGenerator addCircle(final FourierCircle circle) {
		circles.add(circle);
		return this;
	}

	/**
	 * degree -> radian
	 * 
	 * @param degree
	 * @return
	 */
	private double theta(final double degree) {
		return MathUtils.morphRange(0, 360, 0, 2 * Math.PI, ((int) degree) % 360);
	}

	public Collection<FourierCircle> getCircles() {
		return new ArrayList<>(circles);
	}

}
