/**
 * 
 */
package de.zintel.utils;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Friedemann
 *
 */
public class Processor<T> {

	private final Iterator<T> iterator;

	private final Consumer<T> consumer;

	private T current;

	public Processor(Iterator<T> iterator, Consumer<T> consumer) {
		super();
		this.iterator = iterator;
		this.consumer = consumer;
	}

	/**
	 * does the whole processing of all remaining steps.
	 */
	public void process() {
		while (inProcess()) {
			progress();
		}
	}

	public boolean inProcess() {
		return iterator.hasNext();
	}

	/**
	 * will progress by one step.
	 */
	public void progress() {

		current = iterator.next();
		if (consumer != null) {
			consumer.accept(current);
		}

	}

	public T getCurrent() {
		return current;
	}

	public Consumer<T> getConsumer() {
		return consumer;
	}

}
