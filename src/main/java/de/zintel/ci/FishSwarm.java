package de.zintel.ci;

import de.zintel.math.Vector2DPlain;

public class FishSwarm extends Swarm {

	public FishSwarm(Vector2DPlain center) {
		super(center);
		setInfluenceOfAlignment(22);
	}

}
