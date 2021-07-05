/**
 * 
 */
package de.zintel.math.transform;

import java.util.function.Function;

import de.zintel.math.AVectorND;
import de.zintel.math.Vector3D;

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
		return AVectorND.diagmult(scalevector, vector);
	}

	public Scaler3D snapshot() {
		return new Scaler3D(new Vector3D(scalevector));
	}

	public Scaler3D inverse() {
		return new Scaler3D(new Vector3D(1 / scalevector.x(), 1 / scalevector.y(), 1 / scalevector.z()));
	}

	public Scaler3D cat(Scaler3D sc) {
		return new Scaler3D(Vector3D.diagmult(this.scalevector, sc.scalevector));
	}

}
