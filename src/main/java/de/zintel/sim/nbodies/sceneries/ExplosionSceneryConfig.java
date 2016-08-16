package de.zintel.sim.nbodies.sceneries;

import de.zintel.sim.nbodies.Physics;

public class ExplosionSceneryConfig extends SceneryConfig {

	public ExplosionSceneryConfig() {
		super(330, 100, 20000, 100000, 1000, 10, false, true, 30,
				new Physics().setRestitutionCoefficient(0.995).setDecay(2).setThresholdCollision(1.5).setParticleMass(200)
						.setParticleVelocityReduction(0.5).setExplosionThreshold(130).setAccelerationFactor(2).setUseExplosion(true)
						.setCollision(true).setParticleGravitation(false).setParticleCollision(true).setParticleInfluence(true)
						.setDestroyParticlesSpeedThreshold(170).setGravitation(true));
	}
}
