/**
 * 
 */
package de.zintel.sim.nbodies.sceneries;

import java.io.IOException;

import de.zintel.gfx.g2d.Field;
import de.zintel.physics.Body;
import de.zintel.physics.gravitation.BodyDeserializingProducer;
import de.zintel.physics.gravitation.BodyParameterDeserializingProducer;
import de.zintel.physics.gravitation.GravitationSystem;
import de.zintel.physics.gravitation.IBodyProducer;

/**
 * @author friedo
 *
 */
public final class Scenery {

	protected final SceneryConfig sceneryConfig;

	public Scenery(SceneryConfig sceneryConfig) {
		this.sceneryConfig = sceneryConfig;
	}

	public IBodyProducer createGravitationSystem() {

		if (sceneryConfig instanceof BodyDeserializerSceneryConfig) {

			try {
				return new BodyDeserializingProducer(((BodyDeserializerSceneryConfig) sceneryConfig).getFilename());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

		} else if (sceneryConfig instanceof BodyParameterDeserializerSceneryConfig) {

			try {
				return new BodyParameterDeserializingProducer(((BodyParameterDeserializerSceneryConfig) sceneryConfig).getFilename());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

		} else {

			final GravitationSystem gravitationSystem = new GravitationSystem(
					new Field(sceneryConfig.spaceMin(sceneryConfig.getWidth()), sceneryConfig.spaceMin(sceneryConfig.getHeight()),
							sceneryConfig.spaceMax(sceneryConfig.getWidth()), sceneryConfig.spaceMax(sceneryConfig.getHeight())),
					sceneryConfig.getPhysics());

			for (final Body body : sceneryConfig.getInitialBodies()) {
				gravitationSystem.addBody(body);
			}

			return gravitationSystem;
		}

	}

	public SceneryConfig getSceneryConfig() {
		return sceneryConfig;
	}

}
