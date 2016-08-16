package de.zintel.sim.nbodies.sceneries;

import de.zintel.sim.nbodies.Physics;

public class TestSceneryConfig extends SceneryConfig {

	public TestSceneryConfig() {
		super(80, 10, 30, 30000, 1, 1, false, false, 5,
				new Physics().setRestitutionCoefficient(1).setDecay(1).setThresholdCollision(0).setParticleMass(600)
						.setParticleVelocityReduction(3).setExplosionThreshold(25).setAccelerationFactor(0.2).setUseExplosion(true)
						.setCollision(true).setParticleGravitation(true).setGravitation(false));
	}
}
