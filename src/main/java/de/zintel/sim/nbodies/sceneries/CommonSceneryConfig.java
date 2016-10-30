/**
 * 
 */
package de.zintel.sim.nbodies.sceneries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import de.zintel.gfx.g2d.Vector2D;
import de.zintel.physics.Body;
import de.zintel.physics.gravitation.Physics;

/**
 * @author friedemann.zintel
 *
 */
public class CommonSceneryConfig extends SceneryConfig {

	/**
	 * @param nmbBodies
	 * @param minBodySize
	 * @param maxBodySize
	 * @param maxMass
	 * @param minMass
	 * @param delay
	 * @param drawVectors
	 * @param starfield
	 * @param distance
	 * @param physics
	 */
	public CommonSceneryConfig(int width, int height, int nmbBodies, int minBodySize, int maxBodySize, int maxMass, int minMass, long delay,
			boolean drawVectors, boolean starfield, double distance, Physics physics) {
		super(width, height, nmbBodies, minBodySize, maxBodySize, maxMass, minMass, delay, drawVectors, starfield, distance, physics);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.sceneries.SceneryConfig#getInitialBodies()
	 */
	@Override
	public Collection<Body> getInitialBodies() {

		Collection<Body> bodies = new ArrayList<>();
		for (int i = 0; i < getNmbBodies(); i++) {
			bodies.add(makeRandomBody(String.valueOf(i)));
		}
		return bodies;
	}

	protected Body makeRandomBody(String id) {

		int mass = RANDOM.nextInt((getMaxMass() - getMinMass()) + 1) + getMinMass();
		// int size = (mass * MAX_BODY_SIZE - MIN_BODY_SIZE) / MAX_MASS +
		// MIN_BODY_SIZE;
		double size = Physics.calculateSize(mass);
		return new Body(id, size, mass,
				makeRandomPoint((int) spaceMin(width), (int) spaceMin(height), (int) spaceMax(width), (int) spaceMax(height)),
				makeRandomVector(1, 1));
	}

	protected static final Random RANDOM = new Random();

	protected static Vector2D makeRandomPoint(int minX, int minY, int maxX, int maxY) {
		return new Vector2D(RANDOM.nextInt(maxX - minX - 1) + minX, RANDOM.nextInt(maxY - minY - 1) + minY);
	}

	protected static Vector2D makeRandomVector(int width, int height) {

		int dX = (RANDOM.nextInt(2) == 1 ? 1 : -1);
		int dY = (RANDOM.nextInt(2) == 1 ? 1 : -1);

		return new Vector2D(dX * RANDOM.nextInt(width), dY * RANDOM.nextInt(height));
	}

}
