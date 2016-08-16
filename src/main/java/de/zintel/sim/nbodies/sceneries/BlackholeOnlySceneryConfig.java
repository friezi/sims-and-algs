package de.zintel.sim.nbodies.sceneries;

import de.zintel.sim.nbodies.Physics;

public class BlackholeOnlySceneryConfig extends SceneryConfig {

	public BlackholeOnlySceneryConfig() {
		super(0, 100, 20000, 100000, 100, 10, false, true, 30,
				new Physics().setRestitutionCoefficient(0.995).setDecay(3).setThresholdCollision(1.5).setParticleMass(1000)
						.setParticleVelocityReduction(1).setExplosionThreshold(0.0000001).setAccelerationFactor(0.2).setUseExplosion(true)
						.setCollision(true).setParticleGravitation(true).setParticleCollision(true).setParticleInfluence(true)
						.setParticleOnParticle(false).setDestroyParticlesSpeedThreshold(150).setGravitation(true).setUseMelting(false));
	}
}
