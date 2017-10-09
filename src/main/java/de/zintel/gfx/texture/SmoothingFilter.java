/**
 * 
 */
package de.zintel.gfx.texture;

import java.awt.Color;

/**
 * @author friedemann.zintel
 *
 */
public class SmoothingFilter extends FilteredTexture {

	/**
	 * @param texture
	 */
	public SmoothingFilter(ITexture texture) {
		super(texture);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.texture.ITexture#getColor(double, double)
	 */
	@Override
	public Color getColor(double x, double y) {

		final double ix = Math.floor(x);
		final double iy = Math.floor(y);

		if (ix == getTexture().getWidth() - 1 && iy == getTexture().getHeight() - 1) {
			return getTexture().getColor(ix, iy);
		} else if (ix == getTexture().getWidth() - 1) {

			double dx = x - Math.floor(x);

			Color ul = getTexture().getColor(ix, iy);
			Color dl = getTexture().getColor(ix, iy + 1);

			return new Color(interpolateColor(ul.getRed(), dl.getRed(), dx), interpolateColor(ul.getGreen(), dl.getGreen(), dx),
					interpolateColor(ul.getBlue(), dl.getBlue(), dx));

		} else if (iy == getTexture().getHeight() - 1) {

			double dy = y - Math.floor(y);

			Color ul = getTexture().getColor(ix, iy);
			Color ur = getTexture().getColor(ix + 1, iy);

			return new Color(interpolateColor(ul.getRed(), ur.getRed(), dy), interpolateColor(ul.getGreen(), ur.getGreen(), dy),
					interpolateColor(ul.getBlue(), ur.getBlue(), dy));

		} else {

			double dx = x - Math.floor(x);
			double dy = y - Math.floor(y);

			Color ul = getTexture().getColor(ix, iy);
			Color ur = getTexture().getColor(ix + 1, iy);
			Color dl = getTexture().getColor(ix, iy + 1);
			Color dr = getTexture().getColor(ix + 1, iy + 1);

			return new Color(interpolateColor(ul.getRed(), ur.getRed(), dl.getRed(), dr.getRed(), dx, dy),
					interpolateColor(ul.getGreen(), ur.getGreen(), dl.getGreen(), dr.getGreen(), dx, dy),
					interpolateColor(ul.getBlue(), ur.getBlue(), dl.getBlue(), dr.getBlue(), dx, dy));

		}
	}

	private int interpolateColor(double from, double to, double dx) {
		return (int) ((1 - dx) * from + dx * to);
	}

	private int interpolateColor(double ul, double ur, double dl, double dr, double dx, double dy) {
		final double dualdx = 1 - dx;
		final double dualdy = 1 - dy;
		return (int) (dualdx * dualdy * ul + dualdy * dx * ur + dualdx * dy * dl + dx * dy * dr);
	}

}
