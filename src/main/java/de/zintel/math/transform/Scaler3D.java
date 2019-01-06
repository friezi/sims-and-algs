/**
 * 
 */
package de.zintel.math.transform;

import java.util.function.Function;

import de.zintel.math.Vector3D;
import de.zintel.math.VectorND;

/**
 * @author friedemann.zintel
 *
 */
public class Scaler3D implements Function<Vector3D, Vector3D> {

	private final Vector3D scalevector;

	public Scaler3D(Vector3D scalevector) {
		this.scalevector = scalevector;
	}

	@Override
	public Vector3D apply(Vector3D vector) {
		return new Vector3D(VectorND.diagmult(scalevector, vector));
	}

}
