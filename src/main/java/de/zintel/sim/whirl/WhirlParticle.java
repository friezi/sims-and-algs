package de.zintel.sim.whirl;

import de.zintel.math.Vector3D;
import de.zintel.physics.particles.Particle;

public class WhirlParticle<A> extends Particle<A> {

	public final double velocity;

	public double angle = 0;

	public final Vector3D initialPosition;

	public WhirlParticle(Vector3D position, double velocity) {
		super(position);
		this.initialPosition = new Vector3D(position);
		this.velocity = velocity;
	}

}