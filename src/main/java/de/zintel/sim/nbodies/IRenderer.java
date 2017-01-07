/**
 * 
 */
package de.zintel.sim.nbodies;

import java.util.Collection;

import de.zintel.physics.Body;

/**
 * @author Friedemann
 *
 */
public interface IRenderer {

	void initGraphics();

	void initBodyProperties(Collection<Body> bodies);

	void renderBody(Body body);

	void renderCorona(Body body);

	void renderVelocity(Body body);

	void renderFiretail(Body body);

}
