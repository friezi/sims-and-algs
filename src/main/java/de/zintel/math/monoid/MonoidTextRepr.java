/**
 * 
 */
package de.zintel.math.monoid;

import java.util.function.BiFunction;

/**
 * @author friedemann.zintel
 *
 */
public class MonoidTextRepr extends Monoid<String> {

	public MonoidTextRepr(String op, String neutral, BiFunction<String, String, String> conc) {
		super(conc, neutral);
	}

}
