/**
 * 
 */
package de.zintel.utils;

import java.util.Iterator;

/**
 * @author friedemann.zintel
 *
 */
public class IterableIterator<T> implements Iterator<T>, Iterable<T> {

	private final Iterator<T> iterator;

	public IterableIterator(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		return iterator.next();
	}

}
