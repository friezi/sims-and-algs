package de.zintel.utils;

public class StepUnit<T> {

	private final T element;

	private final int step;

	private final int stepMax;

	public StepUnit(T element, int step, int stepMax) {
		super();
		this.element = element;
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

	public T getElement() {
		return element;
	}

}