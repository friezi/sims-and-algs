/**
 * 
 */
package de.zintel.math;

import java.util.Random;

/**
 * @author Friedemann
 *
 */
public class ContinuousInterpolatingValueProvider {

	private final int min;

	private final int max;

	private int currentStart;

	private int currentStop;

	private int value;

	private Random rnd = new Random();

	public ContinuousInterpolatingValueProvider(int min, int max) {
		this.min = min;
		this.max = max;

		init(min, max);

	}

	private void init(int min, int max) {

		currentStart = rnd.nextInt(max - min) + min;
		currentStop = rnd.nextInt(max - min) + min;

		value = currentStart;
	}

	public int nextValue() {

		if (value == currentStop) {

			currentStart = currentStop;
			currentStop = rnd.nextInt(max - min) + min;
			value = currentStart;

		} else {

			if (currentStop > currentStart) {
				value++;
			} else {
				value--;
			}
		}

		return value;

	}

}
