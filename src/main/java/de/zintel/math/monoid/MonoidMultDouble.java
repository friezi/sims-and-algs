/**
 * 
 */
package de.zintel.math.monoid;

/**
 * @author friedemann.zintel
 *
 */
public class MonoidMultDouble extends Monoid<Double> {

	/**
	 * 
	 */
	public MonoidMultDouble() {
		super((a, b) -> a * b, 1D);
	}

}
