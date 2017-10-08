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

/**
 * @author Friedemann
 *
 */
public class ImageTexture implements ITexture {

	private final BufferedImage image;

	private final boolean colorInterpolation;

	public ImageTexture(BufferedImage image, boolean colorInterpolation) {
		this.image = image;
		this.colorInterpolation = colorInterpolation;
	}

	public ImageTexture(final String path, boolean colorInterpolation) throws IOException {
		this(ImageIO.read(new File(path)), colorInterpolation);
	}

	public ImageTexture(final InputStream is, boolean colorInterpolation) throws IOException {
		this(ImageIO.read(is), colorInterpolation);
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
		if (!colorInterpolation || (ix == image.getWidth() - 1 || iy == image.getHeight() - 1)) {
			return new Color(image.getRGB(ix, iy));
		} else {

			double dx = x - Math.floor(x);
			double dy = y - Math.floor(y);

			Color c1 = new Color(image.getRGB(ix, iy));
			Color c2 = new Color(image.getRGB(ix + 1, iy));
			Color c3 = new Color(image.getRGB(ix, iy + 1));
			Color c4 = new Color(image.getRGB(ix + 1, iy + 1));

			return new Color(interpolateColor(c1.getRed(), c2.getRed(), c3.getRed(), c4.getRed(), dx, dy),
					interpolateColor(c1.getGreen(), c2.getGreen(), c3.getGreen(), c4.getGreen(), dx, dy),
					interpolateColor(c1.getBlue(), c2.getBlue(), c3.getBlue(), c4.getBlue(), dx, dy));

		}
	}

	private int interpolateColor(double v1, double v2, double v3, double v4, double dx, double dy) {
		final double dualdx = 1 - dx;
		final double dualdy = 1 - dy;
		return (int) (dualdx * dualdy * v1 + dualdy * dx * v2 + dualdx * dy * v3 + dx * dy * v4);
	}

}
