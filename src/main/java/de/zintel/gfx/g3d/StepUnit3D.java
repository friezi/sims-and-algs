package de.zintel.gfx.g3d;

public class StepUnit3D {

	private final Point3D point;

	private final int step;

	private final int stepMax;

	public StepUnit3D(Point3D point, int step, int stepMax) {
		super();
		this.point = point;
		this.step = step;
		this.stepMax = stepMax;
	}

	@Override
	public String toString() {
		return "StepUnit [step=" + step + ", stepMax=" + stepMax + "]";
	}

	public int getStep() {
		return step;
	}

	public int getStepMax() {
		return stepMax;
	}

	public Point3D getPoint() {
		return point;
	}

}