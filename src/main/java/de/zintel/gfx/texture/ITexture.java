/**
 * 
 */
package de.zintel.gfx.texture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.zintel.math.IVectorField;
import de.zintel.math.VectorND;

/**
 * @author Friedemann
 *
 */
public interface ITexture extends IVectorField {

	int getWidth();

	int getHeight();

	Color getColor(final double x, final double y);

	public static int mkTIdx(final double v, final int extent) {
		return ((int) (Math.abs(v) * (extent - 1))) % extent;
	}

	@Override
	default List<Integer> getDimensions() {
		return new ArrayList<>(Arrays.asList(getWidth(), getHeight()));
	}

	@Override
	default VectorND getValue(VectorND pos) {
		return color2Vector(getColor(pos.get(0), pos.get(1)));
	}

	default VectorND color2Vector(final Color color) {
		return new VectorND(
				Arrays.asList((double) color.getRed(), (double) color.getGreen(), (double) color.getBlue(), (double) color.getAlpha()));
	}

	@Override
	default int getDimensionsCodomain() {
		return 4; // colors
	}

	@Override
	default void setValue(VectorND pos, VectorND value) {
	}

	@Override
	default List<VectorND> asList() {

		final List<VectorND> vectors = new ArrayList<>(getDimensions().stream().reduce(1, (v1, v2) -> v1 * v2));
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				vectors.add(getValue(new VectorND(Arrays.asList((double) x, (double) y))));
			}
		}

		return vectors;
	}

}
