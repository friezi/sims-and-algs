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

	public ImageTexture(BufferedImage image) {
		this.image = image;
	}

	public ImageTexture(final String path) throws IOException {
		this(ImageIO.read(new File(path)));
	}

	public ImageTexture(final InputStream is) throws IOException {
		this(ImageIO.read(is));
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

		if (x < 0.0) {
			x = 0.0;
		}
		if (y < 0.0) {
			y = 0.0;
		}
		return new Color(image.getRGB((int) x, (int) y));
	}
}
