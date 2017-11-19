/**
 * 
 */
package de.zintel.ci;

import de.zintel.math.Polar2D;
import de.zintel.math.Vector2D;

/**
 * @author Friedemann
 *
 */
public class Boid {

	private Vector2D position;

	private final String id;

	private Vector2D direction = new Vector2D();

	private Polar2D directionPolar = new Polar2D(1, 0);

	private Vector2D previousDirection = new Vector2D();

	private Polar2D previousDirectionPolar = new Polar2D(1, 0);

	private Vector2D panic = new Vector2D();

	private BoidType type = BoidType.MEMBER;

	private boolean convergeAttractor = true;

	private BoidMotioner motioner = null;

	public Boid(Vector2D position, String id) {
		super();
		this.position = position;
		this.id = id;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	public Vector2D getDirection() {
		return direction;
	}

	public void setDirection(Vector2D direction) {
		this.direction = direction;
		directionPolar = direction.toPolar();
	}

	public BoidMotioner getMotioner() {
		return motioner;
	}

	public Boid setMotioner(BoidMotioner motioner) {
		this.motioner = motioner;
		return this;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Boid other = (Boid) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Vector2D getPreviousDirection() {
		return previousDirection;
	}

	public Boid setPreviousDirection(Vector2D previousDirection) {
		this.previousDirection = previousDirection;
		previousDirectionPolar = previousDirection.toPolar();
		return this;
	}

	public boolean isConvergeAttractor() {
		return convergeAttractor;
	}

	public Boid setConvergeAttractor(boolean convergeAttractor) {
		this.convergeAttractor = convergeAttractor;
		return this;
	}

	public BoidType getType() {
		return type;
	}

	public Boid setType(BoidType type) {
		this.type = type;
		return this;
	}

	public Vector2D getPanic() {
		return panic;
	}

	public Boid setPanic(Vector2D panic) {
		this.panic = panic;
		return this;
	}

	public Polar2D getDirectionPolar() {
		return directionPolar;
	}

	public Polar2D getPreviousDirectionPolar() {
		return previousDirectionPolar;
	}

}
