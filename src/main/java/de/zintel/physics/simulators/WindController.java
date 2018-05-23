/**
 * 
 */
package de.zintel.physics.simulators;

import java.util.function.BiFunction;

import de.zintel.math.Vector2D;

/**
 * @author friedemann.zintel
 *
 */
public class WindController implements Runnable, BiFunction<Vector2D, Vector2D, Vector2D> {

	private final WindSimulator windSimulator;

	private boolean doWind = true;

	public WindController(WindSimulator windSimulator) {
		this.windSimulator = windSimulator;
	}

	public boolean isDoWind() {
		return doWind;
	}

	@Override
	public Vector2D apply(Vector2D t, Vector2D u) {
		if (doWind) {
			return windSimulator.calculateWind(t);
		} else {
			return Vector2D.NULL_VECTOR;
		}
	}

	@Override
	public void run() {
		if (doWind) {
			windSimulator.progressWindflaw();
		}
	}

	public WindController setDoWind(boolean doWind) {
		this.doWind = doWind;
		return this;
	}

	public WindSimulator getWindSimulator() {
		return windSimulator;
	}

}
