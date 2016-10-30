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
public class BodyParameterDeserializerSceneryConfig extends SceneryConfig {

	private final String filename;

	/**
	 * @param width
	 * @param filename
	 */
	public BodyParameterDeserializerSceneryConfig(int width, int height, String filename) {
		super(width, height, 230, 100, 20000, 100000, 100, 10, false, true, 30, new Physics());
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
