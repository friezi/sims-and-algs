package de.zintel.gfx.g2d;

import java.awt.Color;

public class AdjustingColorModifier implements ColorModifier<Edge2D> {

	private static final double RANGE = 1 - Color.DARK_GRAY.getRGBColorComponents(null)[0];

	@Override
	public Color getColor(Edge2D edge) {

		final Color origColor = edge.getOrigColor();
		double ratio = edge.getLength() / Vector2D.distance(edge.getFirst().getCurrent(), edge.getSecond().getCurrent());
		if (ratio != 1) {

			final float[] cValues = origColor.getRGBComponents(null);
			return new Color(adjustColor(cValues[0], ratio), adjustColor(cValues[1], ratio), adjustColor(cValues[2], ratio), cValues[3]);

		}

		return origColor;
	}

	private float adjustColor(final float value, double ratio) {
		return (float) (ratio > 1 ? (value + (1 - 1 / ratio) * Math.min(1 - value, RANGE))
				: (value - (1 - ratio) * Math.min(value, RANGE)));
	}

}