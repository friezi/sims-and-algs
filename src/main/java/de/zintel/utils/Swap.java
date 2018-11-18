/**
 * 
 */
package de.zintel.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author friedo
 *
 */
public class Swap<T> {

	private int idx = 1;

	private final List<T> buffer;

	/**
	 * 
	 */
	public Swap(final T first, final T second) {
		this.buffer = new ArrayList<>(Arrays.asList(first, second));
	}

	public T next() {
		return buffer.get(nextIdx());
	}

	private int nextIdx() {
		idx = idx >= 1 ? 0 : 1;
		return idx;
	}
}
