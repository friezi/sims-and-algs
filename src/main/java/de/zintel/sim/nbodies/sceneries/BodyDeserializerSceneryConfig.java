/**
 * 
 */
package de.zintel.sim.nbodies.sceneries;

import java.util.Collection;
import java.util.Collections;

import de.zintel.physics.Body;
import de.zintel.physics.gravitation.Physics;

/**
 * @author friedemann.zintel
 *
 */
public class BodyDeserializerSceneryConfig extends SceneryConfig {

	private final String filename;

	/**
	 * @param width
	 * @param filename
	 */
	public BodyDeserializerSceneryConfig(int width, int height, String filename) {
		super(width, height, 230, 100, 20000, 100000, 100, 10, false, true, 30,
				new Physics().setRestitutionCoefficient(0.995).setDecay(2).setThresholdCollision(1.5).setParticleMass(300)
						.setParticleVelocityReduction(2.2).setExplosionThreshold(50).setAccelerationFactor(0.2).setUseExplosion(true)
						.setCollision(true).setParticleGravitation(true).setParticleCollision(true).setParticleInfluence(true)
						.setParticleOnParticle(false).setDestroyParticlesSpeedThreshold(150).setGravitation(true).setUseMelting(false));
		this.filename = filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.sceneries.SceneryConfig#getInitialBodies()
	 */
	@Override
	public Collection<Body> getInitialBodies() {
		return Collections.emptyList();
	}

	public String getFilename() {
		return filename;
	}

}
