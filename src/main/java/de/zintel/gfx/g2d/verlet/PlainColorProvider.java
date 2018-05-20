/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.awt.Color;
import java.util.function.Function;

/**
 * @author friedo
 *
 */
public class PlainColorProvider implements Function<VLEdge2D, Color> {

	@Override
	public Color apply(VLEdge2D edge) {
		return edge.getColor();
	}

}
