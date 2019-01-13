/**
 * 
 */
package de.zintel.physics.simulators;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import de.zintel.gfx.ScreenParameters;
import de.zintel.math.Vector2D;
import de.zintel.math.Vector2DPlain;
import de.zintel.math.VectorField2D;
import de.zintel.math.AVectorND;

/**
 * @author friedemann.zintel
 *
 */
public class WindSimulator {

	private final Random rnd = new Random(Instant.now().toEpochMilli());

	private final VectorField2D<Vector2D> airstreamField;

	private final ScreenParameters screenParameters;

	private double windIntensity = 0.0;

	private double windIntensityFactor = 1.0;

	private double windIteration = 0;

	private double windIterationStep = 0.5;

	private int rateOfAirstreamChange = 1;

	public WindSimulator(VectorField2D<Vector2D> airstreamField, ScreenParameters screenParameters) {
		this.airstreamField = airstreamField;
		randomAirstream();
		this.screenParameters = screenParameters;
	}

	public void progressWindflaw() {

		shuffleAirstream();
		windIteration += windIterationStep;
		if (windIteration >= 180) {

			windIteration = 0;
			windIterationStep = (rnd.nextInt(21) + 1) / 10.0;
			windIntensityFactor = (rnd.nextInt(40) + 1) / 10.0;

		}
		windIntensity = windIntensityFactor * Math.sin(windIteration * Math.PI / 180);

	}

	public Vector2DPlain calculateWind(final Vector2DPlain pos) {
		return calculateWindTurbulence(calculateAirstream(pos));
	}

	public Vector2D calculateAirstream(final Vector2DPlain pos) {

		final List<Integer> windfieldDimensions = airstreamField.getDimensions();
		final Vector2D coordinates = new Vector2D(Arrays.asList(pos.x * windfieldDimensions.get(0) / screenParameters.WIDTH,
				pos.y * windfieldDimensions.get(1) / screenParameters.HEIGHT));
		return airstreamField.interpolateLinear(coordinates, coordinates);
	}

	private Vector2DPlain calculateWindTurbulence(final Vector2D windVector) {
		// return new VectorND(new
		// ArrayList<Double>(){{windVector.getCoords().stream().forEach(c->add(windIntensity
		// * c * (rnd.nextDouble() * 3 - 1)));}});
		return new Vector2DPlain(windIntensity * windVector.get(0) * (rnd.nextDouble() * 3 - 1),
				windIntensity * windVector.get(1) * (rnd.nextDouble() * 3 - 1));
	}

	public void resetWindflaw() {
		windIntensity = 0.0;
		windIteration = 0;
	}

	public void shuffleAirstream() {

		final Function<Double, Double> gen = v -> rateOfAirstreamChange * (v - 0.5) / 5;
		final Integer width = airstreamField.getDimensions().get(0);
		final Integer height = airstreamField.getDimensions().get(1);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				final Vector2D pos = new Vector2D(Arrays.asList((double) x, (double) y));
				airstreamField.setValue(pos, Vector2D.add(airstreamField.getValue(pos),
						new Vector2D(Arrays.asList(gen.apply(rnd.nextDouble()), gen.apply(rnd.nextDouble())))));
			}
		}

	}

	public void randomAirstream() {

		final Function<Double, Double> gen = v -> v * 2 - 1;
		final Integer width = airstreamField.getDimensions().get(0);
		final Integer height = airstreamField.getDimensions().get(1);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				final Vector2D pos = new Vector2D(Arrays.asList((double) x, (double) y));
				airstreamField.setValue(pos, new Vector2D(Arrays.asList(gen.apply(rnd.nextDouble()), gen.apply(rnd.nextDouble()))));
			}
		}

	}

	public VectorField2D<Vector2D> getAirstreamField() {
		return airstreamField;
	}

	public int getRateOfAirstreamChange() {
		return rateOfAirstreamChange;
	}

	public WindSimulator setRateOfAirstreamChange(int rateOfAirstreamChange) {
		this.rateOfAirstreamChange = rateOfAirstreamChange;
		return this;
	}
}
