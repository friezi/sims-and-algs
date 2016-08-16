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

	public ImageTexture(final String path) throws IOException {
		image = ImageIO.read(new File(path));
	}

	public ImageTexture(final InputStream is) throws IOException {
		image = ImageIO.read(is);
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
	public Color getColor(int x, int y) {
		return new Color(image.getRGB(x, y));
	}

}
