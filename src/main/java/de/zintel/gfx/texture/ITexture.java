/**
 * 
 */
package de.zintel.gfx.texture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.zintel.math.Field;
import de.zintel.math.VectorND;

/**
 * @author Friedemann
 *
 */
public interface ITexture extends Field {

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
		final Color color = getColor(pos.getCoords().get(0), pos.getCoords().get(1));
		return new VectorND(Arrays.asList((double) color.getRed(), (double) color.getGreen(), (double) color.getBlue()));
	}

}
