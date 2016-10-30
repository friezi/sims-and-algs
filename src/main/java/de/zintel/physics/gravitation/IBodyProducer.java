package de.zintel.physics.gravitation;

import java.util.Collection;

import de.zintel.gfx.g2d.Field;
import de.zintel.physics.Body;

public interface IBodyProducer {

	/**
	 * 
	 */
	void calculate();

	Collection<Body> getBodies();

	void setField(Field field);

	void shutdown();

}