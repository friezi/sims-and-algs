/**
 * 
 */
package de.zintel.math.transform;

import java.util.function.Supplier;

/**
 * @author friedemann.zintel
 *
 */
public class TrigonomFnProviderDirect implements ITrigonomFnProvider {

	private final double sin;

	private final double cos;

	public TrigonomFnProviderDirect(double sin, double cos) {
		this.sin = sin;
		this.cos = cos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.transform.ITrigonomFnProvider#sinProvider()
	 */
	@Override
	public Supplier<Double> sinProvider() {
		return () -> sin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.transform.ITrigonomFnProvider#cosProvider()
	 */
	@Override
	public Supplier<Double> cosProvider() {
		return () -> cos;
	}

}
