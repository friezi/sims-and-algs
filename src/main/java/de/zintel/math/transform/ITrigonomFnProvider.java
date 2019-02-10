/**
 * 
 */
package de.zintel.math.transform;

import java.util.function.Supplier;

/**
 * @author friedemann.zintel
 *
 */
public interface ITrigonomFnProvider {

	Supplier<Double> sinProvider();

	Supplier<Double> cosProvider();

}
