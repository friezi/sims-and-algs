/**
 * 
 */
package de.zintel.gfx.color;

import java.awt.Color;
import java.util.Random;

/**
 * @author Friedemann
 *
 */
public final class CUtils {

	@FunctionalInterface
	public interface ColorGenerator {
		Color generateColor();
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
		return (c1.getRed() == c2.getRed() && c1.getGreen() == c2.getGreen() && c1.getBlue() == c2.getBlue()
				&& c1.getAlpha() == c2.getAlpha());
	}

}
