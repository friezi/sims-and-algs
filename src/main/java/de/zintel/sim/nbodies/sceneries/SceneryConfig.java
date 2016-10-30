/**
 * 
 */
package de.zintel.sim.nbodies.sceneries;

import java.util.Collection;
import java.util.Random;

import de.zintel.gfx.g2d.Vector2D;
import de.zintel.physics.Body;
import de.zintel.physics.gravitation.Physics;

/**
 * @author Friedemann
 *
 */
public abstract class SceneryConfig {

	protected static final Random RANDOM = new Random();

	protected final int width;

	protected final int height;

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

	public SceneryConfig(int width, int height, int nmbBodies, int minBodySize, int maxBodySize, int maxMass, int minMass, long delay,
			boolean drawVectors, boolean starfield, double distance, Physics physics) {
		this.width = width;
		this.height = height;
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

	public double spaceMin(int max) {
		return -(max * getDistance() - max) / 2;
	}

	public double spaceMax(int max) {
		return (max * getDistance() + max) / 2;
	}

	public abstract Collection<Body> getInitialBodies();

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
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

	protected static Vector2D makeRandomPoint(int minX, int minY, int maxX, int maxY) {
		return new Vector2D(RANDOM.nextInt(maxX - minX - 1) + minX, RANDOM.nextInt(maxY - minY - 1) + minY);
	}

	protected static Vector2D makeRandomVector(int width, int height) {

		int dX = (RANDOM.nextInt(2) == 1 ? 1 : -1);
		int dY = (RANDOM.nextInt(2) == 1 ? 1 : -1);

		return new Vector2D(dX * RANDOM.nextInt(width), dY * RANDOM.nextInt(height));
	}

}
