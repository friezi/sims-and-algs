/**
 * 
 */
package de.zintel.ci;

import de.zintel.gfx.g2d.Vector2D;

/**
 * @author Friedemann
 *
 */
public class Boid {

	private Vector2D position;

	private final String id;

	private Vector2D direction = new Vector2D();

	private boolean leader = false;

	private boolean predator = false;

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
	}

	public boolean isLeader() {
		return leader;
	}

	public Boid setLeader(boolean leader) {
		this.leader = leader;
		return this;
	}

	public boolean isPredator() {
		return predator;
	}

	public Boid setPredator(boolean predator) {
		this.predator = predator;
		return this;
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

}
