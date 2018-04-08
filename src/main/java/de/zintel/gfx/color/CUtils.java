/**
 * 
 */
package de.zintel.gfx.color;

import java.awt.Color;
import java.util.Collection;
import java.util.Random;

/**
 * @author Friedemann
 *
 */
public final class CUtils {

	@FunctionalInterface
	public static interface ColorGenerator {
		Color generateColor();
	}

	public static class SphericalColorGenerator implements ColorGenerator {

		private boolean center = true;

		private final Color centerColor;

		private final Color edgeColor;

		public SphericalColorGenerator(Color centerColor, Color edgeColor) {
			this.centerColor = centerColor;
			this.edgeColor = edgeColor;
		}

		@Override
		public Color generateColor() {

			if (center) {

				center = false;
				return centerColor;

			} else {
				return edgeColor;
			}
		}

	}

	private CUtils() {
	}

	public static int minDark(final int colorValue) {
		return colorValue / 2;
	}

	public static int maxBright(final int colorValue) {
		return colorValue + (255 - colorValue) / 2;
	}

	public static Color makeRandomColor() {
		Random random = new Random();
		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

	public static Color makeRandomStarColor() {

		Random random = new Random();

		int c = random.nextInt(21);
		Color color = c < 10 ? Color.YELLOW : (c < 20 ? Color.ORANGE : Color.ORANGE);
		float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		int min = color == Color.BLUE ? 200 : 120;
		hsb[1] = (256 - random.nextInt(256 - min)) / 256F;
		hsb[2] = (256 - random.nextInt(256 - min)) / 256F;
		return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	}

	public static boolean equal(final Color c1, final Color c2) {
		return (c1.getRed() == c2.getRed() && c1.getGreen() == c2.getGreen() && c1.getBlue() == c2.getBlue() && c1.getAlpha() == c2.getAlpha());
	}

	public static int brighten(final int cValue, double colorBrigthnessFactor) {

		if (colorBrigthnessFactor < 1) {
			return cValue;
		} else {
			return (int) (cValue + (colorBrigthnessFactor - 1) * ((255 - cValue) / colorBrigthnessFactor));
		}

	}

	public static Color mean(final Collection<Color> colors) {

		final int size = colors.size();

		int red = 0;
		int green = 0;
		int blue = 0;
		int alpha = 0;

		for (Color color : colors) {

			red += color.getRed();
			green += color.getGreen();
			blue += color.getBlue();
			alpha += color.getAlpha();

		}

		return new Color(red / size, green / size, blue / size, alpha / size);

	}

}
