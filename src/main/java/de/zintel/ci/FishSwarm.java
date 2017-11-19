package de.zintel.ci;

import de.zintel.math.Vector2D;

public class FishSwarm extends Swarm {

	public FishSwarm(Vector2D center) {
		super(center);
		setInfluenceOfAlignment(22);
	}

}
