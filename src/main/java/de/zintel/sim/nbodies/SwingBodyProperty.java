/**
 * 
 */
package de.zintel.sim.nbodies;

import java.awt.Color;

/**
 * @author friedo
 *
 */
public class SwingBodyProperty extends BodyProperty {

	public static final String CLASSNAME = SwingBodyProperty.class.getSimpleName();

	private Color bodyColor;

	private Color currentBodyColor;

	private Color currentCoronaColor;

	private Integer rateCoronaRays;

	private Boolean spinned;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.BodyProperty#newInstance()
	 */
	@Override
	public BodyProperty newInstance() {
		return new SwingBodyProperty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.BodyProperty#initialiseInstance(de.zintel.sim.
	 * nbodies.BodyProperty)
	 */
	@Override
	public void initialiseInstance(BodyProperty instance) {

		SwingBodyProperty property = (SwingBodyProperty) instance;
		property.setBodyColor(getBodyColor());
		property.setCurrentBodyColor(getCurrentBodyColor());
		property.setCurrentCoronaColor(getCurrentCoronaColor());
		property.setRateCoronaRays(getRateCoronaRays());
		property.setSpinned(isSpinned());

		super.initialiseInstance(instance);

	}

	public Color getBodyColor() {
		return bodyColor;
	}

	public void setBodyColor(Color color) {
		this.bodyColor = color;
	}

	public Color getCurrentBodyColor() {
		return currentBodyColor;
	}

	public void setCurrentBodyColor(Color currentColor) {
		this.currentBodyColor = currentColor;
	}

	public Color getCurrentCoronaColor() {
		return currentCoronaColor;
	}

	public void setCurrentCoronaColor(Color currentCoronaColor) {
		this.currentCoronaColor = currentCoronaColor;
	}

	public Integer getRateCoronaRays() {
		return rateCoronaRays;
	}

	public void setRateCoronaRays(Integer rateCoronaRays) {
		this.rateCoronaRays = rateCoronaRays;
	}

	public Boolean isSpinned() {
		return spinned;
	}

	public void setSpinned(Boolean spinned) {
		this.spinned = spinned;
	}

}
