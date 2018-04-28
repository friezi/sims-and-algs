package de.zintel.sim.ragdoll;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import de.zintel.gfx.g2d.verlet.AdjustingColorProvider;
import de.zintel.gfx.g2d.verlet.VLChainNet2D;
import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Vector2D;

public class MeanFilledChainNetRenderer implements Consumer<VLChainNet2D> {

	private final IGraphicsSubsystem graphicsSubsystem;

	private final AdjustingColorProvider colorProvider = new AdjustingColorProvider();

	public MeanFilledChainNetRenderer(IGraphicsSubsystem graphicsSubsystem) {
		this.graphicsSubsystem = graphicsSubsystem;
	}

	@Override
	public void accept(VLChainNet2D item) {

		final List<List<List<VLEdge2D>>> edgesH = item.getEdgesH();
		final List<List<List<VLEdge2D>>> edgesV = item.getEdgesV();

		for (int v = edgesH.size() - 2; v >= 0; v--) {
			// rendering from bottom to top to overcome OpenGL convex-only
			// polygon-rendering

			final List<List<VLEdge2D>> currentEdgesHTop = edgesH.get(v);
			final List<List<VLEdge2D>> currentEdgesHBottom = edgesH.get(v + 1);
			for (int h = 0; h < edgesV.size() - 1; h++) {

				final List<VLEdge2D> hTop = currentEdgesHTop.get(h);
				final List<VLEdge2D> vRight = edgesV.get(h + 1).get(v);
				final List<VLEdge2D> hBottom = currentEdgesHBottom.get(h);
				final List<VLEdge2D> vLeft = edgesV.get(h).get(v);

				final Collection<Vector2D> points = new ArrayList<>(hTop.size() + vRight.size() + hBottom.size() + vLeft.size());
				for (int i = 0; i < hTop.size(); i++) {
					final VLEdge2D edge = hTop.get(i);
					points.add(edge.getFirst().getCurrent());
				}
				for (int i = 0; i < vRight.size(); i++) {
					final VLEdge2D edge = vRight.get(i);
					points.add(edge.getFirst().getCurrent());
				}
				for (int i = hBottom.size() - 1; i >= 0; i--) {
					final VLEdge2D edge = hBottom.get(i);
					points.add(edge.getSecond().getCurrent());
				}
				for (int i = vLeft.size() - 1; i >= 0; i--) {
					final VLEdge2D edge = vLeft.get(i);
					points.add(edge.getSecond().getCurrent());
				}
				final Color hTopColor = colorProvider.apply(hTop.iterator().next());
				final Color vRightColor = colorProvider.apply(vRight.iterator().next());
				final Color hBottomColor = colorProvider.apply(hBottom.iterator().next());
				final Color vLeftColor = colorProvider.apply(vLeft.iterator().next());
				graphicsSubsystem.drawFilledPolygon(points,
						new Color((hTopColor.getRed() + vRightColor.getRed() + hBottomColor.getRed() + vLeftColor.getRed()) / 4,
								(hTopColor.getGreen() + vRightColor.getGreen() + hBottomColor.getGreen() + vLeftColor.getGreen()) / 4,
								(hTopColor.getBlue() + vRightColor.getBlue() + hBottomColor.getBlue() + vLeftColor.getBlue()) / 4,
								(hTopColor.getAlpha() + vRightColor.getAlpha() + hBottomColor.getAlpha() + vLeftColor.getAlpha()) / 4));

			}
		}
	}
}