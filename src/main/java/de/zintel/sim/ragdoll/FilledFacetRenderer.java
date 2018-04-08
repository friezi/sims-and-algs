package de.zintel.sim.ragdoll;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.zintel.gfx.g2d.AdjustingColorModifier;
import de.zintel.gfx.g2d.Edge2D;
import de.zintel.gfx.g2d.Facet2D;
import de.zintel.gfx.g2d.IRenderer;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Vector2D;

public class FilledFacetRenderer implements IRenderer<Facet2D> {

	private final IGraphicsSubsystem graphicsSubsystem;

	private final AdjustingColorModifier colorModifier = new AdjustingColorModifier();

	public FilledFacetRenderer(IGraphicsSubsystem graphicsSubsystem) {
		this.graphicsSubsystem = graphicsSubsystem;
	}

	@Override
	public void render(Facet2D item) {

		final List<Edge2D> edges = item.getEdges();
		final Collection<Vector2D> points = edges.stream().map(edge -> edge.getFirst().getCurrent()).collect(Collectors.toList());

		graphicsSubsystem.drawFilledPolygon(points, new EdgesMeanPointsColorBasedGenerator(edges, colorModifier));

	}

}