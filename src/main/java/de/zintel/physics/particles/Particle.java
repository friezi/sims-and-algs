/**
 * 
 */
package de.zintel.physics.particles;

import de.zintel.math.Vector3D;

/**
 * @author friedo
 * 
 * @param <T>
 *            additional properties-type assignable from oustide the class
 *
 */
public class Particle<T> {

	private Vector3D position;

	private T attributes;

	public Particle(Vector3D position) {
		this.position = position;
	}

	public Vector3D getPosition() {
		return position;
	}

	public void setPosition(Vector3D position) {
		this.position = position;
	}

	public T getAttributes() {
		return attributes;
	}

	public void setAttributes(T attributes) {
		this.attributes = attributes;
	}

}
