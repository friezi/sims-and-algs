/**
 * 
 */
package de.zintel.gfx.g3d;

import java.util.Iterator;

import de.zintel.math.Vector3D;

/**
 * @author Friedemann
 *
 */
public abstract class APointInterpolater3D implements Iterator<StepUnit3D>, Iterable<StepUnit3D> {

	private final Vector3D start;

	private final Vector3D end;

	public APointInterpolater3D(Vector3D start, Vector3D end) {
		this.start = start;
		this.end = end;
	}

	public Vector3D getStart() {
		return start;
	}

	public Vector3D getEnd() {
		return end;
	}

	@Override
	public Iterator<StepUnit3D> iterator() {
		return this;
	}

}
