/**
 * 
 */
package de.zintel.gfx.g3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;

/**
 * @author friedemann.zintel
 *
 */
public class RandomPointInterpolaterFactory implements BiFunction<Vector3D, Vector3D, APointInterpolater3D> {

	private final List<BiFunction<Vector3D, Vector3D, APointInterpolater3D>> factories;

	/**
	 * 
	 */
	public RandomPointInterpolaterFactory(BiFunction<Vector3D, Vector3D, APointInterpolater3D>... factories) {
		this.factories = new ArrayList<>(Arrays.asList(factories));
	}

	@Override
	public APointInterpolater3D apply(Vector3D t, Vector3D u) {
		return factories.get(MathUtils.RANDOM.nextInt(factories.size())).apply(t, u);
	}

}
