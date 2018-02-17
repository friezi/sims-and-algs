/**
 * 
 */
package de.zintel.gfx.texture;

import java.awt.Color;
import java.util.function.Function;

import de.zintel.math.MathUtils;

/**
 * @author friedemann.zintel
 *
 */
public class MorphTexture implements ITexture {

	private final ITexture textureLeft;

	private final ITexture textureRight;

	private final Function<Double, Double> morphFactor;

	private final double factorMin;

	private final double factorMax;

	public MorphTexture(ITexture textureLeft, ITexture textureRight, Function<Double, Double> morphFactor, double factorMin,
			double factorMax) {
		this.textureLeft = textureLeft;
		this.textureRight = textureRight;
		this.morphFactor = morphFactor;
		this.factorMin = factorMin;
		this.factorMax = factorMax;

		if (!(textureLeft.getHeight() == textureRight.getHeight() && textureLeft.getWidth() == textureRight.getWidth())) {
			throw new RuntimeException("textures not matching!");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.texture.ITexture#getWidth()
	 */
	@Override
	public int getWidth() {
		return textureLeft.getWidth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.texture.ITexture#getHeight()
	 */
	@Override
	public int getHeight() {
		return textureRight.getHeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.texture.ITexture#getColor(double, double)
	 */
	@Override
	public Color getColor(double x, double y) {
		return new Color(morphColorValue(x, y, c -> c.getRed()), morphColorValue(x, y, c -> c.getGreen()),
				morphColorValue(x, y, c -> c.getBlue()));
	}

	private int morphColorValue(double x, double y, Function<Color, Integer> colorValueChooser) {
		return (int) MathUtils.morph(x1 -> colorValueChooser.apply(textureLeft.getColor(x1, y)).doubleValue(),
				x1 -> colorValueChooser.apply(textureRight.getColor(x1, y)).doubleValue(),
				x1 -> morphFactor.apply(MathUtils.morphRange(0, textureLeft.getWidth() - 1, factorMin, factorMax, x1)), x);
	}

}
