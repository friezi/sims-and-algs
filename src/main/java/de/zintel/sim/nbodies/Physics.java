/**
 * 
 */
package de.zintel.sim.nbodies;

/**
 * @author Friedemann
 *
 */
public class Physics {

	private double restitutionCoefficient = 1;

	private double decay = 1;

	private double thresholdCollision = 3;

	private int particleMass = 100;

	private double explosionThreshold = 65;

	private boolean useExplosion = true;

	private double particleVelocityReduction = 10;

	private double accelerationFactor = 1;

	private boolean collision = true;

	private boolean particleGravitation = true;

	private boolean particleCollision = true;

	private boolean gravitation = true;

	private boolean particleInfluence = true;

	private boolean particleOnParticle = false;

	private int destroyParticlesSpeedThreshold = 0;

	private boolean useMelting = false;

	public double getRestitutionCoefficient() {
		return restitutionCoefficient;
	}

	public Physics setRestitutionCoefficient(double restitutionCoefficient) {
		this.restitutionCoefficient = restitutionCoefficient;
		return this;
	}

	public double getDecay() {
		return decay;
	}

	public Physics setDecay(double decay) {

		if (decay < 1) {
			throw new RuntimeException("decay must be at least 1!");
		}

		this.decay = decay;
		return this;
	}

	public double getThresholdCollision() {
		return thresholdCollision;
	}

	public Physics setThresholdCollision(double thresholdCollision) {
		this.thresholdCollision = thresholdCollision;
		return this;
	}

	public int getParticleMass() {
		return particleMass;
	}

	public Physics setParticleMass(int particleMass) {
		this.particleMass = particleMass;
		return this;
	}

	public double getExplosionThreshold() {
		return explosionThreshold;
	}

	public Physics setExplosionThreshold(double explosionThreshold) {
		this.explosionThreshold = explosionThreshold;
		return this;
	}

	public boolean isUseExplosion() {
		return useExplosion;
	}

	public Physics setUseExplosion(boolean useExplosion) {
		this.useExplosion = useExplosion;
		return this;
	}

	public double getParticleVelocityReduction() {
		return particleVelocityReduction;
	}

	public Physics setParticleVelocityReduction(double particleVelocityReduction) {
		this.particleVelocityReduction = particleVelocityReduction;
		return this;
	}

	public double getAccelerationFactor() {
		return accelerationFactor;
	}

	public Physics setAccelerationFactor(double accelerationFactor) {
		this.accelerationFactor = accelerationFactor;
		return this;
	}

	public boolean isCollision() {
		return collision;
	}

	public Physics setCollision(boolean collision) {
		this.collision = collision;
		return this;
	}

	public boolean isParticleGravitation() {
		return particleGravitation;
	}

	public Physics setParticleGravitation(boolean particleGravitaion) {
		this.particleGravitation = particleGravitaion;
		return this;
	}

	public boolean isGravitation() {
		return gravitation;
	}

	public Physics setGravitation(boolean gravitation) {
		this.gravitation = gravitation;
		return this;
	}

	public boolean isParticleCollision() {
		return particleCollision;
	}

	public Physics setParticleCollision(boolean particleCollision) {
		this.particleCollision = particleCollision;
		return this;
	}

	public boolean isParticleInfluence() {
		return particleInfluence;
	}

	public Physics setParticleInfluence(boolean particleInfluence) {
		this.particleInfluence = particleInfluence;
		return this;
	}

	public int getDestroyParticlesSpeedThreshold() {
		return destroyParticlesSpeedThreshold;
	}

	public Physics setDestroyParticlesSpeedThreshold(int destroyParticlesSpeedThreshold) {
		this.destroyParticlesSpeedThreshold = destroyParticlesSpeedThreshold;
		return this;
	}

	public boolean isParticleOnParticle() {
		return particleOnParticle;
	}

	public Physics setParticleOnParticle(boolean particleOnParticle) {
		this.particleOnParticle = particleOnParticle;
		return this;
	}

	public boolean isUseMelting() {
		return useMelting;
	}

	public Physics setUseMelting(boolean useMelting) {
		this.useMelting = useMelting;
		return this;
	}

}
