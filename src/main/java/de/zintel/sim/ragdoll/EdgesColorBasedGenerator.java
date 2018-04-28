package de.zintel.sim.ragdoll;

import java.awt.Color;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import de.zintel.gfx.g2d.verlet.VLEdge2D;

public class EdgesColorBasedGenerator implements Supplier<Color> {

	private final List<VLEdge2D> edges;
	private final Function<VLEdge2D, Color> colorProvider;
	private final int size;
	private int idx = -1;

	public EdgesColorBasedGenerator(List<VLEdge2D> edges) {
		this(edges, null);
	}

	public EdgesColorBasedGenerator(List<VLEdge2D> edges, Function<VLEdge2D, Color> colorProvider) {
		this.edges = edges;
		this.colorProvider = colorProvider;
		this.size = edges.size();
	}

	@Override
	public Color get() {

		idx++;
		if (idx >= size || idx < 0) {
			idx = 0;
		}

		VLEdge2D edge = edges.get(idx);
		return (colorProvider != null ? colorProvider.apply(edge) : edge.getColor());
	}

}