package de.zintel.sim.ragdoll;

import java.awt.Color;
import java.util.List;

import de.zintel.gfx.ColorModifier;
import de.zintel.gfx.color.CUtils.ColorGenerator;
import de.zintel.gfx.g2d.verlet.VLEdge2D;

public class EdgesColorBasedGenerator implements ColorGenerator {

	private final List<VLEdge2D> edges;
	private final ColorModifier<VLEdge2D> colorModifier;
	private final int size;
	private int idx = -1;

	public EdgesColorBasedGenerator(List<VLEdge2D> edges) {
		this(edges, null);
	}

	public EdgesColorBasedGenerator(List<VLEdge2D> edges, ColorModifier<VLEdge2D> colorModifier) {
		this.edges = edges;
		this.colorModifier = colorModifier;
		this.size = edges.size();
	}

	@Override
	public Color generateColor() {

		idx++;
		if (idx >= size || idx < 0) {
			idx = 0;
		}

		VLEdge2D edge = edges.get(idx);
		return (colorModifier != null ? colorModifier.getColor(edge) : edge.getColor());
	}

}