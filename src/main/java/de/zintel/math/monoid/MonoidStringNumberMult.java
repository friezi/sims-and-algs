/**
 * 
 */
package de.zintel.math.monoid;

import java.util.function.BiFunction;

/**
 * @author friedemann.zintel
 *
 */
public class MonoidStringNumberMult extends MonoidString {

	private static final String NEUTRAL = "1";

	private static final BiFunction<String, String, String> conc = (a, b) -> cat(a, b);

	public MonoidStringNumberMult() {
		super("*", NEUTRAL, conc);
	}

	protected static String cat(final String a, final String b) {

		if (NEUTRAL.equals(a)) {
			return b;
		} else if (NEUTRAL.equals(b)) {
			return a;
		} else if (("0").equals(b) || ("0").equals(a)) {
			return "0";
		} else {
			if (a.startsWith("-") && b.startsWith("-")) {
				return a.substring(1) + "*" + b.substring(1);
			} else if (b.startsWith("-")) {
				return b + "*" + a;
			}
			return a + "*" + b;
		}

	}
}
