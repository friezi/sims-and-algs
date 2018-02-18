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

	private final ITexture textureZero;

	private final ITexture textureOne;

	private final Function<Double, Double> morphFactor;

	private final double factorMin;

	private final double factorMax;

	public MorphTexture(ITexture textureZero, ITexture textureOne, Function<Double, Double> morphFactor, double factorMin,
			double factorMax) {
		this.textureZero = textureZero;
		this.textureOne = textureOne;
		this.morphFactor = morphFactor;
		this.factorMin = factorMin;
		this.factorMax = factorMax;

		if (!(textureZero.getHeight() == textureOne.getHeight() && textureZero.getWidth() == textureOne.getWidth())) {
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
		return textureZero.getWidth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.texture.ITexture#getHeight()
	 */
	@Override
	public int getHeight() {
		return textureOne.getHeight();
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
		return (int) MathUtils.morph(x1 -> colorValueChooser.apply(textureZero.getColor(x1, y)).doubleValue(),
				x1 -> colorValueChooser.apply(textureOne.getColor(x1, y)).doubleValue(),
				x1 -> morphFactor.apply(MathUtils.morphRange(0, textureZero.getWidth() - 1, factorMin, factorMax, x1)), x);
	}

	public ITexture getTextureZero() {
		return textureZero;
	}

	public ITexture getTextureOne() {
		return textureOne;
	}

	public Function<Double, Double> getMorphFactor() {
		return morphFactor;
	}

	public double getFactorMin() {
		return factorMin;
	}

	public double getFactorMax() {
		return factorMax;
	}

}
