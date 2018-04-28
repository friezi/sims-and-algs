package de.zintel.sim.ragdoll;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import de.zintel.gfx.color.CUtils;
import de.zintel.gfx.g2d.verlet.VLEdge2D;

/**
 * generates the mean color-value between each two connected edges.
 * 
 * @author friedo
 *
 */
public class EdgesMeanPointsColorBasedGenerator implements Supplier<Color> {

	private final List<VLEdge2D> edges;
	private final Function<VLEdge2D, Color> colorProvider;
	private final int size;
	private int idx = -1;

	public EdgesMeanPointsColorBasedGenerator(List<VLEdge2D> edges) {
		this(edges, null);
	}

	public EdgesMeanPointsColorBasedGenerator(List<VLEdge2D> edges, Function<VLEdge2D, Color> colorProvider) {
		this.edges = edges;
		this.colorProvider = colorProvider;
		this.size = edges.size();
	}

	@Override
	public Color get() {

		idx = nextIndex(idx);

		VLEdge2D edge1 = edges.get(idx);
		VLEdge2D edge2 = edges.get(nextIndex(idx));
		return (colorProvider != null ? CUtils.mean(Arrays.asList(colorProvider.apply(edge1), colorProvider.apply(edge2)))
				: CUtils.mean(Arrays.asList(edge1.getColor(), edge2.getColor())));
	}

	private int nextIndex(int idx) {

		idx++;
		if (idx >= size || idx < 0) {
			idx = 0;
		}

		return idx;

	}

}