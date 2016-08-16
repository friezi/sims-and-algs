/**
 * 
 */
package de.zintel.sim.nbodies.sceneries;

import de.zintel.sim.nbodies.Physics;

/**
 * @author Friedemann
 *
 */
public class SceneryConfig {

	private final int nmbBodies;

	private final int minBodySize;

	private final int maxBodySize;

	private final int maxMass;

	private final int minMass;

	private final long delay;

	private final boolean drawVectors;

	private final boolean starfield;

	private double distance;

	private final Physics physics;

	public SceneryConfig(int nmbBodies, int minBodySize, int maxBodySize, int maxMass, int minMass, long delay, boolean drawVectors,
			boolean starfield, double distance, Physics physics) {
		this.nmbBodies = nmbBodies;
		this.minBodySize = minBodySize;
		this.maxBodySize = maxBodySize;
		this.maxMass = maxMass;
		this.minMass = minMass;
		this.delay = delay;
		this.drawVectors = drawVectors;
		this.starfield = starfield;
		this.distance = distance;
		this.physics = physics;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getNmbBodies() {
		return nmbBodies;
	}

	public int getMinBodySize() {
		return minBodySize;
	}

	public int getMaxBodySize() {
		return maxBodySize;
	}

	public int getMaxMass() {
		return maxMass;
	}

	public int getMinMass() {
		return minMass;
	}

	public long getDelay() {
		return delay;
	}

	public boolean isDrawVectors() {
		return drawVectors;
	}

	public boolean isStarfield() {
		return starfield;
	}

	public Physics getPhysics() {
		return physics;
	}

}
