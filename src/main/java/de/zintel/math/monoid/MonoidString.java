/**
 * 
 */
package de.zintel.math.monoid;

import java.util.function.BiFunction;

/**
 * @author friedemann.zintel
 *
 */
public class MonoidString extends Monoid<String> {

	private final String neutral;

	public MonoidString(String op, String neutral, BiFunction<String, String, String> conc) {
		super(conc, neutral);
		this.neutral = neutral;
	}

	public String getNeutral() {
		return neutral;
	}

}
