/**
 * 
 */
package de.zintel.gfx.texture;

import java.awt.Color;
import java.util.Arrays;

import de.zintel.math.VectorND;

/**
 * @author friedemann.zintel
 *
 */
public class BilinearFilter extends AFilteredTexture {

	/**
	 * @param texture
	 */
	public BilinearFilter(ITexture texture) {
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

			double dy = y - iy;

			Color ul = getTexture().getColor(ix, iy);
			Color dl = getTexture().getColor(ix, iy + 1);

			return new Color(interpolateColor(ul.getRed(), dl.getRed(), dy), interpolateColor(ul.getGreen(), dl.getGreen(), dy),
					interpolateColor(ul.getBlue(), dl.getBlue(), dy));

		} else if (iy == getTexture().getHeight() - 1) {

			double dx = x - ix;

			Color ul = getTexture().getColor(ix, iy);
			Color ur = getTexture().getColor(ix + 1, iy);

			return new Color(interpolateColor(ul.getRed(), ur.getRed(), dx), interpolateColor(ul.getGreen(), ur.getGreen(), dx),
					interpolateColor(ul.getBlue(), ur.getBlue(), dx));

		} else {

			double dx = x - ix;
			double dy = y - iy;

			Color ul = getTexture().getColor(ix, iy);
			Color ur = getTexture().getColor(ix + 1, iy);
			Color dl = getTexture().getColor(ix, iy + 1);
			Color dr = getTexture().getColor(ix + 1, iy + 1);

			return new Color(interpolateColor(ul.getRed(), ur.getRed(), dl.getRed(), dr.getRed(), dx, dy),
					interpolateColor(ul.getGreen(), ur.getGreen(), dl.getGreen(), dr.getGreen(), dx, dy),
					interpolateColor(ul.getBlue(), ur.getBlue(), dl.getBlue(), dr.getBlue(), dx, dy));

		}
	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see de.zintel.gfx.texture.ITexture#getColor(double, double)
//	 */
//	@Override
//	public Color getColor(double x, double y) {
//
//		final VectorND vectorColor = getTexture().interpolateBilinear(new VectorND(Arrays.asList(x, y)));
//		return new Color(vectorColor.get(0).intValue(), vectorColor.get(1).intValue(), vectorColor.get(2).intValue());
//	}

	private int interpolateColor(double from, double to, double dr) {
		return (int) ((1 - dr) * from + dr * to);
	}

	private int interpolateColor(double ul, double ur, double dl, double dr, double dx, double dy) {
		final double dualdx = 1 - dx;
		final double dualdy = 1 - dy;
		return (int) (dualdx * dualdy * ul + dualdy * dx * ur + dualdx * dy * dl + dx * dy * dr);
	}

}
