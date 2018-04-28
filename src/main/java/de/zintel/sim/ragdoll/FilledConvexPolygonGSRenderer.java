package de.zintel.sim.ragdoll;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.zintel.gfx.g2d.verlet.AdjustingColorProvider;
import de.zintel.gfx.g2d.verlet.IVLEdgeContainer2D;
import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Vector2D;

/**
 * draws a plygon by using the grafics subsystem.
 * 
 * @author friedo
 *
 * @param <T>
 */
public class FilledConvexPolygonGSRenderer<T extends IVLEdgeContainer2D> implements Consumer<T> {

	private final IGraphicsSubsystem graphicsSubsystem;

	private final AdjustingColorProvider colorModifier = new AdjustingColorProvider();

	public FilledConvexPolygonGSRenderer(IGraphicsSubsystem graphicsSubsystem) {
		this.graphicsSubsystem = graphicsSubsystem;
	}

	@Override
	public void accept(T item) {

		final List<VLEdge2D> edges = item.getEdges();
		final Collection<Vector2D> points = edges.stream().map(edge -> edge.getFirst().getCurrent()).collect(Collectors.toList());

		graphicsSubsystem.drawFilledPolygon(points, new EdgesMeanPointsColorBasedGenerator(edges, colorModifier));

	}

}