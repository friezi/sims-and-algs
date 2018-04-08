package de.zintel.sim.ragdoll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.zintel.gfx.g2d.AdjustingColorModifier;
import de.zintel.gfx.g2d.ChainNet2D;
import de.zintel.gfx.g2d.Edge2D;
import de.zintel.gfx.g2d.IRenderer;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Vector2D;

public class SmoothFilledChainNetRenderer implements IRenderer<ChainNet2D> {

	private final IGraphicsSubsystem graphicsSubsystem;

	private final AdjustingColorModifier colorModifier = new AdjustingColorModifier();

	public SmoothFilledChainNetRenderer(IGraphicsSubsystem graphicsSubsystem) {
		this.graphicsSubsystem = graphicsSubsystem;
	}

	@Override
	public void render(ChainNet2D item) {

		final List<List<List<Edge2D>>> edgesH = item.getEdgesH();
		final List<List<List<Edge2D>>> edgesV = item.getEdgesV();

		for (int v = edgesH.size() - 2; v >= 0; v--) {
			// rendering from bottom to top to overcome OpenGL convex-only
			// polygon-rendering

			final List<List<Edge2D>> currentEdgesHTop = edgesH.get(v);
			final List<List<Edge2D>> currentEdgesHBottom = edgesH.get(v + 1);
			for (int h = 0; h < edgesV.size() - 1; h++) {

				final List<Edge2D> hTop = currentEdgesHTop.get(h);
				final List<Edge2D> vRight = edgesV.get(h + 1).get(v);
				final List<Edge2D> hBottom = currentEdgesHBottom.get(h);
				final List<Edge2D> vLeft = edgesV.get(h).get(v);

				final int vecdim = hTop.size() + vRight.size() + hBottom.size() + vLeft.size();
				final Collection<Vector2D> points = new ArrayList<>(vecdim);
				final List<Edge2D> polygonEdges = new ArrayList<>(vecdim);
				for (int i = 0; i < hTop.size(); i++) {
					final Edge2D edge = hTop.get(i);
					polygonEdges.add(edge);
					points.add(edge.getFirst().getCurrent());
				}
				for (int i = 0; i < vRight.size(); i++) {
					final Edge2D edge = vRight.get(i);
					polygonEdges.add(edge);
					points.add(edge.getFirst().getCurrent());
				}
				for (int i = hBottom.size() - 1; i >= 0; i--) {
					final Edge2D edge = hBottom.get(i);
					polygonEdges.add(edge);
					points.add(edge.getSecond().getCurrent());
				}
				for (int i = vLeft.size() - 1; i >= 0; i--) {
					final Edge2D edge = vLeft.get(i);
					polygonEdges.add(edge);
					points.add(edge.getSecond().getCurrent());
				}

				graphicsSubsystem.drawFilledPolygon(points, new EdgesColorBasedGenerator(polygonEdges, colorModifier));

			}
		}
	}
}