/**
 * 
 */
package de.zintel.ci;

import de.zintel.gfx.g2d.Vector2D;

/**
 * @author Friedemann
 *
 */
public class BeeSwarm extends Swarm {

	public BeeSwarm(Vector2D leaderAttractor) {
		super(leaderAttractor);
		setUseAlignment(false);
	}
}
