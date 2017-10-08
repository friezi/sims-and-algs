/**
 * 
 */
package de.zintel.gfx.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import de.zintel.gfx.ESmoothing;

/**
 * @author Friedemann
 *
 */
public class ImageTexture implements ITexture {

	private final BufferedImage image;

	private final ESmoothing smoothing;

	public ImageTexture(BufferedImage image, ESmoothing smoothing) {
		this.image = image;
		this.smoothing = smoothing;
	}

	public ImageTexture(final String path, ESmoothing smoothing) throws IOException {
		this(ImageIO.read(new File(path)), smoothing);
	}

	public ImageTexture(final InputStream is, ESmoothing smoothing) throws IOException {
		this(ImageIO.read(is), smoothing);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.ITexture#getWidth()
	 */
	@Override
	public int getWidth() {
		return image.getWidth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.ITexture#getHeight()
	 */
	@Override
	public int getHeight() {
		return image.getHeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.ITexture#getColor(int, int)
	 */
	@Override
	public Color getColor(double x, double y) {

		final int ix = (int) x;
		final int iy = (int) y;
		if (smoothing == ESmoothing.NONE) {
			return new Color(image.getRGB(ix, iy));
		} else if (smoothing == ESmoothing.COL_IPOL) {

			if (ix == image.getWidth() - 1 && iy == image.getHeight() - 1) {
				return new Color(image.getRGB(ix, iy));
			} else if (ix == image.getWidth() - 1) {

				double dx = x - Math.floor(x);

				Color ul = new Color(image.getRGB(ix, iy));
				Color dl = new Color(image.getRGB(ix, iy + 1));

				return new Color(interpolateColor(ul.getRed(), dl.getRed(), dx), interpolateColor(ul.getGreen(), dl.getGreen(), dx),
						interpolateColor(ul.getBlue(), dl.getBlue(), dx));

			} else if (iy == image.getHeight() - 1) {

				double dy = y - Math.floor(y);

				Color ul = new Color(image.getRGB(ix, iy));
				Color ur = new Color(image.getRGB(ix + 1, iy));

				return new Color(interpolateColor(ul.getRed(), ur.getRed(), dy), interpolateColor(ul.getGreen(), ur.getGreen(), dy),
						interpolateColor(ul.getBlue(), ur.getBlue(), dy));

			} else {

				double dx = x - Math.floor(x);
				double dy = y - Math.floor(y);

				Color ul = new Color(image.getRGB(ix, iy));
				Color ur = new Color(image.getRGB(ix + 1, iy));
				Color dl = new Color(image.getRGB(ix, iy + 1));
				Color dr = new Color(image.getRGB(ix + 1, iy + 1));

				return new Color(interpolateColor(ul.getRed(), ur.getRed(), dl.getRed(), dr.getRed(), dx, dy),
						interpolateColor(ul.getGreen(), ur.getGreen(), dl.getGreen(), dr.getGreen(), dx, dy),
						interpolateColor(ul.getBlue(), ur.getBlue(), dl.getBlue(), dr.getBlue(), dx, dy));

			}

		} else {
			throw new RuntimeException("unsupported smothing: '" + smoothing + "'");
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
