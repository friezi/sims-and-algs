/**
 * 
 */
package de.zintel.gfx.texture;

import java.awt.Color;
import java.util.Arrays;
import java.util.function.Function;

import de.zintel.math.MathUtils;
import de.zintel.math.VectorND;

/**
 * @author friedemann.zintel
 *
 */
public class MorphTexture implements ITexture {

	private final ITexture textureZero;

	private final ITexture textureOne;

	private final Function<VectorND, Double> morphFactor;

	private final VectorND factorXRange;

	private final VectorND factorYRange;

	public MorphTexture(ITexture textureZero, ITexture textureOne, Function<VectorND, Double> morphFactor, VectorND factorXRange,
			VectorND factorYRange) {
		this.textureZero = textureZero;
		this.textureOne = textureOne;
		this.morphFactor = morphFactor;
		this.factorXRange = factorXRange;
		this.factorYRange = factorYRange;

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

		final VectorND colorValues = morphColorValue(new VectorND(Arrays.asList(x, y)));
		return new Color(fit(colorValues.get(0)), fit(colorValues.get(1)), fit(colorValues.get(2)), fit(colorValues.get(3)));
	}

	private int fit(final double value) {
		return (int) Math.max(Math.min(value, 255), 0);
	}

	private VectorND morphColorValue(VectorND coords) {
		return MathUtils.morph(xy -> textureZero.getValue(xy), xy -> textureOne.getValue(xy),
				xy -> morphFactor.apply(new VectorND(Arrays.asList(
						MathUtils.morphRange(0, textureZero.getWidth() - 1, factorXRange.get(0), factorXRange.get(1), xy.get(0)),
						MathUtils.morphRange(0, textureZero.getHeight() - 1, factorYRange.get(0), factorYRange.get(1), xy.get(1))))),
				xy -> 1.0, coords);
	}

	public ITexture getTextureZero() {
		return textureZero;
	}

	public ITexture getTextureOne() {
		return textureOne;
	}

	public Function<VectorND, Double> getMorphFactor() {
		return morphFactor;
	}

	public VectorND getFactorXRange() {
		return factorXRange;
	}

	public VectorND getFactorYRange() {
		return factorYRange;
	}

}