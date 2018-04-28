package de.zintel.sim.ragdoll;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.zintel.gfx.g2d.verlet.AdjustingColorProvider;
import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLFacet2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Vector2D;

public class FilledFacetRenderer implements Consumer<VLFacet2D> {

	private final IGraphicsSubsystem graphicsSubsystem;

	private final AdjustingColorProvider colorModifier = new AdjustingColorProvider();

	public FilledFacetRenderer(IGraphicsSubsystem graphicsSubsystem) {
		this.graphicsSubsystem = graphicsSubsystem;
	}

	@Override
	public void accept(VLFacet2D item) {

		final List<VLEdge2D> edges = item.getEdges();
		final Collection<Vector2D> points = edges.stream().map(edge -> edge.getFirst().getCurrent()).collect(Collectors.toList());

		graphicsSubsystem.drawFilledPolygon(points, new EdgesMeanPointsColorBasedGenerator(edges, colorModifier));

	}

}