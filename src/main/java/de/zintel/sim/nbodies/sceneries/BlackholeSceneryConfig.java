package de.zintel.sim.nbodies.sceneries;

import de.zintel.sim.nbodies.Physics;

public class BlackholeSceneryConfig extends SceneryConfig {

	public BlackholeSceneryConfig() {
		super(230, 100, 20000, 100000, 100, 10, false, true, 30,
				new Physics().setRestitutionCoefficient(0.995).setDecay(2).setThresholdCollision(1.5).setParticleMass(300)
						.setParticleVelocityReduction(2.2).setExplosionThreshold(50).setAccelerationFactor(0.2).setUseExplosion(true)
						.setCollision(true).setParticleGravitation(true).setParticleCollision(true).setParticleInfluence(true)
						.setParticleOnParticle(false).setDestroyParticlesSpeedThreshold(150).setGravitation(true).setUseMelting(false));
	}
}
