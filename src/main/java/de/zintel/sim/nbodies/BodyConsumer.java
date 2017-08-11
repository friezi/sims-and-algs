/**
 * 
 */
package de.zintel.sim.nbodies;

import java.util.Collection;
import java.util.function.Consumer;

import de.zintel.physics.Body;

/**
 * @author friedemann.zintel
 *
 */
public abstract class BodyConsumer implements Consumer<Collection<Body>>, AutoCloseable {
}
