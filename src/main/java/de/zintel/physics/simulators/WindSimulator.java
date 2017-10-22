/**
 * 
 */
package de.zintel.physics.simulators;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import de.zintel.gfx.Coordination;
import de.zintel.gfx.g2d.Vector2D;
import de.zintel.math.VectorField2D;
import de.zintel.math.VectorND;

/**
 * @author friedemann.zintel
 *
 */
public class WindSimulator {

	private final Random rnd = new Random(Instant.now().toEpochMilli());

	private final VectorField2D airstreamField;

	private final Coordination coordination;

	private double windIntensity = 0.0;

	private double windIntensityFactor = 1.0;

	private double windIteration = 0;

	private double windIterationStep = 0.5;

	public WindSimulator(VectorField2D airstreamField, Coordination coordination) {
		this.airstreamField = airstreamField;
		this.coordination = coordination;
	}

	public void progressWindflaw() {

		windIteration += windIterationStep;
		if (windIteration >= 180) {

			windIteration = 0;
			windIterationStep = (rnd.nextInt(21) + 1) / 10.0;
			windIntensityFactor = (rnd.nextInt(40) + 1) / 10.0;
			randomAirstream();

		}
		windIntensity = windIntensityFactor * Math.sin(windIteration * Math.PI / 180);

	}

	public Vector2D calculateWind(final Vector2D pos) {
		return calculateWindTurbulence(calculateAirstream(pos));
	}

	public VectorND calculateAirstream(final Vector2D pos) {

		final List<Integer> windfieldDimensions = airstreamField.getDimensions();
		return airstreamField.interpolateLinear(new VectorND(Arrays.asList(pos.x * windfieldDimensions.get(0) / coordination.WIDTH,
				pos.y * windfieldDimensions.get(1) / coordination.HEIGHT)));
	}

	private Vector2D calculateWindTurbulence(final VectorND windVector) {
		return new Vector2D(windIntensity * windVector.get(0) * (rnd.nextDouble() * 3 - 1),
				windIntensity * windVector.get(1) * (rnd.nextDouble() * 3 - 1));
	}

	public void resetWindflaw() {
		windIntensity = 0.0;
		windIteration = 0;
	}

	public void shuffleAirstream() {

		final Function<Double, Double> gen = v -> (v - 0.5) / 5;
		final Integer width = airstreamField.getDimensions().get(0);
		final Integer height = airstreamField.getDimensions().get(1);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				final VectorND pos = new VectorND(Arrays.asList((double) x, (double) y));
				airstreamField.setValue(pos, VectorND.add(airstreamField.getValue(pos),
						new VectorND(Arrays.asList(gen.apply(rnd.nextDouble()), gen.apply(rnd.nextDouble())))));
			}
		}

	}

	public void randomAirstream() {

		final Function<Double, Double> gen = v -> v * 2 - 1;
		final Integer width = airstreamField.getDimensions().get(0);
		final Integer height = airstreamField.getDimensions().get(1);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				final VectorND pos = new VectorND(Arrays.asList((double) x, (double) y));
				airstreamField.setValue(pos, new VectorND(Arrays.asList(gen.apply(rnd.nextDouble()), gen.apply(rnd.nextDouble()))));
			}
		}

	}

	public VectorField2D getAirstreamField() {
		return airstreamField;
	}
}
