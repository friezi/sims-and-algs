/**
 * 
 */
package de.zintel.sim.nbodies.sceneries;

import java.util.ArrayList;
import java.util.Collection;

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

}
