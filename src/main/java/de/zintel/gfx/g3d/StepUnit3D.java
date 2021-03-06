package de.zintel.gfx.g3d;

import de.zintel.math.Vector3D;

public class StepUnit3D {

	private final Vector3D point;

	private final int step;

	private final int stepMax;

	public StepUnit3D(Vector3D point, int step, int stepMax) {
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

	public Vector3D getPoint() {
		return point;
	}

}