/**
 * 
 */
package de.zintel.gfx.texture;

import java.awt.Color;

/**
 * @author friedemann.zintel
 *
 */
public class InvertFilter extends AFilteredTexture {

	/**
	 * @param texture
	 */
	public InvertFilter(ITexture texture) {
		super(texture);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.texture.ITexture#getColor(double, double)
	 */
	@Override
	public Color getColor(double x, double y) {
		final Color color = getTexture().getColor(x, y);
		return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
	}

}
