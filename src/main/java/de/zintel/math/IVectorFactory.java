/**
 * 
 */
package de.zintel.math;

import java.util.List;

/**
 * @author friedo
 *
 */
public interface IVectorFactory<T extends AVectorND<T>> {

	public abstract T newVector(T vector);

	public abstract T newVector(List<Double> values);

	public abstract T newVector();

}
