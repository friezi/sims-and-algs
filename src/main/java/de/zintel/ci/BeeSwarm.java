/**
 * 
 */
package de.zintel.ci;

import de.zintel.math.Vector2DPlain;

/**
 * @author Friedemann
 *
 */
public class BeeSwarm extends Swarm {

	public BeeSwarm(Vector2DPlain center) {
		super(center);
		setUseAlignment(false);
	}
}
