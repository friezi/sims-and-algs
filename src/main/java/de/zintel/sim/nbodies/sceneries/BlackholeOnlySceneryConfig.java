package de.zintel.sim.nbodies.sceneries;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import de.zintel.physics.Body;
import de.zintel.physics.gravitation.Physics;
import de.zintel.sim.nbodies.SwingBodyProperty;

public class BlackholeOnlySceneryConfig extends SceneryConfig {

	public BlackholeOnlySceneryConfig(int width, int height) {
		super(width, height, 0, 100, 20000, 100000, 100, 10, false, true, 30,
				new Physics().setRestitutionCoefficient(0.995).setDecay(3).setThresholdCollision(1.5).setParticleMass(1000)
						.setParticleVelocityReduction(1).setExplosionThreshold(0.0000001).setAccelerationFactor(0.2).setUseExplosion(true)
						.setCollision(true).setParticleGravitation(true).setParticleCollision(true).setParticleInfluence(true)
						.setParticleOnParticle(false).setDestroyParticlesSpeedThreshold(150).setGravitation(true).setUseMelting(false));
	}

	@Override
	public Collection<Body> getInitialBodies() {

		final Collection<Body> initialBodies = new ArrayList<>();

		final Body blackhole1 = new Body("blackhole1", Physics.calculateSize(1000000) / 10, 140000000,
				makeRandomPoint((int) spaceMin(width / 3), (int) spaceMin(height / 3), (int) spaceMax(width / 3),
						(int) spaceMax(height / 3)),
				makeRandomVector(5, 5));
		final SwingBodyProperty blackhole1Property = new SwingBodyProperty();
		final Color blackhole1Color = Color.BLACK;
		blackhole1Property.setBodyColor(blackhole1Color);
		blackhole1Property.setCurrentBodyColor(blackhole1Color);
		blackhole1Property.setCurrentCoronaColor(blackhole1Color);
		blackhole1.setProperty(SwingBodyProperty.CLASSNAME, blackhole1Property);
		initialBodies.add(blackhole1);
		final Body blackhole2 = new Body("blackhole2", Physics.calculateSize(1000000) / 10, 140000000,
				makeRandomPoint((int) spaceMin(width / 3), (int) spaceMin(height / 3), (int) spaceMax(width / 3),
						(int) spaceMax(height / 3)),
				makeRandomVector(5, 5));
		final SwingBodyProperty blackhole2Property = new SwingBodyProperty();
		final Color blackhole2Color = Color.BLACK;
		blackhole2Property.setBodyColor(blackhole2Color);
		blackhole2Property.setCurrentBodyColor(blackhole2Color);
		blackhole2Property.setCurrentCoronaColor(blackhole2Color);
		blackhole2.setProperty(SwingBodyProperty.CLASSNAME, blackhole2Property);
		initialBodies.add(blackhole2);

		return initialBodies;
	}
}
