/**
 * 
 */
package de.zintel.math.monoid;

import java.util.function.BiFunction;

/**
 * @author friedemann.zintel
 *
 */
public class MonoidTextNumberSum extends MonoidTextRepr {

	private static final String NEUTRAL = "0";

	private static final BiFunction<String, String, String> conc = (a, b) -> cat(a, b);

	public MonoidTextNumberSum() {
		super("+", NEUTRAL, conc);
	}

	protected static String cat(final String a, final String b) {

		if (NEUTRAL.equals(a)) {
			return b;
		} else if (NEUTRAL.equals(b)) {
			return a;
		} else if (("-" + a).equals(b) || ("-" + b).equals(a)) {
			return "0";
		} else {
			if (a.startsWith("-")) {
				return "(" + b + a + ")";
			} else {
				return "(" + a + (b.startsWith("-") ? "" : "+") + b + ")";
			}
		}

	}
}
