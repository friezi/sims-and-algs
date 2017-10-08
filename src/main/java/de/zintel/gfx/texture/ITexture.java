/**
 * 
 */
package de.zintel.gfx.texture;

import java.awt.Color;

/**
 * @author Friedemann
 *
 */
public interface ITexture {

	int getWidth();

	int getHeight();

	Color getColor(final double x, final double y);

	public static int mkTIdx(final double v, final int extent) {
		return ((int) (Math.abs(v) * (extent - 1))) % extent;
	}

}
