/**
 * 
 */
package de.zintel.math.transform;

import java.util.function.Supplier;

/**
 * @author friedemann.zintel
 *
 */
public class TrigonomFnProviderAngle implements ITrigonomFnProvider {

	private final double angle;

	/**
	 * 
	 */
	public TrigonomFnProviderAngle(double angle) {
		this.angle = angle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.transform.ITrigonomFnProvider#sinProvider()
	 */
	@Override
	public Supplier<Double> sinProvider() {
		return () -> Math.sin(angle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.transform.ITrigonomFnProvider#cosProvider()
	 */
	@Override
	public Supplier<Double> cosProvider() {
		return () -> Math.cos(angle);
	}

}
