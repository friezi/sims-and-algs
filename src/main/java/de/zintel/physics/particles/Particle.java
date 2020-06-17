/**
 * 
 */
package de.zintel.physics.particles;

import de.zintel.math.Vector3D;

/**
 * @author friedo
 * 
 * @param <T>
 *            additional atribute-type assignable from oustide the class
 *
 */
public class Particle<T> {

	private Vector3D position;

	private T attribute;

	public Particle(Vector3D position) {
		this.position = position;
	}

	public Vector3D getPosition() {
		return position;
	}

	public void setPosition(Vector3D position) {
		this.position = position;
	}

	public T getAttribute() {
		return attribute;
	}

	public void setAttribute(T attribute) {
		this.attribute = attribute;
	}

}
