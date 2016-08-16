package de.zintel.gfx.g2d;

import java.awt.Point;

public class IterationUnit2D {

	private final Point point;

	private final int iteration;

	private final int maxIterations;

	public IterationUnit2D(Point point, int iteration, int maxIterations) {
		super();
		this.point = point;
		this.iteration = iteration;
		this.maxIterations = maxIterations;
	}

	@Override
	public String toString() {
		return "IterationUnit2D [point=" + point + ", iteration=" + iteration + ", maxIterations=" + maxIterations + "]";
	}

	public int getIteration() {
		return iteration;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public Point getPoint() {
		return point;
	}

}