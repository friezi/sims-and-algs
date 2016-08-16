/**
 * 
 */
package de.zintel.sim.nbodies.sceneries;

import de.zintel.physics.GravitationSystem;

/**
 * @author friedo
 *
 */
public class CommonScenery extends Scenery {

	public CommonScenery(int width, int height, SceneryConfig sceneryConfig) {
		super(width, height, sceneryConfig);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.Scenery#init()
	 */
	@Override
	public GravitationSystem createGravitationSystem() {

		final GravitationSystem gravitationSystem = super.createGravitationSystem();

		for (int i = 0; i < sceneryConfig.getNmbBodies(); i++) {
			gravitationSystem.addBody(makeRandomBody(String.valueOf(i)));
		}

		return gravitationSystem;

	}

}
