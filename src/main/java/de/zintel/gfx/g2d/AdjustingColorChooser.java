package de.zintel.gfx.g2d;

import java.awt.Color;

public class AdjustingColorChooser implements Edge2D.EdgeColorChooser {

	@Override
	public Color getColor(Edge2D edge) {

		final Color origColor = edge.getOrigColor();
		double ratio = Vector2D.distance(edge.getFirst().getCurrent(), edge.getSecond().getCurrent()) / edge.getLength();
		if (ratio != 1) {

			final float[] cValues = origColor.getRGBComponents(null);
			return new Color(adjustColor(cValues[0], ratio), adjustColor(cValues[1], ratio), adjustColor(cValues[2], ratio), cValues[3]);

		}

		return origColor;
	}

	private float adjustColor(final float value, double ratio) {
		return (ratio > 1 ? (float) (value / (1.0 + (ratio - 1.0) / 2)) : (float) (1.0 - (1 - value) * (1.0 * ratio)));
	}

}