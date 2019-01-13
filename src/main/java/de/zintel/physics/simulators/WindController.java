/**
 * 
 */
package de.zintel.physics.simulators;

import java.util.function.BiFunction;

import de.zintel.math.Vector2DPlain;

/**
 * @author friedemann.zintel
 *
 */
public class WindController implements Runnable, BiFunction<Vector2DPlain, Vector2DPlain, Vector2DPlain> {

	private final WindSimulator windSimulator;

	private boolean doWind = true;

	public WindController(WindSimulator windSimulator) {
		this.windSimulator = windSimulator;
	}

	public boolean isDoWind() {
		return doWind;
	}

	@Override
	public Vector2DPlain apply(Vector2DPlain t, Vector2DPlain u) {
		if (doWind) {
			return windSimulator.calculateWind(t);
		} else {
			return Vector2DPlain.NULL_VECTOR;
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
