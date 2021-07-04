/**
 * 
 */
package de.zintel.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * An iterator, which cycles through the elements
 * 
 * @author friedemann.zintel
 *
 */
public class CyclingIterator<E> implements Iterator<E> {

	private final Collection<E> coll;

	private Iterator<E> it;

	public CyclingIterator(Collection<E> coll) {
		this.coll = coll;
		this.it = coll.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return !coll.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public E next() {

		if (!it.hasNext()) {
			it = coll.iterator();
		}

		return it.next();
	}

}
