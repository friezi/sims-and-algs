package de.zintel.sim.nbodies.sceneries;

import java.util.Collection;
import java.util.Collections;

import de.zintel.physics.Body;
import de.zintel.physics.gravitation.Physics;

public class BallSceneryConfig extends SceneryConfig {

	public BallSceneryConfig(int width, int height) {
		super(width, height, 40, 10, 30, 300000, 1, 1, false, false, 5,
				new Physics().setRestitutionCoefficient(0.99).setDecay(3).setThresholdCollision(3).setParticleMass(600)
						.setParticleVelocityReduction(3).setExplosionThreshold(25).setAccelerationFactor(0.2).setUseExplosion(true)
						.setCollision(true).setParticleGravitation(true).setGravitation(true));
	}

	@Override
	public Collection<Body> getInitialBodies() {
		return Collections.emptyList();
	}
}
