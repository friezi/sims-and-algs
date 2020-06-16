/**
 * 
 */
package de.zintel.gfx.g3d;

import java.util.ArrayList;
import java.util.List;

import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;

/**
 * @author Friedemann
 *
 */
public class BezierPointInterpolater3D extends APointInterpolater3D {

	private static class Line {

		private final Vector3D start;

		private final Vector3D end;

		public Line(Vector3D start, Vector3D end) {
			this.start = start;
			this.end = end;
		}

		public Vector3D getStart() {
			return start;
		}

		public Vector3D getEnd() {
			return end;
		}

		public double lengthX() {
			return Math.abs(end.x() - start.x());
		}

		public double lengthY() {
			return Math.abs(end.y() - start.y());
		}

		public double lengthZ() {
			return Math.abs(end.z() - start.z());
		}

	}

	private final List<Vector3D> controlPoints = new ArrayList<>();

	private int iteration = 0;

	private int maxIterations = 0;

	private final List<Line> baseLines = new ArrayList<>();

	private final List<Vector3D> curvePoints = new ArrayList<>();

	private int pointIndex = 0;

	public BezierPointInterpolater3D(Vector3D start, Vector3D end) {
		super(start, end);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return iteration == 0 || pointIndex < curvePoints.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public StepUnit3D next() {

		if (iteration == 0) {

			iteration++;

			if (controlPoints.isEmpty()) {

				baseLines.add(new Line(getStart(), getEnd()));

			} else {

				baseLines.add(new Line(getStart(), controlPoints.get(0)));

				for (int i = 0; i < controlPoints.size() - 1; i++) {
					baseLines.add(new Line(controlPoints.get(i), controlPoints.get(i + 1)));
				}

				baseLines.add(new Line(controlPoints.get(controlPoints.size() - 1), getEnd()));

			}

			maxIterations = getMaxLength() + 1;

			while (iteration <= maxIterations) {

				interpolate(baseLines);
				iteration++;

			}

		}

		final Vector3D curvePoint = curvePoints.get(pointIndex++);

		return new StepUnit3D(curvePoint, pointIndex, curvePoints.size());

	}

	private double interpolate(double v1, double v2, int iterations, int maxIterations) {
		return MathUtils.interpolateLinearReal(v1, v2, iteration, maxIterations);
	}

	private void interpolate(final List<Line> lines) {

		List<Line> currentLines = lines;

		while (true) {

			if (currentLines.size() == 1) {

				Line line = currentLines.get(0);
				final Vector3D point = new Vector3D(interpolate(line.getStart().x(), line.getEnd().x(), iteration, maxIterations),
						interpolate(line.getStart().y(), line.getEnd().y(), iteration, maxIterations),
						interpolate(line.getStart().z(), line.getEnd().z(), iteration, maxIterations));

				curvePoints.add(point);

				break;

			} else {

				List<Line> nextLines = new ArrayList<>();
				Line l1, l2;
				Vector3D point1, point2, previousPoint = null;
				for (int i = 0; i < currentLines.size() - 1; i++) {

					l1 = currentLines.get(i);
					l2 = currentLines.get(i + 1);

					if (previousPoint == null) {
						point1 = new Vector3D(interpolate(l1.getStart().x(), l1.getEnd().x(), iteration, maxIterations),
								interpolate(l1.getStart().y(), l1.getEnd().y(), iteration, maxIterations),
								interpolate(l1.getStart().z(), l1.getEnd().z(), iteration, maxIterations));
					} else {
						point1 = previousPoint;
					}

					point2 = new Vector3D(interpolate(l2.getStart().x(), l2.getEnd().x(), iteration, maxIterations),
							interpolate(l2.getStart().y(), l2.getEnd().y(), iteration, maxIterations),
							interpolate(l2.getStart().z(), l2.getEnd().z(), iteration, maxIterations));

					nextLines.add(new Line(point1, point2));

					previousPoint = point2;
				}

				currentLines = nextLines;

			}
		}

	}

	public BezierPointInterpolater3D addControlPoint(final Vector3D point) {
		controlPoints.add(point);
		return this;
	}

	private int getMaxLength() {

		double maxLength = 0;
		for (final Line line : baseLines) {
			maxLength = Math.max(maxLength, Math.max(line.lengthX(), Math.max(line.lengthY(), line.lengthZ())));
		}

		return (int) maxLength;

	}

	public List<Vector3D> getControlPoints() {
		return controlPoints;
	}

}
