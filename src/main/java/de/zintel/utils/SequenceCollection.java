/**
 * 
 */
package de.zintel.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Friedemann
 *
 */
public class SequenceCollection<E> implements Collection<E> {

	private class SIterator implements Iterator<E> {

		private final Iterator<Collection<? extends E>> cIterator = collections.iterator();

		private Collection<? extends E> collection;

		private Iterator<? extends E> eIterator;

		public SIterator() {

			collection = cIterator.next();
			eIterator = collection.iterator();

		}

		@Override
		public boolean hasNext() {

			while (!eIterator.hasNext()) {

				if (!cIterator.hasNext()) {
					return false;
				}

				collection = cIterator.next();
				eIterator = collection.iterator();

			}

			return true;
		}

		@Override
		public E next() {
			return eIterator.next();
		}

	}

	private final Collection<E> baseCollection = new LinkedList<>();

	private final Collection<Collection<? extends E>> collections = new LinkedList<>();

	/**
	 * 
	 */
	public SequenceCollection() {
		collections.add(baseCollection);
	}

	public SequenceCollection<E> cat(Collection<? extends E> c) {
		collections.add(c);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		return baseCollection.add(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return baseCollection.addAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		baseCollection.clear();
		collections.clear();
		collections.add(null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {

		for (Collection<? extends E> collection : collections) {
			if (collection.contains(o)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {

		int size = size();

		Collection<? extends E> all = new ArrayList<>(size);
		for (Collection<? extends E> collection : collections) {
			all.containsAll(collection);
		}

		return all.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {

		for (Collection<? extends E> collection : collections) {
			if (!collection.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return new SIterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {

		boolean removed = false;

		for (Collection<? extends E> collection : collections) {
			removed |= collection.remove(o);
		}

		return removed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {

		boolean removed = false;

		for (Collection<? extends E> collection : collections) {
			removed |= collection.removeAll(c);
		}

		return removed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {

		boolean changed = false;

		for (Collection<? extends E> collection : collections) {
			changed |= collection.retainAll(c);
		}

		return changed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {

		int size = 0;

		for (Collection<? extends E> collection : collections) {
			size += collection.size();
		}

		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {

		int size = size();

		Object[] all = new Object[size];
		int i = 0;
		for (Collection<? extends E> collection : collections) {
			for (E e : collection) {
				all[i++] = e;
			}
		}

		return all;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		throw new RuntimeException("not yet supported!");
	}

}
