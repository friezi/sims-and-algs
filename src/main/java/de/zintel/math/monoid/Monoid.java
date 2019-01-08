/**
 * 
 */
package de.zintel.math.monoid;

import java.util.function.BiFunction;

/**
 * @author friedemann.zintel
 *
 */
public class Monoid<T> implements BiFunction<T, T, T> {

	private final BiFunction<T, T, T> f;

	private final T neutral;

	public Monoid(BiFunction<T, T, T> f, T neutral) {
		this.f = f;
		this.neutral = neutral;
	}

	@Override
	public T apply(T t, T u) {
		return f.apply(t, u);
	}

	public T neutral() {
		return neutral;
	}

}
