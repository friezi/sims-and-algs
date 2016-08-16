package de.zintel.sim.nbodies.sceneries;

import de.zintel.sim.nbodies.Physics;

public class StarfieldSceneryConfig extends SceneryConfig {

	public StarfieldSceneryConfig() {
		super(330, 100, 20000, 100000, 1000, 10, false, true, 30,
				new Physics().setRestitutionCoefficient(0.995).setDecay(5).setThresholdCollision(1.5).setParticleMass(300)
						.setParticleVelocityReduction(1.5).setExplosionThreshold(130).setAccelerationFactor(1).setUseExplosion(true)
						.setCollision(true).setParticleGravitation(false).setParticleCollision(true).setParticleInfluence(true)
						.setDestroyParticlesSpeedThreshold(300).setGravitation(true));
	}
}
