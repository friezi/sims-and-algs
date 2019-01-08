/**
 * 
 */
package de.zintel.math.monoid;

/**
 * @author friedemann.zintel
 *
 */
public class MonoidSumDouble extends Monoid<Double> {

	/**
	 * 
	 */
	public MonoidSumDouble() {
		super((a, b) -> a + b, 0D);
	}

}
