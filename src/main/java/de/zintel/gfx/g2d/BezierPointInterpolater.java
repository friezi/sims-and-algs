/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import de.zintel.math.Utils;

/**
 * @author Friedemann
 *
 */
public class BezierPointInterpolater extends APointInterpolater2D {

	private static class Line {

		private final Point start;

		private final Point end;

		public Line(Point start, Point end) {
			this.start = start;
			this.end = end;
		}

		public Point getStart() {
			return start;
		}

		public Point getEnd() {
			return end;
		}

		public int lengthX() {
			return Math.abs(end.x - start.x);
		}

		public int lengthY() {
			return Math.abs(end.y - start.y);
		}

	}

	private final boolean autoconnectPoints;

	private final boolean scattering;

	private final List<Point> controlPoints = new ArrayList<>();

	private int iteration = 0;

	private int maxIterations = 0;

	private final List<Line> baseLines = new ArrayList<>();

	private final List<Point> curvePoints = new ArrayList<>();

	private int pointIndex = 0;

	public BezierPointInterpolater(Point start, Point end, boolean autoconnectPoints, boolean scattering) {
		super(start, end);
		this.autoconnectPoints = autoconnectPoints;
		this.scattering = scattering;
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
	public IterationUnit2D next() {

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
			iteration++;

			while (iteration <= maxIterations) {

				interpolate(baseLines);
				iteration++;

			}

		}

		final Point curvePoint = curvePoints.get(pointIndex++);

		return new IterationUnit2D(curvePoint, pointIndex, curvePoints.size());

	}

	private int interpolate(int v1, int v2, int iterations, int maxIterations) {
		return scattering ? Utils.interpolateLinearMoreScattering(v1, v2, iteration, maxIterations)
				: Utils.interpolateLinear(v1, v2, iteration, maxIterations);
	}

	private void interpolate(final List<Line> lines) {

		List<Line> currentLines = lines;

		while (true) {

			if (currentLines.size() == 1) {

				Line line = currentLines.get(0);
				final Point point = new Point(interpolate(line.getStart().x, line.getEnd().x, iteration, maxIterations),
						interpolate(line.getStart().y, line.getEnd().y, iteration, maxIterations));

				if (curvePoints.isEmpty() || !autoconnectPoints) {

					curvePoints.add(point);

				} else {

					final APointInterpolater2D pointInterpolater = new AlternateLinearPointInterpolater2D(
							curvePoints.get(curvePoints.size() - 1), point, true);
					while (pointInterpolater.hasNext()) {
						curvePoints.add(pointInterpolater.next().getPoint());
					}

				}

				break;

			} else {

				List<Line> nextLines = new ArrayList<>();
				Line l1;
				Line l2;
				Point previousPoint = null;
				Point point1;
				Point point2;
				for (int i = 0; i < currentLines.size() - 1; i++) {

					l1 = currentLines.get(i);
					l2 = currentLines.get(i + 1);

					if (previousPoint == null) {
						point1 = new Point(interpolate(l1.getStart().x, l1.getEnd().x, iteration, maxIterations),
								interpolate(l1.getStart().y, l1.getEnd().y, iteration, maxIterations));
					} else {
						point1 = previousPoint;
					}

					point2 = new Point(interpolate(l2.getStart().x, l2.getEnd().x, iteration, maxIterations),
							interpolate(l2.getStart().y, l2.getEnd().y, iteration, maxIterations));

					nextLines.add(new Line(point1, point2));

					previousPoint = point2;
				}

				currentLines = nextLines;

			}
		}

	}

	public BezierPointInterpolater addControlPoint(final Point point) {
		controlPoints.add(point);
		return this;
	}

	private int getMaxLength() {

		int maxLength = 0;
		for (final Line line : baseLines) {
			maxLength = Math.max(maxLength, Math.max(line.lengthX(), line.lengthY()));
		}

		return maxLength;

	}

	public List<Point> getControlPoints() {
		return controlPoints;
	}

}
