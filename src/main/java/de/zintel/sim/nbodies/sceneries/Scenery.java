/**
 * 
 */
package de.zintel.sim.nbodies.sceneries;

import java.util.Random;

import de.zintel.gfx.g2d.Field;
import de.zintel.gfx.g2d.Vector2D;
import de.zintel.physics.Body;
import de.zintel.physics.GravitationSystem;

/**
 * @author friedo
 *
 */
public class Scenery {

	protected static final Random RANDOM = new Random();

	protected static Vector2D makeRandomPoint(int minX, int minY, int maxX, int maxY) {
		return new Vector2D(RANDOM.nextInt(maxX - minX - 1) + minX, RANDOM.nextInt(maxY - minY - 1) + minY);
	}

	protected static Vector2D makeRandomVector(int width, int height) {
	
		int dX = (RANDOM.nextInt(2) == 1 ? 1 : -1);
		int dY = (RANDOM.nextInt(2) == 1 ? 1 : -1);
	
		return new Vector2D(dX * RANDOM.nextInt(width), dY * RANDOM.nextInt(height));
	}

	protected final int width;

	protected final int height;

	protected final SceneryConfig sceneryConfig;

	public Scenery(int width, int height, SceneryConfig sceneryConfig) {
		this.width = width;
		this.height = height;
		this.sceneryConfig = sceneryConfig;
	}

	public GravitationSystem createGravitationSystem() {

		return new GravitationSystem(new Field(spaceMin(width), spaceMin(height), spaceMax(width), spaceMax(height)),
				sceneryConfig.getPhysics());

	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public SceneryConfig getSceneryConfig() {
		return sceneryConfig;
	}

	protected Body makeRandomBody(String id) {
	
		int mass = RANDOM.nextInt((sceneryConfig.getMaxMass() - sceneryConfig.getMinMass()) + 1) + sceneryConfig.getMinMass();
		// int size = (mass * MAX_BODY_SIZE - MIN_BODY_SIZE) / MAX_MASS +
		// MIN_BODY_SIZE;
		double size = GravitationSystem.calculateSize(mass);
		return new Body(id, size, mass,
				makeRandomPoint((int) spaceMin(width), (int) spaceMin(height), (int) spaceMax(width), (int) spaceMax(height)),
				makeRandomVector(1, 1));
	}

	public double spaceMin(int max) {
		return -(max * sceneryConfig.getDistance() - max) / 2;
	}

	public double spaceMax(int max) {
		return (max * sceneryConfig.getDistance() + max) / 2;
	}

}
